package com.etiya.crm.lookupservice.business.concretes;

import com.etiya.crm.lookupservice.business.abstracts.RsrcSpecService;
import com.etiya.crm.shared.contracts.rsrcspec.CreateRsrcSpecRequest;
import com.etiya.crm.shared.contracts.rsrcspec.UpdateRsrcSpecRequest;
import com.etiya.crm.shared.contracts.rsrcspec.RsrcSpecResponse;
import com.etiya.crm.lookupservice.business.exceptions.EntityNotFoundException;
import com.etiya.crm.lookupservice.business.rules.GnlStExistenceRule;
import com.etiya.crm.lookupservice.constants.EntityNames;
import com.etiya.crm.lookupservice.dataAccess.abstracts.RsrcSpecRepository;
import com.etiya.crm.lookupservice.entities.concretes.RsrcSpec;
import com.etiya.crm.lookupservice.mapper.RsrcSpecMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RsrcSpecManager implements RsrcSpecService {

    private final RsrcSpecRepository rsrcSpecRepository;
    private final GnlStExistenceRule gnlStExistenceRule;
    private final RsrcSpecMapper rsrcSpecMapper;

    @Override
    public List<RsrcSpecResponse> getAll() {
        return rsrcSpecRepository.findAll().stream().map(rsrcSpecMapper::toResponse).toList();
    }

    @Override
    public RsrcSpecResponse getById(Long id) {
        return rsrcSpecMapper.toResponse(getEntity(id));
    }

    @Override
    @Transactional
    public RsrcSpecResponse add(CreateRsrcSpecRequest request) {
        gnlStExistenceRule.ensureExists(request.stId());
        RsrcSpec rsrcSpec = rsrcSpecMapper.toEntity(request);
        return rsrcSpecMapper.toResponse(rsrcSpecRepository.save(rsrcSpec));
    }

    @Override
    @Transactional
    public RsrcSpecResponse update(Long id, UpdateRsrcSpecRequest request) {
        gnlStExistenceRule.ensureExists(request.stId());
        RsrcSpec rsrcSpec = getEntity(id);
        rsrcSpec.setName(request.name());
        rsrcSpec.setDescr(request.descr());
        rsrcSpec.setStId(request.stId());
        rsrcSpec.setRsrcCode(request.rsrcCode());
        return rsrcSpecMapper.toResponse(rsrcSpecRepository.save(rsrcSpec));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        rsrcSpecRepository.delete(getEntity(id));
    }

    private RsrcSpec getEntity(Long id) {
        return rsrcSpecRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(EntityNames.RSRC_SPEC, id));
    }
}
