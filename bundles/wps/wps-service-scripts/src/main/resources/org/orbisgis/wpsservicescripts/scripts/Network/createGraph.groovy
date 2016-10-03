package org.orbisgis.wpsservicescripts.scripts.Network

import org.orbisgis.wpsgroovyapi.attributes.Keyword
import org.orbisgis.wpsgroovyapi.attributes.LanguageString
import org.orbisgis.wpsgroovyapi.input.*
import org.orbisgis.wpsgroovyapi.output.*
import org.orbisgis.wpsgroovyapi.process.*



/**
 * This process creates a graph network.
 * @author Erwan Bocher
 * @author Sylvain PALOMINOS
 */
@Process(
        traducedTitles = [
                @LanguageString(value = "Create a graph", lang = "en"),
                @LanguageString(value = "Créer un graphe", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "Create a graph stored in two tables nodes and edges from an input table that contains Multi or LineString.<br>If the input table has name 'input', then the output tables are named 'input_nodes' and 'input_edges'.", lang = "en"),
                @LanguageString(value = "Créer un graphe stocké dans deux tables 'node' (noeud) et 'edge' (arc) depuis une table contenant des objets du type MultiLineString et LineString.<br>Si la table en entrée a pour nom 'input', alors celles en sortie seront nommées 'input_nodes' et 'input_edges'.", lang = "fr")
        ],
        traducedKeywords = [
                @Keyword(traducedKeywords = [
                        @LanguageString(value = "Network", lang = "en"),
                        @LanguageString(value = "Réseau", lang = "fr")
                ]),
                @Keyword(traducedKeywords = [
                        @LanguageString(value = "Geometry", lang = "en"),
                        @LanguageString(value = "Géometrie", lang = "fr")
                ])
        ])
def processing() {    
    if(slope==null){
        slope=false;
    }
	
    String query = " SELECT ST_GRAPH('"   + inputDataStore + "', '"+geometricField[0]+"',"+tolerance+ ", "+ slope+ ")"

    //Execute the query
    sql.execute(query)

    literalOutput = "The graph network has been created."
}

/****************/
/** INPUT Data **/
/****************/

/** This DataStore is the input data source. */
@DataStoreInput(
        traducedTitles = [
                @LanguageString(value = "Input spatial data", lang = "en"),
                @LanguageString(value = "Donnée spatiale d'entrée", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "The spatial data source to create the graphe tables.", lang = "en"),
                @LanguageString(value = "La source de données spatiales servant à la création des tables du graphe.", lang = "fr")
        ],
        dataStoreTypes = ["GEOMETRY"])
String inputDataStore


/** Name of the Geometric field of the DataStore inputDataStore. */
@DataFieldInput(
        traducedTitles = [
                @LanguageString(value = "Geometric field", lang = "en"),
                @LanguageString(value = "Champ géométrique", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "The geometric field of the data source.", lang = "en"),
                @LanguageString(value = "Le champ géométrique de la source de données.", lang = "fr")
        ],
        dataStoreFieldName = "inputDataStore",
        fieldTypes = ["GEOMETRY"])
String[] geometricField

/** Snapping tolerance. */
@LiteralDataInput(
        traducedTitles = [
                @LanguageString(value = "Snapping tolerance", lang = "en"),
                @LanguageString(value = "Tolérance d'accrochage", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "The tolerance value is used specify the side length of a square Envelope around each node used to snap together other nodes within the same Envelope.", lang = "en"),
                @LanguageString(value = "La valeur de tolérance est utilisée pour fixer la taille du coté du carré de l'enveloppe autour de chaque noeud pour rassembler les noeuds appartenant à la meme enveloppe.", lang = "fr")
        ])
Double tolerance 

@LiteralDataInput(
        traducedTitles = [
                @LanguageString(value = "Slope orientation ?", lang = "en"),
                @LanguageString(value = "Orientation selon la pente ?", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "True if edges should be oriented by the z-value of their first and last coordinates (decreasing).", lang = "en"),
                @LanguageString(value = "Vrai si les sommets doivent etre orientés selon les valeurs Z de leur première et dernière corrdonnées.", lang = "fr")
        ],
	    minOccurs = 0)
Boolean slope


/** String output of the process. */
@LiteralDataOutput(
        traducedTitles = [
                @LanguageString(value = "Output message", lang = "en"),
                @LanguageString(value = "Message de sortie", lang = "fr")
        ],
        traducedResumes = [
                @LanguageString(value = "The output message.", lang = "en"),
                @LanguageString(value = "Le message de sortie.", lang = "fr")
        ])
String literalOutput



