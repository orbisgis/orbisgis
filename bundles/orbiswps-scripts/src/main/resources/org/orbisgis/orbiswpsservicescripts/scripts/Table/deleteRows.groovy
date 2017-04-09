package org.orbisgis.orbiswpsservicescripts.scripts.Table

import org.orbisgis.orbiswpsgroovyapi.input.*
import org.orbisgis.orbiswpsgroovyapi.output.*
import org.orbisgis.orbiswpsgroovyapi.process.*

/********************/
/** Process method **/
/********************/

/**
 * This process removes the given rows from the given table.
 * The user has to specify (mandatory):
 *  - The input table 
 *  - The primary key field 
 *  - The primary keys of the rows to remove 
 *
 * @author Sylvain PALOMINOS
 * @author Erwan Bocher
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
        version = "1.0",
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
        identifier = "tableName"
)
String tableName

/**********************/
/** INPUT Parameters **/
/**********************/

/** Name of the PrimaryKey field of the JDBCTable tableName. */
@JDBCColumnInput(
        title = ["PKField","en",
                "Clef primaire","fr"],
        description = ["The primary key column.","en",
                "La colonne de la clef primaire.","fr"],
        jdbcTableReference = "tableName",
        identifier = "pkField"
)
String[] pkField

/** List of primary keys to remove from the table. */
@JDBCValueInput(
        title = ["Primary key values","en",
                "Valeurs des clefs primaires","fr"],
        description = ["The array of the primary keys of the rows to remove.","en",
                "La liste des clefs primaires dont les lignes sont à supprimer.","fr"],
        jdbcColumnReference = "pkField",
        multiSelection = true,
        identifier = "pkToRemove"
)
String[] pkToRemove

/** Output message. */
@LiteralDataOutput(
        title = ["Output message","en",
                "Message de sortie","fr"],
        description = ["The output message.","en",
                "Le message de sortie.","fr"],
        identifier = "literalOutput")
String literalOutput

