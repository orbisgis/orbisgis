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
package org.gdms.sql.customQuery;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.metadata.MetadataUtilities;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.gdms.sql.strategies.SemanticException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

/*
 * call
 * register('../../datas2tests/shp/smallshape2D/portionOfLandcover2000.shp','src');
 * call register('/tmp/dst.cir', 'dst'); create table dst as call EXTRUDE from
 * src values ('gid', 'the_geom', 'runoff_win');
 */

public class Extrude implements CustomQuery {
	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values) throws ExecutionException {
		try {
			final String gidFieldName = values[0].getAsString();
			final String geomFieldName = values[1].getAsString();
			final String highFieldName = values[2].getAsString();
			final SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
					tables[0]);
			sds.open();

			final int gidFieldIndex = sds.getFieldIndexByName(gidFieldName);
			FunctionValidator.failIfFieldDoesNotExist(this, gidFieldName,
					gidFieldIndex, sds.getMetadata());

			// final int geomFieldIndex =
			// sds.getFieldIndexByName(geomFieldName);
			final int highFieldIndex = sds.getFieldIndexByName(highFieldName);
			FunctionValidator.failIfFieldIsNotOfType(this, highFieldName,
					highFieldIndex, Type.DOUBLE, sds.getMetadata());

			sds.setDefaultGeometry(geomFieldName);

			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					getMetadata());
			final GeometryFactory geometryFactory = new GeometryFactory();

			final int rowCount = (int) sds.getRowCount();
			for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				// TODO
				// "sds.getPK(rowIndex)" should replace
				// "sds.getFieldValue(rowIndex, gidFieldIndex)"
				final Value gid = ValueFactory.createValue(sds.getFieldValue(
						rowIndex, gidFieldIndex).toString());
				double high;
				try {
					high = sds.getFieldValue(rowIndex, highFieldIndex)
							.getAsDouble();
				} catch (IncompatibleTypesException e) {
					throw new ExecutionException(
							"The third argument must be a numeric field", e);
				}
				final Geometry g = sds.getGeometry(rowIndex);

				if (g instanceof Polygon) {
					extrudePolygon(geometryFactory, gid, (Polygon) g, high,
							driver);
				} else if (g instanceof MultiPolygon) {
					final MultiPolygon p = (MultiPolygon) g;
					for (int i = 0; i < p.getNumGeometries(); i++) {
						extrudePolygon(geometryFactory, gid, (Polygon) p
								.getGeometryN(i), high, driver);
					}
				} else {
					throw new ExecutionException(
							"Extrude only (Multi-)Polygon geometries");
				}
			}
			sds.cancel();

			return driver;
		} catch (DriverException e) {
			throw new ExecutionException(e);
		} catch (DriverLoadException e) {
			throw new ExecutionException(e);
		} catch (SemanticException e) {
			throw new ExecutionException(e);
		}
	}

	private void extrudePolygon(final GeometryFactory geometryFactory,
			final Value gid, final Polygon polygon, final double high,
			final ObjectMemoryDriver driver) throws DriverException {

		Value wallType = ValueFactory.createValue("wall");

		/* exterior ring */
		final LineString shell = polygon.getExteriorRing();
		Value shellHoleId = ValueFactory.createValue((short) -1);
		for (int i = 1; i < shell.getNumPoints(); i++) {
			final Polygon wall = extrudeEdge(geometryFactory, shell
					.getCoordinateN(i - 1), shell.getCoordinateN(i), high);
			driver.addValues(new Value[] { gid, shellHoleId, wallType,
					ValueFactory.createValue((short) (i - 1)),
					ValueFactory.createValue(wall) });
		}

		/* holes */
		final int nbOfHoles = polygon.getNumInteriorRing();
		for (int i = 0; i < nbOfHoles; i++) {
			final LineString hole = polygon.getInteriorRingN(i);
			shellHoleId = ValueFactory.createValue((short) i);
			for (int j = 1; j < hole.getNumPoints(); j++) {
				final Polygon wall = extrudeEdge(geometryFactory, hole
						.getCoordinateN(j - 1), hole.getCoordinateN(j), high);

				driver.addValues(new Value[] { gid, shellHoleId, wallType,
						ValueFactory.createValue((short) (j - 1)),
						ValueFactory.createValue(wall) });
			}
		}

		/* floor */
		shellHoleId = ValueFactory.createValue((short) -1);
		wallType = ValueFactory.createValue("floor");
		driver.addValues(new Value[] { gid, shellHoleId, wallType,
				ValueFactory.createValue((short) 0),
				ValueFactory.createValue(polygon) });

		/* ceiling */
		wallType = ValueFactory.createValue("ceiling");

		final LinearRing upperShell = translate(geometryFactory, polygon
				.getExteriorRing(), high);
		final LinearRing[] holes = new LinearRing[nbOfHoles];
		for (int i = 0; i < nbOfHoles; i++) {
			holes[i] = translate(geometryFactory, polygon.getInteriorRingN(i),
					high);
		}
		Polygon pp = geometryFactory.createPolygon(upperShell, holes);
		driver.addValues(new Value[] { gid, shellHoleId, wallType,
				ValueFactory.createValue((short) 0),
				ValueFactory.createValue(pp) });
	}

	private Polygon extrudeEdge(final GeometryFactory geometryFactory,
			final Coordinate beginPoint, Coordinate endPoint, final double high) {
		if (Double.isNaN(beginPoint.z)) {
			beginPoint.z = 0d;
		}
		if (Double.isNaN(endPoint.z)) {
			endPoint.z = 0d;
		}

		return geometryFactory.createPolygon(geometryFactory
				.createLinearRing(new Coordinate[] {
						beginPoint,
						new Coordinate(beginPoint.x, beginPoint.y, beginPoint.z
								+ high),
						new Coordinate(endPoint.x, endPoint.y, endPoint.z
								+ high), endPoint, beginPoint }), null);
	}

	private LinearRing translate(final GeometryFactory geometryFactory,
			final LineString ring, final double high) {
		final Coordinate[] src = ring.getCoordinates();
		final Coordinate[] dst = new Coordinate[src.length];
		for (int i = 0; i < src.length; i++) {
			if (Double.isNaN(src[i].z)) {
				src[i].z = 0d;
			}
			dst[i] = new Coordinate(src[i].x, src[i].y, src[i].z + high);
		}
		return geometryFactory.createLinearRing(dst);
	}

	public String getName() {
		return "Extrude";
	}

	public String getSqlOrder() {
		return "select Extrude(id, the_geom, highFieldName) from myTable;";
	}

	public String getDescription() {
		return "Extrude a 2D landcover using a high field value";
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return getMetadata();
	}

	private Metadata getMetadata() throws DriverException {
		try {
			return new DefaultMetadata(new Type[] {
					TypeFactory.createType(Type.STRING),
					TypeFactory.createType(Type.SHORT),
					TypeFactory.createType(Type.STRING),
					TypeFactory.createType(Type.SHORT),
					TypeFactory.createType(Type.GEOMETRY,
							new Constraint[] { new GeometryConstraint(
									GeometryConstraint.POLYGON_3D) }) },
					new String[] { "gid", "shellHoleId", "type", "index",
							"the_geom" });
		} catch (InvalidTypeException e) {
			throw new DriverException(
					"InvalidTypeException in metadata instantiation", e);
		}
	}

	public void validateTypes(Type[] types) throws IncompatibleTypesException {
		FunctionValidator.failIfBadNumberOfArguments(this, types, 3);
		FunctionValidator.failIfNotOfType(this, types[0], Type.STRING);
		FunctionValidator.failIfNotOfType(this, types[1], Type.STRING);
		FunctionValidator.failIfNotOfType(this, types[2], Type.STRING);
	}

	public void validateTables(Metadata[] tables) throws SemanticException {
		try {
			if ((1 != tables.length)
					&& (!MetadataUtilities.isSpatial(tables[0]))) {
				throw new SemanticException(
						"Extrude requires a single spatial table");
			}
		} catch (DriverException e) {
			throw new SemanticException(
					"Extrude requires a single spatial table", e);
		}
	}
}