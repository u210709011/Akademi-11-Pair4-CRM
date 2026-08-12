package com.etiya.crm.lookupservice.business.abstracts;

/**
 * Herhangi bir varliğin herhangi bir string alanini istekteki dile (LocaleContextHolder) gore
 * cevirir. Varsayilan dil Ingilizce'dir - ingilizce istekte translation tablosuna hic gidilmez,
 * defaultValue oldugu gibi donulur.
 */
public interface TranslationService {

    String translate(String entityName, Long entityId, String fieldName, String defaultValue);
}
