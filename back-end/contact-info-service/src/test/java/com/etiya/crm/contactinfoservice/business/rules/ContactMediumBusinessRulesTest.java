package com.etiya.crm.contactinfoservice.business.rules;

import com.etiya.crm.contactinfoservice.business.exceptions.InvalidContactMediumFormatException;
import com.etiya.crm.contactinfoservice.clients.LookupClient;
import com.etiya.crm.contactinfoservice.dataAccess.abstracts.ContactMediumRepository;
import com.etiya.crm.shared.contracts.gnltp.GnlTpResponse;
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

    private static GnlTpResponse gnlTp(Long id, String shrtCode) {
        return new GnlTpResponse(id, shrtCode, shrtCode, shrtCode, "CNTC_MEDIUM", "CNTC_MEDIUM", true,
                null, null, null, null);
    }

    @Test
    void checkDataFormat_passes_forValidEmail() {
        contactMediumBusinessRules = new ContactMediumBusinessRules(contactMediumRepository, lookupClient);
        when(lookupClient.getById(1L)).thenReturn(gnlTp(1L, "EML"));

        contactMediumBusinessRules.checkDataFormat("user@example.com", 1L);
        // no exception thrown
    }

    @Test
    void checkDataFormat_throws_forInvalidEmail() {
        contactMediumBusinessRules = new ContactMediumBusinessRules(contactMediumRepository, lookupClient);
        when(lookupClient.getById(1L)).thenReturn(gnlTp(1L, "EML"));

        assertThatThrownBy(() -> contactMediumBusinessRules.checkDataFormat("not-an-email", 1L))
                .isInstanceOf(InvalidContactMediumFormatException.class);
    }

    @Test
    void checkDataFormat_passes_forValidMobilePhone() {
        contactMediumBusinessRules = new ContactMediumBusinessRules(contactMediumRepository, lookupClient);
        when(lookupClient.getById(2L)).thenReturn(gnlTp(2L, "GSM"));

        contactMediumBusinessRules.checkDataFormat("5551234567", 2L);
        // no exception thrown
    }

    @Test
    void checkDataFormat_throws_forInvalidPhone() {
        contactMediumBusinessRules = new ContactMediumBusinessRules(contactMediumRepository, lookupClient);
        when(lookupClient.getById(2L)).thenReturn(gnlTp(2L, "GSM"));

        assertThatThrownBy(() -> contactMediumBusinessRules.checkDataFormat("abc-phone", 2L))
                .isInstanceOf(InvalidContactMediumFormatException.class);
    }

}
