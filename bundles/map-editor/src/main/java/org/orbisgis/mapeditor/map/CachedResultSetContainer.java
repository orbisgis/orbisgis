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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.SpatialResultSet;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.corejdbc.MetaData;
import org.orbisgis.corejdbc.ReadRowSet;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.renderer.DefaultResultSetProviderFactory;
import org.orbisgis.coremap.renderer.ResultSetProviderFactory;
import org.orbisgis.progress.ProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Use and keep ReadRowSet instance instead of native ResultSet.
 * @author Nicolas Fortin
 */
public class CachedResultSetContainer implements ResultSetProviderFactory {
    private final Map<String, ReadRowSet> cache = new HashMap<>();
    private static final int LOCK_TIMEOUT = 10;
    private static final int FETCH_SIZE = 50;
    private static I18n I18N = I18nFactory.getI18n(CachedResultSetContainer.class);
    private static Logger LOGGER = LoggerFactory.getLogger(CachedResultSetContainer.class);
    private static final int ROWSET_FREE_DELAY = 60000;
    private static final long WAIT_FOR_INITIALISATION_TIMEOUT = 10000;
    // (0-1] Use spatial index query if the query envelope area rational number is smaller than this value.
    private static final double RATIONAL_USAGE_INDEX = 0.5;
    private final ReentrantLock lock = new ReentrantLock();
    private ResultSetProviderFactory defaultFactory = new DefaultResultSetProviderFactory();

    @Override
    public String getName() {
        return "Local index";
    }

    @Override
    public ResultSetProvider getResultSetProvider(ILayer layer, ProgressMonitor pm) throws SQLException {
        try {
            if(lock.tryLock(WAIT_FOR_INITIALISATION_TIMEOUT, TimeUnit.MILLISECONDS)) {
                boolean isH2;
                String integerPK = ""; // Not system PK
                String tableRef;
                try (Connection connection = layer.getDataManager().getDataSource().getConnection()) {
                    isH2 = JDBCUtilities.isH2DataBase(connection.getMetaData());
                    tableRef = TableLocation.parse(layer.getTableReference(), isH2).toString(isH2);
                    integerPK = MetaData.getPkName(connection, tableRef, false);
                }
                if(!isH2) {
                    // Always use cursor with PostGIS
                    return defaultFactory.getResultSetProvider(layer, pm);
                }
                ReadRowSet readRowSet = cache.get(tableRef);
                ResultSetProvider defaultResultSetProvider = defaultFactory.getResultSetProvider(layer, pm);
                if (readRowSet == null) {
                    readRowSet = layer.getDataManager().createReadRowSet();
                    // If the used PK is hidden (because it is system pk)
                    if(integerPK.isEmpty()) {
                        readRowSet.setCommand("SELECT " + defaultResultSetProvider.getPkName() + ", * FROM ");
                    }
                    readRowSet.setFetchSize(FETCH_SIZE);
                    readRowSet.setCloseDelay(ROWSET_FREE_DELAY);
                    readRowSet.setFetchDirection(ResultSet.FETCH_FORWARD);
                    readRowSet.initialize(tableRef, "", pm);
                    cache.put(tableRef, readRowSet);
                }
                return new CachedResultSet(readRowSet, tableRef, layer.getEnvelope(),defaultResultSetProvider);
            } else {
                throw new SQLException("Cannot draw until layer data source is not initialized");
            }
        } catch (InterruptedException ex) {
            throw new SQLException("Cannot draw until layer data source is not initialized");
        } finally {
            lock.unlock();
        }
    }

    public void clearCache() {
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
        if(removedCache != null) {
            removedCache.setCloseDelay(0);
        }
    }

    private static class CachedResultSet implements ResultSetProvider {
        private ReadRowSet readRowSet;
        private String tableReference;
        private Lock lock;
        private Envelope tableEnvelope;
        private ResultSetProvider resultSetProvider;
        private String pkName;

        private CachedResultSet(ReadRowSet readRowSet, String tableReference, Envelope tableEnvelope, ResultSetProvider resultSetProvider) {
            this.readRowSet = readRowSet;
            this.tableReference = tableReference;
            this.tableEnvelope = tableEnvelope;
            this.resultSetProvider = resultSetProvider;
            this.pkName = resultSetProvider.getPkName();
        }

        @Override
        public String getPkName() {
            return pkName;
        }

        @Override
            public SpatialResultSet execute(ProgressMonitor pm, Envelope extent) throws SQLException {
            lock = readRowSet.getReadLock();
            try {
                lock.tryLock(LOCK_TIMEOUT, TimeUnit.SECONDS);
                // Do intersection of envelope
                double intersectionPercentage = extent.intersection(tableEnvelope).getArea() / tableEnvelope.getArea();
                // If there is quite no zoom is great use the "select * from table" cached query.
                if( intersectionPercentage > RATIONAL_USAGE_INDEX) {
                    readRowSet.beforeFirst();
                    return readRowSet;
                } else {
                    return resultSetProvider.execute(pm, extent);
                }
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
            resultSetProvider.close();
        }
    }
}
