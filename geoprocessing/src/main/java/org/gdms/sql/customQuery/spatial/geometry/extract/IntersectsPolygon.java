package org.gdms.sql.customQuery.spatial.geometry.extract;

import java.util.Iterator;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.indexes.DefaultSpatialIndexQuery;
import org.gdms.data.indexes.IndexException;
import org.gdms.data.indexes.IndexManager;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.TableDefinition;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public class IntersectsPolygon implements CustomQuery {

	private ObjectMemoryDriver driver;

	@Override
	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		try {

			final SpatialDataSourceDecorator sds1 = new SpatialDataSourceDecorator(
					tables[0]);

			final SpatialDataSourceDecorator sdsArea = new SpatialDataSourceDecorator(
					tables[1]);

			sds1.open();
			sdsArea.open();

			final String spatialFieldName1 = values[0].toString();

			final String spatialFieldNameArea = values[1].toString();

			if (!dsf.getIndexManager().isIndexed(tables[0].getName(),
					spatialFieldName1)) {
				dsf.getIndexManager().buildIndex(tables[0].getName(),
						spatialFieldName1, IndexManager.RTREE_SPATIAL_INDEX,
						null);
			}

			sds1.setDefaultGeometry(spatialFieldName1);
			sdsArea.setDefaultGeometry(spatialFieldNameArea);

			long countZone = sdsArea.getRowCount();

			if (countZone > 1) {
				throw new ExecutionException("Only one row is allowed !");
			} else {
				Geometry geomZone = sdsArea.getGeometry(0);

				if (geomZone instanceof Polygon) {

					if (geomZone.getEnvelopeInternal().intersects(
							sds1.getFullExtent())) {

						driver = clip(sds1, geomZone, spatialFieldName1, pm);
					}

				}

				else if (geomZone instanceof MultiPolygon) {

					if (geomZone.getNumGeometries() > 1) {

						throw new ExecutionException(
								"Only one polygon is allowed !");
					} else {
						driver = clip(sds1, geomZone, spatialFieldName1, pm);
					}

				}

			}

			sds1.close();
			sdsArea.close();

		} catch (DriverException e) {
			e.printStackTrace();
		} catch (NoSuchTableException e) {
			e.printStackTrace();
		} catch (IndexException e) {
			e.printStackTrace();
		}

		return driver;
	}

	private ObjectMemoryDriver clip(SpatialDataSourceDecorator sds,
			Geometry geomZone, String spatialNameIndex, IProgressMonitor pm)
			throws DriverException {

		final int spatialFieldIndex = sds.getSpatialFieldIndex();
		final ObjectMemoryDriver driver = new ObjectMemoryDriver(sds
				.getMetadata());
		long rowCount = sds.getRowCount();
		Iterator<Integer> it = sds.queryIndex(new DefaultSpatialIndexQuery(
				geomZone.getEnvelopeInternal(), spatialNameIndex));
		int i = 0;
		while (it.hasNext()) {
			Integer index = it.next();

			if (i / 1000 == i / 1000.0) {
				if (pm.isCancelled()) {
					break;
				} else {
					pm.progressTo((int) (100 * i / rowCount));
				}
			}
			i++;

			final Value[] fieldsValues = sds.getRow(index);
			final Geometry geometry = sds.getGeometry(index);

			if (geometry.intersects(geomZone)) {

				if ((geometry.isValid()) && (!geometry.isEmpty())) {

					fieldsValues[spatialFieldIndex] = ValueFactory
							.createValue(geometry);

					driver.addValues(fieldsValues);

				}
			}

		}

		return driver;

	}

	@Override
	public TableDefinition[] geTablesDefinitions() {
		return new TableDefinition[] { TableDefinition.GEOMETRY,
				TableDefinition.GEOMETRY };
	}

	@Override
	public String getDescription() {
		return "Select geometries that intersect a geometry area and keep the attributes";
	}

	@Override
	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.GEOMETRY,
				Argument.GEOMETRY) };
	}

	@Override
	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return tables[0];
	}

	@Override
	public String getName() {
		return "IntersectGeometry";
	}

	@Override
	public String getSqlOrder() {
		return "select IntersectGeometry(a.the_geom, b.the_geom) from myTable a, myZone b;";
	}

}
