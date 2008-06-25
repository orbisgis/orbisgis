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
package org.gdms.sql.customQuery.spatial.convert;

import junit.framework.TestCase;

import org.gdms.Geometries;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.gdms.sql.strategies.SQLProcessor;
import org.gdms.sql.strategies.SemanticException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;

public class ExplodeTest extends TestCase {
	private static DataSourceFactory dsf = new DataSourceFactory();
	private int rowIndex;

	private void print(final DataSource dataSource) throws DriverException {
		dataSource.open();
		final long rowCount = dataSource.getRowCount();
		final long fieldCount = dataSource.getFieldCount();
		for (long row = 0; row < rowCount; row++) {
			for (int field = 0; field < fieldCount; field++) {
				System.err.printf("%s # ", dataSource.getFieldValue(row, field)
						.toString());
			}
			System.err.println();
		}
		dataSource.close();
	}

	private void evaluate(final DataSource dataSource) throws DriverException {
		print(dataSource);

		dataSource.open();
		final long rowCount = dataSource.getRowCount();
		rowIndex = 0;
		while (rowIndex < rowCount) {
			final Value inGCValue = dataSource.getFieldValue(rowIndex, 2);

			if (inGCValue.isNull()) {
				final Value field = dataSource.getFieldValue(rowIndex++, 1);
				assertTrue(field.isNull());
			} else {
				evaluate(dataSource, inGCValue.getAsGeometry());
			}
		}
		dataSource.close();
	}

	private void evaluate(final DataSource dataSource, final Geometry inGC)
			throws IncompatibleTypesException, DriverException {
		if (inGC instanceof GeometryCollection) {
			for (int i = 0; i < inGC.getNumGeometries(); i++) {
				evaluate(dataSource, inGC.getGeometryN(i));
			}
		} else {
			// breaking condition
			final Geometry geometry = dataSource.getFieldValue(rowIndex++, 1)
					.getAsGeometry();
			assertFalse(geometry instanceof GeometryCollection);
			assertTrue(inGC.equals(geometry));
		}
	}

	public void testEvaluate() throws Exception {
		final ObjectMemoryDriver driver1 = new ObjectMemoryDriver(new String[] {
				"pk", "geom" }, new Type[] { TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.GEOMETRY) });

		// insert all filled rows...
		driver1.addValues(new Value[] { ValueFactory.createValue(1),
				ValueFactory.createValue(Geometries.getMultiPoint3D()) });
		driver1.addValues(new Value[] { ValueFactory.createValue(2),
				ValueFactory.createValue(Geometries.getMultiPolygon2D()) });
		driver1.addValues(new Value[] { ValueFactory.createValue(3),
				ValueFactory.createValue(Geometries.getPoint()) });
		driver1.addValues(new Value[] { ValueFactory.createValue(3),
				ValueFactory.createNullValue() });
		driver1.addValues(new Value[] { ValueFactory.createValue(4),
				ValueFactory.createValue(Geometries.getGeometryCollection()) });
		// and register this new driver...
		dsf.getSourceManager().register("ds1", driver1);
		dsf.getSourceManager().register("ds1p",
				"select pk, geom as g1, geom as g2 from ds1;");
		evaluate(dsf.getDataSourceFromSQL("select Explode() from ds1p;"));
		evaluate(dsf.getDataSourceFromSQL("select Explode(g1) from ds1p;"));
	}

	public void testWrongParameters() throws Exception {
		try {
			testWrongParameters("select explode(geom, geom) from ds1p;");
			assertTrue(false);
		} catch (SemanticException e) {
		}
		try {
			testWrongParameters("select explode('o') from ds1p;");
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
		try {
			testWrongParameters("select explode() from ds1p, ds2p;");
			assertTrue(false);
		} catch (SemanticException e) {
		}
	}

	private void testWrongParameters(String sql) throws Exception {
		SQLProcessor pr = new SQLProcessor(dsf);
		pr.prepareInstruction(sql);
	}
}