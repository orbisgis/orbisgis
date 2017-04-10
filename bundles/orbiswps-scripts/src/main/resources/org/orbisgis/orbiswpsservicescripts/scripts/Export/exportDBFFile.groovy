package org.orbisgis.orbiswpsservicescripts.scripts.Export

import org.h2gis.api.DriverFunction
import org.h2gis.functions.io.dbf.DBFDriverFunction
import org.orbisgis.corejdbc.H2GISProgressMonitor

import org.orbisgis.orbiswpsgroovyapi.input.*
import org.orbisgis.orbiswpsgroovyapi.output.*
import org.orbisgis.orbiswpsgroovyapi.process.*
/**
 * @author Erwan Bocher
 */
@Process(title = ["Export DBF file","en","Exporter dans un fichier DBF","fr"],
    description = ["Export a table to a DBF file.","en",
                "Exporter une table dans un fichier DBF.","fr"],
    keywords = ["OrbisGIS,Exporter, Fichier, DBF","fr",
                "OrbisGIS,Export, File, DBF","en"],
    properties = ["DBMS_TYPE", "H2GIS","DBMS_TYPE", "POSTGIS"],
    version = "1.0")
def processing() {
    File outputFile = new File(fileDataInput[0])    
    DriverFunction exp = new DBFDriverFunction();
    exp.exportTable(sql.getDataSource().getConnection(), inputJDBCTable, outputFile,new H2GISProgressMonitor(progressMonitor)); 
    if(dropInputTable){
	sql.execute "drop table if exists " + inputJDBCTable
    }
    literalDataOutput = "The DBF file has been created."
}



@JDBCTableInput(
    title = [
                "Table to export","en",
                "Table à exporter","fr"],
    description = [
                "The table that will be exported in a DBF file","en",
                "La table à exporter dans un fichier DBF.","fr"])
String inputJDBCTable


@LiteralDataInput(
    title = [
				"Drop the input table","en",
				"Supprimer la table d'entrée","fr"],
    description = [
				"Drop the input table when the export is finished.","en",
				"Supprimer la table d'entrée à l'issue l'export.","fr"])
Boolean dropInputTable 


/************/
/** OUTPUT **/
/************/

@RawDataInput(
    title = ["Output DBF","en","Fichier DBF","fr"],
    description = ["The output DBF file to be exported.","en",
                "Nom du fichier DBF à exporter.","fr"],
    fileTypes = ["dbf"],
    isDirectory = false)
String[] fileDataInput




@LiteralDataOutput(
    title = ["Output message","en",
                "Message de sortie","fr"],
    description = ["Output message.","en",
                "Message de sortie.","fr"])
String literalDataOutput
