package org.orbisgis.wpsservicescripts.scripts.IO

import org.orbisgis.wpsgroovyapi.input.EnumerationInput
import org.orbisgis.wpsgroovyapi.input.LiteralDataInput
import org.orbisgis.wpsgroovyapi.output.RawDataOutput
import org.orbisgis.wpsgroovyapi.output.LiteralDataOutput
import org.orbisgis.wpsgroovyapi.process.Process


/**
 * @author Erwan Bocher
 */
@Process(title = ["Export CSV file","en","Exporter en fichier CSV","fr"],
    description = ["Export a table to a CSV file.","en",
                "Exporter une table dans un fichier CSV.","fr"],
    keywords = ["OrbisGIS,Exporter, Fichier, CSV","fr",
                "OrbisGIS,Export, File, CSV","en"],
    properties = ["DBMS_TYPE","H2GIS"])
def processing() {
    File csvFile = new File(csvDataInput[0])
    name = csvFile.getName()
    tableName = name.substring(0, name.lastIndexOf(".")).toUpperCase()
    query = "CALL SHPWrite('"+ csvFile.absolutePath+"','"
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


@RawDataOutput(
    title = ["Output CSV","en","Fichier CSV","fr"],
    description = ["The output CSV file to be exported.","en",
                "Nom du fichier CSV à exporter.","fr"],
    fileTypes = ["csv"], multiSelection=false)
String[] csvDataInput


@EnumerationInput(
    title = ["CSV separator","en","Séparateur CSV","fr"],
    description = ["The CSV separator.","en",
                "Le séparateur CSV.","fr"],
    values=[",", "\t", " ", ";"],
    names=["Coma, Tabulation, Space, Semicolon","en","Virgule, Tabulation, Espace, Point virgule","fr"],
    isEditable = true)
String[] separator = [";"]


/************/
/** OUTPUT **/
/************/
@LiteralDataOutput(
    title = ["Output message","en",
                "Message de sortie","fr"],
    description = ["Output message.","en",
                "Message de sortie.","fr"])
String literalDataOutput
