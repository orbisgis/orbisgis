package org.orbisgis.wpsservice

import org.orbisgis.wpsgroovyapi.input.EnumerationInput
import org.orbisgis.wpsgroovyapi.output.EnumerationOutput
import org.orbisgis.wpsgroovyapi.process.Process
/********************/
/** Process method **/
/********************/

/**
 * Test script for the Enumeration
 * @author Sylvain PALOMINOS
 */
@Process(title = ["Enumeration test","en","Test du Enumeration","fr"],
        description = ["Test script using the Enumeration ComplexData.","en","Scripts test pour l'usage du ComplexData Enumeration.","fr"],
        keywords = ["test,script,wps","en","test,scripte,wps","fr"],
        identifier = "orbisgis:test:enumerationLongProcess",
        metadata = ["website","metadata"]
)
def processing() {
    sleep(50000)
    enumerationOutput = inputEnumeration;
}


/****************/
/** INPUT Data **/
/****************/

/** This Enumeration is the input data source. */
@EnumerationInput(
        title = ["Input Enumeration","en","Entrée Enumeration","fr"],
        description = ["A Enumeration input.","en","Une entrée Enumeration.","fr"],
        keywords = ["input","en","sortie","fr"],
        multiSelection = true,
        isEditable = true,
        values = ["value1", "value2"],
        selectedValues = ["value2"],
        names = ["name1,name2","en","nom1,nom2","fr"],
        minOccurs = 0,
        maxOccurs = 2,
        identifier = "orbisgis:test:enumerationLongProcess:input",
        metadata = ["website","metadata"]
        )
String[] inputEnumeration

/*****************/
/** OUTPUT Data **/
/*****************/

/** This Enumeration is the output data source. */
@EnumerationOutput(
        title = ["Output Enumeration","en","Sortie Enumeration","fr"],
        description = ["A Enumeration output.","en",
                "Une sortie Enumeration.","fr"],
        keywords = ["output","en","sortie","fr"],
        values = ["value1", "value2"],
        identifier = "orbisgis:test:enumerationLongProcess:output",
        metadata = ["website","metadata"]
)
String[] enumerationOutput

