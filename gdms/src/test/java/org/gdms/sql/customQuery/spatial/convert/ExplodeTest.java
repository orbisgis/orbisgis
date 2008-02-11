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
package org.gdms.sql.customQuery.spatial.convert;

import java.io.ByteArrayInputStream;

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
import org.gdms.sql.parser.SQLEngine;
import org.gdms.sql.parser.SimpleNode;
import org.gdms.sql.strategies.LogicTreeBuilder;
import org.gdms.sql.strategies.Operator;
import org.gdms.sql.strategies.Preprocessor;
import org.gdms.sql.strategies.SemanticException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;

public class ExplodeTest extends TestCase {
	private static DataSourceFactory dsf = new DataSourceFactory();

	private void evaluate(final DataSource dataSource) throws DriverException {
		dataSource.open();
		final long rowCount = dataSource.getRowCount();
		long rowIndex = 0;
		while (rowIndex < rowCount) {
			Value collectionValue = dataSource.getFieldValue(rowIndex, 2);
			final Geometry geometryCollection = collectionValue.getAsGeometry();
			for (int i = 0; i < geometryCollection.getNumGeometries(); i++) {
				final Value field = dataSource.getFieldValue(rowIndex++, 1);
				if (collectionValue.isNull()) {
					assertTrue(field.isNull());
				} else {
					final Geometry geometry = field.getAsGeometry();
					assertTrue(geometryCollection.getGeometryN(i).equals(
							geometry));
					assertFalse(geometry instanceof GeometryCollection);
				}
			}
		}
		dataSource.cancel();
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
		// and register this new driver...
		dsf.getSourceManager().register("ds1", driver1);
		dsf.getSourceManager().register("ds1p",
				"select pk, geom as g1, geom as g2 from ds1;");
		evaluate(dsf.getDataSourceFromSQL("select Explode() from ds1p;"));
	}

	public void testWrongParameters() throws Exception {
		try {
			testWrongParameters("select explode('o') from ds1p;");
			assertTrue(false);
		} catch (SemanticException e) {
		}
		try {
			testWrongParameters("select explode() from ds1p, ds2p;");
			assertTrue(false);
		} catch (SemanticException e) {
		}
	}

	private void testWrongParameters(String sql) throws Exception {
		SQLEngine parser = new SQLEngine(new ByteArrayInputStream(sql
				.getBytes()));

		parser.SQLStatement();
		LogicTreeBuilder lp = new LogicTreeBuilder(dsf);
		Operator op = (Operator) lp
				.buildTree((SimpleNode) parser.getRootNode());
		Preprocessor p = new Preprocessor(op);
		p.validate();
	}
}