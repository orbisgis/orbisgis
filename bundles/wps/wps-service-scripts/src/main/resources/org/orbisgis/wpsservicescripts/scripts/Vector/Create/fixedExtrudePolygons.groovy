package org.orbisgis.wpsservicescripts.scripts.Vector.Create

import org.orbisgis.wpsgroovyapi.attributes.TranslatableString
import org.orbisgis.wpsgroovyapi.attributes.LanguageString
import org.orbisgis.wpsgroovyapi.attributes.MetadataAttribute
import org.orbisgis.wpsgroovyapi.input.DataFieldInput
import org.orbisgis.wpsgroovyapi.input.DataStoreInput
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
		translatedTitles = [
				@LanguageString(value = "Fixed extrude polygons", lang = "en"),
				@LanguageString(value = "Extrusion de polygones fixe", lang = "fr")
		],
		translatedResumes = [
				@LanguageString(value = "Extrude a polygon and extends it to a 3D representation, returning a geometry collection containing floor, ceiling and wall geometries.", lang = "en"),
				@LanguageString(value = "Extrusion de polygones en l'étendant à une représentation en 3D, retournant une collection de géométries contenant les géométries du sol, du plafond et des murs.", lang = "fr")
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
						@LanguageString(value = "Create", lang = "en"),
						@LanguageString(value = "Création", lang = "fr")
				])
		],
		metadata = [
				@MetadataAttribute(title="H2GIS", role ="DBMS_TYPE", href = "http://www.h2gis.org/")
		])
def processing() {

    //Build the start of the query
    String query = "CREATE TABLE "+outputTableName+" AS SELECT ST_EXTRUDE("+geometricField[0]+","+height+") AS the_geom "

	for(String field : fieldList) {
		if (field != null) {
			query += ", " + field;
		}
	}

	query+=" FROM "+inputDataStore+";"

    //Execute the query
    sql.execute(query)
	literalOutput = "Process done"
}


/****************/
/** INPUT Data **/
/****************/

@DataStoreInput(
		translatedTitles = [
				@LanguageString(value = "Input spatial data", lang = "en"),
				@LanguageString(value = "Données spatiales d'entrée", lang = "fr")
		],
		translatedResumes = [
				@LanguageString(value = "The spatial data source that must be extruded.", lang = "en"),
				@LanguageString(value = "La source de données qui doit etre extrudée.", lang = "fr")
		],
		dataStoreTypes = ["GEOMETRY"])
String inputDataStore

/**********************/
/** INPUT Parameters **/
/**********************/

@DataFieldInput(
		translatedTitles = [
				@LanguageString(value = "Geometric field", lang = "en"),
				@LanguageString(value = "Champ géométrique", lang = "fr")
		],
		translatedResumes = [
				@LanguageString(value = "The geometric field of the data source.", lang = "en"),
				@LanguageString(value = "Le champ géométrique de la source de données.", lang = "fr")
		],
		variableReference = "inputDataStore",
        fieldTypes = ["GEOMETRY"])
String[] geometricField


@LiteralDataInput(
		translatedTitles = [
				@LanguageString(value = "Height of the polygons", lang = "en"),
				@LanguageString(value = "Hauteur des polygones", lang = "fr")
		],
		translatedResumes = [
				@LanguageString(value = "A numeric value to specify the height of all polygon.", lang = "en"),
				@LanguageString(value = "Une valeur numérique définissant la hauteur des polygones.", lang = "fr")
		])
Double height = 1

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
        variableReference = "inputDataStore")
String[] fieldList


@LiteralDataInput(
		translatedTitles = [
				@LanguageString(value = "Output table name", lang = "en"),
				@LanguageString(value = "Nom de la table de sortie", lang = "fr")
		],
		translatedResumes = [
				@LanguageString(value = "Name of the table containing the result of the process.", lang = "en"),
				@LanguageString(value = "Nom de la table contenant les résultats du traitement.", lang = "fr")
		])
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
		])
String literalOutput

