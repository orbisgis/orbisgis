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
package org.geoalgorithm.urbsat.kmeans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
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
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.SemanticException;
import org.orbisgis.progress.IProgressMonitor;

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
		return "Data clustering problem implementation";
	}

	public String getSqlOrder() {
		return "select KMeans(cellIndex, 7) from myTable;";
	}

	public String getName() {
		return "KMeans";
	}

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		this.dsf = dsf;
		cellIndexFieldName = values[0].toString();
		nbOfCluster = values[1].getAsInt();
		inDs = tables[0];

		try {
			inDs.open();
			rowCount = inDs.getRowCount();
			cellIndexFieldId = inDs.getFieldIndexByName(cellIndexFieldName);
			check();

			// K-Means initialization
			List<DataPoint> listOfCentroids;
			List<DataPoint> listOfNewCentroids = initialization();
			// for (DataPoint dp : listOfNewCentroids) {
			// dp.print();
			// }
			Cluster[] clusters;
			Cluster[] newClusters = new Cluster[listOfNewCentroids.size()];

			// K-Means iterations
			int count = 0;
			do {
				System.out.printf("Iteration number %d : %d centroids\n",
						count++, listOfNewCentroids.size());

				listOfCentroids = listOfNewCentroids;
				clusters = newClusters;

				// find the closest centroid for each DataPoint
				newClusters = new Cluster[listOfNewCentroids.size()];
				for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
					final DataPoint dataPoint = new DataPoint(inDs
							.getRow(rowIndex), cellIndexFieldId);
					final int clusterIndex = dataPoint
							.findClosestCentroidIndex(listOfCentroids);
					if (null == newClusters[clusterIndex]) {
						newClusters[clusterIndex] = new Cluster(dimension,
								inDs, cellIndexFieldId);
					}
					newClusters[clusterIndex].addDataPointIndex(rowIndex);
				}

				// calculate the new centroid of each cluster
				listOfNewCentroids = new ArrayList<DataPoint>(listOfCentroids
						.size());
				for (Cluster cluster : newClusters) {
					if (null != cluster) {
						listOfNewCentroids.add(cluster.getCentroid());
					}
				}
			} while (continueTheIterations(listOfCentroids, listOfNewCentroids,
					clusters, newClusters)
					&& (count < 15));

			// built the driver for the resulting datasource, register it and
			// populate it...
			final ObjectDriver driver = populateResultingDatasource(newClusters);
			inDs.close();
			return driver;
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
		} catch (ParseException e) {
			throw new ExecutionException(e);
		} catch (SemanticException e) {
			throw new ExecutionException(e);
		}
	}

	private ObjectDriver populateResultingDatasource(final Cluster[] newClusters)
			throws InvalidTypeException, DriverException {
		final ObjectMemoryDriver driver = new ObjectMemoryDriver(new String[] {
				cellIndexFieldName, "clusterNumber" }, new Type[] {
				metadata.getFieldType(cellIndexFieldId),
				TypeFactory.createType(Type.INT) });
		for (int clusterIndex = 0; clusterIndex < newClusters.length; clusterIndex++) {
			final Value clusterIndexValue = ValueFactory
					.createValue(clusterIndex);
			for (long rowIndex : newClusters[clusterIndex]
					.getListOfDataPointsIndex()) {
				final Value keyValue = inDs.getFieldValue(rowIndex,
						cellIndexFieldId);
				driver.addValues(new Value[] { keyValue, clusterIndexValue });
			}
		}
		return driver;
	}

	private void check() throws DriverException, ExecutionException {
		metadata = inDs.getMetadata();
		final int fieldCount = metadata.getFieldCount();
		for (int fieldId = 0; fieldId < fieldCount; fieldId++) {
			if (cellIndexFieldId != fieldId) {
				if (!TypeFactory.isNumerical(metadata.getFieldType(fieldId)
						.getTypeCode())) {
					throw new ExecutionException("Field '"
							+ metadata.getFieldName(fieldId)
							+ "' is not numeric !");
				}
			}
		}
	}

	private List<DataPoint> initialization() throws DriverException,
			DriverLoadException, DataSourceCreationException, ParseException,
			SemanticException {
		fieldCount = inDs.getFieldCount();
		// build the sql query
		final StringBuilder queryAvgSb = new StringBuilder();
		final StringBuilder queryStdDevSb = new StringBuilder();

		for (int fieldId = 0; fieldId < fieldCount; fieldId++) {
			if (cellIndexFieldId != fieldId) {
				queryAvgSb.append(
						(queryAvgSb.length() == 0) ? "Avg(" : ", Avg(").append(
						metadata.getFieldName(fieldId)).append(")");
				queryStdDevSb.append(
						(queryStdDevSb.length() == 0) ? "StandardDeviation("
								: ", StandardDeviation(").append(
						metadata.getFieldName(fieldId)).append(")");
			}
		}
		final String query = "select " + queryAvgSb.toString() + ", "
				+ queryStdDevSb.toString() + " from \"" + inDs.getName() + "\"";

		System.err.println(query);

		// execute the query (CollectiveAvg + CollectiveStandardDeviation
		// computations) and retrieve the averages and the standard deviations
		// ValueCollection and arrays of double
		final String tmpDsName = dsf.getSourceManager().nameAndRegister(query);
		final DataSource tmpDs = dsf.getDataSource(tmpDsName);
		tmpDs.open();
		final Value[] resultingValues = tmpDs.getRow(0);
		tmpDs.close();
		dsf.remove(tmpDsName);

		dimension = fieldCount - 1;
		final double[] averages = new double[dimension];
		final double[] standardDeviations = new double[dimension];
		for (int i = 0; i < dimension; i++) {
			averages[i] = resultingValues[i].getAsDouble();
			standardDeviations[i] = resultingValues[i + dimension]
					.getAsDouble();
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

	private boolean continueTheIterations(
			final List<DataPoint> listOfCentroids,
			final List<DataPoint> listOfNewCentroids, final Cluster[] clusters,
			final Cluster[] newClusters) {
		// compare the two lists of centroids
		if (listOfCentroids.size() != listOfNewCentroids.size()) {
			return true;
		}
		final DataPointComparator dataPointComparator = new DataPointComparator();
		Collections.sort(listOfCentroids, dataPointComparator);
		Collections.sort(listOfNewCentroids, dataPointComparator);
		for (int i = 0; i < listOfCentroids.size(); i++) {
			if (0 != dataPointComparator.compare(listOfCentroids.get(i),
					listOfNewCentroids.get(i))) {
				return true;
			}
		}
		// compare the two arrays of clusters
		if (clusters.length != newClusters.length) {
			return true;
		}
		for (int i = 0; i < clusters.length; i++) {
			if (clusters[i].isDifferentFrom(newClusters[i])) {
				return true;
			}
		}
		return false;
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return new DefaultMetadata(new Type[] {
				tables[0].getFieldType(cellIndexFieldId),
				TypeFactory.createType(Type.INT) }, new String[] {
				cellIndexFieldName, "clusterNumber" });
	}

	public TableDefinition[] geTablesDefinitions() {
		return new TableDefinition[] { TableDefinition.ANY };
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.NUMERIC,
				Argument.WHOLE_NUMBER) };
		// return new Arguments[] { new Arguments(new Argument(
		// Argument.TYPE_NUMERIC | Type.STRING | Type.TIME | Type.DATE
		// | Type.TIMESTAMP,
		// "1st argument must be a primary key!", new ArgumentValidator() {
		// public boolean isValid(Type type) {
		// return MetadataUtilities.isPrimaryKey(type);
		// }
		// }), Argument.WHOLE_NUMBER) };
	}
}