package com.etiya.crm.customerservice.api.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.etiya.crm.customerservice.business.abstracts.CustomerContactService;
import com.etiya.crm.customerservice.business.dtos.requests.ContactInfo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerContactControllerTest {

	@Mock
	private CustomerContactService contactService;

	@InjectMocks
	private CustomerContactController controller;

	@Test
	void getContact_returns200WithServiceResult() {
		ContactInfo contact = new ContactInfo("a@b.com", "5551234567", null, null);
		when(contactService.getContact(10L)).thenReturn(contact);

		ResponseEntity<ContactInfo> response = controller.getContact(10L);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isSameAs(contact);
	}

	@Test
	void updateContact_returns200WithUpdatedResult() {
		ContactInfo request = new ContactInfo("new@b.com", "5551234567", null, null);
		when(contactService.updateContact(10L, request)).thenReturn(request);

		ResponseEntity<ContactInfo> response = controller.updateContact(10L, request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isSameAs(request);
	}
}
