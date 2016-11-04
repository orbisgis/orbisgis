package org.orbisgis.wpsservicescripts.scripts.Vector.Properties

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
        translatedTitles = [
                @LanguageString(value = "Geometry properties", lang = "en"),
                @LanguageString(value = "Propriétés géométriques", lang = "fr")
        ],
        translatedResumes = [
                @LanguageString(value = "Compute some basic geometry properties.", lang = "en"),
                @LanguageString(value = "Calcul des propriétés de base des géométries.", lang = "fr")
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
                        @LanguageString(value = "Properties", lang = "en"),
                        @LanguageString(value = "Propriétés", lang = "fr")
                ])
        ],
        metadata = [
                @MetadataAttribute(title="h2gis", role ="DBMS", href = "http://www.h2gis.org/"),
                @MetadataAttribute(title="postgis", role ="DBMS", href = "http://postgis.net/")
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
        translatedTitles = [
                @LanguageString(value = "Input spatial data", lang = "en"),
                @LanguageString(value = "Données spatiales d'entrée", lang = "fr")
        ],
        translatedResumes = [
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
                @LanguageString(value = "Le champ utilisé comme identifiant.", lang = "fr")
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
                @LanguageString(value = "Operation to compute the properties.", lang = "en"),
                @LanguageString(value = "Opération à effectuer.", lang = "fr")
        ],
        values=["geomtype","srid", "length","perimeter","area", "dimension", "coorddim", "num_geoms", "num_pts", "issimple", "isvalid", "isempty"],
        translatedNames = [
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "Geometry type", lang = "en"),
                        @LanguageString(value = "Type de géométrie", lang = "fr")
                ]),
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "SRID", lang = "en"),
                        @LanguageString(value = "SRID", lang = "fr")
                ]),
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "Length", lang = "en"),
                        @LanguageString(value = "Longueur", lang = "fr")
                ]),
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "Perimeter", lang = "en"),
                        @LanguageString(value = "Périmètre", lang = "fr")
                ]),
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "Area", lang = "en"),
                        @LanguageString(value = "Surface", lang = "fr")
                ]),
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "Geometry dimension", lang = "en"),
                        @LanguageString(value = "Dimension de la géométrie", lang = "fr")
                ]),
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "Coordinate dimension", lang = "en"),
                        @LanguageString(value = "Dimension des coordonnées", lang = "fr")
                ]),
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "Number of geometries", lang = "en"),
                        @LanguageString(value = "Nombre de géométries", lang = "fr")
                ]),
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "Number of points", lang = "en"),
                        @LanguageString(value = "Nombre de points", lang = "fr")
                ]),
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "Is simple", lang = "en"),
                        @LanguageString(value = "Est simple", lang = "fr")
                ]),
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "Is valid", lang = "en"),
                        @LanguageString(value = "Est valide", lang = "fr")
                ]),
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "Is empty", lang = "en"),
                        @LanguageString(value = "Est vide", lang = "fr")
                ])
        ],
        selectedValues = "geomtype",
        multiSelection = true)
String[] operations


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

