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
		throw new RuntimeException();
	}

	public Object getAttribute(int index) {
		throw new RuntimeException();
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
				int fieldType = ds.getDataSourceMetadata().getFieldType(i);
				switch (fieldType) {
				case Value.DOUBLE:
					ret[i] = new Double(((DoubleValue)v).getValue());
					break;
				case Value.INT:
					ret[i] = new Integer(((IntValue)v).getValue());
					break;
				case Value.FLOAT:
					ret[i] = new Float(((FloatValue)v).getValue());
					break;
				case Value.SHORT:
					ret[i] = new Short(((ShortValue)v).getValue());
					break;
				case Value.BYTE:
					ret[i] = new Byte(((ByteValue)v).getValue());
					break;
				case Value.LONG:
					ret[i] = new Long(((LongValue)v).getValue());
					break;
				case Value.BOOLEAN:
					ret[i] = new Byte((byte)(((BooleanValue)v).getValue()?1:0));
					break;
				case Value.STRING:
					ret[i] = new String(((StringValue)v).getValue());
					break;
				case Value.DATE:
					ret[i] = ((DateValue)v).getValue();
					break;
				case Value.TIMESTAMP:
					ret[i] = ((TimestampValue)v).getValue();
					break;
				case Value.TIME:
					ret[i] = ((TimeValue)v).getValue();
					break;
				case Value.BINARY:
					//TODO
					throw new UnsupportedOperationException();
				case PTTypes.GEOMETRY:
					ret[i] = ((GeometryValue)v).getGeom();
					break;
				}
			}

			return ret;
		} catch (DriverException e) {
			throw new RuntimeException(e);
		}
	}

	public Envelope getBounds() {
		throw new RuntimeException();
	}

	public Geometry getDefaultGeometry() {
		try {
			return ds.getGeometry(rowIndex);
		} catch (DriverException e) {
			throw new RuntimeException();
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
		throw new RuntimeException();
	}

	public FeatureCollection getParent() {
		throw new RuntimeException();
	}

	public void setAttribute(int position, Object val)
			throws IllegalAttributeException, ArrayIndexOutOfBoundsException {
		throw new RuntimeException();
	}

	public void setAttribute(String xPath, Object attribute)
			throws IllegalAttributeException {
		throw new RuntimeException();
	}

	public void setDefaultGeometry(Geometry geometry)
			throws IllegalAttributeException {
		throw new RuntimeException();
	}

	public void setParent(FeatureCollection collection) {
		throw new RuntimeException();
	}

}
