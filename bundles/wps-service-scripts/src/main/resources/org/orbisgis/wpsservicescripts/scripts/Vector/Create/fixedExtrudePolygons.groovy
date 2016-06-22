package org.orbisgis.wpsservice.scripts

import org.orbisgis.wpsgroovyapi.input.DataFieldInput
import org.orbisgis.wpsgroovyapi.input.DataStoreInput
import org.orbisgis.wpsgroovyapi.input.EnumerationInput
import org.orbisgis.wpsgroovyapi.input.LiteralDataInput
import org.orbisgis.wpsgroovyapi.output.DataStoreOutput
import org.orbisgis.wpsgroovyapi.output.LiteralDataOutput
import org.orbisgis.wpsgroovyapi.process.Process

/********************/
/** Process method **/
/********************/

/**
 * This process is used to extrude 3D polygons.
 *
 * @return A datadase table.
 * @author Erwan BOCHER
 */
@Process(title = "Fixed extrude polygons.",
        resume = "Extrude a polygon and extends it to a 3D representation, returning a geometry collection containing floor, ceiling and wall geometries.",
        keywords = "Vector,Geometry,Create")
def processing() {

    //Build the start of the query
    String query = "CREATE TABLE "+outputTableName+" AS SELECT ST_EXTRUDE("+geometricField+","+height+") AS the_geom "
    
    if(fieldsList!=null){
		query += ", "+ fieldsList;
	}

	query+=" FROM "+inputDataStore+";"

    //Execute the query
    sql.execute(query)
	literalOutput = "Process done"
}


/****************/
/** INPUT Data **/
/****************/

@DataStoreInput(
        title = "Input spatial data",
        resume = "The spatial data source that must be extruded.",
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
String geometricField


@LiteralDataInput(
        title = "Height of the polygons",
        resume = "A numeric value to specify the height of all polygon.")
Double height = 1

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

