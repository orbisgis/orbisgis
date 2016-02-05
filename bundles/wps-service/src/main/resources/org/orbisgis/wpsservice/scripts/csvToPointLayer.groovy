package org.orbisgis.wpsservice.scripts

import org.orbisgis.wpsgroovyapi.input.DataFieldInput
import org.orbisgis.wpsgroovyapi.input.DataStoreInput
import org.orbisgis.wpsgroovyapi.input.EnumerationInput
import org.orbisgis.wpsgroovyapi.output.DataStoreOutput
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
@Process(title = "Point layer from CSV",
        resume = "Creates a point layer from a CSV file containing the id of the point, its X and Y coordinate.",
        keywords = "OrbisGIS,ST_Transform,ST_SetSRID,ST_MakePoint,example")
def processing() {
    outputTableName = dataStoreOutput
    //Open the CSV file
    File csvFile = new File(csvDataInput)
    String csvRead = "CSVRead('"+csvFile.absolutePath+"', NULL, 'fieldSeparator="+separator+"')";
    String create = "CREATE TABLE "+outputTableName+"(ID INT PRIMARY KEY, THE_GEOM GEOMETRY)";
    //Execute the SQL query
    sql.execute("DROP TABLE IF EXISTS " + outputTableName + ";")
    if(inputEPSG != null && outputEPSG != null){
        sql.execute(create+" AS SELECT "+idField+", " +
                "ST_TRANSFORM(ST_SETSRID(ST_MakePoint("+xField+", "+yField+"), "+inputEPSG+"), "+outputEPSG+") THE_GEOM FROM "+csvRead+";");
    }
    else{
        sql.execute(create + " AS SELECT "+idField+", ST_MakePoint("+xField+", "+yField+") THE_GEOM FROM "+csvRead+";");
    }
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
@DataStoreInput(title="Input csv",
        resume="The input CSV file containing the point data.",
        extensions = "csv",
        isSpatial = false,
        isCreateTable = false)
String csvDataInput


/**********************/
/** INPUT Parameters **/
/**********************/
@EnumerationInput(title="CSV separator",
        resume="The CSV separator.",
        values=[",", "\t", " ", ";"],
        names=["coma", "tabulation", "space", "semicolon"],
        defaultValues = ";",
        isEditable = true)
String separator

@DataFieldInput(title="Id field",
        resume="The point id field",
        dataStore="csvDataInput")
String idField

@DataFieldInput(title="X field",
        resume="The X coordinate field",
        dataStore="csvDataInput")
String xField

@DataFieldInput(title="Y field",
        resume="The Y coordinate field",
        dataStore="csvDataInput")
String yField

@EnumerationInput(title="Input EPSG",
        resume="The input .csv EPSG code",
        values=["4326", "2154"],
        minOccurs=0)
Integer inputEPSG

@EnumerationInput(title="Output EPSG"
        , resume="The output .csv EPSG code",
        values=["4326", "2154"],
        minOccurs=0)
Integer outputEPSG

/** OUTPUT **/
@DataStoreOutput(title="Output point layer",
        resume="The output point layer.")
String dataStoreOutput
