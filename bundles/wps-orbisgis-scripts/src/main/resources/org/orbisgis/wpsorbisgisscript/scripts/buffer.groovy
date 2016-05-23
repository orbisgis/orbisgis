//TODO : header

import org.orbisgis.wpsgroovyapi.input.DataFieldInput
import org.orbisgis.wpsgroovyapi.input.DataStoreInput
import org.orbisgis.wpsgroovyapi.input.EnumerationInput
import org.orbisgis.wpsgroovyapi.input.LiteralDataInput
import org.orbisgis.wpsgroovyapi.output.DataStoreOutput
import org.orbisgis.wpsgroovyapi.process.Process

/********************/
/** Process method **/
/********************/

/**
 * This process execute a buffer on a spatial data source using the ST_Buffer() function from H2GIS.
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
 */
@Process(title = "Buffer",
        resume = "Execute a buffer on a geometric field.",
        keywords = "OrbisGIS,ST_Buffer,example")
def processing() {

    //Build the start of the query
    String query = "CREATE TABLE "+dataStoreOutput+" AS SELECT ST_Buffer("+geometricField+","+bufferSize
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
    query += ") AS the_geom FROM "+inputDataStore+";"

    //Execute the query
    sql.execute(query)
}


/****************/
/** INPUT Data **/
/****************/

/** This DataStore is the input data source for the buffer. */
@DataStoreInput(
        title = "Input spatial data",
        resume = "The spatial data source for the buffer",
        isSpatial = true)
String inputDataStore

/**********************/
/** INPUT Parameters **/
/**********************/

/** Name of the Geometric field of the DataStore inputDataStore. */
@DataFieldInput(
        title = "Geometric field",
        resume = "The geometric field of the data source",
        dataStore = "inputDataStore",
        fieldTypes = ["GEOMETRY"])
String geometricField

/** Size of the buffer. */
@LiteralDataInput(
        title="Buffer Size",
        resume="The buffer size")
Double bufferSize

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
        defaultValues=["round"],
        minOccurs = 0)
String endcapStyle

/** Join style. */
@EnumerationInput(
        title="Join style",
        resume="The join style",
        values=["round", "mitre", "miter", "bevel"],
        defaultValues=["round"],
        minOccurs = 0)
String joinStyle

/*****************/
/** OUTPUT Data **/
/*****************/

/** This DataStore is the output data source for the buffer. */
@DataStoreOutput(
        title="Output Data",
        resume="The output spatial data source of the buffer",
        isSpatial = true)
String dataStoreOutput

