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
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
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
package org.gdms.driver.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.vividsolutions.jts.geom.Geometry;
import org.apache.log4j.Logger;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.FileUtils;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.Schema;
import org.gdms.data.types.DefaultTypeDefinition;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.data.values.ValueWriter;
import org.gdms.driver.AbstractDataSet;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileReadWriteDriver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.SourceManager;

/**
 * CSV file driver where the first row is used to define the field names
 * 
 */
public final class CSVDriver extends AbstractDataSet implements FileReadWriteDriver, ValueWriter {

        public static final String DRIVER_NAME = "csv";
        private static final char FIELD_SEPARATOR = ';';
        private boolean open;
        private Schema schema;
        private DefaultMetadata metadata;
        private ValueWriter valueWriter = ValueWriter.DEFAULTWRITER;
        private static final Logger LOG = Logger.getLogger(CSVDriver.class);
        private List<String[]> rows;
        private File file;

        @Override
        public String getDriverId() {
                return DRIVER_NAME;
        }

        private void loadRows() throws DriverException {
                if (rows == null) {
                        if (open) {
                                CSVReader reader = null;
                                try {
                                        reader = getReader();
                                        reader.readNext();
                                        rows = new ArrayList<String[]>();
                                        String[] next = reader.readNext();
                                        while (next != null) {
                                                rows.add(next);
                                                next = reader.readNext();
                                        }
                                } catch (IOException ex) {
                                        throw new DriverException(ex);
                                } finally {
                                        if (reader != null) {
                                                try {
                                                        reader.close();
                                                } catch (IOException ex) {
                                                        throw new DriverException(ex);
                                                }
                                        }
                                }
                        } else {
                                throw new DriverException("The driver must be open to call this method.");
                        }
                }
        }

        private CSVReader getReader() throws FileNotFoundException {
                return new CSVReader(new BufferedReader(new FileReader(file)), FIELD_SEPARATOR);
        }

        @Override
        public void open() throws DriverException {
                LOG.trace("Opening driver");
                CSVReader reader = null;
                String[] metadataContent;
                try {
                        reader = getReader();
                        metadataContent = reader.readNext();
                        reader.close();
                } catch (IOException e) {
                        throw new DriverException(e);
                } finally {
                        if (reader != null) {
                                try {
                                        reader.close();
                                } catch (IOException ex) {
                                        throw new DriverException(ex);
                                }
                        }
                }

                TypeDefinition csvTypeDef = new DefaultTypeDefinition("STRING", Type.STRING);

                metadata.clear();
                for (int i = 0; i < metadataContent.length; i++) {
                        metadata.addField(metadataContent[i], csvTypeDef.createType());
                }
                open = true;
        }

        @Override
        public void close() throws DriverException {
                open = false;
                rows = null;
        }

        private String[] getHeaderRow(final Metadata metaData)
                throws DriverException {
                String[] ret = new String[metaData.getFieldCount()];

                for (int i = 0; i < metaData.getFieldCount(); i++) {
                        ret[i] = metaData.getFieldName(i);
                }

                return ret;
        }

        @Override
        public void writeFile(final File file, final DataSet dataSource,
                ProgressMonitor pm) throws DriverException {
                LOG.trace("Writing File");
                CSVWriter writer = null;
                try {
                        final long rowCount = dataSource.getRowCount();
                        pm.startTask("Writing file", rowCount);
                        writer = new CSVWriter(new FileWriter(file), FIELD_SEPARATOR, CSVWriter.DEFAULT_QUOTE_CHARACTER, '\\');
                        Metadata dMetadata = dataSource.getMetadata();
                        String[] row = getHeaderRow(dMetadata);
                        writer.writeNext(row);
                        for (int i = 0; i < rowCount; i++) {
                                if (i >= 100 && i % 100 == 0) {
                                        if (pm.isCancelled()) {
                                                break;
                                        } else {
                                                pm.progressTo(i);
                                        }
                                }
                                row = new String[dMetadata.getFieldCount()];
                                for (int j = 0; j < dMetadata.getFieldCount(); j++) {
                                        if (dataSource.getFieldValue(i, j).isNull()) {
                                                row[j] = "null";
                                        } else {
                                                row[j] = dataSource.getFieldValue(i, j).toString();
                                        }
                                }
                                writer.writeNext(row);
                        }
                        pm.progressTo(rowCount);
                        writer.flush();
                } catch (IOException e) {
                        throw new DriverException(e);
                } finally {
                        if (writer != null) {
                                try {
                                        writer.close();
                                } catch (IOException ex) {
                                        throw new DriverException(ex);
                                }
                        }
                }
                pm.endTask();
        }

        @Override
        public void createSource(String path, Metadata metadata,
                DataSourceFactory dataSourceFactory) throws DriverException {
                LOG.trace("Creating CSVSource file");
                CSVWriter writer = null;
                try {
                        final File outFile = new File(path);
                        final File parentFile = outFile.getParentFile();
                        if (parentFile != null) {
                                parentFile.mkdirs();
                        }
                        outFile.createNewFile();

                        writer = new CSVWriter(new FileWriter(outFile), FIELD_SEPARATOR);
                        writer.writeNext(getHeaderRow(metadata));
                        writer.flush();

                } catch (IOException e) {
                        throw new DriverException(e);
                } finally {
                        if (writer != null) {
                                try {
                                        writer.close();
                                } catch (IOException ex) {
                                        throw new DriverException(ex);
                                }
                        }
                }
        }

        @Override
        public String getNullStatementString() {
                return valueWriter.getNullStatementString();
        }

        @Override
        public String getStatementString(boolean b) {
                return valueWriter.getStatementString(b);
        }

        @Override
        public String getStatementString(byte[] binary) {
                return valueWriter.getStatementString(binary);
        }

        @Override
        public String getStatementString(Date d) {
                return valueWriter.getStatementString(d);
        }

        @Override
        public String getStatementString(double d, int sqlType) {
                return valueWriter.getStatementString(d, sqlType);
        }

        @Override
        public String getStatementString(int i, int sqlType) {
                return valueWriter.getStatementString(i, sqlType);
        }

        @Override
        public String getStatementString(long i) {
                return valueWriter.getStatementString(i);
        }

        @Override
        public String getStatementString(CharSequence str, int sqlType) {
                return str.toString();
        }

        @Override
        public String getStatementString(Time t) {
                return t.toString();
        }

        @Override
        public String getStatementString(Timestamp ts) {
                return ts.toString();
        }

        @Override
        public void setDataSourceFactory(DataSourceFactory dsf) {
        }

        @Override
        public String getStatementString(Geometry g) {
                return valueWriter.getStatementString(g);
        }

        @Override
        public void copy(File in, File out) throws IOException {
                FileUtils.copy(in, out);
        }

        @Override
        public boolean isCommitable() {
                return true;
        }

        @Override
        public TypeDefinition[] getTypesDefinitions() {
                return new TypeDefinition[]{new DefaultTypeDefinition("STRING",
                                Type.STRING)};
        }

        @Override
        public int getSupportedType() {
                return SourceManager.FILE;
        }

        @Override
        public int getType() {
                return SourceManager.FILE;
        }

        @Override
        public String validateMetadata(Metadata m) throws DriverException {
                for (int i = 0; i < m.getFieldCount(); i++) {
                        int typeCode = m.getFieldType(i).getTypeCode();
                        if (typeCode != Type.STRING) {
                                return "Can only store strings on a csv. "
                                        + TypeFactory.getTypeName(typeCode) + " found";
                        }
                }

                return null;
        }

        @Override
        public String[] getFileExtensions() {
                return new String[]{"csv"};
        }

        @Override
        public String getTypeDescription() {
                return "Comma Separated Values";
        }

        @Override
        public String getTypeName() {
                return "CSV";
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
                return this;
        }

        @Override
        public void setFile(File file) {
                this.file = file;
                schema = new DefaultSchema("CSV" + file.getAbsolutePath().hashCode());
                metadata = new DefaultMetadata();
                schema.addTable(DriverManager.DEFAULT_SINGLE_TABLE_NAME, metadata);
        }

        @Override
        public Value getFieldValue(long rowIndex, int fieldId) throws DriverException {
                loadRows();
                final String[] fields = rows.get((int) (rowIndex));
                if (fieldId < fields.length) {
                        return createValue(fields[fieldId]);
                } else {
                        throw new IllegalArgumentException("fieldId: " + fieldId + ", fields: " + fields.length);
                }
        }

        private Value createValue(String val) {
                if (val.equals("null")) {
                        return ValueFactory.createNullValue();
                } else {
                        return ValueFactory.createValue(val);
                }
        }

        @Override
        public long getRowCount() throws DriverException {
                loadRows();
                return rows.size();
        }

        @Override
        public Number[] getScope(int dimension) throws DriverException {
                return null;
        }

        @Override
        public Metadata getMetadata() throws DriverException {
                return schema.getTableByName(DriverManager.DEFAULT_SINGLE_TABLE_NAME);
        }

        @Override
        public boolean isOpen() {
                return open;
        }

        @Override
        public Iterator<Value[]> iterator() {
                if (rows == null) {
                        try {
                                return new CSVIterator();
                        } catch (DriverException ex) {
                                throw new IllegalStateException(ex);
                        }
                } else {
                        return new Iterator<Value[]>() {

                                private Iterator<String[]> it = rows.iterator();

                                @Override
                                public boolean hasNext() {
                                        return it.hasNext();
                                }

                                @Override
                                public Value[] next() {
                                        String[] next = it.next();
                                        Value[] v = new Value[next.length];
                                        for (int i = 0; i < next.length; i++) {
                                                v[i] = createValue(next[i]);
                                        }
                                        return v;
                                }

                                @Override
                                public void remove() {
                                        throw new UnsupportedOperationException();
                                }
                        };
                }
        }

        private class CSVIterator implements Iterator<Value[]> {

                private String[] next;
                private boolean nextLoaded = false;
                private CSVReader reader;

                CSVIterator() throws DriverException {
                        try {
                                reader = getReader();
                                reader.readNext();
                        } catch (IOException ex) {
                                throw new DriverException(ex);
                        }
                }

                @Override
                public boolean hasNext() {
                        try {
                                next = reader.readNext();
                        } catch (IOException ex) {
                                LOG.error("Error accessing CSV file", ex);
                                return false;
                        }
                        nextLoaded = true;
                        return next == null;
                }

                @Override
                public Value[] next() {
                        if (!nextLoaded) {
                                try {
                                        next = reader.readNext();
                                } catch (IOException ex) {
                                        throw new IllegalStateException("Error accessing CSV file", ex);
                                }
                        } else {
                                nextLoaded = false;
                        }
                        Value[] v = new Value[next.length];
                        for (int i = 0; i < next.length; i++) {
                                v[i] = createValue(next[i]);
                        }
                        return v;
                }

                @Override
                public void remove() {
                        throw new UnsupportedOperationException();
                }
        }
}
