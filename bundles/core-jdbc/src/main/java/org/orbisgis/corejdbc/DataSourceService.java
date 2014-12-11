/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.corejdbc;

import org.h2gis.utilities.JDBCUrlParser;
import org.h2gis.utilities.SFSUtilities;
import org.orbisgis.frameworkapi.CoreWorkspace;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.jdbc.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import javax.swing.SwingWorker;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * Using CoreWorkspace to declare the DataSource service in Declarative Service framework.
 * @author Nicolas Fortin
 */
@Component
public class DataSourceService implements DataSource {
    private DataSource dataSource;
    private CoreWorkspace coreWorkspace;
    private Map<String, DataSourceFactory> dataSourceFactories = new HashMap<>();
    private static final Map<String,String> URI_DRIVER_TO_OSGI_DRIVER = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceService.class);
    private static final I18n I18N = I18nFactory.getI18n(DataSourceService.class);
    static {
        URI_DRIVER_TO_OSGI_DRIVER.put("h2","H2 JDBC Driver");
        URI_DRIVER_TO_OSGI_DRIVER.put("postgresql","Postgresql");
    }

    /**
     * @param coreWorkspace CoreWorkspace with valid connection
     * @throws SQLException If the DataSource could not be created
     */
    @Reference
    public void setCoreWorkspace(CoreWorkspace coreWorkspace) throws SQLException {
        this.coreWorkspace = coreWorkspace;
    }

    /**
     * Create internal datasource using {@link #setCoreWorkspace(org.orbisgis.frameworkapi.CoreWorkspace)} and {@link
     * #addDataSourceFactory(org.osgi.service.jdbc.DataSourceFactory, java.util.Map)}
     */
    @Activate
    public void activate() {
        try {
            // Build DataSource
            newDataSource();
        } catch (SQLException ex) {
            throw new IllegalStateException(ex.getLocalizedMessage(), ex);
        }
    }

    private void newDataSource() throws SQLException {
        String jdbcConnectionReference = coreWorkspace.getJDBCConnectionReference();
        if(!jdbcConnectionReference.isEmpty()) {
            Properties properties = JDBCUrlParser.parse(jdbcConnectionReference);
            String driverName = jdbcConnectionReference.split(":")[1];
            // Fetch requested Driver
            DataSourceFactory dataSourceFactory = dataSourceFactories.get(URI_DRIVER_TO_OSGI_DRIVER.get(driverName));
            if(dataSourceFactory != null) {
                dataSource = SFSUtilities.wrapSpatialDataSource(dataSourceFactory.createDataSource(properties));
            } else {
                throw new SQLException(String.format("The database driver %s is not available",driverName));
            }
            // Print DataSource infos
            new PrintDriverVersion(dataSource).execute();
        } else {
            throw new SQLException("DataBase path not found");
        }
    }

    public void unsetCoreWorkspace(CoreWorkspace coreWorkspace) {
        coreWorkspace = null;
    }

    /**
     * @param dataSourceFactory DataSourceFactory instance
     * @param serviceProperties Must contain DataSourceFactory.OSGI_JDBC_DRIVER_NAME entry.
     */
    @Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE, policy = ReferencePolicy.DYNAMIC)
    public void addDataSourceFactory(DataSourceFactory dataSourceFactory, Map<String,String> serviceProperties) {
        dataSourceFactories.put(serviceProperties.get(DataSourceFactory.OSGI_JDBC_DRIVER_NAME), dataSourceFactory);
    }

    /**
     * @param dataSourceFactory DataSourceFactory instance
     * @param serviceProperties Must contain DataSourceFactory.OSGI_JDBC_DRIVER_NAME entry.
     */
    public void removeDataSourceFactory(DataSourceFactory dataSourceFactory, Map<String,String> serviceProperties) {
        dataSourceFactories.remove(serviceProperties.get(DataSourceFactory.OSGI_JDBC_DRIVER_NAME));
    }


    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return dataSource.getConnection(username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return dataSource.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        dataSource.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        dataSource.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return dataSource.getLoginTimeout();
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return dataSource.getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return dataSource.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return dataSource.isWrapperFor(iface);
    }

    private static class PrintDriverVersion extends SwingWorker<String, String> {
        DataSource dataSource;

        public PrintDriverVersion(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        protected String doInBackground() throws SQLException {
            try(Connection connection = dataSource.getConnection()) {
                DatabaseMetaData meta = connection.getMetaData();
                return I18N.tr("Data source available {0} version {1}", meta.getDriverName(), meta.getDriverVersion());
            }
        }

        @Override
        protected void done() {
            try {
                LOGGER.info(get());
            } catch (Exception ex) {
                // Ignore
            }
        }
    }
}
