package com.etiya.crm.lookupservice.business.concretes;

import com.etiya.crm.shared.contracts.gnlcharval.CreateGnlCharValRequest;
import com.etiya.crm.shared.contracts.gnlcharval.UpdateGnlCharValRequest;
import com.etiya.crm.lookupservice.business.abstracts.TranslationService;
import com.etiya.crm.lookupservice.business.exceptions.BusinessException;
import com.etiya.crm.lookupservice.business.exceptions.EntityNotFoundException;
import com.etiya.crm.lookupservice.dataAccess.abstracts.GnlCharRepository;
import com.etiya.crm.lookupservice.dataAccess.abstracts.GnlCharValRepository;
import com.etiya.crm.lookupservice.entities.concretes.GnlCharVal;
import com.etiya.crm.lookupservice.mapper.GnlCharValMapper;
import com.etiya.crm.lookupservice.mapper.GnlCharValMapperImpl;
import com.etiya.crm.shared.contracts.gnlcharval.GnlCharValResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GnlCharValManagerTest {

    @Mock
    private GnlCharValRepository gnlCharValRepository;

    @Mock
    private GnlCharRepository gnlCharRepository;

    @Mock
    private TranslationService translationService;

    private final GnlCharValMapper gnlCharValMapper = new GnlCharValMapperImpl();

    private GnlCharValManager manager() {
        return new GnlCharValManager(gnlCharValRepository, gnlCharRepository, gnlCharValMapper, translationService);
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

    private static GnlCharVal entity(Long id) {
        GnlCharVal gnlCharVal = new GnlCharVal();
        gnlCharVal.setCharValId(id);
        gnlCharVal.setCharId(1L);
        gnlCharVal.setDflt(false);
        gnlCharVal.setVal("Red");
        gnlCharVal.setShrtCode("RED");
        gnlCharVal.setSdate(LocalDate.of(2026, 1, 1));
        gnlCharVal.setActive(true);
        return gnlCharVal;
    }

    @Test
    void getAll_returnsPassthroughResponses_whenTranslationEqualsBase() {
        when(gnlCharValRepository.findAll()).thenReturn(List.of(entity(1L)));
        when(translationService.translate(anyString(), any(), anyString(), anyString()))
                .thenAnswer(invocation -> invocation.getArgument(3));

        List<GnlCharValResponse> responses = manager().getAll();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).val()).isEqualTo("Red");
    }

    @Test
    void getById_returnsTranslatedResponse_whenTranslationDiffersFromBase() {
        GnlCharVal gnlCharVal = entity(1L);
        when(gnlCharValRepository.findById(1L)).thenReturn(Optional.of(gnlCharVal));
        when(translationService.translate("GNL_CHAR_VAL", 1L, "VAL", "Red")).thenReturn("Kirmizi");

        GnlCharValResponse response = manager().getById(1L);

        assertThat(response.val()).isEqualTo("Kirmizi");
        assertThat(response.charValId()).isEqualTo(1L);
    }

    @Test
    void add_savesAndReturnsResponse_whenCharExistsAndDateRangeValid() {
        CreateGnlCharValRequest request = new CreateGnlCharValRequest(
                1L, false, "Red", "RED", LocalDate.of(2026, 1, 1), null, true);
        when(gnlCharRepository.existsById(1L)).thenReturn(true);
        when(gnlCharValRepository.save(any(GnlCharVal.class))).thenAnswer(invocation -> {
            GnlCharVal saved = invocation.getArgument(0);
            saved.setCharValId(1L);
            return saved;
        });

        GnlCharValResponse response = manager().add(request);

        assertThat(response.charValId()).isEqualTo(1L);
        assertThat(response.val()).isEqualTo("Red");
    }

    @Test
    void update_overwritesAllMutableFields_whenDateRangeValid() {
        GnlCharVal gnlCharVal = entity(1L);
        when(gnlCharValRepository.findById(1L)).thenReturn(Optional.of(gnlCharVal));
        when(gnlCharValRepository.save(any(GnlCharVal.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UpdateGnlCharValRequest request = new UpdateGnlCharValRequest(
                true, "Red2", "RED2", LocalDate.of(2026, 2, 1), LocalDate.of(2026, 12, 31), false);

        GnlCharValResponse response = manager().update(1L, request);

        assertThat(response.val()).isEqualTo("Red2");
        assertThat(response.dflt()).isTrue();
        assertThat(response.shrtCode()).isEqualTo("RED2");
        assertThat(response.active()).isFalse();
    }
}
