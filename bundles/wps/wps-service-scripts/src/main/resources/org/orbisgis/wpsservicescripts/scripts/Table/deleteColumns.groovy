package org.orbisgis.wpsservicescripts.scripts.Table

import org.orbisgis.wpsgroovyapi.attributes.TranslatableString
import org.orbisgis.wpsgroovyapi.attributes.LanguageString
import org.orbisgis.wpsgroovyapi.attributes.MetadataAttribute
import org.orbisgis.wpsgroovyapi.input.DataFieldInput
import org.orbisgis.wpsgroovyapi.input.DataStoreInput
import org.orbisgis.wpsgroovyapi.input.FieldValueInput
import org.orbisgis.wpsgroovyapi.output.LiteralDataOutput
import org.orbisgis.wpsgroovyapi.process.Process

/********************/
/** Process method **/
/********************/

/**
 * This process deletes the given columns from the given table.
 * The user has to specify (mandatory):
 *  - The input table (DataStore)
 *  - The column to delete (DataField)
 *
 * @author Sylvain PALOMINOS
 */
@Process(
        translatedTitles = [
                @LanguageString(value = "Delete columns", lang = "en"),
                @LanguageString(value = "Suppression de colonnes", lang = "fr")
        ],
        translatedResumes = [
                @LanguageString(value = "Delete columns from a table.", lang = "en"),
                @LanguageString(value = "Supprime des colonnes d'une table.", lang = "fr")
        ],
        translatedKeywords = [
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "Table", lang = "en"),
                        @LanguageString(value = "Table", lang = "fr")
                ]),
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "Delete", lang = "en"),
                        @LanguageString(value = "Suppression", lang = "fr")
                ])
        ],
        metadata = [
                @MetadataAttribute(title="h2gis", role ="DBMS", href = "http://www.h2gis.org/"),
                @MetadataAttribute(title="postgis", role ="DBMS", href = "http://postgis.net/")
        ],
        identifier = "wps:orbisgis:internal:DeleteColumns"
)
def processing() {
    //Build the start of the query
    for (String columnName : columnNames) {
        String query = String.format("ALTER TABLE %s DROP COLUMN `%s`", tableName, columnName)
        //Execute the query
        sql.execute(query)
    }
    literalOutput = "Delete done."
}


/****************/
/** INPUT Data **/
/****************/

/** This DataStore is the input data source table. */
@DataStoreInput(
        translatedTitles = [
                @LanguageString(value = "Table", lang = "en"),
                @LanguageString(value = "Table", lang = "fr")
        ],
        translatedResumes = [
                @LanguageString(value = "The table to edit.", lang = "en"),
                @LanguageString(value = "La table à éditer.", lang = "fr")
        ],
        identifier = "wps:orbisgis:internal:DeleteColumns:Table"
)
String tableName

/**********************/
/** INPUT Parameters **/
/**********************/

/** Name of the columns of the DataStore tableName to remove. */
@DataFieldInput(
        translatedTitles = [
                @LanguageString(value = "Columns", lang = "en"),
                @LanguageString(value = "Colonnes", lang = "fr")
        ],
        translatedResumes = [
                @LanguageString(value = "The columns to remove names.", lang = "en"),
                @LanguageString(value = "Le nom des colonnes à supprimer.", lang = "fr")
        ],
        variableReference = "tableName",
        identifier = "wps:orbisgis:internal:DeleteColumns:Columns"
)
String[] columnNames


/** Output message. */
@LiteralDataOutput(
        translatedTitles = [
                @LanguageString(value = "Output message", lang = "en"),
                @LanguageString(value = "Message de sortie", lang = "fr")
        ],
        translatedResumes = [
                @LanguageString(value = "The output message.", lang = "en"),
                @LanguageString(value = "Le message de sortie.", lang = "fr")
        ])
String literalOutput

