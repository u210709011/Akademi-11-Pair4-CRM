package com.etiya.crm.partyservice.business.concretes;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.partyservice.dataAccess.abstracts.PartyRoleRepository;
import com.etiya.crm.partyservice.entities.concretes.PartyRole;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartyRoleManagerTest {

	@Mock
	private PartyRoleRepository partyRoleRepository;

	@InjectMocks
	private PartyRoleManager manager;

	@Test
	void deactivatePartyRole_setsInactive_whenFound() {
		PartyRole role = new PartyRole();
		role.setActive(true);
		when(partyRoleRepository.findById(10L)).thenReturn(Optional.of(role));

		manager.deactivatePartyRole(10L);

		assertThat(role.isActive()).isFalse();
	}

	@Test
	void deactivatePartyRole_logsWarning_doesNotThrow_whenNotFound() {
		when(partyRoleRepository.findById(99L)).thenReturn(Optional.empty());

		manager.deactivatePartyRole(99L);
	}
}
