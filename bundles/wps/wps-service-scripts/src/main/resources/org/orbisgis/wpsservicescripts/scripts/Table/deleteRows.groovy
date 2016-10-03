package org.orbisgis.wpsservicescripts.scripts.Table

import org.orbisgis.wpsgroovyapi.attributes.Keyword
import org.orbisgis.wpsgroovyapi.attributes.LanguageString
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
        traducedTitles = [
                @LanguageString(value = "Delete rows", lang = "en"),
                @LanguageString(value = "Suppression de lignes", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "Delete rows from a table.", lang = "en"),
                @LanguageString(value = "Supprime des lignes d'une table.", lang = "fr")
        ],
        traducedKeywords = [
                @Keyword(traducedKeywords = [
                        @LanguageString(value = "Table", lang = "en"),
                        @LanguageString(value = "Table", lang = "fr")
                ]),
                @Keyword(traducedKeywords = [
                        @LanguageString(value = "Delete", lang = "en"),
                        @LanguageString(value = "Suppression", lang = "fr")
                ])
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
        traducedTitles = [
                @LanguageString(value = "Table", lang = "en"),
                @LanguageString(value = "Table", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "The table to edit.", lang = "en"),
                @LanguageString(value = "La table à éditer.", lang = "fr")
        ])
String tableName

/**********************/
/** INPUT Parameters **/
/**********************/

/** Name of the PrimaryKey field of the DataStore tableName. */
@DataFieldInput(
        traducedTitles = [
                @LanguageString(value = "PKField", lang = "en"),
                @LanguageString(value = "Champ clef primaire", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "The primary key field.", lang = "en"),
                @LanguageString(value = "Le champ de la clef primaire.", lang = "fr")
        ],
        dataStoreFieldName = "tableName")
String[] pkField

/** List of primary keys to remove from the table. */
@FieldValueInput(
        traducedTitles = [
                @LanguageString(value = "PKArray", lang = "en"),
                @LanguageString(value = "Liste clef primaire", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "The array of the primary keys of the rows to remove.", lang = "en"),
                @LanguageString(value = "La liste des clefs primaires dont les lignes sont à supprimer.", lang = "fr")
        ],
        dataFieldFieldName = "pkField",
        multiSelection = true)
String[] pkToRemove

/** Output message. */
@LiteralDataOutput(
        traducedTitles = [
                @LanguageString(value = "Output message", lang = "en"),
                @LanguageString(value = "Message de sortie", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "The output message.", lang = "en"),
                @LanguageString(value = "Le message de sortie.", lang = "fr")
        ])
String literalOutput

