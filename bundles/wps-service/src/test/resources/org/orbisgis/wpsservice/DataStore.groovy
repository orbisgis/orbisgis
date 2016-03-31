package org.orbisgis.wpsservice

import org.orbisgis.wpsgroovyapi.attributes.Keyword
import org.orbisgis.wpsgroovyapi.attributes.LanguageString
import org.orbisgis.wpsgroovyapi.attributes.MetadataAttribute
import org.orbisgis.wpsgroovyapi.input.DataStoreInput
import org.orbisgis.wpsgroovyapi.output.DataStoreOutput
import org.orbisgis.wpsgroovyapi.process.Process

/********************/
/** Process method **/
/********************/

/**
 * Test script for the DataStore
 * @author Sylvain PALOMINOS
 */
@Process(title = "DataStoreTest",
        traducedTitles = [
                @LanguageString(value = "DataStore test", lang = "en"),
                @LanguageString(value = "Test du DataStore", lang = "fr")
        ],
        resume = "Test script using the DataStore ComplexData.",
        traducedResumes = [
                @LanguageString(value = "Test script using the DataStore ComplexData.", lang = "en"),
                @LanguageString(value = "Scripts test pour l'usage du ComplexData DataStore.", lang = "fr")
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
        identifier = "orbisgis:test:datastore",
        metadata = [
                @MetadataAttribute(title = "metadata", role = "website", href = "http://orbisgis.org/")
        ]
)
def processing() {
    dataStoreOutput = inputDataStore;
}


/****************/
/** INPUT Data **/
/****************/

/** This DataStore is the input data source. */
@DataStoreInput(
        title = "Input DataStore",
        traducedTitles = [
                @LanguageString(value = "Input DataStore", lang = "en"),
                @LanguageString(value = "Entrée DataStore", lang = "fr")
        ],
        resume = "A DataStore input.",
        traducedResumes = [
                @LanguageString(value = "A DataStore input.", lang = "en"),
                @LanguageString(value = "Une entrée DataStore.", lang = "fr")
        ],
        keywords = ["input"],
        traducedKeywords = [
                @Keyword(traducedKeywords = [
                        @LanguageString(value = "input", lang = "en"),
                        @LanguageString(value = "entrée", lang = "fr")
                ])
        ],
        isSpatial = true,
        isCreateTable = false,
        extensions = ["geocatalog", "shp", "dbf"],
        minOccurs = 0,
        maxOccurs = 2,
        identifier = "orbisgis:test:datastore:input",
        metadata = [
                @MetadataAttribute(title = "metadata", role = "website", href = "http://orbisgis.org/")
        ]
        )
String inputDataStore

/*****************/
/** OUTPUT Data **/
/*****************/

/** This DataStore is the output data source. */
@DataStoreOutput(
        title="Output DataStore",
        traducedTitles = [
                @LanguageString(value = "Output DataStore", lang = "en"),
                @LanguageString(value = "Sortie DataStore", lang = "fr")
        ],
        resume="A DataStore output",
        traducedResumes = [
                @LanguageString(value = "A DataStore output.", lang = "en"),
                @LanguageString(value = "Une sortie DataStore.", lang = "fr")
        ],
        keywords = ["output"],
        traducedKeywords = [
                @Keyword(traducedKeywords = [
                        @LanguageString(value = "output", lang = "en"),
                        @LanguageString(value = "sortie", lang = "fr")
                ])
        ],
        isSpatial = false,
        isCreateTable = true,
        extensions = ["geocatalog", "shp", "dbf"],
        identifier = "orbisgis:test:datastore:output",
        metadata = [
                @MetadataAttribute(title = "metadata", role = "website", href = "http://orbisgis.org/")
        ]
)
String dataStoreOutput

