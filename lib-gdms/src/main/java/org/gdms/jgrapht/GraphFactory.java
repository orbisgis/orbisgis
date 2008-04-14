package org.gdms.jgrapht;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.alg.*;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * This utility class offers static methods to build graphs from feature
 * collections.
 * @author Michael Michaud
 * @version 0.1 (2007-04-20)
 */

public class GraphFactory {
    
    
   /**
    * Create an undirected graph from a collection of features
    * @param features a collection of features.
    * @param dim3 true means that nodes are evaluated equals when x,y,z are equals
    * @return a WeightedMultigraph or a DirectedWeightedMultigraph
    */
    public static WeightedGraph<INode,Geometry>
        createGraph(Collection geometries, boolean dim3) {
        WeightedGraph<INode, Geometry> graph = new WeightedPseudograph(Geometry.class);
        return add(graph, geometries, dim3);
    }
    
   /**
    * Create a directed graph from a collection of features
    * @param features a collection of features.
    * @param dim3 true means that nodes are evaluated equals when x,y,z are equals
    * @return a WeightedMultigraph or a DirectedWeightedMultigraph
    */
    public static WeightedGraph<INode,Geometry>
        createDirectedGraph(Collection geometries, boolean dim3) {
        WeightedGraph<INode, Geometry> graph = new DirectedWeightedMultigraph(Geometry.class);
        return add(graph, geometries, dim3);
    }
    
    
    private static WeightedGraph<INode,Geometry>
        add(WeightedGraph<INode,Geometry> graph, Collection geometries, boolean dim3) {
        Coordinate[] cc;
        for (Iterator it = geometries.iterator() ; it.hasNext() ; ) {
               Geometry g = (Geometry) it.next();
            cc = g.getCoordinates();
            INode node1 = dim3? new Node3D(cc[0]) : new Node2D(cc[0]);
            INode node2 = dim3? new Node3D(cc[cc.length-1]) : new Node2D(cc[cc.length-1]);
            graph.addVertex(node1);
            graph.addVertex(node2);
            graph.addEdge(node1, node2, g);
            //graph.setEdgeWeight(g, g.getLength());
        }
        return graph;
    }
    
    
  
}

