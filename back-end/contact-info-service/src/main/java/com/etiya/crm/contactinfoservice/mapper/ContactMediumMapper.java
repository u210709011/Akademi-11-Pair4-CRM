package com.etiya.crm.contactinfoservice.mapper;

import com.etiya.crm.contactinfoservice.entities.concretes.ContactMedium;
import com.etiya.crm.shared.contracts.contactmedium.ContactMediumResponse;
import com.etiya.crm.shared.contracts.contactmedium.CreateContactMediumRequest;
import com.etiya.crm.shared.contracts.contactmedium.UpdateContactMediumRequest;

public class ContactMediumMapper {

    private ContactMediumMapper() {
    }

    public static ContactMedium toEntity(CreateContactMediumRequest request) {
        ContactMedium contactMedium = new ContactMedium();
        contactMedium.setRowId(request.rowId());
        contactMedium.setDataTypeId(request.dataTypeId());
        contactMedium.setCntcData(request.cntcData());
        contactMedium.setCntcMediumTypeId(request.cntcMediumTypeId());
        return contactMedium;
    }

    public static void updateEntity(ContactMedium contactMedium, UpdateContactMediumRequest request) {
        contactMedium.setCntcData(request.cntcData());
        contactMedium.setCntcMediumTypeId(request.cntcMediumTypeId());
    }

    public static ContactMediumResponse toResponse(ContactMedium contactMedium) {
        return new ContactMediumResponse(
                contactMedium.getId(),
                contactMedium.getRowId(),
                contactMedium.getDataTypeId(),
                contactMedium.getCntcData(),
                contactMedium.getCntcMediumTypeId(),
                contactMedium.getCdate(),
                contactMedium.getCuser(),
                contactMedium.getUdate(),
                contactMedium.getUuser());
    }

}
