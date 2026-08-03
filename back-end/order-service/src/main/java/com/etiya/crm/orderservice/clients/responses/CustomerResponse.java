package com.etiya.crm.orderservice.clients.responses;

import java.util.List;

public record CustomerResponse(

    // same as customer response from customer service
    Long custId,
    Long partyRoleId,
    Long custTpId,
    boolean active,
    List<CustomerAccountResponse> accounts
) {



}
