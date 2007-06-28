package org.gdms.geotoolsAdapter;

import org.gdms.data.types.Type;
import org.gdms.data.values.BooleanValue;
import org.gdms.data.values.DateValue;
import org.gdms.data.values.NullValue;
import org.gdms.data.values.NumericValue;
import org.gdms.data.values.StringValue;
import org.gdms.data.values.TimeValue;
import org.gdms.data.values.TimestampValue;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.spatial.GeometryValue;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureType;
import org.geotools.feature.IllegalAttributeException;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class FeatureAdapter implements Feature {

	private SpatialDataSourceDecorator ds;

	private int rowIndex;

	public FeatureAdapter(SpatialDataSourceDecorator ds, int i) {
		this.ds = ds;
		this.rowIndex = i;
	}

	public Object getAttribute(String xPath) {
		try {
			int fieldIndex = ds.getFieldIndexByName(xPath);
			return value2Object(ds.getFieldValue(rowIndex, fieldIndex),
					fieldIndex);
		} catch (DriverException e) {
			throw new Error();
		}
	}

	public Object getAttribute(int index) {
		throw new Error();
	}

	private Object value2Object(Value v, int fieldId) throws DriverException {
		if (v instanceof NullValue) {
			return null;
		}
		Object ret = null;
		int fieldType = ds.getMetadata().getFieldType(fieldId).getTypeCode();
		switch (fieldType) {
		case Type.DOUBLE:
			ret = new Double(((NumericValue) v).doubleValue());
			break;
		case Type.INT:
			ret = new Integer(((NumericValue) v).intValue());
			break;
		case Type.FLOAT:
			ret = new Float(((NumericValue) v).floatValue());
			break;
		case Type.SHORT:
			ret = new Short(((NumericValue) v).shortValue());
			break;
		case Type.BYTE:
			ret = new Byte(((NumericValue) v).byteValue());
			break;
		case Type.LONG:
			ret = new Long(((NumericValue) v).longValue());
			break;
		case Type.BOOLEAN:
			ret = new Byte((byte) (((BooleanValue) v).getValue() ? 1 : 0));
			break;
		case Type.STRING:
			ret = new String(((StringValue) v).getValue());
			break;
		case Type.DATE:
			ret = ((DateValue) v).getValue();
			break;
		case Type.TIMESTAMP:
			ret = ((TimestampValue) v).getValue();
			break;
		case Type.TIME:
			ret = ((TimeValue) v).getValue();
			break;
		case Type.BINARY:
			// TODO
			throw new UnsupportedOperationException();
		case Type.GEOMETRY:
			ret = ((GeometryValue) v).getGeom();
			break;
		}

		return ret;
	}

	public Object[] getAttributes(Object[] attributes) {
		/*
		 * TODO
		 */
		try {
			int fieldCount = ds.getMetadata().getFieldCount();
			Object[] ret = new Object[fieldCount];
			for (int i = 0; i < ret.length; i++) {
				Value v = ds.getFieldValue(this.rowIndex, i);
				ret[i] = value2Object(v, i);
			}

			return ret;
		} catch (DriverException e) {
			throw new Error(e);
		}
	}

	public Envelope getBounds() {
		throw new Error();
	}

	public Geometry getDefaultGeometry() {
		try {
			return ds.getGeometry(rowIndex);
		} catch (DriverException e) {
			throw new Error();
		}
	}

	public FeatureType getFeatureType() {
		// TODO
		return new FeatureTypeAdapter(ds);
	}

	public String getID() {
		return Integer.toString(rowIndex);
	}

	public int getNumberOfAttributes() {
		throw new Error();
	}

	public FeatureCollection getParent() {
		throw new Error();
	}

	public void setAttribute(int position, Object val)
			throws IllegalAttributeException, ArrayIndexOutOfBoundsException {
		throw new Error();
	}

	public void setAttribute(String xPath, Object attribute)
			throws IllegalAttributeException {
		throw new Error();
	}

	public void setDefaultGeometry(Geometry geometry)
			throws IllegalAttributeException {
		throw new Error();
	}

	public void setParent(FeatureCollection collection) {
		throw new Error();
	}

}
