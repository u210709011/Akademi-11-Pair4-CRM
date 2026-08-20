package com.etiya.crm.productservice.business.concretes;

import java.util.Locale;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.i18n.LocaleContextHolder;

import com.etiya.crm.productservice.dataAccess.abstracts.TranslationRepository;
import com.etiya.crm.productservice.entities.concretes.Translation;

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

		String result = translationService.translate("PROD_CATAL", 1L, "NAME", "Mobile");

		assertThat(result).isEqualTo("Mobile");
		verifyNoInteractions(translationRepository);
	}

	@Test
	void translate_returnsTranslatedValue_whenRepositoryHasMatch() {
		LocaleContextHolder.setLocale(Locale.forLanguageTag("tr"));
		Translation translation = new Translation();
		translation.setValue("Mobil");
		when(translationRepository.findByEntityNameAndEntityIdAndFieldNameAndLocale("PROD_CATAL", 1L, "NAME", "tr"))
				.thenReturn(Optional.of(translation));

		String result = translationService.translate("PROD_CATAL", 1L, "NAME", "Mobile");

		assertThat(result).isEqualTo("Mobil");
	}

	@Test
	void translate_fallsBackToDefaultValue_whenRepositoryHasNoMatch() {
		LocaleContextHolder.setLocale(Locale.forLanguageTag("tr"));
		when(translationRepository.findByEntityNameAndEntityIdAndFieldNameAndLocale("PROD_CATAL", 1L, "NAME", "tr"))
				.thenReturn(Optional.empty());

		String result = translationService.translate("PROD_CATAL", 1L, "NAME", "Mobile");

		assertThat(result).isEqualTo("Mobile");
	}

}
