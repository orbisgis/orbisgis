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
package org.gdms.driver.dbf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.FileUtils;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.WarningListener;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.Schema;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.DefaultTypeDefinition;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.LengthConstraint;
import org.gdms.data.types.PrecisionConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.AbstractDataSet;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileReadWriteDriver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.SourceManager;

public final class DBFDriver extends AbstractDataSet implements FileReadWriteDriver {

        public static final String STRING = "String";
        public static final String DOUBLE = "Double";
        public static final String INTEGER = "Integer";
        public static final String LONG = "Long";
        public static final String DATE = "Date";
        public static final String BOOLEAN = "Boolean";
        public static final String LENGTH = "Length";
        public static final String PRECISION = "Precision";
        public static final String DRIVER_NAME = "Dbase driver";
        private DbaseFileReader dbaseReader;
        private DataSourceFactory dataSourceFactory;
        private Schema schema;
        private DefaultMetadata metadata;
        private File file;
        private static final Logger LOG = Logger.getLogger(DBFDriver.class);

        @Override
        public void setDataSourceFactory(DataSourceFactory dsf) {
                this.dataSourceFactory = dsf;
        }

        @Override
        public void createSource(String path, Metadata metadata,
                DataSourceFactory dataSourceFactory) throws DriverException {
                LOG.trace("Creating source file");
                try {
                        FileOutputStream fos = new FileOutputStream(new File(path));
                        DbaseFileHeader header = getHeader(metadata, 0, dataSourceFactory.getWarningListener());
                        DbaseFileWriter writer = new DbaseFileWriter(header, fos.getChannel());
                        writer.close();
                } catch (IOException e) {
                        throw new DriverException(e);
                } catch (DbaseFileException e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public void writeFile(File file, DataSet dataSource, ProgressMonitor pm)
                throws DriverException {
                writeFile(file, new DefaultRowProvider(dataSource), dataSourceFactory.getWarningListener(), pm);
        }

        public void writeFile(File file, RowProvider dataSource,
                WarningListener warningListener, ProgressMonitor pm)
                throws DriverException {
                LOG.trace("Writing source file");
                try {
                        FileOutputStream fos = new FileOutputStream(file);
                        DbaseFileHeader header = getHeader(dataSource.getMetadata(),
                                (int) dataSource.getRowCount(), dataSourceFactory.getWarningListener());
                        DbaseFileWriter writer = new DbaseFileWriter(header, fos.getChannel());
                        final int numRecords = header.getNumRecords();
                        pm.startTask("Writing file", numRecords);
                        for (int i = 0; i < numRecords; i++) {
                                if (i >= 100 && i % 100 == 0) {
                                        if (pm.isCancelled()) {
                                                break;
                                        } else {
                                                pm.progressTo(i);
                                        }
                                }
                                writer.write(dataSource.getRow(i));
                        }
                        pm.progressTo(numRecords);
                        writer.close();
                } catch (IOException e) {
                        throw new DriverException(e);
                } catch (DbaseFileException e) {
                        throw new DriverException(e);
                }
                pm.endTask();
        }

        private DbaseFileHeader getHeader(Metadata m, int rowCount,
                WarningListener warningListener) throws DriverException,
                DbaseFileException {
                DbaseFileHeader header = new DbaseFileHeader();
                for (int i = 0; i < m.getFieldCount(); i++) {
                        String fieldName = m.getFieldName(i);
                        Type gdmsType = m.getFieldType(i);
                        DBFType type = getDBFType(gdmsType);
                        header.addColumn(fieldName, type.type, type.fieldLength,
                                type.decimalCount, warningListener);
                }
                header.setNumRecords(rowCount);

                return header;
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
        public Value getFieldValue(long rowIndex, int fieldId)
                throws DriverException {
                try {
                        return dbaseReader.getFieldValue((int) rowIndex, fieldId,
                                dataSourceFactory.getWarningListener());
                } catch (IOException e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public long getRowCount() throws DriverException {
                return dbaseReader.getHeader().getNumRecords();
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
        public void setFile(File file) {
                this.file = file;

                // building schema and metadata
                schema = new DefaultSchema("DBF" + file.getAbsolutePath().hashCode());
                metadata = new DefaultMetadata();
                schema.addTable(DriverManager.DEFAULT_SINGLE_TABLE_NAME, metadata);
        }

        @Override
        public boolean isOpen() {
                return dbaseReader != null;
        }

        private static class DBFType {

                char type;
                int fieldLength;
                int decimalCount;

                DBFType(char type, int fieldLength, int decimalCount) {
                        super();
                        this.type = type;
                        this.fieldLength = fieldLength;
                        this.decimalCount = decimalCount;
                }
        }

        private DBFType getDBFType(Type fieldType) throws DriverException {
                Constraint lengthConstraint = fieldType.getConstraint(Constraint.LENGTH);
                int length = Integer.MAX_VALUE;
                if (lengthConstraint != null) {
                        length = Integer.parseInt(lengthConstraint.getConstraintValue());
                }

                Constraint decimalCountConstraint = fieldType.getConstraint(Constraint.PRECISION);
                int decimalCount = Integer.MAX_VALUE;
                if (decimalCountConstraint != null) {
                        decimalCount = Integer.parseInt(decimalCountConstraint.getConstraintValue());
                }

                switch (fieldType.getTypeCode()) {
                        case Type.BOOLEAN:
                                return new DBFType('l', 1, 0);
                        case Type.BYTE:
                                return new DBFType('n', Math.min(3, length), 0);
                        case Type.DATE:
                                return new DBFType('d', 8, 0);
                        case Type.DOUBLE:
                        case Type.FLOAT:
                                return new DBFType('f', Math.min(20, length), Math.min(18,
                                        decimalCount));
                        case Type.INT:
                                return new DBFType('n', Math.min(10, length), 0);
                        case Type.LONG:
                                return new DBFType('n', Math.min(18, length), 0);
                        case Type.SHORT:
                                return new DBFType('n', Math.min(5, length), 0);
                        case Type.STRING:
                                return new DBFType('c', Math.min(254, length), 0);
                        default:
                                throw new DriverException("Cannot store "
                                        + TypeFactory.getTypeName(fieldType.getTypeCode())
                                        + " in dbase");
                }
        }

        @Override
        public void copy(File in, File out) throws IOException {
                FileUtils.copy(in, out);
        }

        private void loadInternalMetadata() throws DriverException {
                metadata.clear();
                DbaseFileHeader header = dbaseReader.getHeader();

                for (int i = 0; i < header.getNumFields(); i++) {
                        String fieldsName = header.getFieldName(i);
                        final int type = getFieldType(i);
                        Type fieldType;
                        try {
                                switch (type) {
                                        case Type.STRING:
                                                fieldType = TypeFactory.createType(Type.STRING,
                                                        STRING, new LengthConstraint(
                                                        header.getFieldLength(i)));
                                                break;
                                        case Type.INT:
                                                fieldType = TypeFactory.createType(Type.INT, INTEGER,
                                                        new LengthConstraint(
                                                        header.getFieldLength(i)));
                                                break;
                                        case Type.LONG:
                                                fieldType = TypeFactory.createType(Type.LONG, LONG,
                                                        new LengthConstraint(
                                                        header.getFieldLength(i)));
                                                break;
                                        case Type.DOUBLE:
                                                fieldType = TypeFactory.createType(Type.DOUBLE,
                                                        DOUBLE, new Constraint[]{
                                                                new LengthConstraint(
                                                                header.getFieldLength(i)),
                                                               new PrecisionConstraint(
                                                                header.getFieldDecimalCount(i))});
                                                break;
                                        case Type.BOOLEAN:
                                                fieldType = TypeFactory.createType(Type.BOOLEAN,
                                                        BOOLEAN);
                                                break;
                                        case Type.DATE:
                                                fieldType = TypeFactory.createType(Type.DATE, DATE);
                                                break;
                                        default:
                                                throw new DriverException("Unknown dbf driver type: " + type);
                                }
                        } catch (InvalidTypeException e) {
                                throw new DriverException(e);
                        }
                        metadata.addField(fieldsName, fieldType);
                }
        }

        private int getFieldType(int i) throws DriverException {
                DbaseFileHeader header = dbaseReader.getHeader();
                char fieldType = header.getFieldType(i);

                switch (fieldType) {
                        // (L)logical (T,t,F,f,Y,y,N,n)
                        case 'l':
                        case 'L':
                                return Type.BOOLEAN;
                        // (C)character (String)
                        case 'c':
                        case 'C':
                                return Type.STRING;
                        // (D)date (Date)
                        case 'd':
                        case 'D':
                                return Type.DATE;
                        // (F)floating (Double)
                        case 'n':
                        case 'N':
                                if ((header.getFieldDecimalCount(i) == 0)) {
                                        if ((header.getFieldLength(i) >= 0)
                                                && (header.getFieldLength(i) < 10)) {
                                                return Type.INT;
                                        } else {
                                                return Type.LONG;
                                        }
                                }
                        case 'f':
                        case 'F': // floating point number
                                return Type.DOUBLE;
                        default:
                                throw new DriverException("Unknown field type");
                }
        }

        @Override
        public void open() throws DriverException {
                LOG.trace("Opening file");
                try {
                        FileInputStream fis = new FileInputStream(file);
                        dbaseReader = new DbaseFileReader(fis.getChannel(),
                                dataSourceFactory.getWarningListener());
                        loadInternalMetadata();
                } catch (IOException e) {
                        throw new DriverException(e);
                }
        }

        @Override
        public void close() throws DriverException {
                LOG.trace("Closing file");
                try {
                        dbaseReader.close();
                } catch (IOException e) {
                        throw new DriverException(e);
                } finally {
                        dbaseReader = null;
                }
        }

        @Override
        public String getDriverId() {
                return DRIVER_NAME;
        }

        @Override
        public TypeDefinition[] getTypesDefinitions() {
                return new TypeDefinition[]{
                                new DefaultTypeDefinition(STRING, Type.STRING,
                                new int[]{Constraint.LENGTH}),
                                new DefaultTypeDefinition(INTEGER, Type.INT,
                                new int[]{Constraint.LENGTH}),
                                new DefaultTypeDefinition(DOUBLE, Type.DOUBLE, new int[]{
                                        Constraint.LENGTH, Constraint.PRECISION}),
                                new DefaultTypeDefinition(BOOLEAN, Type.BOOLEAN),
                                new DefaultTypeDefinition(DATE, Type.DATE)};
        }

        @Override
        public boolean isCommitable() {
                return true;
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
        public String validateMetadata(Metadata metadata) {
                return null;
        }

        @Override
        public String[] getFileExtensions() {
                return new String[]{"dbf"};
        }

        @Override
        public String getTypeDescription() {
                return "dBase file";
        }

        @Override
        public String getTypeName() {
                return "DBF";
        }
}
