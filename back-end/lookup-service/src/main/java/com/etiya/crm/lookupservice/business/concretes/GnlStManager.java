package com.etiya.crm.lookupservice.business.concretes;

import com.etiya.crm.lookupservice.business.abstracts.GnlStService;
import com.etiya.crm.lookupservice.business.abstracts.TranslationService;
import com.etiya.crm.shared.contracts.gnlst.CreateGnlStRequest;
import com.etiya.crm.shared.contracts.gnlst.UpdateGnlStRequest;
import com.etiya.crm.shared.contracts.gnlst.GnlStResponse;
import com.etiya.crm.lookupservice.business.exceptions.EntityNotFoundException;
import com.etiya.crm.lookupservice.constants.EntityNames;
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

    private static final String ENTITY_NAME = "GNL_ST";

    private final GnlStRepository gnlStRepository;
    private final GnlStMapper gnlStMapper;
    private final TranslationService translationService;

    @Override
    public List<GnlStResponse> getAll() {
        return gnlStRepository.findAll().stream().map(this::toTranslatedResponse).toList();
    }

    @Override
    public List<GnlStResponse> getAllByEntCodeName(String entCodeName) {
        return gnlStRepository.findAllByEntCodeName(entCodeName).stream().map(this::toTranslatedResponse).toList();
    }

    @Override
    public GnlStResponse getById(Long id) {
        return toTranslatedResponse(getEntity(id));
    }

    @Override
    public GnlStResponse getByEntCodeNameAndShrtCode(String entCodeName, String shrtCode) {
        GnlSt gnlSt = gnlStRepository.findByEntCodeNameAndShrtCode(entCodeName, shrtCode)
                .orElseThrow(() -> new EntityNotFoundException(EntityNames.GNL_ST, entCodeName + "/" + shrtCode));
        return toTranslatedResponse(gnlSt);
    }

    /**
     * name/descr taban degerleri Ingilizce'dir (varsayilan dil) - istekteki dil Ingilizce disi ise
     * translation tablosundan overlay uygulanir, yoksa taban deger degismeden doner (bkz. GnlTpManager
     * ile ayni desen).
     */
    private GnlStResponse toTranslatedResponse(GnlSt gnlSt) {
        GnlStResponse response = gnlStMapper.toResponse(gnlSt);
        String translatedName = translationService.translate(ENTITY_NAME, gnlSt.getGnlStId(), "NAME", response.name());
        String translatedDescr = translationService.translate(ENTITY_NAME, gnlSt.getGnlStId(), "DESCR", response.descr());
        if (translatedName.equals(response.name()) && translatedDescr.equals(response.descr())) {
            return response;
        }
        return new GnlStResponse(response.gnlStId(), translatedName, translatedDescr, response.shrtCode(),
                response.active(), response.entCodeName(), response.entName(), response.cdate(), response.cuser(),
                response.udate(), response.uuser());
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
        return gnlStRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(EntityNames.GNL_ST, id));
    }
}
