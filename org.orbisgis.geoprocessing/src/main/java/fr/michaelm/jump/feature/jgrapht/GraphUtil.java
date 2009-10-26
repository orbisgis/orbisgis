package fr.michaelm.jump.feature.jgrapht;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.alg.*;

//import com.vividsolutions.jump.feature.*;
//import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * Utility class to work with graphs built from feature collections.
 * @author Michael Michaud
 * @version 0.1 (2007-05-28)
 */

public class GraphUtil {
    
    
   /**
    * Returns true if the graph formed by features is connected.
    * @param features the collection of features
    * @param dim3 true if c(x,y,z) and c(x,y,z') are considered as different nodes
    */
    public static boolean isGraphConnected(Collection features, boolean dim3) {
        Graph<INode,FeatureAsEdge> g = GraphFactory.createGraph(features, dim3);
        return new ConnectivityInspector((UndirectedGraph<INode,FeatureAsEdge>)g)
                   .isGraphConnected();
    }
    
   /**
    * Returns a list of connected Set s of vertices.
    * @param features the collection of features
    * @param dim3 true if c(x,y,z) and c(x,y,z') are considered as different nodes
    */
    public static List createConnectedNodeSets(Collection features, boolean dim3) {
        Graph<INode,FeatureAsEdge> g = GraphFactory.createGraph(features, dim3);
        return new ConnectivityInspector((UndirectedGraph<INode,FeatureAsEdge>)g)
                   .connectedSets();
    }
    
   /**
    * Returns vertices having a deegree higher than min and lower than max as a list of
    * geometries.
    * @param features the collection of features
    * @param deegree the degree of nodes to return (inclusive)
    * @param dim3 true if c(x,y,z) and c(x,y,z') are considered as different nodes
    */
    public static List getVertices(Collection features, int degree, boolean dim3) {
        return getVertices(features, degree, degree, dim3);
    }
    
    /**
    * Returns vertices having a deegree higher than min and lower than max as a list of
    * geometries.
    * @param features the collection of features
    * @param minDegree the minimum degree of nodes to return (inclusive)
    * @param maxDegree the maximum degree of nodes to return (inclusive)
    * @param dim3 true if c(x,y,z) and c(x,y,z') are considered as different nodes
    */
    public static List getVertices(Collection features, int minDegree, int maxDegree, boolean dim3) {
        assert minDegree >= 0 : "" + minDegree + " : minDegree must be positive or null";
        assert maxDegree >= minDegree : "" + maxDegree + " : maxDegree must more or equals to minDegree";
        UndirectedGraph<INode,FeatureAsEdge> g =
            (UndirectedGraph<INode,FeatureAsEdge>)GraphFactory.createGraph(features, dim3);
        List geometries = new ArrayList();
        for (Iterator<INode> it = g.vertexSet().iterator() ; it.hasNext() ; ) {
            INode node = it.next();
            int degree = g.degreeOf(node);
            if (degree>=minDegree && degree<=maxDegree) {
                geometries.add(node.getGeometry());
            }
        }
        return geometries;
    }
    
    
    //public static List shortestPath(Collection features, boolean dim3) {
    
    //}
    
    
   /**
    * Create a graph from a collection of features
    * @param features a collection of features.
    * @param attribute the attribute used to determine the edges weight
    * @param directed if true, the returned graph is directed
    * @param dim3 true if the node are equals when x,y,z are equals
    * @return a WeightedMultigraph or a DirectedWeightedMultigraph
    */
    /*
    public static Graph createGraph(FeatureCollection features, String attribute,
                                    boolean directed, boolean dim3) {
        Graph graph;
        if (directed){graph = new DirectedWeightedMultigraph();}
        else {graph = new WeightedPseudograph();}
        for (Iterator it = features.iterator() ; it.hasNext() ; ) {
            Feature f = (Feature)it.next();
            // NODES
            Coordinate[] cc = f.getGeometry().getCoordinates();
            //GNode node1 = new GNode(cc[0]);
            INode node1;
            if (dim3) node1 = new Node3D(cc[0]);
            else node1 = new Node2D(cc[0]);
            graph.addVertex(node1);
            //GNode node2 = new GNode(cc[cc.length-1]);
            INode node2;
            if (dim3) node2 = new Node3D(cc[cc.length-1]);
            else node2 = new Node2D(cc[cc.length-1]);
            graph.addVertex(node2);
            // EDGE
            Edge edge;
            if (directed){edge = new DWEdge(node1, node2, f);}
            else {edge = new UWEdge(node1, node2, f);}
            if (attribute.equals("1")) {
                edge.setWeight(1.0);
            }
            else if (attribute.equals("Geometry length")) {
                edge.setWeight(f.getGeometry().getLength());
            }
            else {
                edge.setWeight(f.getDouble(features.getFeatureSchema().getAttributeIndex(attribute)));
            }
            graph.addEdge(edge);
        }
        return graph;
    }
    */
    
    
   /**
    * Create a graph for the features of layer filtered by filter
    * @param layer
    * @param filter a map with attribute/value pairs. Features of the layer
    * which have not these attributes values are ignored. A null value for
    * filter means no filter.
    * @param directed if true, the returned graph is directed
    * @param dim3 true if the node are equals when x,y,z are equals
    * @return a WeightedMultigraph or a DirectedWeightedMultigraph
    */
    /*
    public static Graph createGraph(FeatureCollection fc, Map filter,
                                    boolean directed, boolean dim3) {
        Graph graph;
        if (directed){graph = new DirectedWeightedMultigraph();}
        else {graph = new WeightedPseudograph();}
        for (Iterator it = fc.iterator() ; it.hasNext() ; ) {
            Feature f = (Feature)it.next();
            if (condition(f, filter)) {
                Coordinate[] cc = f.getGeometry().getCoordinates();
                // NODE 1
                //GNode node1 = new GNode(cc[0]);
                INode node1;
                if (dim3) node1 = new Node3D(cc[0]);
                else node1 = new Node2D(cc[0]);
                graph.addVertex(node1);
                // NODE 2
                //GNode node2 = new GNode(cc[cc.length-1]);
                INode node2;
                if (dim3) node2 = new Node3D(cc[cc.length-1]);
                else node2 = new Node2D(cc[cc.length-1]);
                graph.addVertex(node2);
                // EDGE
                Edge edge;
                if (directed){edge = new DWEdge(node1, node2, f);}
                else {edge = new UWEdge(node1, node2, f);}
                edge.setWeight(f.getGeometry().getLength());
                graph.addEdge(edge);
            }
        }
        return graph;
    }
    */

   /**
    * Utility to filter features to be included in the graph
    */
    /*
    public static boolean condition(Feature f, Map filter) {
        Iterator keys = filter.keySet().iterator();
        while(keys.hasNext()){
            Object key = keys.next();
            if (!f.getAttribute(key.toString()).toString().trim().equals(filter.get(key).toString())) {
                return false;
            }
        }
        return true;
    }
    */

    
   /**
    * Create a Map with each different attribute values as keys and 
    * corresponding graphs as values
    * @param fc a FeatureCollection
    * @param attributes different values of those attributes are mapped to
    * different graphs
    * @param directed if true, the returned graph is directed
    * @param dim3 true if the node are equals when x,y,z are equals
    * @return a map with concatened attribute values as keys and graphs as
    * values. Graphs may be WeightedMultigraph or DirectedWeightedMultigraph.
    */
    /*
    public static Map createGraph(FeatureCollection fc, String[] attributes,
                                  boolean directed, boolean dim3) {
        Map map = new TreeMap();
        Graph graph;
        for (Iterator it = fc.iterator() ; it.hasNext() ; ) {
            Feature f = (Feature)it.next();
            StringBuffer sb = new StringBuffer();
            for (int i = 0 ; i < attributes.length ; i++) {
                if (sb.length()>0) sb.append("\t");
                if (fc.getFeatureSchema().hasAttribute(attributes[i])) {
                    String attValue = f.getAttribute(attributes[i]).toString();
                    sb.append(attValue==null?"":attValue);
                }
            }
            if (map.get(sb.toString())==null) {
                if (directed){graph = new DirectedWeightedMultigraph();}
                else {graph = new WeightedPseudograph();}
                map.put(sb.toString(), graph);
            }
            else {graph = (Graph)map.get(sb.toString());}
            Coordinate[] cc = f.getGeometry().getCoordinates();
            // NODE 1
            //GNode node1 = new GNode(cc[0]);
            INode node1;
            if (dim3) node1 = new Node3D(cc[0]);
            else node1 = new Node2D(cc[0]);
            graph.addVertex(node1);
            // NODE 2
            //GNode node2 = new GNode(cc[cc.length-1]);
            INode node2;
            if (dim3) node2 = new Node3D(cc[cc.length-1]);
            else node2 = new Node2D(cc[cc.length-1]);
            graph.addVertex(node2);
            // EDGE
            Edge edge;
            if (directed){edge = new DWEdge(node1, node2, f);}
            else {edge = new UWEdge(node1, node2, f);}
            edge.setWeight(f.getGeometry().getLength());
            graph.addEdge(edge);
        }
        return map;
    }
    */
    
   
    
    
    
   /**
    * Create a point along a graph from a curviligne abscisse and an offset
    * @param graph a graph
    * @param node1 node of the graph with an abscisse <= pr
    * @param node2 node of the graph with an abscisse >= pr
    * @param pr abscisse along the line
    * @param offset positive offset for points located on the right side
    * of the line, negative offset fror the left side
    */
    /*
    public static Geometry createPoint(Graph graph, GNode node1, GNode node2, double pr, double distaxe) {
        List list = DijkstraShortestPath.findPathBetween(graph, node1, node2);
        if (list==null) return null;
        double longueur = 0;
        CoordinateList clist = new CoordinateList();
        // Coordonnée du noeud correspondant au PR précédent
        Coordinate lastNodeCoordinate = node1.getCoordinate();
        // Parcours des arcs composants le plus court chemin
        // Construction d'un CoordinateList joignant les noeuds node1 et node2
        for (Iterator it = list.iterator() ; it.hasNext() ; ) {
            UWEdge e = (UWEdge)it.next();
            Coordinate n1 = ((GNode)e.getSource()).getCoordinate();
            Coordinate n2 = ((GNode)e.getTarget()).getCoordinate();
            Coordinate[] cc = ((Feature)e.getLabel()).getGeometry().getCoordinates();
            if (n1.equals(lastNodeCoordinate)) {
                clist.add(cc,false);
                lastNodeCoordinate = cc[cc.length-1];
            }
            else {
                clist.add(cc,false,false);
                lastNodeCoordinate = cc[0];
            }
            longueur += e.getWeight();
        }
        double pr1 = ((Double)node1.getLabel()).doubleValue();
        double pr2 = ((Double)node2.getLabel()).doubleValue();
        double f = (pr2-pr1)/longueur;
        Coordinate[] cc = clist.toCoordinateArray();
        //if (cc.length==0) cc = new Coordinate[]{node1.getCoordinate();}
        Coordinate c1 = cc[0];
        Coordinate c2 = cc[0];
        pr2=pr1;
        // Parcours de l'arc node1-node2 segment par segment
        for (int i = 1 ; i < cc.length ; i++) {
            c1 = cc[i-1];
            c2 = cc[i];
            pr1 = pr2;
            pr2 = pr1 + (c1.distance(c2))*f;
            if (pr2>pr) break;
        }
        double r = (pr-pr1)/(pr2-pr1);
        // Coordonnée interpolée
        Coordinate ic = new Coordinate(c1.x+(c2.x-c1.x)*r, c1.y+(c2.y-c1.y)*r);
        // Interpolation du z si possible
        if ((!Double.isNaN(c1.z))&&(!Double.isNaN(c2.z))) {
            ic.z = c1.z + (c2.z-c1.z)*c1.distance(ic)/c1.distance(c2);
        }
        // Décalage perpendiculairement à l'axe
        double cc2 = ic.distance(c2);
        double dx = distaxe*(c2.y-ic.y)/cc2;
        double dy = -distaxe*(c2.x-ic.x)/cc2;
        Coordinate finalc = new Coordinate(ic.x +dx, ic.y+dy, ic.z);
        return new GeometryFactory().createPoint(finalc);
    }
    */
    
   /**
    * Create a line along a graph from 2 curviligne abscisses and offsets
    * @param graph a graph
    * @param node1 node of the graph with an abscisse <= pr
    * @param node2 node of the graph with an abscisse >= pr
    * @param pr abscisse along the line
    * @param offset positive offset for points located on the right side
    * of the line, negative offset fror the left side
    */
    /*
    public static Geometry createPoint(Graph graph, GNode node1, GNode node2,
                                       double pri, double prf,
                                       double distaxe1, double distaxe2) {
        List list = DijkstraShortestPath.findPathBetween(graph, node1, node2);
        if (list==null) return null;
        double longueur = 0;
        CoordinateList clist = new CoordinateList();
        Coordinate lastNodeCoordinate = node1.getCoordinate();
        for (Iterator it = list.iterator() ; it.hasNext() ; ) {
            UWEdge e = (UWEdge)it.next();
            Coordinate n1 = ((GNode)e.getSource()).getCoordinate();
            Coordinate n2 = ((GNode)e.getTarget()).getCoordinate();
            Coordinate[] cc = ((Feature)e.getLabel()).getGeometry().getCoordinates();
            if (n1.equals(lastNodeCoordinate)) {
                clist.add(cc,false);
                lastNodeCoordinate = cc[cc.length-1];
            }
            else {
                clist.add(cc,false,false);
                lastNodeCoordinate = cc[0];
            }
            longueur += e.getWeight();
        }
        double pr1 = ((Double)node1.getLabel()).doubleValue();
        double pr2 = ((Double)node2.getLabel()).doubleValue();
        double f = (pr2-pr1)/longueur;
        Coordinate[] cc = clist.toCoordinateArray();
        Coordinate c1 = cc[0];
        Coordinate c2 = cc[0];
        pr2=pr1;
        CoordinateList clist2 = new CoordinateList();
        boolean outside = true;
        for (int i = 1 ; i < cc.length ; i++) {
            c1 = cc[i-1];
            c2 = cc[i];
            pr1 = pr2;
            pr2 = pr1 + (c1.distance(c2))*f;
            if (pr2>pri && outside == true) {
                outside = false;
                double r = (pri-pr1)/(pr2-pr1);
                Coordinate ic = new Coordinate(c1.x+(c2.x-c1.x)*r, c1.y+(c2.y-c1.y)*r);
                if ((!Double.isNaN(c1.z))&&(!Double.isNaN(c2.z))) {
                    ic.z = c1.z + (c2.z-c1.z)*c1.distance(ic)/c1.distance(c2);
                }
                double cc2 = ic.distance(c2);
                double dx = distaxe1*(c2.y-ic.y)/cc2;
                double dy = -distaxe1*(c2.x-ic.x)/cc2;
                clist2.add(new Coordinate(ic.x +dx, ic.y+dy, ic.z),false);
            }
            else if (prf<pr2 && outside==false) {
                outside = true;
                double r = (prf-pr1)/(pr2-pr1);
                Coordinate ic = new Coordinate(c1.x+(c2.x-c1.x)*r, c1.y+(c2.y-c1.y)*r);
                if ((!Double.isNaN(c1.z))&&(!Double.isNaN(c2.z))) {
                    ic.z = c1.z + (c2.z-c1.z)*c1.distance(ic)/c1.distance(c2);
                }
                double cc2 = ic.distance(c2);
                double dx = distaxe2*(c2.y-ic.y)/cc2;
                double dy = -distaxe2*(c2.x-ic.x)/cc2;
                clist2.add(new Coordinate(ic.x +dx, ic.y+dy, ic.z),false);
            }
            else if (outside==true){
                
            }
        }
        //return new GeometryFactory().createLineString(clist2);
        return null;
    }
    */
    
    /*
    double[] getPR(Graph graph, GNode node1, GNode node2, Coordinate c){return null;}
    */
    
}

