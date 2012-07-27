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
package org.gdms.driver.gdms;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintFactory;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.ByteProvider;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadBufferManager;

/**
 * Reader dedicated to the GDMS file format. Used by the GdmsDriver to retrieve informations.
 *
 */
public class GdmsReader {
        private static final int RASTERHEADERSIZE = ValueFactory.getRasterHeaderSize();
        
        private FileInputStream fis;
        private ReadBufferManager rbm;
        private int rowCount;
        private Envelope fullExtent;
        private DefaultMetadata metadata;
        private long[] rowIndexes;
        private byte version;
        private File file;
        private Map<Point, Value> rasterValueCache = new HashMap<Point, Value>();

        /**
         * Create a new GdmsReader instance
         * @param file
         *              the GDMS file.
         * @throws IOException if there is a problem when opening the file.
         */
        public GdmsReader(File file) throws IOException {
                this.file = file;
                metadata = new DefaultMetadata();
        }

        public void open() throws IOException {
                fis = new FileInputStream(file);
                rbm = new ReadBufferManager(fis.getChannel());
                rbm.position(0);
        }
        
        /**
         * Gets the state of this reader.
         * @return true if the driver is open.
         */
        public boolean isOpen() {
                return fis != null;
        }

        /**
         * Close the reader. It will close the inpu stream associated with the reader.
         * @throws IOException
         */
        public void close() throws IOException {
                fis.close();
                fis = null;
                rbm = null;
        }

        /**
         * Checks if there is enough remaining bytes, i.e. if there is more than
         * nb bytes left in the file
         * @param nb
         * @throws DriverException
         * @throws IOException
         */
        private void checkRemainingBytes(long nb) throws DriverException, IOException {
                if (rbm.remaining() < nb) {
                        throw new DriverException("The file is not a well formed GDMS file.");
                }
        }

        /**
         * Retrieve the metadata contained in the gdms file.
         * @throws IOException
         *                  If the file format is not supported
         * @throws DriverException
         *                  If there is a problem while reading the metadata.
         */
        public void readMetadata() throws IOException, DriverException {
                // right position
                rbm.position(0);

                // Check for long-enough header
                // version: 1 byte
                // rowCount: 4 bytes
                // fieldCount: 4 bytes
                // min: 16 bytes
                // max: 16 bytes
                checkRemainingBytes(41);

                // Read version
                version = rbm.get();
                if ((version != 2) && (version != 3) && (version != 4)) {
                        throw new IOException("Unsupported gdms format version: " + version);
                }

                // read dimensions
                rowCount = rbm.getInt();
                int fieldCount = rbm.getInt();

                // read Envelope
                Coordinate min = new Coordinate(rbm.getDouble(), rbm.getDouble());
                Coordinate max = new Coordinate(rbm.getDouble(), rbm.getDouble());
                fullExtent = new Envelope(min, max);

                // read field metadata
                String[] fieldNames = new String[fieldCount];
                Type[] fieldTypes = new Type[fieldCount];
                for (int i = 0; i < fieldCount; i++) {

                        // check there is a name length: 4 bytes
                        checkRemainingBytes(4);

                        // read name
                        int nameLength = rbm.getInt();

                        // check there is the name
                        checkRemainingBytes(nameLength);

                        // read the name
                        byte[] nameBytes = new byte[nameLength];
                        rbm.get(nameBytes);
                        fieldNames[i] = new String(nameBytes);

                        // check that there is the type
                        // typeCode: 4 bytes
                        // numConstraints: 4 bytes
                        checkRemainingBytes(8);

                        // read type
                        int typeCode = rbm.getInt();
                        int numConstraints = rbm.getInt();
                        Constraint[] constraints = new Constraint[numConstraints];
                        for (int j = 0; j < numConstraints; j++) {
                                // check that there is the info
                                checkRemainingBytes(8);

                                // read type
                                int type = rbm.getInt();
                                int size = rbm.getInt();

                                // check that there is the bytes
                                checkRemainingBytes(size);

                                byte[] constraintBytes = new byte[size];
                                rbm.get(constraintBytes);
                                constraints[j] = ConstraintFactory.createConstraint(type,
                                        constraintBytes);
                        }
                        fieldTypes[i] = TypeFactory.createType(typeCode, constraints);
                }
                metadata.clear();
                for (int i = 0; i < fieldTypes.length; i++) {
                        Type type = fieldTypes[i];
                        metadata.addField(fieldNames[i], type);
                }

                this.rowIndexes = new long[rowCount];
                if (version == 2) {
                        // check there is enough rowIndexes
                        // 4 bytes / index
                        checkRemainingBytes(rowCount * 4L);

                        // read row indexes after metadata
                        for (int i = 0; i < rowCount; i++) {
                                this.rowIndexes[i] = rbm.getInt();
                        }
                } else if (version == 3 && rowCount > 0) {
                        checkRemainingBytes(4);

                        // read row indexes at the end of the file
                        int rowIndexesDir = rbm.getInt();
                        rbm.position(rowIndexesDir);

                        // check there is enough rowIndexes
                        // 4 bytes / index
                        checkRemainingBytes(rowCount * 4L);

                        for (int i = 0; i < rowCount; i++) {
                                this.rowIndexes[i] = rbm.getInt();
                        }
                } else if (version == GdmsDriver.VERSION_NUMBER && rowCount > 0) {
                        checkRemainingBytes(8);

                        // read row indexes at the end of the file
                        long rowIndexesDir = rbm.getLong();
                        rbm.position(rowIndexesDir);

                        // check there is enough rowIndexes
                        // 8 bytes / index
                        checkRemainingBytes(rowCount * 8L);

                        for (int i = 0; i < rowCount; i++) {
                                this.rowIndexes[i] = rbm.getLong();
                        }
                }
        }

        /**
         * get the metadata contained in the GDMS file.
         * @return
         * @throws DriverException  
         */
        public Metadata getMetadata() throws DriverException {
                try {
                        if (rbm != null && metadata.getFieldCount() == 0 && rbm.getLength() > 0) {
                                readMetadata();
                        }
                } catch (IOException ex) {
                        throw new DriverException(ex);
                }

                return metadata;
        }

        /**
         * Get the value stored at row rowIndex, in the field fieldId
         * @param rowIndex
         * @param fieldId
         * @return
         * @throws DriverException
         */
        public Value getFieldValue(long rowIndex, int fieldId)
                throws DriverException {
                synchronized (this) {
                        int fieldType = metadata.getFieldType(fieldId).getTypeCode();
                        if (fieldType == Type.RASTER) {
                                Point point = new Point((int) rowIndex, fieldId);
                                Value ret = rasterValueCache.get(point);
                                if (ret != null) {
                                        return ret;
                                } else {
                                        try {
                                                // ignore value size
                                                moveBufferAndGetSize(rowIndex, fieldId);
                                                int valueType = rbm.getInt();
                                                if (valueType == Type.NULL) {
                                                        return ValueFactory.createNullValue();
                                                } else {
                                                        // Read header
                                                        byte[] valueBytes = new byte[RASTERHEADERSIZE];
                                                        rbm.get(valueBytes);
                                                        Value lazyRasterValue = ValueFactory.createLazyValue(fieldType, valueBytes,
                                                                new RasterByteProvider(rowIndex,
                                                                fieldId));
                                                        lazyRasterValue.getAsRaster().open();
                                                        rasterValueCache.put(point, lazyRasterValue);
                                                        return lazyRasterValue;
                                                }
                                        } catch (IOException e) {
                                                throw new DriverException(e.getMessage(), e);
                                        }
                                }
                        } else {
                                return getFullValue(rowIndex, fieldId);
                        }
                }
        }

        private Value getFullValue(long rowIndex, int fieldId)
                throws DriverException {
                try {
                        int valueSize = moveBufferAndGetSize(rowIndex, fieldId);
                        int valueType = rbm.getInt();
                        byte[] valueBytes = new byte[valueSize];
                        rbm.get(valueBytes);
                        return ValueFactory.createValue(valueType, valueBytes);
                } catch (IOException e) {
                        throw new DriverException(e.getMessage(), e);
                }
        }

        private int moveBufferAndGetSize(long rowIndex, int fieldId)
                throws IOException {
                //We retrieve the position of this row in the file
                long rowBytePosition = rowIndexes[(int) rowIndex];
                //We move the good position : we want to know the adress of the
                //field number fieldId, so we need to know the adress were it is.
                long fieldBytePosition;
                if (version == GdmsDriver.VERSION_NUMBER) {
                        rbm.position(rowBytePosition + 8 * fieldId);
                        //We retrieve the adress...
                        fieldBytePosition = rbm.getLong();
                } else {
                        rbm.position(rowBytePosition + 4 * fieldId);
                        //We retrieve the adress...
                        fieldBytePosition = rbm.getInt();
                }
                //And then we move.
                rbm.position(fieldBytePosition);

                // read byte array size
                return rbm.getInt();
        }

        private class RasterByteProvider implements ByteProvider {

                private long rowIndex;
                private int fieldId;

                RasterByteProvider(long rowIndex, int fieldId) {
                        this.rowIndex = rowIndex;
                        this.fieldId = fieldId;
                }

                @Override
                public byte[] getBytes() throws IOException {
                        synchronized (GdmsReader.this) {
                                int valueSize = moveBufferAndGetSize(rowIndex, fieldId);
                                // Ignore type. If it's null it's not read lazily
                                rbm.getInt();
                                byte[] valueBytes = new byte[valueSize];
                                rbm.get(valueBytes);

                                // Restore buffer size
                                moveBufferAndGetSize(rowIndex, fieldId);
                                rbm.get();

                                return valueBytes;
                        }
                }
        }

        /**
         * Get the envelope which contains these datas
         * @return
         */
        public Envelope getFullExtent() {
                return fullExtent;
        }

        /**
         * Get the number of rows in the table
         * @return
         */
        public long getRowCount() {
                return rowCount;
        }

        public Number[] getScope(int dimension) {
                if (dimension == DataSet.X) {
                        return new Number[]{getFullExtent().getMinX(),
                                        getFullExtent().getMaxX()};
                } else if (dimension == DataSet.Y) {
                        return new Number[]{getFullExtent().getMinY(),
                                        getFullExtent().getMaxY()};
                } else {
                        return null;
                }
        }
}
