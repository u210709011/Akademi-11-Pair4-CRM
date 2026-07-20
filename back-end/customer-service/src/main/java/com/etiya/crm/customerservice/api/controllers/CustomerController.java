package com.etiya.crm.customerservice.api.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.etiya.crm.customerservice.business.abstracts.CustomerService;
import com.etiya.crm.customerservice.business.dtos.requests.AddressEditRequest;
import com.etiya.crm.customerservice.business.dtos.requests.ContactInfo;
import com.etiya.crm.customerservice.business.dtos.requests.CreateBillingAccountRequest;
import com.etiya.crm.customerservice.business.dtos.requests.CustomerSearchRequest;
import com.etiya.crm.customerservice.business.dtos.requests.IndividualInfo;
import com.etiya.crm.customerservice.business.dtos.requests.OnboardCustomerRequest;
import com.etiya.crm.customerservice.business.dtos.requests.UpdateIndividualInfo;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerAccountResponse;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerResponse;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerSearchResponse;
import com.etiya.crm.customerservice.business.dtos.responses.IdentityVerificationResponse;
import com.etiya.crm.shared.contracts.address.AddressResponse;
import com.etiya.crm.shared.contracts.individual.IndividualResponse;
import com.etiya.crm.customerservice.constants.Roles;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Musteri onboarding, arama, editleme (kisisel bilgi / adres / contact) ve
 * hesap (account) uc noktalari. Bu servis party-service ve
 * contact-info-service icin orkestrasyon (front door) katmanidir - front-end
 * her zaman buraya yazar, downstream servislere doğrudan gitmez.
 */
@Tag(name = "Customers", description = "Musteri onboarding, arama, editleme ve hesap yonetimi")
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('" + Roles.CRM_AGENT + "')")
public class CustomerController {

	private final CustomerService customerService;

	// ACC-009..013: adres/kontakt adimlarindan once kimlik dogrulama + tekillik kontrolu.
	@Operation(summary = "Kimlik dogrulama (KPS) + TC no tekillik kontrolu",
			description = "DB'ye hicbir sey yazmaz, sadece dogrular. Onboarding formunun ilk adiminda "
					+ "(adres/contact girilmeden once) cagrilir; ayni dogrulama onboard() icinde de "
					+ "tekrar calisir (defense in depth). Basarisiz olursa 422 (kimlik dogrulanamadi) "
					+ "veya 409 (TC no zaten kayitli) doner.")
	@PostMapping("/onboarding/verify-identity")
	public ResponseEntity<IdentityVerificationResponse> verifyIdentity(@Valid @RequestBody IndividualInfo individual) {
		return ResponseEntity.ok(customerService.verifyIdentity(individual));
	}

	// ACC-023: Create butonu, tum onboarding'i (party + customer + contact/address) yapar.
	@Operation(summary = "Yeni musteri olustur (onboarding)",
			description = "Tek istekte uc servise yazar: party-service (kisi+rol), customer-service "
					+ "(musteri + otomatik 223 tipi hesap), contact-info-service (adres+iletisim). "
					+ "Saga: bir adim basarisiz olursa oncekiler otomatik geri alinir (compensation).")
	@PostMapping("/onboarding")
	public ResponseEntity<CustomerResponse> onboard(@Valid @RequestBody OnboardCustomerRequest request) {
		CustomerResponse response = customerService.onboard(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "Musteriyi id ile getir")
	@GetMapping("/{custId}")
	public ResponseEntity<CustomerResponse> getById(@PathVariable Long custId) {
		return ResponseEntity.ok(customerService.getById(custId));
	}

	@Operation(summary = "Musteri ara",
			description = "firstName/lastName ikisi birlikte verilirse AND ile tek bir grup olusturur; "
					+ "bu grup ile tcNo/acctNo/custId birbirine ve isim grubuna her zaman OR ile baglanir "
					+ "(ör. hem ad-soyad hem tcNo verilirse, ya ada-soyada UYAN ya da o tcNo'ya sahip "
					+ "musteriler doner). Hicbir parametre verilmezse tum (aktif) musteriler doner. "
					+ "Soft-delete edilmis musteriler sonuca dahil olmaz. ACC-007: varsayilan sayfa "
					+ "boyutu 50 - ilk 50 kayit dogrudan doner, kalani page/size ile sayfalanir.")
	@GetMapping("/search")
	public ResponseEntity<Page<CustomerSearchResponse>> search(
			@Parameter(description = "Ad (kismi/prefix eslesme)", example = "Ahmet")
			@RequestParam(required = false) String firstName,
			@Parameter(description = "Soyad (kismi/prefix eslesme)", example = "Yilmaz")
			@RequestParam(required = false) String lastName,
			@Parameter(description = "T.C. Kimlik No (tam eslesme)", example = "10000000146")
			@RequestParam(required = false) String tcNo,
			@Parameter(description = "Hesap no (tam eslesme), format: ACC-{custId}", example = "ACC-1")
			@RequestParam(required = false) String acctNo,
			@Parameter(description = "Musteri no (tam eslesme), CUST-{custId} onekindeki sayisal kisim.", example = "1")
			@RequestParam(required = false) Long custId,
			@Parameter(description = "GSM no (tam eslesme), basinda ulke kodu/sifir olmadan rakamlar.", example = "5551234567")
			@RequestParam(required = false) String gsm,
			@Parameter(description = "Sayfa numarasi (0'dan baslar)", example = "0")
			@RequestParam(defaultValue = "0") int page,
			@Parameter(description = "Sayfa basina kayit sayisi", example = "50")
			@RequestParam(defaultValue = "50") int size) {
		CustomerSearchRequest request = new CustomerSearchRequest(firstName, lastName, tcNo, acctNo, custId, gsm);
		return ResponseEntity.ok(customerService.search(request, PageRequest.of(page, size)));
	}

	@Operation(summary = "Musteriyi sil (soft-delete)",
			description = "Fiziksel silme yapilmaz; musteri ve hesaplari pasife alinir, arama "
					+ "sonuclarindan cikar. party-service'e CustomerDeleted event'i (Kafka, async) "
					+ "yayinlanir - party tarafi bu event'i dinleyip kendi kaydini pasiflestirir.")
	@DeleteMapping("/{custId}")
	public ResponseEntity<Void> softDelete(@PathVariable Long custId) {
		customerService.softDelete(custId);
		return ResponseEntity.noContent().build();
	}

	// --- Editleme: kisisel bilgi party-service'e, adres/contact contact-info-service'e proxylenir ---

	@Operation(summary = "Kisisel bilgiyi getir", description = "party-service'e proxy. birthDate/nationalId de doner (salt-okunur, edit formunda gosterilir).")
	@GetMapping("/{custId}/individual")
	public ResponseEntity<IndividualResponse> getIndividual(@PathVariable Long custId) {
		return ResponseEntity.ok(customerService.getIndividual(custId));
	}

	@Operation(summary = "Kisisel bilgiyi guncelle",
			description = "party-service'e proxy. nationalId/birthDate de guncellenebilir; nationalId "
					+ "baska bir musteriyle cakisirsa 409 doner.")
	@PutMapping("/{custId}/individual")
	public ResponseEntity<IndividualResponse> updateIndividual(@PathVariable Long custId,
			@Valid @RequestBody UpdateIndividualInfo request) {
		return ResponseEntity.ok(customerService.updateIndividual(custId, request));
	}

	@Operation(summary = "Musterinin tum adreslerini listele", description = "contact-info-service'e proxy (max 5 adet).")
	@GetMapping("/{custId}/addresses")
	public ResponseEntity<List<AddressResponse>> getAddresses(@PathVariable Long custId) {
		return ResponseEntity.ok(customerService.getAddresses(custId));
	}

	@Operation(summary = "Musteriye yeni adres ekle",
			description = "contact-info-service'e proxy. Musteri basina en fazla 5 adres kurali burada "
					+ "uygulanir (6. eklemede 409 doner) - contact-info-service bu kurali bilmez, genericttir.")
	@PostMapping("/{custId}/addresses")
	public ResponseEntity<AddressResponse> addAddress(@PathVariable Long custId,
			@Valid @RequestBody AddressEditRequest request) {
		AddressResponse response = customerService.addAddress(custId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "Var olan bir adresi guncelle (primary yapma dahil)",
			description = "contact-info-service'e proxy. addressId'nin gercekten bu custId'ye ait olup "
					+ "olmadigi kontrol edilir (IDOR korumasi) - baska musterinin adresi 404 doner.")
	@PutMapping("/{custId}/addresses/{addressId}")
	public ResponseEntity<AddressResponse> updateAddress(@PathVariable Long custId,
			@Parameter(description = "Guncellenecek adresin id'si (GET .../addresses cevabindaki 'id')") @PathVariable Long addressId,
			@Valid @RequestBody AddressEditRequest request) {
		return ResponseEntity.ok(customerService.updateAddress(custId, addressId, request));
	}

	@Operation(summary = "Contact bilgisini getir", description = "contact-info-service'e proxy (musteri basina tek contact bilgisi).")
	@GetMapping("/{custId}/contact")
	public ResponseEntity<ContactInfo> getContact(@PathVariable Long custId) {
		return ResponseEntity.ok(customerService.getContact(custId));
	}

	@Operation(summary = "Contact bilgisini guncelle",
			description = "contact-info-service'e proxy. email/mobilePhone her zaman zorunlu; "
					+ "homePhone/fax bos gonderilirse mevcut kayit varsa dokunulmaz, yoksa olusturulmaz.")
	@PutMapping("/{custId}/contact")
	public ResponseEntity<ContactInfo> updateContact(@PathVariable Long custId,
			@Valid @RequestBody ContactInfo request) {
		return ResponseEntity.ok(customerService.updateContact(custId, request));
	}

	// --- ACC-001..014: Customer Account tab / Create Billing Account ---

	@Operation(summary = "Musterinin hesaplarini listele", description = "Onboarding'de otomatik acilan varsayilan hesap + sonradan eklenen billing account'lar.")
	@GetMapping("/{custId}/accounts")
	public ResponseEntity<List<CustomerAccountResponse>> getAccounts(@PathVariable Long custId) {
		return ResponseEntity.ok(customerService.getAccounts(custId));
	}

	@Operation(summary = "Musteriye yeni billing account ekle",
			description = "addressId (var olan adres) veya newAddress (yeni adres) alanlarindan tam "
					+ "olarak biri doldurulmali - bkz. CreateBillingAccountRequest.")
	@PostMapping("/{custId}/accounts")
	public ResponseEntity<CustomerAccountResponse> createBillingAccount(@PathVariable Long custId,
			@Valid @RequestBody CreateBillingAccountRequest request) {
		CustomerAccountResponse response = customerService.createBillingAccount(custId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "Bir adresin herhangi bir hesapta kullanilip kullanilmadigini kontrol et",
			description = "contact-info-service'in bir adresi silmeden once soracagi varlik kontrolu. "
					+ "custId altinda degil: silme aninda bilinen tek bilgi addressId'dir.")
	@GetMapping("/accounts/exists-by-address/{addressId}")
	public ResponseEntity<Boolean> existsAccountByAddress(@PathVariable Long addressId) {
		return ResponseEntity.ok(customerService.existsAccountByAddressId(addressId));
	}
}
