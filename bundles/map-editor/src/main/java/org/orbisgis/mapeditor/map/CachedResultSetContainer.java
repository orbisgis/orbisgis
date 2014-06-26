package org.orbisgis.mapeditor.map;

import com.vividsolutions.jts.geom.Envelope;
import org.h2gis.utilities.SpatialResultSet;
import org.orbisgis.corejdbc.MetaData;
import org.orbisgis.corejdbc.ReadRowSet;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.renderer.ResultSetProviderFactory;
import org.orbisgis.progress.NullProgressMonitor;
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

    @Override
    public CachedResultSet getResultSetProvider(ILayer layer, ProgressMonitor pm) throws SQLException {
        ReadRowSet readRowSet = cache.get(layer.getTableReference());
        if(readRowSet != null) {
            return new CachedResultSet(readRowSet, layer.getTableReference());
        } else {
            readRowSet = layer.getDataManager().createReadRowSet();
            try(Connection connection = layer.getDataManager().getDataSource().getConnection()) {
                String pkName = MetaData.getPkName(connection, layer.getTableReference(), true);
                readRowSet.initialize(layer.getTableReference(), pkName, pm);
                cache.put(layer.getTableReference(), readRowSet);
                return new CachedResultSet(readRowSet, layer.getTableReference());
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
