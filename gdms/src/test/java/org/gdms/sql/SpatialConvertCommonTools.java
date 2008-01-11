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
package org.gdms.sql;

import junit.framework.TestCase;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.PrimaryKeyConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.function.FunctionManager;

import com.vividsolutions.jts.io.WKTReader;

public class SpatialConvertCommonTools extends TestCase {
	public static DataSourceFactory dsf = new DataSourceFactory();

	static {
		new QueryManager();
		new FunctionManager();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		final WKTReader wktr = new WKTReader();

		// first datasource
		final ObjectMemoryDriver driver1 = new ObjectMemoryDriver(new String[] {
				"pk", "geom" }, new Type[] {
				TypeFactory.createType(Type.INT,
						new Constraint[] { new PrimaryKeyConstraint() }),
				TypeFactory.createType(Type.GEOMETRY,
						new Constraint[] { new GeometryConstraint() }) });

		// insert all filled rows...
		String g1 = "MULTIPOLYGON (((0 0, 1 1, 0 1, 0 0)), ((1 1, 1 2, 2 2, 1 1)))";
		String g2 = "MULTILINESTRING ((0 0, 1 0, 1 1), (0 1, 2 2, 0 2, 2 -2))";
		String g3 = "MULTIPOINT (0 0, 0 1, 3 3, 4 4, 5 5, 15 10)";
		driver1.addValues(new Value[] { ValueFactory.createValue(1),
				ValueFactory.createValue(wktr.read(g1)) });
		driver1.addValues(new Value[] { ValueFactory.createValue(2),
				ValueFactory.createValue(wktr.read(g2)) });
		driver1.addValues(new Value[] { ValueFactory.createValue(3),
				ValueFactory.createValue(wktr.read(g3)) });
		// and register this new driver...
		dsf.getSourceManager().register("ds1", driver1);

		// second datasource
		final ObjectMemoryDriver driver2 = new ObjectMemoryDriver(new String[] {
				"pk", "geom" }, new Type[] {
				TypeFactory.createType(Type.INT,
						new Constraint[] { new PrimaryKeyConstraint() }),
				TypeFactory.createType(Type.GEOMETRY,
						new Constraint[] { new GeometryConstraint() }) });

		// and register this new driver...
		dsf.getSourceManager().register("ds2", driver2);
		final DataSource dataSource2 = dsf.getDataSource("ds2");
		dataSource2.open();
		// insert all filled rows...
		g1 = "MULTIPOINT (0 0, 1 1, 2 2, 1 1)";
		g2 = "MULTIPOINT (0 0, 1 0, 0 1)";
		g3 = "MULTIPOINT (0 0, 0 1, 3 3)";
		dataSource2.insertFilledRow(new Value[] { ValueFactory.createValue(11),
				ValueFactory.createValue(wktr.read(g1)) });
		dataSource2.insertFilledRow(new Value[] { ValueFactory.createValue(22),
				ValueFactory.createValue(wktr.read(g2)) });
		dataSource2.insertFilledRow(new Value[] { ValueFactory.createValue(33),
				ValueFactory.createValue(wktr.read(g3)) });
		dataSource2.commit();

		// third datasource
		final ObjectMemoryDriver driver3 = new ObjectMemoryDriver(new String[] {
				"pk", "geom" }, new Type[] {
				TypeFactory.createType(Type.INT,
						new Constraint[] { new PrimaryKeyConstraint() }),
				TypeFactory.createType(Type.GEOMETRY,
						new Constraint[] { new GeometryConstraint() }) });

		// insert all filled rows...
		g1 = "MULTIPOLYGON (((0 0, 1 1, 0 1, 0 0)))";
		g2 = "MULTILINESTRING ((0 0, 1 1, 0 1, 0 0))";
		g3 = "MULTIPOINT (0 0, 1 1, 0 1, 0 0)";
		driver3.addValues(new Value[] { ValueFactory.createValue(1),
				ValueFactory.createValue(wktr.read(g1)) });
		driver3.addValues(new Value[] { ValueFactory.createValue(2),
				ValueFactory.createValue(wktr.read(g2)) });
		driver3.addValues(new Value[] { ValueFactory.createValue(3),
				ValueFactory.createValue(wktr.read(g3)) });
		// and register this new driver...
		dsf.getSourceManager().register("ds3", driver3);
	}

	@Override
	protected void tearDown() throws Exception {
		if (dsf.getSourceManager().exists("ds1")) {
			dsf.getSourceManager().remove("ds1");
		}
		if (dsf.getSourceManager().exists("ds2")) {
			dsf.getSourceManager().remove("ds2");
		}
		if (dsf.getSourceManager().exists("ds3")) {
			dsf.getSourceManager().remove("ds3");
		}
		super.tearDown();
	}
}