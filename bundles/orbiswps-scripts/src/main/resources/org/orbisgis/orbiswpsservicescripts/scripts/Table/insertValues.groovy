package org.orbisgis.orbiswpsservicescripts.scripts.Table

import org.orbisgis.orbiswpsgroovyapi.input.*
import org.orbisgis.orbiswpsgroovyapi.output.*
import org.orbisgis.orbiswpsgroovyapi.process.*

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
 *  - The field list concerned by the value insertion
 *
 * @author Sylvain PALOMINOS
 */
@Process(
        title = ["Insert values in a table","en",
                "Insertion de valeurs dans une table","fr"],
        description = ["Insert values into a table.","en",
                "Insert de valeurs dans une table.","fr"],
        keywords = ["Table,Insert,Values", "en",
                "Table,Insertion,Valeurs", "fr"],
        properties = ["DBMS_TYPE", "H2GIS",
                "DBMS_TYPE", "POSTGIS"],
        version = "1.0",
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
        queryBase += fieldsStr+") ";
}
    queryBase += " VALUES (";
    //execute the query for each row
    String[] rowArray = values.split(":")
    for(String row : rowArray){
        String query = queryBase
        String[] valueArray = row.split(";", -1)
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

/** This JDBCTable is the input data source table. */
@JDBCTableInput(
        title = ["Table","en",
                "Table","fr"],
        description = ["The table to edit.","en",
                "La table à éditer.","fr"],
        identifier = "tableName")
String tableName

/**********************/
/** INPUT Parameters **/
/**********************/

/** Field list concerned by the value insertion. */
@JDBCColumnInput(
        title = ["Columns","en",
                "Colonnes","fr"],
        description = [
                "The columns concerned by the value insertion.","en",
                "Les colonnes concernés par les insertions de valeurs.","fr"],
        jdbcTableReference = "tableName",
        multiSelection = true,
        minOccurs = 0,
        identifier = "fieldList")
String[] fieldList

/** Coma separated values to insert. */
@LiteralDataInput(
        title = ["Values","en",
                "Valeurs","fr"],
        description = [
                "The input values. The values should be separated by a ',' and rows by ';'","en",
                "Les valeurs à insérer. Elles doivent etre séparées par une ',' et les lignes par un ';'","fr"],
        identifier = "values")
String values

/** String output of the process. */
@LiteralDataOutput(
        title = ["Output message","en",
                "Message de sortie","fr"],
        description = [
                "The output message.","en",
                "Le message de sortie.","fr"],
        identifier = "literalOutput")
String literalOutput

