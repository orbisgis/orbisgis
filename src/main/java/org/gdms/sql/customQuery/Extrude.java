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
package org.gdms.sql.customQuery;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.DimensionConstraint;
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
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public class Extrude implements CustomQuery {
	private final static GeometryFactory geometryFactory = new GeometryFactory();

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		try {
			final String idFieldName = values[0].getAsString();
			final String heightFieldName = values[1].getAsString();

			final SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
					tables[0]);

			if (3 == values.length) {
				// if no spatial's field's name is provided, the default (first)
				// one is arbitrarily chosen.
				final String geomFieldName = values[2].toString();
				sds.setDefaultGeometry(geomFieldName);
			}

			final int idFieldIndex = sds.getFieldIndexByName(idFieldName);
			final int heightFieldIndex = sds
					.getFieldIndexByName(heightFieldName);

			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					getMetadata(null));
			final int rowCount = (int) sds.getRowCount();

			for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				// TODO
				// "sds.getPK(rowIndex)" should replace
				// "sds.getFieldValue(rowIndex, gidFieldIndex)"

				if (rowIndex / 100 == rowIndex / 100.0) {
					if (pm.isCancelled()) {
						break;
					} else {
						pm.progressTo((int) (100 * rowIndex / rowCount));
					}
				}

				final Value gid = ValueFactory.createValue(sds.getFieldValue(
						rowIndex, idFieldIndex).toString());
				final double height = sds.getFieldValue(rowIndex,
						heightFieldIndex).getAsDouble();
				final Geometry g = sds.getGeometry(rowIndex);

				if (g instanceof Polygon) {
					extrudePolygon(gid, (Polygon) g, height, driver);
				} else if (g instanceof MultiPolygon) {
					final MultiPolygon p = (MultiPolygon) g;
					for (int i = 0; i < p.getNumGeometries(); i++) {
						extrudePolygon(gid, (Polygon) p.getGeometryN(i),
								height, driver);
					}
				} else {
					throw new ExecutionException(
							"Extrude only (Multi-)Polygon geometries");
				}
			}

			return driver;
		} catch (DriverException e) {
			throw new ExecutionException(e);
		} catch (DriverLoadException e) {
			throw new ExecutionException(e);
		}
	}

	private LineString getClockWise(final LineString lineString) {
		final Coordinate c0 = lineString.getCoordinateN(0);
		final Coordinate c1 = lineString.getCoordinateN(1);
		final Coordinate c2 = lineString.getCoordinateN(2);

		if (CGAlgorithms.computeOrientation(c0, c1, c2) == CGAlgorithms.CLOCKWISE) {
			return lineString;
		} else {
			return (LineString) lineString.reverse();
		}
	}

	private LineString getCounterClockWise(final LineString lineString) {
		final Coordinate c0 = lineString.getCoordinateN(0);
		final Coordinate c1 = lineString.getCoordinateN(1);
		final Coordinate c2 = lineString.getCoordinateN(2);

		if (CGAlgorithms.computeOrientation(c0, c1, c2) == CGAlgorithms.COUNTERCLOCKWISE) {
			return lineString;
		} else {
			return (LineString) lineString.reverse();
		}
	}

	private Polygon getClockWise(final Polygon polygon) {
		final LinearRing shell = geometryFactory.createLinearRing(getClockWise(
				polygon.getExteriorRing()).getCoordinates());
		final int nbOfHoles = polygon.getNumInteriorRing();
		final LinearRing[] holes = new LinearRing[nbOfHoles];
		for (int i = 0; i < nbOfHoles; i++) {
			holes[i] = geometryFactory.createLinearRing(getCounterClockWise(
					polygon.getInteriorRingN(i)).getCoordinates());
		}
		return geometryFactory.createPolygon(shell, holes);
	}

	private Polygon getCounterClockWise(final Polygon polygon) {
		final LinearRing shell = geometryFactory
				.createLinearRing(getCounterClockWise(polygon.getExteriorRing())
						.getCoordinates());
		final int nbOfHoles = polygon.getNumInteriorRing();
		final LinearRing[] holes = new LinearRing[nbOfHoles];
		for (int i = 0; i < nbOfHoles; i++) {
			holes[i] = geometryFactory.createLinearRing(getClockWise(
					polygon.getInteriorRingN(i)).getCoordinates());
		}
		return geometryFactory.createPolygon(shell, holes);
	}

	private void extrudePolygon(final Value gid, final Polygon polygon,
			final double high, final ObjectMemoryDriver driver)
			throws DriverException {
		Value wallType = ValueFactory.createValue("wall");

		/* exterior ring */
		final LineString shell = getClockWise(polygon.getExteriorRing());
		Value shellHoleId = ValueFactory.createValue((short) -1);
		for (int i = 1; i < shell.getNumPoints(); i++) {
			final Polygon wall = extrudeEdge(shell.getCoordinateN(i - 1), shell
					.getCoordinateN(i), high);
			driver.addValues(new Value[] { gid, shellHoleId, wallType,
					ValueFactory.createValue((short) (i - 1)),
					ValueFactory.createValue(wall) });
		}

		/* holes */
		final int nbOfHoles = polygon.getNumInteriorRing();
		for (int i = 0; i < nbOfHoles; i++) {
			final LineString hole = getCounterClockWise(polygon
					.getInteriorRingN(i));
			shellHoleId = ValueFactory.createValue((short) i);
			for (int j = 1; j < hole.getNumPoints(); j++) {
				final Polygon wall = extrudeEdge(hole.getCoordinateN(j - 1),
						hole.getCoordinateN(j), high);

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
				ValueFactory.createValue(getClockWise(polygon)) });

		/* roof */
		wallType = ValueFactory.createValue("ceiling");

		final LinearRing upperShell = translate(polygon.getExteriorRing(), high);
		final LinearRing[] holes = new LinearRing[nbOfHoles];
		for (int i = 0; i < nbOfHoles; i++) {
			holes[i] = translate(polygon.getInteriorRingN(i), high);
		}
		final Polygon pp = geometryFactory.createPolygon(upperShell, holes);
		driver.addValues(new Value[] { gid, shellHoleId, wallType,
				ValueFactory.createValue((short) 0),
				ValueFactory.createValue(getCounterClockWise(pp)) });
	}

	private Polygon extrudeEdge(final Coordinate beginPoint,
			Coordinate endPoint, final double high) {
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

	private LinearRing translate(final LineString ring, final double high) {
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
		return "select Extrude(id, height[, the_geom]) from myTable;";
	}

	public String getDescription() {
		return "Extrude a 2D polygon using a height field value";
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		try {
			return new DefaultMetadata(new Type[] {
					TypeFactory.createType(Type.STRING),
					TypeFactory.createType(Type.SHORT),
					TypeFactory.createType(Type.STRING),
					TypeFactory.createType(Type.SHORT),
					TypeFactory.createType(Type.GEOMETRY, new Constraint[] {
							new GeometryConstraint(GeometryConstraint.POLYGON),
							new DimensionConstraint(3) }) }, new String[] {
					"gid", "shellHoleId", "type", "index", "the_geom" });
		} catch (InvalidTypeException e) {
			throw new DriverException(
					"InvalidTypeException in metadata instantiation", e);
		}
	}

	public TableDefinition[] geTablesDefinitions() {
		return new TableDefinition[] { TableDefinition.GEOMETRY };
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] {
				new Arguments(Argument.WHOLE_NUMBER, Argument.NUMERIC),
				new Arguments(Argument.WHOLE_NUMBER, Argument.NUMERIC,
						Argument.GEOMETRY) };
	}
}