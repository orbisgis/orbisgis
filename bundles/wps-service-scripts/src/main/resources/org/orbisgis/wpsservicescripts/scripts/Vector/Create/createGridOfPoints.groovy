package org.orbisgis.wpsservice.scripts

import org.orbisgis.wpsgroovyapi.input.DataStoreInput
import org.orbisgis.wpsgroovyapi.input.LiteralDataInput
import org.orbisgis.wpsgroovyapi.output.LiteralDataOutput
import org.orbisgis.wpsgroovyapi.process.Process

/********************/
/** Process method **/
/********************/

/**
 * This process is used to create a grid of points.
 *
 * @return A datadase table.
 * @author Erwan BOCHER
 */
@Process(title = "Create a grid of points.",
        resume = "Create a grid of points.",
        keywords = "Vector,Geometry,Create")
def processing() {

    //Build the start of the query
    String query = "CREATE TABLE "+outputTableName+" AS SELECT * from ST_MakeGridPoints('"+inputDataStore+"',"+x_distance+","+y_distance+")"
    
    //Execute the query
    sql.execute(query)
    literalOutput = "Process done"
}


/****************/
/** INPUT Data **/
/****************/

@DataStoreInput(
        title = "Input spatial data",
        resume = "The spatial data source to compute the grid. The extend of grid is based on the full extend of the table.",
        dataStoreTypes = ["GEOMETRY"])
String inputDataStore

/**********************/
/** INPUT Parameters **/
/**********************/

@LiteralDataInput(
        title="X cell size",
        resume="The X cell size")
Double x_distance =1

@LiteralDataInput(
        title="Y cell size",
        resume="The Y cell size")
Double y_distance =1


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

