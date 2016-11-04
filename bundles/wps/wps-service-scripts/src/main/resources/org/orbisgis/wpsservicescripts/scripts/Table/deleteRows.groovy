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
 * This process removes the given rows from the given table.
 * The user has to specify (mandatory):
 *  - The input table (DataStore)
 *  - The primary key field (DataField)
 *  - The primary keys of the rows to remove (FieldValue)
 *
 * @author Sylvain PALOMINOS
 */
@Process(
        translatedTitles = [
                @LanguageString(value = "Delete rows", lang = "en"),
                @LanguageString(value = "Suppression de lignes", lang = "fr")
        ],
        translatedResumes = [
                @LanguageString(value = "Delete rows from a table.", lang = "en"),
                @LanguageString(value = "Supprime des lignes d'une table.", lang = "fr")
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
        ])
def processing() {
    //Build the start of the query
    for (String s : pkToRemove) {
        String query = "DELETE FROM " + tableName + " WHERE " + pkField[0] + " = " + Long.parseLong(s)
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
        ])
String tableName

/**********************/
/** INPUT Parameters **/
/**********************/

/** Name of the PrimaryKey field of the DataStore tableName. */
@DataFieldInput(
        translatedTitles = [
                @LanguageString(value = "PKField", lang = "en"),
                @LanguageString(value = "Champ clef primaire", lang = "fr")
        ],
        translatedResumes = [
                @LanguageString(value = "The primary key field.", lang = "en"),
                @LanguageString(value = "Le champ de la clef primaire.", lang = "fr")
        ],
        variableReference = "tableName")
String[] pkField

/** List of primary keys to remove from the table. */
@FieldValueInput(
        translatedTitles = [
                @LanguageString(value = "PKArray", lang = "en"),
                @LanguageString(value = "Liste clef primaire", lang = "fr")
        ],
        translatedResumes = [
                @LanguageString(value = "The array of the primary keys of the rows to remove.", lang = "en"),
                @LanguageString(value = "La liste des clefs primaires dont les lignes sont à supprimer.", lang = "fr")
        ],
        variableReference = "pkField",
        multiSelection = true)
String[] pkToRemove

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

