package org.orbisgis.orbiswpsservice

import org.orbisgis.orbiswpsgroovyapi.input.RawDataInput
import org.orbisgis.orbiswpsgroovyapi.output.RawDataOutput
import org.orbisgis.orbiswpsgroovyapi.process.Process
/********************/
/** Process method **/
/********************/

/**
 * Test script for the RawData
 * @author Sylvain PALOMINOS
 */
@Process(title = ["RawData test","en","Test du RawData","fr"],
        description = ["Test script using the RawData ComplexData.","en",
                "Scripts test pour l'usage du ComplexData RawData.","fr"],
        keywords = ["test,script,wps","en","test,scripte,wps","fr"],
        identifier = "orbisgis:test:rawdata",
        metadata = ["website","metadata"]
)
def processing() {
        rawDataOutput = inputRawData;
}


/****************/
/** INPUT Data **/
/****************/

/** This RawData is the input data source. */
@RawDataInput(
        title = ["Input RawData","en","Entrée RawData","fr"],
        description = ["A RawData input.","en","Une entrée RawData.","fr"],
        keywords = ["input","en","entrée","fr"],
        isDirectory = false,
        minOccurs = 0,
        maxOccurs = 2,
        identifier = "orbisgis:test:rawdata:input",
        metadata = ["website","metadata"]
        )
String inputRawData

/*****************/
/** OUTPUT Data **/
/*****************/

/** This RawData is the output data source. */
@RawDataOutput(
        title = ["Output RawData","en","Sortie RawData","fr"],
        description = ["A RawData output.","en","Une sortie RawData.","fr"],
        keywords = ["output","en","sortie","fr"],
        isFile = false,
        multiSelection = true,
        identifier = "orbisgis:test:rawdata:output",
        metadata = ["website","metadata"]
)
String rawDataOutput

