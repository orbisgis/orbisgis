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
package org.orbisgis.postgis_jts_osgi;

import org.postgis.PGbox2d;
import org.postgis.PGbox3d;
import org.postgis.jts.JtsGeometry;
import org.postgresql.PGConnection;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * Configure Postgre connection to use PostGIS.
 * @author Nicolas Fortin
 */
public class DataSourceWrapper implements DataSource {
    private DataSource pgDataSource;

    /**
     * Constructor.
     * @param pgDataSource Instance of Postgre datasource
     */
    public DataSourceWrapper(DataSource pgDataSource) {
        this.pgDataSource = pgDataSource;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        pgDataSource.setLoginTimeout(seconds);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return configureConnection(pgDataSource.getConnection());
    }

    private Connection configureConnection(Connection connection) throws SQLException {
        if(connection instanceof PGConnection) {
            ((PGConnection) connection).addDataType("geometry", JtsGeometry.class);
            ((PGConnection) connection).addDataType("box3d", PGbox3d.class);
            ((PGConnection) connection).addDataType("box2d", PGbox2d.class);
        }
        return new ConnectionWrapper(connection);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return configureConnection(pgDataSource.getConnection(username, password));
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return pgDataSource.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        pgDataSource.setLogWriter(out);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return pgDataSource.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return pgDataSource.getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException("Unsupported operation");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
