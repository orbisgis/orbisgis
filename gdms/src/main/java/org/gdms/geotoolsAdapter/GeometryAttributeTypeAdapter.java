package org.gdms.geotoolsAdapter;

import org.geotools.feature.GeometryAttributeType;
import org.geotools.feature.IllegalAttributeException;
import org.geotools.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.GeometryFactory;

public class GeometryAttributeTypeAdapter implements GeometryAttributeType {

	public static CoordinateReferenceSystem currentCRS;

	public CoordinateReferenceSystem getCoordinateSystem() {
		//TODO we use the default CRS in MapContext
		return currentCRS;
	}

	public GeometryFactory getGeometryFactory() {
		throw new RuntimeException();
	}

	public Filter getRestriction() {
		throw new RuntimeException();
	}

	public Class getType() {
		throw new RuntimeException();
	}

	public boolean isGeometry() {
		throw new RuntimeException();
	}

	public Object createDefaultValue() {
		throw new RuntimeException();
	}

	public Object duplicate(Object src) throws IllegalAttributeException {
		throw new RuntimeException();
	}

	public int getMaxOccurs() {
		throw new RuntimeException();
	}

	public int getMinOccurs() {
		throw new RuntimeException();
	}

	public String getName() {
		throw new RuntimeException();
	}

	public boolean isNillable() {
		throw new RuntimeException();
	}

	public Object parse(Object value) throws IllegalArgumentException {
		throw new RuntimeException();
	}

	public void validate(Object obj) throws IllegalArgumentException {
		throw new RuntimeException();
	}

}
