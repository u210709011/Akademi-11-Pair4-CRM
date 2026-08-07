package com.etiya.crm.customerservice.business.exceptions;

import com.etiya.crm.customerservice.constants.MessageKeys;

/** FR-002 ACC-003: /customers/search hicbir filtre parametresi olmadan cagrilmis. */
public class SearchFilterRequiredException extends BusinessException {

	public SearchFilterRequiredException() {
		super(MessageKeys.SEARCH_FILTER_REQUIRED);
	}
}
