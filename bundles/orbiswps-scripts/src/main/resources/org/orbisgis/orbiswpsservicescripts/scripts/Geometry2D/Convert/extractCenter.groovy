package org.orbisgis.orbiswpsservicescripts.scripts.Geometry2D.Convert

import org.orbisgis.orbiswpsgroovyapi.input.*
import org.orbisgis.orbiswpsgroovyapi.output.*
import org.orbisgis.orbiswpsgroovyapi.process.*

/**
 * This process extract the center of a geometry table using  SQL functions.
 * The user has to specify (mandatory):
 *  - The input spatial data source (JDBCTable)
 *  - The geometry column (LiteralData)
 *  - The geometry operation (centroid or interior point)
 *  - The output data source (JDBCTable)
 *
 * @return A datadase table.
 * @author Erwan Bocher
 * @author Sylvain PALOMINOS
 */
@Process(
		title = ["Extract center","en",
				"Extraction du centre","fr"],
		description = ["Extract the center of a geometry.","en",
				"Extraction du centre d'une géométrie.","fr"],
		keywords = ["Vector,Geometry,Extract,Center", "en",
				"Vecteur,Géométrie,Extraction,Centre", "fr"],

		properties = ["DBMS_TYPE", "H2GIS",
				"DBMS_TYPE", "POSTGIS"],
                version = "1.0")
def processing() {
	//Build the start of the query
	String query = "CREATE TEMPORARY TABLE "+outputTableName+" AS SELECT "
   

	if(operation[0].equalsIgnoreCase("centroid")){
		query += " ST_Centroid("+geometricField[0]+""
	}
	else{
		query += " ST_PointOnSurface("+geometricField[0]+""
	}
    //Build the end of the query
    query += ") AS the_geom ,"+ idField[0]+ " FROM "+inputJDBCTable+";"
    
    if(dropTable){
	sql.execute "drop table if exists " + outputTableName
    }
    
    //Execute the query
    sql.execute(query)
    if(dropInputTable){
        sql.execute "drop table if exists " + inputJDBCTable
    }
    literalOutput = "Process done"
}


/****************/
/** INPUT Data **/
/****************/

/** This JDBCTable is the input data source. */
@JDBCTableInput(
		title = ["Extract center","en",
				"Extraction du centre","fr"],
		description = [
				"Extract the center of a geometry.","en",
				"Extraction du centre d'une géométrie.","fr"],
        dataTypes = ["GEOMETRY"])
String inputJDBCTable

/**********************/
/** INPUT Parameters **/
/**********************/

/** Name of the Geometric field of the JDBCTable inputJDBCTable. */
@JDBCColumnInput(
		title = [
				"Geometric column","en",
				"Colonne géométrique","fr"],
		description = [
				"The geometric column of the data source.","en",
				"La colonne géométrique de la source de données.","fr"],
        jdbcTableReference = "inputJDBCTable",
        dataTypes = ["GEOMETRY"])
String[] geometricField

/** Name of the identifier field of the JDBCTable inputJDBCTable. */
@JDBCColumnInput(
		title = ["Column identifier","en",
				"Colonne identifiant","fr"],
		description = [
				"A column used as an identifier.","en",
				"Colonne utilisée comme identifiant.","fr"],
		excludedTypes=["GEOMETRY"],
		jdbcTableReference = "inputJDBCTable")
String[] idField

@EnumerationInput(
		title = ["Operation","en",
				"Opération","fr"],
		description = [
				"Operation to extract the points.","en",
				"Opération d'extraction des points.","fr"],
        values=["centroid", "interior"],
        names=["Centroid", "Interior"])
String[] operation = ["centroid"]

@LiteralDataInput(
    title = [
				"Drop the output table if exists","en",
				"Supprimer la table de sortie si elle existe","fr"],
    description = [
				"Drop the output table if exists.","en",
				"Supprimer la table de sortie si elle existe.","fr"])
Boolean dropTable 

@LiteralDataInput(
		title = ["Output table name","en",
				"Nom de la table de sortie","fr"],
		description = [
				"Name of the table containing the result of the process.","en",
				"Nom de la table contenant les résultats du traitement.","fr"])
String outputTableName


@LiteralDataInput(
    title = [
				"Drop the input table","en",
				"Supprimer la table d'entrée","fr"],
    description = [
				"Drop the input table when the script is finished.","en",
				"Supprimer la table d'entrée lorsque le script est terminé.","fr"])
Boolean dropInputTable 


/*****************/
/** OUTPUT Data **/
/*****************/

/** String output of the process. */
@LiteralDataOutput(
		title = ["Output message","en",
				"Message de sortie","fr"],
		description = [
				"The output message.","en",
				"Le message de sortie.","fr"])
String literalOutput

