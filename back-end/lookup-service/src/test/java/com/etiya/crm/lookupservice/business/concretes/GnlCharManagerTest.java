package com.etiya.crm.lookupservice.business.concretes;

import com.etiya.crm.lookupservice.business.exceptions.EntityNotFoundException;
import com.etiya.crm.lookupservice.dataAccess.abstracts.GnlCharRepository;
import com.etiya.crm.lookupservice.entities.concretes.GnlChar;
import com.etiya.crm.lookupservice.mapper.GnlCharMapper;
import com.etiya.crm.lookupservice.mapper.GnlCharMapperImpl;
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
class GnlCharManagerTest {

    @Mock
    private GnlCharRepository gnlCharRepository;

    private final GnlCharMapper gnlCharMapper = new GnlCharMapperImpl();

    private GnlCharManager manager() {
        return new GnlCharManager(gnlCharRepository, gnlCharMapper);
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
}
