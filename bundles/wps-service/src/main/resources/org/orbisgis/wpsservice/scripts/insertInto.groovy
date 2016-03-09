//TODO : header

package org.orbisgis.wpsservice.scripts

import org.orbisgis.wpsgroovyapi.input.DataFieldInput
import org.orbisgis.wpsgroovyapi.input.DataStoreInput
import org.orbisgis.wpsgroovyapi.input.EnumerationInput
import org.orbisgis.wpsgroovyapi.input.FieldValueInput
import org.orbisgis.wpsgroovyapi.input.LiteralDataInput
import org.orbisgis.wpsgroovyapi.output.DataStoreOutput
import org.orbisgis.wpsgroovyapi.output.LiteralDataOutput
import org.orbisgis.wpsgroovyapi.process.Process
import org.orbisgis.wpsservice.model.LiteralData

/********************/
/** Process method **/
/********************/

/**
 * This process insert the given value in the given table.
 * The user has to specify (mandatory):
 *  - The input table (DataStore)
 *  - The primary keys of the rows to remove (LiteralData)
 *
 * @author Sylvain PALOMINOS
 */
@Process(title = "InsertInto",
        resume = "Insert values into a table.",
        keywords = "OrbisGIS,table_editor")
def processing() {
    //Build the start of the query
    String queryBase = "INSERT INTO " + tableName + "(" + fields + ") VALUES ("
    String[] rowArray = values.split(";")
    for(String row : rowArray){
        String query = queryBase
        String[] valueArray = row.split(",")
        String formatedValues = ""
        for(String value : valueArray){
            if(formatedValues.isEmpty()){
                formatedValues += "'" + value + "'";
            }
            else{
                formatedValues += ",'" + value + "'";
            }
        }
        query += formatedValues + ");"
        print query
        sql.execute(query)
    }
    literalOutput = "Insert done."
}


/****************/
/** INPUT Data **/
/****************/

/** This DataStore is the input data source table. */
@DataStoreInput(
        title = "Table",
        resume = "The table to edit",
        extensions = ["geocatalog"])
String tableName

/**********************/
/** INPUT Parameters **/
/**********************/

/** Field list concerned by the value insertion. */
@DataFieldInput(
        title = "Fields",
        resume = "The field concerned by the value insertion",
        dataStore = "tableName",
        isMultipleField = true)
String fields

/** This DataStore is the output data source for the buffer. */
@LiteralDataInput(
        title="Values",
        resume="The input values. The values should be separated by a ',' and rows by ';'")
String values

/** This DataStore is the output data source for the buffer. */
@LiteralDataOutput(
        title="Output message",
        resume="The output message")
String literalOutput

