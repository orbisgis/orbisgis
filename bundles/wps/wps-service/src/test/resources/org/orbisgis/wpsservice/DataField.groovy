package org.orbisgis.wpsservice

import org.orbisgis.wpsgroovyapi.attributes.Keyword
import org.orbisgis.wpsgroovyapi.attributes.LanguageString
import org.orbisgis.wpsgroovyapi.attributes.MetadataAttribute
import org.orbisgis.wpsgroovyapi.input.DataFieldInput
import org.orbisgis.wpsgroovyapi.input.DataStoreInput
import org.orbisgis.wpsgroovyapi.output.DataFieldOutput
import org.orbisgis.wpsgroovyapi.output.DataStoreOutput
import org.orbisgis.wpsgroovyapi.process.Process

/********************/
/** Process method **/
/********************/

/**
 * Test script for the DataField
 * @author Sylvain PALOMINOS
 */
@Process(title = "DataFieldTest",
        traducedTitles = [
                @LanguageString(value = "DataField test", lang = "en"),
                @LanguageString(value = "Test du DataField", lang = "fr")
        ],
        resume = "Test script using the DataField ComplexData.",
        traducedResumes = [
                @LanguageString(value = "Test script using the DataField ComplexData.", lang = "en"),
                @LanguageString(value = "Scripts test pour l'usage du DataField DataField.", lang = "fr")
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
        identifier = "orbisgis:test:datafield",
        metadata = [
                @MetadataAttribute(title = "metadata", role = "website", href = "http://orbisgis.org/")
        ]
)
def processing() {
    dataFieldOutput = inputDataField;
}


/****************/
/** INPUT Data **/
/****************/

@DataStoreInput(title = "DataStore for the DataField",
        identifier = "orbisgis:test:datastore:input")
String dataStoreInput

/** This DataField is the input data source. */
@DataFieldInput(
        title = "Input DataField",
        traducedTitles = [
                @LanguageString(value = "Input DataField", lang = "en"),
                @LanguageString(value = "Entrée DataField", lang = "fr")
        ],
        resume = "A DataField input.",
        traducedResumes = [
                @LanguageString(value = "A DataField input.", lang = "en"),
                @LanguageString(value = "Une entrée DataField.", lang = "fr")
        ],
        keywords = ["input"],
        traducedKeywords = [
                @Keyword(traducedKeywords = [
                        @LanguageString(value = "input", lang = "en"),
                        @LanguageString(value = "entrée", lang = "fr")
                ])
        ],
        variableReference = "orbisgis:test:datastore:input",
        excludedTypes = ["BOOLEAN"],
        minOccurs = 0,
        maxOccurs = 2,
        identifier = "orbisgis:test:datafield:input",
        metadata = [
                @MetadataAttribute(title = "metadata", role = "website", href = "http://orbisgis.org/")
        ]
        )
String inputDataField

/*****************/
/** OUTPUT Data **/
/*****************/

@DataStoreOutput(title = "DataStore for the DataField",
        identifier = "orbisgis:test:datastore:output")
String dataStoreOutput

/** This DataField is the output data source. */
@DataFieldOutput(
        title="Output DataField",
        traducedTitles = [
                @LanguageString(value = "Output DataField", lang = "en"),
                @LanguageString(value = "Sortie DataField", lang = "fr")
        ],
        resume="A DataField output",
        traducedResumes = [
                @LanguageString(value = "A DataField output.", lang = "en"),
                @LanguageString(value = "Une sortie DataField.", lang = "fr")
        ],
        keywords = ["output"],
        traducedKeywords = [
                @Keyword(traducedKeywords = [
                        @LanguageString(value = "output", lang = "en"),
                        @LanguageString(value = "sortie", lang = "fr")
                ])
        ],
        variableReference = "orbisgis:test:datastore:output",
        fieldTypes = ["GEOMETRY", "NUMBER"],
        multiSelection = true,
        identifier = "orbisgis:test:datafield:output",
        metadata = [
                @MetadataAttribute(title = "metadata", role = "website", href = "http://orbisgis.org/")
        ]
)
String dataFieldOutput

