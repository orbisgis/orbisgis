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
import org.h2gis.api.EmptyProgressVisitor


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
    DriverFunction exp = new CSVDriverFunction();
    exp.exportTable(sql.getDataSource().getConnection(), inputJDBCTable, csvFile,new H2GISProgressMonitor(progressMonitor)); 
    literalDataOutput = "The CSV file has been exported."
}



@JDBCTableInput(
        title = [
                "Table to export","en",
                "Table à exporter","fr"],
        description = [
                "The table that will be exported in a CSV file","en",
                "La table à exporter dans un fichier CSV.","fr"])
String inputJDBCTable


@EnumerationInput(
    title = ["CSV separator","en","Séparateur CSV","fr"],
    description = ["The CSV separator.","en",
                "Le séparateur CSV.","fr"],
    values=[",", "\t", " ", ";"],
    names=["Coma, Tabulation, Space, Semicolon","en","Virgule, Tabulation, Espace, Point virgule","fr"],
    isEditable = false)
String[] separator = [";"]


/************/
/** OUTPUT **/
/************/

@RawDataInput(
    title = ["Output CSV","en","Fichier CSV","fr"],
    description = ["The output CSV file to be exported.","en",
                "Nom du fichier CSV à exporter.","fr"],
    fileTypes = ["csv"], multiSelection=false)
String[] csvDataInput


@LiteralDataOutput(
    title = ["Output message","en",
                "Message de sortie","fr"],
    description = ["Output message.","en",
                "Message de sortie.","fr"])
String literalDataOutput
