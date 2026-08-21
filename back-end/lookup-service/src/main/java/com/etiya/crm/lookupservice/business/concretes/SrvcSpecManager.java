package com.etiya.crm.lookupservice.business.concretes;

import com.etiya.crm.lookupservice.business.abstracts.SrvcSpecService;
import com.etiya.crm.lookupservice.business.abstracts.TranslationService;
import com.etiya.crm.shared.contracts.srvcspec.CreateSrvcSpecRequest;
import com.etiya.crm.shared.contracts.srvcspec.UpdateSrvcSpecRequest;
import com.etiya.crm.shared.contracts.srvcspec.SrvcSpecResponse;
import com.etiya.crm.lookupservice.business.exceptions.EntityNotFoundException;
import com.etiya.crm.lookupservice.business.rules.GnlStExistenceRule;
import com.etiya.crm.lookupservice.constants.EntityNames;
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

    private static final String ENTITY_NAME = "SRVC_SPEC";

    private final SrvcSpecRepository srvcSpecRepository;
    private final GnlStExistenceRule gnlStExistenceRule;
    private final SrvcSpecMapper srvcSpecMapper;
    private final TranslationService translationService;

    @Override
    public List<SrvcSpecResponse> getAll() {
        return srvcSpecRepository.findAll().stream().map(this::toTranslatedResponse).toList();
    }

    @Override
    public SrvcSpecResponse getById(Long id) {
        return toTranslatedResponse(getEntity(id));
    }

    /**
     * name/descr taban degerleri Ingilizce'dir (varsayilan dil) - istekteki dil Ingilizce disi ise
     * translation tablosundan overlay uygulanir, yoksa taban deger degismeden doner (bkz. GnlTpManager
     * ile ayni desen).
     */
    private SrvcSpecResponse toTranslatedResponse(SrvcSpec srvcSpec) {
        SrvcSpecResponse response = srvcSpecMapper.toResponse(srvcSpec);
        String translatedName = translationService.translate(ENTITY_NAME, srvcSpec.getSrvcSpecId(), "NAME", response.name());
        String translatedDescr = translationService.translate(ENTITY_NAME, srvcSpec.getSrvcSpecId(), "DESCR", response.descr());
        if (translatedName.equals(response.name()) && translatedDescr.equals(response.descr())) {
            return response;
        }
        return new SrvcSpecResponse(response.srvcSpecId(), translatedName, translatedDescr, response.srvcCode(),
                response.stId(), response.cdate(), response.cuser(), response.udate(), response.uuser());
    }

    @Override
    @Transactional
    public SrvcSpecResponse add(CreateSrvcSpecRequest request) {
        gnlStExistenceRule.ensureExists(request.stId());
        SrvcSpec srvcSpec = srvcSpecMapper.toEntity(request);
        return srvcSpecMapper.toResponse(srvcSpecRepository.save(srvcSpec));
    }

    @Override
    @Transactional
    public SrvcSpecResponse update(Long id, UpdateSrvcSpecRequest request) {
        gnlStExistenceRule.ensureExists(request.stId());
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
        return srvcSpecRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(EntityNames.SRVC_SPEC, id));
    }
}
