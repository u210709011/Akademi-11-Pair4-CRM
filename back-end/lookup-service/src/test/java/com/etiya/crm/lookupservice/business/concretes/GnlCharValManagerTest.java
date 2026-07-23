package com.etiya.crm.lookupservice.business.concretes;

import com.etiya.crm.shared.contracts.gnlcharval.CreateGnlCharValRequest;
import com.etiya.crm.shared.contracts.gnlcharval.UpdateGnlCharValRequest;
import com.etiya.crm.lookupservice.business.exceptions.BusinessException;
import com.etiya.crm.lookupservice.business.exceptions.EntityNotFoundException;
import com.etiya.crm.lookupservice.dataAccess.abstracts.GnlCharRepository;
import com.etiya.crm.lookupservice.dataAccess.abstracts.GnlCharValRepository;
import com.etiya.crm.lookupservice.entities.concretes.GnlCharVal;
import com.etiya.crm.lookupservice.mapper.GnlCharValMapper;
import com.etiya.crm.lookupservice.mapper.GnlCharValMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GnlCharValManagerTest {

    @Mock
    private GnlCharValRepository gnlCharValRepository;

    @Mock
    private GnlCharRepository gnlCharRepository;

    private final GnlCharValMapper gnlCharValMapper = new GnlCharValMapperImpl();

    private GnlCharValManager manager() {
        return new GnlCharValManager(gnlCharValRepository, gnlCharRepository, gnlCharValMapper);
    }

    @Test
    void add_throwsEntityNotFoundException_whenCharIdMissing() {
        CreateGnlCharValRequest request = new CreateGnlCharValRequest(
                1L, false, "Kirmizi", "RED", LocalDate.of(2026, 1, 1), null, true);
        when(gnlCharRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> manager().add(request)).isInstanceOf(EntityNotFoundException.class);

        verify(gnlCharValRepository, never()).save(any());
    }

    @Test
    void add_throwsBusinessException_whenEdateBeforeSdate() {
        CreateGnlCharValRequest request = new CreateGnlCharValRequest(
                1L, false, "Kirmizi", "RED", LocalDate.of(2026, 6, 1), LocalDate.of(2026, 1, 1), true);
        when(gnlCharRepository.existsById(1L)).thenReturn(true);

        assertThatThrownBy(() -> manager().add(request)).isInstanceOf(BusinessException.class);

        verify(gnlCharValRepository, never()).save(any());
    }

    @Test
    void delete_softDeletes_setsActiveFalseAndSaves() {
        GnlCharVal gnlCharVal = new GnlCharVal();
        gnlCharVal.setCharValId(1L);
        gnlCharVal.setActive(true);
        when(gnlCharValRepository.findById(1L)).thenReturn(Optional.of(gnlCharVal));
        when(gnlCharValRepository.save(any(GnlCharVal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        manager().delete(1L);

        assertThat(gnlCharVal.isActive()).isFalse();
        verify(gnlCharValRepository).save(gnlCharVal);
    }

    @Test
    void update_throwsBusinessException_whenEdateBeforeSdate() {
        UpdateGnlCharValRequest request = new UpdateGnlCharValRequest(
                false, "Kirmizi", "RED", LocalDate.of(2026, 6, 1), LocalDate.of(2026, 1, 1), true);

        assertThatThrownBy(() -> manager().update(1L, request)).isInstanceOf(BusinessException.class);

        verify(gnlCharValRepository, never()).save(any());
    }
}
