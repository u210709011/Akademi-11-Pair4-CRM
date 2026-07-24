package com.etiya.crm.lookupservice.business.concretes;

import com.etiya.crm.shared.contracts.rsrcspec.CreateRsrcSpecRequest;
import com.etiya.crm.lookupservice.business.exceptions.EntityNotFoundException;
import com.etiya.crm.lookupservice.dataAccess.abstracts.GnlStRepository;
import com.etiya.crm.lookupservice.dataAccess.abstracts.RsrcSpecRepository;
import com.etiya.crm.lookupservice.entities.concretes.RsrcSpec;
import com.etiya.crm.lookupservice.mapper.RsrcSpecMapper;
import com.etiya.crm.lookupservice.mapper.RsrcSpecMapperImpl;
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
class RsrcSpecManagerTest {

    @Mock
    private RsrcSpecRepository rsrcSpecRepository;

    @Mock
    private GnlStRepository gnlStRepository;

    private final RsrcSpecMapper rsrcSpecMapper = new RsrcSpecMapperImpl();

    private RsrcSpecManager manager() {
        return new RsrcSpecManager(rsrcSpecRepository, gnlStRepository, rsrcSpecMapper);
    }

    @Test
    void add_throwsEntityNotFoundException_whenStatusMissing() {
        CreateRsrcSpecRequest request = new CreateRsrcSpecRequest("SIM Kart", "Aciklama", 1L, "SIM");
        when(gnlStRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> manager().add(request)).isInstanceOf(EntityNotFoundException.class);

        verify(rsrcSpecRepository, never()).save(any());
    }

    @Test
    void getById_throwsEntityNotFoundException_whenMissing() {
        when(rsrcSpecRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> manager().getById(1L)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void delete_removesEntity() {
        RsrcSpec rsrcSpec = new RsrcSpec();
        rsrcSpec.setRsrcSpecId(1L);
        when(rsrcSpecRepository.findById(1L)).thenReturn(Optional.of(rsrcSpec));

        manager().delete(1L);

        verify(rsrcSpecRepository).delete(rsrcSpec);
    }
}
