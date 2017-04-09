package org.orbisgis.orbiswpsservicescripts.scripts.Import

import org.orbisgis.orbiswpsgroovyapi.input.*
import org.orbisgis.orbiswpsgroovyapi.output.*
import org.orbisgis.orbiswpsgroovyapi.process.*

/**
 * @author Erwan Bocher
 */
@Process(title = ["Import a shapeFile","en","Importer un fichier SHP","fr"],
    description = ["Import in the database a shapeFile as a new table.","en",
                "Import d'un fichier SHP dans la base de données.","fr"],
    keywords = ["OrbisGIS,Importer, Fichier, SHP","fr",
                "OrbisGIS,Import, File, SHP","en"],
    properties = ["DBMS_TYPE","H2GIS"],
    version = "1.0")
def processing() {
    File shpFile = new File(shpDataInput[0])
    name = shpFile.getName()
    tableName = name.substring(0, name.lastIndexOf(".")).toUpperCase()
    query = "CALL SHPREAD('"+ shpFile.absolutePath+"','"
    if(jdbcTableOutputName != null){
	tableName = jdbcTableOutputName
    }
    if(dropTable){
	sql.execute "drop table if exists " + tableName
    }

    if(encoding!=null && !encoding[0].equals("System")){
	query+= tableName+ "','"+ encoding[0] + "')"
    }else{
	query += tableName+"')"	
    }

    sql.execute query

    if(createIndex){
        sql.execute "create spatial index on "+ tableName + " (the_geom)"
    }

    literalDataOutput = "The SHP file has been imported."
}


@RawDataInput(
    title = ["Input SHP","en","Fichier SHP","fr"],
    description = ["The input shapeFile to be imported.","en",
                "Selectionner un fichier SHP à importer.","fr"],
    fileTypes = ["shp"],
    isDirectory = false)
String[] shpDataInput



@EnumerationInput(
    title = ["File Encoding","en","Encodage du fichier","fr"],
    description = ["The file encoding .","en",
                "L'encodage du fichier.","fr"],
    values=["System", "utf-8", "ISO-8859-1", "ISO-8859-2", "ISO-8859-4", "ISO-8859-5", "ISO-8859-7", "ISO-8859-9", "ISO-8859-13","ISO-8859-15"],
    names=["System, utf-8, ISO-8859-1, ISO-8859-2, ISO-8859-4, ISO-8859-5, ISO-8859-7, ISO-8859-9, ISO-8859-13,ISO-8859-15","en", "Système, utf-8, ISO-8859-1, ISO-8859-2, ISO-8859-4, ISO-8859-5, ISO-8859-7, ISO-8859-9, ISO-8859-13,ISO-8859-15","fr"],
    isEditable = false)
String[] encoding = ["System"]


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
    description = ["Table name to store the shapeFile. If it is not defined the name of the file will be used.","en",
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
