package com.etiya.crm.dataseeder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.etiya.crm.dataseeder.client.CustomerClient;
import com.etiya.crm.dataseeder.client.CustomerClient.AddressInfo;
import com.etiya.crm.dataseeder.client.CustomerClient.AddressResponse;
import com.etiya.crm.dataseeder.client.CustomerClient.ContactInfo;
import com.etiya.crm.dataseeder.client.CustomerClient.CreateBillingAccountRequest;
import com.etiya.crm.dataseeder.client.CustomerClient.IndividualInfo;
import com.etiya.crm.dataseeder.client.CustomerClient.OnboardCustomerRequest;
import com.etiya.crm.dataseeder.client.LookupClient;
import com.etiya.crm.dataseeder.client.OrderClient;
import com.etiya.crm.dataseeder.client.OrderClient.BasketItemRequest;
import com.etiya.crm.dataseeder.client.ProductClient;
import com.etiya.crm.dataseeder.client.ProductClient.ProductOfferingRelationResponse;
import com.etiya.crm.dataseeder.client.ProductClient.ProductOfferingResponse;
import com.etiya.crm.dataseeder.data.IdentifierGenerator;
import com.etiya.crm.dataseeder.data.TurkishNameData;

import lombok.extern.slf4j.Slf4j;

/**
 * Tum stack ayaktayken (podman compose up sonrasi) calistirilir: gercek REST cagrilariyla,
 * bagimlilik sirasina gore (lookup -> customer onboarding -> adres/hesap -> siparis) gercekci
 * Turkce demo veri yazar. Tek bir musteri/siparis basarisiz olursa calisma durmaz - hata loglanir,
 * sonraki musteriyle devam edilir; sonda ozet basilir.
 */
@Slf4j
@Component
public class SeedRunner implements CommandLineRunner {

	private static final DateTimeFormatter BIRTH_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private final SeederProperties props;
	private final LookupClient lookupClient;
	private final CustomerClient customerClient;
	private final ProductClient productClient;
	private final OrderClient orderClient;
	private final IdentifierGenerator idGen;

	private int customersCreated;
	private int customerFailures;
	private int ordersCreated;
	private int ordersFinished;
	private int ordersCancelled;
	private int ordersLeftWaiting;
	private int orderFailures;

	public SeedRunner(SeederProperties props, LookupClient lookupClient, CustomerClient customerClient,
			ProductClient productClient, OrderClient orderClient, IdentifierGenerator idGen) {
		this.props = props;
		this.lookupClient = lookupClient;
		this.customerClient = customerClient;
		this.productClient = productClient;
		this.orderClient = orderClient;
		this.idGen = idGen;
	}

	@Override
	public void run(String... args) {
		ThreadLocalRandom random = ThreadLocalRandom.current();

		Long maleGenderId = lookupClient.resolveGnlTpId("GENDER", "MALE");
		Long femaleGenderId = lookupClient.resolveGnlTpId("GENDER", "FEMALE");
		List<Long> cityIds = TurkishNameData.CITY_SHORT_CODES.stream()
				.map(code -> lookupClient.resolveGnlTpId("CITY", code))
				.toList();

		List<ProductOfferingResponse> offerings = productClient.getAllOfferings();
		if (offerings.isEmpty()) {
			log.warn("product-service'te hic offering bulunamadi - siparisler atlanacak.");
		}

		log.info("Seeding basliyor: {} musteri hedefleniyor.", props.customerCount());

		for (int i = 0; i < props.customerCount(); i++) {
			try {
				seedOneCustomer(random, maleGenderId, femaleGenderId, cityIds, offerings);
				customersCreated++;
			} catch (Exception e) {
				customerFailures++;
				log.warn("Musteri #{} olusturulamadi: {}", i + 1, e.getMessage());
			}
		}

		log.info("Seeding bitti. Musteri: {} basarili / {} basarisiz. Siparis: {} olusturuldu "
						+ "({} tamamlandi, {} iptal, {} beklemede) / {} basarisiz.",
				customersCreated, customerFailures, ordersCreated, ordersFinished, ordersCancelled,
				ordersLeftWaiting, orderFailures);
	}

	private void seedOneCustomer(ThreadLocalRandom random, Long maleGenderId, Long femaleGenderId,
			List<Long> cityIds, List<ProductOfferingResponse> offerings) {
		boolean isMale = random.nextBoolean();
		String firstName = pick(random, isMale ? TurkishNameData.MALE_FIRST_NAMES : TurkishNameData.FEMALE_FIRST_NAMES);
		String lastName = pick(random, TurkishNameData.LAST_NAMES);
		String motherName = pick(random, TurkishNameData.FEMALE_FIRST_NAMES);
		String fatherName = pick(random, TurkishNameData.MALE_FIRST_NAMES);
		LocalDate birthDate = randomBirthDate(random);

		IndividualInfo individual = new IndividualInfo(firstName, null, lastName, birthDate.format(BIRTH_DATE_FORMAT),
				isMale ? maleGenderId : femaleGenderId, motherName, fatherName, idGen.nextNationalId());

		AddressInfo primaryAddress = randomAddress(random, cityIds);
		ContactInfo contact = new ContactInfo(idGen.nextEmail(firstName, lastName), idGen.nextMobilePhone(),
				random.nextBoolean() ? idGen.homePhone() : null, null);

		OnboardCustomerRequest onboardRequest = new OnboardCustomerRequest(individual, List.of(primaryAddress), contact);
		var customer = customerClient.onboard(onboardRequest);

		Long custId = customer.custId();

		// custAcctId=223 (Musteri Hesap), onboarding'de otomatik acilan genel/temel hesaptir -
		// urun eklenebilen gercek hesap turu 224/BILL_ACCT'tir (bkz. CreateBillingAccountRequest,
		// "Create Billing Account" ekrani). Bu yuzden her musteri icin en az bir 224 hesabi ZORUNLU
		// olusturulur - garanti siparis buraya gider, default 223 hesabina degil.
		List<AccountAddressPair> billingAccounts = new ArrayList<>();
		billingAccounts.add(createBillingAccount(random, custId, cityIds));
		if (random.nextDouble() < props.extraBillingAccountRatio()) {
			billingAccounts.add(createBillingAccount(random, custId, cityIds));
		}

		if (!offerings.isEmpty()) {
			Set<Long> usedOfferingIds = new HashSet<>();

			// Garanti siparis: ilk 224 hesabina, basarili olana kadar farkli offering'ler denenerek
			// (bkz. placeOrderWithRetry) eklenir VE her zaman finish edilir (WAIT/REJECTED'te
			// birakilmaz) - aksi halde hesapta "aktif" bir urun olmaz.
			AccountAddressPair billingAccount = billingAccounts.get(0);
			try {
				usedOfferingIds.addAll(placeOrderWithRetry(random, custId, billingAccount, offerings, usedOfferingIds, true));
				ordersCreated++;
			} catch (Exception e) {
				orderFailures++;
				log.error("Musteri {} icin GARANTI siparis olusturulamadi (denenen hicbir offering basarili "
						+ "olmadi): {}", custId, e.getMessage());
			}

			// Ekstra siparisler: best-effort, cesitlilik icin (rastgele finish/cancel/wait) -
			// basarisiz olursa musteri yine de yukaridaki garanti siparise sahip olur.
			int bonusOrderCount = random.nextDouble() < props.orderAttemptRatio() ? random.nextInt(0, 3) : 0;
			for (int i = 0; i < bonusOrderCount; i++) {
				try {
					AccountAddressPair account = pick(random, billingAccounts);
					usedOfferingIds.addAll(placeOrderWithRetry(random, custId, account, offerings, usedOfferingIds, false));
					ordersCreated++;
				} catch (Exception e) {
					orderFailures++;
					log.warn("Musteri {} icin ek siparis olusturulamadi: {}", custId, e.getMessage());
				}
			}
		}
	}

	private AccountAddressPair createBillingAccount(ThreadLocalRandom random, Long custId, List<Long> cityIds) {
		AddressInfo addressInfo = randomAddress(random, cityIds);
		AddressResponse address = customerClient.addAddress(custId, addressInfo);
		var account = customerClient.createBillingAccount(custId,
				new CreateBillingAccountRequest(addressInfo.addressDesc() + " Hesabi", "Fatura hesabi", address.id(),
						null));
		return new AccountAddressPair(account.custAcctId(), address.id());
	}

	/**
	 * excludeIds'te olmayan offering'leri karistirip sirayla dener; ilk basarili validate-basket +
	 * create + finish/cancel/wait akisinda kullanilan offering id'lerini dondurur. Hepsi
	 * basarisiz olursa (katalogda gercekten uyumlu kombinasyon yoksa) son hatayla birlikte fırlatır.
	 * forceFinish=true ise siparis rastgele cancel/wait'e degil, her zaman finish'e gider.
	 */
	private List<Long> placeOrderWithRetry(ThreadLocalRandom random, Long custId, AccountAddressPair account,
			List<ProductOfferingResponse> offerings, Set<Long> excludeIds, boolean forceFinish) {
		List<ProductOfferingResponse> candidates = new ArrayList<>(offerings.stream()
				.filter(o -> !excludeIds.contains(o.productOfferingId()))
				.toList());
		if (candidates.isEmpty()) {
			candidates = new ArrayList<>(offerings);
		}
		Collections.shuffle(candidates, random);

		Exception lastError = null;
		for (ProductOfferingResponse primary : candidates) {
			try {
				return submitOrder(random, custId, account, primary, offerings, excludeIds, forceFinish);
			} catch (Exception e) {
				lastError = e;
			}
		}
		throw new IllegalStateException(
				"Denenen " + candidates.size() + " offering'in hicbiriyle siparis olusturulamadi", lastError);
	}

	private List<Long> submitOrder(ThreadLocalRandom random, Long custId, AccountAddressPair account,
			ProductOfferingResponse primary, List<ProductOfferingResponse> offerings, Set<Long> excludeIds,
			boolean forceFinish) {
		List<ProductOfferingRelationResponse> relations = productClient.getRelations(primary.productOfferingId());

		List<Long> offeringIds = new ArrayList<>();
		offeringIds.add(primary.productOfferingId());
		List<Long> exclusiveWith = new ArrayList<>();
		for (ProductOfferingRelationResponse relation : relations) {
			Long other = otherSide(relation, primary.productOfferingId());
			if (other == null) {
				continue;
			}
			if (Boolean.TRUE.equals(relation.mandatory())
					&& Objects.equals(relation.productOfferingId1(), primary.productOfferingId())) {
				offeringIds.add(other);
			}
			if (Boolean.TRUE.equals(relation.exclusive())) {
				exclusiveWith.add(other);
			}
		}

		if (random.nextDouble() < props.secondOfferingRatio()) {
			offerings.stream()
					.map(ProductOfferingResponse::productOfferingId)
					.filter(id -> !offeringIds.contains(id) && !exclusiveWith.contains(id) && !excludeIds.contains(id))
					.skip(random.nextInt(Math.max(1, offerings.size())))
					.findFirst()
					.ifPresent(offeringIds::add);
		}

		List<BasketItemRequest> items = offeringIds.stream()
				.map(id -> new BasketItemRequest(id, null, null))
				.toList();

		orderClient.validateBasket(custId, account.custAcctId(), items);
		var order = orderClient.create(custId, account.custAcctId(), items);
		orderClient.saveConfiguration(order.custOrdId(), account.addressId());

		if (forceFinish) {
			orderClient.finish(order.custOrdId());
			ordersFinished++;
		} else {
			double outcome = random.nextDouble();
			if (outcome < props.orderFinishRatio()) {
				orderClient.finish(order.custOrdId());
				ordersFinished++;
			} else if (outcome < props.orderFinishRatio() + props.orderCancelRatio()) {
				orderClient.cancel(order.custOrdId());
				ordersCancelled++;
			} else {
				ordersLeftWaiting++;
			}
		}
		return offeringIds;
	}

	private static Long otherSide(ProductOfferingRelationResponse relation, Long selfId) {
		if (Objects.equals(relation.productOfferingId1(), selfId)) {
			return relation.productOfferingId2();
		}
		if (Objects.equals(relation.productOfferingId2(), selfId)) {
			return relation.productOfferingId1();
		}
		return null;
	}

	private AddressInfo randomAddress(ThreadLocalRandom random, List<Long> cityIds) {
		return new AddressInfo(pick(random, cityIds), pick(random, TurkishNameData.STREET_NAMES),
				"No:" + random.nextInt(1, 60) + " Kat:" + random.nextInt(1, 9) + " Daire:" + random.nextInt(1, 20),
				pick(random, TurkishNameData.DESCRIPTIONS));
	}

	private static LocalDate randomBirthDate(ThreadLocalRandom random) {
		LocalDate earliest = LocalDate.now().minusYears(75);
		LocalDate latest = LocalDate.now().minusYears(18);
		long randomEpochDay = random.nextLong(earliest.toEpochDay(), latest.toEpochDay());
		return LocalDate.ofEpochDay(randomEpochDay);
	}

	private static <T> T pick(ThreadLocalRandom random, List<T> values) {
		return values.get(random.nextInt(values.size()));
	}

	private record AccountAddressPair(Long custAcctId, Long addressId) {
	}
}
