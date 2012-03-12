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
package org.gdms.data.indexes;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.indexes.tree.IndexVisitor;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.gdms.driver.MemoryDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.Source;
import org.gdms.data.types.IncompatibleTypesException;
import org.gdms.driver.DataSet;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.utils.FileUtils;

/**
 * This class manages all indexes linked to a {@code DataSourceFactory}.
 * @author Antoine Gourlay
 */
public class IndexManager {

        private static final String TEMPINDEXPREFIX = "tempindex";
        private static final Logger logger = Logger.getLogger(IndexManager.class);
        public static final String INDEX_PROPERTY_PREFIX = "org.gdms.index";
        public static final String RTREE_SPATIAL_INDEX = "org.gdms.rtree";
        public static final String BTREE_ALPHANUMERIC_INDEX = "org.gdms.btree";
        private DataSourceFactory dsf;
        private Map<String, Class<? extends DataSourceIndex>> indexRegistry = new HashMap<String, Class<? extends DataSourceIndex>>();
        private Map<IndexDefinition, DataSourceIndex> indexCache = new HashMap<IndexDefinition, DataSourceIndex>();
        private List<IndexManagerListener> listeners = new ArrayList<IndexManagerListener>();

        /**
         * Creates a new IndexManager linked to a given DataSourceFactory.
         * @param dsf a DataSourceFactory.
         */
        public IndexManager(DataSourceFactory dsf) {
                this.dsf = dsf;
        }

        /**
         * Adds an IndexManagerListener to this manager.
         * @param listener a listener
         */
        public void addIndexManagerListener(IndexManagerListener listener) {
                listeners.add(listener);
        }

        /**
         * Removes an IndexManagerListener from this manager.
         * @param listener a listener
         * @return true if the manager was really removed
         */
        public boolean removeIndexManagerListener(IndexManagerListener listener) {
                return listeners.remove(listener);
        }

        /**
         * Builds the specified index on the specified field of the datasource.
         * Saves the index in a file
         *
         * @param dsName name of the source
         * @param fieldName name of the field to index
         * @param indexId the index type; can be null.
         * @param pm a progress monitor; can be null.
         * @throws IndexException if there is a problem building the index.
         * @throws NoSuchTableException if the table <tt>dsName</tt> does not exist.
         */
        public void buildIndex(String dsName, String fieldName, String indexId,
                ProgressMonitor pm) throws IndexException, NoSuchTableException {
                if (pm == null) {
                        pm = new NullProgressMonitor();
                }
                dsName = dsf.getSourceManager().getMainNameFor(dsName);

                Source src = dsf.getSourceManager().getSource(dsName);

                // Get index id if null
                DataSource ds = null;
                String propertyName = null;
                try {
                        ds = dsf.getDataSource(dsName, DataSourceFactory.NORMAL);
                        ds.open();
                        if (indexId == null) {
                                int code = ds.getFieldType(ds.getFieldIndexByName(fieldName)).getTypeCode();
                                if ((code & Type.GEOMETRY) != 0) {
                                        indexId = RTREE_SPATIAL_INDEX;
                                } else {
                                        indexId = BTREE_ALPHANUMERIC_INDEX;
                                }
                        }

                        propertyName = INDEX_PROPERTY_PREFIX + "-" + fieldName + "-"
                                + indexId;

                        // build index

                        if (src.getFileProperty(propertyName) != null) {
                                throw new IndexException("There is already an "
                                        + "index on that field for that source: " + dsName
                                        + "." + fieldName);
                        }
                        File indexFile = src.createFileProperty(propertyName);
                        DataSourceIndex index = null;
                        index = instantiateIndex(indexId);
                        index.setFile(indexFile);
                        index.setFieldName(fieldName);
                        index.buildIndex(dsf, ds, pm);
                        if (pm.isCancelled()) {
                                src.deleteProperty(propertyName);
                                return;
                        }
                        index.save();
                        index.close();

                        IndexDefinition def = new IndexDefinition(dsName, fieldName);
                        indexCache.put(def, index);
                        fireIndexCreated(dsName, fieldName, indexId, pm);
                } catch (DriverLoadException e) {
                        throw new IndexException("Cannot read source", e);
                } catch (IncompatibleTypesException e) {
                        try {
                                src.deleteProperty(propertyName);
                        } catch (IOException e1) {
                                logger.debug("Cannot create index and remove property", e1);
                        }
                        throw new IndexException("Cannot create an "
                                + "index with that field type: " + fieldName, e);
                } catch (IOException e) {
                        throw new IndexException("Cannot associate index with source", e);
                } catch (DriverException e) {
                        try {
                                src.deleteProperty(propertyName);
                        } catch (IOException e1) {
                                logger.debug("Cannot create index and remove property", e1);
                        }
                        throw new IndexException("Cannot access data to index", e);
                } catch (NoSuchTableException e) {
                        throw new IndexException("The source doesn't exist", e);
                } catch (DataSourceCreationException e) {
                        throw new IndexException("Cannot access the source", e);
                } finally {
                        if (ds != null && ds.isOpen()) {
                                try {
                                        ds.close();
                                } catch (DriverException ex) {
                                        throw new IndexException("Cannot access the source", ex);
                                }
                        }
                }

        }

        /**
         * Builds the specified index on the specified field of the datasource.
         * The index type if determined based on the field type.
         * Saves the index in a file.
         *
         * @param tableName name of the source
         * @param fieldName name of the field to index
         * @param pm a progress monitor; can be null.
         * @throws NoSuchTableException
         * @throws IndexException
         */
        public void buildIndex(String tableName, String fieldName,
                ProgressMonitor pm) throws NoSuchTableException, IndexException {
                buildIndex(tableName, fieldName, null, pm);
        }

        /**
         * Builds the specified index on the specified field of the datasource.
         * Saves the index in a file
         *
         * @param src the source
         * @param fieldName name of the field to index
         * @param indexId the index type; can be null.
         * @param pm a progress monitor; can be null.
         * @throws IndexException if there is a problem building the index.
         * @throws NoSuchTableException if the table <tt>dsName</tt> does not exist.
         */
        public void buildIndex(DataSet src, String fieldName, String indexId,
                ProgressMonitor pm) throws IndexException, NoSuchTableException {
                if (src instanceof DataSource) {
                        DataSource ds = (DataSource) src;
                        buildIndex(ds.getName(), fieldName, pm);
                } else {
                        // building temp index
                        if (pm == null) {
                                pm = new NullProgressMonitor();
                        }
                        String tempName = TEMPINDEXPREFIX + src.hashCode();

                        // Get index id if null
                        try {
                                if (indexId == null) {
                                        int code = src.getMetadata().getFieldType(src.getMetadata().getFieldIndex(fieldName)).getTypeCode();
                                        if (((code & MetadataUtilities.ANYGEOMETRY) != 0)) {
                                                indexId = RTREE_SPATIAL_INDEX;
                                        } else {
                                                indexId = BTREE_ALPHANUMERIC_INDEX;
                                        }
                                }

                                // build index

                                IndexDefinition def = new IndexDefinition(tempName, fieldName);
                                if (indexCache.get(def) != null) {
                                        throw new IndexException("There is already an "
                                                + "index on that field for that temp source on "
                                                + fieldName);
                                }
                                File indexFile = new File(dsf.getTempFile("idx"));
                                DataSourceIndex index = null;
                                index = instantiateIndex(indexId);
                                index.setFile(indexFile);
                                index.setFieldName(fieldName);
                                index.buildIndex(dsf, src, pm);
                                if (pm.isCancelled()) {
                                        return;
                                }
                                index.save();
                                index.close();

                                indexCache.put(def, index);
                                fireIndexCreated(tempName, fieldName, indexId, pm);
                        } catch (DriverLoadException e) {
                                throw new IndexException("Cannot read source", e);
                        } catch (IncompatibleTypesException e) {
                                throw new IndexException("Cannot create an "
                                        + "index with that field type: " + fieldName, e);
                        } catch (IOException e) {
                                throw new IndexException("Cannot associate index with source", e);
                        } catch (DriverException e) {
                                throw new IndexException("Cannot access data to index", e);
                        }
                }
        }

        /**
         * Builds the specified index on the specified field of the DataSet.
         * The index type if determined based on the field type.
         * Saves the index in a file.
         *
         * @param src the source
         * @param fieldName name of the field to index
         * @param pm a progress monitor; can be null.
         * @throws NoSuchTableException
         * @throws IndexException
         */
        public void buildIndex(DataSet src, String fieldName,
                ProgressMonitor pm) throws NoSuchTableException, IndexException {
                buildIndex(src, fieldName, null, pm);
        }

        private void fireIndexCreated(String dsName, String fieldName,
                String indexId, ProgressMonitor pm) throws IndexException {
                for (IndexManagerListener listener : listeners) {
                        listener.indexCreated(dsName, fieldName, indexId, this, pm);
                }
        }

        private void fireIndexDeleted(String dsName, String fieldName,
                String indexId) throws IndexException {
                for (IndexManagerListener listener : listeners) {
                        listener.indexDeleted(dsName, fieldName, indexId, this);
                }
        }

        /**
         * Registers an index into the collection of indexes.
         *
         * @param id
         * @param index
         */
        public void addIndex(String id, Class<? extends DataSourceIndex> index) {
                indexRegistry.put(id, index);
        }

        DataSourceIndex instantiateIndex(String indexId) throws IndexException {
                final Class<? extends DataSourceIndex> indexClass = indexRegistry.get(indexId);
                if (indexClass == null) {
                        throw new IllegalArgumentException("Cannot find the index type: " + indexId);
                }
                try {
                        return indexClass.newInstance();
                } catch (InstantiationException e) {
                        throw new IndexException("Cannot instantiate the index", e);
                } catch (IllegalAccessException e) {
                        throw new IndexException("Cannot instantiate the index", e);
                }
        }

        /**
         * Gets the index for the specified source name. All instances of DataSource
         * that access to the specified source will use the same index instance.
         *
         * @param dsName the name of the source
         * @param fieldName the name of the indexed field
         * @return the index, or null if not found.
         * @throws IndexException
         * @throws NoSuchTableException
         */
        public DataSourceIndex getIndex(String dsName, String fieldName)
                throws IndexException, NoSuchTableException {
                IndexDefinition def = new IndexDefinition(dsName, fieldName);
                DataSourceIndex ret = indexCache.get(def);
                if (ret == null) {
                        String[] indexProperties = getIndexProperties(dsName);
                        for (String indexProperty : indexProperties) {
                                String propertyFieldName = getFieldName(indexProperty);
                                if (propertyFieldName.equals(fieldName)) {
                                        DataSourceIndex index = instantiateIndex(getIndexId(indexProperty));
                                        Source src = dsf.getSourceManager().getSource(dsName);
                                        index.setFieldName(fieldName);
                                        index.setFile(src.getFileProperty(indexProperty));
                                        index.load();
                                        indexCache.put(def, index);
                                        ret = index;
                                        break;
                                }
                        }
                }

                return ret;
        }

        /**
         * Gets the index for the specified dataset. All instances of DataSource
         * that access to the specified source will use the same index instance.
         *
         * @param src a dataset
         * @param fieldName the name of the indexed field
         * @return the index, or null if not found.
         * @throws IndexException
         * @throws NoSuchTableException
         */
        public DataSourceIndex getIndex(DataSet src, String fieldName) throws IndexException, NoSuchTableException {
                if (src instanceof DataSource) {
                        DataSource ds = (DataSource) src;
                        return getIndex(ds.getName(), fieldName);
                } else {
                        String code = TEMPINDEXPREFIX + String.valueOf(src.hashCode());
                        IndexDefinition def = new IndexDefinition(code, fieldName);
                        return indexCache.get(def);
                }
        }

        private static class IndexDefinition {

                public String dsName;
                public String fieldName;

                IndexDefinition(String dsName, String fieldName) {
                        super();
                        this.dsName = dsName;
                        this.fieldName = fieldName;
                }

                @Override
                public boolean equals(Object obj) {
                        if (obj instanceof IndexDefinition) {
                                IndexDefinition id = (IndexDefinition) obj;
                                return dsName.equals(id.dsName) && fieldName.equals(id.fieldName);
                        } else {
                                return false;
                        }
                }

                @Override
                public int hashCode() {
                        return dsName.hashCode() + fieldName.hashCode();
                }
        }

        /**
         * Triggers a rebuilding of the given indexes on the given table.
         * @param dsName a table name
         * @param modifiedIndexes some indexes that were modified
         * @throws IOException if the index files could not be modified
         */
        public void indexesChanged(String dsName, DataSourceIndex[] modifiedIndexes)
                throws IOException {
                try {
                        dsName = dsf.getSourceManager().getMainNameFor(dsName);
                        String[] indexProperties = getIndexProperties(dsName);
                        Source src = dsf.getSourceManager().getSource(dsName);
                        for (String indexProperty : indexProperties) {
                                // Search the modified index related to this property
                                for (DataSourceIndex dataSourceIndex : modifiedIndexes) {
                                        if (dataSourceIndex.getFieldName().equals(
                                                getFieldName(indexProperty))) {
                                                // Get the file of the property and change the contents
                                                File dest = src.getFileProperty(indexProperty);
                                                File source = dataSourceIndex.getFile();
                                                BufferedOutputStream out = null;

                                                try {
                                                        out = new BufferedOutputStream(
                                                                new FileOutputStream(dest));

                                                        dataSourceIndex.save();
                                                        dataSourceIndex.close();

                                                        BufferedInputStream in = null;
                                                        try {
                                                                in = new BufferedInputStream(
                                                                        new FileInputStream(source));
                                                                FileUtils.copy(in, out);
                                                        } finally {
                                                                if (in != null) {
                                                                        in.close();
                                                                }
                                                        }

                                                } catch (IOException ex) {
                                                        src.deleteProperty(indexProperty);
                                                } catch (IndexException ex) {
                                                        src.deleteProperty(indexProperty);
                                                } finally {
                                                        if (out != null) {
                                                                out.close();
                                                        }
                                                }


                                                IndexDefinition def = new IndexDefinition(dsName,
                                                        getFieldName(indexProperty));
                                                indexCache.remove(def);
                                        }
                                }
                        }
                } catch (NoSuchTableException e) {
                        throw new IllegalArgumentException("bug: The source "
                                + "does not exist: " + dsName, e);
                }
        }

        /**
         * Gets the indexes of the specified source
         *
         * @param dsName the name of the source
         * @return all indexes on the source <tt>dsName</tt>
         * @throws IndexException
         * @throws NoSuchTableException
         */
        public DataSourceIndex[] getIndexes(String dsName) throws IndexException,
                NoSuchTableException {
                ArrayList<DataSourceIndex> ret = new ArrayList<DataSourceIndex>();
                dsName = dsf.getSourceManager().getMainNameFor(dsName);
                String[] indexProperties = getIndexProperties(dsName);
                for (String indexProperty : indexProperties) {
                        String fieldName = getFieldName(indexProperty);
                        DataSourceIndex index = getIndex(dsName, fieldName);
                        if (index != null) {
                                ret.add(index);
                        }
                }

                return ret.toArray(new DataSourceIndex[ret.size()]);
        }

        private String getIndexId(String propertyName) {
                String prop = propertyName.substring(INDEX_PROPERTY_PREFIX.length() + 1);
                String indexId = prop.substring(prop.indexOf('-') + 1);

                return indexId;
        }

        private String getFieldName(String propertyName) {
                String prop = propertyName.substring(INDEX_PROPERTY_PREFIX.length() + 1);
                String fieldName = prop.substring(0, prop.indexOf('-'));

                return fieldName;
        }

        private String[] getIndexProperties(String dsName) {
                ArrayList<String> ret = new ArrayList<String>();
                Source src = dsf.getSourceManager().getSource(dsName);
                if (src != null) {
                        String[] fileProperties = src.getFilePropertyNames();
                        for (String fileProperty : fileProperties) {
                                if (fileProperty.startsWith(INDEX_PROPERTY_PREFIX)) {
                                        ret.add(fileProperty);
                                }
                        }
                } else {
                        throw new IllegalArgumentException("The source doesn't exist: "
                                + dsName);
                }

                return ret.toArray(new String[ret.size()]);
        }

        /**
         * Queries the index of the specified source, with the specified query
         *
         * @param dsName the name of the source
         * @param indexQuery a query to execute against the source
         * @return The iterator or null if there is no index in the specified field
         * @throws IndexException
         * @throws NoSuchTableException
         * @throws IndexQueryException 
         */
        public int[] queryIndex(String dsName, IndexQuery indexQuery)
                throws IndexException, NoSuchTableException, IndexQueryException {
                DataSourceIndex dsi;
                dsi = getIndex(dsName, indexQuery.getFieldName());
                if (dsi != null) {
                        if (!dsi.isOpen()) {
                                dsi.load();
                        }
                        return dsi.getIterator(indexQuery);
                } else {
                        return null;
                }
        }

        /**
         * Queries the index of the specified source, with the specified query and the specified visitor.
         * 
         * The visitor must have the correct type parameter (the type of the values being stored in the index).
         *
         * @param dsName the name of the source
         * @param indexQuery a query to execute against the source
         * @param visitor an index visitor
         * @return The iterator or null if there is no index in the specified field
         * @throws IndexException
         * @throws NoSuchTableException
         * @throws IndexQueryException 
         */
        public int[] queryIndex(String dsName, IndexQuery indexQuery, IndexVisitor<?> visitor)
                throws IndexException, NoSuchTableException, IndexQueryException {
                DataSourceIndex dsi;
                dsi = getIndex(dsName, indexQuery.getFieldName());
                if (dsi != null) {
                        if (!dsi.isOpen()) {
                                dsi.load();
                        }
                        return dsi.getIterator(indexQuery, visitor);
                } else {
                        return null;
                }
        }

        /**
         * Queries the specified source, using the specified query on an index if there is one.
         * Otherwise returns a full iterator of the source.
         *
         * @param dsName the name of the source
         * @param indexQuery a query to execute against the source
         * @return The iterator or null if there is no index in the specified field
         * @throws DriverException
         */
        public Iterator<Integer> iterateUsingIndexQuery(String dsName, IndexQuery indexQuery)
                throws DriverException {
                try {
                        int[] ret = queryIndex(dsName, indexQuery);

                        if (ret != null) {
                                return new ResultIterator(ret);
                        } else {
                                return new FullIterator(dsf.getDataSource(dsName));
                        }
                } catch (IndexException e) {
                        throw new DriverException(e);
                } catch (IndexQueryException e) {
                        throw new DriverException(e);
                } catch (NoSuchTableException e) {
                        throw new DriverException(e);
                } catch (DataSourceCreationException e) {
                        throw new DriverException(e);
                }
        }

        /**
         * Queries the specified source, using the specified query on an index if there is one.
         * Otherwise returns a full iterator of the source.
         *
         * @param src the source
         * @param indexQuery a query to execute against the source
         * @return The iterator or null if there is no index in the specified field
         * @throws DriverException
         */
        public Iterator<Integer> iterateUsingIndexQuery(DataSet src, IndexQuery indexQuery)
                throws DriverException {
                if (src instanceof DataSource) {
                        DataSource ds = (DataSource) src;
                        return iterateUsingIndexQuery(ds.getName(), indexQuery);
                }

                try {
                        int[] ret = queryIndex(TEMPINDEXPREFIX + src.hashCode(), indexQuery);

                        if (ret != null) {
                                return new ResultIterator(ret);
                        } else {
                                return new FullIterator(src);
                        }
                } catch (IndexException e) {
                        throw new DriverException(e);
                } catch (IndexQueryException e) {
                        throw new DriverException(e);
                } catch (NoSuchTableException e) {
                        throw new DriverException(e);
                }
        }

        /**
         * Queries the index of the specified source, with the specified query
         *
         * @param src  the source
         * @param indexQuery a query to execute against the source
         * @return The iterator or null if there is no index in the specified field
         * @throws IndexException
         * @throws NoSuchTableException
         * @throws IndexQueryException 
         */
        public int[] queryIndex(DataSet src, IndexQuery indexQuery)
                throws IndexException, NoSuchTableException, IndexQueryException {
                return queryIndex(src, indexQuery, null);
        }

        /**
         * Queries the index of the specified source, with the specified query  and the specified visitor.
         * 
         * The visitor must have the correct type parameter (the type of the values being stored in the index).
         *
         * @param src  the source
         * @param indexQuery a query to execute against the source
         * @param visitor 
         * @return The iterator or null if there is no index in the specified field
         * @throws IndexException
         * @throws NoSuchTableException
         * @throws IndexQueryException 
         */
        public int[] queryIndex(DataSet src, IndexQuery indexQuery, IndexVisitor<?> visitor)
                throws IndexException, NoSuchTableException, IndexQueryException {
                DataSourceIndex dsi;
                if (src instanceof DataSource) {
                        DataSource ds = (DataSource) src;
                        return queryIndex(ds.getName(), indexQuery);
                }

                dsi = getIndex(TEMPINDEXPREFIX + src.hashCode(), indexQuery.getFieldName());
                if (dsi != null) {
                        if (!dsi.isOpen()) {
                                dsi.load();
                        }
                        return dsi.getIterator(indexQuery, visitor);
                } else {
                        return null;
                }
        }

        /**
         * Gets the field names of the given table that have an index on them.
         * @param name a table name
         * @return a (possibly empty) array of field names
         */
        public String[] getIndexedFieldNames(String name) {
                String[] indexProperties = getIndexProperties(name);
                String[] ret = new String[indexProperties.length];
                for (int i = 0; i < ret.length; i++) {
                        ret[0] = getFieldName(indexProperties[i]);
                }

                return ret;
        }

        /**
         * Removes the index for the source. All the current DataSource instances
         * are affected
         *
         * @param dsName the name of the source
         * @param fieldName the name of the indexed field
         * @throws NoSuchTableException 
         * @throws IllegalArgumentException
         *             If the index doesn't exist
         * @throws IndexException
         */
        public void deleteIndex(String dsName, String fieldName)
                throws NoSuchTableException, IndexException {
                try {
                        dsName = dsf.getSourceManager().getMainNameFor(dsName);
                        String[] indexProperties = getIndexProperties(dsName);
                        for (String indexProperty : indexProperties) {
                                if (indexProperty.startsWith(INDEX_PROPERTY_PREFIX + "-"
                                        + fieldName + "-")) {
                                        Source src = dsf.getSourceManager().getSource(dsName);
                                        src.deleteProperty(indexProperty);
                                        IndexDefinition def = new IndexDefinition(dsName, fieldName);
                                        indexCache.remove(def);
                                        fireIndexDeleted(dsName, fieldName,
                                                getIndexId(indexProperty));
                                        return;
                                }
                        }
                        throw new IllegalArgumentException(dsName + " does not have "
                                + "an index on the field'" + fieldName + "'");
                } catch (IOException e) {
                        throw new IndexException("Cannot remove index property of "
                                + dsName + " at field " + fieldName, e);
                }
        }

        /**
         * Removes the index for the source. All the current DataSource instances
         * are affected.
         *
         * @param src the source
         * @param fieldName the name of the indexed field
         * @throws NoSuchTableException 
         * @throws IllegalArgumentException
         *             If the index doesn't exist
         * @throws IndexException
         */
        public void deleteIndex(DataSet src, String fieldName)
                throws NoSuchTableException, IndexException {
                if (src instanceof DataSource) {
                        DataSource ds = (DataSource) src;
                        deleteIndex(ds.getName(), fieldName);
                } else {
                        deleteIndex(TEMPINDEXPREFIX + src.hashCode(), fieldName);
                }
        }

        /**
         * Gets an ad-hoc index on a field of a table of a MemoryDriver.
         * @param rightSource a MemoryDriver
         * @param tableName the name of the table of the driver
         * @param fieldName the name of the field to index
         * @param indexId the id (type) of the index 
         * @param pm an optional ProgressMonitor
         * @return the index
         * @throws IndexException if there is an error building the index
         */
        public AdHocIndex getAdHocIndex(MemoryDriver rightSource, String tableName, String fieldName,
                String indexId, ProgressMonitor pm) throws IndexException {
                if (pm == null) {
                        pm = new NullProgressMonitor();
                }
                DataSourceIndex index = instantiateIndex(indexId);

                try {
                        DataSource ds = dsf.getDataSource(rightSource, tableName,
                                DataSourceFactory.NORMAL);
                        index.setFieldName(fieldName);
                        ds.open();
                        index.buildIndex(dsf, ds, pm);
                        ds.close();
                        return index;
                } catch (DriverLoadException e) {
                        throw new IndexException("Cannot read source", e);
                } catch (IncompatibleTypesException e) {
                        throw new IndexException("Cannot create an "
                                + "index with that field type: " + fieldName, e);
                } catch (DriverException e) {
                        throw new IndexException("Cannot access data to index", e);
                }

        }

        /**
         * Returns true if there is an index on the specified field.
         * @param sourceName the name of the source
         * @param fieldName the name of the field
         * @return true if there is an index, false otherwise.
         * @throws NoSuchTableException
         */
        public boolean isIndexed(String sourceName, String fieldName)
                throws NoSuchTableException {
                sourceName = dsf.getSourceManager().getMainNameFor(sourceName);
                String[] indexProperties = getIndexProperties(sourceName);
                for (String indexProperty : indexProperties) {
                        if (indexProperty.startsWith(INDEX_PROPERTY_PREFIX + "-"
                                + fieldName + "-")) {
                                return true;
                        }
                }
                return false;
        }

        /**
         * Returns true if there is an index of the specified field of this readable source.
         * @param src a data set
         * @param fieldName a field name
         * @return true if there is an index on the data set.
         */
        public boolean isIndexed(DataSet src, String fieldName) {
                try {
                        if (src instanceof DataSource) {
                                DataSource ds = (DataSource) src;
                                return isIndexed(ds.getName(), fieldName);
                        } else {
                                return isIndexed(TEMPINDEXPREFIX + src.hashCode(), fieldName);
                        }
                } catch (NoSuchTableException ex) {
                        return false;
                }
        }
}
