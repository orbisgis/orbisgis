package org.orbisgis.wpsservicescripts.scripts.Vector.Convert

import org.orbisgis.wpsgroovyapi.attributes.TranslatableString
import org.orbisgis.wpsgroovyapi.attributes.LanguageString
import org.orbisgis.wpsgroovyapi.attributes.MetadataAttribute
import org.orbisgis.wpsgroovyapi.input.*
import org.orbisgis.wpsgroovyapi.output.*
import org.orbisgis.wpsgroovyapi.process.*

/**
 * This process extract the center of a geometry table using  SQL functions.
 * The user has to specify (mandatory):
 *  - The input spatial data source (DataStore)
 *  - The geometry column (LiteralData)
 *  - The geometry operation (centroid or interior point)
 *  - The output data source (DataStore)
 *
 * @return A datadase table.
 * @author Erwan Bocher
 * @author Sylvain PALOMINOS
 */
@Process(
		translatedTitles = [
				@LanguageString(value = "Extract center", lang = "en"),
				@LanguageString(value = "Extraction du centre", lang = "fr")
		],
		translatedResumes = [
				@LanguageString(value = "Extract the center of a geometry.", lang = "en"),
				@LanguageString(value = "Extraction du centre d'une géométrie.", lang = "fr")
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
						@LanguageString(value = "Extract", lang = "en"),
						@LanguageString(value = "Extraction", lang = "fr")
				]),
				@TranslatableString(translatableStrings = [
						@LanguageString(value = "Center", lang = "en"),
						@LanguageString(value = "Centre", lang = "fr")
				])
		],
		metadata = [
				@MetadataAttribute(title="h2gis", role ="DBMS_TITLE", href = "http://www.h2gis.org/"),
				@MetadataAttribute(title="postgis", role ="DBMS_TITLE", href = "http://postgis.net/")
		])
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
    query += ") AS the_geom ,"+ idField[0]+ " FROM "+inputDataStore+";"

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
				@LanguageString(value = "Extract center", lang = "en"),
				@LanguageString(value = "Extraction du centre", lang = "fr")
		],
		translatedResumes = [
				@LanguageString(value = "Extract the center of a geometry.", lang = "en"),
				@LanguageString(value = "Extraction du centre d'une géométrie.", lang = "fr")
		],
        dataStoreTypes = ["GEOMETRY"])
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
        variableReference = "inputDataStore",
        fieldTypes = ["GEOMETRY"])
String[] geometricField

/** Name of the identifier field of the DataStore inputDataStore. */
@DataFieldInput(
		translatedTitles = [
				@LanguageString(value = "Identifier field", lang = "en"),
				@LanguageString(value = "Champ identifiant", lang = "fr")
		],
		translatedResumes = [
				@LanguageString(value = "A field used as an identifier.", lang = "en"),
				@LanguageString(value = "Champ utilisé comme identifiant.", lang = "fr")
		],
	excludedTypes=["GEOMETRY"],
		variableReference = "inputDataStore")
String[] idField

@EnumerationInput(
		translatedTitles = [
				@LanguageString(value = "Operation", lang = "en"),
				@LanguageString(value = "Opération", lang = "fr")
		],
		translatedResumes = [
				@LanguageString(value = "Operation to extract the points.", lang = "en"),
				@LanguageString(value = "Opération d'extraction des points.", lang = "fr")
		],
        values=["centroid", "interior"],
        names=["Centroid", "Interior"],
        selectedValues = "centroid")
String[] operation


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

