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
package org.geoalgorithm.kmeans;

import junit.framework.TestCase;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.PrimaryKeyConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.customQuery.QueryManager;
import org.geoalgorithm.urbsat.kmeans.KMeans;

public class KMeansTest extends TestCase {
	private static DataSourceFactory dsf = new DataSourceFactory();
	private double[] meansX = new double[] { 125, -34, 13 };
	private double[] meansY = new double[] { 57, 18, -123 };
	private long rowCount = 100;

	static {
		QueryManager.registerQuery(KMeans.class);
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
				"id", "indicator1", "indicator2" }, new Type[] {
				TypeFactory.createType(Type.INT, new PrimaryKeyConstraint()),
				TypeFactory.createType(Type.DOUBLE),
				TypeFactory.createType(Type.DOUBLE) });
		for (int i = 1; i <= rowCount; i++) {
			addDriverValue(driver, i,
					meansX[i % meansX.length] + Math.random(), meansY[i
							% meansY.length]
							+ Math.random());
		}
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
				"select KMeans(id,13) from \"inDs\";");

		final DataSource outDs = dsf.getDataSource("outDs");
		outDs.open();
		assertTrue(rowCount == outDs.getRowCount());
		assertTrue(2 == outDs.getFieldCount());
		for (int i = 0; i < rowCount; i++) {
			final int pk = outDs.getFieldValue(i, 0).getAsInt();
			// why does the following instruction code throw a
			// ClassCastException ?
			// final int pk = outDs.getFieldValue(i, 0)).getAsInt();
			final int clusterId = outDs.getFieldValue(i, 1).getAsInt();
			assertTrue((pk - 1) % 3 == clusterId);
		}
		outDs.close();
	}
}