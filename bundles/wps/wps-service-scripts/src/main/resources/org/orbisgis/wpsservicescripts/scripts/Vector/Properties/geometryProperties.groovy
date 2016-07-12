package org.orbisgis.wpsservicescripts.scripts.Vector.Properties

import org.orbisgis.wpsgroovyapi.input.*
import org.orbisgis.wpsgroovyapi.output.*
import org.orbisgis.wpsgroovyapi.process.*

/********************/
/** Process method **/
/********************/



/**
 * This process extract the center of a geometry table using the SQL function.
 * The user has to specify (mandatory):
 *  - The input spatial data source (DataStore)
 *  - The geometry column (LiteralData)
 *  - A column identifier (LiteralData)
 *  - The geometry operations (centroid or interior point)
 *  - The output data source (DataStore)
 *
 * @return A database table or a file.
 * @author Erwan Bocher
 */
@Process(title = "Geometry properties",
        resume = "Compute some basic geometry properties.",
        keywords = ["Vector","Geometry","Properties"])
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
    query += idField[0] + " FROM "+inputDataStore+";"

    //Execute the query
    sql.execute(query)
    literalOutput = "Process done"
}


/****************/
/** INPUT Data **/
/****************/

/** This DataStore is the input data source. */
@DataStoreInput(
        title = "Input spatial data",
        resume = "The spatial data source to compute the geometry properties.",
        dataStoreTypes = ["GEOMETRY"])
String inputDataStore

/**********************/
/** INPUT Parameters **/
/**********************/

/** Name of the Geometric field of the DataStore inputDataStore. */
@DataFieldInput(
        title = "Geometric field",
        resume = "The geometric field of the data source",
        dataStoreTitle = "Input spatial data",
        fieldTypes = ["GEOMETRY"])
String[] geometricField

/** Name of the identifier field of the DataStore inputDataStore. */
@DataFieldInput(
        title = "Identifier field",
        resume = "A field used as an identifier",
	excludedTypes=["GEOMETRY"],
        dataStoreTitle = "Input spatial data")
String[] idField

@EnumerationInput(title="Operation",
        resume="Operation to compute the properties.",
        values=["geomtype","srid", "length","perimeter","area", "dimension", "coorddim", "num_geoms", "num_pts", "issimple", "isvalid", "isempty"],
        names=["Geometry type","SRID", "Length", "Perimeter", "Area", "Geometry dimension","Coordinate dimension", "Number of geometries", "Number of points", "Is simple", "Is valid", "Is empty" ],
        selectedValues = "geomtype",
multiSelection = true)
String[] operations


@LiteralDataInput(
        title="Output table name",
        resume="Name of the table containing the result of the process.")
String outputTableName

/*****************/
/** OUTPUT Data **/
/*****************/

/** String output of the process. */
@LiteralDataOutput(
        title="Output message",
        resume="The output message")
String literalOutput

