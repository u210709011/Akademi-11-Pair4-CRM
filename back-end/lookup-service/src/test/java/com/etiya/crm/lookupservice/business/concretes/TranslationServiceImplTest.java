package com.etiya.crm.lookupservice.business.concretes;

import java.util.Locale;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.i18n.LocaleContextHolder;

import com.etiya.crm.lookupservice.dataAccess.abstracts.TranslationRepository;
import com.etiya.crm.lookupservice.entities.concretes.Translation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TranslationServiceImplTest {

	@Mock
	private TranslationRepository translationRepository;

	@InjectMocks
	private TranslationServiceImpl translationService;

	@AfterEach
	void resetLocale() {
		LocaleContextHolder.resetLocaleContext();
	}

	@Test
	void translate_returnsDefaultValue_whenLocaleIsEnglish_withoutQueryingRepository() {
		LocaleContextHolder.setLocale(Locale.ENGLISH);

		String result = translationService.translate("GNL_CHAR", 1L, "NAME", "Color");

		assertThat(result).isEqualTo("Color");
		verifyNoInteractions(translationRepository);
	}

	@Test
	void translate_returnsTranslatedValue_whenRepositoryHasMatch() {
		LocaleContextHolder.setLocale(Locale.forLanguageTag("tr"));
		Translation translation = new Translation();
		translation.setValue("Renk");
		when(translationRepository.findByEntityNameAndEntityIdAndFieldNameAndLocale("GNL_CHAR", 1L, "NAME", "tr"))
				.thenReturn(Optional.of(translation));

		String result = translationService.translate("GNL_CHAR", 1L, "NAME", "Color");

		assertThat(result).isEqualTo("Renk");
	}

	@Test
	void translate_fallsBackToDefaultValue_whenRepositoryHasNoMatch() {
		LocaleContextHolder.setLocale(Locale.forLanguageTag("tr"));
		when(translationRepository.findByEntityNameAndEntityIdAndFieldNameAndLocale("GNL_CHAR", 1L, "NAME", "tr"))
				.thenReturn(Optional.empty());

		String result = translationService.translate("GNL_CHAR", 1L, "NAME", "Color");

		assertThat(result).isEqualTo("Color");
	}

}
