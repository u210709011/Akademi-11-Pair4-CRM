package com.etiya.crm.productservice.clients;

import com.etiya.crm.shared.contracts.gnltp.GnlTpResponse;
import com.etiya.crm.shared.contracts.gnlst.GnlStResponse;
import com.etiya.crm.shared.contracts.rsrcspec.RsrcSpecResponse;
import com.etiya.crm.shared.contracts.srvcspec.SrvcSpecResponse;
import com.etiya.crm.shared.contracts.gnlchar.GnlCharResponse;
import com.etiya.crm.shared.contracts.gnlcharval.GnlCharValResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** lookup-service REST kontrati - bkz. lookup-service/LOOKUP_SERVICE_INTEGRATION.md. */
@FeignClient(name = "lookup-service")
public interface LookupClient {

    // ---- kod ile çözülenler (GNL_TP / GNL_ST) ----

    @GetMapping("/api/v1/general-types/resolve/{entCodeName}/{shrtCode}")
    GnlTpResponse resolveType(@PathVariable("entCodeName") String entCodeName,
                              @PathVariable("shrtCode") String shrtCode);

    @GetMapping("/api/v1/general-statuses/resolve/{entCodeName}/{shrtCode}")
    GnlStResponse resolveStatus(@PathVariable("entCodeName") String entCodeName,
                                @PathVariable("shrtCode") String shrtCode);

    // ---- id ile doğrudan getirilenler (bağımsız tablolar - ileride kullanılacak) ----

    @GetMapping("/api/v1/resource-specs/{id}")
    RsrcSpecResponse getResourceSpecById(@PathVariable("id") Long id);

    @GetMapping("/api/v1/service-specs/{id}")
    SrvcSpecResponse getServiceSpecById(@PathVariable("id") Long id);

    @GetMapping("/api/v1/characteristics/{id}")
    GnlCharResponse getCharacteristicById(@PathVariable("id") Long id);

    @GetMapping("/api/v1/characteristic-values/{id}")
    GnlCharValResponse getCharacteristicValueById(@PathVariable("id") Long id);
}