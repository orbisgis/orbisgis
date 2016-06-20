package org.orbisgis.wpsservice.scripts

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
 * This process is used to create a grid of polygons.
 *
 * @return A datadase table.
 * @author Erwan BOCHER
 */
@Process(title = "Create a grid of polygons.",
        resume = "Create a grid of polygons.",
        keywords = "Vector,Geometry,Create")
def processing() {

    //Build the start of the query
    String query = "CREATE TABLE "+dataStoreOutput+" AS SELECT * from ST_MakeGrid('"+inputDataStore+"',"+x_distance+","+y_distance+")"
    
    //Execute the query
    sql.execute(query)
}


/****************/
/** INPUT Data **/
/****************/

@DataStoreInput(
        title = "Input spatial data",
        resume = "The spatial data source to compute the grid. The extend of grid is based on the full extend of the table.",
        isSpatial = true)
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

/*****************/
/** OUTPUT Data **/
/*****************/

@DataStoreOutput(
        title="Output grid",
        resume="The output grid",
        isSpatial = true)
String dataStoreOutput

