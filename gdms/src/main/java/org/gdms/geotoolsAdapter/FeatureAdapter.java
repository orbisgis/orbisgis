package org.gdms.geotoolsAdapter;

import org.gdms.data.values.BooleanValue;
import org.gdms.data.values.ByteValue;
import org.gdms.data.values.DateValue;
import org.gdms.data.values.DoubleValue;
import org.gdms.data.values.FloatValue;
import org.gdms.data.values.IntValue;
import org.gdms.data.values.LongValue;
import org.gdms.data.values.ShortValue;
import org.gdms.data.values.StringValue;
import org.gdms.data.values.TimeValue;
import org.gdms.data.values.TimestampValue;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.spatial.GeometryValue;
import org.gdms.spatial.PTTypes;
import org.gdms.spatial.SpatialDataSource;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureType;
import org.geotools.feature.IllegalAttributeException;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class FeatureAdapter implements Feature {

	private SpatialDataSource ds;

	private int rowIndex;

	public FeatureAdapter(SpatialDataSource ds, int i) {
		this.ds = ds;
		this.rowIndex = i;
	}

	public Object getAttribute(String xPath) {
		try {
			int fieldIndex = ds.getFieldIndexByName(xPath);
			return value2Object(ds.getFieldValue(rowIndex, fieldIndex), fieldIndex);
		} catch (DriverException e) {
			throw new Error();
		}
	}

	public Object getAttribute(int index) {
		throw new Error();
	}

	private Object value2Object(Value v, int fieldId) throws DriverException {
		Object ret = null;
		int fieldType = ds.getDataSourceMetadata().getFieldType(fieldId);
		switch (fieldType) {
		case Value.DOUBLE:
			ret = new Double(((DoubleValue) v).getValue());
			break;
		case Value.INT:
			ret = new Integer(((IntValue) v).getValue());
			break;
		case Value.FLOAT:
			ret = new Float(((FloatValue) v).getValue());
			break;
		case Value.SHORT:
			ret = new Short(((ShortValue) v).getValue());
			break;
		case Value.BYTE:
			ret = new Byte(((ByteValue) v).getValue());
			break;
		case Value.LONG:
			ret = new Long(((LongValue) v).getValue());
			break;
		case Value.BOOLEAN:
			ret = new Byte((byte) (((BooleanValue) v).getValue() ? 1 : 0));
			break;
		case Value.STRING:
			ret = new String(((StringValue) v).getValue());
			break;
		case Value.DATE:
			ret = ((DateValue) v).getValue();
			break;
		case Value.TIMESTAMP:
			ret = ((TimestampValue) v).getValue();
			break;
		case Value.TIME:
			ret = ((TimeValue) v).getValue();
			break;
		case Value.BINARY:
			// TODO
			throw new UnsupportedOperationException();
		case PTTypes.GEOMETRY:
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
			int fieldCount = ds.getDataSourceMetadata().getFieldCount();
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
		return ds.getFID(rowIndex).toString();
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
