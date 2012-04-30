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
package org.gdms.data.indexes;

import java.io.File;
import java.io.IOException;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import org.orbisgis.progress.ProgressMonitor;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.indexes.rtree.DiskRTree;
import org.gdms.data.indexes.tree.IndexVisitor;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.data.types.IncompatibleTypesException;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;

public class RTreeIndex implements DataSourceIndex<Envelope> {

        private String[] fieldNames;
        private DiskRTree index;
        private File indexFile;

        @Override
        public void setFieldNames(String[] fieldNames) {
                this.fieldNames = fieldNames;
        }

        @Override
        public void buildIndex(DataSourceFactory dsf, DataSet dataSource,
                ProgressMonitor pm) throws IndexException {
                try {
                        int fieldId = dataSource.getMetadata().getFieldIndex(fieldNames[0]);
                        if (fieldId == -1) {
                                throw new IndexException("Cannot find the field " + fieldNames + " to index");
                        }
                        if ((dataSource.getMetadata().getFieldType(fieldId).getTypeCode() & MetadataUtilities.ANYGEOMETRY) == 0) {
                                throw new IndexException(fieldNames + " is not spatial");
                        }
                        long rowCount = dataSource.getRowCount();
                        pm.startTask("Building index", rowCount);
                        index = new DiskRTree(255, 1024);
                        if (indexFile != null) {
                                index.newIndex(indexFile);
                        }
                        
                        for (int i = 0; i < rowCount; i++) {
                                if (i >= 1000 && i % 1000 == 0) {
                                        if (pm.isCancelled()) {
                                                return;
                                        }
                                        pm.progressTo(i);
                                }
                                Value fieldValue = dataSource.getFieldValue(i, fieldId);
                                if (fieldValue.getType() != Type.NULL) {
                                        Geometry g = fieldValue.getAsGeometry();
                                        if (g != null) {
                                                if (!g.isEmpty()) {
                                                        index.insert(g.getEnvelopeInternal(),
                                                                Integer.valueOf(i));
                                                }
                                        }
                                }
                        }
                        pm.progressTo(rowCount);
                } catch (IOException e) {
                        throw new IndexException("Cannot create the index", e);
                } catch (AlreadyClosedException e) {
                        throw new IndexException(e);
                } catch (DriverException e) {
                        throw new IndexException(e);
                }
                pm.endTask();
        }

        @Override
        public void deleteRow(Value value, int row) throws IndexException {
                if (!value.isNull()) {
                        Geometry g = value.getAsGeometry();
                        try {
                                index.delete(g.getEnvelopeInternal(), row);
                        } catch (IOException e) {
                                throw new IndexException("Cannot delete at the index", e);
                        }
                }
        }

        @Override
        public String[] getFieldNames() {
                return fieldNames;
        }

        @Override
        public int[] query(IndexQuery query)
                throws IndexException, IndexQueryException {
                if (!(query instanceof SpatialIndexQuery)) {
                        throw new IllegalArgumentException("Wrong query type. RTreeIndex only supports SpatialIndexQuery.");
                }
                SpatialIndexQuery q = (SpatialIndexQuery) query;
                try {
                        return index.query(q.getArea());
                } catch (IOException e) {
                        throw new IndexException("Cannot access the index", e);
                } catch (IncompatibleTypesException e) {
                        throw new IndexException("Cannot compute the value to "
                                + "query the index", e);
                }
        }
        
        @Override
        public void query(IndexQuery query, IndexVisitor<Envelope> visitor)
                throws IndexException, IndexQueryException {
                if (!(query instanceof SpatialIndexQuery)) {
                        throw new IllegalArgumentException("Wrong query type. RTreeIndex only supports SpatialIndexQuery.");
                }
                SpatialIndexQuery q = (SpatialIndexQuery) query;
                try {
                        index.query(q.getArea(), visitor);
                } catch (IOException e) {
                        throw new IndexException("Cannot access the index", e);
                } catch (IncompatibleTypesException e) {
                        throw new IndexException("Cannot compute the value to "
                                + "query the index", e);
                }
        }

        @Override
        public void insertRow(Value value, int row) throws IndexException {
                Value newGeometry = value;
                try {
                        if (newGeometry.isNull()) {
                                index.updateRows(row, 1);
                                /*
                                 * The index cannot hold null geometries
                                 */
                                return;
                        } else {
                                Geometry g = newGeometry.getAsGeometry();
                                index.insert(g.getEnvelopeInternal(), row);
                        }
                } catch (IOException e) {
                        throw new IndexException("Cannot insert at the index", e);
                }
        }

        @Override
        public void load() throws IndexException {
                try {
                        index = new DiskRTree(255, 1024);
                        index.openIndex(indexFile);
                } catch (IOException e) {
                        throw new IndexException("Cannot load index from file", e);
                }
        }

        @Override
        public void save() throws IndexException {
                try {
                        index.save();
                } catch (IOException e) {
                        throw new IndexException("Cannot save index", e);
                }
        }

        @Override
        public void setFieldValue(Value oldGeometry, Value newGeometry, int rowIndex)
                throws IndexException {
                if (!oldGeometry.isNull()) {
                        Geometry g = oldGeometry.getAsGeometry();
                        try {
                                index.delete(g.getEnvelopeInternal(), rowIndex);
                        } catch (IOException e) {
                                throw new IndexException("Cannot delete old value from index", e);
                        }
                }

                if (!newGeometry.isNull()) {
                        Geometry g = newGeometry.getAsGeometry();
                        try {
                                index.insert(g.getEnvelopeInternal(), rowIndex);
                        } catch (IOException e) {
                                throw new IndexException("Cannot perform modification "
                                        + "in index. Index is corrupted. "
                                        + "It's recommended to rebuild the index from scratch", e);
                        }
                }
        }

        @Override
        public File getFile() {
                return indexFile;
        }

        @Override
        public void setFile(File file) {
                this.indexFile = file;
        }

        @Override
        public void close() throws IOException {
                if (index != null) {
                        index.close();
                        index = null;
                }
        }

        @Override
        public boolean isOpen() {
                return index != null;
        }
}
