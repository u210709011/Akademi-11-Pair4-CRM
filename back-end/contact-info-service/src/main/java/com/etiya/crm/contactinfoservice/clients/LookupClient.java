package com.etiya.crm.contactinfoservice.clients;

import com.etiya.crm.shared.contracts.gnltp.GnlTpResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** lookup-service GNL_TP REST kontrati - bkz. back-end/lookup-service/LOOKUP_SERVICE_INTEGRATION.md. */
@FeignClient(name = "lookup-service")
public interface LookupClient {

	@GetMapping("/api/v1/general-types/{id}")
	GnlTpResponse getById(@PathVariable("id") Long id);
}
