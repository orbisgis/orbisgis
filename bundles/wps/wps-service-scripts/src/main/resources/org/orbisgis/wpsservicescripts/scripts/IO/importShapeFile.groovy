package org.orbisgis.wpsservicescripts.scripts.IO

import org.orbisgis.wpsgroovyapi.input.EnumerationInput
import org.orbisgis.wpsgroovyapi.input.LiteralDataInput
import org.orbisgis.wpsgroovyapi.input.RawDataInput
import org.orbisgis.wpsgroovyapi.output.LiteralDataOutput
import org.orbisgis.wpsgroovyapi.process.Process


/**
 * @author Erwan Bocher
 */
@Process(title = ["Import a shapeFile","en","Importer un fichier SHP","fr"],
        description = ["Import in the database a shapeFile as a new table.","en",
                "Import d'un fichier SHP dans la base de données","fr"],
        keywords = ["OrbisGIS,Importer, Fichier, SHP","fr",
                "OrbisGIS,Import, File, SHP","en"],
        properties = ["DBMS_TYPE","H2GIS"])
def processing() {

	if(jdbcTableOutputName != null){
	}
	else{
	}  

    literalDataOutput = "The shape file has been imported."
}


@RawDataInput(
        title = ["Input SHP","en","Fichier SHP","fr"],
        description = ["The input shapeFile to be imported.","en",
                "Selectionner un fichier SHP à importer.","fr"],
        fileTypes = ["shp"], multiSelection=false)
String[] shpDataInput



@EnumerationInput(
        title = ["File Encoding","en","'Encodage du fichier","fr"],
        description = ["The file encoding .","en",
                "L'encodage du fichier.","fr"],
        values=["System", "utf-8", "ISO-8859-1", "ISO-8859-2", "ISO-8859-4", "ISO-8859-5", "ISO-8859-7", "ISO-8859-9", "ISO-8859-13","ISO-8859-15"],
        names=["System, utf-8, ISO-8859-1, ISO-8859-2, ISO-8859-4, ISO-8859-5, ISO-8859-7, ISO-8859-9, ISO-8859-13,ISO-8859-15","en"],
        isEditable = false)
String[] encoding = ["System"]




/** Optional table name. */
@LiteralDataInput(
        title = ["Output table name","en","Nom de la table importée","fr"],
        description = ["Table name to store the shapeFile.","en",
                "Nom de la table importée.","fr"],
	minOccurs = 0)
String jdbcTableOutputName

/************/
/** OUTPUT **/
/************/
@LiteralDataOutput(
        title = ["Output message","en",
                "Message de sortie","fr"],
        description = ["Output message.","en",
                "Message de sortie.","fr"])
String literalDataOutput
