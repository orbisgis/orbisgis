package org.orbisgis.orbiswpsservicescripts.scripts.Import

import org.orbisgis.orbiswpsgroovyapi.input.*
import org.orbisgis.orbiswpsgroovyapi.output.*
import org.orbisgis.orbiswpsgroovyapi.process.*

/**
 * @author Erwan Bocher
 */
@Process(title = ["Import a CSV file","en","Importer un fichier CSV","fr"],
    description = ["Import in the database a CSV file as a new table.","en",
                "Import d'un fichier CSV dans la base de données.","fr"],
    keywords = ["OrbisGIS,Importer, Fichier, CSV","fr",
                "OrbisGIS,Import, File, CSV","en"],
    properties = ["DBMS_TYPE","H2GIS"],
    version = "1.0")
def processing() {
    File csvFile = new File(csvDataInput[0])
    name = csvFile.getName()
    tableName = name.substring(0, name.lastIndexOf(".")).toUpperCase()
    if(jdbcTableOutputName != null){
	tableName = jdbcTableOutputName
    }
    if(dropTable){
	sql.execute "drop table if exists " + tableName
    }

    String csvRead = "CSVRead('"+csvFile.absolutePath+"', NULL, 'fieldSeparator="+separator+"')";
    String create = "CREATE TABLE "+ tableName ;    
    sql.execute(create + " AS SELECT * FROM "+csvRead+";");   

    literalDataOutput = "The CSV file has been imported."
}


@RawDataInput(
    title = ["Input CSV","en","Fichier CSV","fr"],
    description = ["The input CSV file to be imported.","en",
                "Selectionner un fichier CSV à importer.","fr"],
    fileTypes = ["csv"],
    isDirectory = false)
String[] csvDataInput


@EnumerationInput(
    title = ["CSV separator","en","Séparateur CSV","fr"],
    description = ["The CSV separator.","en",
                "Le séparateur CSV.","fr"],
    values=[",", "\t", " ", ";"],
    names=["Coma, Tabulation, Space, Semicolon","en","Virgule, Tabulation, Espace, Point virgule","fr"],
    isEditable = true)
String[] separator = [";"]




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
    description = ["Table name to store the CSV file. If it is not defined the name of the file will be used.","en",
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
