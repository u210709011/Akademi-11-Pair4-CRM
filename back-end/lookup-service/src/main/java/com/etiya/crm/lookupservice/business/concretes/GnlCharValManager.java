package com.etiya.crm.lookupservice.business.concretes;

import com.etiya.crm.lookupservice.business.abstracts.GnlCharValService;
import com.etiya.crm.lookupservice.business.abstracts.TranslationService;
import com.etiya.crm.shared.contracts.gnlcharval.CreateGnlCharValRequest;
import com.etiya.crm.shared.contracts.gnlcharval.UpdateGnlCharValRequest;
import com.etiya.crm.shared.contracts.gnlcharval.GnlCharValResponse;
import com.etiya.crm.lookupservice.business.exceptions.EntityNotFoundException;
import com.etiya.crm.lookupservice.business.exceptions.InvalidDateRangeException;
import com.etiya.crm.lookupservice.constants.EntityNames;
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

    private static final String ENTITY_NAME = "GNL_CHAR_VAL";

    private final GnlCharValRepository gnlCharValRepository;
    private final GnlCharRepository gnlCharRepository;
    private final GnlCharValMapper gnlCharValMapper;
    private final TranslationService translationService;

    @Override
    public List<GnlCharValResponse> getAll() {
        return gnlCharValRepository.findAll().stream().map(this::toTranslatedResponse).toList();
    }

    @Override
    public GnlCharValResponse getById(Long id) {
        return toTranslatedResponse(getEntity(id));
    }

    /** val taban degeri Ingilizce'dir - bkz. GnlTpManager.toTranslatedResponse (ayni desen). */
    private GnlCharValResponse toTranslatedResponse(GnlCharVal gnlCharVal) {
        GnlCharValResponse response = gnlCharValMapper.toResponse(gnlCharVal);
        String translatedVal = translationService.translate(ENTITY_NAME, gnlCharVal.getCharValId(), "VAL", response.val());
        if (translatedVal.equals(response.val())) {
            return response;
        }
        return new GnlCharValResponse(response.charValId(), response.charId(), response.dflt(), translatedVal,
                response.shrtCode(), response.sdate(), response.edate(), response.active(), response.cdate(),
                response.cuser(), response.udate(), response.uuser());
    }

    @Override
    @Transactional
    public GnlCharValResponse add(CreateGnlCharValRequest request) {
        if (!gnlCharRepository.existsById(request.charId())) {
            throw new EntityNotFoundException(EntityNames.GNL_CHAR, request.charId());
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
        return gnlCharValRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(EntityNames.GNL_CHAR_VAL, id));
    }

    private void checkDateRange(LocalDate sdate, LocalDate edate) {
        if (edate != null && edate.isBefore(sdate)) {
            throw new InvalidDateRangeException(sdate, edate);
        }
    }
}
