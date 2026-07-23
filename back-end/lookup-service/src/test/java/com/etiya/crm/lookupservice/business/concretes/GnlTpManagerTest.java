package com.etiya.crm.lookupservice.business.concretes;

import com.etiya.crm.shared.contracts.gnltp.UpdateGnlTpRequest;
import com.etiya.crm.lookupservice.business.exceptions.EntityNotFoundException;
import com.etiya.crm.lookupservice.dataAccess.abstracts.GnlTpRepository;
import com.etiya.crm.lookupservice.entities.concretes.GnlTp;
import com.etiya.crm.lookupservice.mapper.GnlTpMapper;
import com.etiya.crm.lookupservice.mapper.GnlTpMapperImpl;
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
class GnlTpManagerTest {

    @Mock
    private GnlTpRepository gnlTpRepository;

    private final GnlTpMapper gnlTpMapper = new GnlTpMapperImpl();

    private GnlTpManager manager() {
        return new GnlTpManager(gnlTpRepository, gnlTpMapper);
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
        when(gnlTpRepository.findByEntCodeNameAndShrtCode("CNTC_MEDIUM", "GSM")).thenReturn(Optional.of(gnlTp));

        var response = manager().getByEntCodeNameAndShrtCode("CNTC_MEDIUM", "GSM");

        assertThat(response.gnlTpId()).isEqualTo(5L);
    }

    @Test
    void getByEntCodeNameAndShrtCode_throwsEntityNotFoundException_whenMissing() {
        when(gnlTpRepository.findByEntCodeNameAndShrtCode("CNTC_MEDIUM", "UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> manager().getByEntCodeNameAndShrtCode("CNTC_MEDIUM", "UNKNOWN"))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
