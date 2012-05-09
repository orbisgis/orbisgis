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
package org.gdms.driver.gdms;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import com.vividsolutions.jts.geom.Envelope;
import org.orbisgis.progress.ProgressMonitor;

import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadWriteBufferManager;
import org.gdms.driver.io.RowWriter;

/**
 * Class to write gdms files.
 *
 * @author Fernando Gonzalez Cortes
 */
public class GdmsWriter implements RowWriter {

        private long[] rowindexes = new long[1024];
        private RandomAccessFile raf;
        private ReadWriteBufferManager bm;
        private long previousRowEnd = -1;
        private Metadata metadata;
        private Envelope env = null;
        private int currentRow = 0;
        private long rowIndexesDirPos;

        /**
         * Creates a new writer on the given file.
         *
         * @param file a writable file
         * @throws IOException if there is an error accessing the file
         */
        public GdmsWriter(File file) throws IOException {
                raf = new RandomAccessFile(file, "rw");
                bm = new ReadWriteBufferManager(raf.getChannel());
        }

        /**
         * Closes (and flushes) the writer.
         *
         * @throws IOException
         */
        public void close() throws IOException {
                // setting rowIndexes to null would require a check for != null for every call
                // of addValues. With this it does not, if addValues is called after close the bm
                // will throw an exception
                rowindexes = new long[1];
                currentRow = 0;
                
                // close the streams
                bm.close();
                raf.close();
        }

        /**
         * Writes all the content of the given DataSet to the file.
         *
         * @param dataSource a DataSet
         * @param pm a ProgressMonitor
         * @throws IOException if there is an error accessing the file
         * @throws DriverException if there is an error accessing the data set
         */
        public void write(DataSet dataSource, ProgressMonitor pm)
                throws IOException, DriverException {
                writeMetadata(dataSource.getRowCount(), dataSource.getMetadata());
                // Write the file building the row indexes in memory
                final long rowcount = dataSource.getRowCount();
                pm.startTask("Writing file", rowcount);
                final int colCount = dataSource.getMetadata().getFieldCount();
                rowindexes = new long[(int) rowcount];
                currentRow = 0;
                for (int i = 0; i < rowcount; i++) {
                        if (i >= 100 && i % 100 == 0) {
                                if (pm.isCancelled()) {
                                        break;
                                } else {
                                        pm.progressTo(i);
                                }
                        }

                        Value[] row = new Value[colCount];
                        for (int j = 0; j < row.length; j++) {
                                row[j] = dataSource.getFieldValue(i, j);
                        }

                        addRow(row);
                }
                // write the row indexes
                writeRowIndexes();
                pm.progressTo(rowcount);
                // write envelope
                writeExtent();
                pm.endTask();
        }

        /**
         * Write the address of each row in the file we are writing in.
         *
         * @throws IOException If a problem occurs while writing the file
         */
        public void writeRowIndexes() throws IOException {
                long rowIndexesDir = previousRowEnd;
                bm.position(previousRowEnd);
                for (int i = 0; i < currentRow; i++) {
                        bm.putLong(rowindexes[i]);
                }
                // Write row indexes position
                bm.position(rowIndexesDirPos);
                bm.putLong(rowIndexesDir);
        }

        /**
         * Adds a row to the Gdms file.
         *
         * @param row an array of values representing a row
         * @throws DriverException
         */
        public void addValues(Value[] row) throws DriverException {
                ensureCapacity();
                try {
                        addRow(row);
                } catch (IOException e) {
                        throw new DriverException("Cannot add values", e);
                }
        }

        private void ensureCapacity() {
                int size = rowindexes.length;
                if (currentRow == size) {
                        int newSize = (size * 3) / 2 + 1;
                        rowindexes = Arrays.copyOf(rowindexes, newSize);
                }
        }

        private void addRow(Value[] row) throws DriverException, IOException {
                if (previousRowEnd == -1) {
                        previousRowEnd = bm.getPosition();
                }
                rowindexes[currentRow] = previousRowEnd;
                bm.position(previousRowEnd);
                // Leave space for the row header
                long rowHeaderStart = bm.getPosition();
                //The row header will contain the adress of the associated field.
                //This adress is stored in a long, we need 8 bytes.
                int rowHeaderSize = metadata.getFieldCount() * 8;
                bm.position(rowHeaderStart + rowHeaderSize);

                // Write the row and keep the field positions in memory
                long[] fieldPositions = new long[metadata.getFieldCount()];
                for (int j = 0; j < metadata.getFieldCount(); j++) {
                        fieldPositions[j] = bm.getPosition();
                        Value value = row[j];
                        byte[] bytes = value.getBytes();
                        //We put the number of bytes we need to store the value.
                        bm.putInt(bytes.length);
                        //We put the type of the value.
                        bm.putInt(value.getType());
                        //And finally, we put the value, as an array of bytes.
                        bm.put(bytes);
                        int typeCode = metadata.getFieldType(j).getTypeCode();
                        Envelope fieldEnvelope = null;
                        if (!value.isNull() && (typeCode & Type.GEOMETRY) != 0) {
                                fieldEnvelope = new Envelope(value.getAsGeometry().getEnvelopeInternal());
                        } else if (!value.isNull() && (typeCode == Type.RASTER)) {
                                fieldEnvelope = new Envelope(value.getAsRaster().getMetadata().getEnvelope());
                        }
                        if (fieldEnvelope != null) {
                                if (env == null) {
                                        env = new Envelope(fieldEnvelope);
                                } else {
                                        env.expandToInclude(fieldEnvelope);
                                }
                        }
                }

                previousRowEnd = bm.getPosition();

                // Write row header
                bm.position(rowHeaderStart);
                for (long fieldPosition : fieldPositions) {
                        bm.putLong(fieldPosition);
                }

                currentRow++;
        }

        /**
         * Writes the overall extend of the content of the file in the metadata.
         *
         * @throws IOException
         */
        public void writeExtent() throws IOException {
                if (env != null) {
                        bm.position(0);
                        // version
                        bm.skip(1);
                        // dimensions
                        bm.skip(4);
                        bm.skip(4);

                        // write extent
                        bm.putDouble(env.getMinX());
                        bm.putDouble(env.getMinY());
                        bm.putDouble(env.getMaxX());
                        bm.putDouble(env.getMaxY());
                }
        }

        /**
         * Writes the metadata of the file
         *
         * @param rowCount the rowCount to write
         * @param metadata the metadata object to write
         * @throws IOException
         * @throws DriverException
         */
        public void writeMetadata(long rowCount, Metadata metadata)
                throws IOException, DriverException {
                this.metadata = metadata;

                bm.position(0);

                // Write version number
                bm.put(GdmsDriver.VERSION_NUMBER);

                // write dimensions
                bm.putInt((int) rowCount);
                bm.putInt(metadata.getFieldCount());

                // write default extent
                bm.putDouble(0);
                bm.putDouble(0);
                bm.putDouble(0);
                bm.putDouble(0);

                // write field metadata
                for (int i = 0; i < metadata.getFieldCount(); i++) {
                        // write name
                        byte[] fieldName = metadata.getFieldName(i).getBytes();
                        bm.putInt(fieldName.length);
                        bm.put(fieldName);

                        // write type
                        Type type = metadata.getFieldType(i);
                        int typeCode = type.getTypeCode();
                        bm.putInt(typeCode);
                        Constraint[] constrs = type.getConstraints();

                        bm.putInt(constrs.length);
                        for (Constraint constraint : constrs) {
                                bm.putInt(constraint.getConstraintCode());
                                byte[] constraintContent = constraint.getBytes();
                                bm.putInt(constraintContent.length);
                                bm.put(constraintContent);
                        }
                }

                rowIndexesDirPos = bm.getPosition();

                // Skip rowIndexes position
                bm.putLong(-1);
        }

        /**
         * Writes the number of rows that were written.
         *
         * @throws IOException
         */
        public void writeWritenRowCount() throws IOException {
                bm.position(0);
                // version
                bm.skip(1);
                // dimensions
                bm.putInt(currentRow);
        }
}
