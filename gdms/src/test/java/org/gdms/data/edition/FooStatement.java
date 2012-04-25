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
package org.gdms.data.edition;

import java.sql.*;

/**
 *
 * @author alexis
 */


public class FooStatement implements Statement {

        @Override
        public ResultSet executeQuery(String sql) throws SQLException {
                return new FooEmptyResultSet("pk");
        }

        @Override
        public int executeUpdate(String sql) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void close() throws SQLException {
        }

        @Override
        public int getMaxFieldSize() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setMaxFieldSize(int max) throws SQLException {
        }

        @Override
        public int getMaxRows() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setMaxRows(int max) throws SQLException {
        }

        @Override
        public void setEscapeProcessing(boolean enable) throws SQLException {
        }

        @Override
        public int getQueryTimeout() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setQueryTimeout(int seconds) throws SQLException {
        }

        @Override
        public void cancel() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public SQLWarning getWarnings() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void clearWarnings() throws SQLException {
        }

        @Override
        public void setCursorName(String name) throws SQLException {
        }

        @Override
        public boolean execute(String sql) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public ResultSet getResultSet() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getUpdateCount() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean getMoreResults() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setFetchDirection(int direction) throws SQLException {
        }

        @Override
        public int getFetchDirection() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setFetchSize(int rows) throws SQLException {
        }

        @Override
        public int getFetchSize() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getResultSetConcurrency() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getResultSetType() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void addBatch(String sql) throws SQLException {
        }

        @Override
        public void clearBatch() throws SQLException {
        }

        @Override
        public int[] executeBatch() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Connection getConnection() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean getMoreResults(int current) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public ResultSet getGeneratedKeys() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int executeUpdate(String sql, String[] columnNames) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean execute(String sql, int[] columnIndexes) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean execute(String sql, String[] columnNames) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getResultSetHoldability() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isClosed() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setPoolable(boolean poolable) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isPoolable() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        public void closeOnCompletion() throws SQLException {
        }

        public boolean isCloseOnCompletion() throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

}
