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
		throw new Error();
	}

	public Filter getRestriction() {
		throw new Error();
	}

	public Class getType() {
		throw new Error();
	}

	public boolean isGeometry() {
		throw new Error();
	}

	public Object createDefaultValue() {
		throw new Error();
	}

	public Object duplicate(Object src) throws IllegalAttributeException {
		throw new Error();
	}

	public int getMaxOccurs() {
		throw new Error();
	}

	public int getMinOccurs() {
		throw new Error();
	}

	public String getName() {
		throw new Error();
	}

	public boolean isNillable() {
		throw new Error();
	}

	public Object parse(Object value) throws IllegalArgumentException {
		throw new Error();
	}

	public void validate(Object obj) throws IllegalArgumentException {
		throw new Error();
	}

}
