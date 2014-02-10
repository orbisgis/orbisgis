/**
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
package org.orbisgis.postgis_jts_osgi;

import org.osgi.service.jdbc.DataSourceFactory;
import org.postgresql.ds.PGPoolingDataSource;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;

public class DataSourceFactoryImpl implements DataSourceFactory {
    @Override
    public DataSource createDataSource(Properties properties) throws SQLException {
        if (properties == null) {
            properties = new Properties();
        }
        PGPoolingDataSource dataSource = new PGPoolingDataSource();
        // Set dataSourceName, databaseName, user, and password
        dataSource.setDataSourceName(properties.getProperty(JDBC_DATASOURCE_NAME));
        String url = properties.getProperty(JDBC_URL);
        if(url != null) {
            dataSource.setUrl(url);
        }
        dataSource.setPortNumber(Integer.valueOf(properties.getProperty(JDBC_PORT_NUMBER, Integer.toString(dataSource.getPortNumber()))));
        dataSource.setServerName(properties.getProperty(JDBC_SERVER_NAME, dataSource.getServerName()));
        dataSource.setUser(properties.getProperty(JDBC_USER, dataSource.getUser()));
        dataSource.setPassword(properties.getProperty(JDBC_PASSWORD, dataSource.getPassword()));
        dataSource.setDatabaseName(properties.getProperty(JDBC_DATABASE_NAME, dataSource.getDatabaseName()));
        return new DataSourceWrapper(dataSource);
    }

    @Override
    public ConnectionPoolDataSource createConnectionPoolDataSource(Properties properties) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public XADataSource createXADataSource(Properties properties) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public Driver createDriver(Properties properties) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
}