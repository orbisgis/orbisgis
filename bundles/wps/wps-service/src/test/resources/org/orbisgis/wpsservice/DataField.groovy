package org.orbisgis.wpsservice

import org.orbisgis.wpsgroovyapi.attributes.TranslatableString
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
@Process(title = ["DataField test","en","Test du DataField","fr"],
        resume = "Test script using the DataField ComplexData.",
        translatedResumes = [
                @LanguageString(value = "Test script using the DataField ComplexData.", lang = "en"),
                @LanguageString(value = "Scripts test pour l'usage du DataField DataField.", lang = "fr")
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
        title = ["Input DataField","en","Entrée DataField","fr"],
        resume = "A DataField input.",
        translatedResumes = [
                @LanguageString(value = "A DataField input.", lang = "en"),
                @LanguageString(value = "Une entrée DataField.", lang = "fr")
        ],
        keywords = ["input"],
        translatedKeywords = [
                @TranslatableString(translatableStrings = [
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
        title = ["Output DataField","en","Sortie DataField","fr"],
        resume="A DataField output",
        translatedResumes = [
                @LanguageString(value = "A DataField output.", lang = "en"),
                @LanguageString(value = "Une sortie DataField.", lang = "fr")
        ],
        keywords = ["output"],
        translatedKeywords = [
                @TranslatableString(translatableStrings = [
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

