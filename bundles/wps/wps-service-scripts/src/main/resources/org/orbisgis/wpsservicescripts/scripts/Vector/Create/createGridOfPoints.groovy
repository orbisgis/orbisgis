package org.orbisgis.wpsservicescripts.scripts.Vector.Create

import org.orbisgis.wpsgroovyapi.input.JDBCTableInput
import org.orbisgis.wpsgroovyapi.input.LiteralDataInput
import org.orbisgis.wpsgroovyapi.output.LiteralDataOutput
import org.orbisgis.wpsgroovyapi.process.Process

/********************/
/** Process method **/
/********************/

/**
 * This process is used to create a grid of points.
 *
 * @return A datadase table.
 * @author Erwan BOCHER
 * @author Sylvain PALOMINOS
 */
@Process(
        title = ["Create a grid of points","en",
                "Création d'une grille de points","fr"],
        description = [
                "Create a grid of points.","en",
                "Création d'une grille de points.","fr"],
        keywords = ["Vector,Geometry,Creation", "en",
                "Vecteur,Géométrie,Création", "fr"],
        properties = ["DBMS_TYPE", "H2GIS"])
def processing() {

    //Build the start of the query
    String query = "CREATE TABLE "+outputTableName+" AS SELECT * from ST_MakeGridPoints('"+inputJDBCTable+"',"+x_distance+","+y_distance+")"
    
    //Execute the query
    sql.execute(query)
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
        title = ["X cell size","en",
                "Taille X des cellules","fr"],
        description = ["The X cell size.","en",
                "La taille X des cellules.","fr"])
Double x_distance =1

@LiteralDataInput(
        title = ["Y cell size","en",
                "Taille Y des cellules","fr"],
        description = [
                "The Y cell size.","en",
                "La taille Y des cellules.","fr"])
Double y_distance =1


@LiteralDataInput(
        title = ["Output table name","en",
                "Nom de la table de sortie","fr"],
        description = [
                "Name of the table containing the result of the process.","en",
                "Nom de la table contenant les résultats du traitement.","fr"])
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

