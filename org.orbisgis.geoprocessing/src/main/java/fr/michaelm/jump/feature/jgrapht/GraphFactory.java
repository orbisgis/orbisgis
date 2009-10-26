package fr.michaelm.jump.feature.jgrapht;

import java.util.*;

import org.gdms.model.AttributeType;
import org.gdms.model.Feature;
import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.alg.*;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.index.strtree.STRtree;

/**
 * This utility class offers static methods to build graphs from feature
 * collections.
 * @author Michael Michaud
 * @version 0.1 (2007-04-20)
 */

public class GraphFactory {

    /**
     * Relation used to connect features.
     */
    protected static enum Relation
    {
        /**
         * Two FeatureAsNode are connected if they intersect .
         */
        INTERSECTS,
        /**
         * Two FeatureAsNode are connected if they touch.
         */
        TOUCHES,
        /**
         * Two FeatureAsNode are near from each other.
         */
        ISWITHIN
    };

   /**
    * Create an undirected graph from a collection of features
    * @param features a collection of features.
    * @param dim3 true means that nodes are evaluated equals when x,y,z are equals
    * @return a WeightedMultigraph or a DirectedWeightedMultigraph
    */
    public static WeightedGraph<INode,FeatureAsEdge>
        createGraph(Collection features, boolean dim3) {
        WeightedGraph<INode,FeatureAsEdge> graph = new WeightedPseudograph(FeatureAsEdge.class);
        return add(graph, features, dim3);
    }

   /**
    * Create a directed graph from a collection of features
    * @param features a collection of features.
    * @param dim3 true means that nodes are evaluated equals when x,y,z are equals
    * @return a WeightedMultigraph or a DirectedWeightedMultigraph
    */
    public static WeightedGraph<INode,FeatureAsEdge>
        createDirectedGraph(Collection features, boolean dim3) {
        WeightedGraph<INode,FeatureAsEdge> graph = new DirectedWeightedMultigraph(FeatureAsEdge.class);
        return add(graph, features, dim3);
    }

   /**
    * Create an undirected weighted graph
    * from a collection of features and a topological relation
    * @param features a collection of features.
    * @param relation the relation defining edges
    * @return a WeightedMultigraph with Features as nodes and relation as edges
    */
    public static WeightedGraph<FeatureAsNode,FeatureRelation>
        createGraph(Collection features, Relation relation) {
        WeightedGraph<FeatureAsNode,FeatureRelation> graph = new WeightedMultigraph(FeatureRelation.class);
        return add(graph, features, relation, 0);
    }

       /**
    * Create an undirected weighted graph
    * from a collection of features and a topological relation
    * @param features a collection of features.
    * @param relation the relation defining edges
    * @return a WeightedMultigraph with Features as nodes and relation as edges
    */
    public static WeightedGraph<FeatureAsNode,FeatureRelation>
        createGraph(Collection features, double maxdist) {
        WeightedGraph<FeatureAsNode,FeatureRelation> graph = new WeightedMultigraph(FeatureRelation.class);
        return add(graph, features, Relation.ISWITHIN, maxdist);
    }

   /**
    * Create an undirected graph from a collection of features with a weight attribute
    * @param features a collection of features.
    * @param direct_weight attribute containing the weight of the edge in the linestring
    * direction (a negative weight is interpreted as "no edge for this direction")
    * @param inverse_weight attribute containing the weight of the edge in the inverse
    * linestring direction (a negative weight is interpreted as "no edge for this direction")
    * @param dim3 true means that nodes are evaluated equals when x,y,z are equals
    * @return a WeightedMultigraph or a DirectedWeightedMultigraph
    */
    public static WeightedGraph<INode,FeatureAsEdge> createGraph(Collection features,
                                                                 String direct_weight,
                                                                 String inverse_weight,
                                                                 boolean dim3) {
        DirectedWeightedMultigraph<INode,FeatureAsEdge> graph =
            new DirectedWeightedMultigraph(FeatureAsEdge.class);
        return add(graph, features, direct_weight, inverse_weight, dim3);
    }

    private static WeightedGraph<INode,FeatureAsEdge>
        add(WeightedGraph<INode,FeatureAsEdge> graph, Collection features, boolean dim3) {
        Coordinate[] cc;
        for (Iterator it = features.iterator() ; it.hasNext() ; ) {
            Feature f = (Feature)it.next();
            Geometry g = f.getGeometry();
            cc = f.getGeometry().getCoordinates();
            INode node1 = dim3? new Node3D(cc[0]) : new Node2D(cc[0]);
            INode node2 = dim3? new Node3D(cc[cc.length-1]) : new Node2D(cc[cc.length-1]);
            graph.addVertex(node1);
            graph.addVertex(node2);
            FeatureAsEdge edge = new FeatureAsEdge(f);
            graph.addEdge(node1, node2, edge);
            graph.setEdgeWeight(edge, g.getLength());
        }
        return graph;
    }

    private static WeightedGraph<FeatureAsNode,FeatureRelation>
        add(WeightedGraph<FeatureAsNode,FeatureRelation> graph, Collection features,
            Relation relation, double maxdist) {
        //Coordinate[] cc;
        Collection<FeatureAsNode> featureAsNodes = new ArrayList<FeatureAsNode>();
        STRtree index = new STRtree();
        for (Iterator it = features.iterator() ; it.hasNext() ; ) {
            FeatureAsNode f = new FeatureAsNode((Feature)it.next());
            index.insert(f.getGeometry().getEnvelopeInternal(), f);
            featureAsNodes.add(f);
        }
        for (Iterator<FeatureAsNode> it = featureAsNodes.iterator() ; it.hasNext() ; ) {
            FeatureAsNode f = it.next();
            Envelope env = f.getGeometry().getEnvelopeInternal();
            env.expandBy(maxdist);
            List<FeatureAsNode> list = (List<FeatureAsNode>)index.query(env);
            for (FeatureAsNode candidate : list) {
                if (candidate == f) continue;
                if (relation==Relation.INTERSECTS &&
                    f.getGeometry().intersects(candidate.getGeometry())) {
                    graph.addVertex(f);
                    graph.addVertex(candidate);
                    FeatureRelation fr = new FeatureRelation(Relation.INTERSECTS);
                    graph.addEdge(f, candidate, fr);
                }
                else if (relation==Relation.TOUCHES &&
                    f.getGeometry().touches(candidate.getGeometry())) {
                    graph.addVertex(f);
                    graph.addVertex(candidate);
                    FeatureRelation fr = new FeatureRelation(Relation.TOUCHES);
                    graph.addEdge(f, candidate, fr);
                }
                else if (relation==Relation.ISWITHIN &&
                    f.getGeometry().distance(candidate.getGeometry())<=maxdist) {
                    graph.addVertex(f);
                    graph.addVertex(candidate);
                    FeatureRelation fr = new FeatureRelation(Relation.ISWITHIN);
                    graph.addEdge(f, candidate, fr);
                    graph.setEdgeWeight(fr, f.getGeometry().distance(candidate.getGeometry()));
                }
            }
        }
        return graph;
    }


    private static WeightedGraph<INode,FeatureAsEdge>
        add(DirectedWeightedMultigraph<INode,FeatureAsEdge> graph,
            Collection features,
            String direct_weight,
            String inverse_weight,
            boolean dim3) {
        Coordinate[] cc;
        for (Iterator it = features.iterator() ; it.hasNext() ; ) {
            Feature f = (Feature)it.next();
            Geometry g = f.getGeometry();
            cc = f.getGeometry().getCoordinates();
            INode node1 = dim3? new Node3D(cc[0]) : new Node2D(cc[0]);
            INode node2 = dim3? new Node3D(cc[cc.length-1]) : new Node2D(cc[cc.length-1]);
            graph.addVertex(node1);
            graph.addVertex(node2);
            FeatureAsEdge edge = new FeatureAsEdge(f);
            if (f.getSchema().hasAttribute("direct_weight") &&
                f.getSchema().getAttributeType("direct_weight") == AttributeType.DOUBLE) {
                double weight = f.getDouble(f.getSchema().getAttributeIndex("direct_weight"));
                if (weight >= 0) {
                    graph.addEdge(node1, node2, edge);
                    graph.setEdgeWeight(edge, weight);
                }
            }
            if (f.getSchema().hasAttribute("inverse_weight") &&
                f.getSchema().getAttributeType("inverse_weight") == AttributeType.DOUBLE) {
                double weight = f.getDouble(f.getSchema().getAttributeIndex("inverse_weight"));
                if (weight >= 0) {
                    graph.addEdge(node2, node1, edge);
                    graph.setEdgeWeight(edge, weight);
                }
            }
        }
        return graph;
    }






    public static class FeatureRelation {

        Relation relation;
        //double distance;

        public FeatureRelation(Relation rel) {
            this.relation = rel;
        }
/*
        public FeatureRelation(double dist) {
            this.relation = Relation.NEAR;
            this.distance = dist;
        }
*/
        public String toString() {
            return relation.toString();
        }
    }

}

