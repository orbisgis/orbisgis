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

import org.gdms.data.types.Constraint;
import org.gdms.data.types.DimensionConstraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.ColumnValue;
import org.gdms.sql.FunctionTest;
import org.gdms.sql.strategies.IncompatibleTypesException;

import com.vividsolutions.jts.geom.Geometry;

public class SpatialFunctionTest extends FunctionTest {

	public void testConstraint3D() throws Exception {
		// Test null input
		Constraint3D function = new Constraint3D();
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()));
		assertTrue(res.isNull());

		// Test normal input value and type
		Value vg1 = ValueFactory.createValue(g1);
		res = evaluate(function, vg1);
		assertTrue(res.getType() == Type.GEOMETRY);
		assertTrue(res.equals(vg1).getAsBoolean());

		// Test too many parameters
		try {
			res = evaluate(function, vg1, ValueFactory
					.createValue(g2));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {

		}

		// Test wrong parameter type
		try {
			res = evaluate(function, ValueFactory.createValue(true));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		// Test return type
		Type type = TypeFactory.createType(Type.GEOMETRY, new Constraint[] {
				new GeometryConstraint(GeometryConstraint.LINESTRING) });
		type = evaluate(function, type);
		assertTrue(type.getTypeCode() == Type.GEOMETRY);
		assertTrue(type.getIntConstraint(Constraint.GEOMETRY_DIMENSION) == 3);
	}

	public void testBoundary() throws Exception {
		// Test null input
		Boundary function = new Boundary();
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()));
		assertTrue(res.isNull());

		// Test normal input value and type
		res = evaluate(function, ValueFactory.createValue(g1));
		assertTrue(res.getType() == Type.GEOMETRY);
		assertTrue(res.getAsGeometry().equalsExact(g1.getBoundary()));

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(g1), ValueFactory
					.createValue(g2));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {

		}

		// Test wrong parameter type
		try {
			res = evaluate(function, ValueFactory.createValue(true));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		// Test return type
		Type type = TypeFactory.createType(Type.GEOMETRY, new Constraint[] {
				new GeometryConstraint(GeometryConstraint.LINESTRING),
				new DimensionConstraint(3) });
		type = evaluate(function, type);
		assertTrue(type.getTypeCode() == Type.GEOMETRY);
	}

	public final void testToMultiline() throws Exception {
		Geometry g = testSpatialFunction(new ToMultiLine(), g1, 1)
				.getAsGeometry();
		assertTrue(g1.getEnvelopeInternal().equals(g.getEnvelopeInternal()));
	}

	public final void testToMultipoint() throws Exception {
		Geometry g = testSpatialFunction(new ToMultiPoint(), g1, 1)
				.getAsGeometry();
		assertTrue(g1.getEnvelopeInternal().equals(g.getEnvelopeInternal()));
	}

	public final void testCentroid() throws Exception {
		Geometry g = testSpatialFunction(new Centroid(), g1, 1).getAsGeometry();
		assertTrue(g1.getCentroid().equals(g));
	}

	

}