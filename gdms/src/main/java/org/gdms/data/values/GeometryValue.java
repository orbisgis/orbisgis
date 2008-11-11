/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.data.values;

import org.gdms.data.types.Type;
import org.gdms.sql.strategies.IncompatibleTypesException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

class GeometryValue extends AbstractValue {

	private Geometry geom;

	private static final WKBWriter writer3D = new WKBWriter(3);
	private static final WKBWriter writer2D = new WKBWriter();

	private static final WKTWriter textWriter3D = new WKTWriter(3);
	private static final WKTWriter textWriter2D = new WKTWriter();

	private static final WKBReader wkbReader = new WKBReader();
	private static final WKTReader wktReader = new WKTReader();

	public GeometryValue(Geometry g) {
		this.geom = g;
	}

	public String getStringValue(ValueWriter writer) {
		return writer.getStatementString(geom);
	}

	public int getType() {
		return Type.GEOMETRY;
	}

	public Geometry getGeom() {
		return geom;
	}

	public int doHashCode() {
		Coordinate coord = geom.getCoordinate();
		return (int) (coord.x + coord.y);
	}

	@Override
	public Value equals(Value obj) {
		if (obj.getType() == Type.STRING) {
			return ValueFactory.createValue(obj.equals(this.geom.toText()));
		} else {
			return ValueFactory.createValue(equalsExact(geom, obj
					.getAsGeometry()));
		}
	}

	private boolean equalsExact(Geometry geom1, Geometry geom2) {
		if (geom1 instanceof GeometryCollection) {
			GeometryCollection gc1 = (GeometryCollection) geom1;
			if (geom2 instanceof GeometryCollection) {
				GeometryCollection gc2 = (GeometryCollection) geom2;
				for (int i = 0; i < gc1.getNumGeometries(); i++) {
					if (!equalsExact(gc1.getGeometryN(i), gc2.getGeometryN(i))) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		} else {
			if (geom1.getGeometryType().equals(geom2.getGeometryType())) {
				Coordinate[] coords1 = geom1.getCoordinates();
				Coordinate[] coords2 = geom2.getCoordinates();
				if (coords1.length != coords2.length) {
					return false;
				} else {
					for (int i = 0; i < coords2.length; i++) {
						Coordinate c1 = coords1[i];
						Coordinate c2 = coords2[i];
						if (c1.equals(c2)) {
							if (Double.isNaN(c1.z)) {
								return Double.isNaN(c2.z);
							} else if (c1.z != c2.z) {
								return false;
							}
						} else {
							return false;
						}
					}

					return true;
				}
			} else {
				return false;
			}
		}
	}

	@Override
	public Value notEquals(Value value) throws IncompatibleTypesException {
		return equals(value).inversa();
	}

	public String toString() {
		if (Double.isNaN(geom.getCoordinate().z)) {
			return textWriter2D.write(geom);
		} else {
			return textWriter3D.write(geom);
		}
	}

	public byte[] getBytes() {
		GetDimensionSequenceFilter sf = new GetDimensionSequenceFilter();
		geom.apply(sf);
		if (sf.dimension == 3) {
			return writer3D.write(geom);
		} else {
			return writer2D.write(geom);
		}
	}

	public static Value readBytes(byte[] buffer) {
		try {
			return new GeometryValue(wkbReader.read(buffer));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Geometry getAsGeometry() throws IncompatibleTypesException {
		return geom;
	}

	private final class GetDimensionSequenceFilter implements
			CoordinateSequenceFilter {
		private boolean isDone = false;
		private int dimension = 0;

		@Override
		public boolean isGeometryChanged() {
			return false;
		}

		@Override
		public boolean isDone() {
			return isDone;
		}

		@Override
		public void filter(CoordinateSequence arg0, int arg1) {
			dimension = arg0.getDimension();
			isDone = true;
		}
	}

	public static Value parseString(String text) throws ParseException {
		Geometry readGeometry = wktReader.read(text);
		if (readGeometry != null) {
			return new GeometryValue(readGeometry);
		} else {
			throw new ParseException("Cannot parse geometry: " + text);
		}
	}
}