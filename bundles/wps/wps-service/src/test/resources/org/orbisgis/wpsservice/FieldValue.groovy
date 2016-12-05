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
@Process(title = ["FieldValue test","en","Test du FieldValue","fr"],
        description = ["Test script using the FieldValue ComplexData.","en",
                "Scripts test pour l'usage du ComplexData FieldValue.","fr"],
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
        title = ["Input FieldValue","en","Entrée FieldValue","fr"],
        description = ["A FieldValue input.","en","Une entrée FieldValue.","fr"],
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
        title = ["Output FieldValue","en","Sortie FieldValue","fr"],
        description = ["A FieldValue output.","en","Une sortie FieldValue.","fr"],
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

