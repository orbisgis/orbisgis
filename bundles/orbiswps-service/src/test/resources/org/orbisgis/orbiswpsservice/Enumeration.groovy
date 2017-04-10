package org.orbisgis.orbiswpsservice

import org.orbisgis.orbiswpsgroovyapi.input.EnumerationInput
import org.orbisgis.orbiswpsgroovyapi.output.EnumerationOutput
import org.orbisgis.orbiswpsgroovyapi.process.Process
/********************/
/** Process method **/
/********************/

/**
 * Test script for the Enumeration
 * @author Sylvain PALOMINOS
 */
@Process(title = ["Enumeration test","en","Test du Enumeration","fr"],
        description = ["Test script using the Enumeration ComplexData.","en",
                "Scripts test pour l'usage du ComplexData Enumeration.","fr"],
        keywords = ["test,script,wps","en","test,scripte,wps","fr"],
        identifier = "orbisgis:test:enumeration",
        metadata = ["website","metadata"]
)
def processing() {
    sleep(500)
    enumerationOutput = inputEnumeration;
}


/****************/
/** INPUT Data **/
/****************/

/** This Enumeration is the input data source. */
@EnumerationInput(
        title = ["Input Enumeration","en","Entrée Enumeration","fr"],
        description = ["A Enumeration input.","en","Une entrée Enumeration.","fr"],
        keywords = ["input","en","entrée","fr"],
        multiSelection = true,
        isEditable = true,
        values = ["value1", "value2"],
        names = ["name,name","en","nom,nom","fr"],
        minOccurs = 0,
        maxOccurs = 2,
        identifier = "input",
        metadata = ["website","metadata"]
        )
String[] inputEnumeration = ["value2"]

/*****************/
/** OUTPUT Data **/
/*****************/

/** This Enumeration is the output data source. */
@EnumerationOutput(
        title = ["Output Enumeration","en","Sortie Enumeration","fr"],
        description = ["A Enumeration output.","en","Une sortie Enumeration.","fr"],
        keywords = ["output","en","sortie","fr"],
        values = ["value1", "value2"],
        identifier = "output",
        metadata = ["website","metadata"]
)
String[] enumerationOutput

