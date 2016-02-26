package org.orbisgis.wpsservice.scripts

import org.orbisgis.wpsgroovyapi.input.DataFieldInput
import org.orbisgis.wpsgroovyapi.input.DataStoreInput
import org.orbisgis.wpsgroovyapi.output.DataStoreOutput
import org.orbisgis.wpsgroovyapi.process.Process

/********************/
/** Process method **/
/********************/

/**
 * This process does an union of all the municipality by their region, summing the population and doing the
 * union of the geometry.
 * The user has to specify (mandatory):
 *  - The input spatial data source (DataStore)
 *  - The region geometry field (DataField)
 *  - The region field (DataField)
 *  - The population field (DataField)
 *  - The output data (DataStore)
 *  - The output csv CSV (DataStore)
 *
 * @return A spatial data containing the region, its geometry and the population.
 * @return A CSV file containing only the region and the population.
 * @see http://www.h2gis.org/docs/dev/ST_Union/
 * @see http://www.h2gis.org/docs/dev/ST_Accum/
 * @author Sylvain PALOMINOS
 */
@Process(title = "Population region",
        resume = "Union of municipality by their region, summing the population and doing the union of the geometry ",
        keywords = "OrbisGIS,ST_Union,ST_Accum,example")
def processing() {
    //Build the query
    String query = "CREATE TABLE "+dataStoreOutput+" AS" +
            " SELECT "+regionDataInput+"."+regionField+
            ", ST_Union(ST_Accum("+regionDataInput+"."+regionGeometricField+")) AS "+regionGeometricField+" " +
            ", SUM("+regionDataInput+"."+populationField+") AS "+populationField+" " +
            " FROM "+regionDataInput+" " +
            "GROUP BY "+regionDataInput+"."+regionField+";"
    //Execute the query
    sql.execute(query)
    //Generate the second output
    query = "CREATE TABLE "+csvDataOutput+" AS" +
            " SELECT "+regionDataInput+"."+regionField+
            ", SUM("+regionDataInput+"."+populationField+") " +
            " FROM "+regionDataInput+" " +
            "GROUP BY "+regionDataInput+"."+regionField+";"
    sql.execute(query)
}


/****************/
/** INPUT Data **/
/****************/

/** This DataStore is the input data source which conatins all the informations about the municipalities. */
@DataStoreInput(title="Input municipality data",
        resume="The input data source containing the municipalities, their geometry, their region and their population.",
        isSpatial = true)
String regionDataInput

/**********************/
/** INPUT Parameters **/
/**********************/

/** Geometric field of the municipalities. */
@DataFieldInput(title="Municipality geometric field",
        resume="The geometric field of the municipalities",
        dataStore="regionDataInput",
        fieldTypes = ["GEOMETRY"])
String regionGeometricField

/** Region field which will be used to group the municipalities. */
@DataFieldInput(title="Region field",
        resume="The field which contains the region used to group the municipalities.",
        dataStore="regionDataInput",
        fieldTypes = ["STRING"])
String regionField

/** Population field. */
@DataFieldInput(title="Population field",
        resume="The population field",
        dataStore="regionDataInput",
        fieldTypes = ["NUMBER"])
String populationField


/*****************/
/** OUTPUT Data **/
/*****************/

/** The spatial data output containing the region, its geometry and the population. */
@DataStoreOutput(title="Spatial output data",
        resume="The spatial output data, containing the region, its geometry and the population.",
        isSpatial = true)
String dataStoreOutput

/** The CSV output containing only the region and the population. */
@DataStoreOutput(title="CSV output",
        resume="The CSV output containing only the region and the population.",
        extensions="csv")
String csvDataOutput