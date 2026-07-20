package com.etiya.crm.lookupservice.business.concretes;

import com.etiya.crm.lookupservice.business.abstracts.TypeValueService;
import com.etiya.crm.lookupservice.business.dtos.requests.CreateTypeValueRequest;
import com.etiya.crm.lookupservice.business.dtos.requests.UpdateTypeValueRequest;
import com.etiya.crm.lookupservice.business.dtos.responses.TypeValueResponse;
import com.etiya.crm.lookupservice.business.exceptions.EntityNotFoundException;
import com.etiya.crm.lookupservice.dataAccess.abstracts.GnlStRepository;
import com.etiya.crm.lookupservice.dataAccess.abstracts.GnlTpRepository;
import com.etiya.crm.lookupservice.dataAccess.abstracts.TypeValueRepository;
import com.etiya.crm.lookupservice.entities.concretes.TypeValue;
import com.etiya.crm.lookupservice.mapper.TypeValueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TypeValueManager implements TypeValueService {

    private final TypeValueRepository typeValueRepository;
    private final GnlTpRepository gnlTpRepository;
    private final GnlStRepository gnlStRepository;
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
        checkParentExists(request.tableName(), request.fieldName());
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

    private void checkParentExists(String tableName, Long fieldName) {
        boolean exists = "GNL_TP".equals(tableName)
                ? gnlTpRepository.existsById(fieldName)
                : gnlStRepository.existsById(fieldName);
        if (!exists) {
            throw new EntityNotFoundException(tableName, fieldName);
        }
    }
}
