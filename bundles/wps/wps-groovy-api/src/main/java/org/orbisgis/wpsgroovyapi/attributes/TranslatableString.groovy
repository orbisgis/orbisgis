package org.orbisgis.wpsgroovyapi.attributes

/**
 * @author Sylvain PALOMINOS
 */

@interface TranslatableString {
    /** List of LanguagesString containing the translated keywords. */
    LanguageString[] translatableStrings() default []


    /********************/
    /** default values **/
    /********************/
    public static final LanguageString[] defaultTranslatableKeywords = []
}