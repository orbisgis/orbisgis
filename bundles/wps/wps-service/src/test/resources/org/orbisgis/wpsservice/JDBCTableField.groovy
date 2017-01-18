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
 * Test script for the JDBCTableField
 * @author Sylvain PALOMINOS
 */
@Process(title = ["JDBCTableField test","en","Test du JDBCTableField","fr"],
        description = ["Test script using the JDBCTableField ComplexData.","en",
                "Scripts test pour l'usage du ComplexData JDBCTableField.","fr"],
        keywords = ["test,script,wps", "en", "test,scripte,wps", "fr"],
        identifier = "orbisgis:test:jdbctablefield",
        metadata = ["website","metadata"]
)
def processing() {
    jdbcTableFieldOutput = inputJDBCTableField;
}


/****************/
/** INPUT Data **/
/****************/

@JDBCTableInput(title = "JDBCTable for the JDBCTableField",
        identifier = "orbisgis:test:jdbctable:input")
String jdbcTableInput

/** This JDBCTableField is the input data source. */
@JDBCTableFieldInput(
        title = ["Input JDBCTableField","en","Entrée JDBCTableField","fr"],
        description = ["A JDBCTableField input.","en","Une entrée JDBCTableField.","fr"],
        keywords = ["input","en","entrée","fr"],
        jdbcTableReference = "orbisgis:test:jdbctable:input",
        excludedTypes = ["BOOLEAN"],
        minOccurs = 0,
        maxOccurs = 2,
        identifier = "orbisgis:test:jdbctablefield:input",
        metadata = ["website","metadata"]
        )
String inputJDBCTableField

/*****************/
/** OUTPUT Data **/
/*****************/

@JDBCTableOutput(title = "JDBCTable for the JDBCTableField",
        identifier = "orbisgis:test:jdbctable:output")
String jdbcTableOutput

/** This JDBCTableField is the output data source. */
@JDBCTableFieldOutput(
        title = ["Output JDBCTableField","en","Sortie JDBCTableField","fr"],
        description = ["A JDBCTableField output.","en","Une sortie JDBCTableField.","fr"],
        keywords = ["output","en","sortie","fr"],
        jdbcTableReference = "orbisgis:test:jdbctable:output",
        dataTypes = ["GEOMETRY", "NUMBER"],
        multiSelection = true,
        identifier = "orbisgis:test:jdbctablefield:output",
        metadata = ["website","metadata"]
)
String jdbcTableFieldOutput

