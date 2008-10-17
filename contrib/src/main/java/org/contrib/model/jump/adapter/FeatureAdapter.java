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
package org.contrib.model.jump.adapter;

import org.contrib.model.jump.model.BasicFeature;
import org.contrib.model.jump.model.Feature;
import org.contrib.model.jump.model.FeatureSchema;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class FeatureAdapter implements Feature, Comparable {

	private SpatialDataSourceDecorator ds;

	private int index;

	private int sfi;

	public FeatureAdapter(SpatialDataSourceDecorator ds, int index) {
		this.ds = ds;
		this.index = index;
		try {
			this.sfi = ds.getSpatialFieldIndex();
		} catch (DriverException e) {
			throw new RuntimeException(e);
		}
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
		// TODO Auto-generated method stub

	}

	public int getID() {
		return index;
	}

	public void setAttribute(int attributeIndex, Object newAttribute) {
		try {
			ds.setFieldValue(index, attributeIndex, (Value) newAttribute);
		} catch (DriverException e) {
			throw new RuntimeException(e);
		}
	}

	public void setAttribute(String attributeName, Object newAttribute) {
		// TODO Auto-generated method stub

	}

	public void setGeometry(Geometry geometry) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub
		return null;
	}

	public int getInteger(int attributeIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getDouble(int attributeIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getString(String attributeName) {
		// TODO Auto-generated method stub
		return null;
	}

	public Geometry getGeometry() {
		Value v;
		Geometry geom = null;
		try {
			v = ds.getFieldValue(index, sfi);

			if (v.isNull()) {
				GeometryFactory gf = new GeometryFactory();
				String type = ds.getGeometry(sfi).getGeometryType();
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
				geom =  v.getAsGeometry();
			}
		} catch (DriverException e) {
			System.out.println(e);
		}
		return geom;

	}

	public FeatureSchema getSchema() {
		return new FeatureSchemaAdapter(ds);
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

}
