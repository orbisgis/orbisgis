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
package org.orbisgis.postgis_jts_osgi;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Wrap metadata for geometry related columns.
 * @author Nicolas Fortin
 */
public class ResultSetMetaDataWrapper implements ResultSetMetaData {
    private ResultSetMetaData resultSetMetaData;

    public ResultSetMetaDataWrapper(ResultSetMetaData resultSetMetaData) {
        this.resultSetMetaData = resultSetMetaData;
    }

    @Override
    public int getColumnCount() throws SQLException {
        return resultSetMetaData.getColumnCount();
    }

    @Override
    public boolean isAutoIncrement(int i) throws SQLException {
        return resultSetMetaData.isAutoIncrement(i);
    }

    @Override
    public boolean isCaseSensitive(int i) throws SQLException {
        return resultSetMetaData.isCaseSensitive(i);
    }

    @Override
    public boolean isSearchable(int i) throws SQLException {
        return resultSetMetaData.isSearchable(i);
    }

    @Override
    public boolean isCurrency(int i) throws SQLException {
        return resultSetMetaData.isCurrency(i);
    }

    @Override
    public int isNullable(int i) throws SQLException {
        return resultSetMetaData.isNullable(i);
    }

    @Override
    public boolean isSigned(int i) throws SQLException {
        return resultSetMetaData.isSigned(i);
    }

    @Override
    public int getColumnDisplaySize(int i) throws SQLException {
        return resultSetMetaData.getColumnDisplaySize(i);
    }

    @Override
    public String getColumnLabel(int i) throws SQLException {
        return resultSetMetaData.getColumnLabel(i);
    }

    @Override
    public String getColumnName(int i) throws SQLException {
        return resultSetMetaData.getColumnName(i);
    }

    @Override
    public String getSchemaName(int i) throws SQLException {
        return resultSetMetaData.getSchemaName(i);
    }

    @Override
    public int getPrecision(int i) throws SQLException {
        return resultSetMetaData.getPrecision(i);
    }

    @Override
    public int getScale(int i) throws SQLException {
        return resultSetMetaData.getScale(i);
    }

    @Override
    public String getTableName(int i) throws SQLException {
        return resultSetMetaData.getTableName(i);
    }

    @Override
    public String getCatalogName(int i) throws SQLException {
        return resultSetMetaData.getCatalogName(i);
    }

    @Override
    public int getColumnType(int i) throws SQLException {
        return resultSetMetaData.getColumnType(i);
    }

    @Override
    public String getColumnTypeName(int i) throws SQLException {
        String typeName = resultSetMetaData.getColumnTypeName(i);
        if(ResultSetWrapper.GEOMETRY_COLUMNS.contains(typeName)) {
            return "GEOMETRY";
        } else {
            return typeName;
        }
    }

    @Override
    public boolean isReadOnly(int i) throws SQLException {
        return resultSetMetaData.isReadOnly(i);
    }

    @Override
    public boolean isWritable(int i) throws SQLException {
        return resultSetMetaData.isWritable(i);
    }

    @Override
    public boolean isDefinitelyWritable(int i) throws SQLException {
        return resultSetMetaData.isDefinitelyWritable(i);
    }

    @Override
    public String getColumnClassName(int i) throws SQLException {
        return resultSetMetaData.getColumnClassName(i);
    }


    @Override
    public <T> T unwrap(Class<T> tClass) throws SQLException {
        if(tClass.isAssignableFrom(this.getClass())) {
            return tClass.cast(this);
        } else {
            return resultSetMetaData.unwrap(tClass);
        }
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return aClass.isAssignableFrom(this.getClass()) || resultSetMetaData.isWrapperFor(aClass);
    }
}
