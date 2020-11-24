/*
 * Bundle common-utils is part of the OrbisGIS platform
 *
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
 * OSM is distributed under LGPL 3 license.
 *
 * Copyright (C) 2020 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * OSM is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OSM is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * OSM. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.common_utils

import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.h2gis.utilities.TableLocation

import java.sql.Connection
import java.sql.SQLException

/**
 * Utility script used as extension module adding methods to Connection class.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */

static boolean execute(Connection connection, String sql) {
    return new Sql(connection).execute(sql)
}

static boolean execute(Connection connection, GString sql) {
    return new Sql(connection).execute(sql.toString())
}

static List<GroovyRowResult> rows(Connection connection, String sql) {
    return new Sql(connection).rows((String)sql)
}

static List<GroovyRowResult> rows(Connection connection, GString sql) {
    return new Sql(connection).rows(sql.toString())
}

static GroovyRowResult firstRow(Connection connection, String sql) {
    return new Sql(connection).firstRow(sql.toString())
}

static GroovyRowResult firstRow(Connection connection, GString sql) {
    return new Sql(connection).firstRow(sql.toString())
}

static GroovyRowResult firstRow(Connection connection, String sql, Object[] params) throws SQLException {
    return new Sql(connection).firstRow(sql, params);
}

static boolean isIndexed(Connection connection, TableLocation tableName, String columnName) {
    if (connection == null || columnName == null || tableName == null) {
        error("Unable to find an index")
        return false
    }
    if (tableName == null) {
        error("Unable to find an index on a query")
        return false
    }
    try {
        if (connection.isH2DataBase()) {
            Map<?, ?> map = connection.firstRow("SELECT INDEX_TYPE_NAME FROM INFORMATION_SCHEMA.INDEXES " +
                    "WHERE INFORMATION_SCHEMA.INDEXES.TABLE_NAME=? " +
                    "AND INFORMATION_SCHEMA.INDEXES.TABLE_SCHEMA=? " +
                    "AND INFORMATION_SCHEMA.INDEXES.COLUMN_NAME=?;",
                    new Object[]{tableName.getTable(), tableName.getSchema("PUBLIC"), columnName});
            return map != null
        } else {
            String query =  "SELECT  cls.relname, am.amname " +
                    "FROM  pg_class cls " +
                    "JOIN pg_am am ON am.oid=cls.relam where cls.oid " +
                    " in(select attrelid as pg_class_oid from pg_catalog.pg_attribute " +
                    " where attname = ? and attrelid in " +
                    "(select b.oid from pg_catalog.pg_indexes a, pg_catalog.pg_class b  where a.schemaname =? and a.tablename =? " +
                    "and a.indexname = b.relname)) and am.amname in('btree', 'hash', 'gin', 'brin', 'gist', 'spgist') ;";
            Map<?, ?> map =  connection.firstRow(query, new Object[]{columnName, tableName.getSchema("public"), tableName.getTable()});
            return map != null;
        }
    } catch (SQLException e) {
        error("Unable to check if the column '" + columnName + "' from the table '" + tableName + "' is indexed.\n" +
                e.getLocalizedMessage())
    }
    return false
}