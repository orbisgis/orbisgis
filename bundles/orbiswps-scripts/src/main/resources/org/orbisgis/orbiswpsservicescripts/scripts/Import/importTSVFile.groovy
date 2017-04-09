package org.orbisgis.orbiswpsservicescripts.scripts.Import

import org.orbisgis.orbiswpsgroovyapi.input.*
import org.orbisgis.orbiswpsgroovyapi.output.*
import org.orbisgis.orbiswpsgroovyapi.process.*

/**
 * @author Erwan Bocher
 */
@Process(title = ["Import a TSV file","en","Importer un fichier TSV","fr"],
    description = ["Import in the database a TSV file as a new table.","en",
                "Import d'un fichier TSV dans la base de données.","fr"],
    keywords = ["OrbisGIS,Importer, Fichier, TSV","fr",
                "OrbisGIS,Import, File, TSV","en"],
    properties = ["DBMS_TYPE","H2GIS"],
    version = "1.0")
def processing() {
    File fileData = new File(fileDataInput[0])
    name = fileData.getName()
    tableName = name.substring(0, name.lastIndexOf(".")).toUpperCase()
    query = "CALL TSVRead('"+ fileData.absolutePath+"','"
    if(jdbcTableOutputName != null){
	tableName = jdbcTableOutputName
    }
    if(dropTable){
	sql.execute "drop table if exists " + tableName
    }
    
    query += tableName+"')"	    

    sql.execute query

    literalDataOutput = "The TSV file has been imported."
}


@RawDataInput(
    title = ["Input TSV","en","Fichier TSV","fr"],
    description = ["The input TSV file to be imported.","en",
                "Selectionner un fichier TSV à importer.","fr"],
    fileTypes = ["tsv"],
    isDirectory = false)
String[] fileDataInput




@LiteralDataInput(
    title = [
				"Drop the existing table","en",
				"Supprimer la table existante","fr"],
    description = [
				"Drop the existing table.","en",
				"Supprimer la table existante.","fr"])
Boolean dropTable 



/** Optional table name. */
@LiteralDataInput(
    title = ["Output table name","en","Nom de la table importée","fr"],
    description = ["Table name to store the TSV file. If it is not defined the name of the file will be used.","en",
                "Nom de la table importée. Par défaut le nom de la table correspond au nom du fichier.","fr"],
    minOccurs = 0)
String jdbcTableOutputName





/************/
/** OUTPUT **/
/************/
@LiteralDataOutput(
    title = ["Output message","en",
                "Message de sortie","fr"],
    description = ["Output message.","en",
                "Message de sortie.","fr"])
String literalDataOutput
