package org.orbisgis.wpsservice

import org.orbisgis.wpsgroovyapi.attributes.TranslatableString
import org.orbisgis.wpsgroovyapi.attributes.LanguageString
import org.orbisgis.wpsgroovyapi.attributes.MetadataAttribute
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
        keywords = ["test", "script", "wps"],
        translatedKeywords = [
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "test", lang = "en"),
                        @LanguageString(value = "test", lang = "fr")
                ]),
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "script", lang = "en"),
                        @LanguageString(value = "scripte", lang = "fr")
                ]),
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "wps", lang = "en"),
                        @LanguageString(value = "wps", lang = "fr")
                ])
        ],
        identifier = "orbisgis:test:geometry",
        metadata = [
                @MetadataAttribute(title = "metadata", role = "website", href = "http://orbisgis.org/")
        ]
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
        keywords = ["input"],
        translatedKeywords = [
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "input", lang = "en"),
                        @LanguageString(value = "entrée", lang = "fr")
                ])
        ],
        dimension = 3,
        excludedTypes = ["MULTIPOINT", "POINT"],
        minOccurs = 0,
        maxOccurs = 2,
        identifier = "orbisgis:test:geometry:input",
        metadata = [
                @MetadataAttribute(title = "metadata", role = "website", href = "http://orbisgis.org/")
        ]
        )
String inputGeometry

/*****************/
/** OUTPUT Data **/
/*****************/

/** This Geometry is the output data source. */
@GeometryOutput(
        title = ["Output Geometry","en","Sortie Geometry","fr"],
        description = ["A Geometry output.","en","Une sortie Geometry.","fr"],
        keywords = ["output"],
        translatedKeywords = [
                @TranslatableString(translatableStrings = [
                        @LanguageString(value = "output", lang = "en"),
                        @LanguageString(value = "sortie", lang = "fr")
                ])
        ],
        dimension = 2,
        geometryTypes = ["POLYGON", "POINT"],
        identifier = "orbisgis:test:geometry:output",
        metadata = [
                @MetadataAttribute(title = "metadata", role = "website", href = "http://orbisgis.org/")
        ]
)
String geometryOutput

