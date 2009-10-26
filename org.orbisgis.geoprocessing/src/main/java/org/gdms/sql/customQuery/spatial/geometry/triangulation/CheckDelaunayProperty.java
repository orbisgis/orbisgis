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
package org.gdms.sql.customQuery.spatial.geometry.triangulation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Triangle;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.strtree.STRtree;

public class CheckDelaunayProperty implements CustomQuery {
	private static final GeometryFactory gf = new GeometryFactory();

	private class TriangleCircumCircle {
		static final double EPSILON = 1E-4;

		Coordinate centre;
		double radius;
		Envelope envelope;

		TriangleCircumCircle(final Coordinate p0, final Coordinate p1,
				final Coordinate p2) {
			centre = Triangle.circumcentre(p0, p1, p2);
			radius = Math.sqrt((centre.x - p0.x) * (centre.x - p0.x)
					+ (centre.y - p0.y) * (centre.y - p0.y));
			envelope = new Envelope(centre.x - radius, centre.x + radius,
					centre.y - radius, centre.y + radius);
		}

		boolean contains(final Coordinate coordinate) {
			return centre.distance(coordinate) <= radius;
			// return centre.distance(coordinate) < radius + EPSILON;
		}

		Geometry getGeometry() {
			return gf.createPoint(centre).buffer(radius);
		}
	}

	@SuppressWarnings("unchecked")
	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		SpatialDataSourceDecorator inSds = new SpatialDataSourceDecorator(
				tables[0]);
		try {
			inSds.open();
			long rowCount = inSds.getRowCount();

			// first step, build the set of all vertices and the corresponding
			// spatial index...
			Set<Coordinate> setOfVertices = new HashSet<Coordinate>(
					(int) rowCount * 2);
			final SpatialIndex verticesSpatialIndex = new STRtree(10);

			for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				final Coordinate[] coordinates = inSds.getGeometry(rowIndex)
						.getCoordinates();
				if (setOfVertices.add(coordinates[0])) {
					verticesSpatialIndex.insert(new Envelope(coordinates[0]),
							coordinates[0]);
				}
				if (setOfVertices.add(coordinates[1])) {
					verticesSpatialIndex.insert(new Envelope(coordinates[1]),
							coordinates[1]);
				}
				if (setOfVertices.add(coordinates[2])) {
					verticesSpatialIndex.insert(new Envelope(coordinates[2]),
							coordinates[2]);
				}
			}
			setOfVertices = null;
			inSds.close();

			// second step, look for all the triangles that do not verify the
			// Delaunay empty circle property (no more than 3 vertices for each
			// triangle's circum-circle)
			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					getMetadata(null));
			long index = 0;
			for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {

				if (rowIndex / 100 == rowIndex / 100.0) {
					if (pm.isCancelled()) {
						break;
					} else {
						pm.progressTo((int) (100 * rowIndex / rowCount));
					}
				}

				final Coordinate[] coordinates = inSds.getGeometry(rowIndex)
						.getCoordinates();
				final TriangleCircumCircle tcc = new TriangleCircumCircle(
						coordinates[0], coordinates[1], coordinates[2]);
				final List<Coordinate> sublistOfVertices = verticesSpatialIndex
						.query(tcc.envelope);

				int counter = 0;
				for (Coordinate c : sublistOfVertices) {
					if (tcc.contains(c)) {
						counter++;
						if (counter > 3) {
							driver
									.addValues(new Value[] {
											ValueFactory.createValue(index++),
											ValueFactory.createValue(tcc
													.getGeometry()) });
							break;
						}
					}
				}
			}

			return driver;
		} catch (DriverException e) {
			throw new ExecutionException(e);
		}
	}

	public String getDescription() {
		return "This custom query checks the empty circum circle property "
				+ "(Delaunay property) for every triangle of the set";
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
		return "CheckDelaunayProperty";
	}

	public String getSqlOrder() {
		return "select CheckDelaunayProperty() from mytin;";
	}

	public TableDefinition[] geTablesDefinitions() {
		return new TableDefinition[] { TableDefinition.GEOMETRY };
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments() };
	}
}