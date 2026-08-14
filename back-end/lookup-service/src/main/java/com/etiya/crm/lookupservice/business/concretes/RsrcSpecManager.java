package com.etiya.crm.lookupservice.business.concretes;

import com.etiya.crm.lookupservice.business.abstracts.RsrcSpecService;
import com.etiya.crm.lookupservice.business.abstracts.TranslationService;
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

    private static final String ENTITY_NAME = "RSRC_SPEC";

    private final RsrcSpecRepository rsrcSpecRepository;
    private final GnlStExistenceRule gnlStExistenceRule;
    private final RsrcSpecMapper rsrcSpecMapper;
    private final TranslationService translationService;

    @Override
    public List<RsrcSpecResponse> getAll() {
        return rsrcSpecRepository.findAll().stream().map(this::toTranslatedResponse).toList();
    }

    @Override
    public RsrcSpecResponse getById(Long id) {
        return toTranslatedResponse(getEntity(id));
    }

    /**
     * name/descr taban degerleri Ingilizce'dir (varsayilan dil) - istekteki dil Ingilizce disi ise
     * translation tablosundan overlay uygulanir, yoksa taban deger degismeden doner (bkz. GnlTpManager
     * ile ayni desen).
     */
    private RsrcSpecResponse toTranslatedResponse(RsrcSpec rsrcSpec) {
        RsrcSpecResponse response = rsrcSpecMapper.toResponse(rsrcSpec);
        String translatedName = translationService.translate(ENTITY_NAME, rsrcSpec.getRsrcSpecId(), "NAME", response.name());
        String translatedDescr = translationService.translate(ENTITY_NAME, rsrcSpec.getRsrcSpecId(), "DESCR", response.descr());
        if (translatedName.equals(response.name()) && translatedDescr.equals(response.descr())) {
            return response;
        }
        return new RsrcSpecResponse(response.rsrcSpecId(), translatedName, translatedDescr, response.stId(),
                response.rsrcCode(), response.cdate(), response.cuser(), response.udate(), response.uuser());
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
