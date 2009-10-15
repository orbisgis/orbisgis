package fr.michaelm.jump.feature.jgrapht;

import org.gdms.model.Feature;
import org.gdms.model.FeatureSchema;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * This class implements both Feature from jump library and INode from jump-jgrapht bridge.<br>
 * initial release
 * @author Michael Michaud
 * @version 0.3 (2008-02-02)
 */
public class FeatureAsNode implements INode {

    private Feature feature;

    public static final GeometryFactory DEFAULT_GEOMETRY_FACTORY = new GeometryFactory();

   /**
    * Create Feature as an edge of a weighted graph
    * @param feature the feature as an edge of a graph.
    */
    public FeatureAsNode(Feature feature) {
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

    // This method is common to Feature interface and INode interface
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

   // INode implementation
   /**
    * Return the coordinate of this Node.
    */
    public Coordinate getCoordinate() {
        return feature.getGeometry().getInteriorPoint().getCoordinate();
    }

}

