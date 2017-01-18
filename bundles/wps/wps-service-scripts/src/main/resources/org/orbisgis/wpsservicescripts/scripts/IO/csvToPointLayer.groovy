package org.orbisgis.wpsservicescripts.scripts.IO

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
 *  - The input CSV file (JDBCTable)
 *  - The CSV separators (Enumeration)
 *  - If the field name is on the first line (LiteralData)
 *  - The X field (JDBCTableField)
 *  - The Y field (JDBCTableField)
 *  - The Output data source (JDBCTable)
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
@Process(title = ["Point layer from CSV","en","Couche ponctuelle depuis un CSV","fr"],
        description = ["Creates a point layer from a CSV file containing the id of the point, its X and Y coordinate.","en",
                "Création d'une couche ponctuelle depuis un fichier CSV contenant l'identifiant du point ainsi que ses coordonnées X et Y.","fr"],
        keywords = ["OrbisGIS,ST_Transform,ST_SetSRID,ST_MakePoint,example","en",
                "OrbisGIS,ST_Transform,ST_SetSRID,ST_MakePoint,exemple","en"],
        properties = ["DBMS_TYPE","H2GIS"])
def processing() {
    outputTableName = jdbcTableOutputName
    //Open the CSV file
    File csvFile = new File(csvDataInput[0])
    String csvRead = "CSVRead('"+csvFile.absolutePath+"', NULL, 'fieldSeparator="+separator+"')";
    String create = "CREATE TABLE "+outputTableName+"(ID INT PRIMARY KEY, THE_GEOM GEOMETRY)";
    //Execute the SQL query
    if(inputEPSG != null && outputEPSG != null){
        sql.execute(create+" AS SELECT "+idField+", " +
                "ST_TRANSFORM(ST_SETSRID(ST_MakePoint("+xField+", "+yField+"), "+inputEPSG[0]+"), "+outputEPSG[0]+") THE_GEOM FROM "+csvRead+";");
    }
    else{
        sql.execute(create + " AS SELECT "+idField+", ST_MakePoint("+xField+", "+yField+") THE_GEOM FROM "+csvRead+";");
    }

    literalDataOutput = "Process done"
}

/****************/
/** INPUT Data **/
/****************/

/** This JDBCTable is the input CSV file containing the points coordinates. It should be formed this way :
 * |ID|X|Y|
 * |--|-|-|
 * |1 |1|1|
 * ........
 * */
@RawDataInput(
        title = ["Input csv","en","Fichier CSV","fr"],
        description = ["The input CSV file containing the point data.","en",
                "Le fichier CSV d'entrée contenant les données ponctuelles.","fr"],
        fileTypes = ["csv"])
String[] csvDataInput


/**********************/
/** INPUT Parameters **/
/**********************/
@EnumerationInput(
        title = ["CSV separator","en","Séparateur CSV","fr"],
        description = ["The CSV separator.","en",
                "Le séparateur CSV.","fr"],
        values=[",", "\t", " ", ";"],
        names=["coma, tabulation, space, semicolon","en"],
        isEditable = true)
String[] separator = [";"]

@LiteralDataInput(
        title = ["Id field","en","Champ identifiant","fr"],
        description = ["The point id field.","en",
                "Le champ contenant l'identifiant du point.","fr"])
String idField

@LiteralDataInput(
        title = ["X field","en","Champ X","fr"],
        description = ["The X coordinate field.","en",
                "Le champ de la coordonnée X.","fr"])
String xField

@LiteralDataInput(
        title = ["Y field","en","Champ Y","fr"],
        description = ["The Y coordinate field.","en",
                "Le champ de la coordonnée Y.","fr"])
String yField

@EnumerationInput(
        title = ["Input EPSG","en","EPSG d'entrée","fr"],
        description = ["The input CSV EPSG code.","en",
                "Le code EPSG du fichier CSV.","fr"],
        values=["4326", "2154"],
        minOccurs=0)
String[] inputEPSG

@EnumerationInput(
        title = ["Output EPSG","en","EPSG de sortie","fr"],
        description = ["The output .csv EPSG code.","en",
                "Le code EPSG de la couche en sortie.","fr"],
        values=["4326", "2154"],
        minOccurs=0)
String[] outputEPSG

/** Output JDBCTable name. */
@LiteralDataInput(
        title = ["JDBCTable name","en","Nom du JDBCTable","fr"],
        description = ["The JDBCTable name.","en",
                "Le nom du JDBCTable.","fr"])
String jdbcTableOutputName

/************/
/** OUTPUT **/
/************/
@LiteralDataOutput(
        title = ["Output message","en",
                "Message de sortie","fr"],
        description = ["The output message.","en",
                "Le message de sortie.","fr"])
String literalDataOutput
