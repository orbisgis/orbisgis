package org.contrib.gdms.sql.customQuery.spatial.geometry.tin;

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
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.TableDefinition;
import org.gdms.sql.function.Arguments;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Triangle;

public class Tin2Voronoi implements CustomQuery {
	private static final GeometryFactory gf = new GeometryFactory();

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		try {
			final SpatialDataSourceDecorator triangles = new SpatialDataSourceDecorator(
					tables[0]);
			final long rowCount = triangles.getRowCount();
			final Coordinate[] centroids = new Coordinate[(int) rowCount];

			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					getMetadata(null));

			int count = 0;
			for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				if (rowIndex / 100 == rowIndex / 100.0) {
					if (pm.isCancelled()) {
						break;
					} else {
						pm.progressTo((int) (100 * rowIndex / rowCount));
					}
				}

				final Value[] rowValues = triangles.getRow(rowIndex);
				final Coordinate[] triVertices = rowValues[1].getAsGeometry()
						.getCoordinates();
				final int[] neighGid = new int[] { rowValues[5].getAsInt(),
						rowValues[6].getAsInt(), rowValues[7].getAsInt() };

				centroids[(int) rowIndex] = Triangle.circumcentre(
						triVertices[0], triVertices[1], triVertices[2]);

				for (int i = 0; i < neighGid.length; i++) {
					if ((neighGid[i] < rowIndex) && (-1 < neighGid[i])) {
						driver
								.addValues(new Value[] {
										ValueFactory.createValue(count++),
										ValueFactory
												.createValue(gf
														.createLineString(new Coordinate[] {
																centroids[neighGid[i]],
																centroids[(int) rowIndex] })) });
					}
				}
			}
			return driver;
		} catch (DriverException e) {
			throw new ExecutionException(e);
		} catch (DriverLoadException e) {
			throw new ExecutionException(e);
		}
	}

	@Override
	public TableDefinition[] geTablesDefinitions() {
		return new TableDefinition[] { TableDefinition.GEOMETRY };
	}

	@Override
	public String getDescription() {
		return "Transform a set of triangles into a Voronoi diagram";
	}

	@Override
	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments() };
	}

	@Override
	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		try {
			return new DefaultMetadata(new Type[] {
					TypeFactory.createType(Type.INT),
					TypeFactory.createType(Type.GEOMETRY,
							new Constraint[] { new GeometryConstraint(
									GeometryConstraint.LINESTRING) }) },
					new String[] { "gid", "the_geom" });
		} catch (InvalidTypeException e) {
			throw new DriverException(
					"InvalidTypeException in metadata instantiation", e);
		}
	}

	@Override
	public String getName() {
		return "Tin2Voronoi";
	}

	@Override
	public String getSqlOrder() {
		return "select " + getName() + "() from triangles;";
	}
}