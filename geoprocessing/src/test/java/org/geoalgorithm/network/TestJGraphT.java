package org.geoalgorithm.network;

import java.io.File;
import java.util.Collection;
import java.util.Set;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.model.Feature;
import org.gdms.model.FeatureCollection;
import org.gdms.model.FeatureCollectionDecorator;
import org.jgrapht.DirectedGraph;
import org.jgrapht.WeightedGraph;

import fr.michaelm.jump.feature.jgrapht.FeatureAsEdge;
import fr.michaelm.jump.feature.jgrapht.FeatureAsNode;
import fr.michaelm.jump.feature.jgrapht.GraphFactory;
import fr.michaelm.jump.feature.jgrapht.INode;
import fr.michaelm.jump.feature.jgrapht.Node2D;

public class TestJGraphT {

	public static DataSourceFactory dsf = new DataSourceFactory();

	public static String path = "/home/bocher/Bureau/fabrice/data/lesdonnees3/EPG_UG_R1_polyline.shp";

	public static void main(String[] args) throws DriverLoadException,
			DataSourceCreationException, DriverException {

		DataSource mydata = dsf.getDataSource(new File(path));

		FeatureCollectionDecorator fc = new FeatureCollectionDecorator(mydata);

		fc.open();
		// le calcul du graphe ne tient pas compte du z des coordonnees
		boolean dim3 = true;

		System.out.println("Nombre d'edges en entrée  : " + fc.getRowCount());

		// Creation d'un graphe orienté
		DirectedGraph<INode, FeatureAsEdge> d_graph = (DirectedGraph<INode, FeatureAsEdge>) GraphFactory
				.createDirectedGraph(fc.getFeatures(), dim3);

		System.out.println("Nombre de vertex  : " + d_graph.vertexSet().size());

		System.out.println("Nombre d'edges  : " + d_graph.edgeSet().size());

		Set<FeatureAsEdge> edges = d_graph.edgeSet();

		Set<INode> nodes = d_graph.vertexSet();



		int k = 0;
		for (INode node : nodes) {
			k++;
			int innode = d_graph.inDegreeOf(node);



			//if (innode > 1) {

			Set<FeatureAsEdge> edge = d_graph.edgesOf(node);



			for (FeatureAsEdge featureAsEdge : edge) {

				System.out.println(innode + "  edge  " + featureAsEdge.getID());
			}


			//}

		}

		Feature depart = (Feature) fc.getFeatures().get(12);

		// System.out.println(d_graph.inDegreeOf(new
		// Node2D(depart.getGeometry().getCoordinate())));

		// Recherche du plus court chemin
		/*
		 * depart = new Node2D(fc.getFeatures().get(0).coordinate); arrive = new
		 * Node2D(fc.getFeatures().get(10).geometry.coordinate); chemin =
		 * DijkstraShortestPath.findPathBetween(u_graph, depart, arrive);
		 */

		fc.close();

	}
}
