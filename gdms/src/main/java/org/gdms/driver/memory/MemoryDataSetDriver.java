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
package org.gdms.driver.memory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.Schema;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.GDMSModelDriver;
import org.gdms.driver.EditableMemoryDriver;
import org.gdms.driver.DataSet;
import org.gdms.driver.FileDriver;
import org.gdms.driver.MemoryDriver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.SourceManager;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.progress.NullProgressMonitor;

public class MemoryDataSetDriver extends GDMSModelDriver implements
        EditableMemoryDriver {

        protected List<List<Value>> contents = new ArrayList<List<Value>>();
        private String[] columnsNames;
        private Type[] columnsTypes;
        private boolean commitable = true;
        public static final String DRIVER_NAME = "Generic driver";
        private Schema schema;
        private DataSet realSource;
        private static final Logger LOG = Logger.getLogger(MemoryDataSetDriver.class);

        /**
         * Create a new empty source of data in memory. The source will have as many
         * columns as specified in the 'columnsNames' parameter. The values in this
         * array are the names of the columns and the values in the 'columnsTypes'
         * array are constants in the org.gdms.data.values.Value interface and
         * specify the type of each column.
         *
         * @param columnsNames
         * @param columnsTypes  
         */
        public MemoryDataSetDriver(String[] columnsNames, Type[] columnsTypes) {
                LOG.trace("Constructor");
                this.columnsNames = columnsNames == null ? new String[0] : columnsNames;
                this.columnsTypes = columnsTypes == null ? new Type[0] : columnsTypes;
                this.schema = new DefaultSchema(DRIVER_NAME + this.hashCode());
                schema.addTable(DriverManager.DEFAULT_SINGLE_TABLE_NAME, new DefaultMetadata(this.columnsTypes, this.columnsNames));
        }

        public MemoryDataSetDriver() {
                this(new String[0], new Type[0]);
        }

        public MemoryDataSetDriver(final Metadata metadata) throws DriverException {
                LOG.trace("Constructor from metadata");
                this.columnsNames = new String[metadata.getFieldCount()];
                this.columnsTypes = new Type[metadata.getFieldCount()];
                for (int i = 0; i < columnsNames.length; i++) {
                        columnsNames[i] = metadata.getFieldName(i);
                        columnsTypes[i] = metadata.getFieldType(i);
                }
                this.schema = new DefaultSchema(DRIVER_NAME + this.hashCode());
                schema.addTable("main", new DefaultMetadata(columnsTypes, columnsNames));
        }

        public MemoryDataSetDriver(final DataSource dataSource)
                throws DriverException {
                this(dataSource.getMetadata());
                LOG.trace("For datasource " + dataSource.getName());
                dataSource.open();
                write(dataSource, new NullProgressMonitor());
                dataSource.close();
        }

        public MemoryDataSetDriver(final DataSet set, boolean noCopy) throws DriverException {
                Metadata metadata;
                if (set == null) {
                        metadata = new DefaultMetadata();
                } else {
                        metadata = set.getMetadata();
                }
                this.columnsNames = new String[metadata.getFieldCount()];
                this.columnsTypes = new Type[metadata.getFieldCount()];
                for (int i = 0; i < columnsNames.length; i++) {
                        columnsNames[i] = metadata.getFieldName(i);
                        columnsTypes[i] = metadata.getFieldType(i);
                }
                this.schema = new DefaultSchema(DRIVER_NAME + this.hashCode());
                schema.addTable("main", new DefaultMetadata(this.columnsTypes, this.columnsNames));

                if (set != null) {
                        if (noCopy) {
                                this.realSource = set;
                        } else {
                                write(set, new NullProgressMonitor());
                        }
                }
        }

        public MemoryDataSetDriver(final DataSet set) throws DriverException {
                this(set, false);
        }

        @Override
        public final boolean write(DataSet dataSource, ProgressMonitor pm)
                throws DriverException {
                LOG.trace("Writing");
                final long rowCount = dataSource.getRowCount();
                pm.startTask("Saving in memory", rowCount);
                final Metadata metadata = dataSource.getMetadata();
                final int fieldCount = metadata.getFieldCount();
                List<List<Value>> newContents = new ArrayList<List<Value>>();
                for (int i = 0; i < rowCount; i++) {
                        if (i >= 100 && i % 100 == 0) {
                                if (pm.isCancelled()) {
                                        break;
                                } else {
                                        pm.progressTo(i);
                                }
                        }
                        List<Value> rowArray = new ArrayList<Value>();
                        for (int j = 0; j < fieldCount; j++) {
                                rowArray.add(dataSource.getFieldValue(i, j));
                        }

                        newContents.add(rowArray);
                }
                pm.progressTo(rowCount);
                contents = newContents;
                columnsNames = new String[fieldCount];
                columnsTypes = new Type[fieldCount];
                for (int i = 0; i < columnsTypes.length; i++) {
                        columnsNames[i] = metadata.getFieldName(i);
                        columnsTypes[i] = metadata.getFieldType(i);
                }

                pm.endTask();
                return false;
        }

        @Override
        public void start() throws DriverException {
                if (realSource instanceof MemoryDriver) {
                        ((MemoryDriver) realSource).start();
                }
        }

        @Override
        public void stop() throws DriverException {
                if (realSource instanceof MemoryDriver) {
                        ((MemoryDriver) realSource).stop();
                }
        }

        @Override
        public void setDataSourceFactory(DataSourceFactory dsf) {
        }

        @Override
        public String getDriverId() {
                return DRIVER_NAME;
        }

        @Override
        public boolean isCommitable() {
                return commitable;
        }

        public void setCommitable(boolean commitable) {
                this.commitable = commitable;
        }

        public void addValues(Value... values) {
                ArrayList<Value> row = new ArrayList<Value>();
                row.addAll(Arrays.asList(values));
                contents.add(row);
        }

        @Override
        public int getSupportedType() {
                return SourceManager.MEMORY | SourceManager.VECTORIAL | SourceManager.RASTER;
        }

        @Override
        public int getType() {
                int type = SourceManager.MEMORY;
                try {
                        Metadata m = schema.getTableByName("main");
                        for (int i = 0; i < m.getFieldCount(); i++) {
                                Type fieldType = m.getFieldType(i);
                                if ((fieldType.getTypeCode() & Type.GEOMETRY) != 0) {
                                        type |= SourceManager.VECTORIAL;
                                } else if (fieldType.getTypeCode() == Type.RASTER) {
                                        type |= SourceManager.RASTER;
                                }
                        }
                } catch (DriverException ex) {
                }
                return type;
        }

        @Override
        public String validateMetadata(Metadata metadata) {
                return null;
        }

        @Override
        public String getTypeDescription() {
                return "Generic Object Driver";
        }

        @Override
        public String getTypeName() {
                return "GenericObjectDriver";
        }

        @Override
        public Schema getSchema() throws DriverException {
                return schema;
        }

        @Override
        public DataSet getTable(String name) {
                if (!name.equals("main")) {
                        return null;
                }
                if (realSource == null) {
                        return this;
                } else {
                        return realSource;
                }
        }

        @Override
        public Value getFieldValue(long rowIndex, int fieldId) throws DriverException {
                return contents.get((int) rowIndex).get(fieldId);
        }

        @Override
        public long getRowCount() throws DriverException {
                return contents.size();
        }

        @Override
        public Number[] getScope(int dimension) throws DriverException {
                return null;
        }

        @Override
        public Metadata getMetadata() throws DriverException {
                return schema.getTableByName("main");
        }

        @Override
        public Value[] getRow(long rowIndex) throws DriverException {
                Value[] ret = new Value[getMetadata().getFieldCount()];

                for (int i = 0; i < ret.length; i++) {
                        ret[i] = getFieldValue(rowIndex, i);
                }

                return ret;
        }

        @Override
        public Iterator<Integer> queryIndex(DataSourceFactory dsf, IndexQuery queryIndex) throws DriverException {
                return dsf.getIndexManager().iterateUsingIndexQuery(this, queryIndex);
        }
}
