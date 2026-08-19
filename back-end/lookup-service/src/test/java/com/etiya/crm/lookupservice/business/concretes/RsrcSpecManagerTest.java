package com.etiya.crm.lookupservice.business.concretes;

import com.etiya.crm.shared.contracts.rsrcspec.CreateRsrcSpecRequest;
import com.etiya.crm.shared.contracts.rsrcspec.RsrcSpecResponse;
import com.etiya.crm.shared.contracts.rsrcspec.UpdateRsrcSpecRequest;
import com.etiya.crm.lookupservice.business.abstracts.TranslationService;
import com.etiya.crm.lookupservice.business.exceptions.EntityNotFoundException;
import com.etiya.crm.lookupservice.business.rules.GnlStExistenceRule;
import com.etiya.crm.lookupservice.dataAccess.abstracts.GnlStRepository;
import com.etiya.crm.lookupservice.dataAccess.abstracts.RsrcSpecRepository;
import com.etiya.crm.lookupservice.entities.concretes.RsrcSpec;
import com.etiya.crm.lookupservice.mapper.RsrcSpecMapper;
import com.etiya.crm.lookupservice.mapper.RsrcSpecMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RsrcSpecManagerTest {

    @Mock
    private RsrcSpecRepository rsrcSpecRepository;

    @Mock
    private GnlStRepository gnlStRepository;

    @Mock
    private TranslationService translationService;

    private final RsrcSpecMapper rsrcSpecMapper = new RsrcSpecMapperImpl();

    private RsrcSpecManager manager() {
        return new RsrcSpecManager(rsrcSpecRepository, new GnlStExistenceRule(gnlStRepository), rsrcSpecMapper,
                translationService);
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

    private static RsrcSpec entity(Long id) {
        RsrcSpec rsrcSpec = new RsrcSpec();
        rsrcSpec.setRsrcSpecId(id);
        rsrcSpec.setName("SIM Kart");
        rsrcSpec.setDescr("Aciklama");
        rsrcSpec.setStId(1L);
        rsrcSpec.setRsrcCode("SIM");
        return rsrcSpec;
    }

    @Test
    void getAll_returnsPassthroughResponses_whenTranslationEqualsBase() {
        when(rsrcSpecRepository.findAll()).thenReturn(List.of(entity(1L)));
        when(translationService.translate(anyString(), anyLong(), anyString(), anyString()))
                .thenAnswer(invocation -> invocation.getArgument(3));

        List<RsrcSpecResponse> responses = manager().getAll();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).name()).isEqualTo("SIM Kart");
    }

    @Test
    void getById_returnsTranslatedResponse_whenTranslationDiffersFromBase() {
        RsrcSpec rsrcSpec = entity(1L);
        when(rsrcSpecRepository.findById(1L)).thenReturn(Optional.of(rsrcSpec));
        when(translationService.translate("RSRC_SPEC", 1L, "NAME", "SIM Kart")).thenReturn("SIM Card");
        when(translationService.translate("RSRC_SPEC", 1L, "DESCR", "Aciklama")).thenReturn("Description");

        RsrcSpecResponse response = manager().getById(1L);

        assertThat(response.name()).isEqualTo("SIM Card");
        assertThat(response.descr()).isEqualTo("Description");
    }

    @Test
    void add_savesAndReturnsResponse_whenStatusExists() {
        CreateRsrcSpecRequest request = new CreateRsrcSpecRequest("SIM Kart", "Aciklama", 1L, "SIM");
        when(gnlStRepository.existsById(1L)).thenReturn(true);
        when(rsrcSpecRepository.save(any(RsrcSpec.class))).thenAnswer(invocation -> {
            RsrcSpec saved = invocation.getArgument(0);
            saved.setRsrcSpecId(1L);
            return saved;
        });

        RsrcSpecResponse response = manager().add(request);

        assertThat(response.rsrcSpecId()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("SIM Kart");
    }

    @Test
    void update_throwsEntityNotFoundException_whenStatusMissing() {
        UpdateRsrcSpecRequest request = new UpdateRsrcSpecRequest("SIM Kart", "Aciklama", 1L, "SIM");
        when(gnlStRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> manager().update(1L, request)).isInstanceOf(EntityNotFoundException.class);

        verify(rsrcSpecRepository, never()).save(any());
    }

    @Test
    void update_overwritesAllMutableFields_whenStatusExists() {
        RsrcSpec rsrcSpec = entity(1L);
        when(gnlStRepository.existsById(2L)).thenReturn(true);
        when(rsrcSpecRepository.findById(1L)).thenReturn(Optional.of(rsrcSpec));
        when(rsrcSpecRepository.save(any(RsrcSpec.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UpdateRsrcSpecRequest request = new UpdateRsrcSpecRequest("SIM Kart 2", "Aciklama 2", 2L, "SIM2");

        RsrcSpecResponse response = manager().update(1L, request);

        assertThat(response.name()).isEqualTo("SIM Kart 2");
        assertThat(response.descr()).isEqualTo("Aciklama 2");
        assertThat(response.stId()).isEqualTo(2L);
        assertThat(response.rsrcCode()).isEqualTo("SIM2");
    }
}
