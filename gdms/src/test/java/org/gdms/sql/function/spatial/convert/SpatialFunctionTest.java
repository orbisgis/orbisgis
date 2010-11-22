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
import org.gdms.sql.function.spatial.geometry.convert.ST_Centroid;
import org.gdms.sql.function.spatial.geometry.convert.ST_Force_2D;
import org.gdms.sql.function.spatial.geometry.convert.ST_Force_3D;
import org.gdms.sql.function.spatial.geometry.convert.ST_ToMultiLine;
import org.gdms.sql.function.spatial.geometry.convert.ST_ToMultiPoint;
import org.gdms.sql.function.spatial.geometry.convert.ST_ToMultiSegments;
import org.gdms.sql.function.spatial.geometry.create.ST_Boundary;
import org.gdms.sql.strategies.IncompatibleTypesException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public class SpatialFunctionTest extends FunctionTest {

	public void testConstraint2D() throws Exception {
		// Test null input
		ST_Force_2D function = new ST_Force_2D();
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()));
		assertTrue(res.isNull());

		// Test normal input value and type
		Value vg1 = ValueFactory.createValue(JTSPoint3D);
		res = evaluate(function, vg1);
		assertTrue(res.getType() == Type.GEOMETRY);
		assertTrue(res.equals(vg1).getAsBoolean());

		// Test too many parameters
		try {
			res = evaluate(function, vg1, ValueFactory
					.createValue(JTSMultiLineString2D));
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
		Type type = TypeFactory.createType(Type.GEOMETRY,
				new Constraint[] { new GeometryConstraint(
						GeometryConstraint.LINESTRING) });
		type = evaluate(function, type);
		assertTrue(type.getTypeCode() == Type.GEOMETRY);
		assertTrue(type.getIntConstraint(Constraint.GEOMETRY_DIMENSION) == 2);

		// Test coordinates
		try {
			res = evaluate(function, new ColumnValue(Type.GEOMETRY,
					ValueFactory.createValue(JTSPoint3D)));
			Coordinate coordResult = res.getAsGeometry().getCoordinate();

			Coordinate coordSource = JTSPoint3D.getCoordinate();

			System.out.println(JTSPoint3D.getCoordinate().z);
			assertTrue(coordResult.equals2D(coordSource));

		} catch (IncompatibleTypesException e) {
		}
	}

	public void testConstraint3D() throws Exception {
		// Test null input
		ST_Force_3D function = new ST_Force_3D();
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()));
		assertTrue(res.isNull());

		// Test normal input value and type
		Value vg1 = ValueFactory.createValue(JTSMultiPolygon2D);
		res = evaluate(function, vg1);
		assertTrue(res.getType() == Type.GEOMETRY);
		assertTrue(res.equals(vg1).getAsBoolean());

		// Test too many parameters
		try {
			res = evaluate(function, vg1, ValueFactory
					.createValue(JTSMultiLineString2D));
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
		Type type = TypeFactory.createType(Type.GEOMETRY,
				new Constraint[] { new GeometryConstraint(
						GeometryConstraint.LINESTRING) });
		type = evaluate(function, type);
		assertTrue(type.getTypeCode() == Type.GEOMETRY);
		assertTrue(type.getIntConstraint(Constraint.GEOMETRY_DIMENSION) == 3);
	}

	public void testST_ToMultiSegments() throws Exception {

		ST_ToMultiSegments function = new ST_ToMultiSegments();
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()));
		assertTrue(res.isNull());

		Value vg1 = ValueFactory.createValue(JTSMultiLineString2D);
		res = evaluate(function, vg1);

		assertTrue(res.getAsGeometry().getNumGeometries() == 3);
	}

	public void testBoundary() throws Exception {
		// Test null input
		ST_Boundary function = new ST_Boundary();
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()));
		assertTrue(res.isNull());

		// Test normal input value and type
		res = evaluate(function, ValueFactory.createValue(JTSMultiPolygon2D));
		assertTrue(res.getType() == Type.GEOMETRY);
		assertTrue(res.getAsGeometry().equalsExact(
				JTSMultiPolygon2D.getBoundary()));

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory
					.createValue(JTSMultiPolygon2D), ValueFactory
					.createValue(JTSMultiLineString2D));
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
		Geometry g = testSpatialFunction(new ST_ToMultiLine(),
				JTSMultiPolygon2D, 1).getAsGeometry();
		assertTrue(JTSMultiPolygon2D.getEnvelopeInternal().equals(
				g.getEnvelopeInternal()));
	}

	public final void testToMultipoint() throws Exception {
		Geometry g = testSpatialFunction(new ST_ToMultiPoint(),
				JTSMultiPolygon2D, 1).getAsGeometry();
		assertTrue(JTSMultiPolygon2D.getCoordinates().length == g
				.getNumGeometries());

	}

	public final void testCentroid() throws Exception {
		Geometry g = testSpatialFunction(new ST_Centroid(), JTSMultiPolygon2D,
				1).getAsGeometry();
		assertTrue(JTSMultiPolygon2D.getCentroid().equals(g));
	}

}