package com.etiya.crm.lookupservice.business.concretes;

import com.etiya.crm.shared.contracts.gnltp.CreateGnlTpRequest;
import com.etiya.crm.shared.contracts.gnltp.GnlTpResponse;
import com.etiya.crm.shared.contracts.gnltp.UpdateGnlTpRequest;
import com.etiya.crm.lookupservice.business.abstracts.TranslationService;
import com.etiya.crm.lookupservice.business.exceptions.EntityNotFoundException;
import com.etiya.crm.lookupservice.dataAccess.abstracts.GnlTpRepository;
import com.etiya.crm.lookupservice.entities.concretes.GnlTp;
import com.etiya.crm.lookupservice.mapper.GnlTpMapper;
import com.etiya.crm.lookupservice.mapper.GnlTpMapperImpl;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GnlTpManagerTest {

    @Mock
    private GnlTpRepository gnlTpRepository;

    @Mock
    private TranslationService translationService;

    private final GnlTpMapper gnlTpMapper = new GnlTpMapperImpl();

    private GnlTpManager manager() {
        return new GnlTpManager(gnlTpRepository, gnlTpMapper, translationService);
    }

    @Test
    void getById_throwsEntityNotFoundException_whenMissing() {
        when(gnlTpRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> manager().getById(1L)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void delete_softDeletes_setsActiveFalseAndSaves() {
        GnlTp gnlTp = new GnlTp();
        gnlTp.setGnlTpId(1L);
        gnlTp.setActive(true);
        when(gnlTpRepository.findById(1L)).thenReturn(Optional.of(gnlTp));
        when(gnlTpRepository.save(any(GnlTp.class))).thenAnswer(invocation -> invocation.getArgument(0));

        manager().delete(1L);

        assertThat(gnlTp.isActive()).isFalse();
        verify(gnlTpRepository).save(gnlTp);
    }

    @Test
    void update_changesShrtCode() {
        GnlTp gnlTp = new GnlTp();
        gnlTp.setGnlTpId(1L);
        gnlTp.setShrtCode("OLD_CODE");
        when(gnlTpRepository.findById(1L)).thenReturn(Optional.of(gnlTp));
        when(gnlTpRepository.save(any(GnlTp.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateGnlTpRequest request = new UpdateGnlTpRequest("Yeni Ad", "Yeni Aciklama", "NEW_CODE", "ACCT_TP", null, true);
        manager().update(1L, request);

        assertThat(gnlTp.getShrtCode()).isEqualTo("NEW_CODE");
    }

    @Test
    void getByEntCodeNameAndShrtCode_returnsMappedResponse() {
        GnlTp gnlTp = new GnlTp();
        gnlTp.setGnlTpId(5L);
        gnlTp.setEntCodeName("CNTC_MEDIUM");
        gnlTp.setShrtCode("GSM");
        gnlTp.setName("Mobile");
        gnlTp.setDescr("Mobile phone number");
        when(gnlTpRepository.findByEntCodeNameAndShrtCode("CNTC_MEDIUM", "GSM")).thenReturn(Optional.of(gnlTp));
        // Ingilizce disi ceviri yoksa TranslationService taban degeri (4. arg) oldugu gibi doner.
        when(translationService.translate(anyString(), anyLong(), anyString(), anyString()))
                .thenAnswer(invocation -> invocation.getArgument(3));

        var response = manager().getByEntCodeNameAndShrtCode("CNTC_MEDIUM", "GSM");

        assertThat(response.gnlTpId()).isEqualTo(5L);
    }

    @Test
    void getByEntCodeNameAndShrtCode_throwsEntityNotFoundException_whenMissing() {
        when(gnlTpRepository.findByEntCodeNameAndShrtCode("CNTC_MEDIUM", "UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> manager().getByEntCodeNameAndShrtCode("CNTC_MEDIUM", "UNKNOWN"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    private static GnlTp entity(Long id) {
        GnlTp gnlTp = new GnlTp();
        gnlTp.setGnlTpId(id);
        gnlTp.setName("Mobile");
        gnlTp.setDescr("Mobile phone number");
        gnlTp.setShrtCode("GSM");
        gnlTp.setEntCodeName("CNTC_MEDIUM");
        gnlTp.setEntName("CNTC_MEDIUM");
        gnlTp.setActive(true);
        return gnlTp;
    }

    @Test
    void getAll_returnsPassthroughResponses_whenTranslationEqualsBase() {
        when(gnlTpRepository.findAll()).thenReturn(List.of(entity(1L)));
        when(translationService.translate(anyString(), anyLong(), anyString(), anyString()))
                .thenAnswer(invocation -> invocation.getArgument(3));

        List<GnlTpResponse> responses = manager().getAll();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).name()).isEqualTo("Mobile");
    }

    @Test
    void getAllByEntCodeName_returnsFilteredResponses() {
        when(gnlTpRepository.findAllByEntCodeName("CNTC_MEDIUM")).thenReturn(List.of(entity(1L)));
        when(translationService.translate(anyString(), anyLong(), anyString(), anyString()))
                .thenAnswer(invocation -> invocation.getArgument(3));

        List<GnlTpResponse> responses = manager().getAllByEntCodeName("CNTC_MEDIUM");

        assertThat(responses).hasSize(1);
    }

    @Test
    void getById_returnsTranslatedResponse_whenTranslationDiffersFromBase() {
        GnlTp gnlTp = entity(1L);
        when(gnlTpRepository.findById(1L)).thenReturn(Optional.of(gnlTp));
        when(translationService.translate("GNL_TP", 1L, "NAME", "Mobile")).thenReturn("Mobil");
        when(translationService.translate("GNL_TP", 1L, "DESCR", "Mobile phone number")).thenReturn("Mobil");

        GnlTpResponse response = manager().getById(1L);

        assertThat(response.name()).isEqualTo("Mobil");
    }

    @Test
    void add_savesAndReturnsResponse() {
        CreateGnlTpRequest request = new CreateGnlTpRequest("Mobile", "Mobile phone number", "GSM", "CNTC_MEDIUM",
                "CNTC_MEDIUM", true);
        when(gnlTpRepository.save(any(GnlTp.class))).thenAnswer(invocation -> {
            GnlTp saved = invocation.getArgument(0);
            saved.setGnlTpId(1L);
            return saved;
        });

        GnlTpResponse response = manager().add(request);

        assertThat(response.gnlTpId()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Mobile");
    }

    @Test
    void update_overwritesAllMutableFields() {
        GnlTp gnlTp = entity(1L);
        when(gnlTpRepository.findById(1L)).thenReturn(Optional.of(gnlTp));
        when(gnlTpRepository.save(any(GnlTp.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UpdateGnlTpRequest request = new UpdateGnlTpRequest("Mobile2", "desc2", "GSM2", "CNTC_MEDIUM2",
                "CNTC_MEDIUM2", false);

        GnlTpResponse response = manager().update(1L, request);

        assertThat(response.name()).isEqualTo("Mobile2");
        assertThat(response.descr()).isEqualTo("desc2");
        assertThat(response.shrtCode()).isEqualTo("GSM2");
        assertThat(response.entCodeName()).isEqualTo("CNTC_MEDIUM2");
        assertThat(response.entName()).isEqualTo("CNTC_MEDIUM2");
        assertThat(response.active()).isFalse();
    }
}
