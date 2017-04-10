package org.orbisgis.orbiswpsservicescripts.scripts.Export

import org.orbisgis.orbiswpsgroovyapi.input.*
import org.orbisgis.orbiswpsgroovyapi.output.*
import org.orbisgis.orbiswpsgroovyapi.process.*

import org.h2gis.functions.io.tsv.TSVDriverFunction
import org.orbisgis.corejdbc.H2GISProgressMonitor
import org.h2gis.api.DriverFunction


/**
 * @author Erwan Bocher
 */
@Process(title = ["Export TSV file","en","Exporter dans un fichier TSV","fr"],
    description = ["Export a table to a TSV file.","en",
                "Exporter une table dans un fichier TSV.","fr"],
    keywords = ["OrbisGIS,Exporter, Fichier, TSV","fr",
                "OrbisGIS,Export, File, TSV","en"],
    properties = ["DBMS_TYPE", "H2GIS","DBMS_TYPE", "POSTGIS"],
    version = "1.0")
def processing() {
    File outputFile = new File(fileDataInput[0])    
    DriverFunction exp = new TSVDriverFunction();
    exp.exportTable(sql.getDataSource().getConnection(), inputJDBCTable, outputFile,new H2GISProgressMonitor(progressMonitor)); 
    if(dropInputTable){
	sql.execute "drop table if exists " + inputJDBCTable
    }
    literalDataOutput = "The TSV file has been created."
}



@JDBCTableInput(
    title = [
                "Table to export","en",
                "Table à exporter","fr"],
    description = [
                "The table that will be exported in a TSV file","en",
                "La table à exporter dans un fichier TSV.","fr"])
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
    title = ["Output TSV","en","Fichier TSV","fr"],
    description = ["The output TSV file to be exported.","en",
                "Nom du fichier TSV à exporter.","fr"],
    fileTypes = ["tsv"],
    isDirectory = false)
String[] fileDataInput


@LiteralDataOutput(
    title = ["Output message","en",
                "Message de sortie","fr"],
    description = ["Output message.","en",
                "Message de sortie.","fr"])
String literalDataOutput
