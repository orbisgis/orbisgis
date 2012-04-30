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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.utils.FileUtils;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.DriverException;

/**
 * Manages the edition of the indexes of a DataSource.
 * 
 * @author Antoine Gourlay
 * @author Fernando Gonzalez Cortes
 */
public class IndexEditionManager {

        private DataSource ds;
        private IndexManager im;
        private List<DataSourceIndex> modifiedIndexes = null;
        private IndexManagerListener indexManagerListener;

        /**
         * Creates an IndexEditionManager linked to the specified DataSourceFactory and on
         * the given DataSource.
         * @param dsf a DataSourceFactory
         * @param ds a DataSource
         */
        public IndexEditionManager(DataSourceFactory dsf, DataSource ds) {
                this.im = dsf.getIndexManager();
                this.ds = ds;
        }

        /**
         * Opens the IndexEditionManager.
         */
        public void open() {
                modifiedIndexes = null;
        }

        /**
         * Triggers a commit on the indexes (usually during/after a commit on the DataSource).
         * 
         * If <code>rebuildIndexes</code> is set to true, the indexes are deleted and rebuild. If not,
         * the IndexManager is notified of the change on the modified indexes and handles what to do.
         * 
         * @param rebuildIndexes true if indexes have to be rebuilt.
         * @throws DriverException
         */
        public void commit(boolean rebuildIndexes) throws DriverException {
                if (modifiedIndexes != null) {
                        if (rebuildIndexes) {
                                String[][] indexedFieldNames = new String[modifiedIndexes.size()][];
                                for (int i = 0; i < modifiedIndexes.size(); i++) {
                                        indexedFieldNames[i] = modifiedIndexes.get(i).getFieldNames();
                                }
                                for (String[] indexFieldNames : indexedFieldNames) {
                                        try {
                                                im.deleteIndex(ds.getName(), indexFieldNames);
                                                im.buildIndex(ds.getName(), indexFieldNames,
                                                        new NullProgressMonitor());
                                        } catch (NoSuchTableException e) {
                                                throw new DriverException("Cannot rebuild index", e);
                                        } catch (IndexException e) {
                                                throw new DriverException("Cannot rebuild index", e);
                                        }
                                }
                        } else {
                                try {
                                        im.indexesChanged(ds.getName(), modifiedIndexes.toArray(new DataSourceIndex[modifiedIndexes.size()]));
                                } catch (IOException e) {
                                        throw new DriverException("Cannot replace index content", e);
                                }
                        }
                }
                cancel();
        }

        /**
         * Cancels all changes on the indexes.
         */
        public void cancel() {
                if (modifiedIndexes != null) {
                        for (int i = 0; i < modifiedIndexes.size(); i++) {
                                modifiedIndexes.get(i).getFile().delete();

                        }
                        modifiedIndexes = null;
                }
                if (im != null) {
                        im.removeIndexManagerListener(indexManagerListener);
                }
        }

        /**
         * Gets the indexes (either modified or original) on the DataSource.
         * @return a (possibly empty) array of indexes.
         * @throws IndexException
         */
        public DataSourceIndex[] getDataSourceIndexes() throws IndexException {
                if (ds.isModified()) {
                        return getModifiedIndexes();
                } else {
                        try {
                                return im.getIndexes(ds.getName());
                        } catch (NoSuchTableException e) {
                                throw new IndexException(e);
                        }
                }
        }

        private DataSourceIndex[] getModifiedIndexes() throws IndexException {
                if (modifiedIndexes == null) {
                        try {
                                DataSourceIndex[] toClone = im.getIndexes(ds.getName());
                                modifiedIndexes = new ArrayList<DataSourceIndex>();
                                for (int i = 0; i < toClone.length; i++) {
                                        if (toClone[i].isOpen()) {
                                                toClone[i].save();
                                                toClone[i].close();
                                        }
                                        File indexFile = toClone[i].getFile();
                                        File copied = new File(ds.getDataSourceFactory().getTempFile());
                                        FileUtils.copy(indexFile, copied);
                                        DataSourceIndex cloned = toClone[i].getClass().newInstance();
                                        cloned.setFieldNames(toClone[i].getFieldNames());
                                        cloned.setFile(copied);
                                        cloned.load();
                                        toClone[i].load();
                                        modifiedIndexes.add(cloned);
                                }
                        } catch (NoSuchTableException e) {
                                throw new IndexException(e);
                        } catch (IOException e) {
                                throw new IndexException("Cannot duplicate index file", e);
                        } catch (InstantiationException e) {
                                throw new IndexException("Cannot instanciate cloned index", e);
                        } catch (IllegalAccessException e) {
                                throw new IndexException(e);
                        }
                        indexManagerListener = new IndexManagerListener() {

                                @Override
                                public void indexCreated(String source, String[] fields,
                                        String indexId, IndexManager im, ProgressMonitor pm)
                                        throws IndexException {
                                        try {
                                                if (source.equals(ds.getDataSourceFactory().getSourceManager().getMainNameFor(ds.getName()))) {
                                                        addIndexWithDataInEdition(fields, indexId, im, pm);
                                                }
                                        } catch (NoSuchTableException e) {
                                                throw new IndexException(e);
                                        }
                                }

                                @Override
                                public void indexDeleted(String source, String[] fields,
                                        String indexId, IndexManager im) throws IndexException {
                                        try {
                                                if (source.equals(ds.getDataSourceFactory().getSourceManager().getMainNameFor(ds.getName()))) {
                                                        DataSourceIndex toDelete = null;
                                                        for (DataSourceIndex index : modifiedIndexes) {
                                                                if (Arrays.equals(index.getFieldNames(), fields)) {
                                                                        toDelete = index;
                                                                        break;
                                                                }
                                                        }
                                                        modifiedIndexes.remove(toDelete);
                                                }
                                        } catch (NoSuchTableException e) {
                                                throw new IndexException(e);
                                        }
                                }
                        };
                        im.addIndexManagerListener(indexManagerListener);
                }

                return modifiedIndexes.toArray(new DataSourceIndex[modifiedIndexes.size()]);
        }

        private void addIndexWithDataInEdition(String[] fieldNames, String indexId,
                IndexManager im, ProgressMonitor pm) throws IndexException {
                DataSourceIndex index = im.instantiateIndex(indexId);
                index.setFile(new File(ds.getDataSourceFactory().getTempFile()));
                index.setFieldNames(fieldNames);
                index.buildIndex(ds.getDataSourceFactory(), ds, pm);
                index.save();
                modifiedIndexes.add(index);
        }

        /**
         * Query the modified (and/or original) indexes on the dataSource.
         * @param indexQuery an IndexQuery
         * @return a (possibly empty) array of row indexes, or null if there is no index to query.
         * @throws IndexException
         * @throws IndexQueryException
         */
        public int[] query(IndexQuery indexQuery) throws IndexException, IndexQueryException {
                DataSourceIndex[] indexes = getDataSourceIndexes();
                for (DataSourceIndex dataSourceIndex : indexes) {
                        if (Arrays.equals(dataSourceIndex.getFieldNames(), indexQuery.getFieldNames())) {
                                return dataSourceIndex.query(indexQuery);
                        }
                }

                return null;
        }
}
