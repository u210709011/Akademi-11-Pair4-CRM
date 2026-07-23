package com.etiya.crm.lookupservice.business.concretes;

import com.etiya.crm.lookupservice.business.abstracts.GnlStService;
import com.etiya.crm.shared.contracts.gnlst.CreateGnlStRequest;
import com.etiya.crm.shared.contracts.gnlst.UpdateGnlStRequest;
import com.etiya.crm.shared.contracts.gnlst.GnlStResponse;
import com.etiya.crm.lookupservice.business.exceptions.EntityNotFoundException;
import com.etiya.crm.lookupservice.dataAccess.abstracts.GnlStRepository;
import com.etiya.crm.lookupservice.entities.concretes.GnlSt;
import com.etiya.crm.lookupservice.mapper.GnlStMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GnlStManager implements GnlStService {

    private final GnlStRepository gnlStRepository;
    private final GnlStMapper gnlStMapper;

    @Override
    public List<GnlStResponse> getAll() {
        return gnlStRepository.findAll().stream().map(gnlStMapper::toResponse).toList();
    }

    @Override
    public List<GnlStResponse> getAllByEntCodeName(String entCodeName) {
        return gnlStRepository.findAllByEntCodeName(entCodeName).stream().map(gnlStMapper::toResponse).toList();
    }

    @Override
    public GnlStResponse getById(Long id) {
        return gnlStMapper.toResponse(getEntity(id));
    }

    @Override
    public GnlStResponse getByEntCodeNameAndShrtCode(String entCodeName, String shrtCode) {
        GnlSt gnlSt = gnlStRepository.findByEntCodeNameAndShrtCode(entCodeName, shrtCode)
                .orElseThrow(() -> new EntityNotFoundException("GnlSt", entCodeName + "/" + shrtCode));
        return gnlStMapper.toResponse(gnlSt);
    }

    @Override
    @Transactional
    public GnlStResponse add(CreateGnlStRequest request) {
        GnlSt gnlSt = gnlStMapper.toEntity(request);
        return gnlStMapper.toResponse(gnlStRepository.save(gnlSt));
    }

    @Override
    @Transactional
    public GnlStResponse update(Long id, UpdateGnlStRequest request) {
        GnlSt gnlSt = getEntity(id);
        gnlSt.setName(request.name());
        gnlSt.setDescr(request.descr());
        gnlSt.setShrtCode(request.shrtCode());
        gnlSt.setActive(request.active());
        gnlSt.setEntCodeName(request.entCodeName());
        gnlSt.setEntName(request.entName());
        return gnlStMapper.toResponse(gnlStRepository.save(gnlSt));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        GnlSt gnlSt = getEntity(id);
        gnlSt.setActive(false);
        gnlStRepository.save(gnlSt);
    }

    private GnlSt getEntity(Long id) {
        return gnlStRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("GnlSt", id));
    }
}
