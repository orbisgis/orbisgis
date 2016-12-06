package org.orbisgis.wpsservice

import org.orbisgis.wpsgroovyapi.attributes.TranslatableString
import org.orbisgis.wpsgroovyapi.attributes.LanguageString
import org.orbisgis.wpsgroovyapi.attributes.MetadataAttribute
import org.orbisgis.wpsgroovyapi.input.RawDataInput
import org.orbisgis.wpsgroovyapi.output.RawDataOutput
import org.orbisgis.wpsgroovyapi.process.Process

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
        metadata = [
                @MetadataAttribute(title = "metadata", role = "website", href = "http://orbisgis.org/")
        ]
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
        metadata = [
                @MetadataAttribute(title = "metadata", role = "website", href = "http://orbisgis.org/")
        ]
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
        metadata = [
                @MetadataAttribute(title = "metadata", role = "website", href = "http://orbisgis.org/")
        ]
)
String rawDataOutput

