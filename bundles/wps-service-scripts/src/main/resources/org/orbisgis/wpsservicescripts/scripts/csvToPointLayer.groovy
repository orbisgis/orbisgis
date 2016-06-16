package org.orbisgis.wpsservicescripts.scripts

import org.orbisgis.wpsgroovyapi.input.DataFieldInput
import org.orbisgis.wpsgroovyapi.input.EnumerationInput
import org.orbisgis.wpsgroovyapi.input.LiteralDataInput
import org.orbisgis.wpsgroovyapi.input.RawDataInput
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
        keywords = ["OrbisGIS","ST_Transform","ST_SetSRID","ST_MakePoint","example"])
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

    dataStoreOutput = dataStoreOutputName;
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
@RawDataInput(title="Input csv",
        resume="The input CSV file containing the point data.")
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

@LiteralDataInput(title="Id field",
        resume="The point id field")
String idField

@LiteralDataInput(title="X field",
        resume="The X coordinate field")
String xField

@LiteralDataInput(title="Y field",
        resume="The Y coordinate field")
String yField

@EnumerationInput(title="Input EPSG",
        resume="The input .csv EPSG code",
        values=["4326", "2154"],
        minOccurs=0)
Integer inputEPSG

@EnumerationInput(title="Output EPSG",
        resume="The output .csv EPSG code",
        values=["4326", "2154"],
        minOccurs=0)
Integer outputEPSG

/** Output DataStore name. */
@LiteralDataInput(
        title="DataStore name",
        resume="The DataStore name"
)
String dataStoreOutputName

/************/
/** OUTPUT **/
/************/
@DataStoreOutput(title="Output point layer",
        resume="The output point layer.")
String dataStoreOutput
