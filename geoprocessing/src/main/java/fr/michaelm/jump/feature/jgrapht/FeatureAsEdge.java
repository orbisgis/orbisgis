package fr.michaelm.jump.feature.jgrapht;

import java.util.*;

import org.gdms.model.Feature;
import org.gdms.model.FeatureSchema;
import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.alg.*;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * Encapsulate a Feature into a DefaultWeightedEdge
 * [NOTE : this is the only method I found to use weighted graph, because AbstractBaseGraph
 * use the following assert : assert (e instanceof DefaultWeightedEdge)]
 * collections.
 * @author Michael Michaud
 * @version 0.1 (2007-04-21)
 */

public class FeatureAsEdge extends DefaultWeightedEdge implements Feature {

    private Feature feature;

   /**
    * Create Feature as an edge of a weighted graph
    * @param feature the feature as an edge of a graph.
    */
    public FeatureAsEdge(Feature feature) {
        this.feature = feature;
    }

    public Feature getFeature() {
        return feature;
    }

    // Implementation of Feature interface using the Decorator pattern

    public Object clone() {
        return feature.clone();
    }

    public Feature clone(boolean deep) {
        return feature.clone(deep);
    }

    public Object getAttribute(int i) {
        return feature.getAttribute(i);
    }

    public Object getAttribute(String name) {
        return feature.getAttribute(name);
    }

    public Object[] getAttributes() {
        return feature.getAttributes();
    }
    public double getDouble(int attributeIndex) {
        return feature.getDouble(attributeIndex);
    }

    public Geometry getGeometry() {
        return feature.getGeometry();
    }

    public int getID() {
        return feature.getID();
    }

    public int getInteger(int attributeIndex) {
        return feature.getInteger(attributeIndex);
    }

    public FeatureSchema getSchema() {
        return feature.getSchema();
    }

    public String getString(int attributeIndex) {
        return feature.getString(attributeIndex);
    }

    public String getString(java.lang.String attributeName) {
        return feature.getString(attributeName);
    }

    public void setAttribute(int attributeIndex, Object newAttribute) {
        feature.setAttribute(attributeIndex, newAttribute);
    }

    public void setAttribute(String attributeName, Object newAttribute) {
        feature.setAttribute(attributeName, newAttribute);
    }

    public void setAttributes(Object[] attributes) {
        feature.setAttributes(attributes);
    }

    public void setGeometry(Geometry geometry) {
        feature.setGeometry(geometry);
    }

    public void setSchema(FeatureSchema schema) {
        feature.setSchema(schema);
    }

    public int compareTo(Object o) {
        return feature.compareTo(o);
    }
}

