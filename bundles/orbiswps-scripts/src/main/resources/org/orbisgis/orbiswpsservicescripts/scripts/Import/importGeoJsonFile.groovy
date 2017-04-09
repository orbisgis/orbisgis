package org.orbisgis.orbiswpsservicescripts.scripts.Import

import org.orbisgis.orbiswpsgroovyapi.input.*
import org.orbisgis.orbiswpsgroovyapi.output.*
import org.orbisgis.orbiswpsgroovyapi.process.*

/**
 * @author Erwan Bocher
 */
@Process(title = ["Import a GeoJSON file","en","Importer un fichier GeoJSON","fr"],
    description = ["Import in the database a GeoJSON file as a new table.","en",
                "Import d'un fichier GeoJSON dans la base de données.","fr"],
    keywords = ["OrbisGIS,Importer, Fichier, GeoJSON","fr",
                "OrbisGIS,Import, File, GeoJSON","en"],
    properties = ["DBMS_TYPE","H2GIS"],
    version = "1.0")
def processing() {
    File fileData = new File(fileDataInput[0])
    name = fileData.getName()
    tableName = name.substring(0, name.lastIndexOf(".")).toUpperCase()
    query = "CALL GeoJsonRead('"+ fileData.absolutePath+"','"
    if(jdbcTableOutputName != null){
	tableName = jdbcTableOutputName
    }
    if(dropTable){
	sql.execute "drop table if exists " + tableName
    }
    
    query += tableName+"')"	    

    sql.execute query

    if(createIndex){
        sql.execute "create spatial index on "+ tableName + " (the_geom)"
    }

    literalDataOutput = "The GeoJSON file has been imported."
}


@RawDataInput(
    title = ["Input GeoJSON","en","Fichier GeoJSON","fr"],
    description = ["The input GeoJSON file to be imported.","en",
                "Selectionner un fichier GeoJSON à importer.","fr"],
    fileTypes = ["geojson"],
    isDirectory = false)
String[] fileDataInput



@LiteralDataInput(
    title = [
				"Add a spatial index","en",
				"Créer un index spatial","fr"],
    description = [
				"Add a spatial index on the geometry column.","en",
				"Ajout d'un index spatial sur la géometrie de la table.","fr"])
Boolean createIndex


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
    description = ["Table name to store the GeoJSON file. If it is not defined the name of the file will be used.","en",
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
