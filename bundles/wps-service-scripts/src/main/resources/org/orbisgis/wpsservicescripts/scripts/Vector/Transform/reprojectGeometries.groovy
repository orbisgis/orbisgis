package org.orbisgis.wpsservice.scripts

import org.orbisgis.wpsgroovyapi.input.*
import org.orbisgis.wpsgroovyapi.output.*
import org.orbisgis.wpsgroovyapi.process.*

/********************/
/** Process method **/
/********************/



/**
 * This process reproject a geometry table using the SQL function.
 * The user has to specify (mandatory):
 *  - The input spatial data source (DataStore)
 *  - The geometry column (LiteralData)
 *  - The SRID value selected from the spatial_ref table
 *  - The output data source (DataStore)
 *
 * @return A database table or a file.
 * @author Erwan Bocher
 */
@Process(title = "Reproject geometries",
        resume = "Reproject geometries from one Coordinate Reference System to another.",
        keywords = "Vector,Geometry,Reproject")
def processing() {
//Build the start of the query
    String query = "CREATE TABLE "+outputTableName+" AS SELECT ST_TRANSFORM("
query += geometricField+","+srid[0]
   
    //Build the end of the query
    query += ") AS the_geom ";

if(fieldsList!=null){
query += ", "+ fieldsList;
}

 	query +=  " FROM "+inputDataStore+";"

    //Execute the query
    sql.execute(query)
	literalOutput = "Process done"
}

/****************/
/** INPUT Data **/
/****************/

/** This DataStore is the input data source. */
@DataStoreInput(
        title = "Input spatial data",
        resume = "The spatial data source to be reprojected.",
		dataStoreTypes = ["GEOMETRY"])
String inputDataStore


/**********************/
/** INPUT Parameters **/
/**********************/

/** Name of the Geometric field of the DataStore inputDataStore. */
@DataFieldInput(
        title = "Geometric field",
        resume = "The geometric field of the data source",
        dataStoreTitle = "Input spatial data",
        fieldTypes = ["GEOMETRY"])
String geometricField


/** The spatial_ref SRID */
@FieldValueInput(title="SRID",
		resume="The spatial reference system identifier",
		dataFieldTitle = "\$public\$spatial_ref_sys\$srid\$",
		multiSelection = false)
String[] srid


/** Fields to keep. */
@DataFieldInput(
        title = "Fields to keep",
        resume = "The fields that will be kept in the ouput",
		excludedTypes=["GEOMETRY"],
		multiSelection = true,
		minOccurs = 0,
        dataStoreTitle = "Input spatial data")
String fieldsList


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


