/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
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

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 *
 * @author alexis
 */


public class FooResultSetMetadata implements ResultSetMetaData{

        @Override
        public int getColumnCount() throws SQLException {
                return 0;
        }

        @Override
        public boolean isAutoIncrement(int column) throws SQLException {
                return false;
        }

        @Override
        public boolean isCaseSensitive(int column) throws SQLException {
                return false;
        }

        @Override
        public boolean isSearchable(int column) throws SQLException {
                return false;
        }

        @Override
        public boolean isCurrency(int column) throws SQLException {
                return false;
        }

        @Override
        public int isNullable(int column) throws SQLException {
                return -1;
        }

        @Override
        public boolean isSigned(int column) throws SQLException {
                return false;
        }

        @Override
        public int getColumnDisplaySize(int column) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getColumnLabel(int column) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getColumnName(int column) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getSchemaName(int column) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getPrecision(int column) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getScale(int column) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getTableName(int column) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getCatalogName(int column) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getColumnType(int column) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getColumnTypeName(int column) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isReadOnly(int column) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isWritable(int column) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isDefinitelyWritable(int column) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getColumnClassName(int column) throws SQLException {
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
