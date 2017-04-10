package org.orbisgis.orbiswpsservicescripts.scripts.Export

import org.orbisgis.orbiswpsgroovyapi.input.*
import org.orbisgis.orbiswpsgroovyapi.output.*
import org.orbisgis.orbiswpsgroovyapi.process.*

import org.h2gis.functions.io.kml.KMLDriverFunction
import org.orbisgis.corejdbc.H2GISProgressMonitor
import org.h2gis.api.DriverFunction


/**
 * @author Erwan Bocher
 */
@Process(title = ["Export into a KML file","en","Exporter dans un fichier KML","fr"],
    description = ["Export a table to a KML file.","en",
                "Exporter une table dans un fichier KML.","fr"],
    keywords = ["OrbisGIS,Exporter, Fichier, KML","fr",
                "OrbisGIS,Export, File, KML","en"],
    properties = ["DBMS_TYPE", "H2GIS","DBMS_TYPE", "POSTGIS"],
    version = "1.0")
def processing() {
    File outputFile = new File(fileDataInput[0])    
    DriverFunction exp = new KMLDriverFunction();
    exp.exportTable(sql.getDataSource().getConnection(), inputJDBCTable, outputFile,new H2GISProgressMonitor(progressMonitor)); 
    if(dropInputTable){
	sql.execute "drop table if exists " + inputJDBCTable
    }
    literalDataOutput = "The KML file has been created."
}



@JDBCTableInput(
    title = [
                "Table to export","en",
                "Table à exporter","fr"],
    description = [
                "The table that will be exported in a KML file","en",
                "La table à exporter dans un fichier KML.","fr"],
    dataTypes = ["GEOMETRY"])
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
    title = ["Output KML","en","Fichier KML","fr"],
    description = ["The output KML file to be exported.","en",
                "Nom du fichier KML à exporter.","fr"],
    fileTypes = ["kml", "kmz"],
    isDirectory = false)
String[] fileDataInput


@LiteralDataOutput(
    title = ["Output message","en",
                "Message de sortie","fr"],
    description = ["Output message.","en",
                "Message de sortie.","fr"])
String literalDataOutput
