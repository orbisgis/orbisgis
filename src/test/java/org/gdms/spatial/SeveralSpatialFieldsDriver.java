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
package org.gdms.spatial;

import java.util.ArrayList;

import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.ObjectMemoryDriver;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

public class SeveralSpatialFieldsDriver extends ObjectMemoryDriver {

	private Envelope envelope;

	public SeveralSpatialFieldsDriver() throws InvalidTypeException {
		super(new String[] { "geom1", "geom2", "geom3", "alpha" }, new Type[] {
				TypeFactory.createType(Type.GEOMETRY, "GEOMETRY"),
				TypeFactory.createType(Type.GEOMETRY, "GEOMETRY"),
				TypeFactory.createType(Type.GEOMETRY, "GEOMETRY"),
				TypeFactory.createType(Type.STRING, "String") });
		GeometryFactory gf = new GeometryFactory();

		contents = new ArrayList<ArrayList<Value>>();
		ArrayList<Value> row = new ArrayList<Value>();
		row.add(ValueFactory.createValue(gf.createPoint(new Coordinate(0, 0))));
		row.add(ValueFactory.createValue(gf.createLineString(new Coordinate[] {
				new Coordinate(10, 0), new Coordinate(10, 10),
				new Coordinate(110, 10), new Coordinate(10, 0), })));
		row.add(ValueFactory.createValue(gf.createPolygon(gf
				.createLinearRing(new Coordinate[] { new Coordinate(0, 30),
						new Coordinate(0, 310), new Coordinate(10, 310),
						new Coordinate(10, 30), new Coordinate(0, 30) }),
				new LinearRing[0])));
		row.add(ValueFactory.createValue(1));
		contents.add(row);

		row = new ArrayList<Value>();
		row
				.add(ValueFactory.createValue(gf.createPoint(new Coordinate(10,
						0))));
		row.add(ValueFactory.createValue(gf.createLineString(new Coordinate[] {
				new Coordinate(0, 0), new Coordinate(0, 10),
				new Coordinate(10, 10), new Coordinate(0, 0), })));
		row.add(ValueFactory.createValue(gf.createPolygon(gf
				.createLinearRing(new Coordinate[] { new Coordinate(0, 0),
						new Coordinate(0, 10), new Coordinate(10, 10),
						new Coordinate(10, 0), new Coordinate(0, 0) }),
				new LinearRing[0])));
		row.add(ValueFactory.createValue(2));
		contents.add(row);

		row = new ArrayList<Value>();
		row
				.add(ValueFactory.createValue(gf.createPoint(new Coordinate(0,
						10))));
		row.add(ValueFactory.createValue(gf.createLineString(new Coordinate[] {
				new Coordinate(20, 0), new Coordinate(20, 10),
				new Coordinate(210, 10), new Coordinate(20, 0), })));
		row.add(ValueFactory.createValue(gf.createPolygon(gf
				.createLinearRing(new Coordinate[] { new Coordinate(0, 20),
						new Coordinate(0, 210), new Coordinate(10, 210),
						new Coordinate(10, 20), new Coordinate(0, 20) }),
				new LinearRing[0])));
		row.add(ValueFactory.createValue(3));
		contents.add(row);

		calculateEnvelopes();
	}

	private void calculateEnvelopes() {
		for (ArrayList<Value> row : contents) {
			for (Value value : row) {
				if (value.getType() == Type.GEOMETRY) {
					if (envelope != null) {
						envelope.expandToInclude(value.getAsGeometry()
								.getEnvelopeInternal());
					} else {
						envelope = value.getAsGeometry().getEnvelopeInternal();
					}

				}
			}
		}
	}

	public Number[] getScope(int dimension) throws DriverException {
		calculateEnvelopes();
		if (dimension == X) {
			return new Number[] { envelope.getMinX(), envelope.getMaxX() };
		} else if (dimension == Y) {
			return new Number[] { envelope.getMinY(), envelope.getMaxY() };
		} else {
			return null;
		}
	}

	public String getDriverId() {
		return "severalSpatialFieldsDriver";
	}
}