package org.orbisgis.orbiswpsservicescripts.scripts.Import

import org.orbisgis.orbiswpsgroovyapi.input.*
import org.orbisgis.orbiswpsgroovyapi.output.*
import org.orbisgis.orbiswpsgroovyapi.process.*

/**
 * @author Erwan Bocher
 */
@Process(title = ["Import a GPX file","en","Importer un fichier GPX","fr"],
    description = ["Import a GPX file from path and creates several tables prefixed by tableName representing the file’s contents.\n Please go to  http://www.h2gis.org","en",
                "Import d'un fichier GPX en plusieurs tables.\n Pour plus d'informations consulter http://www.h2gis.org.","fr"],
    keywords = ["OrbisGIS,Importer, Fichier, GPX","fr",
                "OrbisGIS,Import, File, GPX","en"],
    properties = ["DBMS_TYPE","H2GIS"],
    version = "1.0")
def processing() {
    File fileData = new File(fileDataInput[0])
    name = fileData.getName()
    tableName = name.substring(0, name.lastIndexOf(".")).toUpperCase()
    query = "CALL GPXRead('"+ fileData.absolutePath+"','"
    if(jdbcTableOutputName != null){
	tableName = jdbcTableOutputName
    }
    
    query += tableName+"')"	    

    sql.execute query
    

    literalDataOutput = "The GPX file has been imported."
}


@RawDataInput(
    title = ["Input GPX","en","Fichier GPX","fr"],
    description = ["The input GPX file to be imported.","en",
                "Selectionner un fichier GPX à importer.","fr"],
    fileTypes = ["gpx"],
    isDirectory = false)
String[] fileDataInput



/** Optional table name. */
@LiteralDataInput(
    title = ["Prefix for all tables","en","Prefixe pour les tables créées","fr"],
    description = ["Prefix for all table names to store the GPX file.","en",
                "Prefixe pour les tables créées.","fr"],
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
