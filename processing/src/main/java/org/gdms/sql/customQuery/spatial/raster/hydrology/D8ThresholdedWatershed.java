package org.gdms.sql.customQuery.spatial.raster.hydrology;

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
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.gdms.sql.strategies.SemanticException;
import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.processing.operation.hydrology.WatershedsWithThreshold;
import org.orbisgis.progress.IProgressMonitor;

public class D8ThresholdedWatershed implements CustomQuery {

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		final SpatialDataSourceDecorator sds1 = new SpatialDataSourceDecorator(
				tables[0]);
		final SpatialDataSourceDecorator sds2 = new SpatialDataSourceDecorator(
				tables[1]);
		final SpatialDataSourceDecorator sds3 = new SpatialDataSourceDecorator(
				tables[2]);
		final int watershedThreshold = values[0].getAsInt();

		try {
			sds1.open();
			sds2.open();
			sds3.open();

			sds1.setDefaultGeometry(values[1].toString());
			sds2.setDefaultGeometry(values[2].toString());
			sds3.setDefaultGeometry(values[3].toString());

			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					getMetadata(null));

			final long rowCount1 = sds1.getRowCount();
			final long rowCount2 = sds2.getRowCount();
			final long rowCount3 = sds3.getRowCount();

			if ((rowCount1 != rowCount2) && (rowCount1 != rowCount3)) {
				throw new ExecutionException(
						"The three input raster tables must have the same number of rows");
			}

			for (int rowIndex = 0; rowIndex < rowCount1; rowIndex++) {

				if (rowIndex / 100 == rowIndex / 100.0) {
					if (pm.isCancelled()) {
						break;
					} else {
						pm.progressTo((int) (100 * rowIndex / rowCount1));
					}
				}

				final GeoRaster grSlopesAccumulations = sds1
						.getRaster(rowIndex);
				final GeoRaster grAllWatersheds = sds2.getRaster(rowIndex);
				final GeoRaster grAllOutlets = sds3.getRaster(rowIndex);

				// extract some "big" watersheds
				final Operation watershedsWithThreshold = new WatershedsWithThreshold(
						grAllWatersheds, grAllOutlets, watershedThreshold);

				// populate the resulting ObjectMemoryDriver
				driver.addValues(new Value[] { ValueFactory
						.createValue(grSlopesAccumulations
								.doOperation(watershedsWithThreshold)) });
			}
			sds2.cancel();
			sds1.cancel();
			return driver;
		} catch (DriverException e) {
			throw new ExecutionException(e);
		} catch (OperationException e) {
			throw new ExecutionException(e);
		} catch (GeoreferencingException e) {
			throw new ExecutionException(e);
		}
	}

	public String getDescription() {
		return "Compute all the \"big\" watersheds (that is to say all those whose outlet accumulate "
				+ "more than the WatershedThreshold integer value";
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return new DefaultMetadata(new Type[] { TypeFactory
				.createType(Type.RASTER) }, new String[] { "raster" });
	}

	public String getName() {
		return "D8ThresholdedWatershed";
	}

	public String getSqlOrder() {
		return "select D8ThresholdedWatershed(WatershedThreshold, a.raster, w.raster, o.raster) from accumulations a, allwatersheds w, alloutlets o;";
	}

	public void validateTables(Metadata[] tables) throws SemanticException,
			DriverException {
		FunctionValidator.failIfBadNumberOfTables(this, tables, 3);
		FunctionValidator.failIfNotRasterDataSource(this, tables[0], 1);
		FunctionValidator.failIfNotRasterDataSource(this, tables[1], 2);
		FunctionValidator.failIfNotRasterDataSource(this, tables[2], 3);
	}

	public void validateTypes(Type[] types) throws IncompatibleTypesException {
		FunctionValidator.failIfBadNumberOfArguments(this, types, 4);
		FunctionValidator.failIfNotNumeric(this, types[0], 1);
		FunctionValidator.failIfNotOfType(this, types[1], Type.RASTER);
		FunctionValidator.failIfNotOfType(this, types[2], Type.RASTER);
		FunctionValidator.failIfNotOfType(this, types[3], Type.RASTER);
	}
}