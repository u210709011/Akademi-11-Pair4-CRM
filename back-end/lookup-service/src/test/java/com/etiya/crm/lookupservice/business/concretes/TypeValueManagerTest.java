package com.etiya.crm.lookupservice.business.concretes;

import com.etiya.crm.lookupservice.business.dtos.requests.CreateTypeValueRequest;
import com.etiya.crm.lookupservice.business.dtos.requests.UpdateTypeValueRequest;
import com.etiya.crm.lookupservice.business.dtos.responses.TypeValueResponse;
import com.etiya.crm.lookupservice.business.exceptions.EntityNotFoundException;
import com.etiya.crm.lookupservice.dataAccess.abstracts.GnlStRepository;
import com.etiya.crm.lookupservice.dataAccess.abstracts.GnlTpRepository;
import com.etiya.crm.lookupservice.dataAccess.abstracts.TypeValueRepository;
import com.etiya.crm.lookupservice.entities.concretes.TypeValue;
import com.etiya.crm.lookupservice.mapper.TypeValueMapper;
import com.etiya.crm.lookupservice.mapper.TypeValueMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TypeValueManagerTest {

    @Mock
    private TypeValueRepository typeValueRepository;

    @Mock
    private GnlTpRepository gnlTpRepository;

    @Mock
    private GnlStRepository gnlStRepository;

    private final TypeValueMapper typeValueMapper = new TypeValueMapperImpl();

    private TypeValueManager manager() {
        return new TypeValueManager(typeValueRepository, gnlTpRepository, gnlStRepository, typeValueMapper);
    }

    private TypeValue typeValue(Long id) {
        TypeValue typeValue = new TypeValue();
        typeValue.setTypeValueId(id);
        typeValue.setTableName("GNL_TP");
        typeValue.setFieldName(1L);
        typeValue.setValue("CODE");
        typeValue.setDescription("Description");
        return typeValue;
    }

    @Test
    void add_throwsEntityNotFoundException_whenParentGnlTpMissing() {
        CreateTypeValueRequest request = new CreateTypeValueRequest("GNL_TP", 1L, "Yeni deger", "NEW", null);
        when(gnlTpRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> manager().add(request)).isInstanceOf(EntityNotFoundException.class);

        verify(typeValueRepository, never()).save(any());
    }

    @Test
    void add_savesTypeValue_whenParentExists() {
        CreateTypeValueRequest request = new CreateTypeValueRequest("GNL_ST", 2L, "Yeni deger", "NEW", null);
        when(gnlStRepository.existsById(2L)).thenReturn(true);
        when(typeValueRepository.save(any(TypeValue.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TypeValueResponse response = manager().add(request);

        assertThat(response.tableName()).isEqualTo("GNL_ST");
        assertThat(response.fieldName()).isEqualTo(2L);
    }

    @Test
    void getById_throwsEntityNotFoundException_whenMissing() {
        when(typeValueRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> manager().getById(1L)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void delete_deletesTypeValue() {
        when(typeValueRepository.findById(500L)).thenReturn(Optional.of(typeValue(500L)));

        manager().delete(500L);

        verify(typeValueRepository).deleteById(500L);
    }

    @Test
    void update_changesDescriptionValueAndUsingModuleName() {
        TypeValue existing = typeValue(4001L);
        when(typeValueRepository.findById(4001L)).thenReturn(Optional.of(existing));
        when(typeValueRepository.save(any(TypeValue.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateTypeValueRequest request = new UpdateTypeValueRequest("Degisen aciklama", "CHANGED", "customer-service");
        TypeValueResponse response = manager().update(4001L, request);

        assertThat(response.description()).isEqualTo("Degisen aciklama");
        assertThat(response.value()).isEqualTo("CHANGED");
        assertThat(response.usingModuleName()).isEqualTo("customer-service");
    }
}
