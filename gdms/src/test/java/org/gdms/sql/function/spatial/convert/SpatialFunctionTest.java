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
package org.gdms.sql.function.spatial.convert;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.FunctionTest;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.io.WKTReader;

public class SpatialFunctionTest extends FunctionTest {
	private WKTReader wktr;
	private Geometry g1;
	private Geometry g2;
	private Geometry g3;

	@Override
	protected void setUp() throws Exception {
		wktr = new WKTReader();
		g1 = wktr.read("MULTIPOLYGON (((0 0, 1 1, 0 1, 0 0)))");
		g2 = wktr.read("MULTILINESTRING ((0 0, 1 1, 0 1, 0 0))");
		g3 = wktr.read("MULTIPOINT (0 0, 1 1, 0 1, 0 0)");
	}

	public final void testToMultiline() throws Exception {
		ToMultiLine function = new ToMultiLine();
		Value result = evaluate(function, ValueFactory.createValue(g3));
		Geometry geom = result.getAsGeometry();
		assertTrue(geom.isEmpty());
		result = evaluate(function, ValueFactory.createValue(g2));
		Value result2 = evaluate(function, ValueFactory.createValue(g1));
		geom = result.getAsGeometry();
		Geometry geom2 = result2.getAsGeometry();
		assertTrue(geom instanceof MultiLineString);
		assertTrue(geom2 instanceof MultiLineString);
		assertTrue(geom2.equals(geom));
	}

	public final void testToMultipoint() throws Exception {
		ToMultiPoint function = new ToMultiPoint();
		Value result1 = evaluate(function, ValueFactory.createValue(g1));
		Value result2 = evaluate(function, ValueFactory.createValue(g2));
		Value result3 = evaluate(function, ValueFactory.createValue(g3));
		Geometry geom1 = result1.getAsGeometry();
		Geometry geom2 = result2.getAsGeometry();
		Geometry geom3 = result3.getAsGeometry();
		assertTrue(g3.equals(geom1));
		assertTrue(g3.equals(geom2));
		assertTrue(g3.equals(geom3));
	}
}