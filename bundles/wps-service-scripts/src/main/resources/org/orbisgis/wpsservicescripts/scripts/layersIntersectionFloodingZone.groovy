package org.orbisgis.wpsservicescripts.scripts

import org.orbisgis.wpsgroovyapi.input.*
import org.orbisgis.wpsgroovyapi.output.DataStoreOutput
import org.orbisgis.wpsgroovyapi.process.Process


/********************/
/** Process method **/
/********************/

/**
 * This process does the intersection of buildings with a flooding zone.
 * The user has to specify (mandatory):
 *  - The input building data source (DataStore)
 *  - The input river data source (DataStore)
 *  - The building geometric field (DataField)
 *  - The river geometric field (DataField)
 *  - The flood zone size (LiteralData)
 *  - The output data source (DataStore)
 *
 *  The user can specify (optional) :
 *  - The number of segments used to approximate a quarter circle (LiteralData)
 *  - The endcap style (Enumeration)
 *  - The join style (Enumeration)
 *  - The mitre ratio limit (only affects mitered join style) (LiteralData)
 *
 * @return The output intersection layer between the vegetation and a city.
 * @see http://www.h2gis.org/docs/dev/ST_Accum/
 * @see http://www.h2gis.org/docs/dev/ST_Buffer/
 * @see http://www.h2gis.org/docs/dev/ST_Intersects/
 * @author Sylvain PALOMINOS
 */
@Process(title = "Intersection buildings flooding zone",
        resume = "Intersection of building with a flooding zone.",
        keywords = ["OrbisGIS","ST_Intersects","ST_Buffer","ST_Accum","example","layerIntersection"])
def processing() {

    String condition = ""
    riverNameValue.each {cityName ->
        if(condition.isEmpty()){
            condition+=riverDataInput+"."+riverNameField+" LIKE '"+cityName+"'"
        }
        else{
            condition+=" OR "+riverDataInput+"."+riverNameField+" LIKE '"+cityName+"'"
        }
    }

    //Build the query
    String query =
            "CREATE TABLE "+dataStoreOutputName+" AS " +
                    "SELECT "+buildingDataInput+".* " +
                    "FROM "+riverDataInput+","+buildingDataInput+" " +
                    "WHERE ("+condition+
                    ") AND ST_INTERSECTS("+
                    buildingDataInput+"."+buildingGeometricField+"," +
                    "SELECT ST_Buffer(ST_ACCUM("+
                    riverDataInput+"."+riverGeometricField+")," +
                    floodZoneSize


    String optionalParameter = "";
    //If quadSegs is defined
    if(quadSegs != null){
        optionalParameter += "quad_segs="+quadSegs+" "
    }
    //If endcapStyle is defined
    if(endcapStyle != null){
        optionalParameter += "endcap="+endcapStyle+" "
    }
    //If joinStyle is defined
    if(joinStyle != null){
        optionalParameter += "join="+joinStyle+" "
    }
    //If mitreLimit is defined
    if(mitreLimit != null){
        optionalParameter += "mitre_limit="+mitreLimit+" "
    }
    //If optionalParameter is not empty, add it to the request
    if(!optionalParameter.isEmpty()){
        query += ",'"+optionalParameter+"'";
    }
    query += "));";

    dataStoreOutput = dataStoreOutputName;

    //Execute the query
    sql.execute(query)
}

/****************/
/** INPUT Data **/
/****************/

@DataStoreInput(title="Input building",
        resume="The input building data source",
        dataStoreTypes = "GEOMETRY")
String buildingDataInput

@DataStoreInput(title="Input river",
        resume="The input river data source",
        dataStoreTypes = "GEOMETRY")
String riverDataInput

/** INPUT Parameters **/
@DataFieldInput(title="Building geometric field",
        resume="The building geometric field",
        dataStoreTitle="Input building",
        fieldTypes = ["GEOMETRY"])
String buildingGeometricField

@DataFieldInput(title="River geometric field",
        resume="The river geometric field",
        dataStoreTitle="Input river",
        fieldTypes = ["GEOMETRY"])
String riverGeometricField

@DataFieldInput(title="River name field",
        resume="The river name field",
        dataStoreTitle="Input river",
        fieldTypes = ["STRING"])
String riverNameField

@FieldValueInput(title="River name",
        resume="The river name",
        dataFieldTitle = "River name field",
        multiSelection = true)
String[] riverNameValue

@LiteralDataInput(title="Flood zone size",
        resume="The flood zone size")
Integer floodZoneSize = 100

/********************/
/** Buffer options **/
/********************/
/** Mitre ratio limit (only affects mitered join style). */
@LiteralDataInput(
        title="Mitre limit",
        resume="Mitre ratio limit (only affects mitered join style)",
        minOccurs = 0)
Double mitreLimit = 5.0

/** Number of segments used to approximate a quarter circle. */
@LiteralDataInput(
        title="Segment number for a quarter circle",
        resume="Number of segments used to approximate a quarter circle",
        minOccurs = 0)
Integer quadSegs = 8

/** Endcap style. */
@EnumerationInput(
        title="Endcap style",
        resume="The endcap style",
        values=["round", "flat", "butt", "square"],
        selectedValues=["round"],
        minOccurs = 0)
String endcapStyle

/** Join style. */
@EnumerationInput(
        title="Join style",
        resume="The join style",
        values=["round", "mitre", "miter", "bevel"],
        selectedValues=["round"],
        minOccurs = 0)
String joinStyle

/** Output DataStore name. */
@LiteralDataInput(
        title="DataStore name",
        resume="The DataStore name"
)
String dataStoreOutputName
/************/
/** OUTPUT **/
/************/
@DataStoreOutput(title="Output Data",
        resume="The output data source",
        dataStoreTypes = "GEOMETRY")
String dataStoreOutput
