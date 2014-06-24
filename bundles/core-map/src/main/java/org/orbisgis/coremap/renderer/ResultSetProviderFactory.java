package org.orbisgis.coremap.renderer;

import com.vividsolutions.jts.geom.Envelope;
import org.h2gis.utilities.SpatialResultSet;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.progress.ProgressMonitor;

import java.sql.SQLException;

/**
 * In order to split-up Data query and rendering. The renderer use this interface to query the database.
 * @author Nicolas Fortin
 */
public interface ResultSetProviderFactory {

    /**
     * @param layer Layer to be requested
     * @return Object that query the database.
     */
    ResultSetProvider getResultSetProvider(ILayer layer);

    /**
     * Object that query the database.
     */
    public interface ResultSetProvider extends AutoCloseable {
        /**
         * The returned result set may preserve the {@link java.sql.ResultSet#getRow()} of the entire table without
         * filtering.
         * @param extent Filter entities by this envelope
         * @return The content of the table
         */
        SpatialResultSet execute(ProgressMonitor pm, Envelope extent) throws SQLException;

        @Override
        void close() throws SQLException;
    }
}
