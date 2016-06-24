package org.orbisgis.wpsservice.scripts

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
@Process(title = "Variable distance buffer",
        resume = "Execute a buffer on a geometric field using another field to specify the distance.",
        keywords = ["Vector","Geometry"])
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
        title = "Input spatial data",
        resume = "The spatial data source for the buffer",
        dataStoreTypes = ["GEOMETRY"])
String inputDataStore

/**********************/
/** INPUT Parameters **/
/**********************/

@DataFieldInput(
        title = "Geometric field",
        resume = "The geometric field of the data source",
        dataStoreTitle = "Input spatial data",
        fieldTypes = ["GEOMETRY"])
String[] geometricField


@DataFieldInput(
        title = "Size field",
        resume = "A numeric field to specify the size of the buffer",
        dataStoreTitle = "Input spatial data",
        fieldTypes = ["DOUBLE", "INTEGER", "LONG"])
String[] bufferSize

/** Mitre ratio limit (only affects mitered join style). */
@LiteralDataInput(
        title="Mitre limit",
        resume="Mitre ratio limit (only affects mitered join style)",
        minOccurs = 0)
Double mitreLimit = 5.0

/** Number of segments used to approximate a quarter circle. */
@LiteralDataInput(
        title="Segment number for a quarter circle",
        resume="Number of segments used to approximate a quarter circle",
        minOccurs = 0)
Integer quadSegs = 8

/** Endcap style. */
@EnumerationInput(
        title="Endcap style",
        resume="The endcap style",
        values=["round", "flat", "butt", "square"],
        selectedValues = ["round"],
        minOccurs = 0)
String endcapStyle

/** Join style. */
@EnumerationInput(
        title="Join style",
        resume="The join style",
        values=["round", "mitre", "miter", "bevel"],
        selectedValues=["round"],
        minOccurs = 0)
String joinStyle

/** Fields to keep. */
@DataFieldInput(
        title = "Fields to keep",
        resume = "The fields that will be kept in the ouput",
        excludedTypes=["GEOMETRY"],
        multiSelection = true,
        minOccurs = 0,
        dataStoreTitle = "Input spatial data")
String[] fieldList


@LiteralDataInput(
        title="Output table name",
        resume="Name of the table containing the result of the process.")
String outputTableName

/*****************/
/** OUTPUT Data **/
/*****************/

/** String output of the process. */
@LiteralDataOutput(
        title="Output message",
        resume="The output message")
String literalOutput

