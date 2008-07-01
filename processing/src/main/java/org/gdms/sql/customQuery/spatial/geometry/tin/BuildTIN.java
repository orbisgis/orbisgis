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
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.triangulation.core.TriangulatedIrregularNetwork;
import org.gdms.triangulation.jts.Triangle;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class BuildTIN implements CustomQuery {
	private static GeometryFactory geometryFactory = new GeometryFactory();
	private static double BUFFER = 10.d;

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		final SpatialDataSourceDecorator inSds = new SpatialDataSourceDecorator(
				tables[0]);

		try {
			// build the TIN using the input spatial data source
			inSds.open();
			if (1 == values.length) {
				// if no spatial's field's name is provided, the default (first)
				// one is arbitrarily chosen.
				final String spatialFieldName = values[0].toString();
				inSds.setDefaultGeometry(spatialFieldName);
			}

			final TriangulatedIrregularNetwork theTin = new TriangulatedIrregularNetwork(
					geometryFactory, toGeometry(inSds.getFullExtent()).buffer(
							BUFFER).getEnvelopeInternal());

			final long rowCount = inSds.getRowCount();
			for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				final Geometry geometry = inSds.getGeometry(rowIndex);
				if (geometry instanceof Point) {
					addToTIN(theTin, (Point) geometry);
				} else if (geometry instanceof LineString) {
					addToTIN(theTin, (LineString) geometry);
				} else if (geometry instanceof GeometryCollection) {
					final GeometryCollection gc = (GeometryCollection) geometry;
					for (int i = 0; i < gc.getNumGeometries(); i++) {
						addToTIN(theTin, gc.getGeometryN(i));
					}
				}
			}
			theTin.buildIndex();
			inSds.close();

			// convert the TIN into a data source
			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					getMetadata(null));
			long index = 0;
			for (Triangle triangle : theTin.getTriangles()) {
				driver.addValues(new Value[] { ValueFactory.createValue(index),
						ValueFactory.createValue(triangle.getPolygon()) });
			}

			return driver;
		} catch (DriverException e) {
			throw new ExecutionException(e);
		}
	}

	private void addToTIN(final TriangulatedIrregularNetwork theTin,
			final Point point) {
		theTin.insertNode(point);
	}

	private void addToTIN(final TriangulatedIrregularNetwork theTin,
			final LineString lineString) {
		final Coordinate[] vertices = lineString.getCoordinates();

		theTin.insertNode(vertices[0]);
		// TODO delete duplicate segments...
		for (int i = 1; i < vertices.length; i++) {
			theTin.insertNode(vertices[i]);
			theTin.insertEdge(geometryFactory
					.createLineString(new Coordinate[] { vertices[i - 1],
							vertices[i] }));
		}
	}

	private void addToTIN(final TriangulatedIrregularNetwork theTin,
			final Polygon polygon) {
		// TODO deal with holes
		addToTIN(theTin, polygon.getExteriorRing());
	}

	private void addToTIN(final TriangulatedIrregularNetwork theTin,
			final Geometry geometry) {
		if (geometry instanceof Point) {
			addToTIN(theTin, (Point) geometry);
		} else if (geometry instanceof LineString) {
			addToTIN(theTin, (LineString) geometry);
		} else if (geometry instanceof Polygon) {
			addToTIN(theTin, (Polygon) geometry);
		} else if (geometry instanceof GeometryCollection) {
			addToTIN(theTin, (GeometryCollection) geometry);
		}
	}

	private void addToTIN(final TriangulatedIrregularNetwork theTin,
			final GeometryCollection geometry) {
		final GeometryCollection gc = (GeometryCollection) geometry;
		for (int i = 0; i < gc.getNumGeometries(); i++) {
			addToTIN(theTin, gc.getGeometryN(i));
		}
	}

	private static Geometry toGeometry(Envelope envelope) {
		if ((envelope.getWidth() == 0) && (envelope.getHeight() == 0)) {
			return geometryFactory.createPoint(new Coordinate(envelope
					.getMinX(), envelope.getMinY()));
		}

		if ((envelope.getWidth() == 0) || (envelope.getHeight() == 0)) {
			return geometryFactory.createLineString(new Coordinate[] {
					new Coordinate(envelope.getMinX(), envelope.getMinY()),
					new Coordinate(envelope.getMaxX(), envelope.getMaxY()) });
		}

		return geometryFactory.createLinearRing(new Coordinate[] {
				new Coordinate(envelope.getMinX(), envelope.getMinY()),
				new Coordinate(envelope.getMinX(), envelope.getMaxY()),
				new Coordinate(envelope.getMaxX(), envelope.getMaxY()),
				new Coordinate(envelope.getMaxX(), envelope.getMinY()),
				new Coordinate(envelope.getMinX(), envelope.getMinY()) });
	}

	public String getDescription() {
		return "Build a 2D TIN using the given table's shapes as input constraints";
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
		return "select BuildTIN([the_geom]) from myTable";
	}

	public TableDefinition[] geTablesDefinitions() {
		return new TableDefinition[] { TableDefinition.GEOMETRY };
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.GEOMETRY),
				new Arguments() };
	}
}