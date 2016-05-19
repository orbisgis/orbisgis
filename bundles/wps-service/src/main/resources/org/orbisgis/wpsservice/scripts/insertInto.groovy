//TODO : header

package org.orbisgis.wpsservice.scripts

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
@Process(title = "InsertInto",
        resume = "Insert values into a table.",
        keywords = "OrbisGIS,table_editor")
def processing() {
    //Build the query
    String queryBase = "INSERT INTO " + tableName;
    if(fields != null){
        queryBase += " (" + fields + ") ";
    }
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
        dataStore = "Table",
        isMultipleField = true,
        minOccurs = 0)
String fields

/** Coma separated values to insert. */
@LiteralDataInput(
        title="Values",
        resume="The input values. The values should be separated by a ',' and rows by ';'")
String values

/** String output of the process. */
@LiteralDataOutput(
        title="Output message",
        resume="The output message")
String literalOutput

