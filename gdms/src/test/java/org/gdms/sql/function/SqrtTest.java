/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.sql.function;

import junit.framework.TestCase;

import org.gdms.Geometries;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;

public class SqrtTest extends TestCase {
	private final static DataSourceFactory dsf = new DataSourceFactory();
	private DataSource ds;

	protected void setUp() throws Exception {
		super.setUp();

		final ObjectMemoryDriver omd = new ObjectMemoryDriver(new String[] {
				"id", "geom", "field" }, new Type[] {
				TypeFactory.createType(Type.STRING),
				TypeFactory.createType(Type.GEOMETRY),
				TypeFactory.createType(Type.FLOAT) });
		dsf.getSourceManager().register("obj", new ObjectSourceDefinition(omd));
		ds = dsf.getDataSource("obj");
		ds.open();
		ds.insertFilledRow(new Value[] { ValueFactory.createValue("13"),
				ValueFactory.createValue(Geometries.getPolygon()),
				ValueFactory.createValue(16.0f), });
		ds.insertFilledRow(new Value[] { ValueFactory.createValue("13"),
				ValueFactory.createValue(Geometries.getPolygon()),
				ValueFactory.createValue(Float.NaN), });
		ds.insertFilledRow(new Value[] { ValueFactory.createValue("270"),
				ValueFactory.createValue(Geometries.getPoint()),
				ValueFactory.createValue(-49.0f), });
		ds.commit();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testEvaluate() throws SyntaxException,
			DriverLoadException, NoSuchTableException, ExecutionException,
			DriverException {
		final DataSource resultDsOne = dsf
				.executeSQL("select id, field, sqrt(sqrt(field)) from obj;");
		resultDsOne.open();
		final long rowCount = resultDsOne.getRowCount();
		final int fieldCount = resultDsOne.getFieldCount();

		assertTrue(3 == rowCount);
		assertTrue(3 == fieldCount);

		for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			final Value[] fields = resultDsOne.getRow(rowIndex);
			double tmpOne = fields[1].getAsDouble();
			double tmpTwo = fields[2].getAsDouble();
			if ((0 > tmpOne) || (Double.isNaN(tmpOne))) {
				assertTrue(Double.isNaN(tmpTwo));
			} else {
				assertTrue(tmpOne == tmpTwo * tmpTwo * tmpTwo * tmpTwo);
			}
		}
		resultDsOne.cancel();

		final DataSource resultDsTwo = dsf
				.executeSQL("select id, field, sqrt(geom) from obj;");
		resultDsTwo.open();
		boolean bug = true;
		for (long rowIndex = 0; rowIndex < resultDsTwo.getRowCount(); rowIndex++) {
			try {
				resultDsTwo.getRow(rowIndex);
			} catch (ClassCastException e) {
				bug = false;
			}
		}
		resultDsTwo.cancel();
		assertFalse(bug);
	}
}