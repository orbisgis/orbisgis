/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.coremap.renderer;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.SpatialResultSet;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.corejdbc.MetaData;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.commons.progress.ProgressMonitor;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * Standard select * from mytable query without cache or index.
 * @author Nicolas Fortin
 */
@Component
public class DefaultResultSetProviderFactory implements ResultSetProviderFactory {
    private static final int FETCH_SIZE = 300;
    private static final I18n I18N = I18nFactory.getI18n(DefaultResultSetProviderFactory.class);

    @Override
    public ResultSetProvider getResultSetProvider(ILayer layer, ProgressMonitor pm) {
        return new DefaultResultSetProvider(layer.getDataManager().getDataSource(), layer);
    }

    @Override
    public String getName() {
        return "Remote index";
    }

    private static class DefaultResultSetProvider implements ResultSetProvider {
        private DataSource dataSource;
        private ILayer layer;

        private Connection connection;
        private PreparedStatement st;
        private PropertyChangeListener cancelListener;
        private ProgressMonitor pm;
        private static final Logger LOGGER = LoggerFactory.getLogger(DefaultResultSetProvider.class);
        private String pkName = "";

        private DefaultResultSetProvider(DataSource dataSource, ILayer layer) {
            this.dataSource = dataSource;
            this.layer = layer;
            try(Connection conn = dataSource.getConnection()) {
                pkName = MetaData.getPkName(conn, layer.getTableReference(), true);
            } catch (SQLException ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
            }
        }

        @Override
        public String getPkName() {
            return pkName;
        }

        @Override
        public SpatialResultSet execute(ProgressMonitor pm, Envelope extent, Set<String> fields) throws SQLException {
            this.pm = pm;
            connection = dataSource.getConnection();
            List<String> geometryFields = SFSUtilities.getGeometryFields(connection, TableLocation.parse(layer.getTableReference()));
            if(geometryFields.isEmpty()) {
                throw new SQLException(I18N.tr("Table {0} does not contains geometry fields",layer.getTableReference()));
            }
            st = createStatement(connection, geometryFields.get(0), layer.getTableReference(), fields);
            st.setFetchSize(FETCH_SIZE);
            st.setFetchDirection(ResultSet.FETCH_FORWARD);
            connection.setAutoCommit(false);
            cancelListener = EventHandler.create(PropertyChangeListener.class, st, "cancel");
            pm.addPropertyChangeListener(ProgressMonitor.PROP_CANCEL, cancelListener);
            GeometryFactory geometryFactory = new GeometryFactory();
            if(st.getParameterMetaData().getParameterCount() > 0) {
                st.setObject(1, geometryFactory.toGeometry(extent)); // filter geometry by envelope
            }
            return st.executeQuery().unwrap(SpatialResultSet.class);
        }

        /**
         * Build a preparedstatement using 
         * @param connection
         * @param geometryField the first geometryfield
         * @param tableReference the name of the input table
         * @param fields a list of columns
         * @return
         * @throws SQLException 
         */
        private PreparedStatement createStatement(Connection connection,String geometryField,String tableReference, Set<String> fields) throws SQLException {            
            if(fields.isEmpty()){            
            return connection.prepareStatement(
                        String.format("select "+pkName+",%s from %s where %s && ?", geometryField,tableReference, geometryField),
                        ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            }
            else{
                StringBuilder sb = new StringBuilder("select ").append(pkName).append(",");
                
                for (String field : fields) {
                    sb.append(field).append(",");
                    
                }
                if(fields.contains(geometryField)){
                sb.append(" from ").append(tableReference).append(" where ").append(geometryField).append(" && ?");
                }
                else{
                sb.append(geometryField).append(" from ").append(tableReference).append(" where ").append(geometryField).append(" && ?");
                }
                return connection.prepareStatement(sb.toString());
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
