package org.orbisgis.wpsservicescripts.scripts.Vector.Transform

import org.orbisgis.wpsgroovyapi.input.JDBCTableFieldInput
import org.orbisgis.wpsgroovyapi.input.JDBCTableInput
import org.orbisgis.wpsgroovyapi.input.JDBCTableFieldValueInput
import org.orbisgis.wpsgroovyapi.input.LiteralDataInput
import org.orbisgis.wpsgroovyapi.output.LiteralDataOutput
import org.orbisgis.wpsgroovyapi.process.Process

/********************/
/** Process method **/
/********************/



/**
 * This process reproject a geometry table using the SQL function.
 * The user has to specify (mandatory):
 *  - The input spatial data source (JDBCTable)
 *  - The geometry column (LiteralData)
 *  - The SRID value selected from the spatial_ref table
 *  - The output data source (JDBCTable)
 *
 * @return A database table or a file.
 * @author Erwan Bocher
 */
@Process(
		title = [
				"Reproject geometries","en",
				"Reprojection de géométries","fr"],
		description = [
				"Reproject geometries from one Coordinate Reference System to another.","en",
				"Reprojection une géométrie d'un SRID vers un autre.","fr"],
		keywords = ["Vector,Geometry,Reproject", "en",
				"Vecteur,Géométrie,Reprojection", "fr"],
		properties = ["DBMS_TYPE", "H2GIS",
				"DBMS_TYPE", "POSTGIS"],
		identifier = "orbisgis:wps:official:reprojectGeometries"
)
def processing() {
	//Build the start of the query
	String query = "CREATE TABLE " + outputTableName + " AS SELECT ST_TRANSFORM("
	query += geometricField[0] + "," + srid[0]

	//Build the end of the query
	query += ") AS the_geom ";

	for (String field : fieldList) {
		if (field != null) {
			query += ", " + field;
		}
	}

    query +=  " FROM "+inputJDBCTable+";"
    logger.warn(query)
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

/** This JDBCTable is the input data source. */
@JDBCTableInput(
		title = [
				"Input spatial data","en",
				"Données spatiales d'entrée","fr"],
		description = [
				"The spatial data source to be reprojected.","en",
				"La source de données spatiales pour la reprojection.","fr"],
		dataTypes = ["GEOMETRY"],
		identifier = "orbisgis:wps:official:reprojectGeometries:inputJDBCTable"
)
String inputJDBCTable


/**********************/
/** INPUT Parameters **/
/**********************/

/** Name of the Geometric field of the JDBCTable inputJDBCTable. */
@JDBCTableFieldInput(
		title = [
				"Geometric field","en",
				"Champ géométrique","fr"],
		description = [
				"The geometric field of the data source.","en",
				"Le champ géométrique de la source de données.","fr"],
        jdbcTableReference = "orbisgis:wps:official:reprojectGeometries:inputJDBCTable",
        dataTypes = ["GEOMETRY"],
		identifier = "orbisgis:wps:official:reprojectGeometries:geometryField"
)
String[] geometricField


/** The spatial_ref SRID */
@JDBCTableFieldValueInput(
		title = [
				"SRID","en",
				"SRID","fr"],
		description = [
				"The spatial reference system identifier.","en",
				"L'identifiant du système de référence spatiale.","fr"],
		jdbcTableFieldReference = "\$public\$spatial_ref_sys\$srid\$",
		multiSelection = false,
		identifier = "orbisgis:wps:official:reprojectGeometries:srid"
)
String[] srid


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
        	jdbcTableReference = "orbisgis:wps:official:reprojectGeometries:inputJDBCTable",
		identifier = "orbisgis:wps:official:reprojectGeometries:fieldList"
)
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
				"Nom de la table contenant les résultats du traitement.","fr"],
		identifier = "orbisgis:wps:official:reprojectGeometries:outputTableName"
)
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
				"Le message de sortie.","fr"],
		identifier = "orbisgis:wps:official:reprojectGeometries:literalOutput"
)
String literalOutput


