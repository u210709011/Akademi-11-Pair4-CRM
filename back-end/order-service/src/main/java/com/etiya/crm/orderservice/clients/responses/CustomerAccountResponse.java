package com.etiya.crm.orderservice.clients.responses;

public record CustomerAccountResponse(

    Long custAcctId,
    String accountNo,
    String accountName,
    String accountDesc,
    Long addressId,
    Long accountTpId,
    Long acctStId,
    boolean active
) {

}
