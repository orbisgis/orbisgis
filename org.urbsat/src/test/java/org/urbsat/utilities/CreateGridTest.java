/*
 * UrbSAT is a set of spatial functionalities to build morphological
 * and aerodynamic urban indicators. It has been developed on
 * top of GDMS and OrbisGIS. UrbSAT is distributed under GPL 3
 * license. It is produced by the geomatic team of the IRSTV Institute
 * <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of UrbSAT.
 *
 * UrbSAT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UrbSAT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UrbSAT. If not, see <http://www.gnu.org/licenses/>.
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
package org.urbsat.utilities;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SQLSourceDefinition;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.urbsat.UrbsatTestsCommonTools;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

public class CreateGridTest extends UrbsatTestsCommonTools {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		if (dsf.getSourceManager().exists("ds1ppp")) {
			dsf.getSourceManager().remove("ds1ppp");
		}
		if (dsf.getSourceManager().exists("ds1pp")) {
			dsf.getSourceManager().remove("ds1pp");
		}
		if (dsf.getSourceManager().exists("ds1p")) {
			dsf.getSourceManager().remove("ds1p");
		}
		super.tearDown();
	}

	private void check(final DataSource dataSource, final boolean checkCentroid)
			throws AlreadyClosedException, DriverException {
		dataSource.open();
		final long rowCount = dataSource.getRowCount();
		final int fieldCount = dataSource.getFieldCount();
		assertTrue(4 == rowCount);
		assertTrue(2 == fieldCount);
		for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			final Value[] fields = dataSource.getRow(rowIndex);
			final Geometry geom = fields[0].getAsGeometry();
			final int id = fields[1].getAsInt();
			assertTrue(geom instanceof Polygon);
			assertTrue(Math.abs(1 - geom.getArea()) < 0.000001);
			assertTrue(4 == geom.getLength());
			assertTrue(5 == geom.getNumPoints());
			if (checkCentroid) {
				assertTrue(0.5 + (id - 1) / 2 == geom.getCentroid()
						.getCoordinate().x);
				assertTrue(0.5 + (id - 1) % 2 == geom.getCentroid()
						.getCoordinate().y);
			}

			for (int fieldIndex = 0; fieldIndex < fieldCount; fieldIndex++) {
				System.out.print(fields[fieldIndex].toString() + ", ");
			}
			System.out.println();
		}
		dataSource.cancel();
	}

	public final void testEvaluate() throws DriverLoadException,
			NoSuchTableException, DataSourceCreationException, DriverException {
		dsf.getSourceManager().register("ds1p",
				new SQLSourceDefinition("select creategrid(1.0, 1) from ds1;"));
		check(dsf.getDataSource("ds1p"), true);

		dsf.getSourceManager().register("ds1pp",
				new SQLSourceDefinition("select creategrid(1,1,0) from ds1;"));
		check(dsf.getDataSource("ds1pp"), true);

		dsf.getSourceManager().register("ds1ppp",
				new SQLSourceDefinition("select creategrid(1,1,90) from ds1;"));
		check(dsf.getDataSource("ds1ppp"), false);
	}
}