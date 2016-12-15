package org.orbisgis.wpsservice

import org.orbisgis.wpsgroovyapi.input.JDBCTableFieldInput
import org.orbisgis.wpsgroovyapi.input.JDBCTableInput
import org.orbisgis.wpsgroovyapi.output.JDBCTableFieldOutput
import org.orbisgis.wpsgroovyapi.output.JDBCTableOutput
import org.orbisgis.wpsgroovyapi.process.Process
/********************/
/** Process method **/
/********************/

/**
 * Test script for the DataField
 * @author Sylvain PALOMINOS
 */
@Process(title = ["DataField test","en","Test du DataField","fr"],
        description = ["Test script using the DataField ComplexData.","en",
                "Scripts test pour l'usage du DataField DataField.","fr"],
        keywords = ["test,script,wps", "en", "test,scripte,wps", "fr"],
        identifier = "orbisgis:test:datafield",
        metadata = ["website","metadata"]
)
def processing() {
    dataFieldOutput = inputDataField;
}


/****************/
/** INPUT Data **/
/****************/

@JDBCTableInput(title = "DataStore for the DataField",
        identifier = "orbisgis:test:datastore:input")
String dataStoreInput

/** This DataField is the input data source. */
@JDBCTableFieldInput(
        title = ["Input DataField","en","Entrée DataField","fr"],
        description = ["A DataField input.","en","Une entrée DataField.","fr"],
        keywords = ["input","en","entrée","fr"],
        jdbcTableReference = "orbisgis:test:datastore:input",
        excludedTypes = ["BOOLEAN"],
        minOccurs = 0,
        maxOccurs = 2,
        identifier = "orbisgis:test:datafield:input",
        metadata = ["website","metadata"]
        )
String inputDataField

/*****************/
/** OUTPUT Data **/
/*****************/

@JDBCTableOutput(title = "DataStore for the DataField",
        identifier = "orbisgis:test:datastore:output")
String dataStoreOutput

/** This DataField is the output data source. */
@JDBCTableFieldOutput(
        title = ["Output DataField","en","Sortie DataField","fr"],
        description = ["A DataField output.","en","Une sortie DataField.","fr"],
        keywords = ["output","en","sortie","fr"],
        jdbcTableReference = "orbisgis:test:datastore:output",
        dataTypes = ["GEOMETRY", "NUMBER"],
        multiSelection = true,
        identifier = "orbisgis:test:datafield:output",
        metadata = ["website","metadata"]
)
String dataFieldOutput

