package org.orbisgis.orbiswpsservicescripts.scripts.Export

import org.orbisgis.orbiswpsgroovyapi.input.*
import org.orbisgis.orbiswpsgroovyapi.output.*
import org.orbisgis.orbiswpsgroovyapi.process.*

import org.h2gis.functions.io.shp.SHPDriverFunction
import org.orbisgis.corejdbc.H2GISProgressMonitor
import org.h2gis.api.DriverFunction


/**
 * @author Erwan Bocher
 */
@Process(title = ["Export into a SHP file","en","Exporter dans un fichier SHP","fr"],
    description = ["Export a table to a SHP file.","en",
                "Exporter une table dans un fichier SHP.","fr"],
    keywords = ["OrbisGIS,Exporter, Fichier, SHP","fr",
                "OrbisGIS,Export, File, SHP","en"],
    properties = ["DBMS_TYPE", "H2GIS","DBMS_TYPE", "POSTGIS"],
    version = "1.0")
def processing() {
    File outputFile = new File(fileDataInput[0])    
    DriverFunction exp = new SHPDriverFunction();
    exp.exportTable(sql.getDataSource().getConnection(), inputJDBCTable, outputFile,new H2GISProgressMonitor(progressMonitor)); 
    if(dropInputTable){
	sql.execute "drop table if exists " + inputJDBCTable
    }
    literalDataOutput = "The ShapeFile has been created."
}



@JDBCTableInput(
    title = [
                "Table to export","en",
                "Table à exporter","fr"],
    description = [
                "The table that will be exported in a shapeFile file","en",
                "La table à exporter dans un fichier shapeFile.","fr"],
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
    title = ["Output shapeFile","en","Fichier shapeFile","fr"],
    description = ["The output shapeFile file to be exported.","en",
                "Nom du fichier shapeFile à exporter.","fr"],
    fileTypes = ["shp"],
    isDirectory = false)
String[] fileDataInput


@LiteralDataOutput(
    title = ["Output message","en",
                "Message de sortie","fr"],
    description = ["Output message.","en",
                "Message de sortie.","fr"])
String literalDataOutput
