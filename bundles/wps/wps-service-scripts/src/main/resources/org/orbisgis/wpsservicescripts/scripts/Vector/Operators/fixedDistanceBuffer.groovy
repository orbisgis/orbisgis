package org.orbisgis.wpsservicescripts.scripts.Vector.Operators

import org.orbisgis.wpsgroovyapi.input.DataFieldInput
import org.orbisgis.wpsgroovyapi.input.DataStoreInput
import org.orbisgis.wpsgroovyapi.input.EnumerationInput
import org.orbisgis.wpsgroovyapi.input.LiteralDataInput
import org.orbisgis.wpsgroovyapi.output.LiteralDataOutput
import org.orbisgis.wpsgroovyapi.process.Process
/********************/
/** Process method **/
/********************/

/**
 * This process execute a buffer on a spatial data source using the ST_Buffer() function.
 * The user has to specify (mandatory):
 *  - The input spatial data source (DataStore)
 *  - The BufferSize (LiteralData)
 *  - The output data source (DataStore)
 *
 * The user can specify (optional) :
 *  - The number of segments used to approximate a quarter circle (LiteralData)
 *  - The endcap style (Enumeration)
 *  - The join style (Enumeration)
 *  - The mitre ratio limit (only affects mitered join style) (LiteralData)
 *
 * @return A datadase table.
 * @see http://www.h2gis.org/docs/dev/ST_Buffer/
 * @author Sylvain PALOMINOS
 * @author Erwan BOCHER
 */
@Process(
        title = [
                "Fixed distance buffer","en",
                "Buffer à distance fixe","fr"
        ],
        description = [
                "Execute a buffer on a geometric field with a constant distance.","en",
                "Génère une zone tampon sur un champ géométrique avec une distance constante.","fr"
        ],
        keywords = ["Vector,Geometry", "en",
                "Vecteur,Géométrie", "fr"],
        properties = ["DBMS_TYPE", "H2GIS",
              "DBMS_TYPE", "POSTGIS"])
def processing() {

    //Build the start of the query
    String query = "CREATE TABLE "+outputTableName+" AS SELECT ST_Buffer("+geometricField+","+bufferSize
    //Build the third optional parameter
    String optionalParameter = "";
    //If quadSegs is defined
    if(quadSegs != null){
        optionalParameter += "quad_segs="+quadSegs+" "
    }
    //If endcapStyle is defined
    if(endcapStyle != null){
        optionalParameter += "endcap="+endcapStyle[0]+" "
    }
    //If joinStyle is defined
    if(joinStyle != null){
        optionalParameter += "join="+joinStyle[0]+" "
    }
    //If mitreLimit is defined
    if(mitreLimit != null){
        optionalParameter += "mitre_limit="+mitreLimit+" "
    }
    //If optionalParameter is not empty, add it to the request
    if(!optionalParameter.isEmpty()){
        query += ",'"+optionalParameter+"'";
    }
    //Build the end of the query
    query += ") AS the_geom";
    for(String field : fieldList) {
        if (field != null) {
            query += ", " + field;
        }
    }

	query+=" FROM "+inputDataStore+";"

    //Execute the query
    sql.execute(query)
    literalOutput = "Process done"
}


/****************/
/** INPUT Data **/
/****************/

/** This DataStore is the input data source for the buffer. */
@DataStoreInput(
        title = [
                "Input spatial data","en",
                "Données spatiales d'entrée","fr"],
        description = [
                "The spatial data source for the buffer.","en",
                "La source de données spatiales pour le tampon.","fr"],
        dataStoreTypes = "GEOMETRY")
String inputDataStore

/**********************/
/** INPUT Parameters **/
/**********************/

/** Name of the Geometric field of the DataStore inputDataStore. */
@DataFieldInput(
        title = [
                "Geometric field","en",
                "Champ géométrique","fr"],
        description = [
                "The geometric field of the data source.","en",
                "Le champ géométrique de la source de données.","fr"],
        variableReference = "inputDataStore",
        fieldTypes = ["GEOMETRY"])
String geometricField

/** Size of the buffer. */
@LiteralDataInput(
        title = [
                "Buffer Size","en",
                "Taille du tampon","fr"],
        description = [
                "The buffer size.","en",
                "La taille du tampon.","fr"])
Double bufferSize 

/** Mitre ratio limit (only affects mitered join style). */
@LiteralDataInput(
        title = [
                "Mitre limit","en",
                "Limite de mitre","fr"],
        description = [
                "Mitre ratio limit (only affects mitered join style)","en",
                "Le rapport limite de mitre. (Utilisé uniquement pour le style de jointure mitre)","fr"],
        minOccurs = 0)
Double mitreLimit = 5.0

/** Number of segments used to approximate a quarter circle. */
@LiteralDataInput(
        title = [
                "Segment number for a quarter circle","en",
                "Nombre de segment pour un quart de cercle","fr"],
        description = [
                "Number of segments used to approximate a quarter circle.","en",
                "Le nombre de segments utilisé pour approximer un quart de cercle.","fr"],
        minOccurs = 0)
Integer quadSegs = 8

/** Endcap style. */
@EnumerationInput(
        title = [
                "Endcap style","en",
                "Style de l'extrémité","fr"],
        description = [
                "The endcap style.","en",
                "Le style de l'extrémité.","fr"],
        values=["round", "flat", "butt", "square"],
        selectedValues = ["round"],
        minOccurs = 0)
String[] endcapStyle

/** Join style. */
@EnumerationInput(
        title = [
                "Join style","en",
                "Style de jointure","fr"],
        description = [
                "The join style.","en",
                "Le style de jointure.","fr"],
        values=["round", "mitre", "miter", "bevel"],
        selectedValues = ["round"],
        minOccurs = 0)
String[] joinStyle

/** Fields to keep. */
@DataFieldInput(
        title = [
                "Fields to keep","en",
                "Champs à conserver","fr"],
        description = [
                "The fields that will be kept in the output.","en",
                "Les champs qui seront conservés dans la table de sortie.","fr"],
        excludedTypes=["GEOMETRY"],
        multiSelection = true,
        minOccurs = 0,
        variableReference = "inputDataStore")
String[] fieldList


@LiteralDataInput(
        title = [
                "Output table name","en",
                "Nom de la table de sortie","fr"],
        description = [
                "Name of the table containing the result of the process.","en",
                "Nom de la table contenant les résultats du traitement.","fr"])
String outputTableName

/*****************/
/** OUTPUT Data **/
/*****************/

/** String output of the process. */
@LiteralDataOutput(
        title = [
                "Output message","en",
                "Message de sortie","fr"],
        description = [
                "The output message.","en",
                "Le message de sortie.","fr"])
String literalOutput

