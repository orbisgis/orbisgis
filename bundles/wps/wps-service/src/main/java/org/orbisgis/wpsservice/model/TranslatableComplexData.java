package org.orbisgis.wpsservice.model;

import net.opengis.wps._2_0.ComplexDataType;

/**
 * This interface is used to make a WPS ComplexData translatable and gives a method to translate the attributes of
 * the ComplexData
 *
 * @author Sylvain PALOMINOS
 */
public interface TranslatableComplexData {

    /**
     * Returns a translated version of this object.
     * The translated version must be the same as the original excepted the human readable strings which can be
     * translated.
     * This method receive the default server language, the client asked language. The translated language must be, if
     * possible, the client language or if not found the server language. It none of the language are found, uses any
     * language. If the server or the client language is '*', all the languages are accepted.
     *
     * As example :
     * server language : 'en', client languages : 'fr_FR'
     * 1) try to get the fr_FR translated language. (first client requested language)
     * 2) try to get the fr translated language. (first client requested language without the regional one)
     * 4) try to get the en translated language. (server default language)
     * 5) any language.
     *
     * @param serverLanguage The default server language.
     * @param clientLanguages The languages requested by the client.
     * @return A copy of the object itself but with its attribute translated.
     */
    public ComplexDataType getTranslatedData(String serverLanguage, String clientLanguages);
}
