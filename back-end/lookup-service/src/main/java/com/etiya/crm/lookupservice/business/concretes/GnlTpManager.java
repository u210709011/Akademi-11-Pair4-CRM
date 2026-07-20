package com.etiya.crm.lookupservice.business.concretes;

import com.etiya.crm.lookupservice.business.abstracts.GnlTpService;
import com.etiya.crm.lookupservice.business.dtos.requests.CreateGnlTpRequest;
import com.etiya.crm.lookupservice.business.dtos.requests.UpdateGnlTpRequest;
import com.etiya.crm.lookupservice.business.dtos.responses.GnlTpResponse;
import com.etiya.crm.lookupservice.business.exceptions.EntityNotFoundException;
import com.etiya.crm.lookupservice.dataAccess.abstracts.GnlTpRepository;
import com.etiya.crm.lookupservice.entities.concretes.GnlTp;
import com.etiya.crm.lookupservice.mapper.GnlTpMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GnlTpManager implements GnlTpService {

    private final GnlTpRepository gnlTpRepository;
    private final GnlTpMapper gnlTpMapper;

    @Override
    public List<GnlTpResponse> getAll() {
        return gnlTpRepository.findAll().stream().map(gnlTpMapper::toResponse).toList();
    }

    @Override
    public GnlTpResponse getById(Long id) {
        return gnlTpMapper.toResponse(getEntity(id));
    }

    @Override
    @Transactional
    public GnlTpResponse add(CreateGnlTpRequest request) {
        GnlTp gnlTp = gnlTpMapper.toEntity(request);
        return gnlTpMapper.toResponse(gnlTpRepository.save(gnlTp));
    }

    @Override
    @Transactional
    public GnlTpResponse update(Long id, UpdateGnlTpRequest request) {
        GnlTp gnlTp = getEntity(id);
        gnlTp.setName(request.name());
        gnlTp.setDescr(request.descr());
        gnlTp.setShrtCode(request.shrtCode());
        gnlTp.setEntCodeName(request.entCodeName());
        gnlTp.setEntName(request.entName());
        gnlTp.setActive(request.active());
        return gnlTpMapper.toResponse(gnlTpRepository.save(gnlTp));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        GnlTp gnlTp = getEntity(id);
        gnlTp.setActive(false);
        gnlTpRepository.save(gnlTp);
    }

    private GnlTp getEntity(Long id) {
        return gnlTpRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("GnlTp", id));
    }
}
