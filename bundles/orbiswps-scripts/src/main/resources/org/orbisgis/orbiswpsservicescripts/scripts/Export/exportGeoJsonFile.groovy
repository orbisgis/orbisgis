package org.orbisgis.orbiswpsservicescripts.scripts.Export

import org.orbisgis.orbiswpsgroovyapi.input.*
import org.orbisgis.orbiswpsgroovyapi.output.*
import org.orbisgis.orbiswpsgroovyapi.process.*

import org.h2gis.functions.io.geojson.GeoJsonDriverFunction
import org.orbisgis.corejdbc.H2GISProgressMonitor
import org.h2gis.api.DriverFunction


/**
 * @author Erwan Bocher
 */
@Process(title = ["Export into a GeoJSON","en","Exporter dans un fichier GeoJSON","fr"],
    description = ["Export a table to a GeoJSON.","en",
                "Exporter une table dans un fichier GeoJSON.","fr"],
    keywords = ["OrbisGIS,Exporter, Fichier, GeoJSON","fr",
                "OrbisGIS,Export, File, GeoJSON","en"],
    properties = ["DBMS_TYPE", "H2GIS","DBMS_TYPE", "POSTGIS"],
    version = "1.0")
def processing() {
    File outputFile = new File(fileDataInput[0])    
    DriverFunction exp = new GeoJsonDriverFunction();
    exp.exportTable(sql.getDataSource().getConnection(), inputJDBCTable, outputFile,new H2GISProgressMonitor(progressMonitor)); 
    if(dropInputTable){
	sql.execute "drop table if exists " + inputJDBCTable
    }
    literalDataOutput = "The GeoJSON file has been created."
}



@JDBCTableInput(
    title = [
                "Table to export","en",
                "Table à exporter","fr"],
    description = [
                "The table that will be exported in a GeoJSON file","en",
                "La table à exporter dans un fichier GeoJSON.","fr"],
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
    title = ["Output GeoJSON","en","Fichier GeoJSON","fr"],
    description = ["The output GeoJSON file to be exported.","en",
                "Nom du fichier GeoJSON à exporter.","fr"],
    fileTypes = ["geojson"],
    isDirectory = false)
String[] fileDataInput


@LiteralDataOutput(
    title = ["Output message","en",
                "Message de sortie","fr"],
    description = ["Output message.","en",
                "Message de sortie.","fr"])
String literalDataOutput
