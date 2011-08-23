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
package org.gdms.driver.csv;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.Schema;
import org.gdms.data.types.DefaultTypeDefinition;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.data.values.ValueWriter;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileReadWriteDriver;
import org.gdms.driver.DataSet;
import org.gdms.source.SourceManager;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.FileUtils;

import com.vividsolutions.jts.geom.Geometry;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.DefaultSchema;

/**
 * CSV file driver where the first row is used to define the field names
 * 
 */
public final class CSVDriver implements FileReadWriteDriver, ValueWriter, DataSet {

        public static final String DRIVER_NAME = "csv";
        private static final char FIELD_SEPARATOR = ';';
        private CSVReader reader;
        private Schema schema;
        private DefaultMetadata metadata;
        private ValueWriter valueWriter = ValueWriter.internalValueWriter;
        private static final Logger LOG = Logger.getLogger(CSVDriver.class);
        private List<String[]> rows = new ArrayList<String[]>();
        private File file;

        @Override
        public String getDriverId() {
                return DRIVER_NAME;
        }

        private void loadRows() throws IOException {
                if (reader != null) {
                        String[] next = reader.readNext();
                        while (next != null) {
                                rows.add(next);
                                next = reader.readNext();
                        }
                }
        }

        @Override
        public void open() throws DriverException {
                LOG.trace("Opening driver");
                try {
                        reader = new CSVReader(new BufferedReader(new FileReader(file)), FIELD_SEPARATOR);

                        String[] metadataContent = reader.readNext();

                        TypeDefinition csvTypeDef = new DefaultTypeDefinition("STRING", Type.STRING);

                        metadata.clear();
                        for (int i = 0; i < metadataContent.length; i++) {
                                metadata.addField(metadataContent[i], csvTypeDef.createType());
                        }

                        // finished building schema

                        loadRows();

                        reader.close();
                } catch (IOException e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public void close() throws DriverException {
                try {
                        reader.close();
                } catch (IOException ex) {
                        throw new DriverException(ex);
                }
                reader = null;
                rows.clear();
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
                        writer = new CSVWriter(new FileWriter(file), FIELD_SEPARATOR);
                        Metadata metadata = dataSource.getMetadata();
                        String[] row = getHeaderRow(metadata);
                        writer.writeNext(row);
                        for (int i = 0; i < rowCount; i++) {
                                if (i >= 100 && i % 100 == 0) {
                                        if (pm.isCancelled()) {
                                                break;
                                        } else {
                                                pm.progressTo(i);
                                        }
                                }
                                row = new String[metadata.getFieldCount()];
                                for (int j = 0; j < metadata.getFieldCount(); j++) {
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
        public String getStatementString(String str, int sqlType) {
                return str;
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
                if (!name.equals("main")) {
                        return null;
                }
                return this;
        }

        @Override
        public void setFile(File file) {
                this.file = file;
                schema = new DefaultSchema("CSV" + file.getAbsolutePath().hashCode());
                metadata = new DefaultMetadata();
                schema.addTable("main", metadata);
        }

        @Override
        public Value getFieldValue(long rowIndex, int fieldId) throws DriverException {
                final String[] fields = rows.get((int) (rowIndex));
                if (fieldId < fields.length) {
                        if (fields[fieldId].equals("null")) {
                                return ValueFactory.createNullValue();
                        } else {
                                return ValueFactory.createValue(fields[fieldId]);
                        }
                } else {
                        throw new IllegalArgumentException("fieldId: " + fieldId + ", fields: " + fields.length);
                }
        }

        @Override
        public long getRowCount() throws DriverException {
                return rows.size();
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
        public boolean isOpen() {
                return reader != null;
        }
}
