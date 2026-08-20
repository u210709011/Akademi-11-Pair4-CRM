package com.etiya.crm.contactinfoservice.api.controllers;

import com.etiya.crm.contactinfoservice.business.abstracts.AddressService;
import com.etiya.crm.contactinfoservice.business.abstracts.ContactMediumService;
import com.etiya.crm.shared.contracts.contactmedium.AddressCommand;
import com.etiya.crm.shared.contracts.contactmedium.ContactMediumCommand;
import com.etiya.crm.shared.contracts.address.CreateAddressRequest;
import com.etiya.crm.shared.contracts.contactmedium.ContactMediumResponse;
import com.etiya.crm.shared.contracts.contactmedium.CreateContactCommand;
import com.etiya.crm.shared.contracts.contactmedium.CreateContactMediumRequest;
import com.etiya.crm.shared.contracts.contactmedium.UpdateContactMediumRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactMediumControllerTest {

    private static final Long CUSTOMER_DATA_TYPE_ID = 12L;

    @Mock
    private ContactMediumService contactMediumService;

    @Mock
    private AddressService addressService;

    @InjectMocks
    private ContactMediumController contactMediumController;

    @Test
    void createContact_createsAddressesAndContactMediums_withCustIdAsRowId() {
        CreateContactCommand command = new CreateContactCommand(
                42L,
                CUSTOMER_DATA_TYPE_ID,
                List.of(new AddressCommand(5L, "Street", "12", "Desc", true)),
                List.of(new ContactMediumCommand(4001L, "user@example.com")));

        contactMediumController.createContact(command);

        ArgumentCaptor<CreateAddressRequest> addressCaptor = ArgumentCaptor.forClass(CreateAddressRequest.class);
        verify(addressService).add(addressCaptor.capture());
        CreateAddressRequest capturedAddress = addressCaptor.getValue();
        assertThat(capturedAddress.rowId()).isEqualTo(42L);
        assertThat(capturedAddress.dataTypeId()).isEqualTo(CUSTOMER_DATA_TYPE_ID);
        assertThat(capturedAddress.houseName()).isEqualTo("12");
        assertThat(capturedAddress.primary()).isTrue();

        ArgumentCaptor<CreateContactMediumRequest> contactCaptor = ArgumentCaptor.forClass(CreateContactMediumRequest.class);
        verify(contactMediumService).add(contactCaptor.capture());
        CreateContactMediumRequest capturedContact = contactCaptor.getValue();
        assertThat(capturedContact.rowId()).isEqualTo(42L);
        assertThat(capturedContact.dataTypeId()).isEqualTo(CUSTOMER_DATA_TYPE_ID);
        assertThat(capturedContact.cntcData()).isEqualTo("user@example.com");
        assertThat(capturedContact.cntcMediumTypeId()).isEqualTo(4001L);
    }

    @Test
    void createContact_callsNothing_whenListsAreEmpty() {
        CreateContactCommand command = new CreateContactCommand(42L, CUSTOMER_DATA_TYPE_ID, List.of(), List.of());

        contactMediumController.createContact(command);

        verify(addressService, times(0)).add(org.mockito.ArgumentMatchers.any());
        verify(contactMediumService, times(0)).add(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void deleteByCustomerId_deactivatesAddressesAndContactMediumsForCustomer() {
        contactMediumController.deleteByCustomerId(42L, CUSTOMER_DATA_TYPE_ID);

        verify(addressService).deactivateAllForRow(42L, CUSTOMER_DATA_TYPE_ID);
        verify(contactMediumService).deactivateAllForRow(42L, CUSTOMER_DATA_TYPE_ID);
    }

    @Test
    void getAll_returnsByRowIdAndDataTypeId_whenBothProvided() {
        ContactMediumResponse response = new ContactMediumResponse(1L, 42L, CUSTOMER_DATA_TYPE_ID,
                "user@example.com", 4001L, null, null, null, null);
        when(contactMediumService.getByRowIdAndDataTypeId(42L, CUSTOMER_DATA_TYPE_ID)).thenReturn(List.of(response));

        ResponseEntity<List<ContactMediumResponse>> result = contactMediumController.getAll(42L, CUSTOMER_DATA_TYPE_ID);

        assertThat(result.getBody()).containsExactly(response);
        verify(contactMediumService, times(0)).getAll();
    }

    @Test
    void getAll_returnsAll_whenRowIdOrDataTypeIdMissing() {
        ContactMediumResponse response = new ContactMediumResponse(1L, 42L, CUSTOMER_DATA_TYPE_ID,
                "user@example.com", 4001L, null, null, null, null);
        when(contactMediumService.getAll()).thenReturn(List.of(response));

        ResponseEntity<List<ContactMediumResponse>> result = contactMediumController.getAll(null, null);

        assertThat(result.getBody()).containsExactly(response);
    }

    @Test
    void getById_returnsContactMedium() {
        ContactMediumResponse response = new ContactMediumResponse(1L, 42L, CUSTOMER_DATA_TYPE_ID,
                "user@example.com", 4001L, null, null, null, null);
        when(contactMediumService.getById(1L)).thenReturn(response);

        ResponseEntity<ContactMediumResponse> result = contactMediumController.getById(1L);

        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void add_returns201WithCreatedContactMedium() {
        CreateContactMediumRequest request = new CreateContactMediumRequest(42L, CUSTOMER_DATA_TYPE_ID,
                "user@example.com", 4001L);
        ContactMediumResponse response = new ContactMediumResponse(1L, 42L, CUSTOMER_DATA_TYPE_ID,
                "user@example.com", 4001L, null, null, null, null);
        when(contactMediumService.add(request)).thenReturn(response);

        ResponseEntity<ContactMediumResponse> result = contactMediumController.add(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void update_returnsUpdatedContactMedium() {
        UpdateContactMediumRequest request = new UpdateContactMediumRequest("user2@example.com", 4001L);
        ContactMediumResponse response = new ContactMediumResponse(1L, 42L, CUSTOMER_DATA_TYPE_ID,
                "user2@example.com", 4001L, null, null, null, null);
        when(contactMediumService.update(1L, request)).thenReturn(response);

        ResponseEntity<ContactMediumResponse> result = contactMediumController.update(1L, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void delete_returns204AndDelegatesToService() {
        ResponseEntity<Void> result = contactMediumController.delete(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(contactMediumService).delete(1L);
    }

}
