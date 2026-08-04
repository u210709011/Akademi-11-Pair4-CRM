package com.etiya.crm.customerservice.business.dtos.responses;

import java.util.List;

/**
 * GET /api/v1/customers/accounts/by-address/{addressId} yaniti - bir adrese bagli TUM (silinmemis)
 * billing account'larin sayisi + kendisi. existsAccountByAddressId (boolean) ile ayni veriye dayanir,
 * onun genisletilmis hali - contact-info-service'in boolean check'ini bozmamak icin ayri endpoint.
 */
public record AddressBillingAccountsResponse(long count, List<CustomerAccountResponse> accounts) {
}
