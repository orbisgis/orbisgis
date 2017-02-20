package org.orbisgis.wpsservicescripts.scripts.IO

import org.orbisgis.wpsgroovyapi.input.EnumerationInput
import org.orbisgis.wpsgroovyapi.input.JDBCTableInput
import org.orbisgis.wpsgroovyapi.input.LiteralDataInput
import org.orbisgis.wpsgroovyapi.input.RawDataInput
import org.orbisgis.wpsgroovyapi.output.LiteralDataOutput
import org.orbisgis.wpsgroovyapi.process.Process
import org.h2gis.functions.io.csv.CSVDriverFunction
import org.orbisgis.corejdbc.H2GISProgressMonitor
import org.h2gis.api.DriverFunction


/**
 * @author Erwan Bocher
 */
@Process(title = ["Export CSV file","en","Exporter dans un fichier CSV","fr"],
    description = ["Export a table to a CSV file.","en",
                "Exporter une table dans un fichier CSV.","fr"],
    keywords = ["OrbisGIS,Exporter, Fichier, CSV","fr",
                "OrbisGIS,Export, File, CSV","en"],
    properties = ["DBMS_TYPE", "H2GIS","DBMS_TYPE", "POSTGIS"])
def processing() {
    File outputFile = new File(fileDataInput[0])    
    DriverFunction exp = new CSVDriverFunction();
    exp.exportTable(sql.getDataSource().getConnection(), inputJDBCTable, outputFile,new H2GISProgressMonitor(progressMonitor)); 
    
    if(dropTable){
	sql.execute "drop table if exists " + inputJDBCTable
    }
    
    literalDataOutput = "The CSV file has been created."
}



@JDBCTableInput(
    title = [
                "Table to export","en",
                "Table à exporter","fr"],
    description = [
                "The table that will be exported in a CSV file","en",
                "La table à exporter dans un fichier CSV.","fr"])
String inputJDBCTable


@LiteralDataInput(
    title = [
				"Drop the table","en",
				"Supprimer la table","fr"],
    description = [
				"Drop the table when the export is finished.","en",
				"Supprimer la table à l'issue l'export.","fr"])
Boolean dropTable 



/************/
/** OUTPUT **/
/************/

@RawDataInput(
    title = ["Output CSV","en","Fichier CSV","fr"],
    description = ["The output CSV file to be exported.","en",
                "Nom du fichier CSV à exporter.","fr"],
    fileTypes = ["csv"], multiSelection=false)
String[] fileDataInput


@LiteralDataOutput(
    title = ["Output message","en",
                "Message de sortie","fr"],
    description = ["Output message.","en",
                "Message de sortie.","fr"])
String literalDataOutput
