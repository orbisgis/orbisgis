package org.orbisgis.orbiswpsservicescripts.scripts.Network

import org.orbisgis.orbiswpsgroovyapi.input.*
import org.orbisgis.orbiswpsgroovyapi.output.*
import org.orbisgis.orbiswpsgroovyapi.process.*

/**
 * This process creates a graph network.
 * @author Erwan Bocher
 * @author Sylvain PALOMINOS
 */
@Process(
        title = ["Create a graph","en",
                "Créer un graphe","fr"],
        description = [
                "Create a graph stored in two tables nodes and edges from an input table that contains Multi or LineString.<br>If the input table has name 'input', then the output tables are named 'input_nodes' and 'input_edges'.","en",
                "Créer un graphe stocké dans deux tables 'nodes' (noeuds) et 'edges' (arcs) depuis une table contenant des objets du type MultiLineString et LineString.<br>Si la table en entrée a pour nom 'input', alors celles en sortie seront nommées 'input_nodes' et 'input_edges'.","fr"],
        keywords = ["Network,Geometry","en",
                "Réseau,Géometrie","fr"],
        properties = ["DBMS_TYPE", "H2GIS"],
        version = "1.0")
def processing() {    
    if(slope==null){
        slope=false;
    }
	
    String query = " SELECT ST_GRAPH('"   + inputJDBCTable + "', '"+geometricField[0]+"',"+tolerance+ ", "+ slope+ ")"

    //Execute the query
    sql.execute(query)
    
    if(dropInputTable){
        sql.execute "drop table if exists " + inputJDBCTable
    }

    literalOutput = "The graph network has been created."
}

/****************/
/** INPUT Data **/
/****************/

/** This JDBCTable is the input data source. */
@JDBCTableInput(
        title = ["Input spatial data","en","Donnée spatiale d'entrée","fr"],
        description = [
                "The spatial data source to create the graphe tables.","en",
                "La source de données spatiales servant à la création des tables du graphe.","fr"],
        dataTypes = ["LINESTRING", "MULTILINESTRING"])
String inputJDBCTable


/** Name of the Geometric field of the JDBCTable inputJDBCTable. */
@JDBCColumnInput(
        title = ["Geometric column","en",
                "Colonne géométrique","fr"],
        description = [
                "The geometric column of the data source.","en",
                "La colonne géométrique de la source de données.","fr"],
        jdbcTableReference = "inputJDBCTable",
        dataTypes = ["LINESTRING", "MULTILINESTRING"])
String[] geometricField

/** Snapping tolerance. */
@LiteralDataInput(
        title = ["Snapping tolerance","en",
                "Tolérance d'accrochage","fr"],
        description = [
                "The tolerance value is used specify the side length of a square Envelope around each node used to snap together other nodes within the same Envelope.","en",
                "La valeur de tolérance est utilisée pour fixer la taille du coté du carré de l'enveloppe autour de chaque noeud  qui est utilisée pour rassembler les noeuds appartenant à la meme enveloppe.","fr"])
Double tolerance 

@LiteralDataInput(
        title = ["Slope orientation ?","en",
                "Orientation selon la pente ?","fr"],
        description = ["True if edges should be oriented by the z-value of their first and last coordinates (decreasing).","en",
                "Vrai si les sommets doivent etre orientés selon les valeurs Z de leur première et dernière coordonnées.","fr"],
	    minOccurs = 0)
Boolean slope

@LiteralDataInput(
    title = [
				"Drop the input table","en",
				"Supprimer la table d'entrée","fr"],
    description = [
				"Drop the input table when the script is finished.","en",
				"Supprimer la table d'entrée lorsque le script est terminé.","fr"])
Boolean dropInputTable 


/** String output of the process. */
@LiteralDataOutput(
        title = ["Output message", "en",
                "Message de sortie", "fr"],
        description = ["The output message.", "en",
                "Le message de sortie.", "fr"])
String literalOutput



