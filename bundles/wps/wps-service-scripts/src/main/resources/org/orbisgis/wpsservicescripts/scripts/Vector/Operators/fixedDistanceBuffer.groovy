package org.orbisgis.wpsservicescripts.scripts.Vector.Operators

import org.orbisgis.wpsgroovyapi.attributes.Keyword
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
 * This process execute a buffer on a spatial data source using the ST_Buffer() function.
 * The user has to specify (mandatory):
 *  - The input spatial data source (DataStore)
 *  - The BufferSize (LiteralData)
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
        traducedTitles = [
                @LanguageString(value = "Fixed distance buffer", lang = "en"),
                @LanguageString(value = "Buffer à distance fixe", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "Execute a buffer on a geometric field with a constant distance.", lang = "en"),
                @LanguageString(value = "Génère une zone tampon sur un champ géométrique avec une distance constante.", lang = "fr")
        ],
        traducedKeywords = [
                @Keyword(traducedKeywords = [
                        @LanguageString(value = "Vector", lang = "en"),
                        @LanguageString(value = "Vecteur", lang = "fr")
                ]),
                @Keyword(traducedKeywords = [
                        @LanguageString(value = "Geometry", lang = "en"),
                        @LanguageString(value = "Géometrie", lang = "fr")
                ])
        ],
        metadata = [
                @MetadataAttribute(title="h2gis", role ="DBMS", href = "http://www.h2gis.org/"),
                @MetadataAttribute(title="postgis", role ="DBMS", href = "http://postgis.net/")
        ])
def processing() {

    //Build the start of the query
    String query = "CREATE TABLE "+outputTableName+" AS SELECT ST_Buffer("+geometricField+","+bufferSize
    //Build the third optional parameter
    String optionalParameter = "";
    //If quadSegs is defined
    if(quadSegs != null){
        optionalParameter += "quad_segs="+quadSegs+" "
    }
    //If endcapStyle is defined
    if(endcapStyle != null){
        optionalParameter += "endcap="+endcapStyle+" "
    }
    //If joinStyle is defined
    if(joinStyle != null){
        optionalParameter += "join="+joinStyle+" "
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
        traducedTitles = [
                @LanguageString(value = "Input spatial data", lang = "en"),
                @LanguageString(value = "Données spatiales d'entrée", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "The spatial data source for the buffer.", lang = "en"),
                @LanguageString(value = "La source de données spatiales pour le tampon.", lang = "fr")
        ],
        dataStoreTypes = "GEOMETRY")
String inputDataStore

/**********************/
/** INPUT Parameters **/
/**********************/

/** Name of the Geometric field of the DataStore inputDataStore. */
@DataFieldInput(
        traducedTitles = [
                @LanguageString(value = "Geometric field", lang = "en"),
                @LanguageString(value = "Champ géométrique", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "The geometric field of the data source.", lang = "en"),
                @LanguageString(value = "Le champ géométrique de la source de données.", lang = "fr")
        ],
        variableReference = "inputDataStore",
        fieldTypes = ["GEOMETRY"])
String geometricField

/** Size of the buffer. */
@LiteralDataInput(
        traducedTitles = [
                @LanguageString(value = "Buffer Size", lang = "en"),
                @LanguageString(value = "Taille du tampon", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "The buffer size.", lang = "en"),
                @LanguageString(value = "La taille du tampon.", lang = "fr")
        ])
Double bufferSize 

/** Mitre ratio limit (only affects mitered join style). */
@LiteralDataInput(
        traducedTitles = [
                @LanguageString(value = "Mitre limit", lang = "en"),
                @LanguageString(value = "Limite de mitre", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "Mitre ratio limit (only affects mitered join style)", lang = "en"),
                @LanguageString(value = "Le rapport limite de mitre. (Utilisé uniquement pour le style de jointure mitre)", lang = "fr")
        ],
        minOccurs = 0)
Double mitreLimit = 5.0

/** Number of segments used to approximate a quarter circle. */
@LiteralDataInput(
        traducedTitles = [
                @LanguageString(value = "Segment number for a quarter circle", lang = "en"),
                @LanguageString(value = "Nombre de segment pour un quart de cercle", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "Number of segments used to approximate a quarter circle.", lang = "en"),
                @LanguageString(value = "Le nombre de segments utilisé pour approximer un quart de cercle.", lang = "fr")
        ],
        minOccurs = 0)
Integer quadSegs = 8

/** Endcap style. */
@EnumerationInput(
        traducedTitles = [
                @LanguageString(value = "Endcap style", lang = "en"),
                @LanguageString(value = "Style de l'extrémité", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "The endcap style.", lang = "en"),
                @LanguageString(value = "Le style de l'extrémité.", lang = "fr")
        ],
        values=["round", "flat", "butt", "square"],
        selectedValues = ["round"],
        minOccurs = 0)
String endcapStyle

/** Join style. */
@EnumerationInput(
        traducedTitles = [
                @LanguageString(value = "Join style", lang = "en"),
                @LanguageString(value = "Style de jointure", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "The join style.", lang = "en"),
                @LanguageString(value = "Le style de jointure.", lang = "fr")
        ],
        values=["round", "mitre", "miter", "bevel"],
        selectedValues = ["round"],
        minOccurs = 0)
String joinStyle

/** Fields to keep. */
@DataFieldInput(
        traducedTitles = [
                @LanguageString(value = "Fields to keep", lang = "en"),
                @LanguageString(value = "Champs à conserver", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "The fields that will be kept in the output.", lang = "en"),
                @LanguageString(value = "Les champs qui seront conservés dans la table de sortie.", lang = "fr")
        ],
        excludedTypes=["GEOMETRY"],
        multiSelection = true,
        minOccurs = 0,
        variableReference = "inputDataStore")
String[] fieldList


@LiteralDataInput(
        traducedTitles = [
                @LanguageString(value = "Output table name", lang = "en"),
                @LanguageString(value = "Nom de la table de sortie", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "Name of the table containing the result of the process.", lang = "en"),
                @LanguageString(value = "Nom de la table contenant les résultats du traitement.", lang = "fr")
        ])
String outputTableName

/*****************/
/** OUTPUT Data **/
/*****************/

/** String output of the process. */
@LiteralDataOutput(
        traducedTitles = [
                @LanguageString(value = "Output message", lang = "en"),
                @LanguageString(value = "Message de sortie", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "The output message.", lang = "en"),
                @LanguageString(value = "Le message de sortie.", lang = "fr")
        ])
String literalOutput

