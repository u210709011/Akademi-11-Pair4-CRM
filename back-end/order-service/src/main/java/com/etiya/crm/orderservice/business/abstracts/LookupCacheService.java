package com.etiya.crm.orderservice.business.abstracts;

import com.etiya.crm.shared.contracts.gnlchar.GnlCharResponse;
import com.etiya.crm.shared.contracts.gnlcharval.GnlCharValResponse;
import com.etiya.crm.shared.contracts.gnltp.GnlTpResponse;

public interface LookupCacheService {

	/** general-statuses/resolve/{entCodeName}/{shrtCode} - GNL_ST id'sini doner. */
	Long resolveStatusId(String entCodeName, String shrtCode);

	/** type-values icinde tableName'e karsilik gelen fieldName'i (polimorfik tip etiketi) doner. */
	Long resolveDataTypeId(String tableName);

	/** charId'nin gercekten var oldugunu dogrular (bulunamazsa lookup-service 404 firlatir). */
	GnlCharResponse getCharacteristic(Long charId);

	/** charValId'nin gercekten var oldugunu dogrular (bulunamazsa lookup-service 404 firlatir). */
	GnlCharValResponse getCharacteristicValue(Long charValId);

	/** servis adresindeki cityId'yi (GNL_TP) isme cevirmek icin. */
	GnlTpResponse getGeneralType(Long gnlTpId);
}
