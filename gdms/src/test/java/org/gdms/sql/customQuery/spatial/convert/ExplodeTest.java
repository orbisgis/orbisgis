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
package org.gdms.sql.customQuery.spatial.convert;

import org.gdms.data.DataSource;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.SpatialConvertCommonTools;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;

public class ExplodeTest extends SpatialConvertCommonTools {
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		if (dsf.getSourceManager().exists("ds1p")) {
			dsf.getSourceManager().remove("ds1p");
		}
		if (dsf.getSourceManager().exists("ds2p")) {
			dsf.getSourceManager().remove("ds2p");
		}
		super.tearDown();
	}

	private void evaluate(final DataSource dataSource) throws SyntaxException,
			DriverLoadException, NoSuchTableException, ExecutionException,
			DriverException {
		dataSource.open();
		final long rowCount = dataSource.getRowCount();
		long rowIndex = 0;
		while (rowIndex < rowCount) {
			final Geometry geometryCollection = dataSource
					.getFieldValue(rowIndex, 2).getAsGeometry();
			for (int i = 0; i < geometryCollection.getNumGeometries(); i++) {
				final Value[] fields = dataSource.getRow(rowIndex++);
				final Geometry geometry = fields[1].getAsGeometry();
				assertTrue(geometryCollection.getGeometryN(i).equals(geometry));
				assertFalse(geometry instanceof GeometryCollection);

				System.out.printf("%d, %s, %s\n", rowIndex,
						geometry.toString(), geometryCollection.toString());
			}
		}
		dataSource.cancel();
	}

	public void testEvaluate1() throws SyntaxException, DriverLoadException,
			NoSuchTableException, ExecutionException, DriverException {
		dsf.getSourceManager().register("ds1p",
				"select pk, geom, geom from ds1;");
		evaluate(dsf.executeSQL("select Explode() from ds1p;"));
	}

	public void testEvaluate2() throws SyntaxException, DriverLoadException,
			NoSuchTableException, ExecutionException, DriverException {
		dsf.getSourceManager().register("ds2p",
				"select pk, geom, geom from ds2;");
		evaluate(dsf.executeSQL("select Explode() from ds2p;"));
	}
}