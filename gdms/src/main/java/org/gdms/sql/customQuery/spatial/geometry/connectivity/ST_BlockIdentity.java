/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.sql.customQuery.spatial.geometry.connectivity;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.indexes.DefaultSpatialIndexQuery;
import org.gdms.data.indexes.IndexException;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DiskBufferDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.TableDefinition;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.orbisgis.progress.IProgressMonitor;

/**
 * Function to aggregate geometries by blocks based on a spatial predicate.
 *
 * if a comma-separated list of field names is given after the geometry field
 * (e.g. SELECT ST_BlockIdentity(the_geom, 'the_geom, myId, myDesc') FROM ... )
 * then these fields are returned in the output for the selected geometries. Unique IDs are best suited for this
 * use, but anything can be used. By default if nothing is specified, all field of the input table is kept.
 *
 * The algorithm uses an index to get the nearest geometries of a fixed geometry and then uses {@link DistanceOp}
 * to check if the (smallest) distance between the two is 0. If it is, the two are grouped and the same
 * algorithm is computed on the newly added geometries, until the group does not expand anymore.
 *
 * Note: BRUT-FORCE & NON-ROBUST algorithm.
 *
 * Using {@link DistanceOp} is MUCH faster than <code>geom1.touches(geom2)</code>, because it does not need
 * to compute the DE-9IM intersection matrix. However it is still a brut-force algorithm on the lines and points
 * of both geometry, that uses a non-robust CG algorithm in {@link  CGAlgorithms}. This could be improved
 * by writing a custom JTS TouchesOp dedicated to that effect.
 *
 * @author Antoine Gourlay, Erwan Bocher
 */
public class ST_BlockIdentity implements CustomQuery {

        private HashSet<Integer> idsToProcess;
        private SpatialDataSourceDecorator sds;
        private int[] fieldIds;

        @Override
        public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables, Value[] values, IProgressMonitor pm) throws
                ExecutionException {

                DataSource ds = tables[0];

                //We need to read our source.
                sds = new SpatialDataSourceDecorator(ds);

                try {
                        sds.open();

                        String[] fieldNames;
                        String geomField;
                        if (values.length == 2) {
                                geomField = values[0].getAsString();
                                fieldNames = values[1].getAsString().split(", *");
                        } else {
                                geomField = values[0].getAsString();
                                fieldNames = sds.getFieldNames();
                        }

                        fieldIds = new int[fieldNames.length];
                        for (int i = 0; i < fieldNames.length; i++) {
                                fieldIds[i] = sds.getFieldIndexByName(fieldNames[i]);
                        }

                        pm.startTask("Building indexes");
                        // build indexes
                        if (!dsf.getIndexManager().isIndexed(sds.getName(), geomField)) {
                                dsf.getIndexManager().buildIndex(sds.getName(), geomField, pm);
                        }
                        pm.endTask();


                        //Populate a hashset with all row ids
                        idsToProcess = new HashSet<Integer>();
                        for (int i = 0; i < sds.getRowCount(); i++) {
                                idsToProcess.add(i);
                        }

                        // results
                        DefaultMetadata met = new DefaultMetadata();
                        for (int i = 0; i < fieldIds.length; i++) {
                                met.addField(fieldNames[i], sds.getFieldType(fieldIds[i]));
                        }
                        met.addField("block_id", TypeFactory.createType(Type.LONG));

                        DiskBufferDriver diskBufferDriver = new DiskBufferDriver(dsf, met);


                        int blockId = 0;
                        while (!idsToProcess.isEmpty()) {

                                // starts the block
                                int start = idsToProcess.iterator().next();
                                HashSet<Integer> block = new HashSet<Integer>();
                                block.add(start);

                                // aggregates the block
                                aggregateNeighbours(start, block);

                                // writes the block
                                Iterator<Integer> it = block.iterator();
                                while (it.hasNext()) {
                                        final Integer next = it.next();
                                        Value[] res = new Value[fieldIds.length + 1];
                                        for (int i = 0; i < fieldIds.length; i++) {
                                                res[i] = sds.getFieldValue(next, fieldIds[i]);
                                        }
                                        res[fieldIds.length] = ValueFactory.createValue(blockId);
                                        diskBufferDriver.addValues(res);
                                }

                                // mark all those geometries as processed
                                idsToProcess.removeAll(block);

                                blockId++;
                        }
                        pm.endTask();

                        diskBufferDriver.writingFinished();
                        pm.endTask();
                        sds.close();

                        return diskBufferDriver;


                } catch (DriverException ex) {
                        throw new ExecutionException(ex);
                } catch (NoSuchTableException ex) {
                        throw new ExecutionException(ex);
                } catch (IndexException ex) {
                        throw new ExecutionException(ex);
                }
        }

        private void aggregateNeighbours(int id, Set<Integer> agg) throws DriverException {
                int size = agg.size();

                Set<Integer> re = relativesOf(id, agg);
                agg.addAll(re);
                int nSize = agg.size();


                if (nSize == size) {
                        // blockSize has not changed, there is no more blocks to add
                        return;
                } else {
                        Iterator<Integer> it = re.iterator();
                        while (it.hasNext()) {
                                aggregateNeighbours(it.next(), agg);
                        }
                }
        }

        private Set<Integer> relativesOf(int id, Set<Integer> excluded) throws DriverException {
                Geometry geom = sds.getGeometry(id);

                // query index
                DefaultSpatialIndexQuery query = new DefaultSpatialIndexQuery(geom.getEnvelopeInternal(), sds.
                        getSpatialFieldName());
                Iterator<Integer> s = sds.queryIndex(query);

                HashSet<Integer> h = new HashSet<Integer>();
                while (s.hasNext()) {
                        int i = s.next();

                        // i != id to prevent adding itself
                        // !excluded.contains(i) to filter already added geometries
                        //      (this is O(1) while the next test is far from it...)
                        // test if both geoms are at 0 distance, i.e. touches
                        if (i != id && !excluded.contains(i) && DistanceOp.isWithinDistance(geom, sds.getGeometry(i), 0)) {
                                h.add(i);
                        }
                }
                return h;
        }

        @Override
        public String getName() {
                return "ST_BlockIdentity";
        }

        @Override
        public String getDescription() {
                return "Return all geometry blocks. A block is a set of connected geometry.";

        }

        @Override
        public String getSqlOrder() {
                return "SELECT ST_BlockIdentity(the_geom [, 'the_geom, titi, toto' ]) from myTable";
        }

        @Override
        public Metadata getMetadata(Metadata[] tables) throws DriverException {
                // hack to be able to inject into the map without a CREATE TABLE
                return new DefaultMetadata(new Type[] { TypeFactory.createType(Type.GEOMETRY) }, new String[] { "the_geom" });
        }

        @Override
        public TableDefinition[] getTablesDefinitions() {
                return new TableDefinition[]{TableDefinition.GEOMETRY};
        }

        @Override
        public Arguments[] getFunctionArguments() {
                return new Arguments[]{new Arguments(Argument.GEOMETRY, Argument.STRING),
                                new Arguments(Argument.GEOMETRY)};
        }
}
