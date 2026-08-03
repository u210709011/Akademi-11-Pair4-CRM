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

}
