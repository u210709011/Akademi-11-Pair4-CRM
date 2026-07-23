package com.etiya.crm.lookupservice.business.concretes;

import com.etiya.crm.lookupservice.business.exceptions.EntityNotFoundException;
import com.etiya.crm.lookupservice.dataAccess.abstracts.GnlStRepository;
import com.etiya.crm.lookupservice.entities.concretes.GnlSt;
import com.etiya.crm.lookupservice.mapper.GnlStMapper;
import com.etiya.crm.lookupservice.mapper.GnlStMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GnlStManagerTest {

    @Mock
    private GnlStRepository gnlStRepository;

    private final GnlStMapper gnlStMapper = new GnlStMapperImpl();

    private GnlStManager manager() {
        return new GnlStManager(gnlStRepository, gnlStMapper);
    }

    @Test
    void getById_throwsEntityNotFoundException_whenMissing() {
        when(gnlStRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> manager().getById(1L)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void delete_softDeletes_setsActiveFalseAndSaves() {
        GnlSt gnlSt = new GnlSt();
        gnlSt.setGnlStId(1L);
        gnlSt.setActive(true);
        when(gnlStRepository.findById(1L)).thenReturn(Optional.of(gnlSt));
        when(gnlStRepository.save(any(GnlSt.class))).thenAnswer(invocation -> invocation.getArgument(0));

        manager().delete(1L);

        assertThat(gnlSt.isActive()).isFalse();
        verify(gnlStRepository).save(gnlSt);
    }

    @Test
    void getByEntCodeNameAndShrtCode_returnsMappedResponse() {
        GnlSt gnlSt = new GnlSt();
        gnlSt.setGnlStId(5L);
        gnlSt.setEntCodeName("CUST_STATUS");
        gnlSt.setShrtCode("ACTIVE");
        when(gnlStRepository.findByEntCodeNameAndShrtCode("CUST_STATUS", "ACTIVE")).thenReturn(Optional.of(gnlSt));

        var response = manager().getByEntCodeNameAndShrtCode("CUST_STATUS", "ACTIVE");

        assertThat(response.gnlStId()).isEqualTo(5L);
    }

    @Test
    void getByEntCodeNameAndShrtCode_throwsEntityNotFoundException_whenMissing() {
        when(gnlStRepository.findByEntCodeNameAndShrtCode("CUST_STATUS", "UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> manager().getByEntCodeNameAndShrtCode("CUST_STATUS", "UNKNOWN"))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
