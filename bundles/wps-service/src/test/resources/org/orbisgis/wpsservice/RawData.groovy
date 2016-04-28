package org.orbisgis.wpsservice

import org.orbisgis.wpsgroovyapi.attributes.Keyword
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
@Process(title = "RawDataTest",
        traducedTitles = [
                @LanguageString(value = "RawData test", lang = "en"),
                @LanguageString(value = "Test du RawData", lang = "fr")
        ],
        resume = "Test script using the RawData ComplexData.",
        traducedResumes = [
                @LanguageString(value = "Test script using the RawData ComplexData.", lang = "en"),
                @LanguageString(value = "Scripts test pour l'usage du ComplexData RawData.", lang = "fr")
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
        title = "Input RawData",
        traducedTitles = [
                @LanguageString(value = "Input RawData", lang = "en"),
                @LanguageString(value = "Entrée RawData", lang = "fr")
        ],
        resume = "A RawData input.",
        traducedResumes = [
                @LanguageString(value = "A RawData input.", lang = "en"),
                @LanguageString(value = "Une entrée RawData.", lang = "fr")
        ],
        keywords = ["input"],
        traducedKeywords = [
                @Keyword(traducedKeywords = [
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
        title="Output RawData",
        traducedTitles = [
                @LanguageString(value = "Output RawData", lang = "en"),
                @LanguageString(value = "Sortie RawData", lang = "fr")
        ],
        resume="A RawData output",
        traducedResumes = [
                @LanguageString(value = "A RawData output.", lang = "en"),
                @LanguageString(value = "Une sortie RawData.", lang = "fr")
        ],
        keywords = ["output"],
        traducedKeywords = [
                @Keyword(traducedKeywords = [
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

