package org.orbisgis.wpsservice

import org.orbisgis.wpsgroovyapi.attributes.TranslatableString
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
@Process(title = ["Enumeration test","en","Test du Enumeration","fr"],
        resume = "Test script using the Enumeration ComplexData.",
        translatedResumes = [
                @LanguageString(value = "Test script using the Enumeration ComplexData.", lang = "en"),
                @LanguageString(value = "Scripts test pour l'usage du ComplexData Enumeration.", lang = "fr")
        ],
        keywords = ["test", "script", "wps"],
        translatedKeywords = [
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "test", lang = "en"),
                        @LanguageString(value = "test", lang = "fr")
                ]),
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "script", lang = "en"),
                        @LanguageString(value = "scripte", lang = "fr")
                ]),
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "wps", lang = "en"),
                        @LanguageString(value = "wps", lang = "fr")
                ])
        ],
        identifier = "orbisgis:test:enumeration",
        metadata = [
                @MetadataAttribute(title = "metadata", role = "website", href = "http://orbisgis.org/")
        ]
)
def processing() {
    sleep(500)
    enumerationOutput = inputEnumeration;
}


/****************/
/** INPUT Data **/
/****************/

/** This Enumeration is the input data source. */
@EnumerationInput(
        title = ["Input Enumeration","en","Entrée Enumeration","fr"],
        resume = "A Enumeration input.",
        translatedResumes = [
                @LanguageString(value = "A Enumeration input.", lang = "en"),
                @LanguageString(value = "Une entrée Enumeration.", lang = "fr")
        ],
        keywords = ["input"],
        translatedKeywords = [
                @TranslatableString(translatableStrings = [
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
        identifier = "orbisgis:test:enumeration:input",
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
        title = ["Output Enumeration","en","Sortie Enumeration","fr"],
        resume="A Enumeration output",
        translatedResumes = [
                @LanguageString(value = "A Enumeration output.", lang = "en"),
                @LanguageString(value = "Une sortie Enumeration.", lang = "fr")
        ],
        keywords = ["output"],
        translatedKeywords = [
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "output", lang = "en"),
                        @LanguageString(value = "sortie", lang = "fr")
                ])
        ],
        values = ["value1", "value2"],
        identifier = "orbisgis:test:enumeration:output",
        metadata = [
                @MetadataAttribute(title = "metadata", role = "website", href = "http://orbisgis.org/")
        ]
)
String[] enumerationOutput

