package com.etiya.crm.contactinfoservice.mapper;

import com.etiya.crm.contactinfoservice.business.dtos.requests.CreateContactMediumRequest;
import com.etiya.crm.contactinfoservice.business.dtos.requests.UpdateContactMediumRequest;
import com.etiya.crm.contactinfoservice.business.dtos.responses.ContactMediumResponse;
import com.etiya.crm.contactinfoservice.entities.concretes.ContactMedium;

public class ContactMediumMapper {

    private ContactMediumMapper() {
    }

    public static ContactMedium toEntity(CreateContactMediumRequest request) {
        ContactMedium contactMedium = new ContactMedium();
        contactMedium.setRowId(request.getRowId());
        contactMedium.setDataTypeId(request.getDataTypeId());
        contactMedium.setCntcData(request.getCntcData());
        contactMedium.setCntcMediumTypeId(request.getCntcMediumTypeId());
        contactMedium.setStatusId(request.getStatusId());
        return contactMedium;
    }

    public static void updateEntity(ContactMedium contactMedium, UpdateContactMediumRequest request) {
        contactMedium.setCntcData(request.getCntcData());
        contactMedium.setCntcMediumTypeId(request.getCntcMediumTypeId());
        contactMedium.setStatusId(request.getStatusId());
    }

    public static ContactMediumResponse toResponse(ContactMedium contactMedium) {
        ContactMediumResponse response = new ContactMediumResponse();
        response.setId(contactMedium.getId());
        response.setRowId(contactMedium.getRowId());
        response.setDataTypeId(contactMedium.getDataTypeId());
        response.setCntcData(contactMedium.getCntcData());
        response.setCntcMediumTypeId(contactMedium.getCntcMediumTypeId());
        response.setStatusId(contactMedium.getStatusId());
        response.setCdate(contactMedium.getCdate());
        response.setCuser(contactMedium.getCuser());
        response.setUdate(contactMedium.getUdate());
        response.setUuser(contactMedium.getUuser());
        return response;
    }

}
