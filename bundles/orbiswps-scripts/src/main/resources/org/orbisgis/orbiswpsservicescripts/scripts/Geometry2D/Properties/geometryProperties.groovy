package org.orbisgis.orbiswpsservicescripts.scripts.Geometry2D.Properties

import org.orbisgis.orbiswpsgroovyapi.input.*
import org.orbisgis.orbiswpsgroovyapi.output.*
import org.orbisgis.orbiswpsgroovyapi.process.*

/********************/
/** Process method **/
/********************/



/**
 * This process extract the center of a geometry table using the SQL function.
 * The user has to specify (mandatory):
 *  - The input spatial data source (JDBCTable)
 *  - The geometry column (LiteralData)
 *  - A column identifier (LiteralData)
 *  - The geometry operations (centroid or interior point)
 *  - The output data source (JDBCTable)
 *
 * @return A database table or a file.
 * @author Erwan Bocher
 * @author Sylvain PALOMINOS
 */
@Process(
        title = ["Geometry properties","en",
                "Propriétés géométriques","fr"],
        description = [
                "Compute some basic geometry properties.","en",
                "Calcul des propriétés de base des géométries.","fr"],
        keywords = ["Vector,Geometry,Properties", "en",
                "Vecteur,Géométrie,Propriétés", "fr"],
        properties = ["DBMS_TYPE", "H2GIS",
                "DBMS_TYPE", "POSTGIS"],
        version = "1.0",
        identifier = "orbisgis:wps:official:geometryProperties"
)
def processing() {
//Build the start of the query
    String query = "CREATE TABLE "+outputTableName+" AS SELECT "

    for (String operation : operations) {
        if(operation.equals("geomtype")){
            query += " ST_GeometryType("+geometricField[0]+") as geomType,"
        }
        else if(operation.equals("srid")){
            query += " ST_SRID("+geometricField[0]+") as srid,"
        }
        else if(operation.equals("length")){
            query += " ST_Length("+geometricField[0]+") as length,"
        }
        else if(operation.equals("perimeter")){
            query += " ST_Perimeter("+geometricField[0]+") as perimeter,"
        }
        else if(operation.equals("area")){
            query += " ST_Area("+geometricField[0]+") as area,"
        }
        else if(operation.equals("dimension")){
            query += " ST_Dimension("+geometricField[0]+") as dimension,"
        }
        else if(operation.equals("coorddim")){
            query += " ST_Coorddim("+geometricField[0]+") as coorddim,"
        }
        else if(operation.equals("num_geoms")){
            query += " ST_NumGeometries("+geometricField[0]+") as numGeometries,"
        }
        else if(operation.equals("num_pts")){
            query += " ST_NPoints("+geometricField[0]+") as numPts,"
        }
        else if(operation.equals("issimple")){
            query += " ST_Issimple("+geometricField[0]+") as issimple,"
        }
        else if(operation.equals("isvalid")){
            query += " ST_Isvalid("+geometricField[0]+") as isvalid,"
        }
        else if(operation.equals("isempty")){
            query += " ST_Isempty("+geometricField[0]+") as isempty,"
        }
    }


    //Add the field id
    query += idField[0] + " FROM "+inputJDBCTable+";"

    if(dropTable){
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

/** This JDBCTable is the input data source. */
@JDBCTableInput(
        title = [
                "Input spatial data","en",
                "Données spatiales d'entrée","fr"],
        description = [
                "The spatial data source to compute the geometry properties.","en",
                "La source de données spatiales pour le calcul des propriétés géométriques.","fr"],
        dataTypes = ["GEOMETRY"],
        identifier = "inputJDBCTable")
String inputJDBCTable

/**********************/
/** INPUT Parameters **/
/**********************/

/** Name of the Geometric field of the JDBCTable inputJDBCTable. */
@JDBCColumnInput(
        title = [
                "Geometric column","en",
                "Colonne géométrique","fr"],
        description = [
                "The geometric column of the data source.","en",
                "La colonne géométrique de la source de données.","fr"],
        jdbcTableReference = "inputJDBCTable",
        identifier = "geometricField",
        dataTypes = ["GEOMETRY"])
String[] geometricField

/** Name of the identifier field of the JDBCTable inputJDBCTable. */
@JDBCColumnInput(
        title = [
                "Column identifier","en",
                "Colonne identifiant","fr"],
        description = [
                "A column used as an identifier.","en",
                "La colonne utilisée comme identifiant.","fr"],
	    excludedTypes=["GEOMETRY"],
        jdbcTableReference = "inputJDBCTable",
        identifier = "idField")
String[] idField

@EnumerationInput(
        title = [
                "Operation","en",
                "Opération","fr"],
        description = [
                "Operation to compute the properties.","en",
                "Opération à effectuer.","fr"],
        values=["geomtype","srid", "length","perimeter","area", "dimension", "coorddim", "num_geoms", "num_pts", "issimple", "isvalid", "isempty"],
        names = ["Geometry type,SRID,Length,Perimeter,Area,Geometry dimension,Coordinate dimension,Number of geometries,Number of points,Is simple,Is valid,Is empty","en",
                "Type de géométrie,SRID,Longueur,Périmètre,Surface,Dimension de la géométrie,Dimension des coordonnées,Nombre de géométries,Nombre de points,Est simple,Est valide,Est vide","fr"],
        multiSelection = true,
        identifier = "operations")
String[] operations = ["geomtype"]

@LiteralDataInput(
    title = [
				"Drop the output table if exists","en",
				"Supprimer la table de sortie si elle existe","fr"],
    description = [
				"Drop the output table if exists.","en",
				"Supprimer la table de sortie si elle existe.","fr"])
Boolean dropTable 

@LiteralDataInput(
        title = [
                "Output table name","en",
                "Nom de la table de sortie","fr"],
        description = [
                "Name of the table containing the result of the process.","en",
                "Nom de la table contenant les résultats du traitement.","fr"],
        identifier = "outputTableName")
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
                "Le message de sortie.","fr"],
        identifier = "literalOutput")
String literalOutput

