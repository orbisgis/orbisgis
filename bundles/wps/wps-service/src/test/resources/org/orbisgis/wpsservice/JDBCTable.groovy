package org.orbisgis.wpsservice

import org.orbisgis.wpsgroovyapi.input.JDBCTableInput
import org.orbisgis.wpsgroovyapi.output.JDBCTableOutput
import org.orbisgis.wpsgroovyapi.process.Process
/********************/
/** Process method **/
/********************/

/**
 * Test script for the JDBCTable
 * @author Sylvain PALOMINOS
 */
@Process(title = ["JDBCTable test","en","Test du JDBCTable","fr"],
        description = ["Test script using the JDBCTable ComplexData.","en",
                "Scripts test pour l'usage du ComplexData JDBCTable.","fr"],
        keywords = ["test,script,wps","en","test,scripte,wps","fr"],
        identifier = "orbisgis:test:jdbctable",
        metadata = ["website","metadata"]
)
def processing() {
    jdbcTableOutput = inputJDBCTable;
}


/****************/
/** INPUT Data **/
/****************/

/** This JDBCTable is the input data source. */
@JDBCTableInput(
        title = ["Input JDBCTable","en","Entrée JDBCTable","fr"],
        description = ["A JDBCTable input.","en","Une entrée JDBCTable.","fr"],
        keywords = ["input","en","entrée","fr"],
        dataTypes = ["GEOMETRY"],
        minOccurs = 0,
        maxOccurs = 2,
        identifier = "orbisgis:test:jdbctable:input",
        metadata = ["website","metadata"]
        )
String inputJDBCTable

/*****************/
/** OUTPUT Data **/
/*****************/

/** This JDBCTable is the output data source. */
@JDBCTableOutput(
        title = ["Output JDBCTable","en","Sortie JDBCTable","fr"],
        description = ["A JDBCTable output.","en","Une sortie JDBCTable.","fr"],
        keywords = ["output","en","sortie","fr"],
        identifier = "orbisgis:test:jdbctable:output",
        metadata = ["website","metadata"]
)
String jdbcTableOutput

