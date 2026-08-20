package com.etiya.crm.lookupservice.business.concretes;

import com.etiya.crm.lookupservice.business.abstracts.GnlCharService;
import com.etiya.crm.lookupservice.business.abstracts.TranslationService;
import com.etiya.crm.shared.contracts.gnlchar.CreateGnlCharRequest;
import com.etiya.crm.shared.contracts.gnlchar.UpdateGnlCharRequest;
import com.etiya.crm.shared.contracts.gnlchar.GnlCharResponse;
import com.etiya.crm.lookupservice.business.exceptions.EntityNotFoundException;
import com.etiya.crm.lookupservice.constants.EntityNames;
import com.etiya.crm.lookupservice.dataAccess.abstracts.GnlCharRepository;
import com.etiya.crm.lookupservice.entities.concretes.GnlChar;
import com.etiya.crm.lookupservice.mapper.GnlCharMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GnlCharManager implements GnlCharService {

    private static final String ENTITY_NAME = "GNL_CHAR";

    private final GnlCharRepository gnlCharRepository;
    private final GnlCharMapper gnlCharMapper;
    private final TranslationService translationService;

    @Override
    public List<GnlCharResponse> getAll() {
        return gnlCharRepository.findAll().stream().map(this::toTranslatedResponse).toList();
    }

    @Override
    public GnlCharResponse getById(Long id) {
        return toTranslatedResponse(getEntity(id));
    }

    /** name/descr taban degerleri Ingilizce'dir - bkz. GnlTpManager.toTranslatedResponse (ayni desen). */
    private GnlCharResponse toTranslatedResponse(GnlChar gnlChar) {
        GnlCharResponse response = gnlCharMapper.toResponse(gnlChar);
        String translatedName = translationService.translate(ENTITY_NAME, gnlChar.getCharId(), "NAME", response.name());
        String translatedDescr = translationService.translate(ENTITY_NAME, gnlChar.getCharId(), "DESCR", response.descr());
        if (translatedName.equals(response.name()) && translatedDescr.equals(response.descr())) {
            return response;
        }
        return new GnlCharResponse(response.charId(), translatedName, translatedDescr, response.prvdrCls(),
                response.shrtCode(), response.active(), response.cdate(), response.cuser(), response.udate(),
                response.uuser());
    }

    @Override
    @Transactional
    public GnlCharResponse add(CreateGnlCharRequest request) {
        GnlChar gnlChar = gnlCharMapper.toEntity(request);
        return gnlCharMapper.toResponse(gnlCharRepository.save(gnlChar));
    }

    @Override
    @Transactional
    public GnlCharResponse update(Long id, UpdateGnlCharRequest request) {
        GnlChar gnlChar = getEntity(id);
        gnlChar.setName(request.name());
        gnlChar.setDescr(request.descr());
        gnlChar.setPrvdrCls(request.prvdrCls());
        gnlChar.setActive(request.active());
        return gnlCharMapper.toResponse(gnlCharRepository.save(gnlChar));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        GnlChar gnlChar = getEntity(id);
        gnlChar.setActive(false);
        gnlCharRepository.save(gnlChar);
    }

    private GnlChar getEntity(Long id) {
        return gnlCharRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(EntityNames.GNL_CHAR, id));
    }
}
