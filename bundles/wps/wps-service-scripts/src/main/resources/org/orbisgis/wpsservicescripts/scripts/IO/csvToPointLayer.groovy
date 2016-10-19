package org.orbisgis.wpsservicescripts.scripts.IO

import org.orbisgis.wpsgroovyapi.attributes.TranslatableString
import org.orbisgis.wpsgroovyapi.attributes.LanguageString
import org.orbisgis.wpsgroovyapi.attributes.MetadataAttribute
import org.orbisgis.wpsgroovyapi.input.EnumerationInput
import org.orbisgis.wpsgroovyapi.input.LiteralDataInput
import org.orbisgis.wpsgroovyapi.input.RawDataInput
import org.orbisgis.wpsgroovyapi.output.LiteralDataOutput
import org.orbisgis.wpsgroovyapi.process.Process

/********************/
/** Process method **/
/********************/

/**
 * This process creates a Point layer from a .CSV file.
 * The user has to specify (mandatory) :
 *  - The input CSV file (DataStore)
 *  - The CSV separators (Enumeration)
 *  - If the field name is on the first line (LiteralData)
 *  - The X field (DataField)
 *  - The Y field (DataField)
 *  - The Output data source (DataStore)
 *
 * The user can specify (optional):
 *  - The input csv EPSG code (Enumeration)
 *  - The output csv EPSG code (Enumeration)
 *
 * @return The point layer data source created from a CSV file.
 *
 * @see http://www.h2gis.org/docs/dev/ST_Transform/
 * @see http://www.h2gis.org/docs/dev/ST_SetSRID/
 * @see http://www.h2gis.org/docs/dev/ST_SeST_MakePointSRID/
 * @author Sylvain PALOMINOS
 */
@Process(translatedTitles = [
                @LanguageString(value = "Point layer from CSV", lang = "en"),
                @LanguageString(value = "Couche ponctuelle depuis un CSV", lang = "fr")
        ],
        translatedResumes = [
                @LanguageString(value = "Creates a point layer from a CSV file containing the id of the point, its X and Y coordinate.", lang = "en"),
                @LanguageString(value = "Création d'une couche ponctuelle depuis un fichier CSV contenant l'identifiant du point ainsi que ses coordonnées X et Y.", lang = "fr")
        ],
        translatedKeywords = [
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "OrbisGIS", lang = "en"),
                        @LanguageString(value = "OrbisGIS", lang = "fr")
                ]),
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "ST_Transform", lang = "en"),
                        @LanguageString(value = "ST_Transform", lang = "fr")
                ]),
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "ST_SetSRID", lang = "en"),
                        @LanguageString(value = "ST_SetSRID", lang = "fr")
                ]),
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "ST_MakePoint", lang = "en"),
                        @LanguageString(value = "ST_MakePoint", lang = "fr")
                ]),
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "example", lang = "en"),
                        @LanguageString(value = "exemple", lang = "fr")
                ]),
        ],
        metadata = [
                @MetadataAttribute(title="h2gis", role ="DBMS", href = "http://www.h2gis.org/")
        ])
def processing() {
    outputTableName = dataStoreOutputName
    //Open the CSV file
    File csvFile = new File(csvDataInput)
    String csvRead = "CSVRead('"+csvFile.absolutePath+"', NULL, 'fieldSeparator="+separator+"')";
    String create = "CREATE TABLE "+outputTableName+"(ID INT PRIMARY KEY, THE_GEOM GEOMETRY)";
    //Execute the SQL query
    if(inputEPSG != null && outputEPSG != null){
        sql.execute(create+" AS SELECT "+idField+", " +
                "ST_TRANSFORM(ST_SETSRID(ST_MakePoint("+xField+", "+yField+"), "+inputEPSG+"), "+outputEPSG+") THE_GEOM FROM "+csvRead+";");
    }
    else{
        sql.execute(create + " AS SELECT "+idField+", ST_MakePoint("+xField+", "+yField+") THE_GEOM FROM "+csvRead+";");
    }

    literalOutput = "Process done"
}

/****************/
/** INPUT Data **/
/****************/

/** This DataStore is the input CSV file containing the points coordinates. It should be formed this way :
 * |ID|X|Y|
 * |--|-|-|
 * |1 |1|1|
 * ........
 * */
@RawDataInput(
        translatedTitles = [
                @LanguageString(value = "Input csv", lang = "en"),
                @LanguageString(value = "Fichier CSV", lang = "fr")
        ],
        translatedResumes = [
                @LanguageString(value = "The input CSV file containing the point data.", lang = "en"),
                @LanguageString(value = "Le fichier CSV d'entrée contenant les données ponctuelles.", lang = "fr")
        ])
String[] csvDataInput


/**********************/
/** INPUT Parameters **/
/**********************/
@EnumerationInput(
        translatedTitles = [
                @LanguageString(value = "CSV separator", lang = "en"),
                @LanguageString(value = "Séparateur CSV", lang = "fr")
        ],
        translatedResumes = [
                @LanguageString(value = "The CSV separator.", lang = "en"),
                @LanguageString(value = "Le séparateur CSV.", lang = "fr")
        ],
        values=[",", "\t", " ", ";"],
        names=["coma", "tabulation", "space", "semicolon"],
        selectedValues = ";",
        isEditable = true)
String[] separator

@LiteralDataInput(
        translatedTitles = [
                @LanguageString(value = "Id field", lang = "en"),
                @LanguageString(value = "Champ identifiant", lang = "fr")
        ],
        translatedResumes = [
                @LanguageString(value = "The point id field.", lang = "en"),
                @LanguageString(value = "Le champ contenant l'identifiant du point.", lang = "fr")
        ])
String idField

@LiteralDataInput(
        translatedTitles = [
                @LanguageString(value = "X field", lang = "en"),
                @LanguageString(value = "Champ X", lang = "fr")
        ],
        translatedResumes = [
                @LanguageString(value = "The X coordinate field.", lang = "en"),
                @LanguageString(value = "Le champ de la coordonnée X.", lang = "fr")
        ])
String xField

@LiteralDataInput(
        translatedTitles = [
                @LanguageString(value = "Y field", lang = "en"),
                @LanguageString(value = "Champ Y", lang = "fr")
        ],
        translatedResumes = [
                @LanguageString(value = "The Y coordinate field.", lang = "en"),
                @LanguageString(value = "Le champ de la coordonnée Y.", lang = "fr")
        ])
String yField

@EnumerationInput(
        translatedTitles = [
                @LanguageString(value = "Input EPSG", lang = "en"),
                @LanguageString(value = "EPSG d'entrée", lang = "fr")
        ],
        translatedResumes = [
                @LanguageString(value = "The input CSV EPSG code.", lang = "en"),
                @LanguageString(value = "Le code EPSG du fichier CSV.", lang = "fr")
        ],
        values=["4326", "2154"],
        minOccurs=0)
String[] inputEPSG

@EnumerationInput(
        translatedTitles = [
                @LanguageString(value = "Output EPSG", lang = "en"),
                @LanguageString(value = "EPSG de sortie", lang = "fr")
        ],
        translatedResumes = [
                @LanguageString(value = "The output .csv EPSG code.", lang = "en"),
                @LanguageString(value = "Le code EPSG de la couche en sortie.", lang = "fr")
        ],
        values=["4326", "2154"],
        minOccurs=0)
String[] outputEPSG

/** Output DataStore name. */
@LiteralDataInput(
        translatedTitles = [
                @LanguageString(value = "DataStore name", lang = "en"),
                @LanguageString(value = "Nom du DataStore", lang = "fr")
        ],
        translatedResumes = [
                @LanguageString(value = "The DataStore name.", lang = "en"),
                @LanguageString(value = "Le nom du DataStore.", lang = "fr")
        ])
String dataStoreOutputName

/************/
/** OUTPUT **/
/************/
@LiteralDataOutput(
        translatedTitles = [
                @LanguageString(value = "Output message", lang = "en"),
                @LanguageString(value = "Message de sortie", lang = "fr")
        ],
        translatedResumes = [
                @LanguageString(value = "The output message.", lang = "en"),
                @LanguageString(value = "Le message de sortie.", lang = "fr")
        ])
String literalDataOutput
