package org.orbisgis.wpsservice

import org.orbisgis.wpsgroovyapi.input.JDBCTableInput
import org.orbisgis.wpsgroovyapi.output.JDBCTableOutput
import org.orbisgis.wpsgroovyapi.process.Process
/********************/
/** Process method **/
/********************/

/**
 * Test script for the DataStore
 * @author Sylvain PALOMINOS
 */
@Process(title = ["DataStore test","en","Test du DataStore","fr"],
        description = ["Test script using the DataStore ComplexData.","en",
                "Scripts test pour l'usage du ComplexData DataStore.","fr"],
        keywords = ["test,script,wps","en","test,scripte,wps","fr"],
        identifier = "orbisgis:test:datastore",
        metadata = ["website","metadata"]
)
def processing() {
    dataStoreOutput = inputDataStore;
}


/****************/
/** INPUT Data **/
/****************/

/** This DataStore is the input data source. */
@JDBCTableInput(
        title = ["Input DataStore","en","Entrée DataStore","fr"],
        description = ["A DataStore input.","en","Une entrée DataStore.","fr"],
        keywords = ["input","en","entrée","fr"],
        dataTypes = ["GEOMETRY"],
        minOccurs = 0,
        maxOccurs = 2,
        identifier = "orbisgis:test:datastore:input",
        metadata = ["website","metadata"]
        )
String inputDataStore

/*****************/
/** OUTPUT Data **/
/*****************/

/** This DataStore is the output data source. */
@JDBCTableOutput(
        title = ["Output DataStore","en","Sortie DataStore","fr"],
        description = ["A DataStore output.","en","Une sortie DataStore.","fr"],
        keywords = ["output","en","sortie","fr"],
        identifier = "orbisgis:test:datastore:output",
        metadata = ["website","metadata"]
)
String dataStoreOutput

