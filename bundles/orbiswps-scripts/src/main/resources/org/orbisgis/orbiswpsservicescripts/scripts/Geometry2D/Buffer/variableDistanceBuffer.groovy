package org.orbisgis.orbiswpsservicescripts.scripts.Geometry2D.Buffer

import org.orbisgis.orbiswpsgroovyapi.input.*
import org.orbisgis.orbiswpsgroovyapi.output.*
import org.orbisgis.orbiswpsgroovyapi.process.*

/********************/
/** Process method **/
/********************/

/**
 * This process execute a buffer on a spatial data source using the ST_Buffer().
 * The user has to specify (mandatory):
 *  - The input spatial data source (JDBCTable)
 *  - The BufferSize (JDBCColumn)
 *  - The output data source (JDBCTable)
 *
 * The user can specify (optional) :
 *  - The number of segments used to approximate a quarter circle (LiteralData)
 *  - The endcap style (Enumeration)
 *  - The join style (Enumeration)
 *  - The mitre ratio limit (only affects mitered join style) (LiteralData)
 *
 * @return A datadase table.
 * @author Sylvain PALOMINOS
 * @author Erwan BOCHER
 */
@Process(
        title = [
                "Variable distance buffer","en",
                "Buffer avec une distance variable","fr"],
        description = [
                "Execute a buffer on a geometric field using another field to specify the distance.","en",
                "Génère une zone tampon sur un champ géométrique en utilisant un autre champ pour définir la distance.","fr"],
        keywords = ["Vector,Geometry", "en",
                "Vecteur,Géométrie", "fr"],
        properties = ["DBMS_TYPE", "H2GIS",
                "DBMS_TYPE", "POSTGIS"],
        version = "1.0")
def processing() {

    //Build the start of the query
    String query = "CREATE TABLE "+outputTableName+" AS SELECT ST_Buffer("+geometricField[0]+","+bufferSize[0]
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

	query+=" FROM "+inputJDBCTable+";"

    if(dropOutputTable){
	sql.execute "drop table if exists " + outputTableName
    }
    
    //Execute the query
    sql.execute(query)
    
    if(dropInputTable){
        sql.execute "drop table if exists " + inputJDBCTable
    }
    
    literalOutput = "Process done"
}


/****************/
/** INPUT Data **/
/****************/

/** This JDBCTable is the input data source for the buffer. */
@JDBCTableInput(
        title = [
                "Input table","en",
                "Table en entrée","fr"],
        description = [
                "The input geometry table to compute the buffer.","en",
                "La table contenant une colone géometrie pour construire la zone tampon.","fr"],
        dataTypes = ["GEOMETRY"])
String inputJDBCTable

/**********************/
/** INPUT Parameters **/
/**********************/

@JDBCColumnInput(
        title = [
                "Geometric column","en",
                "Colonne géométrique","fr"],
        description = [
                "The geometric column of the input table.","en",
                "La colonne géométrique de la table d'entrée.","fr"],
        jdbcTableReference = "inputJDBCTable",
        dataTypes = ["GEOMETRY"])
String[] geometricField


@JDBCColumnInput(
        title = [
                "Buffer size","en",
                "Largeur de la zone tampon","fr"],
        description = [
                "A numeric column to specify the size of the buffer.","en",
                "Colonne numérique contenant les tailles de tampon.","fr"],
        jdbcTableReference = "inputJDBCTable",
        dataTypes = ["DOUBLE", "INTEGER", "LONG"])
String[] bufferSize

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
        minOccurs = 0)
String[] endcapStyle = ["round"]

/** Join style. */
@EnumerationInput(
        title = [
                "Join style","en",
                "Style de jointure","fr"],
        description = [
                "The join style.","en",
                "Le style de jointure.","fr"],
        values=["round", "mitre", "miter", "bevel"],
        minOccurs = 0)
String[] joinStyle = ["round"]

/** Fields to keep. */
@JDBCColumnInput(
        title = [
                "Columns to keep","en",
                "Colonne à conserver","fr"],
        description = [
                "The columns that will be kept in the output.","en",
                "Les colonnes qui seront conservées dans la table de sortie.","fr"],
        excludedTypes=["GEOMETRY"],
        multiSelection = true,
        minOccurs = 0,
        jdbcTableReference = "inputJDBCTable")
String[] fieldList


@LiteralDataInput(
    title = [
				"Drop the output table if exists","en",
				"Supprimer la table de sortie si elle existe","fr"],
    description = [
				"Drop the output table if exists.","en",
				"Supprimer la table de sortie si elle existe.","fr"])
Boolean dropOutputTable 

@LiteralDataInput(
        title = [
                "Output table name","en",
                "Nom de la table de sortie","fr"],
        description = [
                "Name of the table containing the result of the process.","en",
                "Nom de la table contenant les résultats du traitement.","fr"])
String outputTableName


@LiteralDataInput(
    title = [
				"Drop the input table","en",
				"Supprimer la table d'entrée","fr"],
    description = [
				"Drop the input table when the script is finished.","en",
				"Supprimer la table d'entrée lorsque le script est terminé.","fr"])
Boolean dropInputTable 

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

