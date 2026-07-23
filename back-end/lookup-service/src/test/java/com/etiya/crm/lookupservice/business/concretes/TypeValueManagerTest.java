package com.etiya.crm.lookupservice.business.concretes;

import com.etiya.crm.shared.contracts.typevalue.CreateTypeValueRequest;
import com.etiya.crm.shared.contracts.typevalue.UpdateTypeValueRequest;
import com.etiya.crm.shared.contracts.typevalue.TypeValueResponse;
import com.etiya.crm.lookupservice.business.exceptions.EntityNotFoundException;
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

    private final TypeValueMapper typeValueMapper = new TypeValueMapperImpl();

    private TypeValueManager manager() {
        return new TypeValueManager(typeValueRepository, typeValueMapper);
    }

    private TypeValue typeValue(Long id) {
        TypeValue typeValue = new TypeValue();
        typeValue.setTypeValueId(id);
        typeValue.setTableName("PARTY");
        typeValue.setFieldName(9L);
        typeValue.setDescription("Party_id");
        return typeValue;
    }

    @Test
    void add_savesTypeValue() {
        CreateTypeValueRequest request = new CreateTypeValueRequest("CUST", 12L, "Cust_id", null, null);
        when(typeValueRepository.save(any(TypeValue.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TypeValueResponse response = manager().add(request);

        assertThat(response.tableName()).isEqualTo("CUST");
        assertThat(response.fieldName()).isEqualTo(12L);
    }

    @Test
    void getById_throwsEntityNotFoundException_whenMissing() {
        when(typeValueRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> manager().getById(1L)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void delete_deletesTypeValue() {
        when(typeValueRepository.findById(9L)).thenReturn(Optional.of(typeValue(9L)));

        manager().delete(9L);

        verify(typeValueRepository).deleteById(9L);
    }

    @Test
    void delete_throwsEntityNotFoundException_whenMissing() {
        when(typeValueRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> manager().delete(999L)).isInstanceOf(EntityNotFoundException.class);

        verify(typeValueRepository, never()).deleteById(any());
    }

    @Test
    void update_changesDescriptionValueAndUsingModuleName() {
        TypeValue existing = typeValue(9L);
        when(typeValueRepository.findById(9L)).thenReturn(Optional.of(existing));
        when(typeValueRepository.save(any(TypeValue.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateTypeValueRequest request = new UpdateTypeValueRequest("Degisen aciklama", "CHANGED", "party-service");
        TypeValueResponse response = manager().update(9L, request);

        assertThat(response.description()).isEqualTo("Degisen aciklama");
        assertThat(response.value()).isEqualTo("CHANGED");
        assertThat(response.usingModuleName()).isEqualTo("party-service");
    }
}
