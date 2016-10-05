package org.orbisgis.wpsservice

import org.orbisgis.wpsgroovyapi.attributes.Keyword
import org.orbisgis.wpsgroovyapi.attributes.LanguageString
import org.orbisgis.wpsgroovyapi.attributes.MetadataAttribute
import org.orbisgis.wpsgroovyapi.input.EnumerationInput
import org.orbisgis.wpsgroovyapi.output.EnumerationOutput
import org.orbisgis.wpsgroovyapi.process.Process

/********************/
/** Process method **/
/********************/

/**
 * Test script for the Enumeration
 * @author Sylvain PALOMINOS
 */
@Process(title = "EnumerationTest",
        traducedTitles = [
                @LanguageString(value = "Enumeration test", lang = "en"),
                @LanguageString(value = "Test du Enumeration", lang = "fr")
        ],
        resume = "Test script using the Enumeration ComplexData.",
        traducedResumes = [
                @LanguageString(value = "Test script using the Enumeration ComplexData.", lang = "en"),
                @LanguageString(value = "Scripts test pour l'usage du ComplexData Enumeration.", lang = "fr")
        ],
        keywords = ["test", "script", "wps"],
        traducedKeywords = [
                @Keyword(traducedKeywords = [
                        @LanguageString(value = "test", lang = "en"),
                        @LanguageString(value = "test", lang = "fr")
                ]),
                @Keyword(traducedKeywords = [
                        @LanguageString(value = "script", lang = "en"),
                        @LanguageString(value = "scripte", lang = "fr")
                ]),
                @Keyword(traducedKeywords = [
                        @LanguageString(value = "wps", lang = "en"),
                        @LanguageString(value = "wps", lang = "fr")
                ])
        ],
        identifier = "orbisgis:test:enumerationLongProcess",
        metadata = [
                @MetadataAttribute(title = "metadata", role = "website", href = "http://orbisgis.org/")
        ]
)
def processing() {
    sleep(50000)
    enumerationOutput = inputEnumeration;
}


/****************/
/** INPUT Data **/
/****************/

/** This Enumeration is the input data source. */
@EnumerationInput(
        title = "Input Enumeration",
        traducedTitles = [
                @LanguageString(value = "Input Enumeration", lang = "en"),
                @LanguageString(value = "Entrée Enumeration", lang = "fr")
        ],
        resume = "A Enumeration input.",
        traducedResumes = [
                @LanguageString(value = "A Enumeration input.", lang = "en"),
                @LanguageString(value = "Une entrée Enumeration.", lang = "fr")
        ],
        keywords = ["input"],
        traducedKeywords = [
                @Keyword(traducedKeywords = [
                        @LanguageString(value = "input", lang = "en"),
                        @LanguageString(value = "entrée", lang = "fr")
                ])
        ],
        multiSelection = true,
        isEditable = true,
        values = ["value1", "value2"],
        selectedValues = ["value2"],
        names = ["name1", "name2"],
        minOccurs = 0,
        maxOccurs = 2,
        identifier = "orbisgis:test:enumerationLongProcess:input",
        metadata = [
                @MetadataAttribute(title = "metadata", role = "website", href = "http://orbisgis.org/")
        ]
        )
String[] inputEnumeration

/*****************/
/** OUTPUT Data **/
/*****************/

/** This Enumeration is the output data source. */
@EnumerationOutput(
        title="Output Enumeration",
        traducedTitles = [
                @LanguageString(value = "Output Enumeration", lang = "en"),
                @LanguageString(value = "Sortie Enumeration", lang = "fr")
        ],
        resume="A Enumeration output",
        traducedResumes = [
                @LanguageString(value = "A Enumeration output.", lang = "en"),
                @LanguageString(value = "Une sortie Enumeration.", lang = "fr")
        ],
        keywords = ["output"],
        traducedKeywords = [
                @Keyword(traducedKeywords = [
                        @LanguageString(value = "output", lang = "en"),
                        @LanguageString(value = "sortie", lang = "fr")
                ])
        ],
        values = ["value1", "value2"],
        identifier = "orbisgis:test:enumerationLongProcess:output",
        metadata = [
                @MetadataAttribute(title = "metadata", role = "website", href = "http://orbisgis.org/")
        ]
)
String[] enumerationOutput

