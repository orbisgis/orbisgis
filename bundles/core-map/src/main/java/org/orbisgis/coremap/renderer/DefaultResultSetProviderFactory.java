package org.orbisgis.coremap.renderer;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.SpatialResultSet;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.progress.ProgressMonitor;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Standard select * from mytable query without cache or index.
 * @author Nicolas Fortin
 */
public class DefaultResultSetProviderFactory implements ResultSetProviderFactory {
    private DataSource dataSource;
    private static final int FETCH_SIZE = 300;
    private static final I18n I18N = I18nFactory.getI18n(DefaultResultSetProviderFactory.class);

    /**
     * Standard select * from mytable query without cache or index.
     * @param dataSource Connection data source
     */
    public DefaultResultSetProviderFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ResultSetProvider getResultSetProvider(ILayer layer) {
        return new DefaultResultSetProvider(dataSource, layer);
    }

    private static class DefaultResultSetProvider implements ResultSetProvider {
        private DataSource dataSource;
        private ILayer layer;

        private Connection connection;
        private PreparedStatement st;
        private PropertyChangeListener cancelListener;
        private ProgressMonitor pm;

        private DefaultResultSetProvider(DataSource dataSource, ILayer layer) {
            this.dataSource = dataSource;
            this.layer = layer;
        }

        @Override
        public SpatialResultSet execute(ProgressMonitor pm, Envelope extent) throws SQLException {
            this.pm = pm;
            connection = dataSource.getConnection();
            List<String> geometryFields = SFSUtilities.getGeometryFields(connection, TableLocation.parse(layer.getTableReference()));
            if(geometryFields.isEmpty()) {
                throw new SQLException(I18N.tr("Table {0} does not contains geometry fields",layer.getTableReference()));
            }
            st = createStatement(connection, geometryFields.get(0), layer.getTableReference(), !layer.getSelection().isEmpty());
            st.setFetchSize(FETCH_SIZE);
            cancelListener = EventHandler.create(PropertyChangeListener.class, st, "cancel");
            pm.addPropertyChangeListener(ProgressMonitor.PROP_CANCEL, cancelListener);
            GeometryFactory geometryFactory = new GeometryFactory();
            if(st.getParameterMetaData().getParameterCount() > 0) {
                st.setObject(1, geometryFactory.toGeometry(extent)); // Filter geometry by envelope
            }
            return st.executeQuery().unwrap(SpatialResultSet.class);
        }

        private PreparedStatement createStatement(Connection connection,String geometryField,String tableReference, boolean hasSelection) throws SQLException {
            if(!hasSelection) {
                return connection.prepareStatement(
                        String.format("select * from %s where %s && ?", tableReference, geometryField));
            } else {
                return  connection.prepareStatement(
                        String.format("select * from %s",tableReference));
            }
        }

        @Override
        public void close() throws SQLException {
            if(cancelListener != null) {
                pm.removePropertyChangeListener(cancelListener);
            }
            if(st != null) {
                st.close();
            }
            if(connection != null) {
                connection.close();
            }
        }
    }
}
