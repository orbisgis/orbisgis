package org.orbisgis.wpsservicescripts.scripts.Table

import org.orbisgis.wpsgroovyapi.input.JDBCTableFieldInput
import org.orbisgis.wpsgroovyapi.input.JDBCTableInput
import org.orbisgis.wpsgroovyapi.input.EnumerationInput
import org.orbisgis.wpsgroovyapi.input.LiteralDataInput
import org.orbisgis.wpsgroovyapi.output.LiteralDataOutput
import org.orbisgis.wpsgroovyapi.process.Process

/********************/
/** Process method **/
/********************/



/**
 * This process joins two tables.
 * @return A database table or a file.
 * @author Erwan Bocher
 * @author Sylvain PALOMINOS
 */
@Process(
		title = ["Table join","en",
				"Jointure de table","fr"],
		description = ["Join two tables.","en",
				"Jointure de deux tables.","fr"],
		keywords = ["Table,Join", "en",
				"Table,Jointure", "fr"],
		properties = ["DBMS_TYPE", "H2GIS",
				"DBMS_TYPE", "POSTGIS"])
def processing() {

	if(createIndex!=null && createIndex==true){
		sql.execute "create index on "+ rightJDBCTable + "("+ rightField[0] +")"
		sql.execute "create index on "+ leftJDBCTable + "("+ leftField[0] +")"
	}

	String query = "CREATE TABLE "+outputTableName+" AS SELECT * FROM "

	if(operation[0].equals("left")){
		query += leftJDBCTable + "JOIN " + rightJDBCTable + " ON " + leftJDBCTable+ "."+ leftField[0]+ "="+ rightJDBCTable+"."+ rightField[0];
	}
	else if (operation[0].equals("right")){

	}
        
        else if (operation[0].equals("union")){

	}
	//Execute the query
	sql.execute(query);
        
        if(dropInputTables){
            sql.execute "drop table if exists " + leftJDBCTable+","+ rightJDBCTable
        }

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
	literalOutput = "Process done"
}


/****************/
/** INPUT Data **/
/****************/

/** This JDBCTable is the left data source. */
@JDBCTableInput(
		title = ["Left data source","en",
				"Source de données gauche","fr"],
		description = [
				"The left data source used for the join.","en",
				"La source de données gauche utilisée pour la jointure.","fr"])
String leftJDBCTable

/** This JDBCTable is the right data source. */
@JDBCTableInput(
		title = [
				"Right data source","en",
				"Source de données droite","fr"],
		description = [
				"The right data source used for the join.","en",
				"La source de données droite utilisée pour la jointure.","fr"])
String rightJDBCTable

/**********************/
/** INPUT Parameters **/
/**********************/

/** Name of the identifier field of the left jdbcTable. */
@JDBCTableFieldInput(
		title = [
				"Left field(s)","en",
				"Champ(s) gauche(s)","fr"],
		description = [
				"The field identifier of the left data source.","en",
				"L'identifiant du/des champ(s) de la source de données gauche.","fr"],
        jdbcTableReference = "leftJDBCTable",
        excludedTypes = ["GEOMETRY"])
String[] leftField

/** Name of the identifier field of the right jdbcTable. */
@JDBCTableFieldInput(
		title = [
				"Right field(s)","en",
				"Champ(s) droit(s)","fr"],
		description = [
				"The field identifier of the right data source.","en",
				"L'identifiant du/des champ(s) de la source de données droite.","fr"],
        jdbcTableReference = "rightJDBCTable",
        excludedTypes = ["GEOMETRY"])
String[] rightField


@EnumerationInput(
		title = ["Operation","en",
				"Opération","fr"],
		description = [
				"Types of join.","en",
				"Type de jointure.","fr"],
        values=["left","right", "union"],
        names=["Left join","Right join", "Union join" ],
		multiSelection = false)
String[] operation = ["left"]


@LiteralDataInput(
		title = [
				"Create indexes","en",
				"Création d'indexes","fr"],
		description = [
				"Create an index on each field identifiers to perform the join.","en",
				"Création d'un index sur chacun des identifiants des champs avant la jointure.","fr"],
		minOccurs = 0)
Boolean createIndex

@LiteralDataInput(
    title = [
				"Drop the input tables","en",
				"Supprimer les tables d'entrée","fr"],
    description = [
				"Drop the input tables when the script is finished.","en",
				"Supprimer les tables d'entrée lorsque le script est terminé.","fr"])
Boolean dropInputTables 

@LiteralDataInput(
		title = ["Output table name","en",
				"Nom de la table de sortie","fr"],
		description = [
				"Name of the table containing the result of the process.","en",
				"Nom de la table contenant le résultat de la jointure.","fr"])
String outputTableName

/*****************/
/** OUTPUT Data **/
/*****************/

/** String output of the process. */
@LiteralDataOutput(
		title = ["Output message","en",
				"Message de sortie","fr"],
		description = [
				"The output message.","en",
				"Le message de sortie.","fr"])
String literalOutput

