package org.urbsat.kmeans;

import java.util.ArrayList;
import java.util.List;

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
import org.gdms.data.values.DoubleValue;
import org.gdms.data.values.IntValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
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
	private int dimension;
	private long rowCount;
	private int fieldCount;

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
			List<DataPoint> listOfCentroids = initialization();
			for (DataPoint dp : listOfCentroids) {
				dp.print();
			}

			// K-Means iterations
			do {
				final Cluster[] clusters = new Cluster[listOfCentroids.size()];

				// find the closest centroid for each DataPoint
				rowCount = inDs.getRowCount();
				for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
					final DataPoint dataPoint = new DataPoint(inDs
							.getRow(rowIndex), cellIndexFieldId);
					final int clusterIndex = dataPoint
							.findClosestCentroidIndex(listOfCentroids);

					if (null == clusters[clusterIndex]) {
						clusters[clusterIndex] = new Cluster(dimension);
					}
					clusters[clusterIndex].addPoint(rowIndex);
				}
			} while (false);

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

	private List<DataPoint> initialization() throws DriverException,
			DriverLoadException, NoSuchTableException,
			DataSourceCreationException {
		fieldCount = inDs.getFieldCount();
		// build the sql query
		final StringBuilder querySb = new StringBuilder();
		for (int fieldId = 0; fieldId < fieldCount; fieldId++) {
			if (cellIndexFieldId != fieldId) {
				querySb.append(((querySb.length() == 0) ? "" : ", ")
						+ metadata.getFieldName(fieldId));
			}
		}
		final String tmpQuery = querySb.toString();
		final String query = "select CollectiveAvg(" + tmpQuery
				+ "),CollectiveStandardDeviation(" + tmpQuery + ") from "
				+ inDs.getName();

		// execute the query (CollectiveAvg + CollectiveStandardDeviation
		// computations) and retrieve the averages and the standard deviations
		// ValueCollection and arrays of double
		final String tmpDsName = dsf.getSourceManager().nameAndRegister(
				new SQLSourceDefinition(query));
		final DataSource tmpDs = dsf.getDataSource(tmpDsName);
		tmpDs.open();
		final Value[] averagesValues = ((ValueCollection) tmpDs.getFieldValue(
				0, 0)).getValues();
		final Value[] standardDeviationsValues = ((ValueCollection) tmpDs
				.getFieldValue(0, 1)).getValues();
		tmpDs.cancel();
		dsf.remove(tmpDsName);

		dimension = fieldCount - 1;
		final double[] averages = new double[dimension];
		final double[] standardDeviations = new double[dimension];
		for (int i = 0; i < dimension; i++) {
			averages[i] = ((DoubleValue) averagesValues[i]).getValue();
			standardDeviations[i] = ((DoubleValue) standardDeviationsValues[i])
					.getValue();
		}

		// initialize the default list of clusters' centroids with average and
		// standard deviation values...
		final List<DataPoint> centroids = new ArrayList<DataPoint>();
		for (int centroidIdx = 0; centroidIdx < dimension; centroidIdx++) {
			final double[] tmp1 = new double[dimension];
			final double[] tmp2 = new double[dimension];
			final double[] tmp3 = new double[dimension];
			final double[] tmp4 = new double[dimension];
			final double[] tmp5 = new double[dimension];
			final double[] tmp6 = new double[dimension];
			final double[] tmp7 = new double[dimension];
			final double[] tmp8 = new double[dimension];
			for (int i = 0; i < dimension; i++) {
				tmp1[i] = averages[i] - standardDeviations[i];
				tmp2[i] = averages[i] + standardDeviations[i];
				tmp5[i] = averages[i] - 0.5 * standardDeviations[i];
				tmp6[i] = averages[i] + 0.5 * standardDeviations[i];
				if (i == centroidIdx) {
					tmp3[i] = tmp1[i];
					tmp4[i] = tmp2[i];
					tmp7[i] = tmp5[i];
					tmp8[i] = tmp6[i];
				} else {
					tmp3[i] = averages[i];
					tmp4[i] = averages[i];
					tmp7[i] = averages[i];
					tmp8[i] = averages[i];
				}
			}
			centroids.add(new DataPoint(tmp1));
			centroids.add(new DataPoint(tmp2));
			centroids.add(new DataPoint(tmp3));
			centroids.add(new DataPoint(tmp4));
			centroids.add(new DataPoint(tmp5));
			centroids.add(new DataPoint(tmp6));
			centroids.add(new DataPoint(tmp7));
			centroids.add(new DataPoint(tmp8));
		}
		centroids.add(new DataPoint(averages));
		return centroids;
	}
}