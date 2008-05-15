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
import org.grap.processing.operation.hydrology.StrahlerStreamOrder;
import org.orbisgis.progress.IProgressMonitor;

public class D8StrahlerStreamOrder implements CustomQuery {

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		final SpatialDataSourceDecorator sds1 = new SpatialDataSourceDecorator(
				tables[0]);
		final SpatialDataSourceDecorator sds2 = new SpatialDataSourceDecorator(
				tables[1]);
		final int riverThreshold = values[0].getAsInt();

		try {
			sds1.open();
			sds2.open();

			sds1.setDefaultGeometry(values[1].toString());
			sds2.setDefaultGeometry(values[2].toString());

			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					getMetadata(null));

			final long rowCount1 = sds1.getRowCount();
			final long rowCount2 = sds2.getRowCount();
			if (rowCount1 != rowCount2) {
				throw new ExecutionException(
						"The two input raster tables must have the same number of rows");
			}

			for (int rowIndex = 0; rowIndex < rowCount1; rowIndex++) {

				if (rowIndex / 100 == rowIndex / 100.0) {
					if (pm.isCancelled()) {
						break;
					} else {
						pm.progressTo((int) (100 * rowIndex / rowCount1));
					}
				}

				final GeoRaster grSlopesDirections = sds1.getRaster(rowIndex);
				final GeoRaster grSlopesAccumulations = sds2
						.getRaster(rowIndex);

				// prepare the Strahler Stream Order computation
				final Operation opeStrahlerStreamOrder = new StrahlerStreamOrder(
						grSlopesAccumulations, riverThreshold);

				// compute the Strahler stream orders and populate the resulting
				// ObjectMemoryDriver
				driver.addValues(new Value[] { ValueFactory
						.createValue(grSlopesDirections
								.doOperation(opeStrahlerStreamOrder)) });
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
		return "Compute the Strahler Stream Order using a GRAY16/32 DEM slopes accumulations as input table."
				+ "The RiverThreshold is an integer value that corresponds to the minimal value of "
				+ "accumulation for a cell to be seens as a 1st level river.";
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return new DefaultMetadata(new Type[] { TypeFactory
				.createType(Type.RASTER) }, new String[] { "raster" });
	}

	public String getName() {
		return "D8StrahlerStreamOrder";
	}

	public String getSqlOrder() {
		return "select D8StrahlerStreamOrder(RiverThreshold, d.raster, a.raster) from directions d, accumulations a;";
	}

	public void validateTables(Metadata[] tables) throws SemanticException,
			DriverException {
		FunctionValidator.failIfBadNumberOfTables(this, tables, 2);
		FunctionValidator.failIfNotRasterDataSource(this, tables[0], 1);
		FunctionValidator.failIfNotRasterDataSource(this, tables[1], 2);
	}

	public void validateTypes(Type[] types) throws IncompatibleTypesException {
		FunctionValidator.failIfBadNumberOfArguments(this, types, 3);
		FunctionValidator.failIfNotNumeric(this, types[0], 1);
		FunctionValidator.failIfNotOfType(this, types[1], Type.RASTER);
		FunctionValidator.failIfNotOfType(this, types[2], Type.RASTER);
	}
}