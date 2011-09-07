/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.gdms.driver;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.Schema;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.values.Value;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.driver.gdms.GdmsDriver;
import org.gdms.driver.gdms.GdmsReader;
import org.gdms.driver.gdms.GdmsWriter;
import org.gdms.source.SourceManager;

/**
 * This driver writes all the content added with the {@link #addValues(Value[])}
 * method to a file saving main memory. One all calls to
 * {@link #addValues(Value[])} are done, the method {@link #writingFinished()}
 * must be called.
 * 
 */
public class DiskBufferDriver extends AbstractDataSet implements MemoryDriver {

        private Schema schema;
        private GdmsWriter writer;
        private File file;
        private boolean firstRow = true;
        private GdmsReader reader;
        
        private static final Logger LOG = Logger.getLogger(DiskBufferDriver.class);

        /**
         * Creates a new DiskBufferManager.
         * @param dsf the DataSourceFactory
         * @param metadata this driver's content metadata
         * @throws DriverException
         */
        public DiskBufferDriver(DataSourceFactory dsf, Metadata metadata)
                throws DriverException {
                this(new File(dsf.getTempFile("gdms")), metadata);
        }

        /**
         * Creates a new DiskBufferManager.
         * @param file 
         * @param metadata this driver's content metadata
         * @throws DriverException
         */
        public DiskBufferDriver(File file, Metadata metadata)
                throws DriverException {
                this.schema = new DefaultSchema("buffer" + this.hashCode());
                schema.addTable("main", metadata);
                this.file = file;
                try {
                        writer = new GdmsWriter(file);
                } catch (IOException e) {
                        throw new DriverException("Cannot start writing process", e);
                }
        }

        @Override
        public void start() throws DriverException {
                try {
                        // Open file
                        reader = new GdmsReader(file);
                        reader.open();
                        reader.readMetadata();
                } catch (IOException e) {
                        throw new DriverException("Cannot open temporal file for reading",
                                e);
                }
                writer = null;
        }

        /**
         * This method must be called when all the contents have been added to the
         * file
         * 
         * @throws DriverException if the writing process cannot be finalized
         */
        public void writingFinished() throws DriverException {
                // Close writing
                try {
                        if (writer != null) {
                                writeMetadataOnce();
                                writer.writeRowIndexes();
                                writer.writeExtent();
                                writer.writeWritenRowCount();
                                writer.close();
                                writer = null;
                        }
                } catch (IOException e) {
                        throw new DriverException("Cannot finalize writing process", e);
                }
        }

        @Override
        public void stop() throws DriverException {
                try {
                        reader.close();
                } catch (IOException e) {
                        throw new DriverException("Cannot close gdms reader", e);
                }
        }
        
       @Override
        public int getSupportedType() {
                return SourceManager.FILE | SourceManager.VECTORIAL | SourceManager.RASTER;
        }

        @Override
        public int getType() {
                int type = SourceManager.FILE;
                try {
                        Metadata m = schema.getTableByName(DriverManager.DEFAULT_SINGLE_TABLE_NAME);
                        if (m != null) {
                                for (int i = 0; i < m.getFieldCount(); i++) {
                                        if((m.getFieldType(i).getTypeCode() & Type.GEOMETRY) != 0){
                                                type |= SourceManager.VECTORIAL;
                                        } else if((m.getFieldType(i).getTypeCode() & Type.RASTER) != 0){
                                                type |= SourceManager.RASTER;
                                        }
                                }
                        }
                } catch (DriverException ex) {
                        LOG.warn("There was an error while accessing the file.", ex);
                }
                return type;
        }

        @Override
        public void setDataSourceFactory(DataSourceFactory dsf) {
        }

        @Override
        public String getDriverId() {
                return new GdmsDriver().getDriverId();
        }

        /**
         * Add a new row to the file
         * 
         * @param row an array of Value objects.
         * @throws DriverException
         */
        public void addValues(Value... row) throws DriverException {
                writeMetadataOnce();
                writer.addValues(row);
        }
        
        public boolean isOpen() {
                return reader != null && reader.isOpen();
        }

        /**
         * Returns the temp file associated with this driver.
         * @return a file object.
         */
        public File getFile() {
                return file;
        }

        private void writeMetadataOnce() throws DriverException {
                if (firstRow) {
                        try {
                                writer.writeMetadata(0, schema.getTableByName("main"));
                                firstRow = false;
                        } catch (IOException e) {
                                throw new DriverException("Cannot write metadata", e);
                        }
                }
        }

        @Override
        public Schema getSchema() throws DriverException {
                return schema;
        }

        @Override
        public DataSet getTable(String name) {
                if (name.equals(DriverManager.DEFAULT_SINGLE_TABLE_NAME)) {
                        return this;
                } else {
                        return null;
                }
        }

        @Override
        public Value getFieldValue(long rowIndex, int fieldId) throws DriverException {
                return reader.getFieldValue(rowIndex, fieldId);
        }

        @Override
        public long getRowCount() throws DriverException {
                return reader.getRowCount();
        }

        @Override
        public Number[] getScope(int dimension) throws DriverException {
                return reader.getScope(dimension);
        }

        @Override
        public Metadata getMetadata() throws DriverException {
                return schema.getTableByName("main");
        }

        @Override
        public String getTypeName() {
                return "GDMS";
        }

        @Override
        public String getTypeDescription() {
                return "GDMS Memory to disk driver";
        }

        @Override
        public boolean isCommitable() {
                return true;
        }

        @Override
        public TypeDefinition[] getTypesDefinitions() {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String validateMetadata(Metadata metadata) throws DriverException {
                return null;
        }
}
