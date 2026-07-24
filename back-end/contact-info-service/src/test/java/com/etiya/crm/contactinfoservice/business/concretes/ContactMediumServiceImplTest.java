package com.etiya.crm.contactinfoservice.business.concretes;

import com.etiya.crm.shared.contracts.contactmedium.CreateContactMediumRequest;
import com.etiya.crm.shared.contracts.contactmedium.UpdateContactMediumRequest;
import com.etiya.crm.contactinfoservice.business.exceptions.InvalidContactMediumFormatException;
import com.etiya.crm.contactinfoservice.business.rules.ContactMediumBusinessRules;
import com.etiya.crm.contactinfoservice.dataAccess.abstracts.ContactMediumRepository;
import com.etiya.crm.contactinfoservice.entities.concretes.ContactMedium;
import com.etiya.crm.shared.events.outbox.OutboxEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactMediumServiceImplTest {

    @Mock
    private ContactMediumRepository contactMediumRepository;

    @Mock
    private ContactMediumBusinessRules contactMediumBusinessRules;

    @Mock
    private OutboxEventPublisher outboxEventPublisher;

    @InjectMocks
    private ContactMediumServiceImpl contactMediumService;

    @Test
    void add_savesContactMedium_whenFormatIsValid() {
        CreateContactMediumRequest request = new CreateContactMediumRequest(10L, 1L, "user@example.com", 1L);
        when(contactMediumRepository.save(any(ContactMedium.class))).thenAnswer(invocation -> invocation.getArgument(0));

        contactMediumService.add(request);

        verify(contactMediumBusinessRules).checkDataFormat("user@example.com", 1L);
        verify(contactMediumRepository).save(any(ContactMedium.class));
    }

    @Test
    void add_throws_whenFormatIsInvalid() {
        CreateContactMediumRequest request = new CreateContactMediumRequest(10L, 1L, "not-an-email", 1L);
        doThrow(new InvalidContactMediumFormatException("Invalid email format"))
                .when(contactMediumBusinessRules).checkDataFormat("not-an-email", 1L);

        assertThatThrownBy(() -> contactMediumService.add(request))
                .isInstanceOf(InvalidContactMediumFormatException.class);

        verify(contactMediumRepository, never()).save(any());
    }

    @Test
    void update_throws_whenFormatIsInvalid() {
        ContactMedium existing = new ContactMedium();
        existing.setId(1L);
        when(contactMediumBusinessRules.checkIfContactMediumExists(1L)).thenReturn(existing);

        UpdateContactMediumRequest request = new UpdateContactMediumRequest("abc-phone", 2L);
        doThrow(new InvalidContactMediumFormatException("Invalid phone number format"))
                .when(contactMediumBusinessRules).checkDataFormat("abc-phone", 2L);

        assertThatThrownBy(() -> contactMediumService.update(1L, request))
                .isInstanceOf(InvalidContactMediumFormatException.class);

        verify(contactMediumRepository, never()).save(any());
    }

    @Test
    void delete_deactivatesContactMedium() {
        ContactMedium contactMedium = new ContactMedium();
        contactMedium.setId(1L);
        contactMedium.setActive(true);
        when(contactMediumBusinessRules.checkIfContactMediumExists(1L)).thenReturn(contactMedium);
        when(contactMediumRepository.save(contactMedium)).thenReturn(contactMedium);

        contactMediumService.delete(1L);

        assertThat(contactMedium.isActive()).isFalse();
        verify(contactMediumRepository).save(contactMedium);
    }

    @Test
    void deactivateAllForRow_deactivatesAllActiveContactMediumsForRow() {
        ContactMedium first = new ContactMedium();
        first.setActive(true);
        ContactMedium second = new ContactMedium();
        second.setActive(true);

        when(contactMediumRepository.findAllByRowIdAndDataTypeIdAndActiveTrue(10L, 1L))
                .thenReturn(List.of(first, second));

        contactMediumService.deactivateAllForRow(10L, 1L);

        assertThat(first.isActive()).isFalse();
        assertThat(second.isActive()).isFalse();
        verify(contactMediumRepository).saveAll(List.of(first, second));
    }

}
