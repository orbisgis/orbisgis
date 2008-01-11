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
package org.gdms.manual;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.indexes.SpatialIndex;
import org.gdms.driver.DriverException;
import org.gdms.sql.strategies.FirstStrategy;

public class SpatialTests {

	static DataSourceFactory dsf = new DataSourceFactory();

	static DataSource ds1 = null;

	static DataSource ds2 = null;

	static String ds1Name;

	static String ds2Name;

	private static long beginTime;

	public static void main(String[] args) throws Exception {

		beginTime = System.currentTimeMillis();

		File src1 = new File(
				"../../datas2tests/shp/mediumshape2D/bzh5_communes.shp");

		File src2 = new File(
				"../../datas2tests/shp/mediumshape2D/landcover2000.shp");

		ds1 = dsf.getDataSource(src1);
		ds2 = dsf.getDataSource(src2);

		ds1Name = ds1.getName();
		ds2Name = ds2.getName();

		// SpatialDataSourceDecorator sds1 = new
		// SpatialDataSourceDecorator(ds1);

		// SpatialDataSourceDecorator sds2 = new
		// SpatialDataSourceDecorator(ds2);

		// Tests

		// testIntersection(sds1, sds2);

		testContains();

		System.out.printf("=> %d ms\n", System.currentTimeMillis() - beginTime);

	}

	private static void testIntersection(SpatialDataSourceDecorator sds,
			SpatialDataSourceDecorator sds2) throws Exception {

		String sqlQuery = "select Intersection(" + ds1Name + ".the_geom,"
				+ ds2Name + ".the_geom) from " + ds1Name + ", " + ds2Name + ";";

		SpatialDataSourceDecorator spatialds = new SpatialDataSourceDecorator(
				dsf.executeSQL(sqlQuery));

		displayGeometry(spatialds);
	}

	private static void testContains() throws Exception {
		String sqlQuery = "select * from " + ds1Name + ", " + ds2Name
				+ " where Contains(" + ds1Name + ".the_geom," + ds2Name
				+ ".the_geom)" + ";";

		dsf.getIndexManager().buildIndex(ds2Name, "the_geom",
				SpatialIndex.SPATIAL_INDEX);
		System.out.println("exec");
		FirstStrategy.indexes = true;
		DataSource result = dsf.executeSQL(sqlQuery);
		System.out.printf("=> %d ms\n", System.currentTimeMillis() - beginTime);
		SpatialDataSourceDecorator spatialds = new SpatialDataSourceDecorator(
				result);
		System.out.println("fin exec");

		spatialds.open();
		System.out.println(spatialds.getRowCount());
		// displayGeometry(spatialds);
	}

	public static void displayGeometry(SpatialDataSourceDecorator spatialds2)
			throws DriverException {
		spatialds2.open();

		for (int i = 0; i < spatialds2.getRowCount(); i++) {

			if (spatialds2.getGeometry(i).isEmpty()) {

			} else {
				System.out.println(spatialds2.getGeometry(i).toString());
			}
		}

		spatialds2.cancel();
	}
}