package com.etiya.crm.lookupservice.business.concretes;

import com.etiya.crm.lookupservice.business.abstracts.LookupService;
import com.etiya.crm.lookupservice.business.dtos.responses.LookupValueResponse;
import com.etiya.crm.lookupservice.business.exceptions.LookupNotFoundException;
import com.etiya.crm.lookupservice.dataAccess.abstracts.LookupRepository;
import com.etiya.crm.lookupservice.entities.concretes.Lookup;
import com.etiya.crm.lookupservice.mapper.LookupMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LookupManager implements LookupService {

    private final LookupRepository lookupRepository;
    private final LookupMapper lookupMapper;

    @Override
    public LookupValueResponse getByValueId(String groupCode, Long valueId) {
        Lookup lookup = lookupRepository.findByGroupCodeAndValueId(groupCode, valueId)
                .orElseThrow(() -> new LookupNotFoundException(groupCode, valueId));
        return lookupMapper.toResponse(lookup);
    }

    @Override
    public LookupValueResponse getByCode(String groupCode, String code) {
        Lookup lookup = lookupRepository.findByGroupCodeAndCode(groupCode, code)
                .orElseThrow(() -> new LookupNotFoundException(groupCode, code));
        return lookupMapper.toResponse(lookup);
    }
}
