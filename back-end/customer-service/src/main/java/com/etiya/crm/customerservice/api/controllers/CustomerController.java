package com.etiya.crm.customerservice.api.controllers;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.etiya.crm.customerservice.business.abstracts.CustomerOnboardingService;
import com.etiya.crm.customerservice.business.abstracts.CustomerService;
import com.etiya.crm.customerservice.business.dtos.requests.CustomerSearchRequest;
import com.etiya.crm.customerservice.business.dtos.requests.IndividualInfo;
import com.etiya.crm.customerservice.business.dtos.requests.OnboardCustomerRequest;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerResponse;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerSearchResponse;
import com.etiya.crm.customerservice.business.dtos.responses.IdentityVerificationResponse;
import com.etiya.crm.customerservice.constants.Roles;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Musteri onboarding, arama ve Customer aggregate'inin oz yasam dongusu
 * (getById/soft-delete) uc noktalari. Kisisel bilgi/adres/contact/hesap
 * editleme uc noktalari kendi controller'larina tasindi - bkz.
 * CustomerIndividualController, CustomerAddressController,
 * CustomerContactController, CustomerAccountController.
 */
@Tag(name = "Customers", description = "Musteri onboarding, arama ve yasam dongusu yonetimi")
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('" + Roles.CRM_AGENT + "')")
public class CustomerController {

	private final CustomerService customerService;
	private final CustomerOnboardingService onboardingService;

	// ACC-009..013: adres/kontakt adimlarindan once kimlik dogrulama + tekillik kontrolu.
	@Operation(summary = "Kimlik dogrulama (KPS) + TC no tekillik kontrolu",
			description = "DB'ye hicbir sey yazmaz, sadece dogrular. Onboarding formunun ilk adiminda "
					+ "(adres/contact girilmeden once) cagrilir; ayni dogrulama onboard() icinde de "
					+ "tekrar calisir (defense in depth). Basarisiz olursa 422 (kimlik dogrulanamadi) "
					+ "veya 409 (TC no zaten kayitli) doner.")
	@PostMapping("/onboarding/verify-identity")
	public ResponseEntity<IdentityVerificationResponse> verifyIdentity(@Valid @RequestBody IndividualInfo individual) {
		return ResponseEntity.ok(onboardingService.verifyIdentity(individual));
	}

	// ACC-023: Create butonu, tum onboarding'i (party + customer + contact/address) yapar.
	@Operation(summary = "Yeni musteri olustur (onboarding)",
			description = "Tek istekte uc servise yazar: party-service (kisi+rol), customer-service "
					+ "(musteri + otomatik 223 tipi hesap), contact-info-service (adres+iletisim). "
					+ "Saga: bir adim basarisiz olursa oncekiler otomatik geri alinir (compensation).")
	@PostMapping("/onboarding")
	public ResponseEntity<CustomerResponse> onboard(@Valid @RequestBody OnboardCustomerRequest request) {
		CustomerResponse response = onboardingService.onboard(request);
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
					+ "Soft-delete edilmis musteriler sonuca dahil olmaz. Varsayilan sayfa boyutu "
					+ "10 (front-end'in sayfa basina gosterdigi kayit sayisiyla ayni) - ilk 10 kayit "
					+ "dogrudan doner, kalani page/size ile sayfalanir.")
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
			@Parameter(description = "Sayfa basina kayit sayisi", example = "10")
			@RequestParam(defaultValue = "10") int size,
			@Parameter(description = "Siralama alani - custId/firstName/middleName/lastName/tcNo/role disinda "
					+ "bir deger verilirse ya da hic verilmezse siralama uygulanmaz.", example = "lastName")
			@RequestParam(required = false) String sortBy,
			@Parameter(description = "Siralama yonu", example = "asc")
			@RequestParam(defaultValue = "asc") String sortDir) {
		CustomerSearchRequest request = new CustomerSearchRequest(firstName, lastName, tcNo, acctNo, custId, gsm);
		return ResponseEntity.ok(customerService.search(request, buildPageable(page, size, sortBy, sortDir)));
	}

	/**
	 * sortBy, CustomerSearchView'in gercek alan adlariyla birebir sinirlanir (whitelist) -
	 * hem gelisigüzel property-path'lerin JPA'ya sizmasini engeller hem de front-end'in
	 * tikladigi kolonlarla (search-customer.component.ts SortColumn) ayni kumeyi tutar.
	 * Tam sonuc kumesini (sayfa degil) siralar cunku sort, veritabani sorgusunun bir
	 * parcasidir - front-end artik sadece o an ekrandaki sayfayi kendi tarafinda siralamaz.
	 */
	private static final Set<String> SORTABLE_FIELDS =
			Set.of("custId", "firstName", "middleName", "lastName", "tcNo", "role");

	private Pageable buildPageable(int page, int size, String sortBy, String sortDir) {
		if (sortBy == null || !SORTABLE_FIELDS.contains(sortBy)) {
			return PageRequest.of(page, size);
		}
		Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
		return PageRequest.of(page, size, Sort.by(direction, sortBy));
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
}
