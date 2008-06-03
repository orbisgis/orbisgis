package org.gdms.sql.customQuery.spatial.geometry.tin;

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
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.gdms.sql.strategies.SemanticException;
import org.gdms.triangulation.sweepLine4CDT.CDTTriangle;
import org.gdms.triangulation.sweepLine4CDT.CDTVertex;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.quadtree.Quadtree;

public class CheckDelaunayProperty implements CustomQuery {

	@SuppressWarnings("unchecked")
	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		SpatialDataSourceDecorator inSds = new SpatialDataSourceDecorator(
				tables[0]);

		try {
			inSds.open();
			long rowCount = inSds.getRowCount();

			Set<CDTTriangle> setOfCDTTriangles = new HashSet<CDTTriangle>(
					(int) rowCount);
			Set<Coordinate> setOfVertices = new HashSet<Coordinate>(
					(int) rowCount * 2);
			SpatialIndex verticesSpatialIndex = new Quadtree(); // new
			// STRtree(10);

			for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {

				Coordinate[] coordinates = inSds.getGeometry(rowIndex)
						.getCoordinates();
				CDTTriangle cdtTriangle = new CDTTriangle(new CDTVertex(
						coordinates[0]), new CDTVertex(coordinates[1]),
						new CDTVertex(coordinates[2]), null);
				setOfCDTTriangles.add(cdtTriangle);

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
			inSds.cancel();

			// convert the resulting TIN into a data source
			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					getMetadata(null));
			long index = 0;
			for (CDTTriangle cdtTriangle : setOfCDTTriangles) {
				List<Coordinate> sublistOfVertices = verticesSpatialIndex
						.query(cdtTriangle.getCircumCircle()
								.getEnvelopeInternal());

				for (Coordinate c : sublistOfVertices) {
					if ((!cdtTriangle.isAVertex(new CDTVertex(c)))
							&& (!cdtTriangle.respectDelaunayProperty(c))) {
						driver.addValues(new Value[] {
								ValueFactory.createValue(index++),
								ValueFactory.createValue(cdtTriangle
										.getCircumCircle().getGeometry()) });
						break;
					}
				}
			}
			return driver;
		} catch (DriverException e) {
			throw new ExecutionException(e);
		}
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
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

	public void validateTables(Metadata[] tables) throws SemanticException,
			DriverException {
		FunctionValidator.failIfBadNumberOfTables(this, tables, 1);
		FunctionValidator.failIfNotSpatialDataSource(this, tables[0], 0);
	}

	public void validateTypes(Type[] types) throws IncompatibleTypesException {
		FunctionValidator.failIfBadNumberOfArguments(this, types, 0);
	}
}