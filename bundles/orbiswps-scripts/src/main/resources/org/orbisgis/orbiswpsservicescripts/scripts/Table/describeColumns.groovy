package org.orbisgis.orbiswpsservicescripts.scripts.Table

import org.orbisgis.orbiswpsgroovyapi.input.*
import org.orbisgis.orbiswpsgroovyapi.output.*
import org.orbisgis.orbiswpsgroovyapi.process.*

/**
 * This process is used to describe the columns of a table
 * 
 *
 * @author Erwan Bocher
 * @author Sylvain PALOMINOS
 */
@Process(
        title = ["Describe columns","en",
                "Décrire les colonnes","fr"],
        description = ["Extract the name, type and comment from all fields of a table.","en",
                "Extrait le nom, le type et le commentaire de chacun des champs d'un table.","fr"],
        keywords = ["Table,Describe", "en",
                "Table,Description", "fr"],
        properties = ["DBMS_TYPE", "H2GIS",
                "DBMS_TYPE", "POSTGIS"],
        version = "1.0")
def processing() {    
    String query;
    if(isH2){
        query =  "CREATE TABLE " + outputTableName +" as SELECT COLUMN_NAME as col_name, TYPE_NAME as col_type,  REMARKS as col_comment from INFORMATION_SCHEMA.COLUMNS where table_name = '"+ tableName+"';"
    }
    else{
        query =   "CREATE TABLE " + outputTableName +" as SELECT cols.column_name as col_name,cols.udt_name as col_type, pg_catalog.col_description(c.oid, cols.ordinal_position::int) as col_comment FROM pg_catalog.pg_class c, information_schema.columns cols WHERE cols.table_name = '"+tableName +"'AND cols.table_name = c.relname "
    } 
    
    if(dropTable){
	sql.execute "drop table if exists " + outputTableName
    }
    sql.execute(query);
    literalOutput = "The descriptions have been extracted."
}

/****************/
/** INPUT Data **/
/****************/

/** This JDBCTable is the input data source table. */
@JDBCTableInput(
        title = ["Table","en",
                "Table","fr"],
        description = ["Extract name, type and comments from the selected table.","en",
                "Extrait les noms, les types et les commentaires de la table.","fr"])
String tableName

@LiteralDataInput(
    title = [
				"Drop the output table if exists","en",
				"Supprimer la table de sortie si elle existe","fr"],
    description = [
				"Drop the output table if exists.","en",
				"Supprimer la table de sortie si elle existe.","fr"])
Boolean dropTable 

@LiteralDataInput(
        title = ["Output table name","en",
                "Nom de la table de sortie","fr"],
        description = [
                "Name of the table containing the descriptions.","en",
                "Nom de la table contenant les descriptions.","fr"])
String outputTableName


/** Output message. */
@LiteralDataOutput(
        title = ["Output message","en",
                "Message de sortie","fr"],
        description = [
                "The output message.","en",
                "Le message de sortie.","fr"])
String literalOutput

