//TODO : header

package org.orbisgis.wpsservice.scripts

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
@Process(title = "RemoveRow",
        resume = "Remove rows from a table.",
        keywords = "OrbisGIS,table_editor")
def processing() {
    //Build the start of the query
    for (String s : pkToRemove) {
        String query = "DELETE FROM " + tableName + " WHERE " + pkField + " = " + Long.parseLong(s)
        //Execute the query
        sql.execute(query)
    }
    literalOutput = "Remove done."
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

/** Name of the PrimaryKey field of the DataStore tableName. */
@DataFieldInput(
        title = "PKField",
        resume = "The primary key field",
        dataStore = "Table")
String pkField

/** List of primary keys to remove from the table. */
@FieldValueInput(
        title = "PKArray",
        resume = "The array of the primary keys of the rows to remove",
        dataField = "PKField",
        multiSelection = true)
String[] pkToRemove

/** Output message. */
@LiteralDataOutput(
        title="Output message",
        resume="The output message")
String literalOutput

