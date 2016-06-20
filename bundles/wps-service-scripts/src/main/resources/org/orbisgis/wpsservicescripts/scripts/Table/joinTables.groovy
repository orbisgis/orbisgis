package org.orbisgis.orbistoolbox.view.utils.scripts;

import org.orbisgis.wpsgroovyapi.input.*
import org.orbisgis.wpsgroovyapi.output.*
import org.orbisgis.wpsgroovyapi.process.*

/********************/
/** Process method **/
/********************/



/**
 * This process joins two tables.
 * @return A database table or a file.
 * @author Erwan Bocher
 */
@Process(title = "Table join",
        resume = "Join two tables.",
        keywords = "Table,Join")
def processing() {

if(createIndex!=null && createIndex==true){
sql.execute "create index on "+ rightDataStore + "("+ rightField +")"
sql.execute "create index on "+ leftDataStore + "("+ leftField +")"
}

String query = "CREATE TABLE "+dataStoreOutput+" AS SELECT * FROM "

if(operation.equals("left")){
query += leftDataStore + "JOIN " + rightDataStore + " ON " + leftDataStore+ "."+ leftField+ "="+ rightDataStore+"."+ rightField;
}
else if (operation.equals("left")){

}
//Execute the query
sql.execute(query);

//SELECT *
//FROM A
//LEFT JOIN B ON A.key = B.key

//SELECT *
//FROM A
//RIGHT JOIN B ON A.key = B.key

//INNER JOIN
//SELECT *
//FROM A
//INNER JOIN B ON A.key = B.key

}


/****************/
/** INPUT Data **/
/****************/

/** This DataStore is the left data source. */
@DataStoreInput(
        title = "Left data source",
        resume = "The left data source used for the join.")
String leftDataStore

/** This DataStore is the right data source. */
@DataStoreInput(
        title = "Right data source",
        resume = "The right data source used for the join.")
String rightDataStore

/**********************/
/** INPUT Parameters **/
/**********************/

/** Name of the identifier field of the left dataStore. */
@DataFieldInput(
        title = "Left field",
        resume = "The field identifier of the left data source",
        dataStore = "leftDataStore",
        excludedTypes = ["GEOMETRY"])
String leftField

/** Name of the identifier field of the right dataStore. */
@DataFieldInput(
        title = "Right field",
        resume = "The field identifier of the right data source",
        dataStore = "rightDataStore",
        excludedTypes = ["GEOMETRY"])
String rightField


@EnumerationInput(title="Operation",
        resume="Types of join.",
        values=["left","right", "union"],
        names=["Left join","Right join", "Union join" ],
        defaultValues = "left",
multiSelection = false)
String operation


@LiteralDataInput(
        title="Create indexes",
        resume="Create an index on each field identifiers to perform the join.",
	minOccurs = 0)
Boolean createIndex

/*****************/
/** OUTPUT Data **/
/*****************/

/** This DataStore is the output data source. */
@DataStoreOutput(
        title="Join table",
        resume="The output data source to store the result of the join.")
String dataStoreOutput

