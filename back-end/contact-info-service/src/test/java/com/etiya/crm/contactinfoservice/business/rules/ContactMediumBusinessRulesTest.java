package com.etiya.crm.contactinfoservice.business.rules;

import com.etiya.crm.contactinfoservice.business.exceptions.InvalidContactMediumFormatException;
import com.etiya.crm.contactinfoservice.dataAccess.abstracts.ContactMediumRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ContactMediumBusinessRulesTest {

    @Mock
    private ContactMediumRepository contactMediumRepository;

    private ContactMediumBusinessRules contactMediumBusinessRules;

    @Test
    void checkDataFormat_passes_forValidEmail() {
        contactMediumBusinessRules = new ContactMediumBusinessRules(contactMediumRepository);

        contactMediumBusinessRules.checkDataFormat("user@example.com", "EML");
        // no exception thrown
    }

    @Test
    void checkDataFormat_throws_forInvalidEmail() {
        contactMediumBusinessRules = new ContactMediumBusinessRules(contactMediumRepository);

        assertThatThrownBy(() -> contactMediumBusinessRules.checkDataFormat("not-an-email", "EML"))
                .isInstanceOf(InvalidContactMediumFormatException.class);
    }

    @Test
    void checkDataFormat_passes_forValidMobilePhone() {
        contactMediumBusinessRules = new ContactMediumBusinessRules(contactMediumRepository);

        contactMediumBusinessRules.checkDataFormat("5551234567", "GSM");
        // no exception thrown
    }

    @Test
    void checkDataFormat_throws_forInvalidPhone() {
        contactMediumBusinessRules = new ContactMediumBusinessRules(contactMediumRepository);

        assertThatThrownBy(() -> contactMediumBusinessRules.checkDataFormat("abc-phone", "GSM"))
                .isInstanceOf(InvalidContactMediumFormatException.class);
    }

    @Test
    void checkDataFormat_passes_forValidLandline() {
        contactMediumBusinessRules = new ContactMediumBusinessRules(contactMediumRepository);

        contactMediumBusinessRules.checkDataFormat("2125550000", "PSTN");
        // no exception thrown
    }

    @Test
    void checkDataFormat_throws_forInvalidLandline() {
        contactMediumBusinessRules = new ContactMediumBusinessRules(contactMediumRepository);

        assertThatThrownBy(() -> contactMediumBusinessRules.checkDataFormat("abc", "PSTN"))
                .isInstanceOf(InvalidContactMediumFormatException.class);
    }

    @Test
    void checkDataFormat_passes_forValidFax() {
        contactMediumBusinessRules = new ContactMediumBusinessRules(contactMediumRepository);

        contactMediumBusinessRules.checkDataFormat("2125550000", "FAX");
        // no exception thrown
    }

    @Test
    void checkDataFormat_throws_forInvalidFax() {
        contactMediumBusinessRules = new ContactMediumBusinessRules(contactMediumRepository);

        assertThatThrownBy(() -> contactMediumBusinessRules.checkDataFormat("abc", "FAX"))
                .isInstanceOf(InvalidContactMediumFormatException.class);
    }

    @Test
    void checkDataFormat_passesSilently_forUnknownTypeCode() {
        contactMediumBusinessRules = new ContactMediumBusinessRules(contactMediumRepository);

        contactMediumBusinessRules.checkDataFormat("anything-goes-here", "UNKNOWN");
        // no exception thrown - no format rule applies to unrecognized type codes
    }

}
