package org.orbisgis.wpsservicescripts.scripts.Table

import org.orbisgis.wpsgroovyapi.input.JDBCTableFieldInput
import org.orbisgis.wpsgroovyapi.input.JDBCTableInput
import org.orbisgis.wpsgroovyapi.input.JDBCTableFieldValueInput
import org.orbisgis.wpsgroovyapi.output.LiteralDataOutput
import org.orbisgis.wpsgroovyapi.process.Process
/********************/
/** Process method **/
/********************/

/**
 * This process removes the given rows from the given table.
 * The user has to specify (mandatory):
 *  - The input table (JDBCTable)
 *  - The primary key field (JDBCTableField)
 *  - The primary keys of the rows to remove (JDBCTableFieldValue)
 *
 * @author Sylvain PALOMINOS
 */
@Process(
        title = ["Delete rows","en",
                "Suppression de lignes","fr"],
        description = ["Delete rows from a table.","en",
                "Supprime des lignes d'une table.","fr"],
        keywords = ["Table,Delete", "en",
                "Table,Suppression", "fr"],
        properties = ["DBMS_TYPE", "H2GIS",
                "DBMS_TYPE", "POSTGIS"],
        identifier = "orbisgis:wps:official:deleteRows"
)
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

/** This JDBCTable is the input data source table. */
@JDBCTableInput(
        title = ["Table","en",
                "Table","fr"],
        description = ["The table to edit.","en",
                "La table à éditer.","fr"],
        identifier = "orbisgis:wps:official:deleteRows:tableName"
)
String tableName

/**********************/
/** INPUT Parameters **/
/**********************/

/** Name of the PrimaryKey field of the JDBCTable tableName. */
@JDBCTableFieldInput(
        title = ["PKField","en",
                "Champ clef primaire","fr"],
        description = ["The primary key field.","en",
                "Le champ de la clef primaire.","fr"],
        jdbcTableReference = "orbisgis:wps:official:deleteRows:tableName",
        identifier = "orbisgis:wps:official:deleteRows:pkField"
)
String[] pkField

/** List of primary keys to remove from the table. */
@JDBCTableFieldValueInput(
        title = ["PKArray","en",
                "Liste clef primaire","fr"],
        description = ["The array of the primary keys of the rows to remove.","en",
                "La liste des clefs primaires dont les lignes sont à supprimer.","fr"],
        jdbcTableFieldReference = "orbisgis:wps:official:deleteRows:pkField",
        multiSelection = true,
        identifier = "orbisgis:wps:official:deleteRows:pkToRemove"
)
String[] pkToRemove

/** Output message. */
@LiteralDataOutput(
        title = ["Output message","en",
                "Message de sortie","fr"],
        description = ["The output message.","en",
                "Le message de sortie.","fr"],
        identifier = "orbisgis:wps:official:deleteRows:literalOutput")
String literalOutput

