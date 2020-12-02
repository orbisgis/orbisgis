/*
 * Bundle datastore/utils is part of the OrbisGIS platform
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
package org.orbisgis.datastore.jdbcutils

import groovy.sql.GroovyResultSet
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import groovy.transform.Field
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import org.geotools.jdbc.JDBCDataStore

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

/**
 * Utility script used as extension module adding methods to JDBCDataStore class.
 *
 * @author Erwan Bocher (CNRS 2020)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */

private static final @Field Map<JDBCDataStore, Sql> SQLS = new HashMap<>()

private static Sql getSql(JDBCDataStore ds) {
    if(!SQLS.containsKey(ds) || SQLS.get(ds).connection.isClosed()) {
        SQLS.put(ds, new Sql(ds.connection))
    }
    return SQLS.get(ds)
}

/**
 * Shortcut executing JDBCDataStore.dataSource.getConnection()
 *
 * @param ds The {@link JDBCDataStore}.
 * @return The {@link JDBCDataStore} {@link javax.sql.DataSource} {@link java.sql.Connection}.
 */
static Connection getConnection(JDBCDataStore ds) {
    ds.dataSource.connection
}

//TODO : not implemented yet, the dataSet() feature needs some testing to check compatibility with geotools API.
/*
DataSet dataSet(JDBCDataStore ds, String table) {
    return new DataSet(new Sql(ds), table)
}

DataSet dataSet(JDBCDataStore ds, Class<?> type) {
    return new DataSet(new Sql(ds), type)
}*/

/**
 * Performs the given SQL query, which should return a single {@link java.sql.ResultSet} object. The given closure is called
 * with the {@link java.sql.ResultSet} as its argument.
 *
 * Example usages:
 *
 * sql.query("select * from PERSON where firstname like 'S%'") { ResultSet rs ->
 *     while (rs.next()) println rs.getString('firstname') + ' ' + rs.getString(3)
 * }
 *
 * sql.query("call get_people_places()") { ResultSet rs ->
 *     while (rs.next()) println rs.toRowResult().firstname
 * }
 *
 *
 * All resources including the ResultSet are closed automatically after the closure is called.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param sql     The sql statement.
 * @param closure Called for each row with a {@link java.sql.ResultSet}.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void query(JDBCDataStore ds, String sql,
           @ClosureParams(value = SimpleType, options = ["java.sql.ResultSet"]) Closure closure)
        throws SQLException {
    getSql(ds).query(sql, closure)
}

/**
 * Performs the given SQL query, which should return a single {@link java.sql.ResultSet} object. The given closure is
 * called with the {@link java.sql.ResultSet} as its argument.
 * The query may contain placeholder question marks which match the given list of parameters.
 *
 * Example usage:
 *
 * sql.query('select * from PERSON where lastname like ?', ['%a%']) { ResultSet rs ->
 *     while (rs.next()) println rs.getString('lastname')
 * }
 *
 *
 * This method supports named and named ordinal parameters.
 * See the class Javadoc for more details.
 *
 * All resources including the ResultSet are closed automatically after the closure is called.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param sql     The sql statement.
 * @param params  A list of parameters.
 * @param closure Called for each row with a {@link java.sql.ResultSet}.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void query(JDBCDataStore ds, String sql, List<Object> params,
                  @ClosureParams(value = SimpleType.class,options = ["java.sql.ResultSet"]) Closure closure)
        throws SQLException {
    getSql(ds).query(sql, params, closure)
}


/**
 * A variant of {@link #query(JDBCDataStore, String, java.util.List, groovy.lang.Closure)}
 * useful when providing the named parameters as a map.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param sql     The sql statement.
 * @param map     A map containing the named parameters
 * @param closure Called for each row with a {@link java.sql.ResultSet}.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void query(JDBCDataStore ds, String sql, Map map,
           @ClosureParams(value = SimpleType.class,options = ["java.sql.ResultSet"]) Closure closure)
        throws SQLException {
    getSql(ds).query(sql, map, closure)
}

/**
 * A variant of {@link #query(JDBCDataStore, String, java.util.List, groovy.lang.Closure)}
 * useful when providing the named parameters as named arguments.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param map     A map containing the named parameters
 * @param sql     The sql statement.
 * @param closure Called for each row with a {@link java.sql.ResultSet}.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void query(JDBCDataStore ds, Map map, String sql,
           @ClosureParams(value = SimpleType.class,options = ["java.sql.ResultSet"]) Closure closure)
        throws SQLException {
    getSql(ds).query(map, sql, closure)
}

/**
 * Performs the given SQL query, which should return a single {@link java.sql.ResultSet} object. The given closure is
 * called with the {@link java.sql.ResultSet} as its argument.
 * The query may contain GString expressions.
 *
 * Example usage:
 *
 * def location = 25
 * sql.query "select * from PERSON where location_id < $location", { ResultSet rs ->
 *     while (rs.next()) println rs.getString('firstname')
 * }
 *
 *
 * All resources including the ResultSet are closed automatically after the closure is called.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param gstring A {@link GString} containing the SQL query with embedded params
 * @param closure called for each row with a {@link java.sql.ResultSet}
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void query(JDBCDataStore ds, GString gstring,
                  @ClosureParams(value = SimpleType.class,options = ["java.sql.ResultSet"]) Closure closure)
        throws SQLException {
    getSql(ds).query(gstring, closure)
}

/**
 *
 * Performs the given SQL query calling the given Closure with each row of the result set.
 * The row will be a {@link GroovyResultSet} which is a {@link ResultSet} that supports accessing the fields using
 * property style notation and ordinal index values.
 * <
 * Example usages:
 *
 * sql.eachRow("select * from PERSON where firstname like 'S%'") { row ->
 *    println "$row.firstname ${row[2]}}"
 * }
 *
 * sql.eachRow "call my_stored_proc_returning_resultset()", {
 *     println it.firstname
 * }
 *
 *
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param sql     The sql statement.
 * @param closure Called for each row with a {@link GroovyResultSet}.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void eachRow(JDBCDataStore ds, String sql,
             @ClosureParams(value=SimpleType.class, options="groovy.sql.GroovyResultSet") Closure closure)
        throws SQLException {
    getSql(ds).eachRow(sql, closure)
}

/**
 * Performs the given SQL query calling the given {@link Closure} with each row of the result set starting at
 * the provided offset, and including up to maxRows number of rows.
 * The row will be a {@link GroovyResultSet} which is a {@link ResultSet} that supports accessing the fields using
 * property style notation and ordinal index values.
 *
 * Note that the underlying implementation is based on either invoking {@link ResultSet#absolute(int)}, or if the
 * ResultSet type is {@link ResultSet#TYPE_FORWARD_ONLY}, the {@link ResultSet#next()} method is invoked equivalently.
 * The first row of a ResultSet is 1, so passing in an offset of 1 or less has no effect on the initial positioning
 * within the result set.
 *
 * Note that different database and JDBC driver implementations may work differently with respect to this method.
 * Specifically, one should expect that {@link ResultSet#TYPE_FORWARD_ONLY} may be less efficient than a "scrollable"
 * type.
 *
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param sql     The sql statement.
 * @param offset  The 1-based offset for the first row to be processed.
 * @param maxRows The maximum number of rows to be processed.
 * @param closure Called for each row with a {@link GroovyResultSet}.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void eachRow(JDBCDataStore ds, String sql, int offset, int maxRows,
                    @ClosureParams(value=SimpleType.class, options="groovy.sql.GroovyResultSet") Closure closure)
        throws SQLException {
    getSql(ds).eachRow(sql, offset, maxRows, closure)
}

/**
 * Performs the given SQL query calling the given rowClosure with each row of the result set.
 * The row will be a {@link GroovyResultSet} which is a {@link ResultSet} that supports accessing the fields using
 * property style notation and ordinal index values.
 * In addition, the metaClosure will be called once passing in the {@link java.sql.ResultSetMetaData} as argument.
 *
 * Example usage:
 *
 * def printColNames = { meta ->
 *     (1..meta.columnCount).each {
 *         print meta.getColumnLabel(it).padRight(20)
 *     }
 *     println()
 * }
 * def printRow = { row ->
 *     row.toRowResult().values().each{ print it.toString().padRight(20) }
 *     println()
 * }
 * sql.eachRow("select * from PERSON", printColNames, printRow)
 *
 *
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds          {@link JDBCDataStore} on which the query is performed.
 * @param sql         The sql statement.
 * @param metaClosure Called for meta data (only once after sql execution).
 * @param closure     Called for each row with a {@link GroovyResultSet}.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void eachRow(JDBCDataStore ds, String sql,
             @ClosureParams(value=SimpleType.class, options="java.sql.ResultSetMetaData") Closure metaClosure,
            @ClosureParams(value=SimpleType.class, options="groovy.sql.GroovyResultSet") Closure rowClosure)
        throws SQLException {
    getSql(ds).eachRow(sql, metaClosure, 0, 0, rowClosure)
}

/**
 * Performs the given SQL query calling the given rowClosure with each row of the result set starting at the provided
 * offset, and including up to maxRows number of rows.
 * The row will be a {@link GroovyResultSet} which is a {@link ResultSet} that supports accessing the fields using
 * property style notation and ordinal index values.
 *
 * In addition, the metaClosure will be called once passing in the {@link java.sql.ResultSetMetaData} as
 * argument.
 *
 * Note that the underlying implementation is based on either invoking {@link ResultSet#absolute(int)}, or if the
 * ResultSet type is {@link ResultSet#TYPE_FORWARD_ONLY}, the {@link ResultSet#next()} method is invoked equivalently.
 * The first row of a ResultSet is 1, so passing in an offset of 1 or less has no effect on the initial positioning
 * within the result set.
 *
 * Note that different database and JDBC driver implementations may work differently with respect to this method.
 * Specifically, one should expect that {@link ResultSet#TYPE_FORWARD_ONLY} may be less efficient than a
 * "scrollable" type.
 *
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds          {@link JDBCDataStore} on which the query is performed.
 * @param sql         The sql statement.
 * @param offset      The 1-based offset for the first row to be processed.
 * @param maxRows     The maximum number of rows to be processed.
 * @param metaClosure Called for meta data (only once after sql execution).
 * @param closure     Called for each row with a {@link GroovyResultSet}.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void eachRow(JDBCDataStore ds, String sql,
                    @ClosureParams(value=SimpleType.class, options="java.sql.ResultSetMetaData") Closure metaClosure,
                    int offset, int maxRows,
                    @ClosureParams(value=SimpleType.class, options="groovy.sql.GroovyResultSet") Closure rowClosure)
        throws SQLException {
    getSql(ds).eachRow(sql, metaClosure, offset, maxRows, rowClosure)
}

/**
 * Performs the given SQL query calling the given rowClosure with each row of the result set starting at the provided
 * offset, and including up to maxRows number of rows.
 * The row will be a {@link GroovyResultSet} which is a {@link ResultSet} that supports accessing the fields using
 * property style notation and ordinal index values.
 *
 * In addition, the metaClosure will be called once passing in the {@link java.sql.ResultSetMetaData} as argument.
 * The query may contain placeholder question marks which match the given list of parameters.
 *
 * Note that the underlying implementation is based on either invoking {@link ResultSet#absolute(int)}, or if the
 * ResultSet type is {@link ResultSet#TYPE_FORWARD_ONLY}, the {@link ResultSet#next()} method is invoked equivalently.
 * The first row of a ResultSet is 1, so passing in an offset of 1 or less has no effect on the initial positioning
 * within the result set.
 *
 * Note that different database and JDBC driver implementations may work differently with respect to this method.
 * Specifically, one should expect that {@link ResultSet#TYPE_FORWARD_ONLY} may be less efficient than a "scrollable"
 * type.
 *
 * @param ds          {@link JDBCDataStore} on which the query is performed.
 * @param sql         The sql statement.
 * @param params      A list of parameters.
 * @param offset      The 1-based offset for the first row to be processed.
 * @param maxRows     The maximum number of rows to be processed.
 * @param metaClosure Called for meta data (only once after sql execution).
 * @param rowClosure  Called for each row with a {@link GroovyResultSet}.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void eachRow(JDBCDataStore ds, String sql, List<Object> params,
                    @ClosureParams(value=SimpleType.class, options="java.sql.ResultSetMetaData") Closure metaClosure,
                    int offset, int maxRows,
                    @ClosureParams(value=SimpleType.class, options="groovy.sql.GroovyResultSet") Closure rowClosure)
        throws SQLException {
    getSql(ds).eachRow(sql, params, metaClosure, offset, maxRows, rowClosure)
}

/**
 * A variant of {@link #eachRow(JDBCDataStore, String, java.util.List, groovy.lang.Closure, int, int, groovy.lang.Closure)}
 * allowing the named parameters to be supplied in a map.
 *
 * @param ds          {@link JDBCDataStore} on which the query is performed.
 * @param sql         The sql statement.
 * @param map         1 map containing the named parameters.
 * @param offset      The 1-based offset for the first row to be processed?
 * @param maxRows     The maximum number of rows to be processed.
 * @param metaClosure Called for meta data (only once after sql execution).
 * @param rowClosure  Called for each row with a {@link GroovyResultSet}.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void eachRow(JDBCDataStore ds, String sql, Map map,
                    @ClosureParams(value=SimpleType.class, options="java.sql.ResultSetMetaData") Closure metaClosure,
                    int offset, int maxRows,
                    @ClosureParams(value=SimpleType.class, options="groovy.sql.GroovyResultSet") Closure rowClosure)
        throws SQLException {
    getSql(ds).eachRow(sql, map, metaClosure, offset, maxRows, rowClosure)
}

/**
 * A variant of {@link #eachRow(JDBCDataStore, String, java.util.List, groovy.lang.Closure, int, int, groovy.lang.Closure)}
 * allowing the named parameters to be supplied as named arguments.
 *
 * @param ds          {@link JDBCDataStore} on which the query is performed.
 * @param map         A map containing the named parameters.
 * @param sql         The sql statement.
 * @param offset      The 1-based offset for the first row to be processed.
 * @param maxRows     The maximum number of rows to be processed.
 * @param metaClosure Called for meta data (only once after sql execution).
 * @param rowClosure  Called for each row with a {@link GroovyResultSet}.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void eachRow(JDBCDataStore ds, Map map, String sql,
                    @ClosureParams(value=SimpleType.class, options="java.sql.ResultSetMetaData") Closure metaClosure,
                    int offset, int maxRows,
                    @ClosureParams(value=SimpleType.class, options="groovy.sql.GroovyResultSet") Closure rowClosure)
        throws SQLException {
    getSql(ds).eachRow(map, sql, metaClosure, offset, maxRows, rowClosure)
}

/**
 * Performs the given SQL query calling the given Closure with each row of the result set.
 * The row will be a {@link GroovyResultSet} which is a {@link ResultSet}
 * that supports accessing the fields using property style notation and ordinal index values.
 * In addition, the metaClosure will be called once passing in the {@link java.sql.ResultSetMetaData} as argument.
 * The query may contain placeholder question marks which match the given list of parameters.
 *
 * Example usage:
 *
 * def printColNames = { meta ->
 *     (1..meta.columnCount).each {
 *         print meta.getColumnLabel(it).padRight(20)
 *     }
 *     println()
 * }
 * def printRow = { row ->
 *     row.toRowResult().values().each{ print it.toString().padRight(20) }
 *     println()
 * }
 * sql.eachRow("select * from PERSON where lastname like ?", ['%a%'], printColNames, printRow)
 *
 *
 * This method supports named and named ordinal parameters.
 * See the class Javadoc for more details.
 *
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds          {@link JDBCDataStore} on which the query is performed.
 * @param sql         The sql statement.
 * @param params      A list of parameters.
 * @param metaClosure Called for meta data (only once after sql execution).
 * @param rowClosure  Called for each row with a {@link GroovyResultSet}.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void eachRow(JDBCDataStore ds, String sql, List<Object> params,
                    @ClosureParams(value=SimpleType.class, options="java.sql.ResultSetMetaData") Closure metaClosure,
                    @ClosureParams(value=SimpleType.class, options="groovy.sql.GroovyResultSet") Closure rowClosure)
        throws SQLException {
    getSql(ds).eachRow(sql, params, metaClosure, rowClosure)
}

/**
 * A variant of {@link #eachRow(JDBCDataStore, String, java.util.List, groovy.lang.Closure, groovy.lang.Closure)}
 * useful when providing the named parameters as a map.
 *
 * @param ds          {@link JDBCDataStore} on which the query is performed.
 * @param sql         The sql statement.
 * @param params      A map of named parameters.
 * @param metaClosure Called for meta data (only once after sql execution).
 * @param rowClosure  Called for each row with a {@link GroovyResultSet}.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void eachRow(JDBCDataStore ds, String sql, Map params,
                    @ClosureParams(value=SimpleType.class, options="java.sql.ResultSetMetaData") Closure metaClosure,
                    @ClosureParams(value=SimpleType.class, options="groovy.sql.GroovyResultSet") Closure rowClosure)
        throws SQLException {
    getSql(ds).eachRow(sql, params, metaClosure, rowClosure)
}

/**
 * A variant of {@link #eachRow(JDBCDataStore, String, java.util.List, groovy.lang.Closure, groovy.lang.Closure)}
 * useful when providing the named parameters as named arguments.
 *
 * @param ds          {@link JDBCDataStore} on which the query is performed.
 * @param params      A map of named parameters.
 * @param sql         The sql statement.
 * @param metaClosure Called for meta data (only once after sql execution).
 * @param rowClosure  Called for each row with a {@link GroovyResultSet}.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void eachRow(JDBCDataStore ds, Map params, String sql,
                    @ClosureParams(value=SimpleType.class, options="java.sql.ResultSetMetaData") Closure metaClosure,
                    @ClosureParams(value=SimpleType.class, options="groovy.sql.GroovyResultSet") Closure rowClosure) 
        throws SQLException {
    getSql(ds).eachRow(params, sql, metaClosure, rowClosure)
}

/**
 * Performs the given SQL query calling the given Closure with each row of the result set.
 * The row will be a {@link GroovyResultSet} which is a {@link ResultSet} that supports accessing the fields using
 * property style notation and ordinal index values.
 * The query may contain placeholder question marks which match the given list of parameters.
 * 
 * Example usage:
 * 
 * sql.eachRow("select * from PERSON where lastname like ?", ['%a%']) { row ->
 *     println "${row[1]} $row.lastname"
 * }
 * 
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param sql     The sql statement.
 * @param params  A list of parameters.
 * @param closure Called for each row with a {@link GroovyResultSet}.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void eachRow(JDBCDataStore ds, String sql, List<Object> params,
                    @ClosureParams(value=SimpleType.class, options="groovy.sql.GroovyResultSet") Closure closure) 
        throws SQLException {
    getSql(ds).eachRow(sql, params, closure)
}

/**
 * A variant of {@link #eachRow(JDBCDataStore, String, java.util.List, groovy.lang.Closure)} useful when providing the
 * named parameters as a map.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param sql     The sql statement.
 * @param params  A map of named parameters.
 * @param closure Called for each row with a {@link GroovyResultSet}.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void eachRow(JDBCDataStore ds, String sql, Map params,
                    @ClosureParams(value=SimpleType.class, options="groovy.sql.GroovyResultSet") Closure closure)
        throws SQLException {
    getSql(ds).eachRow(sql, params, closure)
}

/**
 * A variant of {@link #eachRow(JDBCDataStore, String, java.util.List, groovy.lang.Closure)} useful when providing the
 * named parameters as named arguments.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param params  A map of named parameters.
 * @param sql     The sql statement.
 * @param closure Called for each row with a {@link GroovyResultSet}.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void eachRow(JDBCDataStore ds, Map params, String sql,
                    @ClosureParams(value=SimpleType.class, options="groovy.sql.GroovyResultSet") Closure closure)
        throws SQLException {
    getSql(ds).eachRow(params, sql, closure)
}

/**
 * Performs the given SQL query calling the given closure with each row of the result set starting at the provided
 * offset, and including up to maxRows number of rows.
 * The row will be a {@link GroovyResultSet} which is a {@link ResultSet} that supports accessing the fields using
 * property style notation and ordinal index values.
 * The query may contain placeholder question marks which match the given list of parameters.
 *
 * Note that the underlying implementation is based on either invoking {@link ResultSet#absolute(int)}, or if the
 * ResultSet type is {@link ResultSet#TYPE_FORWARD_ONLY}, the {@link ResultSet#next()} method is invoked equivalently.
 * The first row of a ResultSet is 1, so passing in an offset of 1 or less has no effect on the initial positioning
 * within the result set.
 *
 * Note that different database and JDBC driver implementations may work differently with respect to this method.
 * Specifically, one should expect that {@link ResultSet#TYPE_FORWARD_ONLY} may be less efficient than a "scrollable"
 * type.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param sql     The sql statement.
 * @param params  A list of parameters.
 * @param offset  The 1-based offset for the first row to be processed.
 * @param maxRows The maximum number of rows to be processed.
 * @param closure Called for each row with a {@link GroovyResultSet}.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void eachRow(JDBCDataStore ds, String sql, List<Object> params, int offset, int maxRows,
                    @ClosureParams(value=SimpleType.class, options="groovy.sql.GroovyResultSet") Closure closure)
        throws SQLException {
    getSql(ds).eachRow(sql, params, offset, maxRows, closure)
}

/**
 * A variant of {@link #eachRow(JDBCDataStore, String, java.util.List, int, int, groovy.lang.Closure)} useful when
 * providing the named parameters as a map.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param sql     The sql statement.
 * @param params  A map of named parameters.
 * @param offset  The 1-based offset for the first row to be processed.
 * @param maxRows The maximum number of rows to be processed.
 * @param closure Called for each row with a {@link GroovyResultSet}.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void eachRow(JDBCDataStore ds, String sql, Map params, int offset, int maxRows,
                    @ClosureParams(value=SimpleType.class, options="groovy.sql.GroovyResultSet") Closure closure)
        throws SQLException {
    getSql(ds).eachRow(sql, params, offset, maxRows, closure)
}

/**
 * A variant of {@link #eachRow(JDBCDataStore, String, java.util.List, int, int, groovy.lang.Closure)} useful when
 * providing the named parameters as named arguments.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param params  A map of named parameters.
 * @param sql     The sql statement.
 * @param offset  The 1-based offset for the first row to be processed.
 * @param maxRows The maximum number of rows to be processed.
 * @param closure Called for each row with a {@link GroovyResultSet}.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void eachRow(JDBCDataStore ds, Map params, String sql, int offset, int maxRows,
                    @ClosureParams(value=SimpleType.class, options="groovy.sql.GroovyResultSet") Closure closure)
        throws SQLException {
    getSql(ds).eachRow(sql, params, offset, maxRows, closure)
}

/**
 * Performs the given SQL query calling the given Closure with each row of the result set.
 * The row will be a {@link GroovyResultSet} which is a {@link ResultSet} that supports accessing the fields using
 * property style notation and ordinal index values.
 *
 * In addition, the metaClosure will be called once passing in the {@link java.sql.ResultSetMetaData} as argument.
 * The query may contain GString expressions.
 *
 * Example usage:
 *
 * def location = 25
 * def printColNames = { meta ->
 *     (1..meta.columnCount).each {
 *         print meta.getColumnLabel(it).padRight(20)
 *     }
 *     println()
 * }
 * def printRow = { row ->
 *     row.toRowResult().values().each{ print it.toString().padRight(20) }
 *     println()
 * }
 * sql.eachRow("select * from PERSON where location_id {@code <} $location", printColNames, printRow)
 *
 *
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds          {@link JDBCDataStore} on which the query is performed.
 * @param gstring     A {@link GString} containing the SQL query with embedded params.
 * @param metaClosure Called for meta data (only once after sql execution).
 * @param rowClosure  Called for each row with a {@link GroovyResultSet}.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void eachRow(JDBCDataStore ds, GString gstring,
                    @ClosureParams(value=SimpleType.class, options="java.sql.ResultSetMetaData") Closure metaClosure,
                    @ClosureParams(value=SimpleType.class, options="groovy.sql.GroovyResultSet") Closure rowClosure)
        throws SQLException {
    getSql(ds).eachRow(gstring, metaClosure, rowClosure)
}

/**
 * Performs the given SQL query calling the given closure with each row of the result set starting at the provided
 * offset, and including up to maxRows number of rows.
 * The row will be a {@link GroovyResultSet} which is a {@link ResultSet} that supports accessing the fields using
 * property style notation and ordinal index values.
 * In addition, the metaClosure will be called once passing in the {@link java.sql.ResultSetMetaData} as argument.
 * The query may contain GString expressions.
 *
 * Note that the underlying implementation is based on either invoking {@link ResultSet#absolute(int)}, or if the
 * ResultSet type is {@link ResultSet#TYPE_FORWARD_ONLY}, the {@link ResultSet#next()} method is invoked equivalently.
 * The first row of a ResultSet is 1, so passing in an offset of 1 or less has no effect on the initial positioning
 * within the result set.
 *
 * Note that different database and JDBC driver implementations may work differently with respect to this method.
 * Specifically, one should expect that {@link ResultSet#TYPE_FORWARD_ONLY} may be less efficient than a "scrollable"
 * type.
 *
 * @param ds          {@link JDBCDataStore} on which the query is performed.
 * @param gstring     A GString containing the SQL query with embedded params.
 * @param metaClosure Called for meta data (only once after sql execution).
 * @param offset      The 1-based offset for the first row to be processed.
 * @param maxRows     The maximum number of rows to be processed.
 * @param rowClosure  Called for each row with a {@link GroovyResultSet}.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void eachRow(JDBCDataStore ds, GString gstring,
                    @ClosureParams(value=SimpleType.class, options="java.sql.ResultSetMetaData") Closure metaClosure,
                    int offset, int maxRows,
                    @ClosureParams(value=SimpleType.class, options="groovy.sql.GroovyResultSet") Closure rowClosure)
        throws SQLException {
    getSql(ds).eachRow(gstring, metaClosure, offset, maxRows, rowClosure)
}

/**
 * Performs the given SQL query calling the given closure with each row of the result set starting at
 * the provided offset, and including up to maxRows number of rows.
 * The row will be a {@link GroovyResultSet} which is a {@link ResultSet} that supports accessing the fields using
 * property style notation and ordinal index values.
 * The query may contain GString expressions.
 *
 * Note that the underlying implementation is based on either invoking {@link ResultSet#absolute(int)}, or if the
 * ResultSet type is {@link ResultSet#TYPE_FORWARD_ONLY}, the {@link ResultSet#next()} method is invoked equivalently.
 * The first row of a ResultSet is 1, so passing in an offset of 1 or less has no effect on the initial positioning
 * within the result set.
 *
 * Note that different database and JDBC driver implementations may work differently with respect to this method.
 * Specifically, one should expect that {@link ResultSet#TYPE_FORWARD_ONLY} may be less efficient than a "scrollable"
 * type.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param gstring A GString containing the SQL query with embedded params.
 * @param offset  The 1-based offset for the first row to be processed.
 * @param maxRows The maximum number of rows to be processed.
 * @param closure Called for each row with a {@link GroovyResultSet}.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void eachRow(JDBCDataStore ds, GString gstring, int offset, int maxRows,
                    @ClosureParams(value=SimpleType.class, options="groovy.sql.GroovyResultSet") Closure closure)
        throws SQLException {
    getSql(ds).eachRow(gstring, offset, maxRows, closure)
}

/**
 * Performs the given SQL query calling the given Closure with each row of the result set.
 * The row will be a {@link GroovyResultSet} which is a {@link ResultSet} that supports accessing the fields using
 * property style notation and ordinal index values.
 * The query may contain GString expressions.
 *
 * Example usage:
 *
 * def location = 25
 * sql.eachRow("select * from PERSON where location_id {@code <} $location") { row ->
 *     println row.firstname
 * }
 *
 *
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param gstring A GString containing the SQL query with embedded params.
 * @param closure Called for each row with a {@link GroovyResultSet}.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void eachRow(JDBCDataStore ds, GString gstring,
                    @ClosureParams(value=SimpleType.class, options="groovy.sql.GroovyResultSet") Closure closure) throws SQLException {
    getSql(ds).eachRow(gstring, closure)
}

/**
 * Performs the given SQL query and return the rows of the result set.
 * 
 * Example usage:
 * 
 * def ans = sql.rows("select * from PERSON where firstname like 'S%'")
 * println "Found ${ans.size()} rows"
 * 
 *
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds  {@link JDBCDataStore} on which the query is performed.
 * @param sql The SQL statement.
 * @return A list of GroovyRowResult objects.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static List<GroovyRowResult> rows(JDBCDataStore ds, String sql) throws SQLException {
    getSql(ds).rows(sql)
}

/**
 * Performs the given SQL query and return a "page" of rows from the result set.  A page is defined as starting at
 * a 1-based offset, and containing a maximum number of rows.
 * 
 * Note that the underlying implementation is based on either invoking {@link ResultSet#absolute(int)},
 * or if the ResultSet type is {@link ResultSet#TYPE_FORWARD_ONLY}, the {@link ResultSet#next()} method
 * is invoked equivalently.  The first row of a ResultSet is 1, so passing in an offset of 1 or less has no effect
 * on the initial positioning within the result set.
 * 
 * Note that different database and JDBC driver implementations may work differently with respect to this method.
 * Specifically, one should expect that {@link ResultSet#TYPE_FORWARD_ONLY} may be less efficient than a
 * "scrollable" type.
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param sql     The SQL statement.
 * @param offset  The 1-based offset for the first row to be processed.
 * @param maxRows The maximum number of rows to be processed.
 * @return A list of GroovyRowResult objects.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static List<GroovyRowResult> rows(JDBCDataStore ds, String sql, int offset, int maxRows) throws SQLException {
    getSql(ds).rows(sql, offset, maxRows, null)
}

/**
 * Performs the given SQL query and return the rows of the result set.
 * In addition, the metaClosure will be called once passing in the
 * ResultSetMetaData as argument.
 * 
 * Example usage:
 * 
 * def printNumCols = { meta -> println "Found $meta.columnCount columns" }
 * def ans = sql.rows("select * from PERSON", printNumCols)
 * println "Found ${ans.size()} rows"
 * 
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds          {@link JDBCDataStore} on which the query is performed.
 * @param sql         The SQL statement.
 * @param metaClosure Called with meta data of the ResultSet.
 * @return A list of GroovyRowResult objects.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static List<GroovyRowResult> rows(JDBCDataStore ds, String sql,
                  @ClosureParams(value=SimpleType.class, options="java.sql.ResultSetMetaData") Closure metaClosure)
        throws SQLException {
    getSql(ds).rows(sql, metaClosure)
}

/**
 * Performs the given SQL query and return a "page" of rows from the result set.  A page is defined as starting at
 * a 1-based offset, and containing a maximum number of rows.
 * In addition, the metaClosure will be called once passing in the {@link java.sql.ResultSetMetaData} as argument.
 * 
 * Note that the underlying implementation is based on either invoking {@link ResultSet#absolute(int)}, or if the
 * ResultSet type is {@link ResultSet#TYPE_FORWARD_ONLY}, the {@link ResultSet#next()} method is invoked equivalently.
 * The first row of a ResultSet is 1, so passing in an offset of 1 or less has no effect on the initial positioning
 * within the result set.
 * 
 * Note that different database and JDBC driver implementations may work differently with respect to this method.
 * Specifically, one should expect that {@link ResultSet#TYPE_FORWARD_ONLY} may be less efficient than a
 * "scrollable" type.
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds          {@link JDBCDataStore} on which the query is performed.
 * @param sql         The SQL statement.
 * @param offset      The 1-based offset for the first row to be processed.
 * @param maxRows     The maximum number of rows to be processed.
 * @param metaClosure Called for meta data (only once after sql execution).
 * @return A list of GroovyRowResult objects.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static List<GroovyRowResult> rows(JDBCDataStore ds, String sql, int offset, int maxRows,
                  @ClosureParams(value=SimpleType.class, options="java.sql.ResultSetMetaData") Closure metaClosure)
        throws SQLException {
    getSql(ds).rows(sql, offset, maxRows, metaClosure)
}

/**
 * Performs the given SQL query and return the rows of the result set.
 * The query may contain placeholder question marks which match the given list of parameters.
 * 
 * Example usage:
 * 
 * def ans = sql.rows("select * from PERSON where lastname like ?", ['%a%'])
 * println "Found ${ans.size()} rows"
 * 
 * 
 * This method supports named and named ordinal parameters by supplying such parameters in the params list. See the
 * class Javadoc for more details.
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds     {@link JDBCDataStore} on which the query is performed.
 * @param sql    The SQL statement.
 * @param params A list of parameters.
 * @return A list of GroovyRowResult objects.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static List<GroovyRowResult> rows(JDBCDataStore ds, String sql, List<Object> params) throws SQLException {
    getSql(ds).rows(sql, params)
}

/**
 * A variant of {@link #rows(JDBCDataStore, String, java.util.List)}
 * useful when providing the named parameters as named arguments.
 *
 * @param ds     {@link JDBCDataStore} on which the query is performed.
 * @param params a map containing the named parameters
 * @param sql    The SQL statement.
 * @return A list of GroovyRowResult objects.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static List<GroovyRowResult> rows(JDBCDataStore ds, Map params, String sql) throws SQLException {
    getSql(ds).rows(params, sql)
}

/**
 * Performs the given SQL query and return a "page" of rows from the result set.  A page is defined as starting at
 * a 1-based offset, and containing a maximum number of rows.
 * The query may contain placeholder question marks which match the given list of parameters.
 * 
 * Note that the underlying implementation is based on either invoking {@link ResultSet#absolute(int)},
 * or if the ResultSet type is {@link ResultSet#TYPE_FORWARD_ONLY}, the {@link ResultSet#next()} method
 * is invoked equivalently.  The first row of a ResultSet is 1, so passing in an offset of 1 or less has no effect
 * on the initial positioning within the result set.
 * 
 * Note that different database and JDBC driver implementations may work differently with respect to this method.
 * Specifically, one should expect that {@link ResultSet#TYPE_FORWARD_ONLY} may be less efficient than a
 * "scrollable" type.
 * 
 * This method supports named and named ordinal parameters by supplying such parameters in the params list. See the
 * class Javadoc for more details.
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param sql     The SQL statement.
 * @param params  A list of parameters.
 * @param offset  The 1-based offset for the first row to be processed.
 * @param maxRows The maximum number of rows to be processed.
 * @return A list of GroovyRowResult objects.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static List<GroovyRowResult> rows(JDBCDataStore ds, String sql, List<Object> params, int offset, int maxRows) throws SQLException {
    getSql(ds).rows(sql, params, offset, maxRows)
}

/**
 * A variant of {@link #rows(JDBCDataStore, String, java.util.List, int, int)} useful when providing the named
 * parameters as a map.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param sql     The SQL statement.
 * @param params  A map of named parameters.
 * @param offset  The 1-based offset for the first row to be processed.
 * @param maxRows The maximum number of rows to be processed.
 * @return A list of GroovyRowResult objects.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static List<GroovyRowResult> rows(JDBCDataStore ds, String sql, Map params, int offset, int maxRows) throws SQLException {
    getSql(ds).rows(sql, params, offset, maxRows)
}

/**
 * A variant of {@link #rows(JDBCDataStore, String, java.util.List, int, int)} useful when providing the named
 * parameters as named arguments.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param params  A map of named parameters.
 * @param sql     The SQL statement.
 * @param offset  The 1-based offset for the first row to be processed.
 * @param maxRows The maximum number of rows to be processed.
 * @return A list of GroovyRowResult objects.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static List<GroovyRowResult> rows(JDBCDataStore ds, Map params, String sql, int offset, int maxRows)
        throws SQLException {
    getSql(ds).rows(params, sql, offset, maxRows)
}

/**
 * Performs the given SQL query and return the rows of the result set.
 * 
 * This method supports named and named ordinal parameters by supplying such parameters in the params array. See the
 * class Javadoc for more details.
 * 
 * An Object array variant of {@link #rows(JDBCDataStore, String, List)}.
 *
 * @param ds     {@link JDBCDataStore} on which the query is performed.
 * @param sql    The SQL statement.
 * @param params an array of parameters
 * @return A list of GroovyRowResult objects.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static List<GroovyRowResult> rows(JDBCDataStore ds, String sql, Object[] params)
        throws SQLException {
    getSql(ds).rows(sql, params)
}

/**
 * Performs the given SQL query and return the rows of the result set.
 * 
 * This method supports named and named ordinal parameters by supplying such parameters in the params array. See the
 * class Javadoc for more details.
 * 
 * An Object array variant of {@link #rows(JDBCDataStore, String, List, int, int)}.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param sql     The SQL statement.
 * @param params  an array of parameters
 * @param offset  The 1-based offset for the first row to be processed.
 * @param maxRows The maximum number of rows to be processed.
 * @return A list of GroovyRowResult objects.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static List<GroovyRowResult> rows(JDBCDataStore ds, String sql, Object[] params, int offset, int maxRows)
        throws SQLException {
    getSql(ds).rows(sql, Arrays.asList(params), offset, maxRows)
}

/**
 * Performs the given SQL query and return the rows of the result set.
 * In addition, the metaClosure will be called once passing in the {@link java.sql.ResultSetMetaData} as argument.
 * The query may contain placeholder question marks which match the given list of parameters.
 * 
 * Example usage:
 * 
 * def printNumCols = { meta -> println "Found $meta.columnCount columns" }
 * def ans = sql.rows("select * from PERSON where lastname like ?", ['%a%'], printNumCols)
 * println "Found ${ans.size()} rows"
 * 
 * 
 * This method supports named and named ordinal parameters by supplying such parameters in the params list. Here is an
 * example:
 * 
 * def printNumCols = { meta -> println "Found $meta.columnCount columns" }
 *
 * def mapParam = [foo: 'Smith']
 * def domainParam = new MyDomainClass(bar: 'John')
 * def qry = 'select * from PERSON where lastname=?1.foo and firstname=?2.bar'
 * def ans = sql.rows(qry, [mapParam, domainParam], printNumCols)
 * println "Found ${ans.size()} rows"
 *
 * def qry2 = 'select * from PERSON where firstname=:first and lastname=:last'
 * def ans2 = sql.rows(qry2, [[last:'Smith', first:'John']], printNumCols)
 * println "Found ${ans2.size()} rows"
 * 
 * See the class Javadoc for more details.
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds          {@link JDBCDataStore} on which the query is performed.
 * @param sql         The SQL statement.
 * @param params      A list of parameters.
 * @param metaClosure Called for meta data (only once after sql execution).
 * @return A list of GroovyRowResult objects.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static List<GroovyRowResult> rows(JDBCDataStore ds, String sql, List<Object> params,
                    @ClosureParams(value=SimpleType.class, options="java.sql.ResultSetMetaData") Closure metaClosure)
        throws SQLException {
    getSql(ds).rows(sql, params, metaClosure)
}

/**
 * A variant of {@link #rows(JDBCDataStore, String, java.util.List, groovy.lang.Closure)} useful when providing the
 * named parameters as a map.
 *
 * @param ds          {@link JDBCDataStore} on which the query is performed.
 * @param sql         The SQL statement.
 * @param params      A map of named parameters.
 * @param metaClosure Called for meta data (only once after sql execution).
 * @return A list of GroovyRowResult objects.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static List<GroovyRowResult> rows(JDBCDataStore ds, String sql, Map params,
                    @ClosureParams(value=SimpleType.class, options="java.sql.ResultSetMetaData") Closure metaClosure)
        throws SQLException {
    getSql(ds).rows(sql, params, metaClosure)
}

/**
 * A variant of {@link #rows(JDBCDataStore, String, java.util.List, groovy.lang.Closure)} useful when providing the
 * named parameters as named arguments.
 *
 * @param ds          {@link JDBCDataStore} on which the query is performed.
 * @param params      A map of named parameters.
 * @param sql         The SQL statement.
 * @param metaClosure Called for meta data (only once after sql execution).
 * @return A list of GroovyRowResult objects.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static List<GroovyRowResult> rows(JDBCDataStore ds, Map params, String sql,
                  @ClosureParams(value=SimpleType.class, options="java.sql.ResultSetMetaData") Closure metaClosure)
        throws SQLException {
    getSql(ds).rows(params, sql, metaClosure)
}

/**
 * Performs the given SQL query and return a "page" of rows from the result set.  A page is defined as starting at
 * a 1-based offset, and containing a maximum number of rows.
 * In addition, the metaClosure will be called once passing in the {@link java.sql.ResultSetMetaData} as argument.
 * The query may contain placeholder question marks which match the given list of parameters.
 * 
 * Note that the underlying implementation is based on either invoking {@link ResultSet#absolute(int)}, or if the
 * ResultSet type is {@link ResultSet#TYPE_FORWARD_ONLY}, the {@link ResultSet#next()} method is invoked equivalently.
 * The first row of a ResultSet is 1, so passing in an offset of 1 or less has no effect
 * on the initial positioning within the result set.
 * 
 * Note that different database and JDBC driver implementations may work differently with respect to this method.
 * Specifically, one should expect that {@link ResultSet#TYPE_FORWARD_ONLY} may be less efficient than a
 * "scrollable" type.
 * 
 * This method supports named and named ordinal parameters by supplying such parameters in the params list. See the
 * class Javadoc for more details.
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds          {@link JDBCDataStore} on which the query is performed.
 * @param sql         The SQL statement.
 * @param params      A list of parameters.
 * @param offset      The 1-based offset for the first row to be processed.
 * @param maxRows     The maximum number of rows to be processed.
 * @param metaClosure Called for meta data (only once after sql execution).
 * @return A list of GroovyRowResult objects.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static List<GroovyRowResult> rows(JDBCDataStore ds, String sql, List<Object> params, int offset, int maxRows,
                  @ClosureParams(value=SimpleType.class, options="java.sql.ResultSetMetaData") Closure metaClosure)
        throws SQLException {
    getSql(ds).rows(sql, params, offset, maxRows, metaClosure)
}

/**
 * A variant of {@link #rows(JDBCDataStore, String, java.util.List, int, int, groovy.lang.Closure)} useful when
 * providing the named parameters as a map.
 *
 * @param ds          {@link JDBCDataStore} on which the query is performed.
 * @param sql         The SQL statement.
 * @param params      A map of named parameters.
 * @param offset      The 1-based offset for the first row to be processed.
 * @param maxRows     The maximum number of rows to be processed.
 * @param metaClosure Called for meta data (only once after sql execution).
 * @return A list of GroovyRowResult objects.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static List<GroovyRowResult> rows(JDBCDataStore ds, String sql, Map params, int offset, int maxRows,
                  @ClosureParams(value=SimpleType.class, options="java.sql.ResultSetMetaData") Closure metaClosure)
        throws SQLException {
    getSql(ds).rows(sql, params, offset, maxRows, metaClosure)
}

/**
 * A variant of {@link #rows(JDBCDataStore, String, java.util.List, int, int, groovy.lang.Closure)} useful when
 * providing the named parameters as named arguments.
 *
 * @param ds          {@link JDBCDataStore} on which the query is performed.
 * @param params      A map of named parameters.
 * @param sql         The SQL statement.
 * @param offset      The 1-based offset for the first row to be processed.
 * @param maxRows     The maximum number of rows to be processed.
 * @param metaClosure Called for meta data (only once after sql execution).
 * @return A list of GroovyRowResult objects.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static List<GroovyRowResult> rows(JDBCDataStore ds, Map params, String sql, int offset, int maxRows,
                  @ClosureParams(value=SimpleType.class, options="java.sql.ResultSetMetaData") Closure metaClosure)
        throws SQLException {
    getSql(ds).rows(params, sql, offset, maxRows, metaClosure)
}

/**
 * Performs the given SQL query and return a "page" of rows from the result set.  A page is defined as starting at
 * a 1-based offset, and containing a maximum number of rows.
 * The query may contain GString expressions.
 * 
 * Note that the underlying implementation is based on either invoking {@link ResultSet#absolute(int)}, or if the
 * ResultSet type is {@link ResultSet#TYPE_FORWARD_ONLY}, the {@link ResultSet#next()} method is invoked equivalently.
 * The first row of a ResultSet is 1, so passing in an offset of 1 or less has no effect
 * on the initial positioning within the result set.
 * 
 * Note that different database and JDBC driver implementations may work differently with respect to this method.
 * Specifically, one should expect that {@link ResultSet#TYPE_FORWARD_ONLY} may be less efficient than a
 * "scrollable" type.
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param sql     The SQL statement.
 * @param offset  The 1-based offset for the first row to be processed.
 * @param maxRows The maximum number of rows to be processed.
 * @return A list of GroovyRowResult objects.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static List<GroovyRowResult> rows(JDBCDataStore ds, GString sql, int offset, int maxRows) throws SQLException {
    getSql(ds).rows(sql, offset, maxRows)
}

/**
 * Performs the given SQL query and return the rows of the result set.
 * The query may contain GString expressions.
 * 
 * Example usage:
 * 
 * def location = 25
 * def ans = sql.rows("select * from PERSON where location_id {@code <} $location")
 * println "Found ${ans.size()} rows"
 *
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param gstring A GString containing the SQL query with embedded params.
 * @return A list of GroovyRowResult objects.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static List<GroovyRowResult> rows(JDBCDataStore ds, GString gstring) throws SQLException {
    getSql(ds).rows(gstring)
}

/**
 * Performs the given SQL query and return the rows of the result set.
 * In addition, the metaClosure will be called once passing in the {@link java.sql.ResultSetMetaData} as argument.
 * The query may contain GString expressions.
 * 
 * Example usage:
 * 
 * def location = 25
 * def printNumCols = { meta -> println "Found $meta.columnCount columns" }
 * def ans = sql.rows("select * from PERSON where location_id {@code <} $location", printNumCols)
 * println "Found ${ans.size()} rows"
 * 
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds          {@link JDBCDataStore} on which the query is performed.
 * @param gstring     A GString containing the SQL query with embedded params.
 * @param metaClosure Called with meta data of the ResultSet.
 * @return A list of GroovyRowResult objects.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static List<GroovyRowResult> rows(JDBCDataStore ds, GString gstring,
                  @ClosureParams(value=SimpleType.class, options="java.sql.ResultSetMetaData") Closure metaClosure)
        throws SQLException {
    getSql(ds).rows(gstring, metaClosure)
}

/**
 * Performs the given SQL query and return a "page" of rows from the result set.  A page is defined as starting at
 * a 1-based offset, and containing a maximum number of rows.
 * In addition, the metaClosure will be called once passing in the {@link java.sql.ResultSetMetaData} as argument.
 * The query may contain GString expressions.
 * 
 * Note that the underlying implementation is based on either invoking {@link ResultSet#absolute(int)},
 * or if the ResultSet type is {@link ResultSet#TYPE_FORWARD_ONLY}, the {@link ResultSet#next()} method
 * is invoked equivalently.  The first row of a ResultSet is 1, so passing in an offset of 1 or less has no effect
 * on the initial positioning within the result set.
 * 
 * Note that different database and JDBC driver implementations may work differently with respect to this method.
 * Specifically, one should expect that {@link ResultSet#TYPE_FORWARD_ONLY} may be less efficient than a
 * "scrollable" type.
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds          {@link JDBCDataStore} on which the query is performed.
 * @param gstring     The SQL statement.
 * @param offset      The 1-based offset for the first row to be processed.
 * @param maxRows     The maximum number of rows to be processed.
 * @param metaClosure Called for meta data (only once after sql execution).
 * @return A list of GroovyRowResult objects.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static List<GroovyRowResult> rows(JDBCDataStore ds, GString gstring, int offset, int maxRows,
                  @ClosureParams(value=SimpleType.class, options="java.sql.ResultSetMetaData") Closure metaClosure) 
        throws SQLException {
    getSql(ds).rows(gstring, offset, maxRows, metaClosure)
}
/**
 * Performs the given SQL query and return the first row of the result set.
 * 
 * Example usage:
 * 
 * def ans = sql.firstRow("select * from PERSON where firstname like 'S%'")
 * println ans.firstname
 * 
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds  {@link JDBCDataStore} on which the query is performed.
 * @param sql The SQL statement.
 * @return A GroovyRowResult object or null if no row is found.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static GroovyRowResult firstRow(JDBCDataStore ds, String sql) throws SQLException {
    getSql(ds).firstRow(sql)
}

/**
 * Performs the given SQL query and return the first row of the result set.
 * The query may contain GString expressions.
 * 
 * Example usage:
 * 
 * def location = 25
 * def ans = sql.firstRow("select * from PERSON where location_id {@code <} $location")
 * println ans.firstname
 * 
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param gstring A GString containing the SQL query with embedded params.
 * @return A GroovyRowResult object or null if no row is found.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static GroovyRowResult firstRow(JDBCDataStore ds, GString gstring) throws SQLException {
    getSql(ds).firstRow(gstring)
}

/**
 * Performs the given SQL query and return the first row of the result set.
 * The query may contain placeholder question marks which match the given list of parameters.
 * 
 * Example usages:
 * 
 * def ans = sql.firstRow("select * from PERSON where lastname like ?", ['%a%'])
 * println ans.firstname
 * 
 * If your database returns scalar functions as ResultSets, you can also use firstRow to gain access to stored 
 * procedure results, e.g. using hsqldb 1.9 RC4:
 * 
 * sql.execute """
 *     create function FullName(p_firstname VARCHAR(40)) returns VARCHAR(80)
 *     BEGIN atomic
 *     DECLARE ans VARCHAR(80);
 *     SET ans = (SELECT firstname || ' ' || lastname FROM PERSON WHERE firstname = p_firstname);
 *     RETURN ans;
 *     END
 * """
 *
 * assert sql.firstRow("{call FullName(?)}", ['Sam'])[0] == 'Sam Pullara'
 * 
 * 
 * This method supports named and named ordinal parameters by supplying such parameters in the params list. See the
 * class Javadoc for more details.
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds     {@link JDBCDataStore} on which the query is performed.
 * @param sql    The SQL statement.
 * @param params A list of parameters.
 * @return A GroovyRowResult object or null if no row is found.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static GroovyRowResult firstRow(JDBCDataStore ds, String sql, List<Object> params) throws SQLException {
    getSql(ds).firstRow(sql, params)
}

/**
 * A variant of {@link #firstRow(JDBCDataStore, String, java.util.List)} useful when providing the named parameters as
 * named arguments.
 *
 * @param ds     {@link JDBCDataStore} on which the query is performed.
 * @param params A map containing the named parameters.
 * @param sql    The SQL statement.
 * @return A GroovyRowResult object or null if no row is found.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static GroovyRowResult firstRow(JDBCDataStore ds, Map params, String sql) throws SQLException {
    getSql(ds).firstRow(params, sql)
}

/**
 * Performs the given SQL query and return the first row of the result set.
 * 
 * An Object array variant of {@link #firstRow(JDBCDataStore, String, List)}.
 * 
 * This method supports named and named ordinal parameters by supplying such parameters in the params array. See the
 * class Javadoc for more details.
 *
 * @param ds     {@link JDBCDataStore} on which the query is performed.
 * @param sql    The SQL statement.
 * @param params An array of parameters.
 * @return A GroovyRowResult object or null if no row is found.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static GroovyRowResult firstRow(JDBCDataStore ds, String sql, Object[] params) throws SQLException {
    getSql(ds).firstRow(sql, params)
}

/**
 * Executes the given piece of SQL.
 * Also saves the updateCount, if any, for subsequent examination.
 * 
 * Example usages:
 * 
 * sql.execute "DROP TABLE IF EXISTS person"
 *
 * sql.execute """
 *     CREATE TABLE person (
 *         id INTEGER NOT NULL,
 *         firstname VARCHAR(100),
 *         lastname VARCHAR(100),
 *         location_id INTEGER
 *     )
 * """
 *
 * sql.execute """
 *     INSERT INTO person (id, firstname, lastname, location_id) VALUES (4, 'Paul', 'King', 40)
 * """
 * assert sql.updateCount == 1
 * 
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds  {@link JDBCDataStore} on which the query is performed.
 * @param sql The SQL to execute.
 * @return True if the first result is a ResultSet object; false if it is an update count or there are no results.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static boolean execute(JDBCDataStore ds, String sql) throws SQLException {
    getSql(ds).execute(sql)
}

/**
 * Executes the given piece of SQL.
 * Also calls the provided processResults Closure to process any ResultSet or UpdateCount results that executing the SQL might produce.
 * 
 * Example usages:
 * 
 * boolean first = true
 * sql.execute "{call FindAllByFirst('J')}", { isResultSet, result ->
 *   if (first) {
 *     first = false
 *     assert !isResultSet {@code &&} result == 0
 *   } else {
 *     assert isResultSet {@code &&} result == [[ID:1, FIRSTNAME:'James', LASTNAME:'Strachan'], [ID:4, FIRSTNAME:'Jean', LASTNAME:'Gabin']]
 *   }
 * }
 * 
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds  {@link JDBCDataStore} on which the query is performed.
 * @param sql The SQL to execute.
 * @param processResults A Closure which will be passed two parameters: either true plus a list of GroovyRowResult
 *                       values derived from statement.getResultSet() or false plus the update count from
 *                       statement.getUpdateCount().
 *                       The closure will be called for each result produced from executing the SQL.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void execute(JDBCDataStore ds, String sql,
                @ClosureParams(value=SimpleType.class,
                        options=["boolean,java.util.List<groovy.sql.GroovyRowResult>", "boolean,int"])
                        Closure processResults)
        throws SQLException {
    getSql(ds).execute(sql, processResults)
}

/**
 * Executes the given piece of SQL with parameters.
 * Also saves the updateCount, if any, for subsequent examination.
 * 
 * Example usage:
 * 
 * sql.execute """
 *     insert into PERSON (id, firstname, lastname, location_id) values (?, ?, ?, ?)
 * """, [1, "Guillaume", "Laforge", 10]
 * assert sql.updateCount == 1
 * 
 * 
 * This method supports named and named ordinal parameters.
 * See the class Javadoc for more details.
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds     {@link JDBCDataStore} on which the query is performed.
 * @param sql    The SQL statement.
 * @param params A list of parameters.
 * @return True if the first result is a ResultSet object; false if it is an update count or there are no results.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static boolean execute(JDBCDataStore ds, String sql, List<Object> params) throws SQLException {
    getSql(ds).execute(sql, params)
}

/**
 * Executes the given piece of SQL with parameters.
 * Also calls the provided processResults Closure to process any ResultSet or UpdateCount results that executing the SQL might produce.
 * 
 * This method supports named and named ordinal parameters.
 * See the class Javadoc for more details.
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds     {@link JDBCDataStore} on which the query is performed.
 * @param sql    The SQL statement.
 * @param params A list of parameters.
 * @param processResults A Closure which will be passed two parameters: either true plus a list of GroovyRowResult
 *                       values derived from {@code statement.getResultSet()} or {@code false} plus the update count from {@code statement.getUpdateCount()}.
 *                       The closure will be called for each result produced from executing the SQL.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void execute(JDBCDataStore ds, String sql, List<Object> params,
                    @ClosureParams(value=SimpleType.class,
                            options=["boolean,java.util.List<groovy.sql.GroovyRowResult>", "boolean,int"])
                            Closure processResults)
        throws SQLException {
    getSql(ds).execute(sql, params, processResults)
}

/**
 * A variant of {@link #execute(JDBCDataStore, String, java.util.List)} useful when providing the named parameters as
 * named arguments.
 *
 * @param ds     {@link JDBCDataStore} on which the query is performed.
 * @param params A map containing the named parameters.
 * @param sql    The SQL statement.
 * @return True if the first result is a ResultSet object; false if it is an update count or there are no results.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static boolean execute(JDBCDataStore ds, Map params, String sql) throws SQLException {
    getSql(ds).execute(params, sql)
}

/**
 * A variant of {@link #execute(JDBCDataStore, String, java.util.List, Closure)} useful when providing the named
 * parameters as named arguments.
 *
 * @param ds     {@link JDBCDataStore} on which the query is performed.
 * @param params A map containing the named parameters.
 * @param sql    The SQL statement.
 * @param processResults A Closure which will be passed two parameters: either true plus a list of GroovyRowResult
 *                       values derived from statement.getResultSet() or false plus the update count from
 *                       statement.getUpdateCount().
 *                       The closure will be called for each result produced from executing the SQL.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void execute(JDBCDataStore ds, Map params, String sql,
                    @ClosureParams(value=SimpleType.class,
                            options=["boolean,java.util.List<groovy.sql.GroovyRowResult>", "boolean,int"])
                            Closure processResults)
        throws SQLException {
    getSql(ds).execute(params, sql, processResults)
}

/**
 * Executes the given piece of SQL with parameters.
 * 
 * An Object array variant of {@link #execute(JDBCDataStore, String, List)}.
 * 
 * This method supports named and named ordinal parameters by supplying such parameters in the params array. See the
 * class Javadoc for more details.
 *
 * @param ds     {@link JDBCDataStore} on which the query is performed.
 * @param sql    The SQL statement.
 * @param params An array of parameters.
 * @return True if the first result is a ResultSet object; false if it is an update count or there are no results.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static boolean execute(JDBCDataStore ds, String sql, Object[] params) throws SQLException {
    getSql(ds).execute(sql, params)
}

/**
 * Executes the given piece of SQL with parameters.
 * 
 * An Object array variant of {@link #execute(JDBCDataStore, String, List, Closure)}.
 * 
 * This method supports named and named ordinal parameters by supplying such
 * parameters in the params array. See the class Javadoc for more details.
 *
 * @param ds     {@link JDBCDataStore} on which the query is performed.
 * @param sql    The SQL statement.
 * @param params An array of parameters.
 * @param processResults A Closure which will be passed two parameters: either true plus a list of GroovyRowResult values
 *                       derived from statement.getResultSet() or false plus the update count from
 *                       statement.getUpdateCount().
 *                       The closure will be called for each result produced from executing the SQL.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void execute(JDBCDataStore ds, String sql, Object[] params,
                    @ClosureParams(value=SimpleType.class,
                            options=["boolean,java.util.List<groovy.sql.GroovyRowResult>", "boolean,int"])
                            Closure processResults)
        throws SQLException {
    getSql(ds).execute(sql, params, processResults)
}

/**
 * Executes the given SQL with embedded expressions inside.
 * Also saves the updateCount, if any, for subsequent examination.
 * 
 * Example usage:
 * 
 * def scott = [firstname: "Scott", lastname: "Davis", id: 5, location_id: 50]
 * sql.execute """
 *     insert into PERSON (id, firstname, lastname, location_id) values ($scott.id, $scott.firstname, $scott.lastname, $scott.location_id)
 * """
 * assert sql.updateCount == 1
 * 
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param gstring A GString containing the SQL query with embedded params.
 * @return True if the first result is a ResultSet object; false if it is an update count or there are no results.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static boolean execute(JDBCDataStore ds, GString gstring) throws SQLException {
    getSql(ds).execute(gstring)
}

/**
 * Executes the given SQL with embedded expressions inside.
 * Also calls the provided processResults Closure to process any ResultSet or UpdateCount results that executing the
 * SQL might produce.
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param gstring A GString containing the SQL query with embedded params.
 * @param processResults A Closure which will be passed two parameters: either true plus a list of GroovyRowResult
 *                       values derived from statement.getResultSet() or false plus the update count from
 *                       statement.getUpdateCount().
 *                       The closure will be called for each result produced from executing the SQL.
 * @throws SQLException Thrown on a database access error occurrence.
 */
static void execute(JDBCDataStore ds, GString gstring,
                    @ClosureParams(value=SimpleType.class,
                            options=["boolean,java.util.List<groovy.sql.GroovyRowResult>", "boolean,int"])
                            Closure processResults)
        throws SQLException {
    getSql(ds).execute(gstring, processResults)
}