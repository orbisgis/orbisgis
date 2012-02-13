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
