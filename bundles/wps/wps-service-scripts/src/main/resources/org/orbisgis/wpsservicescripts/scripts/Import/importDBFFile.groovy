package org.orbisgis.wpsservicescripts.scripts.IO

import org.orbisgis.wpsgroovyapi.input.EnumerationInput
import org.orbisgis.wpsgroovyapi.input.LiteralDataInput
import org.orbisgis.wpsgroovyapi.input.RawDataInput
import org.orbisgis.wpsgroovyapi.output.LiteralDataOutput
import org.orbisgis.wpsgroovyapi.process.Process


/**
 * @author Erwan Bocher
 */
@Process(title = ["Import a DBF file","en","Importer un fichier DBF","fr"],
    description = ["Import in the database a DBF file as a new table.","en",
                "Import d'un fichier DBF dans la base de données.","fr"],
    keywords = ["OrbisGIS,Importer, Fichier, DBF","fr",
                "OrbisGIS,Import, File, DBF","en"],
    properties = ["DBMS_TYPE","H2GIS"],
    version = "1.0")
def processing() {
    File dbfFile = new File(dbfDataInput[0])
    name = dbfFile.getName()
    tableName = name.substring(0, name.lastIndexOf(".")).toUpperCase()
    query = "CALL DBFREAD('"+ dbfFile.absolutePath+"','"
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

    literalDataOutput = "The DBF file has been imported."
}


@RawDataInput(
    title = ["Input DBF","en","Fichier DBF","fr"],
    description = ["The input DBF file to be imported.","en",
                "Selectionner un fichier DBF à importer.","fr"],
    fileTypes = ["dbf"], multiSelection=false)
String[] dbfDataInput



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
				"Drop the existing table","en",
				"Supprimer la table existante","fr"],
    description = [
				"Drop the existing table.","en",
				"Supprimer la table existante.","fr"])
Boolean dropTable 



/** Optional table name. */
@LiteralDataInput(
    title = ["Output table name","en","Nom de la table importée","fr"],
    description = ["Table name to store the DBF file.","en",
                "Nom de la table importée.","fr"],
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
