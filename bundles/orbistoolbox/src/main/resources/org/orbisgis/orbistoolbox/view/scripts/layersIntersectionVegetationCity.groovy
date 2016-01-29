package org.orbisgis.orbistoolbox.view.utils.scripts

import groovy.sql.Sql
import org.orbisgis.orbistoolboxapi.annotations.input.*
import org.orbisgis.orbistoolboxapi.annotations.output.*
import org.orbisgis.orbistoolboxapi.annotations.model.*


/********************/
/** Process method **/
/********************/

/**
 * This process does the intersection of a vegetation layer with a city.
 * The user has to specify (mandatory):
 *  - The input vegetation data source (DataStore)
 *  - The input city data source (DataStore)
 *  - The vegetation geometric field (DataField)
 *  - The city geometric field (DataField)
 *  - The city name field (DataField)
 *  - The city name value (FieldValue)
 *  - The output data source (DataStore)
 *
 * @return The output intersection layer between the vegetation and a city.
 * @see http://www.h2gis.org/docs/dev/ST_Intersects/
 * @author Sylvain PALOMINOS
 */
@Process(title = "Intersection vegetation city",
        resume = "Intersection of a vegetation with a city.",
        keywords = "OrbisGIS,ST_Intersects,example,layerIntersection")
def processing() {
    sql.execute("DROP TABLE IF EXISTS " + dataStoreOutput + ";")
    //Build the query
    String condition = ""
    cityNameValue.each {cityName ->
        if(condition.isEmpty()){
            condition+=cityDataInput+"."+cityNameField+" LIKE '"+cityName+"'"
        }
        else{
            condition+=" OR "+cityDataInput+"."+cityNameField+" LIKE '"+cityName+"'"
        }
    }
    String query =
            "CREATE TABLE "+dataStoreOutput+" AS SELECT "+vegetationDataInput+".* " +
                    "FROM "+cityDataInput+","+vegetationDataInput+" " +
                    "WHERE ST_INTERSECTS("+cityDataInput+"."+cityGeometricField+", "+vegetationDataInput+"."+vegetationGeometricField+")" +
                    " AND ("+condition+");"
    //Execute the query
    sql.execute(query)
}


/****************/
/** INPUT Data **/
/****************/

@DataStoreInput(title="Input vegetation",
        resume="The input vegetation data source",
        isSpatial = true)
String vegetationDataInput

@DataStoreInput(title="Input city",
        resume="The input city data source",
        isSpatial = true)
String cityDataInput

/**********************/
/** INPUT Parameters **/
/**********************/

@DataFieldInput(title="Vegetation geometric field",
        resume="The vegetation geometric field",
        dataStore="vegetationDataInput",
        fieldTypes = ["GEOMETRY"])
String vegetationGeometricField

@DataFieldInput(title="City geometric field",
        resume="The city geometric field",
        dataStore="cityDataInput",
        fieldTypes = ["GEOMETRY"])
String cityGeometricField

@DataFieldInput(title="City name field",
        resume="The city name field",
        dataStore="cityDataInput")
String cityNameField

@FieldValueInput(title="City name value",
        resume="The city name value",
        dataField="cityNameField",
        multiSelection = true)
String[] cityNameValue

/*****************/
/** OUTPUT Data **/
/*****************/

@DataStoreOutput(title="Output Data",
        resume="The output",
        isSpatial = true)
String dataStoreOutput