package org.urbsat.kmeans;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SQLSourceDefinition;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.IntValue;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.customQuery.CustomQuery;

public class KMeans implements CustomQuery {
	private DataSourceFactory dsf;
	private int nbOfCluster;
	private DataSource inDs;
	private String cellIndexFieldName;
	private int cellIndexFieldId;
	private Metadata metadata;

	public String getDescription() {
		return "Usage: select KMeans(cellIndex, nbOfClusters) from myTable;";
	}

	public String getName() {
		return "KMeans";
	}

	public DataSource evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values) throws ExecutionException {
		if (tables.length != 1) {
			throw new ExecutionException("KMeans only operates on one table");
		}
		if (2 != values.length) {
			throw new ExecutionException(
					"KMeans only operates with two values (the cell index field and the number of clusters)");
		}
		this.dsf = dsf;
		cellIndexFieldName = values[0].toString();
		nbOfCluster = ((IntValue) values[1]).intValue();
		inDs = tables[0];

		try {
			// built the driver for the resulting datasource and register it...
			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					new String[] { "index", "clusterNumber" }, new Type[] {
							TypeFactory.createType(Type.INT),
							TypeFactory.createType(Type.INT) });
			final String outDsName = dsf.getSourceManager().nameAndRegister(
					driver);

			inDs.open();
			cellIndexFieldId = inDs.getFieldIndexByName(cellIndexFieldName);
			check();

			// K-Means initialization
			initialization();

			// final int fieldCount = inDs.getFieldCount();
			// final long rowCount = inDs.getRowCount();
			// final double min[] = new double[fieldCount - 1];
			// final double max[] = new double[fieldCount - 1];
			// final double averages[] = new double[fieldCount - 1];
			// final double standardDeviation[] = new double[fieldCount - 1];
			//
			// for (int fieldIndex = 0; fieldIndex < fieldCount; fieldIndex++) {
			// min[fieldIndex] = Double.POSITIVE_INFINITY;
			// max[fieldIndex] = Double.NEGATIVE_INFINITY;
			// averages[fieldIndex] = Double.MAX_VALUE;
			// }
			//
			// for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			// for (int fieldIndex = 0; fieldIndex < fieldCount; fieldIndex++) {
			//
			// }
			// }
			inDs.cancel();

			return dsf.getDataSource(outDsName);
		} catch (DriverException e) {
			throw new ExecutionException(e);
		} catch (InvalidTypeException e) {
			throw new ExecutionException(e);
		} catch (DriverLoadException e) {
			throw new ExecutionException(e);
		} catch (NoSuchTableException e) {
			throw new ExecutionException(e);
		} catch (DataSourceCreationException e) {
			throw new ExecutionException(e);
		}
	}

	private void check() throws DriverException, ExecutionException {
		metadata = inDs.getMetadata();
		final int fieldCount = metadata.getFieldCount();
		for (int fieldId = 0; fieldId < fieldCount; fieldId++) {
			if (cellIndexFieldId != fieldId) {
				final int t = metadata.getFieldType(fieldId).getTypeCode();
				if ((t != 2) && ((t < 4) || (t > 8))) {
					throw new ExecutionException("Field '"
							+ metadata.getFieldName(fieldId)
							+ "' is not numeric !");
				}
			}
		}
	}

	private void initialization() throws DriverException, DriverLoadException,
			NoSuchTableException, DataSourceCreationException {
		final int fieldCount = inDs.getFieldCount();

		final String[] fieldNames = new String[fieldCount];

		// calculate one average value per field...
		// build the corresponding query
		final StringBuilder avgQuerySb = new StringBuilder();
		avgQuerySb.append("select ");
		for (int fieldId = 0; fieldId < fieldCount; fieldId++) {
			if (cellIndexFieldId != fieldId) {
				fieldNames[fieldId] = metadata.getFieldName(fieldId);
				avgQuerySb.append("avg(" + fieldNames[fieldId] + "),");
			} else {
				avgQuerySb.append(cellIndexFieldName + ",");
			}
		}
		avgQuerySb.append(cellIndexFieldName + " from " + inDs.getName());

		final String tmpDsName1 = dsf.getSourceManager().nameAndRegister(
				new SQLSourceDefinition(avgQuerySb.toString()));
		final DataSource tmpDs1 = dsf.getDataSource(tmpDsName1);
		tmpDs1.open();

		final double averages[] = new double[fieldCount];
		for (int fieldId = 0; fieldId < fieldCount; fieldId++) {
			if (cellIndexFieldId != fieldId) {
				averages[fieldId] = tmpDs1.getDouble(0, fieldId);
			}
		}
		tmpDs1.cancel();
		dsf.remove(tmpDsName1);

		// calculate one standard deviation value per field...
		// build the corresponding query
		final StringBuilder standardDeviationQuerySb = new StringBuilder();
		standardDeviationQuerySb.append("select ");
		for (int fieldId = 0; fieldId < fieldCount; fieldId++) {
			if (cellIndexFieldId != fieldId) {
				fieldNames[fieldId] = metadata.getFieldName(fieldId);
				standardDeviationQuerySb.append("StandardDeviation("
						+ fieldNames[fieldId] + "," + averages[fieldId] + "),");
			} else {
				avgQuerySb.append(cellIndexFieldName + ",");
			}
		}
		standardDeviationQuerySb.append(cellIndexFieldName + " from "
				+ inDs.getName());

		final String tmpDsName2 = dsf.getSourceManager().nameAndRegister(
				new SQLSourceDefinition(avgQuerySb.toString()));
		final DataSource tmpDs2 = dsf.getDataSource(tmpDsName2);
		tmpDs2.open();

		final double standardDeviation[] = new double[fieldCount];
		for (int fieldId = 0; fieldId < fieldCount; fieldId++) {
			if (cellIndexFieldId != fieldId) {
				standardDeviation[fieldId] = tmpDs2.getDouble(0, fieldId);
			}
		}
		tmpDs2.cancel();
		dsf.remove(tmpDsName2);

		// initialize the default list of clusters' centroids with average and
		// standard deviation values...
		for (int centroidIdx = 0; centroidIdx < fieldCount; centroidIdx++) {
			
		}
	}
}