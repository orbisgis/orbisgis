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
package org.gdms.sql.customQuery.spatial.geometry.tin;

import java.util.ArrayList;
import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.TableDefinition;
import org.gdms.sql.function.Arguments;
import org.geoalgorithm.orbisgis.triangulation.Triangulation;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class BuildTIN implements CustomQuery {
	private static GeometryFactory gf = new GeometryFactory();

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		SpatialDataSourceDecorator inSds = new SpatialDataSourceDecorator(
				tables[0]);
		try {
			inSds.open();
			// populate and mesh the Planar Straight-Line Graph using the unique
			// table as input data
			final long rowCount = inSds.getRowCount();

			final List<Coordinate> points = new ArrayList<Coordinate>();
			final List<int[]> breakLineList = new ArrayList<int[]>();
			for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				addConstraint(inSds.getGeometry(rowIndex), points,
						breakLineList);
			}

			inSds.close();
			final Triangulation t = new Triangulation(points
					.toArray(new Coordinate[0]), (int[][]) breakLineList
					.toArray(new int[][] {}));
			t.triangulate();

			// convert the resulting TIN into a data source
			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					getMetadata(null));
			final Coordinate[] cc = t.getTriangulatedPoints();

			for (int i = 0; i < cc.length; i += 3) {
				final Polygon tmpPolygon = gf.createPolygon(gf
						.createLinearRing(new Coordinate[] { cc[i], cc[i + 1],
								cc[i + 2], cc[i] }), new LinearRing[0]);
				driver.addValues(new Value[] {
						ValueFactory.createValue(new Integer(i / 3)),
						ValueFactory.createValue(tmpPolygon) });
			}
			return driver;
		} catch (DriverException e) {
			throw new ExecutionException(e);
		}
	}

	private void addConstraint(Geometry geometry, List<Coordinate> points,
			List<int[]> breakLineList) {
		if (geometry instanceof Point) {
			points.add(geometry.getCoordinate());
		} else if (geometry instanceof LineString) {
			addConstraint((LineString) geometry, points, breakLineList);
		} else if (geometry instanceof Polygon) {
			Polygon polygon = (Polygon) geometry;
			addConstraint(polygon.getExteriorRing(), points, breakLineList);
			for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
				addConstraint(polygon.getInteriorRingN(i), points,
						breakLineList);
			}
		} else if (geometry instanceof GeometryCollection) {
			GeometryCollection gc = (GeometryCollection) geometry;
			for (int i = 0; i < gc.getNumGeometries(); i++) {
				addConstraint(gc.getGeometryN(i), points, breakLineList);
			}
		}
	}

	private void addConstraint(LineString lineString, List<Coordinate> points,
			List<int[]> breakLineList) {
		int idx = points.size();
		int[] breakLine = new int[lineString.getNumPoints()];
		for (int j = 0, n = lineString.getNumPoints(); j < n; j++) {
			points.add(lineString.getCoordinates()[j]);
			breakLine[j] = idx++;
		}
		breakLineList.add(breakLine);
	}

	public String getDescription() {
		return "Implementation of a 2D Constrained Delaunay Triangulation written by M. Michaud (France, IGN)";
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		try {
			return new DefaultMetadata(new Type[] {
					TypeFactory.createType(Type.INT),
					TypeFactory.createType(Type.GEOMETRY,
							new Constraint[] { new GeometryConstraint(
									GeometryConstraint.POLYGON) }) },
					new String[] { "gid", "the_geom" });
		} catch (InvalidTypeException e) {
			throw new DriverException(
					"InvalidTypeException in metadata instantiation", e);
		}
	}

	public String getName() {
		return "BuildTIN";
	}

	public String getSqlOrder() {
		return "select BuildTIN() from mydatasource";
	}

	public TableDefinition[] geTablesDefinitions() {
		return new TableDefinition[] { TableDefinition.GEOMETRY };
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments() };
	}
}