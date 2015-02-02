/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
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
package org.gdms.driver.memory;

import java.util.*;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import org.apache.log4j.Logger;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.commons.progress.ProgressMonitor;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.Schema;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.*;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.SourceManager;

/**
 * {@code MemoryDataSetDriver} gives access to a DataSet that is kept in memory.
 * It is editable, because we can add rows to the {@code DataSet}. However, as a
 * {@code DataSet}, it does not provide any method to remove rows.
 * @author alexis
 */
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
        private Envelope envelope = null;
        private List<Integer> geomIndices = null;

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
                schema.addTable(DriverManager.DEFAULT_SINGLE_TABLE_NAME, new DefaultMetadata(columnsTypes, columnsNames));
        }

        public MemoryDataSetDriver(final DataSource dataSource)
                throws DriverException {
                this(dataSource.getMetadata());
                LOG.trace("For datasource " + dataSource.getName());
                dataSource.open();
                write(dataSource, new NullProgressMonitor());
                dataSource.close();
                this.schema = new DefaultSchema(DRIVER_NAME + this.hashCode());
                schema.addTable(DriverManager.DEFAULT_SINGLE_TABLE_NAME, new DefaultMetadata(columnsTypes, columnsNames));
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
                schema.addTable(DriverManager.DEFAULT_SINGLE_TABLE_NAME, new DefaultMetadata(this.columnsTypes, this.columnsNames));

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
                if(geomIndices == null){
                        computeGeomIndices();
                }
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
                        expandTo(rowArray);

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
        public void open() throws DriverException {
                if (realSource instanceof MemoryDriver) {
                        ((MemoryDriver) realSource).open();
                }
        }

        @Override
        public void close() throws DriverException {
                if (realSource instanceof MemoryDriver) {
                        ((MemoryDriver) realSource).close();
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

        public void addValues(Value... values) throws DriverException {
                if(geomIndices == null){
                        computeGeomIndices();
                }
                ArrayList<Value> row = new ArrayList<Value>();
                row.addAll(Arrays.asList(values));
                expandTo(row);
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
                        Metadata m = schema.getTableByName(DriverManager.DEFAULT_SINGLE_TABLE_NAME);
                        for (int i = 0; i < m.getFieldCount(); i++) {
                                Type fieldType = m.getFieldType(i);
                                if ((fieldType.getTypeCode() & Type.GEOMETRY) != 0) {
                                        type |= SourceManager.VECTORIAL;
                                } else if (fieldType.getTypeCode() == Type.RASTER) {
                                        type |= SourceManager.RASTER;
                                }
                        }
                } catch (DriverException ex) {
                        LOG.warn("Failed to get the Type of a Memory driver", ex);
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
                if (!name.equals(DriverManager.DEFAULT_SINGLE_TABLE_NAME)) {
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
                if(geomIndices == null){
                        computeGeomIndices();
                }
                if(envelope == null){
                        envelope = new Envelope();
                }
                if(realSource != null){
                        return realSource.getScope(dimension);
                } else {
                        switch(dimension){
                                case 0:
                                        return new Number[]{envelope.getMinX(), envelope.getMaxX()};
                                case 1:
                                        return new Number[]{envelope.getMinY(), envelope.getMaxY()};
                                default:
                                        throw new DriverException("Can only work in two dimensions here.");
                        }
                }
        }

        @Override
        public Metadata getMetadata() throws DriverException {
                return schema.getTableByName(DriverManager.DEFAULT_SINGLE_TABLE_NAME);
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

        /**
         * Go through the rows, and compute the envelope of this DataSet.
         * @return
         */
        private void expandTo(List<Value> row) throws DriverException{
                if(envelope == null){
                        envelope = new Envelope();
                }
                //We can walk through the rows now
                for(Integer i : geomIndices){
                        Geometry geom = row.get(i).getAsGeometry();
                        //A null value is a geometry.
                        if(geom !=null){
                                envelope.expandToInclude(geom.getEnvelopeInternal());
                        }
                }
        }

        private void computeGeomIndices() throws DriverException{
                Metadata met = getMetadata();
                final int fieldCount = met.getFieldCount();
                List<Integer> geometries = new LinkedList<Integer>();
                for(int i=0; i<fieldCount; i++){
                        if((met.getFieldType(i).getTypeCode() & Type.GEOMETRY)!=0){
                                geometries.add(i);
                        }
                }
                geomIndices = geometries;
        }
}
