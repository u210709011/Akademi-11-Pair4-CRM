package com.etiya.crm.lookupservice.business.rules;

import org.springframework.stereotype.Component;

import com.etiya.crm.lookupservice.business.exceptions.EntityNotFoundException;
import com.etiya.crm.lookupservice.constants.EntityNames;
import com.etiya.crm.lookupservice.dataAccess.abstracts.GnlStRepository;

import lombok.RequiredArgsConstructor;

/** RsrcSpecManager/SrvcSpecManager'da birebir kopyalanmis stId dogrulamasi buraya tasindi. */
@Component
@RequiredArgsConstructor
public class GnlStExistenceRule {

	private final GnlStRepository gnlStRepository;

	public void ensureExists(Long stId) {
		if (!gnlStRepository.existsById(stId)) {
			throw new EntityNotFoundException(EntityNames.GNL_ST, stId);
		}
	}
}
