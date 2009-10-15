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
package org.geoalgorithm;

import junit.framework.TestCase;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.PrimaryKeyConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.memory.ObjectMemoryDriver;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

public class GeoalgorithmDataTests extends TestCase {
	public static DataSourceFactory dsf = new DataSourceFactory();

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		final WKTReader wktr = new WKTReader();

		// first datasource
		final ObjectMemoryDriver driver1 = new ObjectMemoryDriver(new String[] {
				"pk", "geom" }, new Type[] {
				TypeFactory.createType(Type.INT,
						new Constraint[] { new PrimaryKeyConstraint() }),
				TypeFactory.createType(Type.GEOMETRY) });

		// insert all filled rows...
		String g1 = "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))";
		String g2 = "MULTILINESTRING ((1 0, 2 0, 2 1, 1 1, 1 0))";
		String g3 = "LINESTRING (1 1, 2 1, 2 2, 1 2, 1 1)";
		String g4 = "MULTIPOLYGON (((0 1, 1 1, 1 2, 0 2, 0 1)))";
		driver1.addValues(new Value[] { ValueFactory.createValue(1),
				ValueFactory.createValue(wktr.read(g1)) });
		driver1.addValues(new Value[] { ValueFactory.createValue(2),
				ValueFactory.createValue(wktr.read(g2)) });
		driver1.addValues(new Value[] { ValueFactory.createValue(3),
				ValueFactory.createValue(wktr.read(g3)) });
		driver1.addValues(new Value[] { ValueFactory.createValue(4),
				ValueFactory.createValue(wktr.read(g4)) });
		// and register this new driver...
		dsf.getSourceManager().register("ds1", driver1);

		// second datasource
		final ObjectMemoryDriver driver2 = new ObjectMemoryDriver(new String[] {
				"pk", "geom" }, new Type[] {
				TypeFactory.createType(Type.INT,
						new Constraint[] { new PrimaryKeyConstraint() }),
				TypeFactory.createType(Type.GEOMETRY) });
		// insert all filled rows...
		Geometry geometry = wktr.read(g1);
		driver1.addValues(new Value[] { ValueFactory.createValue(1),
				ValueFactory.createValue(geometry) });
		// and register this new driver...
		dsf.getSourceManager().register("ds2", driver2);
	}

	@Override
	protected void tearDown() throws Exception {
		if (dsf.getSourceManager().exists("ds1")) {
			dsf.getSourceManager().remove("ds1");
		}
		if (dsf.getSourceManager().exists("ds1")) {
			dsf.getSourceManager().remove("ds2");
		}
		super.tearDown();
	}
}