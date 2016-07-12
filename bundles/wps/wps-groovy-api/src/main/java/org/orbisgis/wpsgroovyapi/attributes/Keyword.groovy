package org.orbisgis.wpsgroovyapi.attributes

/**
 * @author Sylvain PALOMINOS
 */

@interface Keyword {
    /** List of LanguagesString containing the traduced keywords. */
    LanguageString[] traducedKeywords() default []


    /********************/
    /** default values **/
    /********************/
    public static final LanguageString[] defaultTraducedKeywords = []
}