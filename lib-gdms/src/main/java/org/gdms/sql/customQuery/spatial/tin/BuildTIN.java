package org.gdms.sql.customQuery.spatial.tin;

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
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.gdms.sql.strategies.SemanticException;
import org.gdms.triangulation.core.TriangulatedIrregularNetwork;
import org.gdms.triangulation.jts.Triangle;
import org.orbisgis.IProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

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
						addToTIN(theTin, (LineString) gc.getGeometryN(i));
					}
				}
			}
			theTin.buildIndex();
			inSds.cancel();

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

	public void validateTables(Metadata[] tables) throws SemanticException,
			DriverException {
		FunctionValidator.failIfBadNumberOfTables(this, tables, 1);
		FunctionValidator.failIfNotSpatialDataSource(this, tables[0], 0);
	}

	public void validateTypes(Type[] types) throws IncompatibleTypesException {
		FunctionValidator.failIfBadNumberOfArguments(this, types, 0, 1);
		if (1 == types.length) {
			FunctionValidator.failIfNotOfType(this, types[0], Type.GEOMETRY);
		}
	}
}