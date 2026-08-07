package com.crmlite.ui.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * FR-002 arama kriterleri.
 *
 * <p>UI alan adlari ile API parametre adlari <b>farklidir</b>; esleme burada tek noktada tutulur:
 * <table>
 *   <tr><th>UI alani</th><th>API parametresi</th></tr>
 *   <tr><td>NAT ID Number</td><td>{@code tcNo}</td></tr>
 *   <tr><td>Customer ID</td><td>{@code custId}</td></tr>
 *   <tr><td>Account Number</td><td>{@code acctNo}</td></tr>
 *   <tr><td>GSM Number</td><td>{@code gsm}</td></tr>
 *   <tr><td>First Name</td><td>{@code firstName}</td></tr>
 *   <tr><td>Last Name</td><td>{@code lastName}</td></tr>
 *   <tr><td>Order Number</td><td>— (API karsiligi yok, bkz. izlenebilirlik matrisi)</td></tr>
 * </table>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SearchCriteria(
        String tcNo,
        String custId,
        String acctNo,
        String gsm,
        String firstName,
        String lastName) {

    public static SearchCriteria byNationalId(String tcNo) {
        return new SearchCriteria(tcNo, null, null, null, null, null);
    }

    public static SearchCriteria byCustomerId(String custId) {
        return new SearchCriteria(null, custId, null, null, null, null);
    }

    public static SearchCriteria byAccountNumber(String acctNo) {
        return new SearchCriteria(null, null, acctNo, null, null, null);
    }

    public static SearchCriteria byGsm(String gsm) {
        return new SearchCriteria(null, null, null, gsm, null, null);
    }

    /** FR-002 ACC-002: First Name ve Last Name birbiriyle AND mantigi ile calisir. */
    public static SearchCriteria byFullName(String firstName, String lastName) {
        return new SearchCriteria(null, null, null, null, firstName, lastName);
    }

    /** Yalnizca dolu olan alanlari query parametresi olarak dondurur. */
    public Map<String, String> toQueryParams() {
        Map<String, String> params = new LinkedHashMap<>();
        putIfPresent(params, "tcNo", tcNo);
        putIfPresent(params, "custId", custId);
        putIfPresent(params, "acctNo", acctNo);
        putIfPresent(params, "gsm", gsm);
        putIfPresent(params, "firstName", firstName);
        putIfPresent(params, "lastName", lastName);
        return params;
    }

    private static void putIfPresent(Map<String, String> params, String key, String value) {
        if (value != null && !value.isBlank()) {
            params.put(key, value);
        }
    }
}
