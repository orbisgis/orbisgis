package org.orbisgis.wpsservice

import org.orbisgis.wpsgroovyapi.input.GeometryInput
import org.orbisgis.wpsgroovyapi.output.GeometryOutput
import org.orbisgis.wpsgroovyapi.process.Process
/********************/
/** Process method **/
/********************/

/**
 * Test script for the Geometry
 * @author Sylvain PALOMINOS
 */
@Process(title = ["Geometry test","en","Test du Geometry","fr"],
        description = ["Test script using the Geometry ComplexData.","en",
                "Scripts test pour l'usage du ComplexData Geometry.","fr"],
        keywords = ["test,script,wps","en","test,scripte,wps","fr"],
        identifier = "orbisgis:test:geometry",
        metadata = ["website","metadata"]
)
def processing() {
    geometryOutput = inputGeometry;
}


/****************/
/** INPUT Data **/
/****************/

/** This Geometry is the input data source. */
@GeometryInput(
        title = ["Input Geometry","en","Entrée Geometry","fr"],
        description = ["A Geometry input.","en","Une entrée Geometry.","fr"],
        keywords = ["input","en","entrée","fr"],
        dimension = 3,
        excludedTypes = ["MULTIPOINT", "POINT"],
        minOccurs = 0,
        maxOccurs = 2,
        identifier = "orbisgis:test:geometry:input",
        metadata = ["website","metadata"]
        )
String inputGeometry

/*****************/
/** OUTPUT Data **/
/*****************/

/** This Geometry is the output data source. */
@GeometryOutput(
        title = ["Output Geometry","en","Sortie Geometry","fr"],
        description = ["A Geometry output.","en","Une sortie Geometry.","fr"],
        keywords = ["output","en","sortie","fr"],
        dimension = 2,
        geometryTypes = ["POLYGON", "POINT"],
        identifier = "orbisgis:test:geometry:output",
        metadata = ["website","metadata"]
)
String geometryOutput

