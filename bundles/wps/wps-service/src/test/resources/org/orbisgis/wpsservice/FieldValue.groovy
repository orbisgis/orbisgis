package org.orbisgis.wpsservice

import org.orbisgis.wpsgroovyapi.attributes.TranslatableString
import org.orbisgis.wpsgroovyapi.attributes.LanguageString
import org.orbisgis.wpsgroovyapi.attributes.MetadataAttribute
import org.orbisgis.wpsgroovyapi.input.DataFieldInput
import org.orbisgis.wpsgroovyapi.input.FieldValueInput
import org.orbisgis.wpsgroovyapi.input.DataStoreInput
import org.orbisgis.wpsgroovyapi.output.DataFieldOutput
import org.orbisgis.wpsgroovyapi.output.FieldValueOutput
import org.orbisgis.wpsgroovyapi.output.DataStoreOutput
import org.orbisgis.wpsgroovyapi.process.Process

/********************/
/** Process method **/
/********************/

/**
 * Test script for the FieldValue
 * @author Sylvain PALOMINOS
 */
@Process(title = "FieldValueTest",
        translatedTitles = [
                @LanguageString(value = "FieldValue test", lang = "en"),
                @LanguageString(value = "Test du FieldValue", lang = "fr")
        ],
        resume = "Test script using the FieldValue ComplexData.",
        translatedResumes = [
                @LanguageString(value = "Test script using the FieldValue ComplexData.", lang = "en"),
                @LanguageString(value = "Scripts test pour l'usage du ComplexData FieldValue.", lang = "fr")
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
        identifier = "orbisgis:test:fieldvalue",
        metadata = [
                @MetadataAttribute(title = "metadata", role = "website", href = "http://orbisgis.org/")
        ]
)
def processing() {
    fieldValueOutput = inputFieldValue;
}


/****************/
/** INPUT Data **/
/****************/

@DataStoreInput(title = "DataStore for the FieldValue",
        identifier = "orbisgis:test:datastore:input")
String dataStoreInput

@DataFieldInput(title = "DataField for the FieldValue",
        identifier = "orbisgis:test:datafield:input",
        variableReference = "orbisgis:test:datastore:input")
String dataFieldInput

/** This FieldValue is the input data source. */
@FieldValueInput(
        title = "Input FieldValue",
        translatedTitles = [
                @LanguageString(value = "Input FieldValue", lang = "en"),
                @LanguageString(value = "Entrée FieldValue", lang = "fr")
        ],
        resume = "A FieldValue input.",
        translatedResumes = [
                @LanguageString(value = "A FieldValue input.", lang = "en"),
                @LanguageString(value = "Une entrée FieldValue.", lang = "fr")
        ],
        keywords = ["input"],
        translatedKeywords = [
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "input", lang = "en"),
                        @LanguageString(value = "entrée", lang = "fr")
                ])
        ],
        variableReference = "orbisgis:test:datafield:input",
        minOccurs = 0,
        maxOccurs = 2,
        identifier = "orbisgis:test:fieldvalue:input",
        metadata = [
                @MetadataAttribute(title = "metadata", role = "website", href = "http://orbisgis.org/")
        ]
        )
String inputFieldValue

/*****************/
/** OUTPUT Data **/
/*****************/

@DataStoreOutput(title = "DataStore for the FieldValue",
        identifier = "orbisgis:test:datastore:output")
String dataStoreOutput

@DataFieldOutput(title = "DataField for the FieldValue",
        identifier = "orbisgis:test:datafield:output",
        variableReference = "orbisgis:test:datastore:output")
String dataFieldOutput

/** This FieldValue is the output data source. */
@FieldValueOutput(
        title="Output FieldValue",
        translatedTitles = [
                @LanguageString(value = "Output FieldValue", lang = "en"),
                @LanguageString(value = "Sortie FieldValue", lang = "fr")
        ],
        resume="A FieldValue output",
        translatedResumes = [
                @LanguageString(value = "A FieldValue output.", lang = "en"),
                @LanguageString(value = "Une sortie FieldValue.", lang = "fr")
        ],
        keywords = ["output"],
        translatedKeywords = [
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "output", lang = "en"),
                        @LanguageString(value = "sortie", lang = "fr")
                ])
        ],
        variableReference = "orbisgis:test:datafield:output",
        multiSelection = true,
        identifier = "orbisgis:test:fieldvalue:output",
        metadata = [
                @MetadataAttribute(title = "metadata", role = "website", href = "http://orbisgis.org/")
        ]
)
String fieldValueOutput

