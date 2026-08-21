package com.etiya.crm.lookupservice.business.concretes;

import com.etiya.crm.shared.contracts.srvcspec.CreateSrvcSpecRequest;
import com.etiya.crm.shared.contracts.srvcspec.SrvcSpecResponse;
import com.etiya.crm.shared.contracts.srvcspec.UpdateSrvcSpecRequest;
import com.etiya.crm.lookupservice.business.abstracts.TranslationService;
import com.etiya.crm.lookupservice.business.exceptions.EntityNotFoundException;
import com.etiya.crm.lookupservice.business.rules.GnlStExistenceRule;
import com.etiya.crm.lookupservice.dataAccess.abstracts.GnlStRepository;
import com.etiya.crm.lookupservice.dataAccess.abstracts.SrvcSpecRepository;
import com.etiya.crm.lookupservice.entities.concretes.SrvcSpec;
import com.etiya.crm.lookupservice.mapper.SrvcSpecMapper;
import com.etiya.crm.lookupservice.mapper.SrvcSpecMapperImpl;
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
class SrvcSpecManagerTest {

    @Mock
    private SrvcSpecRepository srvcSpecRepository;

    @Mock
    private GnlStRepository gnlStRepository;

    @Mock
    private TranslationService translationService;

    private final SrvcSpecMapper srvcSpecMapper = new SrvcSpecMapperImpl();

    private SrvcSpecManager manager() {
        return new SrvcSpecManager(srvcSpecRepository, new GnlStExistenceRule(gnlStRepository), srvcSpecMapper,
                translationService);
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

    private static SrvcSpec entity(Long id) {
        SrvcSpec srvcSpec = new SrvcSpec();
        srvcSpec.setSrvcSpecId(id);
        srvcSpec.setName("Aktivasyon");
        srvcSpec.setDescr("Aciklama");
        srvcSpec.setSrvcCode("ACTIVATION");
        srvcSpec.setStId(1L);
        return srvcSpec;
    }

    @Test
    void getAll_returnsPassthroughResponses_whenTranslationEqualsBase() {
        when(srvcSpecRepository.findAll()).thenReturn(List.of(entity(1L)));
        when(translationService.translate(anyString(), anyLong(), anyString(), anyString()))
                .thenAnswer(invocation -> invocation.getArgument(3));

        List<SrvcSpecResponse> responses = manager().getAll();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).name()).isEqualTo("Aktivasyon");
    }

    @Test
    void getById_returnsTranslatedResponse_whenTranslationDiffersFromBase() {
        SrvcSpec srvcSpec = entity(1L);
        when(srvcSpecRepository.findById(1L)).thenReturn(Optional.of(srvcSpec));
        when(translationService.translate("SRVC_SPEC", 1L, "NAME", "Aktivasyon")).thenReturn("Activation");
        when(translationService.translate("SRVC_SPEC", 1L, "DESCR", "Aciklama")).thenReturn("Description");

        SrvcSpecResponse response = manager().getById(1L);

        assertThat(response.name()).isEqualTo("Activation");
        assertThat(response.descr()).isEqualTo("Description");
    }

    @Test
    void add_savesAndReturnsResponse_whenStatusExists() {
        CreateSrvcSpecRequest request = new CreateSrvcSpecRequest("Aktivasyon", "Aciklama", "ACTIVATION", 1L);
        when(gnlStRepository.existsById(1L)).thenReturn(true);
        when(srvcSpecRepository.save(any(SrvcSpec.class))).thenAnswer(invocation -> {
            SrvcSpec saved = invocation.getArgument(0);
            saved.setSrvcSpecId(1L);
            return saved;
        });

        SrvcSpecResponse response = manager().add(request);

        assertThat(response.srvcSpecId()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Aktivasyon");
    }

    @Test
    void update_throwsEntityNotFoundException_whenStatusMissing() {
        UpdateSrvcSpecRequest request = new UpdateSrvcSpecRequest("Aktivasyon", "Aciklama", "ACTIVATION", 1L);
        when(gnlStRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> manager().update(1L, request)).isInstanceOf(EntityNotFoundException.class);

        verify(srvcSpecRepository, never()).save(any());
    }

    @Test
    void update_overwritesAllMutableFields_whenStatusExists() {
        SrvcSpec srvcSpec = entity(1L);
        when(gnlStRepository.existsById(2L)).thenReturn(true);
        when(srvcSpecRepository.findById(1L)).thenReturn(Optional.of(srvcSpec));
        when(srvcSpecRepository.save(any(SrvcSpec.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UpdateSrvcSpecRequest request = new UpdateSrvcSpecRequest("Aktivasyon 2", "Aciklama 2", "ACTIVATION2", 2L);

        SrvcSpecResponse response = manager().update(1L, request);

        assertThat(response.name()).isEqualTo("Aktivasyon 2");
        assertThat(response.descr()).isEqualTo("Aciklama 2");
        assertThat(response.srvcCode()).isEqualTo("ACTIVATION2");
        assertThat(response.stId()).isEqualTo(2L);
    }
}
