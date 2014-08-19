/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.mapeditor.map;

import com.vividsolutions.jts.geom.Envelope;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.SpatialResultSet;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.corejdbc.ReadRowSet;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.renderer.ResultSetProviderFactory;
import org.orbisgis.mapeditorapi.Index;
import org.orbisgis.mapeditorapi.IndexProvider;
import org.orbisgis.progress.ProgressMonitor;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Use and keep ReadRowSet instance instead of native ResultSet.
 * @author Nicolas Fortin
 */
public class CachedResultSetContainer implements ResultSetProviderFactory {
    private final Map<String, ReadRowSet> cache = new HashMap<>();
    private static final int LOCK_TIMEOUT = 10;
    private static I18n I18N = I18nFactory.getI18n(CachedResultSetContainer.class);
    private static final int ROWSET_FREE_DELAY = 60000;
    private IndexProvider indexProvider;
    private Map<String, Index<Integer>> indexMap = new HashMap<>();

    @Override
    public CachedResultSet getResultSetProvider(ILayer layer, ProgressMonitor pm) throws SQLException {
        ReadRowSet readRowSet = cache.get(layer.getTableReference());
        if(readRowSet != null) {
            return new CachedResultSet(readRowSet, layer.getTableReference());
        } else {
            readRowSet = layer.getDataManager().createReadRowSet();
            readRowSet.setCloseDelay(ROWSET_FREE_DELAY);
            readRowSet.initialize(layer.getTableReference(), "", pm);
            String tableRef;
            try (Connection connection = layer.getDataManager().getDataSource().getConnection()) {
                boolean isH2 =  JDBCUtilities.isH2DataBase(connection.getMetaData());
                tableRef = TableLocation.parse(layer.getTableReference(), isH2).toString(isH2);
            }
            cache.put(tableRef, readRowSet);
            return new CachedResultSet(readRowSet, layer.getTableReference());
        }
    }

    /**
     * Provide spatial query optimisation
     * @param indexProvider Index factory
     */
    public void setIndexProvider(IndexProvider indexProvider) {
        if(this.indexProvider != null) {
            clearGeometryIndex();
        }
        this.indexProvider = indexProvider;
    }

    private void clearGeometryIndex() {
        for(Index index : indexMap.values()) {
            try {
                index.close();
            } catch (Exception ex) {
                // Ignore
            }
        }
        indexMap.clear();

    }

    public void clearCache() {
        clearGeometryIndex();
        for(ReadRowSet rowSet : cache.values()) {
            rowSet.setCloseDelay(0);
        }
        cache.clear();
    }

    /**
     * Remove cached ResultSet
     * @param tableReference table identifier
     */
    public void removeCache(String tableReference) {
        if(!cache.containsKey(tableReference)) {
            // Try with removing public schema
            if(TableLocation.parse(tableReference).getSchema().equalsIgnoreCase("public")) {
                tableReference = TableLocation.parse(tableReference).getTable();
            }
        }
        ReadRowSet removedCache = cache.remove(tableReference);
        Index index = indexMap.remove(tableReference);
        if(removedCache != null) {
            removedCache.setCloseDelay(0);
        }
        if(index != null) {
            try {
                index.close();
            } catch (Exception ex) {
                // Ignore
            }
        }
    }

    private static class CachedResultSet implements ResultSetProvider {
        ReadRowSet readRowSet;
        String tableReference;
        Lock lock;

        private CachedResultSet(ReadRowSet readRowSet, String tableReference) {
            this.readRowSet = readRowSet;
            this.tableReference = tableReference;
        }

        @Override
            public SpatialResultSet execute(ProgressMonitor pm, Envelope extent) throws SQLException {
            lock = readRowSet.getReadLock();
            try {
                lock.tryLock(LOCK_TIMEOUT, TimeUnit.SECONDS);
                readRowSet.beforeFirst();
                return readRowSet;
            } catch (InterruptedException ex) {
                throw new SQLException(I18N.tr("Lock timeout while fetching {0}, another job is using this resource.",
                        tableReference));
            }
        }

        @Override
        public void close() throws SQLException {
            if(lock != null) {
                lock.unlock();
            }
        }
    }
}
