package org.orbisgis.orbiswpsservicescripts.scripts.Table

import org.orbisgis.orbiswpsgroovyapi.input.*
import org.orbisgis.orbiswpsgroovyapi.output.*
import org.orbisgis.orbiswpsgroovyapi.process.*

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
		title = ["Tables join","en",
				"Jointure de tables","fr"],
		description = ["SQL join between two tables.","en",
				"Jointure SQL entre deux tables.","fr"],
		keywords = ["Table,Join", "en",
				"Table,Jointure", "fr"],
		properties = ["DBMS_TYPE", "H2GIS",
				"DBMS_TYPE", "POSTGIS"],
                version = "1.0")
def processing() {

	if(createIndex!=null && createIndex==true){
		sql.execute "create index on "+ rightJDBCTable + "("+ rightField[0] +")"
		sql.execute "create index on "+ leftJDBCTable + "("+ leftField[0] +")"
	}

	String query = "CREATE TABLE "+outputTableName+" AS SELECT * FROM "

        if(operation[0].equals("left")){
        query += leftJDBCTable + "LEFT JOIN " + rightJDBCTable + " ON " + leftJDBCTable+ "."+ leftField[0]+ "="+ rightJDBCTable+"."+ rightField[0];
        }
    
        else if (operation[0].equals("left_without_b")){
        query += leftJDBCTable + "LEFT JOIN " + rightJDBCTable + " ON " + leftJDBCTable+ "."+ leftField[0]+ "="+ rightJDBCTable+"."+ rightField[0] 
        + " where " + rightJDBCTable+"."+ rightField[0] + " IS NULL";
        }
    
    
        else if (operation[0].equals("right")){
        query += leftJDBCTable + "RIGHT JOIN " + rightJDBCTable + " ON " + leftJDBCTable+ "."+ leftField[0]+ "="+ rightJDBCTable+"."+ rightField[0];
        
        }
        
        else if (operation[0].equals("right_without_a")){
        query += leftJDBCTable + "RIGHT JOIN " + rightJDBCTable + " ON " + leftJDBCTable+ "."+ leftField[0]+ "="+ rightJDBCTable+"."+ rightField[0] 
        + " where " + leftJDBCTable+ "."+ leftField[0] + " IS NULL";
        }
        
        else if (operation[0].equals("inner")){
            query += leftJDBCTable + "INNER JOIN " + rightJDBCTable + " ON " + leftJDBCTable+ "."+ leftField[0]+ "="+ rightJDBCTable+"."+ rightField[0];
	}
        
        else if (operation[0].equals("cross")){
            query += leftJDBCTable + "CROSS JOIN " + rightJDBCTable ;	
	}
        
        else if (operation[0].equals("natural")){
            query += leftJDBCTable + "NATURAL JOIN " + rightJDBCTable ;	
	}
        
	//Execute the query
	sql.execute(query);
        
        if(dropInputTables){
            sql.execute "drop table if exists " + leftJDBCTable+","+ rightJDBCTable
        }
	literalOutput = "Process done"
}


/****************/
/** INPUT Data **/
/****************/

/** This JDBCTable is the left data source. */
@JDBCTableInput(
		title = ["Left table","en",
				"Table à gauche","fr"],
		description = [
				"The left table used for the join.","en",
				"La table à gauche utilisée pour la jointure.","fr"])
String leftJDBCTable

/** This JDBCTable is the right data source. */
@JDBCTableInput(
		title = [
				"Right table","en",
				"Table de droite","fr"],
		description = [
				"The right table  used for the join.","en",
				"La table de droite utilisée pour la jointure.","fr"])
String rightJDBCTable

/**********************/
/** INPUT Parameters **/
/**********************/

/** Name of the identifier field of the left jdbcTable. */
@JDBCColumnInput(
		title = [
				"Left column identifier","en",
				"Colonne de correspondance à gauche","fr"],
		description = [
				"The column name identifier of the left table.","en",
				"Nom de la colonne de correspondance de la table à gauche.","fr"],
        jdbcTableReference = "leftJDBCTable",
        excludedTypes = ["GEOMETRY"])
String[] leftField

/** Name of the identifier field of the right jdbcTable. */
@JDBCColumnInput(
		title = [
				"Right column identifier","en",
				"Colonne de correspondance à gauche","fr"],
		description = [
				"The column name identifier of the right table.","en",
				"Nom de la colonne de correspondance de la table à droite.","fr"],
        jdbcTableReference = "rightJDBCTable",
        excludedTypes = ["GEOMETRY"])
String[] rightField


@EnumerationInput(
		title = ["Operation","en",
				"Opération","fr"],
		description = [
				"Types of join.","en",
				"Type de jointure.","fr"],
        values=["left","right","left_without_b", "right_without_a", "inner", "cross","natural"],
        names=["Left join,Right join, Left join without rigth values, Right join without left values, Inner join, Cross join, Natural join", "en",
        "Jointure à gauche ,Jointure à droite, Jointure à gauche sans les valeurs de droite, Jointure à droite sans les valeurs de gauche, Intersection des deux tables, Jointure croisée, Jointure naturelle", "fr" ],
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

