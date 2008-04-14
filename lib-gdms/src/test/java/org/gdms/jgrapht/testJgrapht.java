package org.gdms.jgrapht;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.DijkstraShortestPath;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class testJgrapht {

	/**
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws ParseException {
		
		
		ArrayList geometries = new ArrayList();
		
		WKTReader wkReader = new WKTReader();
		
		
		geometries.add(wkReader.read("LINESTRING ( 286 245, 316 159, 225 85, 162 116 )"));
		geometries.add(wkReader.read("LINESTRING ( 162 116, 226 162 )"));
		geometries.add(wkReader.read("LINESTRING ( 226 162, 250 216 )"));
		
		
		
		Geometry geom0 = (Geometry) geometries.get(0);
		Geometry geom1 = (Geometry) geometries.get(1);
		Geometry geom2 = (Geometry) geometries.get(2);
		
		//		le calcul du graphe ne tient pas compte du z des coordonnees
		boolean dim3 = false;
		//Creation d'un graphe non orienté
		WeightedGraph<INode, Geometry> u_graph = GraphFactory.createGraph(geometries, dim3);
		//Creation d'un graphe orienté
		//WeightedGraph<INode, Geometry> d_graph = GraphFactory.createDirectedGraph(geometries, dim3);
		//	Recherche du plus court chemin
		Node2D depart = new Node2D(geom0.getCoordinate());
		Node2D arrive = new Node2D(geom2.getCoordinates()[1]);
		List<Geometry> chemin = DijkstraShortestPath.findPathBetween(u_graph, depart, arrive);
				
		
		for (Iterator itr = chemin.iterator(); itr.hasNext();) {
	           Geometry key = (Geometry) itr.next();
	           System.out.println(key);
			
		}
		
		
	}

}
