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
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
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
package org.urbsat.function;

import junit.framework.TestCase;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.function.FunctionManager;

public class CollectiveAverageTest extends TestCase {
	private static DataSourceFactory dsf = new DataSourceFactory();
	static {
		FunctionManager.addFunction(CollectiveAverage.class);
	}

	private static void addDriverValue(final ObjectMemoryDriver driver,
			double... doubleValues) {
		final Value[] values = new Value[doubleValues.length];
		for (int i = 0; i < doubleValues.length; i++) {
			values[i] = ValueFactory.createValue(doubleValues[i]);
		}
		driver.addValues(values);
	}

	protected void setUp() throws Exception {
		super.setUp();

		final ObjectMemoryDriver driver = new ObjectMemoryDriver(new String[] {
				"field1", "field2" }, new Type[] {
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.DOUBLE) });
		addDriverValue(driver, 3, 30.2);
		addDriverValue(driver, 2, 31.2);
		addDriverValue(driver, 1, 32.2);
		addDriverValue(driver, 4, 33.2);
		addDriverValue(driver, 0, 34.2);
		dsf.getSourceManager().register("inDs", driver);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		if (dsf.getSourceManager().exists("outDs")) {
			dsf.getSourceManager().remove("outDs");
		}
		if (dsf.getSourceManager().exists("inDs")) {
			dsf.getSourceManager().remove("inDs");
		}
	}

	public final void testEvaluate() throws Exception {
		dsf.getSourceManager().register("outDs",
				"select CollectiveAvg(field1, field2) from \"inDs\";");

		final DataSource outDs = dsf.getDataSource("outDs");
		outDs.open();
		final long rowCount = outDs.getRowCount();
		final int fieldCount = outDs.getFieldCount();
		assertTrue(1 == rowCount);
		assertTrue(1 == fieldCount);
		assertTrue(2 == ((ValueCollection) outDs.getFieldValue(0, 0)).get(0)
				.getAsDouble());
		assertTrue(32.2 == ((ValueCollection) outDs.getFieldValue(0, 0)).get(1)
				.getAsDouble());
		outDs.cancel();
	}
}