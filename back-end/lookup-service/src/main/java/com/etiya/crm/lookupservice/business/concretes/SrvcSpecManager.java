package com.etiya.crm.lookupservice.business.concretes;

import com.etiya.crm.lookupservice.business.abstracts.SrvcSpecService;
import com.etiya.crm.lookupservice.business.dtos.requests.CreateSrvcSpecRequest;
import com.etiya.crm.lookupservice.business.dtos.requests.UpdateSrvcSpecRequest;
import com.etiya.crm.lookupservice.business.dtos.responses.SrvcSpecResponse;
import com.etiya.crm.lookupservice.business.exceptions.EntityNotFoundException;
import com.etiya.crm.lookupservice.dataAccess.abstracts.GnlStRepository;
import com.etiya.crm.lookupservice.dataAccess.abstracts.SrvcSpecRepository;
import com.etiya.crm.lookupservice.entities.concretes.SrvcSpec;
import com.etiya.crm.lookupservice.mapper.SrvcSpecMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SrvcSpecManager implements SrvcSpecService {

    private final SrvcSpecRepository srvcSpecRepository;
    private final GnlStRepository gnlStRepository;
    private final SrvcSpecMapper srvcSpecMapper;

    @Override
    public List<SrvcSpecResponse> getAll() {
        return srvcSpecRepository.findAll().stream().map(srvcSpecMapper::toResponse).toList();
    }

    @Override
    public SrvcSpecResponse getById(Long id) {
        return srvcSpecMapper.toResponse(getEntity(id));
    }

    @Override
    @Transactional
    public SrvcSpecResponse add(CreateSrvcSpecRequest request) {
        checkStatusExists(request.stId());
        SrvcSpec srvcSpec = srvcSpecMapper.toEntity(request);
        return srvcSpecMapper.toResponse(srvcSpecRepository.save(srvcSpec));
    }

    @Override
    @Transactional
    public SrvcSpecResponse update(Long id, UpdateSrvcSpecRequest request) {
        checkStatusExists(request.stId());
        SrvcSpec srvcSpec = getEntity(id);
        srvcSpec.setName(request.name());
        srvcSpec.setDescr(request.descr());
        srvcSpec.setSrvcCode(request.srvcCode());
        srvcSpec.setStId(request.stId());
        return srvcSpecMapper.toResponse(srvcSpecRepository.save(srvcSpec));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        srvcSpecRepository.delete(getEntity(id));
    }

    private SrvcSpec getEntity(Long id) {
        return srvcSpecRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("SrvcSpec", id));
    }

    private void checkStatusExists(Long stId) {
        if (!gnlStRepository.existsById(stId)) {
            throw new EntityNotFoundException("GnlSt", stId);
        }
    }
}
