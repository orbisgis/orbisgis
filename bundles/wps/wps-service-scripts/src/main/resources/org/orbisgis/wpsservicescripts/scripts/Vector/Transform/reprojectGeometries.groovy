package org.orbisgis.wpsservicescripts.scripts.Vector.Transform

import org.orbisgis.wpsgroovyapi.attributes.TranslatableString
import org.orbisgis.wpsgroovyapi.attributes.LanguageString
import org.orbisgis.wpsgroovyapi.attributes.MetadataAttribute
import org.orbisgis.wpsgroovyapi.input.*
import org.orbisgis.wpsgroovyapi.output.*
import org.orbisgis.wpsgroovyapi.process.*

/********************/
/** Process method **/
/********************/



/**
 * This process reproject a geometry table using the SQL function.
 * The user has to specify (mandatory):
 *  - The input spatial data source (DataStore)
 *  - The geometry column (LiteralData)
 *  - The SRID value selected from the spatial_ref table
 *  - The output data source (DataStore)
 *
 * @return A database table or a file.
 * @author Erwan Bocher
 */
@Process(
		translatedTitles = [
				@LanguageString(value = "Reproject geometries", lang = "en"),
				@LanguageString(value = "Reprojection de géométries", lang = "fr")
		],
		translatedResumes = [
				@LanguageString(value = "Reproject geometries from one Coordinate Reference System to another.", lang = "en"),
				@LanguageString(value = "Reprojection une géométrie d'un SRID vers un autre.", lang = "fr")
		],
		translatedKeywords = [
				@TranslatableString(translatableStrings = [
						@LanguageString(value = "Vector", lang = "en"),
						@LanguageString(value = "Vecteur", lang = "fr")
				]),
				@TranslatableString(translatableStrings = [
						@LanguageString(value = "Geometry", lang = "en"),
						@LanguageString(value = "Géometrie", lang = "fr")
				]),
				@TranslatableString(translatableStrings = [
						@LanguageString(value = "Reproject", lang = "en"),
						@LanguageString(value = "Reprojection", lang = "fr")
				])
		],
		metadata = [
				@MetadataAttribute(title="h2gis", role ="DBMS", href = "http://www.h2gis.org/"),
				@MetadataAttribute(title="postgis", role ="DBMS", href = "http://postgis.net/")
		],
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

 	query +=  " FROM "+inputDataStore+";"
	logger.warn(query)
    //Execute the query
    sql.execute(query)
	literalOutput = "Process done"
}

/****************/
/** INPUT Data **/
/****************/

/** This DataStore is the input data source. */
@DataStoreInput(
		translatedTitles = [
				@LanguageString(value = "Input spatial data", lang = "en"),
				@LanguageString(value = "Données spatiales d'entrée", lang = "fr")
		],
		translatedResumes = [
				@LanguageString(value = "The spatial data source to be reprojected.", lang = "en"),
				@LanguageString(value = "La source de données spatiales pour la reprojection.", lang = "fr")
		],
		dataStoreTypes = ["GEOMETRY"],
		identifier = "orbisgis:wps:official:reprojectGeometries:inputDataStore"
)
String inputDataStore


/**********************/
/** INPUT Parameters **/
/**********************/

/** Name of the Geometric field of the DataStore inputDataStore. */
@DataFieldInput(
		translatedTitles = [
				@LanguageString(value = "Geometric field", lang = "en"),
				@LanguageString(value = "Champ géométrique", lang = "fr")
		],
		translatedResumes = [
				@LanguageString(value = "The geometric field of the data source.", lang = "en"),
				@LanguageString(value = "Le champ géométrique de la source de données.", lang = "fr")
		],
        variableReference = "orbisgis:wps:official:reprojectGeometries:inputDataStore",
        fieldTypes = ["GEOMETRY"],
		identifier = "orbisgis:wps:official:reprojectGeometries:geometryField"
)
String[] geometricField


/** The spatial_ref SRID */
@FieldValueInput(
		translatedTitles = [
				@LanguageString(value = "SRID", lang = "en"),
				@LanguageString(value = "SRID", lang = "fr")
		],
		translatedResumes = [
				@LanguageString(value = "The spatial reference system identifier.", lang = "en"),
				@LanguageString(value = "L'identifiant du système de référence spatiale.", lang = "fr")
		],
		variableReference = "\$public\$spatial_ref_sys\$srid\$",
		multiSelection = false,
		identifier = "orbisgis:wps:official:reprojectGeometries:srid"
)
String[] srid


/** Fields to keep. */
@DataFieldInput(
		translatedTitles = [
				@LanguageString(value = "Fields to keep", lang = "en"),
				@LanguageString(value = "Champs à conserver", lang = "fr")
		],
		translatedResumes = [
				@LanguageString(value = "The fields that will be kept in the output.", lang = "en"),
				@LanguageString(value = "Les champs qui seront conservés dans la table de sortie.", lang = "fr")
		],
		excludedTypes=["GEOMETRY"],
		multiSelection = true,
		minOccurs = 0,
        variableReference = "orbisgis:wps:official:reprojectGeometries:inputDataStore",
		identifier = "orbisgis:wps:official:reprojectGeometries:fieldList"
)
String[] fieldList


@LiteralDataInput(
		translatedTitles = [
				@LanguageString(value = "Output table name", lang = "en"),
				@LanguageString(value = "Nom de la table de sortie", lang = "fr")
		],
		translatedResumes = [
				@LanguageString(value = "Name of the table containing the result of the process.", lang = "en"),
				@LanguageString(value = "Nom de la table contenant les résultats du traitement.", lang = "fr")
		],
		identifier = "orbisgis:wps:official:reprojectGeometries:outputTableName"
)
String outputTableName

/*****************/
/** OUTPUT Data **/
/*****************/

/** String output of the process. */
@LiteralDataOutput(
		translatedTitles = [
				@LanguageString(value = "Output message", lang = "en"),
				@LanguageString(value = "Message de sortie", lang = "fr")
		],
		translatedResumes = [
				@LanguageString(value = "The output message.", lang = "en"),
				@LanguageString(value = "Le message de sortie.", lang = "fr")
		],
		identifier = "orbisgis:wps:official:reprojectGeometries:literalOutput"
)
String literalOutput


