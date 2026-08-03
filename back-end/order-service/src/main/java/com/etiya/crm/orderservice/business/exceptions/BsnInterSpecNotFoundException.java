package com.etiya.crm.orderservice.business.exceptions;

import com.etiya.crm.orderservice.constants.MessageKeys;

/** BSN_INTER_SPEC seed'inde beklenen kisa kod bulunamadi (bkz. V3__seed_bsn_inter_spec.sql). */
public class BsnInterSpecNotFoundException extends BusinessException {

	public BsnInterSpecNotFoundException(String shrtCode) {
		super(MessageKeys.BSN_INTER_SPEC_NOT_FOUND, shrtCode);
	}
}
