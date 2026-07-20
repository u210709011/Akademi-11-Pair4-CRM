package com.etiya.crm.lookupservice.business.concretes;

import com.etiya.crm.lookupservice.business.dtos.requests.CreateSrvcSpecRequest;
import com.etiya.crm.lookupservice.business.exceptions.EntityNotFoundException;
import com.etiya.crm.lookupservice.dataAccess.abstracts.GnlStRepository;
import com.etiya.crm.lookupservice.dataAccess.abstracts.SrvcSpecRepository;
import com.etiya.crm.lookupservice.entities.concretes.SrvcSpec;
import com.etiya.crm.lookupservice.mapper.SrvcSpecMapper;
import com.etiya.crm.lookupservice.mapper.SrvcSpecMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SrvcSpecManagerTest {

    @Mock
    private SrvcSpecRepository srvcSpecRepository;

    @Mock
    private GnlStRepository gnlStRepository;

    private final SrvcSpecMapper srvcSpecMapper = new SrvcSpecMapperImpl();

    private SrvcSpecManager manager() {
        return new SrvcSpecManager(srvcSpecRepository, gnlStRepository, srvcSpecMapper);
    }

    @Test
    void add_throwsEntityNotFoundException_whenStatusMissing() {
        CreateSrvcSpecRequest request = new CreateSrvcSpecRequest("Aktivasyon", "Aciklama", "ACTIVATION", 1L);
        when(gnlStRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> manager().add(request)).isInstanceOf(EntityNotFoundException.class);

        verify(srvcSpecRepository, never()).save(any());
    }

    @Test
    void getById_throwsEntityNotFoundException_whenMissing() {
        when(srvcSpecRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> manager().getById(1L)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void delete_removesEntity() {
        SrvcSpec srvcSpec = new SrvcSpec();
        srvcSpec.setSrvcSpecId(1L);
        when(srvcSpecRepository.findById(1L)).thenReturn(Optional.of(srvcSpec));

        manager().delete(1L);

        verify(srvcSpecRepository).delete(srvcSpec);
    }
}
