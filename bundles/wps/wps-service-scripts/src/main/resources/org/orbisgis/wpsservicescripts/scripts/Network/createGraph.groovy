package org.orbisgis.wpsservicescripts.scripts.Network

import org.orbisgis.wpsgroovyapi.input.*
import org.orbisgis.wpsgroovyapi.output.*
import org.orbisgis.wpsgroovyapi.process.*



/**
 * This process creates a graph network.
 * @author Erwan Bocher
 */
@Process(title = "Create a graph",
        resume = "Create a graph stored in two tables nodes and edges from an input table that contains Multi or LineString.<br>If the input table has name 'input', then the output tables are named 'input_nodes' and 'input_edges'.",
        keywords = ["Network","Geometry"])
def processing() {    
    if(slope==null){
        slope=false;
    }
	
    String query = " SELECT ST_GRAPH('"   + inputDataStore + "', '"+geometricField[0]+"',"+tolerance+ ", "+ slope+ ")"

    //Execute the query
    sql.execute(query)

    literalOutput = "The graph network has been created."
}

/****************/
/** INPUT Data **/
/****************/

/** This DataStore is the input data source. */
@DataStoreInput(
        title = "Input spatial data",
        resume = "The spatial data source to create the graphe tables.",
        dataStoreTypes = ["GEOMETRY"])
String inputDataStore


/** Name of the Geometric field of the DataStore inputDataStore. */
@DataFieldInput(
        title = "Geometric field",
        resume = "The geometric field of the data source",
        dataStoreTitle = "Input spatial data",
        fieldTypes = ["GEOMETRY"])
String[] geometricField

/** Snapping tolerance. */
@LiteralDataInput(
        title="Snapping tolerance",
        resume="The tolerance value is used specify the side length of a square Envelope around each node used to snap together other nodes within the same Envelope.")
Double tolerance 

@LiteralDataInput(
        title="Slope orientation ?",
        resume="True if edges should be oriented by the z-value of their first and last coordinates (decreasing)",
	minOccurs = 0)
Boolean slope


/** String output of the process. */
@LiteralDataOutput(
        title="Output message",
        resume="The output message")
String literalOutput



