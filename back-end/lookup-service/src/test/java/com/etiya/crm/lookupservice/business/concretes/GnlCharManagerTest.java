package com.etiya.crm.lookupservice.business.concretes;

import com.etiya.crm.lookupservice.business.abstracts.TranslationService;
import com.etiya.crm.lookupservice.business.exceptions.EntityNotFoundException;
import com.etiya.crm.lookupservice.dataAccess.abstracts.GnlCharRepository;
import com.etiya.crm.lookupservice.entities.concretes.GnlChar;
import com.etiya.crm.lookupservice.mapper.GnlCharMapper;
import com.etiya.crm.lookupservice.mapper.GnlCharMapperImpl;
import com.etiya.crm.shared.contracts.gnlchar.CreateGnlCharRequest;
import com.etiya.crm.shared.contracts.gnlchar.GnlCharResponse;
import com.etiya.crm.shared.contracts.gnlchar.UpdateGnlCharRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GnlCharManagerTest {

    @Mock
    private GnlCharRepository gnlCharRepository;

    @Mock
    private TranslationService translationService;

    private final GnlCharMapper gnlCharMapper = new GnlCharMapperImpl();

    private GnlCharManager manager() {
        return new GnlCharManager(gnlCharRepository, gnlCharMapper, translationService);
    }

    @Test
    void getById_throwsEntityNotFoundException_whenMissing() {
        when(gnlCharRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> manager().getById(1L)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void delete_softDeletes_setsActiveFalseAndSaves() {
        GnlChar gnlChar = new GnlChar();
        gnlChar.setCharId(1L);
        gnlChar.setActive(true);
        when(gnlCharRepository.findById(1L)).thenReturn(Optional.of(gnlChar));
        when(gnlCharRepository.save(any(GnlChar.class))).thenAnswer(invocation -> invocation.getArgument(0));

        manager().delete(1L);

        assertThat(gnlChar.isActive()).isFalse();
        verify(gnlCharRepository).save(gnlChar);
    }

    private static GnlChar entity(Long id, String name, String descr) {
        GnlChar gnlChar = new GnlChar();
        gnlChar.setCharId(id);
        gnlChar.setName(name);
        gnlChar.setDescr(descr);
        gnlChar.setPrvdrCls("cls");
        gnlChar.setShrtCode("COLOR");
        gnlChar.setActive(true);
        return gnlChar;
    }

    @Test
    void getAll_returnsPassthroughResponses_whenTranslationEqualsBase() {
        when(gnlCharRepository.findAll()).thenReturn(List.of(entity(1L, "Color", "desc")));
        when(translationService.translate(anyString(), any(), anyString(), anyString()))
                .thenAnswer(invocation -> invocation.getArgument(3));

        List<GnlCharResponse> responses = manager().getAll();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).name()).isEqualTo("Color");
    }

    @Test
    void getById_returnsTranslatedResponse_whenTranslationDiffersFromBase() {
        GnlChar gnlChar = entity(1L, "Color", "desc");
        when(gnlCharRepository.findById(1L)).thenReturn(Optional.of(gnlChar));
        when(translationService.translate("GNL_CHAR", 1L, "NAME", "Color")).thenReturn("Renk");
        when(translationService.translate("GNL_CHAR", 1L, "DESCR", "desc")).thenReturn("aciklama");

        GnlCharResponse response = manager().getById(1L);

        assertThat(response.name()).isEqualTo("Renk");
        assertThat(response.descr()).isEqualTo("aciklama");
        assertThat(response.charId()).isEqualTo(1L);
    }

    @Test
    void add_savesAndReturnsResponse() {
        CreateGnlCharRequest request = new CreateGnlCharRequest("Color", "desc", "cls", "COLOR", true);
        when(gnlCharRepository.save(any(GnlChar.class))).thenAnswer(invocation -> {
            GnlChar saved = invocation.getArgument(0);
            saved.setCharId(1L);
            return saved;
        });

        GnlCharResponse response = manager().add(request);

        assertThat(response.charId()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Color");
    }

    @Test
    void update_overwritesAllMutableFields() {
        GnlChar gnlChar = entity(1L, "Color", "desc");
        when(gnlCharRepository.findById(1L)).thenReturn(Optional.of(gnlChar));
        when(gnlCharRepository.save(any(GnlChar.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UpdateGnlCharRequest request = new UpdateGnlCharRequest("Color2", "desc2", "cls2", false);

        GnlCharResponse response = manager().update(1L, request);

        assertThat(response.name()).isEqualTo("Color2");
        assertThat(response.descr()).isEqualTo("desc2");
        assertThat(response.prvdrCls()).isEqualTo("cls2");
        assertThat(response.active()).isFalse();
    }
}
