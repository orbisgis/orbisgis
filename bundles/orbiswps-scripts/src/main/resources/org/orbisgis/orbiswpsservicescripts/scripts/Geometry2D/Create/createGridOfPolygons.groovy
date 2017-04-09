package org.orbisgis.orbiswpsservicescripts.scripts.Geometry2D.Create

import org.orbisgis.orbiswpsgroovyapi.input.*
import org.orbisgis.orbiswpsgroovyapi.output.*
import org.orbisgis.orbiswpsgroovyapi.process.*

/********************/
/** Process method **/
/********************/

/**
 * This process is used to create a grid of polygons.
 *
 * @return A datadase table.
 * @author Erwan BOCHER
 * @author Sylvain PALOMINOS
 */
@Process(
        title = ["Create a grid of polygons","en",
                "Création d'une grille de polygones","fr"],
        description = [
                "Create a grid of polygons.","en",
                "Création d'une grille de polygones.","fr"],
        keywords = ["Vector,Geometry,Create", "en",
                "Vecteur,Géométrie,Création", "fr"],
        properties = ["DBMS_TYPE", "H2GIS"],
        version = "1.0")
def processing() {

    //Build the start of the query
    String query = "CREATE TABLE "+outputTableName+" AS SELECT * from ST_MakeGrid('"+inputJDBCTable+"',"+x_distance+","+y_distance+")"
    
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

@JDBCTableInput(
        title = ["Input spatial data","en",
                "Données spatiales d'entrée","fr"],
        description = [
                "The spatial data source to compute the grid. The extend of grid is based on the full extend of the table.","en",
                "La source de données spatiales utilisée pour le calcul de la grille. L'étendue de la grille se base sur l'étendue de la table.","fr"],
        dataTypes = ["GEOMETRY"])
String inputJDBCTable

/**********************/
/** INPUT Parameters **/
/**********************/

@LiteralDataInput(
        title = [
                "X cell size","en",
                "Taille X des cellules","fr"],
        description = [
                "The X cell size.","en",
                "La taille X des cellules.","fr"])
Double x_distance =1

@LiteralDataInput(
        title = [
                "Y cell size","en",
                "Taille Y des cellules","fr"
        ],
        description = [
                "The Y cell size.","en",
                "La taille Y des cellules.","fr"])
Double y_distance =1


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

