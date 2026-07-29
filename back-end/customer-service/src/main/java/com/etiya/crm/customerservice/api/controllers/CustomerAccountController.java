package com.etiya.crm.customerservice.api.controllers;

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

import com.etiya.crm.customerservice.business.abstracts.BillingAccountService;
import com.etiya.crm.customerservice.business.dtos.requests.CreateBillingAccountRequest;
import com.etiya.crm.customerservice.business.dtos.requests.UpdateBillingAccountRequest;
import com.etiya.crm.customerservice.business.dtos.responses.CustomerAccountResponse;
import com.etiya.crm.customerservice.constants.Roles;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Musterinin fatura hesaplari (Customer Account tab / Create Billing Account) uc noktalari. */
@Tag(name = "Customers", description = "Musteri fatura hesabi yonetimi")
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('" + Roles.CRM_AGENT + "')")
public class CustomerAccountController {

	private final BillingAccountService billingAccountService;

	@Operation(summary = "Musterinin hesaplarini listele",
			description = "Onboarding'de otomatik acilan varsayilan hesap + sonradan eklenen billing "
					+ "account'lar. ACC-009: varsayilan sayfa boyutu 5, ilk 5 kayit dogrudan doner, kalani "
					+ "page/size ile sayfalanir.")
	@GetMapping("/{custId}/accounts")
	public ResponseEntity<Page<CustomerAccountResponse>> getAccounts(@PathVariable Long custId,
			@Parameter(description = "Sayfa numarasi (0'dan baslar)", example = "0")
			@RequestParam(defaultValue = "0") int page,
			@Parameter(description = "Sayfa basina kayit sayisi", example = "5")
			@RequestParam(defaultValue = "5") int size) {
		return ResponseEntity.ok(billingAccountService.getAccounts(custId, PageRequest.of(page, size)));
	}

	@Operation(summary = "Musteriye yeni billing account ekle",
			description = "addressId (var olan adres) veya newAddress (yeni adres) alanlarindan tam "
					+ "olarak biri doldurulmali - bkz. CreateBillingAccountRequest.")
	@PostMapping("/{custId}/accounts")
	public ResponseEntity<CustomerAccountResponse> createBillingAccount(@PathVariable Long custId,
			@Valid @RequestBody CreateBillingAccountRequest request) {
		CustomerAccountResponse response = billingAccountService.createBillingAccount(custId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "Billing account guncelle",
			description = "Sadece Account Name/Account Description/adres guncellenebilir - accountNo ve "
					+ "accountTpId DEGISTIRILEMEZ. addressId (var olan adres) veya newAddress (yeni adres) "
					+ "alanlarindan tam olarak biri doldurulmali. IDOR: baska musterinin hesabi 404 doner.")
	@PutMapping("/{custId}/accounts/{accountId}")
	public ResponseEntity<CustomerAccountResponse> updateBillingAccount(@PathVariable Long custId,
			@Parameter(description = "Guncellenecek hesabin id'si") @PathVariable Long accountId,
			@Valid @RequestBody UpdateBillingAccountRequest request) {
		return ResponseEntity.ok(billingAccountService.updateBillingAccount(custId, accountId, request));
	}

	@Operation(summary = "Billing account sil (soft-delete)",
			description = "Aktif hesap silinemez (409, 'This billing account is active and cannot be "
					+ "deleted.'). Urun guard'i (pasif hesaba bagli urun) order-service'i bekliyor, henuz "
					+ "uygulanmadi - TODO. IDOR: baska musterinin hesabi 404 doner.")
	@DeleteMapping("/{custId}/accounts/{accountId}")
	public ResponseEntity<Void> deleteBillingAccount(@PathVariable Long custId,
			@Parameter(description = "Silinecek hesabin id'si") @PathVariable Long accountId) {
		billingAccountService.deleteBillingAccount(custId, accountId);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Bir adresin herhangi bir hesapta kullanilip kullanilmadigini kontrol et",
			description = "contact-info-service'in bir adresi silmeden once soracagi varlik kontrolu. "
					+ "custId altinda degil: silme aninda bilinen tek bilgi addressId'dir.")
	@GetMapping("/accounts/exists-by-address/{addressId}")
	public ResponseEntity<Boolean> existsAccountByAddress(@PathVariable Long addressId) {
		return ResponseEntity.ok(billingAccountService.existsAccountByAddressId(addressId));
	}
}
