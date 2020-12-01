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
package org.orbisgis.datastore.utils

import groovy.sql.GroovyResultSet
import groovy.sql.Sql
import groovy.transform.Field
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import org.geotools.data.DataStore
import org.geotools.jdbc.JDBCDataStore

import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException 
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
 * @throws SQLException if a database access error occurs
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
 * In addition, the <code>metaClosure</code> will be called once passing in the {@link java.sql.ResultSetMetaData} as
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