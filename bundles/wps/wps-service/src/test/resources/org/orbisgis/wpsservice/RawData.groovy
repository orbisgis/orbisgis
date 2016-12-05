package org.orbisgis.wpsservice

import org.orbisgis.wpsgroovyapi.attributes.TranslatableString
import org.orbisgis.wpsgroovyapi.attributes.LanguageString
import org.orbisgis.wpsgroovyapi.attributes.MetadataAttribute
import org.orbisgis.wpsgroovyapi.input.RawDataInput
import org.orbisgis.wpsgroovyapi.output.RawDataOutput
import org.orbisgis.wpsgroovyapi.process.Process

/********************/
/** Process method **/
/********************/

/**
 * Test script for the RawData
 * @author Sylvain PALOMINOS
 */
@Process(title = ["RawData test","en","Test du RawData","fr"],
        resume = "Test script using the RawData ComplexData.",
        translatedResumes = [
                @LanguageString(value = "Test script using the RawData ComplexData.", lang = "en"),
                @LanguageString(value = "Scripts test pour l'usage du ComplexData RawData.", lang = "fr")
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
        identifier = "orbisgis:test:rawdata",
        metadata = [
                @MetadataAttribute(title = "metadata", role = "website", href = "http://orbisgis.org/")
        ]
)
def processing() {
        rawDataOutput = inputRawData;
}


/****************/
/** INPUT Data **/
/****************/

/** This RawData is the input data source. */
@RawDataInput(
        title = ["Input RawData","en","Entrée RawData","fr"],
        resume = "A RawData input.",
        translatedResumes = [
                @LanguageString(value = "A RawData input.", lang = "en"),
                @LanguageString(value = "Une entrée RawData.", lang = "fr")
        ],
        keywords = ["input"],
        translatedKeywords = [
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "input", lang = "en"),
                        @LanguageString(value = "entrée", lang = "fr")
                ])
        ],
        isDirectory = false,
        minOccurs = 0,
        maxOccurs = 2,
        identifier = "orbisgis:test:rawdata:input",
        metadata = [
                @MetadataAttribute(title = "metadata", role = "website", href = "http://orbisgis.org/")
        ]
        )
String inputRawData

/*****************/
/** OUTPUT Data **/
/*****************/

/** This RawData is the output data source. */
@RawDataOutput(
        title = ["Output RawData","en","Sortie RawData","fr"],
        resume="A RawData output",
        translatedResumes = [
                @LanguageString(value = "A RawData output.", lang = "en"),
                @LanguageString(value = "Une sortie RawData.", lang = "fr")
        ],
        keywords = ["output"],
        translatedKeywords = [
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "output", lang = "en"),
                        @LanguageString(value = "sortie", lang = "fr")
                ])
        ],
        isFile = false,
        multiSelection = true,
        identifier = "orbisgis:test:rawdata:output",
        metadata = [
                @MetadataAttribute(title = "metadata", role = "website", href = "http://orbisgis.org/")
        ]
)
String rawDataOutput

