package org.gdms.sql.customQuery.spatial.geometry;

import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.quadtree.Quadtree;

public class CheckSpatialEquivalence implements CustomQuery {
	@SuppressWarnings("unchecked")
	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		SpatialDataSourceDecorator inSds1 = new SpatialDataSourceDecorator(
				tables[0]);
		SpatialDataSourceDecorator inSds2 = new SpatialDataSourceDecorator(
				tables[1]);

		try {
			inSds1.open();
			inSds2.open();
			long rowCount1 = inSds1.getRowCount();
			long rowCount2 = inSds2.getRowCount();

			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					getMetadata(null));

			if (rowCount1 != rowCount2) {
				driver.addValues(new Value[] {
						ValueFactory.FALSE,
						ValueFactory
								.createValue("Tables length are differents") });
				return driver;
			}

			// build a spatial index using all the table2 geometries...
			SpatialIndex spatialIndex = new Quadtree(); // new
			for (long rowIndex = 0; rowIndex < rowCount2; rowIndex++) {
				Geometry geometry = inSds2.getGeometry(rowIndex);
				if (null != geometry) {
					spatialIndex.insert(geometry.getEnvelopeInternal(),
							geometry);
				}
			}

			// iterate over all the table1 geometries
			for (long rowIndex = 0; rowIndex < rowCount2; rowIndex++) {
				Geometry geometry1 = inSds1.getGeometry(rowIndex);
				if (null != geometry1) {
					List<Geometry> sublist = spatialIndex.query(geometry1
							.getEnvelopeInternal());
					boolean isIn = false;
					for (Geometry geometry2 : sublist) {
						if (geometry1.contains(geometry2)
								&& geometry2.contains(geometry1)) {
							spatialIndex.remove(
									geometry2.getEnvelopeInternal(), geometry2);
							isIn = true;
							break;
						}
					}
					if (!isIn) {
						driver.addValues(new Value[] {
								ValueFactory.FALSE,
								ValueFactory.createValue("Geometry #"
										+ rowIndex
										+ " from table1 is not in table2 !") });
						return driver;
					}
				}
			}
			inSds1.close();
			inSds2.close();

			driver.addValues(new Value[] {
					ValueFactory.TRUE,
					ValueFactory
							.createValue("table1 is equivalent to table2 !") });

			return driver;
		} catch (DriverException e) {
			throw new ExecutionException(e);
		}
	}

	public TableDefinition[] geTablesDefinitions() {
		return new TableDefinition[] { TableDefinition.GEOMETRY,
				TableDefinition.GEOMETRY };
	}

	public String getDescription() {
		return "This CustomQuery checks if each geometry in table1 is contained and contains a geometry in table2 (and vice versa)";
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments() };
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return new DefaultMetadata(new Type[] {
				TypeFactory.createType(Type.BOOLEAN),
				TypeFactory.createType(Type.STRING) }, new String[] {
				"are equivalent", "comment" });
	}

	public String getName() {
		return "CheckSpatialEquivalence";
	}

	public String getSqlOrder() {
		return "select CheckSpatialEquivalence() from table1, table2;";
	}
}