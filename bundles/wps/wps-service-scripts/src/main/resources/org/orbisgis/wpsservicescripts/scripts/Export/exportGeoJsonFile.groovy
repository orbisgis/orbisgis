package org.orbisgis.wpsservicescripts.scripts.IO

import org.orbisgis.wpsgroovyapi.input.EnumerationInput
import org.orbisgis.wpsgroovyapi.input.JDBCTableInput
import org.orbisgis.wpsgroovyapi.input.LiteralDataInput
import org.orbisgis.wpsgroovyapi.input.RawDataInput
import org.orbisgis.wpsgroovyapi.output.LiteralDataOutput
import org.orbisgis.wpsgroovyapi.process.Process
import org.h2gis.functions.io.geojson.GeoJsonDriverFunction
import org.orbisgis.corejdbc.H2GISProgressMonitor
import org.h2gis.api.DriverFunction
import org.h2gis.api.EmptyProgressVisitor


/**
 * @author Erwan Bocher
 */
@Process(title = ["Export into a GeoJSON","en","Exporter dans un fichier GeoJSON","fr"],
    description = ["Export a table to a GeoJSON.","en",
                "Exporter une table dans un fichier GeoJSON.","fr"],
    keywords = ["OrbisGIS,Exporter, Fichier, GeoJSON","fr",
                "OrbisGIS,Export, File, GeoJSON","en"],
    properties = ["DBMS_TYPE", "H2GIS","DBMS_TYPE", "POSTGIS"])
def processing() {
    File outputFile = new File(fileDataInput[0])    
    DriverFunction exp = new GeoJsonDriverFunction();
    exp.exportTable(sql.getDataSource().getConnection(), inputJDBCTable, outputFile,new H2GISProgressMonitor(progressMonitor)); 
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



/************/
/** OUTPUT **/
/************/

@RawDataInput(
    title = ["Output GeoJSON","en","Fichier GeoJSON","fr"],
    description = ["The output GeoJSON file to be exported.","en",
                "Nom du fichier GeoJSON à exporter.","fr"],
    fileTypes = ["geojson"], multiSelection=false)
String[] fileDataInput


@LiteralDataOutput(
    title = ["Output message","en",
                "Message de sortie","fr"],
    description = ["Output message.","en",
                "Message de sortie.","fr"])
String literalDataOutput
