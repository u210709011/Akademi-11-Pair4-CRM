package com.etiya.crm.orderservice.clients.controllers;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.etiya.crm.shared.contracts.gnlchar.GnlCharResponse;
import com.etiya.crm.shared.contracts.gnlcharval.GnlCharValResponse;
import com.etiya.crm.shared.contracts.gnlst.GnlStResponse;
import com.etiya.crm.shared.contracts.gnltp.GnlTpResponse;
import com.etiya.crm.shared.contracts.typevalue.TypeValueResponse;

@FeignClient(name = "lookup-service")
public interface LookupClient {

    // saveConfiguration'da charId/charValId'nin gercekten var oldugunu dogrulamak icin.
    @GetMapping("/api/v1/characteristics/{id}")
    GnlCharResponse getCharacteristicById(@PathVariable Long id);

    @GetMapping("/api/v1/characteristic-values/{id}")
    GnlCharValResponse getCharacteristicValueById(@PathVariable Long id);

    @GetMapping("/api/v1/general-types")
    List<GnlTpResponse> getAllGeneralTypes(@RequestParam(required = false) String entCodeName);

    @GetMapping("/api/v1/general-types/resolve/{entCodeName}/{shrtCode}")
    GnlTpResponse resolveGeneralType(@PathVariable String entCodeName, @PathVariable String shrtCode);

    @GetMapping("/api/v1/general-statuses")
    List<GnlStResponse> getAllGeneralStatuses(@RequestParam(required = false) String entCodeName);

    @GetMapping("/api/v1/general-statuses/resolve/{entCodeName}/{shrtCode}")
    GnlStResponse resolveGeneralStatus(@PathVariable String entCodeName, @PathVariable String shrtCode);

    @GetMapping("/api/v1/type-values")
    List<TypeValueResponse> getAllTypeValues();

    @GetMapping("/api/v1/type-values/by-table/{tableName}")
    TypeValueResponse getTypeValueByTable(@PathVariable String tableName);

}
