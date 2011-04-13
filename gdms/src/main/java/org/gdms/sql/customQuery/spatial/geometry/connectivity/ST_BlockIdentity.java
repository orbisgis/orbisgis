/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gdms.sql.customQuery.spatial.geometry.connectivity;

import com.vividsolutions.jts.geom.Geometry;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
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
 *
 * @author ebocher
 */
public class ST_BlockIdentity implements CustomQuery {

        private HashSet<Integer> idsDone;
        private ArrayDeque<Integer> geomToBeInspected;

        @Override
        public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables, Value[] values, IProgressMonitor pm) throws ExecutionException {

                DataSource ds = tables[0];
                //We need to read our source.
                SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);

                try {

                        sds.open();
                        String geomField;
                        if (values.length == 1) {
                                geomField = values[0].getAsString();
                        } else {

                                geomField = sds.getSpatialFieldName();

                        }

                        if (!dsf.getIndexManager().isIndexed(sds.getName(), geomField)) {
                                dsf.getIndexManager().buildIndex(sds.getName(), geomField, pm);
                        }


                        //Populate a hashset with all row ide
                        idsDone = new HashSet<Integer>();

                        DefaultMetadata met = new DefaultMetadata();
                        met.addField("row_id", TypeFactory.createType(Type.LONG));
                        met.addField("block_id", TypeFactory.createType(Type.LONG));

                        DiskBufferDriver diskBufferDriver = new DiskBufferDriver(dsf, met);


                        diskBufferDriver.addValues(new Value[]{
                                ValueFactory.createValue(0),
                                ValueFactory.createValue(0)});
                        HashSet<Integer> curr = new HashSet<Integer>();
                        curr.add(0);
                        executeConnectivityInspector(0, 0, curr, sds, diskBufferDriver);


                        diskBufferDriver.writingFinished();
                        sds.close();

                        return diskBufferDriver;


                } catch (DriverException ex) {
                        Logger.getLogger(ST_BlockIdentity.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchTableException ex) {
                        Logger.getLogger(ST_BlockIdentity.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IndexException ex) {
                        Logger.getLogger(ST_BlockIdentity.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
        }

        private void executeConnectivityInspector(int blockId, int rowId, HashSet<Integer> currentBlock, SpatialDataSourceDecorator sds, DiskBufferDriver diskBufferDriver) throws DriverException {

                Geometry geom = sds.getGeometry(rowId);
                DefaultSpatialIndexQuery query = new DefaultSpatialIndexQuery(geom.getEnvelopeInternal(), sds.getSpatialFieldName());
                Iterator<Integer> it = sds.queryIndex(query);
                while (it.hasNext()) {
                        Integer index = it.next();
                        if (rowId == index || currentBlock.contains(index)) {
                                continue;
                        }
                        Geometry intersectGeom = sds.getGeometry(index);
                        if (intersectGeom.touches(geom)) {
                                currentBlock.add(index);
                                diskBufferDriver.addValues(new Value[]{
                                ValueFactory.createValue(index),
                                ValueFactory.createValue(blockId)});
                                executeConnectivityInspector(blockId, index, currentBlock, sds, diskBufferDriver);
                        }
                }
        }

        @Override
        public String getName() {
                return "ST_BlockIdentity";
        }

        @Override
        public String getDescription() {
                return "Return all geometry blocks. A block is a set of connected geometry";

        }

        @Override
        public String getSqlOrder() {
                return "SELECT ST_BlockIdentity() from myTable";
        }

        @Override
        public Metadata getMetadata(Metadata[] tables) throws DriverException {
                return null;
        }

        @Override
        public TableDefinition[] getTablesDefinitions() {
                return new TableDefinition[]{TableDefinition.GEOMETRY};
        }

        @Override
        public Arguments[] getFunctionArguments() {
                return new Arguments[]{new Arguments(Argument.GEOMETRY),
                                new Arguments()};
        }
}
