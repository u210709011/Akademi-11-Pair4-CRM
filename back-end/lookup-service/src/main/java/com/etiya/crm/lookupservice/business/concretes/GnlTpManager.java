package com.etiya.crm.lookupservice.business.concretes;

import com.etiya.crm.lookupservice.business.abstracts.GnlTpService;
import com.etiya.crm.lookupservice.business.abstracts.TranslationService;
import com.etiya.crm.shared.contracts.gnltp.CreateGnlTpRequest;
import com.etiya.crm.shared.contracts.gnltp.UpdateGnlTpRequest;
import com.etiya.crm.shared.contracts.gnltp.GnlTpResponse;
import com.etiya.crm.lookupservice.business.exceptions.EntityNotFoundException;
import com.etiya.crm.lookupservice.constants.EntityNames;
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

    private static final String ENTITY_NAME = "GNL_TP";

    private final GnlTpRepository gnlTpRepository;
    private final GnlTpMapper gnlTpMapper;
    private final TranslationService translationService;

    @Override
    public List<GnlTpResponse> getAll() {
        return gnlTpRepository.findAll().stream().map(this::toTranslatedResponse).toList();
    }

    @Override
    public List<GnlTpResponse> getAllByEntCodeName(String entCodeName) {
        return gnlTpRepository.findAllByEntCodeName(entCodeName).stream().map(this::toTranslatedResponse).toList();
    }

    @Override
    public GnlTpResponse getById(Long id) {
        return toTranslatedResponse(getEntity(id));
    }

    @Override
    public GnlTpResponse getByEntCodeNameAndShrtCode(String entCodeName, String shrtCode) {
        GnlTp gnlTp = gnlTpRepository.findByEntCodeNameAndShrtCode(entCodeName, shrtCode)
                .orElseThrow(() -> new EntityNotFoundException(EntityNames.GNL_TP, entCodeName + "/" + shrtCode));
        return toTranslatedResponse(gnlTp);
    }

    /**
     * name/descr taban degerleri Ingilizce'dir (varsayilan dil) - istekteki dil Ingilizce disi ise
     * translation tablosundan overlay uygulanir, yoksa taban deger degismeden doner.
     */
    private GnlTpResponse toTranslatedResponse(GnlTp gnlTp) {
        GnlTpResponse response = gnlTpMapper.toResponse(gnlTp);
        String translatedName = translationService.translate(ENTITY_NAME, gnlTp.getGnlTpId(), "NAME", response.name());
        String translatedDescr = translationService.translate(ENTITY_NAME, gnlTp.getGnlTpId(), "DESCR", response.descr());
        if (translatedName.equals(response.name()) && translatedDescr.equals(response.descr())) {
            return response;
        }
        return new GnlTpResponse(response.gnlTpId(), translatedName, translatedDescr, response.shrtCode(),
                response.entCodeName(), response.entName(), response.active(), response.cdate(), response.cuser(),
                response.udate(), response.uuser());
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
        return gnlTpRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(EntityNames.GNL_TP, id));
    }
}
