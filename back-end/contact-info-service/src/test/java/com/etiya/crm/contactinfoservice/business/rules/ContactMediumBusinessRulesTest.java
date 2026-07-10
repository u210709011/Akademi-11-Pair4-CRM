package com.etiya.crm.contactinfoservice.business.rules;

import com.etiya.crm.contactinfoservice.business.exceptions.InvalidContactMediumFormatException;
import com.etiya.crm.contactinfoservice.clients.LookupClient;
import com.etiya.crm.contactinfoservice.clients.LookupValueResponse;
import com.etiya.crm.contactinfoservice.constants.LookupGroups;
import com.etiya.crm.contactinfoservice.dataAccess.abstracts.ContactMediumRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactMediumBusinessRulesTest {

    @Mock
    private ContactMediumRepository contactMediumRepository;

    @Mock
    private LookupClient lookupClient;

    private ContactMediumBusinessRules contactMediumBusinessRules;

    @Test
    void checkDataFormat_passes_forValidEmail() {
        contactMediumBusinessRules = new ContactMediumBusinessRules(contactMediumRepository, lookupClient);
        when(lookupClient.getById(LookupGroups.CONTACT_MEDIUM_TYPE, 1L))
                .thenReturn(new LookupValueResponse(1L, "EMAIL", "Email"));

        contactMediumBusinessRules.checkDataFormat("user@example.com", 1L);
        // no exception thrown
    }

    @Test
    void checkDataFormat_throws_forInvalidEmail() {
        contactMediumBusinessRules = new ContactMediumBusinessRules(contactMediumRepository, lookupClient);
        when(lookupClient.getById(LookupGroups.CONTACT_MEDIUM_TYPE, 1L))
                .thenReturn(new LookupValueResponse(1L, "EMAIL", "Email"));

        assertThatThrownBy(() -> contactMediumBusinessRules.checkDataFormat("not-an-email", 1L))
                .isInstanceOf(InvalidContactMediumFormatException.class);
    }

    @Test
    void checkDataFormat_passes_forValidMobilePhone() {
        contactMediumBusinessRules = new ContactMediumBusinessRules(contactMediumRepository, lookupClient);
        when(lookupClient.getById(LookupGroups.CONTACT_MEDIUM_TYPE, 2L))
                .thenReturn(new LookupValueResponse(2L, "MOBILE_PHONE", "Mobile Phone"));

        contactMediumBusinessRules.checkDataFormat("5551234567", 2L);
        // no exception thrown
    }

    @Test
    void checkDataFormat_throws_forInvalidPhone() {
        contactMediumBusinessRules = new ContactMediumBusinessRules(contactMediumRepository, lookupClient);
        when(lookupClient.getById(LookupGroups.CONTACT_MEDIUM_TYPE, 2L))
                .thenReturn(new LookupValueResponse(2L, "MOBILE_PHONE", "Mobile Phone"));

        assertThatThrownBy(() -> contactMediumBusinessRules.checkDataFormat("abc-phone", 2L))
                .isInstanceOf(InvalidContactMediumFormatException.class);
    }

}
