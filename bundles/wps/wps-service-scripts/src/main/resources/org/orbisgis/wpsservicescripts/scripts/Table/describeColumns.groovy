package org.orbisgis.wpsservicescripts.scripts.Table

import org.orbisgis.wpsgroovyapi.input.DataFieldInput
import org.orbisgis.wpsgroovyapi.input.DataStoreInput
import org.orbisgis.wpsgroovyapi.input.FieldValueInput
import org.orbisgis.wpsgroovyapi.input.LiteralDataInput
import org.orbisgis.wpsgroovyapi.output.LiteralDataOutput
import org.orbisgis.wpsgroovyapi.process.Process

/**
 * This process is used to describe the columns of a table
 * 
 *
 * @author Erwan Bocher
 */
@Process(title = "Describe columns",
        resume = "Extract the name, type and comment from all fields of a table.",
        keywords = ["Table","Describe"])
def processing() {
    
    literalOutput = "No descriptions have been extracted."
    
    
    if(isH2){
        String query =  "CREATE TABLE " + outputTableName +" as SELECT COLUMN_NAME as col_name, TYPE_NAME as col_type,  REMARKS as col_comment from INFORMATION_SCHEMA.COLUMNS where table_name = '"+ tableName+"';"
        sql.execute(query);
        literalOutput = "The descriptions have been extracted."
    }
    else{
        String query =   "CREATE TABLE " + outputTableName +" as SELECT cols.column_name as col_name,cols.udt_name as col_type, pg_catalog.col_description(c.oid, cols.ordinal_position::int) as col_comment FROM pg_catalog.pg_class c, information_schema.columns cols WHERE cols.table_name = '"+tableName +"'AND cols.table_name = c.relname "
        sql.execute(query);
        literalOutput = "The descriptions have been extracted."
    } 
    
}

/****************/
/** INPUT Data **/
/****************/

/** This DataStore is the input data source table. */
@DataStoreInput(
        title = "Table",
        resume = "Extract name, type and comments from the selected table.")
String tableName

@LiteralDataInput(
		title="Output table name",
		resume="Name of the table containing the descriptions.")
String outputTableName


/** Output message. */
@LiteralDataOutput(
        title="Output message",
        resume="The output message")
String literalOutput

