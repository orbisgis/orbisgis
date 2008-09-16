/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.contrib.algorithm.jgrapht;

import java.util.Collection;
import java.util.Iterator;

import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.graph.WeightedPseudograph;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

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

