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
package org.geoalgorithm.direction;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.customQuery.QueryManager;
import org.geoalgorithm.GeoalgorithmDataTests;
import org.geoalgorithm.urbsat.direction.MainDirections;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

public class MainDirectionsTest extends GeoalgorithmDataTests {
	static {
		QueryManager.registerQuery(MainDirections.class);
	}

	private static final GeometryFactory geometryFactory = new GeometryFactory();
	private static final double EPSILON = 1E-6;

	private Coordinate centroid; // TODO : should be removed

	protected void setUp() throws Exception {
		// super.setUp();

		final ObjectMemoryDriver driver = new ObjectMemoryDriver(
				new String[] { "geom" }, new Type[] { TypeFactory
						.createType(Type.GEOMETRY) });

		final double offset = 10 * Math.random();
		for (double y : new double[] { 1 + offset, 2 + offset, 3 + offset,
				4 + offset }) {
			final Geometry g1 = geometryFactory
					.createLineString(new Coordinate[] { new Coordinate(0, y),
							new Coordinate(1, y + 1) });
			final Geometry g2 = geometryFactory
					.createLineString(new Coordinate[] { new Coordinate(0, y),
							new Coordinate(-1, y + 1) });
			driver.addValues(new Value[] { ValueFactory.createValue(g1) });
			driver.addValues(new Value[] { ValueFactory.createNullValue() });
			driver.addValues(new Value[] { ValueFactory.createValue(g2) });
		}
		centroid = new Coordinate(0, offset + 3);

		// and register this new driver...
		dsf.getSourceManager().register("ds", driver);
	}

	protected void tearDown() throws Exception {
		if (dsf.getSourceManager().exists("dsp")) {
			dsf.getSourceManager().remove("dsp");
		}
		if (dsf.getSourceManager().exists("ds")) {
			dsf.getSourceManager().remove("ds");
		}
		// super.tearDown();
	}

	private void check(final DataSource dataSource, final Coordinate centroid)
			throws AlreadyClosedException, DriverException {
		dataSource.open();
		final long rowCount = dataSource.getRowCount();
		final int fieldCount = dataSource.getFieldCount();
		assertTrue(2 == rowCount);
		assertTrue(3 == fieldCount);
		for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			final Value[] fields = dataSource.getRow(rowIndex);
			final Geometry geom = fields[0].getAsGeometry();
			final double theta = fields[1].getAsDouble();
			final double percent = fields[2].getAsDouble();

			assertTrue(geom instanceof LineString);
			assertTrue(2 == geom.getCoordinates().length);
			assertTrue(floatingPointNumbersEquality(4 * Math.sqrt(2), geom
					.getLength()));
			assertTrue(0.5 == percent);
			assertTrue(floatingPointNumbersEquality(centroid.x, geom
					.getCoordinates()[0].x));
			assertTrue(floatingPointNumbersEquality(centroid.y, geom
					.getCoordinates()[0].y));

			if (0 == rowIndex) {
				assertTrue(floatingPointNumbersEquality(centroid.x + 4, geom
						.getCoordinates()[1].x));
				assertTrue(floatingPointNumbersEquality(centroid.y + 4, geom
						.getCoordinates()[1].y));
				assertTrue(Math.PI / 4 == theta);
			} else if (1 == rowIndex) {
				assertTrue(floatingPointNumbersEquality(centroid.x - 4, geom
						.getCoordinates()[1].x));
				assertTrue(floatingPointNumbersEquality(centroid.y + 4, geom
						.getCoordinates()[1].y));
				assertTrue(3 * Math.PI / 4 == theta);
			}
			for (int fieldIndex = 0; fieldIndex < fieldCount; fieldIndex++) {
				System.out.print(fields[fieldIndex].toString() + ", ");
			}
			System.out.println();
		}
		dataSource.close();
	}

	public boolean floatingPointNumbersEquality(final double a, final double b) {
		if (Double.isNaN(a)) {
			return Double.isNaN(b);
		} else {
			return Math.abs(a - b) < EPSILON;
		}
	}

	public final void testEvaluate() throws Exception {
		dsf.getSourceManager().register("dsp",
				"select MainDirections(14) from ds;");
		// TODO : why a ClassCastException ?
		// final Coordinate centroid = ((SpatialDataSourceDecorator) dsf
		// .getDataSource("ds")).getFullExtent().centre();
		check(dsf.getDataSource("dsp"), centroid);
	}
}