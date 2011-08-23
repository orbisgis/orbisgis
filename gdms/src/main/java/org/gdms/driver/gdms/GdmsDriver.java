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
package org.gdms.driver.gdms;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.OpenCloseCounter;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.Schema;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileReadWriteDriver;
import org.gdms.driver.GDMSModelDriver;
import org.gdms.driver.DataSet;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.SourceManager;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.FileUtils;

public final class GdmsDriver extends GDMSModelDriver implements FileReadWriteDriver, DataSet {

        // version 1 : not supported anymore
        // version 2 : supported
        //      row indexes are after the metadata, at the beginning of the file
        // version 3 : supported
        //      row indexes are at the end of the file. The location is written
        //      in the header, after the metadata
        // version 4 : current version
        //      locations within the file are now written as Long and not Integer
        //      the file are thus longer, but can be bigger than 2 GB
        //      note that there is still a limit on the number of rows
        //      of Integer.MAX_VALUE - 1
        static final byte VERSION_NUMBER = 4;
        private GdmsReader reader;
        private OpenCloseCounter counter = new OpenCloseCounter("");
        private Schema schema;
        private File file;
        private static final Logger LOG = Logger.getLogger(GdmsDriver.class);

        @Override
        public void copy(File in, File out) throws IOException {
                FileUtils.copy(in, out);
        }

        @Override
        public void createSource(String path, Metadata metadata,
                DataSourceFactory dataSourceFactory) throws DriverException {
                LOG.trace("Creating gdms file");
                try {
                        GdmsWriter writer = new GdmsWriter(new File(path));
                        writer.writeMetadata(0, metadata);
                        writer.close();
                } catch (IOException e) {
                        throw new DriverException("Could not create source: ", e);
                }
        }

        @Override
        public void writeFile(File file, DataSet dataSource, ProgressMonitor pm)
                throws DriverException {
                LOG.trace("Writing gdms file");
                try {
                        GdmsWriter writer = new GdmsWriter(file);
                        writer.write(dataSource, pm);
                        writer.close();
                } catch (IOException e) {
                        throw new DriverException(e.getMessage(), e);
                }
        }

        @Override
        public void close() throws DriverException {
                synchronized (this) {
                        LOG.trace("Closing");
                        if (counter.stop()) {
                                try {
                                        reader.close();
                                } catch (IOException e) {
                                        throw new DriverException(e);
                                }
                        }
                }
        }

        @Override
        public void open() throws DriverException {
                synchronized (this) {
                        LOG.trace("Opening");
                        if (counter.start()) {
                                try {
                                        reader.open();
                                        reader.readMetadata();
                                } catch (IOException e) {
                                        throw new DriverException(e);
                                }
                        }
                }
        }

        @Override
        public int getType() {
                int type = SourceManager.FILE;
                if (schema != null) {
                        try {
                                Metadata m = schema.getTableByName(DriverManager.DEFAULT_SINGLE_TABLE_NAME);
                                if (m != null) {
                                        for (int i = 0; i < m.getFieldCount(); i++) {
                                                switch (m.getFieldType(i).getTypeCode()) {
                                                        case Type.GEOMETRY:
                                                                type |= SourceManager.VECTORIAL;
                                                                break;
                                                        case Type.RASTER:
                                                                type |= SourceManager.RASTER;
                                                                break;
                                                        default:
                                                }
                                        }
                                }
                        } catch (DriverException ex) {
                                LOG.warn("There was an error while accessing the file.", ex);
                        }
                }
                return type;
        }

        @Override
        public void setDataSourceFactory(DataSourceFactory dsf) {
        }

        @Override
        public String getDriverId() {
                return "GDMS driver";
        }

        @Override
        public boolean isCommitable() {
                return true;
        }

        @Override
        public String validateMetadata(Metadata metadata) {
                return null;
        }

        @Override
        public String[] getFileExtensions() {
                return new String[]{"gdms"};
        }

        @Override
        public String getTypeDescription() {
                return "GDMS native file";
        }

        @Override
        public String getTypeName() {
                return "GDMS";
        }

        @Override
        public Schema getSchema() throws DriverException {
                return schema;
        }

        @Override
        public DataSet getTable(String name) {
                return this;
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
                if (schema == null) {
                        return null;
                }
                return schema.getTableByName(DriverManager.DEFAULT_SINGLE_TABLE_NAME);
        }

        @Override
        public void setFile(File file) throws DriverException {
                try {
                        schema = new DefaultSchema("GDMS" + file.getAbsolutePath().hashCode());
                        reader = new GdmsReader(file);
                        this.file = file;
                        Metadata m = reader.getMetadata();
                        schema.addTable(DriverManager.DEFAULT_SINGLE_TABLE_NAME, m);
                } catch (IOException ex) {
                        throw new DriverException(ex);
                }
        }

        @Override
        public boolean isOpen() {
                boolean open = false;
                synchronized (this) {
                        open = reader != null;
                }
                return open;
        }

        /**
         * @return the file
         */
        public File getFile() {
                return file;
        }

        @Override
        public Value[] getRow(long rowIndex) throws DriverException {
                Value[] ret = new Value[getMetadata().getFieldCount()];

                for (int i = 0; i < ret.length; i++) {
                        ret[i] = getFieldValue(rowIndex, i);
                }

                return ret;
        }
}
