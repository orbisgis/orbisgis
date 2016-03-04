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
 * This process removes the given row from the given table.
 * The user has to specify (mandatory):
 *  - The input table (DataStore)
 *  - The primary keys of the rows to remove (LiteralData)
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
        print query
        //Execute the query
        sql.execute(query)
    }
    dataStoreOutput = "Remove done."
}


/****************/
/** INPUT Data **/
/****************/

/** This DataStore is the input data source for the buffer. */
@DataStoreInput(
        title = "Table",
        resume = "The table to edit",
        isSpatial = true)
String tableName

/**********************/
/** INPUT Parameters **/
/**********************/

/** Name of the Geometric field of the DataStore inputDataStore. */
@DataFieldInput(
        title = "PKField",
        resume = "The primary key field",
        dataStore = "tableName")
String pkField

/** Name of the Geometric field of the DataStore inputDataStore. */
@FieldValueInput(
        title = "PKArray",
        resume = "The array of the primary keys of the rows to remove",
        dataField = "pkField",
        multiSelection = true)
String[] pkToRemove

/** This DataStore is the output data source for the buffer. */
@LiteralDataOutput(
        title="Output message",
        resume="The output message")
String dataStoreOutput

