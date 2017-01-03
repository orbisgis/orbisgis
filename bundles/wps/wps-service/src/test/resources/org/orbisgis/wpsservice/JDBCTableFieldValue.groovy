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
 * Test script for the JDBCTableFieldValue
 * @author Sylvain PALOMINOS
 */
@Process(title = ["JDBCTableFieldValue test","en","Test du JDBCTableFieldValue","fr"],
        description = ["Test script using the JDBCTableFieldValue ComplexData.","en",
                "Scripts test pour l'usage du ComplexData JDBCTableFieldValue.","fr"],
        keywords = ["test,script,wps","en","test,scripte,wps","fr"],
        identifier = "orbisgis:test:jdbctablefieldvalue",
        metadata = ["website","metadata"]
)
def processing() {
    jdbcTableFieldValueOutput = inputJDBCTableFieldValue;
}


/****************/
/** INPUT Data **/
/****************/

@JDBCTableInput(title = "JDBCTable for the JDBCTableFieldValue",
        identifier = "orbisgis:test:jdbctable:input")
String jdbcTableInput

@JDBCTableFieldInput(title = "JDBCTableField for the JDBCTableFieldValue",
        identifier = "orbisgis:test:jdbctablefield:input",
        jdbcTableReference = "orbisgis:test:jdbctable:input")
String jdbcTableFieldInput

/** This JDBCTableFieldValue is the input data source. */
@JDBCTableFieldValueInput(
        title = ["Input JDBCTableFieldValue","en","Entrée JDBCTableFieldValue","fr"],
        description = ["A JDBCTableFieldValue input.","en","Une entrée JDBCTableFieldValue.","fr"],
        keywords = ["input","en","entrée","fr"],
        jdbcTableFieldReference = "orbisgis:test:jdbctablefield:input",
        minOccurs = 0,
        maxOccurs = 2,
        identifier = "orbisgis:test:jdbctablefieldvalue:input",
        metadata = ["website","metadata"]
        )
String inputJDBCTableFieldValue

/*****************/
/** OUTPUT Data **/
/*****************/

@JDBCTableOutput(title = "JDBCTable for the JDBCTableFieldValue",
        identifier = "orbisgis:test:jdbctable:output")
String jdbcTableOutput

@JDBCTableFieldOutput(title = "JDBCTableField for the JDBCTableFieldValue",
        identifier = "orbisgis:test:jdbctablefield:output",
        jdbcTableReference = "orbisgis:test:jdbctable:output")
String jdbcTableFieldOutput

/** This JDBCTableFieldValue is the output data source. */
@JDBCTableFieldValueOutput(
        title = ["Output JDBCTableFieldValue","en","Sortie JDBCTableFieldValue","fr"],
        description = ["A JDBCTableFieldValue output.","en","Une sortie JDBCTableFieldValue.","fr"],
        keywords = ["output","en","sortie","fr"],
        jdbcTableFieldReference = "orbisgis:test:jdbctablefield:output",
        multiSelection = true,
        identifier = "orbisgis:test:jdbctablefieldvalue:output",
        metadata = ["website","metadata"]
)
String jdbcTableFieldValueOutput

