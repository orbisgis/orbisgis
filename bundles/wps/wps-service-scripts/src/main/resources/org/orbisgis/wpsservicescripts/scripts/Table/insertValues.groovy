package org.orbisgis.wpsservicescripts.scripts.Table

import org.orbisgis.wpsgroovyapi.attributes.Keyword
import org.orbisgis.wpsgroovyapi.attributes.LanguageString
import org.orbisgis.wpsgroovyapi.attributes.MetadataAttribute
import org.orbisgis.wpsgroovyapi.input.DataFieldInput
import org.orbisgis.wpsgroovyapi.input.DataStoreInput
import org.orbisgis.wpsgroovyapi.input.LiteralDataInput
import org.orbisgis.wpsgroovyapi.output.LiteralDataOutput
import org.orbisgis.wpsgroovyapi.process.Process

/********************/
/** Process method **/
/********************/

/**
 * This process insert the given values in the given table.
 * The user has to specify (mandatory):
 *  - The input table (DataStore)
 *  - The values to insert (LiteralData)
 *
 * The user can specify (optional) :
 *  - The field list concerned by the value insertion (DataField)
 *
 * @author Sylvain PALOMINOS
 */
@Process(
        traducedTitles = [
                @LanguageString(value = "Insert values", lang = "en"),
                @LanguageString(value = "Insertion de valeurs", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "Insert values into a table.", lang = "en"),
                @LanguageString(value = "Insert de valeurs dans une table.", lang = "fr")
        ],
        traducedKeywords = [
                @Keyword(traducedKeywords = [
                        @LanguageString(value = "Table", lang = "en"),
                        @LanguageString(value = "Table", lang = "fr")
                ]),
                @Keyword(traducedKeywords = [
                        @LanguageString(value = "Insert", lang = "en"),
                        @LanguageString(value = "Insertion", lang = "fr")
                ]),
                @Keyword(traducedKeywords = [
                        @LanguageString(value = "Values", lang = "en"),
                        @LanguageString(value = "Valeurs", lang = "fr")
                ])
        ],
        metadata = [
                @MetadataAttribute(title="h2gis", role ="DBMS", href = "http://www.h2gis.org/"),
                @MetadataAttribute(title="postgis", role ="DBMS", href = "http://postgis.net/")
        ])
def processing() {
    //Build the query
    String queryBase = "INSERT INTO " + tableName;
    queryBase += " (";
    String fieldsStr=""
    for(String field : fieldList) {
        if (field != null) {
            if(!fieldsStr.isEmpty()) {
                fieldsStr += ", ";
            }
            fieldsStr += field;
        }
    }
    queryBase += ") ";
    queryBase += " VALUES (";
    //execute the query for each row
    String[] rowArray = values.split(";")
    for(String row : rowArray){
        String query = queryBase
        String[] valueArray = row.split(",", -1)
        //Retrieve the values to insert
        String formatedValues = ""
        for(String value : valueArray){
            if(!formatedValues.isEmpty()){
                formatedValues += ",";
            }
            if(value.isEmpty()){
                formatedValues += "NULL"
            }
            else{
                formatedValues += "'" + value + "'";
            }
        }
        query += formatedValues + ");"
        //execute the query
        sql.execute(query)
    }
    literalOutput = "Insert done."
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

/** Field list concerned by the value insertion. */
@DataFieldInput(
        traducedTitles = [
                @LanguageString(value = "Fields", lang = "en"),
                @LanguageString(value = "Champs", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "The field concerned by the value insertion.", lang = "en"),
                @LanguageString(value = "Les champs concernés par les insertions de valeurs.", lang = "fr")
        ],
        variableReference = "tableName",
        multiSelection = true,
        minOccurs = 0)
String[] fieldList

/** Coma separated values to insert. */
@LiteralDataInput(
        traducedTitles = [
                @LanguageString(value = "Values", lang = "en"),
                @LanguageString(value = "Valeurs", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "The input values. The values should be separated by a ',' and rows by ';'", lang = "en"),
                @LanguageString(value = "Les valeurs à insérer. Elles doivent etre séparées par une ',' et les lignes par un ';'", lang = "fr")
        ])
String values

/** String output of the process. */
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

