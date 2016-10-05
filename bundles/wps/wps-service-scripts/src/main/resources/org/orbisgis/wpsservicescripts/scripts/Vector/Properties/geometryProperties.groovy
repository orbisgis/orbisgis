package org.orbisgis.wpsservicescripts.scripts.Vector.Properties

import org.orbisgis.wpsgroovyapi.attributes.Keyword
import org.orbisgis.wpsgroovyapi.attributes.LanguageString
import org.orbisgis.wpsgroovyapi.input.*
import org.orbisgis.wpsgroovyapi.output.*
import org.orbisgis.wpsgroovyapi.process.*

/********************/
/** Process method **/
/********************/



/**
 * This process extract the center of a geometry table using the SQL function.
 * The user has to specify (mandatory):
 *  - The input spatial data source (DataStore)
 *  - The geometry column (LiteralData)
 *  - A column identifier (LiteralData)
 *  - The geometry operations (centroid or interior point)
 *  - The output data source (DataStore)
 *
 * @return A database table or a file.
 * @author Erwan Bocher
 * @author Sylvain PALOMINOS
 */
@Process(
        traducedTitles = [
                @LanguageString(value = "Geometry properties", lang = "en"),
                @LanguageString(value = "Propriétés géométriques", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "Compute some basic geometry properties.", lang = "en"),
                @LanguageString(value = "Calcul des propriétés de base des géométries.", lang = "fr")
        ],
        traducedKeywords = [
                @Keyword(traducedKeywords = [
                        @LanguageString(value = "Vector", lang = "en"),
                        @LanguageString(value = "Vecteur", lang = "fr")
                ]),
                @Keyword(traducedKeywords = [
                        @LanguageString(value = "Geometry", lang = "en"),
                        @LanguageString(value = "Géometrie", lang = "fr")
                ]),
                @Keyword(traducedKeywords = [
                        @LanguageString(value = "Properties", lang = "en"),
                        @LanguageString(value = "Propriétés", lang = "fr")
                ])
        ])
def processing() {
//Build the start of the query
    String query = "CREATE TABLE "+outputTableName+" AS SELECT "
   
    for (String operation : operations) {
        if(operation.equals("geomtype")){
            query += " ST_GeometryType("+geometricField[0]+") as geomType,"
        }
        else if(operation.equals("srid")){
            query += " ST_SRID("+geometricField[0]+") as srid,"
        }
        else if(operation.equals("length")){
            query += " ST_Length("+geometricField[0]+") as length,"
        }
        else if(operation.equals("perimeter")){
            query += " ST_Perimeter("+geometricField[0]+") as perimeter,"
        }
        else if(operation.equals("area")){
            query += " ST_Area("+geometricField[0]+") as area,"
        }
        else if(operation.equals("dimension")){
            query += " ST_Dimension("+geometricField[0]+") as dimension,"
        }
        else if(operation.equals("coorddim")){
            query += " ST_Coorddim("+geometricField[0]+") as coorddim,"
        }
        else if(operation.equals("num_geoms")){
            query += " ST_NumGeometries("+geometricField[0]+") as numGeometries,"
        }
        else if(operation.equals("num_pts")){
            query += " ST_NPoints("+geometricField[0]+") as numPts,"
        }
        else if(operation.equals("issimple")){
            query += " ST_Issimple("+geometricField[0]+") as issimple,"
        }
        else if(operation.equals("isvalid")){
            query += " ST_Isvalid("+geometricField[0]+") as isvalid,"
        }
        else if(operation.equals("isempty")){
            query += " ST_Isempty("+geometricField[0]+") as isempty,"
        }
    }


    //Add the field id
    query += idField[0] + " FROM "+inputDataStore+";"

    //Execute the query
    sql.execute(query)
    literalOutput = "Process done"
}


/****************/
/** INPUT Data **/
/****************/

/** This DataStore is the input data source. */
@DataStoreInput(
        traducedTitles = [
                @LanguageString(value = "Input spatial data", lang = "en"),
                @LanguageString(value = "Données spatiales d'entrée", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "The spatial data source to compute the geometry properties.", lang = "en"),
                @LanguageString(value = "La source de données spatiales pour le calcul des propriétés géométriques.", lang = "fr")
        ],
        dataStoreTypes = ["GEOMETRY"])
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
String[] geometricField

/** Name of the identifier field of the DataStore inputDataStore. */
@DataFieldInput(
        traducedTitles = [
                @LanguageString(value = "Identifier field", lang = "en"),
                @LanguageString(value = "Champ identifiant", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "A field used as an identifier.", lang = "en"),
                @LanguageString(value = "Le champ utilisé comme identifiant.", lang = "fr")
        ],
	    excludedTypes=["GEOMETRY"],
        variableReference = "inputDataStore")
String[] idField

@EnumerationInput(
        traducedTitles = [
                @LanguageString(value = "Operation", lang = "en"),
                @LanguageString(value = "Opération", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "Operation to compute the properties.", lang = "en"),
                @LanguageString(value = "Opération à effectuer.", lang = "fr")
        ],
        values=["geomtype","srid", "length","perimeter","area", "dimension", "coorddim", "num_geoms", "num_pts", "issimple", "isvalid", "isempty"],
        names=["Geometry type","SRID", "Length", "Perimeter", "Area", "Geometry dimension","Coordinate dimension", "Number of geometries", "Number of points", "Is simple", "Is valid", "Is empty" ],
        selectedValues = "geomtype",
        multiSelection = true)
String[] operations


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

