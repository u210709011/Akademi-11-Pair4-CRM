package com.etiya.crm.lookupservice.business.concretes;

import com.etiya.crm.lookupservice.business.abstracts.TranslationService;
import com.etiya.crm.lookupservice.business.exceptions.EntityNotFoundException;
import com.etiya.crm.lookupservice.dataAccess.abstracts.GnlStRepository;
import com.etiya.crm.lookupservice.entities.concretes.GnlSt;
import com.etiya.crm.lookupservice.mapper.GnlStMapper;
import com.etiya.crm.lookupservice.mapper.GnlStMapperImpl;
import com.etiya.crm.shared.contracts.gnlst.CreateGnlStRequest;
import com.etiya.crm.shared.contracts.gnlst.GnlStResponse;
import com.etiya.crm.shared.contracts.gnlst.UpdateGnlStRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GnlStManagerTest {

    @Mock
    private GnlStRepository gnlStRepository;

    @Mock
    private TranslationService translationService;

    private final GnlStMapper gnlStMapper = new GnlStMapperImpl();

    private GnlStManager manager() {
        lenient().when(translationService.translate(any(), any(), any(), any()))
                .thenAnswer(invocation -> invocation.getArgument(3));
        return new GnlStManager(gnlStRepository, gnlStMapper, translationService);
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
        gnlSt.setName("Active");
        gnlSt.setDescr("Active");
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

    private static GnlSt entity(Long id) {
        GnlSt gnlSt = new GnlSt();
        gnlSt.setGnlStId(id);
        gnlSt.setName("Wait");
        gnlSt.setDescr("Waiting");
        gnlSt.setShrtCode("WAIT");
        gnlSt.setEntCodeName("CUST_ORD");
        gnlSt.setEntName("CUST_ORD");
        gnlSt.setActive(true);
        return gnlSt;
    }

    @Test
    void getAll_returnsPassthroughResponses() {
        when(gnlStRepository.findAll()).thenReturn(List.of(entity(1L)));

        List<GnlStResponse> responses = manager().getAll();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).name()).isEqualTo("Wait");
    }

    @Test
    void getAllByEntCodeName_returnsFilteredResponses() {
        when(gnlStRepository.findAllByEntCodeName("CUST_ORD")).thenReturn(List.of(entity(1L)));

        List<GnlStResponse> responses = manager().getAllByEntCodeName("CUST_ORD");

        assertThat(responses).hasSize(1);
    }

    @Test
    void getById_returnsTranslatedResponse_whenTranslationDiffersFromBase() {
        GnlSt gnlSt = entity(1L);
        when(gnlStRepository.findById(1L)).thenReturn(Optional.of(gnlSt));
        GnlStManager manager = manager();
        when(translationService.translate("GNL_ST", 1L, "NAME", "Wait")).thenReturn("Beklemede");
        when(translationService.translate("GNL_ST", 1L, "DESCR", "Waiting")).thenReturn("Beklemede");

        GnlStResponse response = manager.getById(1L);

        assertThat(response.name()).isEqualTo("Beklemede");
    }

    @Test
    void add_savesAndReturnsResponse() {
        CreateGnlStRequest request = new CreateGnlStRequest("Wait", "Waiting", "WAIT", true, "CUST_ORD", "CUST_ORD");
        when(gnlStRepository.save(any(GnlSt.class))).thenAnswer(invocation -> {
            GnlSt saved = invocation.getArgument(0);
            saved.setGnlStId(1L);
            return saved;
        });

        GnlStResponse response = manager().add(request);

        assertThat(response.gnlStId()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Wait");
    }

    @Test
    void update_overwritesAllMutableFields() {
        GnlSt gnlSt = entity(1L);
        when(gnlStRepository.findById(1L)).thenReturn(Optional.of(gnlSt));
        when(gnlStRepository.save(any(GnlSt.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UpdateGnlStRequest request = new UpdateGnlStRequest("Wait2", "Waiting2", "WAIT2", false, "CUST_ORD2",
                "CUST_ORD2");

        GnlStResponse response = manager().update(1L, request);

        assertThat(response.name()).isEqualTo("Wait2");
        assertThat(response.descr()).isEqualTo("Waiting2");
        assertThat(response.shrtCode()).isEqualTo("WAIT2");
        assertThat(response.active()).isFalse();
        assertThat(response.entCodeName()).isEqualTo("CUST_ORD2");
        assertThat(response.entName()).isEqualTo("CUST_ORD2");
    }
}
