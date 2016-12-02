package org.orbisgis.wpsservicescripts.scripts.Vector.Operators

import org.orbisgis.wpsgroovyapi.attributes.TranslatableString
import org.orbisgis.wpsgroovyapi.attributes.LanguageString
import org.orbisgis.wpsgroovyapi.attributes.MetadataAttribute
import org.orbisgis.wpsgroovyapi.input.DataFieldInput
import org.orbisgis.wpsgroovyapi.input.DataStoreInput
import org.orbisgis.wpsgroovyapi.input.EnumerationInput
import org.orbisgis.wpsgroovyapi.input.LiteralDataInput
import org.orbisgis.wpsgroovyapi.output.LiteralDataOutput
import org.orbisgis.wpsgroovyapi.process.Process

/********************/
/** Process method **/
/********************/

/**
 * This process execute a buffer on a spatial data source using the ST_Buffer().
 * The user has to specify (mandatory):
 *  - The input spatial data source (DataStore)
 *  - The BufferSize (FieldData)
 *  - The output data source (DataStore)
 *
 * The user can specify (optional) :
 *  - The number of segments used to approximate a quarter circle (LiteralData)
 *  - The endcap style (Enumeration)
 *  - The join style (Enumeration)
 *  - The mitre ratio limit (only affects mitered join style) (LiteralData)
 *
 * @return A datadase table.
 * @see http://www.h2gis.org/docs/dev/ST_Buffer/
 * @author Sylvain PALOMINOS
 * @author Erwan BOCHER
 */
@Process(
        translatedTitles = [
                @LanguageString(value = "Variable distance buffer", lang = "en"),
                @LanguageString(value = "Buffer à distance variable", lang = "fr")
        ],
        translatedResumes = [
                @LanguageString(value = "Execute a buffer on a geometric field using another field to specify the distance.", lang = "en"),
                @LanguageString(value = "Génère une zone tampon sur un champ géométrique en utilisant un autre champ pour définir la distance.", lang = "fr")
        ],
        translatedKeywords = [
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "Vector", lang = "en"),
                        @LanguageString(value = "Vecteur", lang = "fr")
                ]),
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "Geometry", lang = "en"),
                        @LanguageString(value = "Géometrie", lang = "fr")
                ])
        ],
        metadata = [
                @MetadataAttribute(title="H2GIS", role ="DBMS_TYPE", href = "http://www.h2gis.org/"),
                @MetadataAttribute(title="POSTGIS", role ="DBMS_TYPE", href = "http://postgis.net/")
        ])
def processing() {

    //Build the start of the query
    String query = "CREATE TABLE "+outputTableName+" AS SELECT ST_Buffer("+geometricField[0]+","+bufferSize[0]
    //Build the third optional parameter
    String optionalParameter = "";
    //If quadSegs is defined
    if(quadSegs != null){
        optionalParameter += "quad_segs="+quadSegs+" "
    }
    //If endcapStyle is defined
    if(endcapStyle != null){
        optionalParameter += "endcap="+endcapStyle[0]+" "
    }
    //If joinStyle is defined
    if(joinStyle != null){
        optionalParameter += "join="+joinStyle[0]+" "
    }
    //If mitreLimit is defined
    if(mitreLimit != null){
        optionalParameter += "mitre_limit="+mitreLimit+" "
    }
    //If optionalParameter is not empty, add it to the request
    if(!optionalParameter.isEmpty()){
        query += ",'"+optionalParameter+"'";
    }
    //Build the end of the query
    query += ") AS the_geom";

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

/** This DataStore is the input data source for the buffer. */
@DataStoreInput(
        translatedTitles = [
                @LanguageString(value = "Input spatial data", lang = "en"),
                @LanguageString(value = "Données spatiales d'entrée", lang = "fr")
        ],
        translatedResumes = [
                @LanguageString(value = "The spatial data source for the buffer.", lang = "en"),
                @LanguageString(value = "La source de données spatiales pour le tampon.", lang = "fr")
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


@DataFieldInput(
        translatedTitles = [
                @LanguageString(value = "Size field", lang = "en"),
                @LanguageString(value = "Champ taille", lang = "fr")
        ],
        translatedResumes = [
                @LanguageString(value = "A numeric field to specify the size of the buffer.", lang = "en"),
                @LanguageString(value = "Champ numérique contenant les tailles de tampon.", lang = "fr")
        ],
        variableReference = "inputDataStore",
        fieldTypes = ["DOUBLE", "INTEGER", "LONG"])
String[] bufferSize

/** Mitre ratio limit (only affects mitered join style). */
@LiteralDataInput(
        translatedTitles = [
                @LanguageString(value = "Mitre limit", lang = "en"),
                @LanguageString(value = "Limite de mitre", lang = "fr")
        ],
        translatedResumes = [
                @LanguageString(value = "Mitre ratio limit (only affects mitered join style)", lang = "en"),
                @LanguageString(value = "Le rapport limite de mitre. (Utilisé uniquement pour le style de jointure mitre)", lang = "fr")
        ],
        minOccurs = 0)
Double mitreLimit = 5.0

/** Number of segments used to approximate a quarter circle. */
@LiteralDataInput(
        translatedTitles = [
                @LanguageString(value = "Segment number for a quarter circle", lang = "en"),
                @LanguageString(value = "Nombre de segment pour un quart de cercle", lang = "fr")
        ],
        translatedResumes = [
                @LanguageString(value = "Number of segments used to approximate a quarter circle.", lang = "en"),
                @LanguageString(value = "Le nombre de segments utilisé pour approximer un quart de cercle.", lang = "fr")
        ],
        minOccurs = 0)
Integer quadSegs = 8

/** Endcap style. */
@EnumerationInput(
        translatedTitles = [
                @LanguageString(value = "Endcap style", lang = "en"),
                @LanguageString(value = "Style de l'extrémité", lang = "fr")
        ],
        translatedResumes = [
                @LanguageString(value = "The endcap style.", lang = "en"),
                @LanguageString(value = "Le style de l'extrémité.", lang = "fr")
        ],
        values=["round", "flat", "butt", "square"],
        selectedValues = ["round"],
        minOccurs = 0)
String[] endcapStyle

/** Join style. */
@EnumerationInput(
        translatedTitles = [
                @LanguageString(value = "Join style", lang = "en"),
                @LanguageString(value = "Style de jointure", lang = "fr")
        ],
        translatedResumes = [
                @LanguageString(value = "The join style.", lang = "en"),
                @LanguageString(value = "Le style de jointure.", lang = "fr")
        ],
        values=["round", "mitre", "miter", "bevel"],
        selectedValues=["round"],
        minOccurs = 0)
String[] joinStyle

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

