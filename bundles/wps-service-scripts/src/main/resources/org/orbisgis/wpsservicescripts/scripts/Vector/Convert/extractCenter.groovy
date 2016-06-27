package org.orbisgis.wpsservice.scripts

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
        keywords = ["Vector","Geometry","Extract","Center"])
def processing() {
	//Build the start of the query
	String query = "CREATE TEMPORARY TABLE "+outputTableName+" AS SELECT "
   

	if(operation[0].equalsIgnoreCase("centroid")){
		query += " ST_Centroid("+geometricField[0]+""
	}
	else{
		query += " ST_PointOnSurface("+geometricField[0]+""
	}
    //Build the end of the query
    query += ") AS the_geom ,"+ idField[0]+ " FROM "+inputDataStore+";"

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
        resume = "The spatial data source to extract the centers.",
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
String[] geometricField

/** Name of the identifier field of the DataStore inputDataStore. */
@DataFieldInput(
        title = "Identifier field",
        resume = "A field used as an identifier",
	excludedTypes=["GEOMETRY"],
		dataStoreTitle = "Input spatial data")
String[] idField

@EnumerationInput(title="Operation",
        resume="Operation to extract the points.",
        values=["centroid", "interior"],
        names=["Centroid", "Interior"],
        selectedValues = "centroid")
String[] operation


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

