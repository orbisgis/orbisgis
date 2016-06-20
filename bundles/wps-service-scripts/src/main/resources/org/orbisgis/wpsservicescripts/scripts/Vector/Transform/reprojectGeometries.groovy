package org.orbisgis.orbistoolbox.view.utils.scripts;

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
    String query = "CREATE TABLE "+dataStoreOutput+" AS SELECT ST_TRANSFORM("   
query += geometricField+","+srid[0]
   
    //Build the end of the query
    query += ") AS the_geom ";

if(fieldsList!=null){
query += ", "+ fieldsList;
}

 	query +=  " FROM "+inputDataStore+";"

    //Execute the query
    sql.execute(query)
}

/****************/
/** INPUT Data **/
/****************/

/** This DataStore is the input data source. */
@DataStoreInput(
        title = "Input spatial data",
        resume = "The spatial data source to be reprojected.",
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


/** The spatial_ref SRID */
@FieldValueInput(title="SRID",
resume="The spatial reference system identifier",
dataField = "\$public\$spatial_ref_sys\$srid\$",
multiSelection = false)
String[] srid


/** Fields to keep. */
@DataFieldInput(
        title = "Fields to keep",
        resume = "The fields that will be kept in the ouput",
	excludedTypes=["GEOMETRY"],
	isMultipleField=true,
	minOccurs = 0,
        dataStore = "inputDataStore")
String fieldsList


/*****************/
/** OUTPUT Data **/
/*****************/

/** This DataStore is the output data source. */
@DataStoreOutput(
        title="Reprojected data",
        resume="The output spatial data source to store the new geometries.",
        isSpatial = true)
String dataStoreOutput


