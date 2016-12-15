package org.orbisgis.wpsservice

import org.orbisgis.wpsgroovyapi.input.JDBCTableFieldInput
import org.orbisgis.wpsgroovyapi.input.JDBCTableInput
import org.orbisgis.wpsgroovyapi.input.JDBCTableFieldValueInput
import org.orbisgis.wpsgroovyapi.output.JDBCTableFieldOutput
import org.orbisgis.wpsgroovyapi.output.JDBCTableOutput
import org.orbisgis.wpsgroovyapi.output.JDBCTableFieldValueOutput
import org.orbisgis.wpsgroovyapi.process.Process
/********************/
/** Process method **/
/********************/

/**
 * Test script for the FieldValue
 * @author Sylvain PALOMINOS
 */
@Process(title = ["FieldValue test","en","Test du FieldValue","fr"],
        description = ["Test script using the FieldValue ComplexData.","en",
                "Scripts test pour l'usage du ComplexData FieldValue.","fr"],
        keywords = ["test,script,wps","en","test,scripte,wps","fr"],
        identifier = "orbisgis:test:fieldvalue",
        metadata = ["website","metadata"]
)
def processing() {
    fieldValueOutput = inputFieldValue;
}


/****************/
/** INPUT Data **/
/****************/

@JDBCTableInput(title = "DataStore for the FieldValue",
        identifier = "orbisgis:test:datastore:input")
String dataStoreInput

@JDBCTableFieldInput(title = "DataField for the FieldValue",
        identifier = "orbisgis:test:datafield:input",
        jdbcTableReference = "orbisgis:test:datastore:input")
String dataFieldInput

/** This FieldValue is the input data source. */
@JDBCTableFieldValueInput(
        title = ["Input FieldValue","en","Entrée FieldValue","fr"],
        description = ["A FieldValue input.","en","Une entrée FieldValue.","fr"],
        keywords = ["input","en","entrée","fr"],
        jdbcTableFieldReference = "orbisgis:test:datafield:input",
        minOccurs = 0,
        maxOccurs = 2,
        identifier = "orbisgis:test:fieldvalue:input",
        metadata = ["website","metadata"]
        )
String inputFieldValue

/*****************/
/** OUTPUT Data **/
/*****************/

@JDBCTableOutput(title = "DataStore for the FieldValue",
        identifier = "orbisgis:test:datastore:output")
String dataStoreOutput

@JDBCTableFieldOutput(title = "DataField for the FieldValue",
        identifier = "orbisgis:test:datafield:output",
        jdbcTableReference = "orbisgis:test:datastore:output")
String dataFieldOutput

/** This FieldValue is the output data source. */
@JDBCTableFieldValueOutput(
        title = ["Output FieldValue","en","Sortie FieldValue","fr"],
        description = ["A FieldValue output.","en","Une sortie FieldValue.","fr"],
        keywords = ["output","en","sortie","fr"],
        jdbcTableFieldReference = "orbisgis:test:datafield:output",
        multiSelection = true,
        identifier = "orbisgis:test:fieldvalue:output",
        metadata = ["website","metadata"]
)
String fieldValueOutput

