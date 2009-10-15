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

import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
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
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.strtree.STRtree;

public class CheckCDT implements CustomQuery {
	private static final GeometryFactory gf = new GeometryFactory();

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		final SpatialDataSourceDecorator constraints_ds = new SpatialDataSourceDecorator(
				tables[0]);
		final SpatialDataSourceDecorator triangles_ds = new SpatialDataSourceDecorator(
				tables[1]);
		try {
			// first step: build the the spatial index of all linear constraints
			final SpatialIndex si = new STRtree(10);
			long constRowCount = constraints_ds.getRowCount();
			for (long rowIndex = 0; rowIndex < constRowCount; rowIndex++) {
				si.insert(constraints_ds.getGeometry(rowIndex)
						.getEnvelopeInternal(), rowIndex);
			}

			// then check each triangle one by one
			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					getMetadata(null));

			long triRowCount = triangles_ds.getRowCount();
			for (long rowIndex = 0; rowIndex < triRowCount; rowIndex++) {
				if (rowIndex / 100 == rowIndex / 100.0) {
					if (pm.isCancelled()) {
						break;
					} else {
						pm.progressTo((int) (100 * rowIndex / triRowCount));
					}
				}

				final Value[] rowValues = triangles_ds.getRow(rowIndex);

				final int triGid = rowValues[0].getAsInt();
				final Geometry geometry = rowValues[1].getAsGeometry();
				final Coordinate[] triVertices = geometry.getCoordinates();
				final int[] vtxGid = new int[] { rowValues[2].getAsInt(),
						rowValues[3].getAsInt(), rowValues[4].getAsInt() };
				final int[] neighGid = new int[] { rowValues[5].getAsInt(),
						rowValues[6].getAsInt(), rowValues[7].getAsInt() };
				final int[] vtxConstraintsGid = new int[] {
						rowValues[8].getAsInt(), rowValues[9].getAsInt(),
						rowValues[10].getAsInt() };
				final Value[] edgConstraintsGid = new Value[] { rowValues[11],
						rowValues[12], rowValues[13] };

				final LineString[] edges = new LineString[] {
						gf.createLineString(new Coordinate[] { triVertices[0],
								triVertices[1] }),
						gf.createLineString(new Coordinate[] { triVertices[1],
								triVertices[2] }),
						gf.createLineString(new Coordinate[] { triVertices[2],
								triVertices[0] }) };

				final Value[] tmp = new Value[] {
						ValueFactory.createValue(triGid),
						ValueFactory.createValue(geometry),
						ValueFactory.createNullValue(),
						ValueFactory.createNullValue(),
						ValueFactory.createNullValue() };
				boolean flag = false;

				for (int i = 0; i < edges.length; i++) {
					final int intersectionDimension = intersectionDimension(
							constraints_ds, si, edges[i]);
					if (8176 == triGid) {
						System.err.println();
					}

					if ((0 < intersectionDimension) == edgConstraintsGid[i]
							.isNull()) {
						// there is something wrong
						tmp[i + 2] = ValueFactory.createValue(1);
						flag = true;
						break;
					}
				}
				if (flag) {
					driver.addValues(tmp);
				}
			}
			return driver;
		} catch (DriverException e) {
			throw new ExecutionException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private int intersectionDimension(final SpatialDataSourceDecorator sds,
			final SpatialIndex spatialIndex, final LineString lineString)
			throws DriverException {
		// The intersection method does not support GeometryCollection arguments
		int result = 0;
		final Coordinate begin = lineString.getCoordinateN(0);
		final Coordinate end = lineString.getCoordinateN(1);
		final List<Long> subList = spatialIndex.query(lineString
				.getEnvelopeInternal());

		for (long idx : subList) {
			if (isASubLineString(sds.getGeometry(idx), begin, end)) {
				return 1;
			}
		}
		return 0;
	}

	private boolean isASubLineString(final GeometryCollection gc,
			final Coordinate begin, final Coordinate end) {
		for (int i = 0; i < gc.getNumGeometries(); i++) {
			if (isASubLineString(gc.getGeometryN(i), begin, end)) {
				return true;
			}
		}
		return false;
	}

	private boolean isASubLineString(final Geometry geometry,
			final Coordinate begin, final Coordinate end) {
		if (geometry instanceof GeometryCollection) {
			return isASubLineString((GeometryCollection) geometry, begin, end);
		} else if (geometry instanceof LineString) {
			return isASubLineString((LineString) geometry, begin, end);
		} else if (geometry instanceof Point) {
			return false;
		} else {
			throw new RuntimeException("Unreachable source code");
		}
	}

	private boolean isASubLineString(final LineString bigLineString,
			final Coordinate begin, final Coordinate end) {
		final Coordinate[] big = bigLineString.getCoordinates();
		for (int i = 0; i < big.length - 1; i++) {
			if ((begin.equals2D(big[i]) && end.equals2D(big[i + 1]))
					|| (end.equals2D(big[i]) && begin.equals2D(big[i + 1]))) {
				return true;
			}
		}
		return false;
	}

	public String getDescription() {
		return "This custom query checks the 'weaker' empty circum circle property "
				+ "(constraint Delaunay property) for every triangle of the set";
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		try {
			return new DefaultMetadata(new Type[] {
					TypeFactory.createType(Type.INT),
					TypeFactory.createType(Type.GEOMETRY),
					TypeFactory.createType(Type.INT),
					TypeFactory.createType(Type.INT),
					TypeFactory.createType(Type.INT) }, new String[] { "gid",
					"geom", "edg0_bad_lbl", "edg1_bad_lbl", "edg2_bad_lbl", });
		} catch (InvalidTypeException e) {
			throw new DriverException(
					"InvalidTypeException in metadata instantiation", e);
		}
	}

	public String getName() {
		return "CheckCDT";
	}

	public String getSqlOrder() {
		return "select " + getName() + "() from constraints, mytin;";
	}

	public TableDefinition[] geTablesDefinitions() {
		return new TableDefinition[] { TableDefinition.GEOMETRY,
				TableDefinition.GEOMETRY };
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments() };
	}
}