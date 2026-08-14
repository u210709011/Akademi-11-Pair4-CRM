package com.etiya.crm.lookupservice.business.concretes;

import com.etiya.crm.lookupservice.business.abstracts.TranslationService;
import com.etiya.crm.lookupservice.dataAccess.abstracts.TranslationRepository;
import com.etiya.crm.lookupservice.entities.concretes.Translation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TranslationServiceImpl implements TranslationService {

    private static final String DEFAULT_LANGUAGE = "en";

    private final TranslationRepository translationRepository;

    @Override
    public String translate(String entityName, Long entityId, String fieldName, String defaultValue) {
        String language = LocaleContextHolder.getLocale().getLanguage();
        if (language.isBlank() || DEFAULT_LANGUAGE.equalsIgnoreCase(language)) {
            return defaultValue;
        }
        return translationRepository.findByEntityNameAndEntityIdAndFieldNameAndLocale(
                        entityName, entityId, fieldName, language)
                .map(Translation::getValue)
                .orElse(defaultValue);
    }
}
