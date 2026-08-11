package com.crmlite.ui.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * {@code /api/v1/customers/{custId}/accounts} yanit govdesi (backend:
 * {@code CustomerAccountResponse}).
 *
 * <p>{@code accountTpId} hesabin tipini ayirir: onboarding'de acilan varsayilan
 * hesap {@code CUST_ACCT}, FR-008 ile acilanlar {@code BILL_ACCT} tipindedir.
 * FR-007 ACC-003 yalnizca ikincisini silme engeli sayar.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BillingAccountResponse(
        Long custAcctId,
        String accountNo,
        String accountName,
        String accountDesc,
        Long accountTpId,
        Long addressId,
        Long acctStId,
        Boolean active) {

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }
}
