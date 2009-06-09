/* OrbisCAD. The Community cartography editor
 *
 * Copyright (C) 2005, 2006 OrbisCAD development team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  OrbisCAD development team
 *   elgallego@users.sourceforge.net
 */
package org.gdms.model;

import org.gdms.data.DataSource;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class FeatureAdapter implements Feature, Comparable {

	private DataSource ds;

	private int index;

	private int sfi;

	public FeatureAdapter(DataSource ds, int index,
			int spatialFieldIndex) {
		this.ds = ds;
		this.index = index;
		this.sfi = spatialFieldIndex;

	}

	public int hashCode() {
		return index;
	}

	public boolean equals(Object obj) {
		if (obj instanceof FeatureAdapter) {
			FeatureAdapter fa = (FeatureAdapter) obj;
			if ((fa.index == index) && (fa.ds == ds)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public void setAttributes(Object[] attributes) {
		// TODO Auto-generated method stub

	}

	public void setSchema(FeatureSchema schema) {

	}

	public int getID() {
		return index;
	}

	public void setAttribute(int attributeIndex, Object newAttribute) {
		try {
			ds.setFieldValue(index, attributeIndex,
					mappGDMSAttributes(newAttribute));
		} catch (DriverException e) {
			throw new RuntimeException(e);
		}
	}

	public void setAttribute(String attributeName, Object newAttribute) {

		try {
			ds.setFieldValue(index, ds.getFieldIndexByName(attributeName),
					mappGDMSAttributes(newAttribute));
		} catch (DriverException e) {
			throw new RuntimeException(e);
		}

	}

	public void setGeometry(Geometry geometry) {

		try {
			ds.setFieldValue(index, sfi, ValueFactory
					.createValue(geometry));
		} catch (DriverException e) {
			e.printStackTrace();
		}

	}

	public Object getAttribute(int i) {
		try {
			return ds.getFieldValue(index, i);
		} catch (DriverException e) {
			throw new RuntimeException(e);
		}
	}

	public Object getAttribute(String name) {
		try {
			return ds.getFieldValue(index, ds.getFieldIndexByName(name));
		} catch (DriverException e) {
			throw new RuntimeException(e);
		}
	}

	public String getString(int attributeIndex) {
		try {
			return ds.getString(index, attributeIndex);
		} catch (DriverException e) {
			throw new RuntimeException(e);
		}
	}

	public int getInteger(int attributeIndex) {
		try {
			return ds.getInt(index, attributeIndex);
		} catch (DriverException e) {
			throw new RuntimeException(e);
		}
	}

	public double getDouble(int attributeIndex) {
		try {
			return ds.getDouble(index, attributeIndex);
		} catch (DriverException e) {
			throw new RuntimeException(e);
		}
	}

	public String getString(String attributeName) {

		try {
			return ds.getString(index, ds.getFieldIndexByName(attributeName));
		} catch (DriverException e) {
			throw new RuntimeException(e);
		}
	}

	public Geometry getGeometry() {
		Value v;
		Geometry geom = null;
		try {
			v = ds.getFieldValue(index, sfi);

			if (v.isNull()) {
				GeometryFactory gf = new GeometryFactory();
				String type = v.getAsGeometry().getGeometryType();
				if (type.equals("POINT")) {
					geom = gf.createPoint((CoordinateSequence) null);
				} else if (type.equals("MULTIPOINT")) {
					return gf.createMultiPoint((Point[]) null);
				}

				else if (type.equals("LINESTRING")) {
					geom = gf.createLineString((Coordinate[]) null);
				} else if (type.equals("MULTILINESTRING")) {
					geom = gf.createMultiLineString(null);
				}

				else if (type.equals("POLYGON")) {
					geom = gf.createPolygon(null, null);
				}

				else if (type.equals("MULTIPOLYGON")) {
					geom = gf.createMultiPolygon(null);
				}

			} else {
				geom = v.getAsGeometry();
			}
		} catch (DriverException e) {
			System.out.println(e);
		}
		return geom;

	}

	public FeatureSchema getSchema() {
		return new FeatureSchemaAdapter(ds, sfi);
	}

	public Object clone() {
		BasicFeature ret = new BasicFeature(getSchema());
		ret.setAttributes(getAttributes());

		return ret;
	}

	public Feature clone(boolean deep) {
		return (Feature) clone();
	}

	public Object[] getAttributes() {
		Object[] atts = new Object[getSchema().getAttributeCount()];
		for (int i = 0; i < atts.length; i++) {
			atts[i] = getAttribute(i);
		}

		return atts;
	}

	public int compareTo(Object o) {
		if (o instanceof FeatureAdapter) {
			FeatureAdapter fa = (FeatureAdapter) o;
			if (fa.ds != ds) {
				return -1;
			} else {
				return fa.index - index;
			}
		} else {
			return -1;
		}
	}

	public Value mappGDMSAttributes(Object attribute) {

		if (attribute instanceof Integer) {
			Integer newatt = (Integer) attribute;
			return ValueFactory.createValue(newatt);
		} else if (attribute instanceof String) {
			String newatt = (String) attribute;
			return ValueFactory.createValue(newatt);

		} else if (attribute instanceof Double) {
			Double newatt = (Double) attribute;
			return ValueFactory.createValue(newatt);

		}

		return ValueFactory.createNullValue();

	}

}
