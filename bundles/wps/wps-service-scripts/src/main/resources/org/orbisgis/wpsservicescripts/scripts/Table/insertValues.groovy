package org.orbisgis.wpsservicescripts.scripts.Table

import org.orbisgis.wpsgroovyapi.input.JDBCTableFieldInput
import org.orbisgis.wpsgroovyapi.input.JDBCTableInput
import org.orbisgis.wpsgroovyapi.input.LiteralDataInput
import org.orbisgis.wpsgroovyapi.output.LiteralDataOutput
import org.orbisgis.wpsgroovyapi.process.Process
/********************/
/** Process method **/
/********************/

/**
 * This process insert the given values in the given table.
 * The user has to specify (mandatory):
 *  - The input table (JDBCTable)
 *  - The values to insert (LiteralData)
 *
 * The user can specify (optional) :
 *  - The field list concerned by the value insertion (JDBCTableField)
 *
 * @author Sylvain PALOMINOS
 */
@Process(
        title = ["Insert values","en",
                "Insertion de valeurs","fr"],
        description = ["Insert values into a table.","en",
                "Insert de valeurs dans une table.","fr"],
        keywords = ["Table,Insert,Values", "en",
                "Table,Insertion,Valeurs", "fr"],
        properties = ["DBMS_TYPE", "H2GIS",
                "DBMS_TYPE", "POSTGIS"],
        identifier = "orbisgis:wps:official:insertValues")
def processing() {
    //Build the query
    String queryBase = "INSERT INTO " + tableName;
    if (fieldList != null) {
        queryBase += " (";
        String fieldsStr = ""
        for (String field : fieldList) {
            if (field != null) {
                if (!fieldsStr.isEmpty()) {
                    fieldsStr += ", ";
                }
                fieldsStr += field;
            }
        }
        queryBase += ") ";
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
@JDBCTableInput(
        title = ["Table","en",
                "Table","fr"],
        description = ["The table to edit.","en",
                "La table à éditer.","fr"],
        identifier = "orbisgis:wps:official:insertValues:tableName")
String tableName

/**********************/
/** INPUT Parameters **/
/**********************/

/** Field list concerned by the value insertion. */
@JDBCTableFieldInput(
        title = ["Fields","en",
                "Champs","fr"],
        description = [
                "The field concerned by the value insertion.","en",
                "Les champs concernés par les insertions de valeurs.","fr"],
        jdbcTableReference = "orbisgis:wps:official:insertValues:tableName",
        multiSelection = true,
        minOccurs = 0,
        identifier = "orbisgis:wps:official:insertValues:fieldList")
String[] fieldList

/** Coma separated values to insert. */
@LiteralDataInput(
        title = ["Values","en",
                "Valeurs","fr"],
        description = [
                "The input values. The values should be separated by a ',' and rows by ';'","en",
                "Les valeurs à insérer. Elles doivent etre séparées par une ',' et les lignes par un ';'","fr"],
        identifier = "orbisgis:wps:official:insertValues:values")
String values

/** String output of the process. */
@LiteralDataOutput(
        title = ["Output message","en",
                "Message de sortie","fr"],
        description = [
                "The output message.","en",
                "Le message de sortie.","fr"],
        identifier = "orbisgis:wps:official:insertValues:literalOutput")
String literalOutput

