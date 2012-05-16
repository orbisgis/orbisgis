/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.sql.function.cluster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.orbisgis.progress.ProgressMonitor;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.MemoryDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.driver.memory.MemoryDataSetDriver;
import org.gdms.sql.engine.ParseException;
import org.gdms.sql.engine.SemanticException;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.table.AbstractTableFunction;
import org.gdms.sql.function.table.TableArgument;
import org.gdms.sql.function.table.TableDefinition;
import org.gdms.sql.function.table.TableFunctionSignature;

public class ST_KMeans extends AbstractTableFunction {

        private DataSourceFactory dsf;
        private DataSet inDs;
        private String cellIndexFieldName;
        private int cellIndexFieldId;
        private Metadata metadata;
        private int dimension;
        private static final Logger LOG = Logger.getLogger(ST_KMeans.class);

        @Override
        public String getDescription() {
                return "Data clustering problem implementation";
        }

        @Override
        public String getSqlOrder() {
                return "select * from ST_KMeans(table, cellIndex, 7);";
        }

        @Override
        public String getName() {
                return "ST_KMeans";
        }

        @Override
        public DataSet evaluate(DataSourceFactory dsf, DataSet[] tables,
                Value[] values, ProgressMonitor pm) throws FunctionException {
                LOG.trace("Evaluating");
                this.dsf = dsf;
                cellIndexFieldName = values[0].toString();
                inDs = tables[0];

                try {
                        long rowCount = inDs.getRowCount();
                        cellIndexFieldId = inDs.getMetadata().getFieldIndex(cellIndexFieldName);
                        check();

                        // K-Means initialization
                        List<DataPoint> listOfCentroids;
                        List<DataPoint> listOfNewCentroids = initialization();
                        // for (DataPoint dp : listOfNewCentroids) {
                        // dp.print();
                        // }
                        Cluster[] clusters;
                        Cluster[] newClusters = new Cluster[listOfNewCentroids.size()];

                        final int ifieldCount = inDs.getMetadata().getFieldCount();

                        // K-Means iterations
                        int count = 0;
                        do {
                                listOfCentroids = listOfNewCentroids;
                                clusters = newClusters;

                                // find the closest centroid for each DataPoint
                                newClusters = new Cluster[listOfNewCentroids.size()];
                                for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                                        Value[] v = new Value[ifieldCount];
                                        for (int i = 0; i < v.length; i++) {
                                                v[i] = inDs.getFieldValue(rowIndex, i);
                                        }
                                        final DataPoint dataPoint = new DataPoint(v, cellIndexFieldId);
                                        final int clusterIndex = dataPoint.findClosestCentroidIndex(listOfCentroids);
                                        if (null == newClusters[clusterIndex]) {
                                                newClusters[clusterIndex] = new Cluster(dimension,
                                                        inDs, cellIndexFieldId);
                                        }
                                        newClusters[clusterIndex].addDataPointIndex(rowIndex);
                                }

                                // calculate the new centroid of each cluster
                                listOfNewCentroids = new ArrayList<DataPoint>(listOfCentroids.size());
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
                        return populateResultingDatasource(newClusters);
                } catch (DriverException e) {
                        throw new FunctionException(e);
                } catch (InvalidTypeException e) {
                        throw new FunctionException(e);
                } catch (DriverLoadException e) {
                        throw new FunctionException(e);
                } catch (DataSourceCreationException e) {
                        throw new FunctionException(e);
                } catch (ParseException e) {
                        throw new FunctionException(e);
                } catch (NoSuchTableException e) {
                        throw new FunctionException(e);
                } catch (SemanticException e) {
                        throw new FunctionException(e);
                }
        }

        private DataSet populateResultingDatasource(final Cluster[] newClusters)
                throws DriverException {
                LOG.trace("Populating resulting datasource");
                final MemoryDataSetDriver driver = new MemoryDataSetDriver(
                        new String[]{cellIndexFieldName, "clusterNumber"},
                        new Type[]{metadata.getFieldType(cellIndexFieldId),
                                TypeFactory.createType(Type.INT)});
                for (int clusterIndex = 0; clusterIndex < newClusters.length; clusterIndex++) {
                        final Value clusterIndexValue = ValueFactory.createValue(clusterIndex);
                        for (long rowIndex : newClusters[clusterIndex].getListOfDataPointsIndex()) {
                                final Value keyValue = inDs.getFieldValue(rowIndex,
                                        cellIndexFieldId);
                                driver.addValues(new Value[]{keyValue, clusterIndexValue});
                        }
                }
                return driver;
        }

        private void check() throws DriverException, FunctionException {
                LOG.trace("Checking");
                metadata = inDs.getMetadata();
                final int fieldCount0 = metadata.getFieldCount();
                for (int fieldId = 0; fieldId < fieldCount0; fieldId++) {
                        if (cellIndexFieldId != fieldId && !TypeFactory.isNumerical(metadata.getFieldType(fieldId).getTypeCode())) {
                                throw new FunctionException("Field '"
                                        + metadata.getFieldName(fieldId)
                                        + "' is not numeric !");
                        }

                }
        }

        private List<DataPoint> initialization() throws DriverException,
                DataSourceCreationException, ParseException,
                NoSuchTableException {
                LOG.trace("Initializing");
                int fieldCount = inDs.getMetadata().getFieldCount();
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

                MemoryDriver d = new MemoryDataSetDriver(inDs, true);
                String name = dsf.getSourceManager().nameAndRegister(d, DriverManager.DEFAULT_SINGLE_TABLE_NAME);

                final String query = "select " + queryAvgSb.toString() + ", "
                        + queryStdDevSb.toString() + " from \"" + name + "\"";

                // execute the query (CollectiveAvg + CollectiveStandardDeviation
                // computations) and retrieve the averages and the standard deviations
                // ValueCollection and arrays of double
                final String tmpDsName = dsf.getSourceManager().nameAndRegister(query);
                final DataSource tmpDs = dsf.getDataSource(tmpDsName);
                tmpDs.open();
                final Value[] resultingValues = tmpDs.getRow(0);
                tmpDs.close();
                dsf.remove(tmpDsName);
                dsf.remove(name);

                dimension = fieldCount - 1;
                final double[] averages = new double[dimension];
                final double[] standardDeviations = new double[dimension];
                for (int i = 0; i < dimension; i++) {
                        averages[i] = resultingValues[i].getAsDouble();
                        standardDeviations[i] = resultingValues[i + dimension].getAsDouble();
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

        @Override
        public Metadata getMetadata(Metadata[] tables) throws DriverException {
                return new DefaultMetadata(new Type[]{
                                tables[0].getFieldType(cellIndexFieldId),
                                TypeFactory.createType(Type.INT)}, new String[]{
                                cellIndexFieldName, "clusterNumber"});
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new TableFunctionSignature(TableDefinition.ANY,
                                new TableArgument(TableDefinition.ANY),
                                ScalarArgument.STRING,
                                ScalarArgument.INT)
                        };
        }
}
