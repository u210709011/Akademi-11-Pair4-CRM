package com.etiya.crm.lookupservice.business.concretes;

import com.etiya.crm.lookupservice.business.abstracts.TypeValueService;
import com.etiya.crm.shared.contracts.typevalue.CreateTypeValueRequest;
import com.etiya.crm.shared.contracts.typevalue.UpdateTypeValueRequest;
import com.etiya.crm.shared.contracts.typevalue.TypeValueResponse;
import com.etiya.crm.lookupservice.business.exceptions.EntityNotFoundException;
import com.etiya.crm.lookupservice.dataAccess.abstracts.TypeValueRepository;
import com.etiya.crm.lookupservice.entities.concretes.TypeValue;
import com.etiya.crm.lookupservice.mapper.TypeValueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * TYPE_VALUE, GNL_TP/GNL_ST'den bagimsizdir - herhangi bir is tablosu (PROD, PARTY, CUST, CUST_ACCT...)
 * icin polimorfik sahiplik etiketi (row_id + tip numarasi) uretmekte kullanilan genel bir
 * tablo->numeric-tip kayit defteridir (eski DATA_TYPE grubunun genellestirilmis hali).
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TypeValueManager implements TypeValueService {

    private final TypeValueRepository typeValueRepository;
    private final TypeValueMapper typeValueMapper;

    @Override
    public List<TypeValueResponse> getAll() {
        return typeValueRepository.findAll().stream().map(typeValueMapper::toResponse).toList();
    }

    @Override
    public TypeValueResponse getById(Long id) {
        return typeValueMapper.toResponse(getEntity(id));
    }

    @Override
    @Transactional
    public TypeValueResponse add(CreateTypeValueRequest request) {
        TypeValue typeValue = typeValueMapper.toEntity(request);
        return typeValueMapper.toResponse(typeValueRepository.save(typeValue));
    }

    @Override
    @Transactional
    public TypeValueResponse update(Long id, UpdateTypeValueRequest request) {
        TypeValue typeValue = getEntity(id);
        typeValue.setDescription(request.description());
        typeValue.setValue(request.value());
        typeValue.setUsingModuleName(request.usingModuleName());
        return typeValueMapper.toResponse(typeValueRepository.save(typeValue));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        getEntity(id);
        typeValueRepository.deleteById(id);
    }

    private TypeValue getEntity(Long id) {
        return typeValueRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("TypeValue", id));
    }
}
