package org.orbisgis.wpsservicescripts.scripts.Vector.Create

import org.orbisgis.wpsgroovyapi.input.JDBCTableFieldInput
import org.orbisgis.wpsgroovyapi.input.JDBCTableInput
import org.orbisgis.wpsgroovyapi.input.LiteralDataInput
import org.orbisgis.wpsgroovyapi.output.LiteralDataOutput
import org.orbisgis.wpsgroovyapi.process.Process
/********************/
/** Process method **/
/********************/

/**
 * This process is used to extrude 3D polygons.
 *
 * @return A datadase table.
 * @author Erwan BOCHER
 */
@Process(
		title = [
				"Fixed extrude polygons","en",
				"Extrusion de polygones fixe","fr"],
		description = [
				"Extrude a polygon and extends it to a 3D representation, returning a geometry collection containing floor, ceiling and wall geometries.","en",
				"Extrusion de polygones en l'étendant à une représentation en 3D, retournant une collection de géométries contenant les géométries du sol, du plafond et des murs.","fr"],
		keywords = ["Vector,Geometry,Create", "en",
				"Vecteur,Géométrie,Création", "fr"],
		properties = ["DBMS_TYPE", "H2GIS"])
def processing() {

    //Build the start of the query
    String query = "CREATE TABLE "+outputTableName+" AS SELECT ST_EXTRUDE("+geometricField[0]+","+height+") AS the_geom "

	for(String field : fieldList) {
		if (field != null) {
			query += ", " + field;
		}
	}

	query+=" FROM "+inputJDBCTable+";"

    if(dropTable){
	sql.execute "drop table if exists " + outputTableName
    }
    
    //Execute the query
    sql.execute(query)
	literalOutput = "Process done"
}


/****************/
/** INPUT Data **/
/****************/

@JDBCTableInput(
		title = [
				"Input spatial data","en",
				"Données spatiales d'entrée","fr"],
		description = [
				"The spatial data source that must be extruded.","en",
				"La source de données qui doit etre extrudée.","fr"],
		dataTypes = ["GEOMETRY"])
String inputJDBCTable

/**********************/
/** INPUT Parameters **/
/**********************/

@JDBCTableFieldInput(
		title = [
				"Geometric field","en",
				"Champ géométrique","fr"],
		description = [
				"The geometric field of the data source.","en",
				"Le champ géométrique de la source de données.","fr"],
		jdbcTableReference = "inputJDBCTable",
        dataTypes = ["GEOMETRY"])
String[] geometricField


@LiteralDataInput(
		title = [
				"Height of the polygons","en",
				"Hauteur des polygones","fr"],
		description = [
				"A numeric value to specify the height of all polygon.","en",
				"Une valeur numérique définissant la hauteur des polygones.","fr"])
Double height = 1

/** Fields to keep. */
@JDBCTableFieldInput(
		title = [
				"Fields to keep","en",
				"Champs à conserver","fr"],
		description = [
				"The fields that will be kept in the output.","en",
				"Les champs qui seront conservés dans la table de sortie.","fr"],
		excludedTypes=["GEOMETRY"],
		multiSelection = true,
		minOccurs = 0,
        jdbcTableReference = "inputJDBCTable")
String[] fieldList

@LiteralDataInput(
    title = [
				"Drop the output table if exists","en",
				"Supprimer la table de sortie si elle existe","fr"],
    description = [
				"Drop the output table if exists.","en",
				"Supprimer la table de sortie si elle existe.","fr"])
Boolean dropTable 

@LiteralDataInput(
		title = [
				"Output table name","en",
				"Nom de la table de sortie","fr"],
		description = [
				"Name of the table containing the result of the process.","en",
				"Nom de la table contenant les résultats du traitement.","fr"])
String outputTableName

/*****************/
/** OUTPUT Data **/
/*****************/

/** String output of the process. */
@LiteralDataOutput(
		title = [
				"Output message","en",
				"Message de sortie","fr"],
		description = [
				"The output message.","en",
				"Le message de sortie.","fr"])
String literalOutput

