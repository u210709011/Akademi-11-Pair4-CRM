package com.etiya.crm.lookupservice.business.concretes;

import com.etiya.crm.lookupservice.business.abstracts.GnlCharValService;
import com.etiya.crm.lookupservice.business.dtos.requests.CreateGnlCharValRequest;
import com.etiya.crm.lookupservice.business.dtos.requests.UpdateGnlCharValRequest;
import com.etiya.crm.lookupservice.business.dtos.responses.GnlCharValResponse;
import com.etiya.crm.lookupservice.business.exceptions.BusinessException;
import com.etiya.crm.lookupservice.business.exceptions.EntityNotFoundException;
import com.etiya.crm.lookupservice.dataAccess.abstracts.GnlCharRepository;
import com.etiya.crm.lookupservice.dataAccess.abstracts.GnlCharValRepository;
import com.etiya.crm.lookupservice.entities.concretes.GnlCharVal;
import com.etiya.crm.lookupservice.mapper.GnlCharValMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GnlCharValManager implements GnlCharValService {

    private final GnlCharValRepository gnlCharValRepository;
    private final GnlCharRepository gnlCharRepository;
    private final GnlCharValMapper gnlCharValMapper;

    @Override
    public List<GnlCharValResponse> getAll() {
        return gnlCharValRepository.findAll().stream().map(gnlCharValMapper::toResponse).toList();
    }

    @Override
    public GnlCharValResponse getById(Long id) {
        return gnlCharValMapper.toResponse(getEntity(id));
    }

    @Override
    @Transactional
    public GnlCharValResponse add(CreateGnlCharValRequest request) {
        if (!gnlCharRepository.existsById(request.charId())) {
            throw new EntityNotFoundException("GnlChar", request.charId());
        }
        checkDateRange(request.sdate(), request.edate());
        GnlCharVal gnlCharVal = gnlCharValMapper.toEntity(request);
        return gnlCharValMapper.toResponse(gnlCharValRepository.save(gnlCharVal));
    }

    @Override
    @Transactional
    public GnlCharValResponse update(Long id, UpdateGnlCharValRequest request) {
        checkDateRange(request.sdate(), request.edate());
        GnlCharVal gnlCharVal = getEntity(id);
        gnlCharVal.setDflt(request.dflt());
        gnlCharVal.setVal(request.val());
        gnlCharVal.setShrtCode(request.shrtCode());
        gnlCharVal.setSdate(request.sdate());
        gnlCharVal.setEdate(request.edate());
        gnlCharVal.setActive(request.active());
        return gnlCharValMapper.toResponse(gnlCharValRepository.save(gnlCharVal));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        GnlCharVal gnlCharVal = getEntity(id);
        gnlCharVal.setActive(false);
        gnlCharValRepository.save(gnlCharVal);
    }

    private GnlCharVal getEntity(Long id) {
        return gnlCharValRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("GnlCharVal", id));
    }

    private void checkDateRange(LocalDate sdate, LocalDate edate) {
        if (edate != null && edate.isBefore(sdate)) {
            throw new BusinessException("edate, sdate'den once olamaz: sdate=" + sdate + ", edate=" + edate);
        }
    }
}
