package com.etiya.crm.customerservice.business.dtos.requests;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Bu paketteki record'lar salt veri tasiyicisi (validation dogrulamasi
 * ayri katmanda) - burada sadece alanlarin dogru tasindigi (accessor'lar,
 * dolayisiyla record'un kendisi) dogrulanir.
 */
class RequestDtosTest {

	@Test
	void addressEditRequest_carriesAllFields() {
		AddressEditRequest request = new AddressEditRequest(5L, "Cad", "No 1", "Ev", true);

		assertThat(request.cityId()).isEqualTo(5L);
		assertThat(request.streetName()).isEqualTo("Cad");
		assertThat(request.buildingName()).isEqualTo("No 1");
		assertThat(request.addressDesc()).isEqualTo("Ev");
		assertThat(request.primary()).isTrue();
	}

	@Test
	void addressInfo_carriesAllFields() {
		AddressInfo info = new AddressInfo(5L, "Cad", "No 1", "Ev");

		assertThat(info.cityId()).isEqualTo(5L);
		assertThat(info.streetName()).isEqualTo("Cad");
		assertThat(info.buildingName()).isEqualTo("No 1");
		assertThat(info.addressDesc()).isEqualTo("Ev");
	}

	@Test
	void contactInfo_carriesAllFields() {
		ContactInfo contact = new ContactInfo("a@b.com", "5551234567", "2121234567", "12345678901");

		assertThat(contact.email()).isEqualTo("a@b.com");
		assertThat(contact.mobilePhone()).isEqualTo("5551234567");
		assertThat(contact.homePhone()).isEqualTo("2121234567");
		assertThat(contact.fax()).isEqualTo("12345678901");
	}

	@Test
	void createBillingAccountRequest_carriesAllFields() {
		AddressInfo newAddress = new AddressInfo(5L, "Cad", "No 1", "Ev");
		CreateBillingAccountRequest request = new CreateBillingAccountRequest("Home", "Aylik fatura", null,
				newAddress);

		assertThat(request.accountName()).isEqualTo("Home");
		assertThat(request.accountDesc()).isEqualTo("Aylik fatura");
		assertThat(request.addressId()).isNull();
		assertThat(request.newAddress()).isEqualTo(newAddress);
	}

	@Test
	void customerSearchRequest_carriesAllFields() {
		CustomerSearchRequest request = new CustomerSearchRequest("Ahmet", "Yilmaz", "10000000146", "000042", 10L,
				"5551234567");

		assertThat(request.firstName()).isEqualTo("Ahmet");
		assertThat(request.lastName()).isEqualTo("Yilmaz");
		assertThat(request.tcNo()).isEqualTo("10000000146");
		assertThat(request.acctNo()).isEqualTo("000042");
		assertThat(request.custId()).isEqualTo(10L);
		assertThat(request.gsm()).isEqualTo("5551234567");
	}

	@Test
	void individualInfo_carriesAllFields() {
		LocalDate birthDate = LocalDate.of(1990, 6, 15);
		IndividualInfo info = new IndividualInfo("Ahmet", "Can", "Yilmaz", birthDate, 1L, "Ayse", "Mehmet",
				"10000000146");

		assertThat(info.firstName()).isEqualTo("Ahmet");
		assertThat(info.middleName()).isEqualTo("Can");
		assertThat(info.lastName()).isEqualTo("Yilmaz");
		assertThat(info.birthDate()).isEqualTo(birthDate);
		assertThat(info.genderId()).isEqualTo(1L);
		assertThat(info.motherName()).isEqualTo("Ayse");
		assertThat(info.fatherName()).isEqualTo("Mehmet");
		assertThat(info.nationalId()).isEqualTo("10000000146");
	}

	@Test
	void onboardCustomerRequest_carriesAllFields() {
		IndividualInfo individual = new IndividualInfo("Ahmet", null, "Yilmaz", LocalDate.of(1990, 6, 15), 1L, null,
				null, "10000000146");
		AddressInfo address = new AddressInfo(5L, "Cad", "No 1", "Ev");
		ContactInfo contact = new ContactInfo("a@b.com", "5551234567", null, null);

		OnboardCustomerRequest request = new OnboardCustomerRequest(individual, List.of(address), contact);

		assertThat(request.individual()).isEqualTo(individual);
		assertThat(request.addresses()).containsExactly(address);
		assertThat(request.contact()).isEqualTo(contact);
	}

	@Test
	void updateBillingAccountRequest_carriesAllFields() {
		UpdateBillingAccountRequest request = new UpdateBillingAccountRequest("Home", "Aylik fatura", 5L, null);

		assertThat(request.accountName()).isEqualTo("Home");
		assertThat(request.accountDesc()).isEqualTo("Aylik fatura");
		assertThat(request.addressId()).isEqualTo(5L);
		assertThat(request.newAddress()).isNull();
	}

	@Test
	void updateBillingAccountStatusRequest_carriesStatus() {
		UpdateBillingAccountStatusRequest request = new UpdateBillingAccountStatusRequest(
				UpdateBillingAccountStatusRequest.PASSIVE);

		assertThat(request.status()).isEqualTo("PASSIVE");
		assertThat(UpdateBillingAccountStatusRequest.ACTIVE).isEqualTo("ACTIVE");
	}

	@Test
	void updateIndividualInfo_carriesAllFields() {
		LocalDate birthDate = LocalDate.of(1990, 6, 15);
		UpdateIndividualInfo info = new UpdateIndividualInfo("Ahmet", "Can", "Yilmaz", 1L, "Ayse", "Mehmet",
				birthDate, "10000000146");

		assertThat(info.firstName()).isEqualTo("Ahmet");
		assertThat(info.lastName()).isEqualTo("Yilmaz");
		assertThat(info.genderId()).isEqualTo(1L);
		assertThat(info.birthDate()).isEqualTo(birthDate);
		assertThat(info.nationalId()).isEqualTo("10000000146");
	}
}
