package org.orbisgis.wpsservicescripts.scripts.Table

import org.orbisgis.wpsgroovyapi.input.JDBCTableFieldInput
import org.orbisgis.wpsgroovyapi.input.JDBCTableInput
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
        title = ["Delete columns","en","Suppression de colonnes","fr"],
        description = ["Delete columns from a table.","en",
                "Supprime des colonnes d'une table.","fr"],
        keywords = ["Table,Delete","en",
                "Table,Suppression","fr"],
        properties = ["DBMS_TYPE", "H2GIS",
                "DBMS_TYPE", "POSTGIS"],
        identifier = "orbisgis:wps:official:deleteColumns"
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
@JDBCTableInput(
        title = ["Table","en","Table","fr"],
        description = ["The table to edit.","en","La table à éditer.","fr"],
        identifier = "orbisgis:wps:official:deleteColumns:tableName"
)
String tableName

/**********************/
/** INPUT Parameters **/
/**********************/

/** Name of the columns of the DataStore tableName to remove. */
@JDBCTableFieldInput(
        title = ["Columns","en","Colonnes","fr"],
        description = ["The columns to remove names.","en",
                "Le nom des colonnes à supprimer.","fr"],
        jdbcTableReference = "orbisgis:wps:official:deleteColumns:tableName",
        identifier = "orbisgis:wps:official:deleteColumns:columnNames"
)
String[] columnNames


/** Output message. */
@LiteralDataOutput(
        title = ["Output message","en",
                "Message de sortie","fr"],
        description = ["The output message.","en",
                "Le message de sortie.","fr"],
        identifier = "orbisgis:wps:official:deleteColumns:literalOutput")
String literalOutput

