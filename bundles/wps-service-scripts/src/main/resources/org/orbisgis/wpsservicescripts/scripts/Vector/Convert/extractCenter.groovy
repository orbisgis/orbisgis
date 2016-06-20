package org.orbisgis.orbistoolbox.view.utils.scripts;

import org.orbisgis.wpsgroovyapi.input.*
import org.orbisgis.wpsgroovyapi.output.*
import org.orbisgis.wpsgroovyapi.process.*

/**
 * This process extract the center of a geometry table using  SQL functions.
 * The user has to specify (mandatory):
 *  - The input spatial data source (DataStore)
 *  - The geometry column (LiteralData)
 *  - The geometry operation (centroid or interior point)
 *  - The output data source (DataStore)
 *
 * @return A datadase table.
 * @author Erwan Bocher
 */
@Process(title = "Extract center",
        resume = "Extract the center of a geometry.",
        keywords = "Vector,Geometry,Extract,Center")
def processing() {
	//Build the start of the query
    	String query = "CREATE TEMPORARY TABLE "+dataStoreOutput+" AS SELECT "
   

	if(operation.equalsIgnoreCase("centroid")){
query += " ST_Centroid("+geometricField+""
}
else{
query += " ST_PointOnSurface("+geometricField+""
}    
    //Build the end of the query
    query += ") AS the_geom ,"+ idField+ " FROM "+inputDataStore+";"

    //Execute the query
    sql.execute(query)
}


/****************/
/** INPUT Data **/
/****************/

/** This DataStore is the input data source. */
@DataStoreInput(
        title = "Input spatial data",
        resume = "The spatial data source to extract the centers.",
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

/** Name of the identifier field of the DataStore inputDataStore. */
@DataFieldInput(
        title = "Identifier field",
        resume = "A field used as an identifier",
	excludedTypes=["GEOMETRY"],
        dataStore = "inputDataStore")
String idField

@EnumerationInput(title="Operation",
        resume="Operation to extract the points.",
        values=["centroid", "interior"],
        names=["Centroid", "Interior"],
        defaultValues = "centroid")
String operation




/*****************/
/** OUTPUT Data **/
/*****************/

/** This DataStore is the output data source. */
@DataStoreOutput(
        title="Points",
        resume="The output spatial data source to store the center of the geometries.",
        isSpatial = true)
String dataStoreOutput

