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
import groovy.sql.OutParameter
import groovy.sql.Sql
import groovy.transform.Field
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import org.geotools.data.DataStore
import org.geotools.jdbc.JDBCDataStore

import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Types

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

class OutParameters{
    public static final OutParameter ARRAY = new OutParameter ( ) { int getType ( ) { return Types.ARRAY } }
    public static final OutParameter BIGINT = new OutParameter ( ) { int getType ( ) { return Types.BIGINT } }
    public static final OutParameter BINARY = new OutParameter ( ) { int getType ( ) { return Types.BINARY } }
    public static final OutParameter BIT = new OutParameter ( ) { int getType ( ) { return Types.BIT } }
    public static final OutParameter BLOB = new OutParameter ( ) { int getType ( ) { return Types.BLOB } }
    public static final OutParameter BOOLEAN = new OutParameter ( ) { int getType ( ) { return Types.BOOLEAN } }
    public static final OutParameter CHAR = new OutParameter ( ) { int getType ( ) { return Types.CHAR } }
    public static final OutParameter CLOB = new OutParameter ( ) { int getType ( ) { return Types.CLOB } }
    public static final OutParameter DATALINK = new OutParameter ( ) { int getType ( ) { return Types.DATALINK } }
    public static final OutParameter DATE = new OutParameter ( ) { int getType ( ) { return Types.DATE } }
    public static final OutParameter DECIMAL = new OutParameter ( ) { int getType ( ) { return Types.DECIMAL } }
    public static final OutParameter DISTINCT = new OutParameter ( ) { int getType ( ) { return Types.DISTINCT } }
    public static final OutParameter DOUBLE = new OutParameter ( ) { int getType ( ) { return Types.DOUBLE } }
    public static final OutParameter FLOAT = new OutParameter ( ) { int getType ( ) { return Types.FLOAT } }
    public static final OutParameter INTEGER = new OutParameter ( ) { int getType ( ) { return Types.INTEGER } }
    public static final OutParameter JAVA_OBJECT = new OutParameter ( ) { int getType ( ) { return Types.JAVA_OBJECT } }
    public static final OutParameter LONGVARBINARY = new OutParameter ( ) { int getType ( ) { return Types.LONGVARBINARY } }
    public static final OutParameter LONGVARCHAR = new OutParameter ( ) { int getType ( ) { return Types.LONGVARCHAR } }
    public static final OutParameter NULL = new OutParameter ( ) { int getType ( ) { return Types.NULL } }
    public static final OutParameter NUMERIC = new OutParameter ( ) { int getType ( ) { return Types.NUMERIC } }
    public static final OutParameter OTHER = new OutParameter ( ) { int getType ( ) { return Types.OTHER } }
    public static final OutParameter REAL = new OutParameter ( ) { int getType ( ) { return Types.REAL } }
    public static final OutParameter REF = new OutParameter ( ) { int getType ( ) { return Types.REF } }
    public static final OutParameter SMALLINT = new OutParameter ( ) { int getType ( ) { return Types.SMALLINT } }
    public static final OutParameter STRUCT = new OutParameter ( ) { int getType ( ) { return Types.STRUCT } }
    public static final OutParameter TIME = new OutParameter ( ) { int getType ( ) { return Types.TIME } }
    public static final OutParameter TIMESTAMP = new OutParameter ( ) { int getType ( ) { return Types.TIMESTAMP } }
    public static final OutParameter TINYINT = new OutParameter ( ) { int getType ( ) { return Types.TINYINT } }
    public static final OutParameter VARBINARY = new OutParameter ( ) { int getType ( ) { return Types.VARBINARY } }
    public static final OutParameter VARCHAR = new OutParameter ( ) { int getType ( ) { return Types.VARCHAR } }
}

public static final @Field OutParameters out = new OutParameters()

/**
 * Method used to access to the {@link OutParameter}.
 *
 */
static def propertyMissing(JDBCDataStore ds, String name) {
    def type
    switch(name){
        case "ARRAY":       type = Types.ARRAY; break
        case "BIGINT":      type = Types.BIGINT; break
        case "BINARY":      type = Types.BINARY; break
        case "BIT":         type = Types.BIT; break
        case "BLOB":        type = Types.BLOB; break
        case "BOOLEAN":     type = Types.BOOLEAN; break
        case "CHAR":        type = Types.CHAR; break
        case "CLOB":        type = Types.CLOB; break
        case "DATALINK":    type = Types.DATALINK; break
        case "DATE":        type = Types.DATE; break
        case "DECIMAL":     type = Types.DECIMAL; break
        case "DISTINCT":    type = Types.DISTINCT; break
        case "DOUBLE":      type = Types.DOUBLE; break
        case "FLOAT":       type = Types.FLOAT; break
        case "INTEGER":     type = Types.INTEGER; break
        case "JAVA_OBJECT": type = Types.JAVA_OBJECT; break
        case "LONGVARBINARY":type = Types.LONGVARBINARY; break
        case "LONGVARCHAR": type = Types.LONGVARCHAR; break
        case "NULL":        type = Types.NULL; break
        case "NUMERIC":     type = Types.NUMERIC; break
        case "OTHER":       type = Types.OTHER; break
        case "REAL":        type = Types.REAL; break
        case "REF":         type = Types.REF; break
        case "SMALLINT":    type = Types.SMALLINT; break
        case "STRUCT":      type = Types.STRUCT; break
        case "TIME":        type = Types.TIME; break
        case "TIMESTAMP":   type = Types.TIMESTAMP; break
        case "TINYINT":     type = Types.TINYINT; break
        case "VARBINARY":   type = Types.VARBINARY; break
        case "VARCHAR":     type = Types.VARCHAR; break
        default : return (ds as DataStore).name
    }
    return new OutParameter(){ @Override int getType() { return type } }
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @param gstring     A GString containing the SQL query with embedded params..
 * @param metaClosure Called for meta data (only once after sql execution).
 * @param offset      The 1-based offset for the first row to be processed.
 * @param maxRows     The maximum number of rows to be processed.
 * @param rowClosure  Called for each row with a {@link GroovyResultSet}.
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @param gstring A GString containing the SQL query with embedded params..
 * @param offset  The 1-based offset for the first row to be processed.
 * @param maxRows The maximum number of rows to be processed.
 * @param closure Called for each row with a {@link GroovyResultSet}.
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @param gstring A GString containing the SQL query with embedded params..
 * @param closure Called for each row with a {@link GroovyResultSet}.
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @param gstring A GString containing the SQL query with embedded params..
 * @return A list of GroovyRowResult objects.
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @param gstring     A GString containing the SQL query with embedded params..
 * @param metaClosure Called with meta data of the ResultSet.
 * @return A list of GroovyRowResult objects.
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @param gstring A GString containing the SQL query with embedded params..
 * @return A GroovyRowResult object or null if no row is found.
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @param gstring A GString containing the SQL query with embedded params..
 * @return True if the first result is a ResultSet object; false if it is an update count or there are no results.
 * @throws SQLException Thrown on a database manipulation error occurrence.
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
 * @param gstring A GString containing the SQL query with embedded params..
 * @param processResults A Closure which will be passed two parameters: either true plus a list of GroovyRowResult
 *                       values derived from statement.getResultSet() or false plus the update count from
 *                       statement.getUpdateCount().
 *                       The closure will be called for each result produced from executing the SQL.
 * @throws SQLException Thrown on a database manipulation error occurrence.
 */
static void execute(JDBCDataStore ds, GString gstring,
                    @ClosureParams(value=SimpleType.class,
                            options=["boolean,java.util.List<groovy.sql.GroovyRowResult>", "boolean,int"])
                            Closure processResults)
        throws SQLException {
    getSql(ds).execute(gstring, processResults)
}

/**
 * Executes the given SQL statement (typically an INSERT statement).
 * Use this variant when you want to receive the values of any auto-generated columns, such as an autoincrement ID
 * field.
 * See {@link #executeInsert(JDBCDataStore, GString)} for more details.
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds  {@link JDBCDataStore} on which the query is performed.
 * @param sql The SQL statement to execute.
 * @return A list of the auto-generated column values for each inserted row (typically auto-generated keys).
 * @throws SQLException Thrown on a database manipulation error occurrence.
 */
static List<List<Object>> executeInsert(JDBCDataStore ds, String sql) throws SQLException {
    getSql(ds).executeInsert(sql)
}

/**
 * Executes the given SQL statement (typically an INSERT statement).
 * Use this variant when you want to receive the values of any auto-generated columns, such as an autoincrement ID
 * field.
 * The query may contain placeholder question marks which match the given list of parameters.
 * See {@link #executeInsert(JDBCDataStore, GString)} for more details.
 * 
 * This method supports named and named ordinal parameters.
 * See the class Javadoc for more details.
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds     {@link JDBCDataStore} on which the query is performed.
 * @param sql    The SQL statement to execute.
 * @param params The parameter values that will be substituted into the SQL statement's parameter slots.
 * @return A list of the auto-generated column values for each inserted row (typically auto-generated keys).
 * @throws SQLException Thrown on a database manipulation error occurrence.
 */
static List<List<Object>> executeInsert(JDBCDataStore ds, String sql, List<Object> params) throws SQLException {
    getSql(ds).executeInsert(sql, params)
}

/**
 * Executes the given SQL statement (typically an INSERT statement).
 * Use this variant when you want to receive the values of any auto-generated columns, such as an autoincrement ID
 * field (or fields) and you know the column name(s) of the ID field(s).
 * The query may contain placeholder question marks which match the given list of parameters.
 * See {@link #executeInsert(JDBCDataStore, GString)} for more details.
 * 
 * This method supports named and named ordinal parameters.
 * See the class Javadoc for more details.
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds             {@link JDBCDataStore} on which the query is performed.
 * @param sql            The SQL statement to execute.
 * @param params         The parameter values that will be substituted into the SQL statement's parameter slots.
 * @param keyColumnNames A list of column names indicating the columns that should be returned from the
 *                       inserted row or rows (some drivers may be case sensitive, e.g. may require uppercase names).
 * @return A list of the auto-generated row results for each inserted row (typically auto-generated keys).
 * @throws SQLException Thrown on a database manipulation error occurrence.
 */
static List<GroovyRowResult> executeInsert(JDBCDataStore ds, String sql, List<Object> params,
                                           List<String> keyColumnNames) throws SQLException {
    getSql(ds).executeInsert(sql, params, keyColumnNames)
}

/**
 * A variant of {@link #executeInsert(JDBCDataStore, String, java.util.List)} useful when providing the named 
 * parameters as named arguments.
 *
 * @param ds     {@link JDBCDataStore} on which the query is performed.
 * @param params A map containing the named parameters.
 * @param sql    The SQL statement to execute.
 * @return A list of the auto-generated column values for each inserted row (typically auto-generated keys).
 * @throws SQLException Thrown on a database manipulation error occurrence.
 */
static List<List<Object>> executeInsert(JDBCDataStore ds, Map params, String sql) throws SQLException {
    getSql(ds).executeInsert(params, sql)
}

/**
 * A variant of {@link #executeInsert(JDBCDataStore, String, List, List)} useful when providing the named parameters
 * as named arguments.
 * This variant allows you to receive the values of any auto-generated columns, such as an autoincrement ID field
 * (or fields) when you know the column name(s) of the ID field(s).
 *
 * @param ds             {@link JDBCDataStore} on which the query is performed.
 * @param params         A map containing the named parameters.
 * @param sql            The SQL statement to execute.
 * @param keyColumnNames A list of column names indicating the columns that should be returned from the
 *                       inserted row or rows (some drivers may be case sensitive, e.g. may require uppercase names).
 * @return A list of the auto-generated row results for each inserted row (typically auto-generated keys).
 * @throws SQLException Thrown on a database manipulation error occurrence.
 */
static List<GroovyRowResult> executeInsert(JDBCDataStore ds, Map params, String sql, List<String> keyColumnNames)
        throws SQLException {
    getSql(ds).executeInsert(params, sql, keyColumnNames)
}

/**
 * Executes the given SQL statement (typically an INSERT statement).
 * 
 * An Object array variant of {@link #executeInsert(JDBCDataStore, String, List)}.
 * 
 * This method supports named and named ordinal parameters by supplying such
 * parameters in the params array. See the class Javadoc for more details.
 *
 * @param ds     {@link JDBCDataStore} on which the query is performed.
 * @param params A map containing the named parameters.
 * @param sql    The SQL statement to execute
 * @param params The parameter values that will be substituted into the SQL statement's parameter slots.
 * @return A list of the auto-generated column values for each inserted row (typically auto-generated keys).
 * @throws SQLException Thrown on a database manipulation error occurrence.
 */
static List<List<Object>> executeInsert(JDBCDataStore ds, String sql, Object[] params) throws SQLException {
    getSql(ds).executeInsert(sql, params)
}

/**
 * Executes the given SQL statement (typically an INSERT statement).
 * This variant allows you to receive the values of any auto-generated columns,
 * such as an autoincrement ID field (or fields) when you know the column name(s) of the ID field(s).
 *
 * This method supports named and named ordinal parameters by supplying such
 * parameters in the params array. See the class Javadoc for more details.
 *
 * @param ds             {@link JDBCDataStore} on which the query is performed.
 * @param params         A map containing the named parameters.
 * @param sql            The SQL statement to execute
 * @param keyColumnNames An array of column names indicating the columns that should be returned from the
 *                       inserted row or rows (some drivers may be case sensitive, e.g. may require uppercase names).
 * @return A list of the auto-generated row results for each inserted row (typically auto-generated keys).
 * @throws SQLException Thrown on a database manipulation error occurrence.
 */
static List<GroovyRowResult> executeInsert(JDBCDataStore ds, String sql, String[] keyColumnNames) throws SQLException {
    getSql(ds).executeInsert(sql, keyColumnNames)
}

/**
 * Executes the given SQL statement (typically an INSERT statement).
 * This variant allows you to receive the values of any auto-generated columns,
 * such as an autoincrement ID field (or fields) when you know the column name(s) of the ID field(s).
 *
 * An array variant of {@link #executeInsert(JDBCDataStore, String, List, List)}.
 *
 * This method supports named and named ordinal parameters by supplying such
 * parameters in the params array. See the class Javadoc for more details.
 *
 * @param ds             {@link JDBCDataStore} on which the query is performed.
 * @param params         A map containing the named parameters.
 * @param sql            The SQL statement to execute.
 * @param keyColumnNames An array of column names indicating the columns that should be returned from the
 *                       inserted row or rows (some drivers may be case sensitive, e.g. may require uppercase names).
 * @param params         The parameter values that will be substituted into the SQL statement's parameter slots.
 * @return A list of the auto-generated row results for each inserted row (typically auto-generated keys).
 * @throws SQLException Thrown on a database manipulation error occurrence.
 */
static List<GroovyRowResult> executeInsert(JDBCDataStore ds, String sql, String[] keyColumnNames, Object[] params)
        throws SQLException {
    getSql(ds).executeInsert(sql, keyColumnNames, params)
}

/**
 * Executes the given SQL statement (typically an INSERT statement).
 * Use this variant when you want to receive the values of any auto-generated columns, such as an autoincrement ID
 * field.
 * The query may contain GString expressions.
 * 
 * Generated key values can be accessed using array notation. For example, to return the second auto-generated column
 * value of the third row, use keys[3][1]. The method is designed to be used with SQL INSERT statements, but is not
 * limited to them.
 * 
 * The standard use for this method is when a table has an autoincrement ID column and you want to know what the ID is
 * for a newly inserted row. In this example, we insert a single row into a table in which the first column contains
 * the autoincrement ID:
 *
 * def sql = Sql.newInstance("jdbc:mysql://localhost:3306/groovy",
 *                           "user",
 *                           "password",
 *                           "com.mysql.jdbc.Driver")
 *
 * def keys = sql.executeInsert("insert into test_table (INT_DATA, STRING_DATA) "
 *                       + "VALUES (1, 'Key Largo')")
 *
 * def id = keys[0][0]
 *
 * // 'id' now contains the value of the new row's ID column.
 * // It can be used to update an object representation's
 * // id attribute for example.
 * ...
 *
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param params  A map containing the named parameters.
 * @param gstring A GString containing the SQL query with embedded params.
 * @return A list of the auto-generated column values for each inserted row (typically auto-generated keys).
 * @throws SQLException Thrown on a database manipulation error occurrence.
 */
static List<List<Object>> executeInsert(JDBCDataStore ds, GString gstring) throws SQLException {
    getSql(ds).executeInsert(gstring)
}

/**
 * Executes the given SQL statement (typically an INSERT statement).
 * Use this variant when you want to receive the values of any auto-generated columns, such as an autoincrement ID 
 * field (or fields) and you know the column name(s) of the ID field(s).
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds             {@link JDBCDataStore} on which the query is performed.
 * @param params         A map containing the named parameters.
 * @param gstring        A GString containing the SQL query with embedded params.
 * @param keyColumnNames A list of column names indicating the columns that should be returned from the
 *                       inserted row or rows (some drivers may be case sensitive, e.g. may require uppercase names).
 * @return A list of the auto-generated row results for each inserted row (typically auto-generated keys).
 * @throws SQLException Thrown on a database manipulation error occurrence.
 */
static List<GroovyRowResult> executeInsert(JDBCDataStore ds, GString gstring, List<String> keyColumnNames)
        throws SQLException {
    getSql(ds).executeInsert(gstring, keyColumnNames)
}

/**
 * Executes the given SQL update.
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds  {@link JDBCDataStore} on which the query is performed.
 * @param sql The SQL to execute.
 * @return The number of rows updated or 0 for SQL statements that return nothing.
 * @throws SQLException Thrown on a database manipulation error occurrence.
 */
static int executeUpdate(JDBCDataStore ds, String sql) throws SQLException {
    getSql(ds).executeUpdate(sql)
}

/**
 * Executes the given SQL update with parameters.
 * 
 * This method supports named and named ordinal parameters.
 * See the class Javadoc for more details.
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds     {@link JDBCDataStore} on which the query is performed.
 * @param sql    The SQL statement.
 * @param params A list of parameters.
 * @return The number of rows updated or 0 for SQL statements that return nothing.
 * @throws SQLException Thrown on a database manipulation error occurrence.
 */
static int executeUpdate(JDBCDataStore ds, String sql, List<Object> params) throws SQLException {
    getSql(ds).executeUpdate(sql, params)
}

/**
 * A variant of {@link #executeUpdate(JDBCDataStore, String, java.util.List)} useful when providing the named
 * parameters as named arguments.
 *
 * @param ds     {@link JDBCDataStore} on which the query is performed.
 * @param params A map containing the named parameters.
 * @param sql    The SQL statement.
 * @return The number of rows updated or 0 for SQL statements that return nothing.
 * @throws SQLException Thrown on a database manipulation error occurrence.
 */
static int executeUpdate(JDBCDataStore ds, Map params, String sql) throws SQLException {
    getSql(ds).executeUpdate(params, sql)
}

/**
 * Executes the given SQL update with parameters.
 * 
 * An Object array variant of {@link #executeUpdate(JDBCDataStore, String, List)}.
 *
 * @param ds     {@link JDBCDataStore} on which the query is performed.
 * @param sql    The SQL statement.
 * @param params An array of parameters.
 * @return The number of rows updated or 0 for SQL statements that return nothing.
 * @throws SQLException Thrown on a database manipulation error occurrence.
 */
static int executeUpdate(JDBCDataStore ds, String sql, Object[] params) throws SQLException {
    getSql(ds).executeUpdate(sql, params)
}

/**
 * Executes the given SQL update with embedded expressions inside.
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param gstring A GString containing the SQL query with embedded params.
 * @return The number of rows updated or 0 for SQL statements that return nothing.
 * @throws SQLException Thrown on a database manipulation error occurrence.
 */
static int executeUpdate(JDBCDataStore ds, GString gstring) throws SQLException {
    getSql(ds).executeUpdate(gstring)
}

/**
 * Performs a stored procedure call.
 * 
 * Example usage (tested with MySQL) - suppose we have the following stored procedure:
 * 
 * sql.execute """
 *     CREATE PROCEDURE HouseSwap(_first1 VARCHAR(50), _first2 VARCHAR(50))
 *     BEGIN
 *         DECLARE _loc1 INT;
 *         DECLARE _loc2 INT;
 *         SELECT location_id into _loc1 FROM PERSON where firstname = _first1;
 *         SELECT location_id into _loc2 FROM PERSON where firstname = _first2;
 *         UPDATE PERSON
 *         set location_id = case firstname
 *             when _first1 then _loc2
 *             when _first2 then _loc1
 *         end
 *         where (firstname = _first1 OR firstname = _first2);
 *     END
 * """
 * 
 * then you can invoke the procedure as follows:
 * 
 * def rowsChanged = sql.call("{call HouseSwap('Guillaume', 'Paul')}")
 * assert rowsChanged == 2
 * 
 *
 * @param ds  {@link JDBCDataStore} on which the query is performed.
 * @param sql The SQL statement.
 * @return The number of rows updated or 0 for SQL statements that return nothing.
 * @throws SQLException Thrown on a database manipulation error occurrence.
 */
static int call(JDBCDataStore ds, String sql) throws Exception {
    getSql(ds).call(sql)
}

/**
 * Performs a stored procedure call with the given embedded parameters.
 * 
 * Example usage - see {@link #call(JDBCDataStore, String)} for more details about creating a HouseSwap(IN name1,
 * IN name2) stored procedure.
 * Once created, it can be called like this:
 * 
 * def p1 = 'Paul'
 * def p2 = 'Guillaume'
 * def rowsChanged = sql.call("{call HouseSwap($p1, $p2)}")
 * assert rowsChanged == 2
 * 
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param gstring A GString containing the SQL query with embedded params.
 * @return The number of rows updated or 0 for SQL statements that return nothing.
 * @throws SQLException Thrown on a database manipulation error occurrence.
 */
static int call(JDBCDataStore ds, GString gstring) throws Exception {
    getSql(ds).call(gstring)
}

/**
 * Performs a stored procedure call with the given parameters.
 * 
 * Example usage - see {@link #call(JDBCDataStore, String)} for more details about creating a HouseSwap(IN name1,
 * IN name2) stored procedure.
 * Once created, it can be called like this:
 * 
 * def rowsChanged = sql.call("{call HouseSwap(?, ?)}", ['Guillaume', 'Paul'])
 * assert rowsChanged == 2
 * 
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds     {@link JDBCDataStore} on which the query is performed.
 * @param sql    The SQL statement.
 * @param params A list of parameters.
 * @return The number of rows updated or 0 for SQL statements that return nothing.
 * @throws SQLException Thrown on a database manipulation error occurrence.
 */
static int call(JDBCDataStore ds, String sql, List<Object> params) throws Exception {
    getSql(ds).call(sql, params)
}

/**
 * Performs a stored procedure call with the given parameters.
 * 
 * An Object array variant of {@link #call(JDBCDataStore, String, List)}.
 *
 * @param ds     {@link JDBCDataStore} on which the query is performed.
 * @param sql    The SQL statement.
 * @param params An array of parameters.
 * @return The number of rows updated or 0 for SQL statements that return nothing.
 * @throws SQLException Thrown on a database manipulation error occurrence.
 */
static int call(JDBCDataStore ds, String sql, Object[] params) throws Exception {
    getSql(ds).call(sql, params)
}

/**
 * Performs a stored procedure call with the given parameters.  The closure is called once with all the out parameters.
 * 
 * Example usage - suppose we create a stored procedure (ignore its simplistic implementation):
 * 
 * // Tested with MySql 5.0.75
 * sql.execute """
 *     CREATE PROCEDURE Hemisphere(
 *         IN p_firstname VARCHAR(50),
 *         IN p_lastname VARCHAR(50),
 *         OUT ans VARCHAR(50))
 *     BEGIN
 *     DECLARE loc INT;
 *     SELECT location_id into loc FROM PERSON where firstname = p_firstname and lastname = p_lastname;
 *     CASE loc
 *         WHEN 40 THEN
 *             SET ans = 'Southern Hemisphere';
 *         ELSE
 *             SET ans = 'Northern Hemisphere';
 *     END CASE;
 *     END;
 * """
 * 
 * we can now call the stored procedure as follows:
 * 
 * sql.call '{call Hemisphere(?, ?, ?)}', ['Guillaume', 'Laforge', Sql.VARCHAR], { dwells ->
 *     println dwells
 * }
 * 
 * which will output 'Northern Hemisphere'.
 * 
 * We can also access stored functions with scalar return values where the return value will be treated as an OUT
 * parameter. Here are examples for various databases for creating such a procedure:
 * 
 * // Tested with MySql 5.0.75
 * sql.execute """
 *     create function FullName(p_firstname VARCHAR(40)) returns VARCHAR(80)
 *     begin
 *         declare ans VARCHAR(80);
 *         SELECT CONCAT(firstname, ' ', lastname) INTO ans FROM PERSON WHERE firstname = p_firstname;
 *         return ans;
 *     end
 * """
 *
 * // Tested with MS SQLServer Express 2008
 * sql.execute """
 *     {@code create function FullName(@firstname VARCHAR(40)) returns VARCHAR(80)}
 *     begin
 *         declare {@code @ans} VARCHAR(80)
 *         {@code SET @ans = (SELECT firstname + ' ' + lastname FROM PERSON WHERE firstname = @firstname)}
 *         return {@code @ans}
 *     end
 * """
 *
 * // Tested with Oracle XE 10g
 * sql.execute """
 *     create function FullName(p_firstname VARCHAR) return VARCHAR is
 *     ans VARCHAR(80);
 *     begin
 *         SELECT CONCAT(CONCAT(firstname, ' '), lastname) INTO ans FROM PERSON WHERE firstname = p_firstname;
 *         return ans;
 *     end;
 * """
 * 
 * and here is how you access the stored function for all databases:
 * 
 * sql.call("{? = call FullName(?)}", [Sql.VARCHAR, 'Sam']) { name ->
 *     assert name == 'Sam Pullara'
 * }
 * 
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param sql     The sql statement.
 * @param params  A list of parameters.
 * @param closure Called for each row with a GroovyResultSet.
 * @throws SQLException Thrown on a database manipulation error occurrence.
 */
static void call(JDBCDataStore ds, String sql, List<Object> params, 
                 @ClosureParams(value=SimpleType.class, options="java.lang.Object[]") Closure closure) 
        throws Exception {
    getSql(ds).call(sql, params, closure)
}

/**
 * Performs a stored procedure call with the given parameters, calling the closure once with all result objects.
 * 
 * See {@link #call(JDBCDataStore, String, List, Closure)} for more details about creating a Hemisphere(IN first, 
 * IN last, OUT dwells) stored procedure.
 * Once created, it can be called like this:
 * 
 * def first = 'Scott'
 * def last = 'Davis'
 * sql.call "{call Hemisphere($first, $last, ${Sql.VARCHAR})}", { dwells ->
 *     println dwells
 * }
 * 
 * 
 * As another example, see {@link #call(JDBCDataStore, String, List, Closure)} for more details about creating a 
 * FullName(IN first) stored function.
 * Once created, it can be called like this:
 * 
 * def first = 'Sam'
 * sql.call("{$Sql.VARCHAR = call FullName($first)}") { name ->
 *     assert name == 'Sam Pullara'
 * }
 * 
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param gstring A GString containing the SQL query with embedded params.
 * @param closure Called for each row with a GroovyResultSet.
 * @throws SQLException Thrown on a database manipulation error occurrence.
 */
static void call(JDBCDataStore ds, GString gstring,
                 @ClosureParams(value=SimpleType.class, options="java.lang.Object[]") Closure closure)
        throws Exception {
    getSql(ds).call(gstring, closure)
}

/**
 * Performs a stored procedure call with the given parameters, calling the closure once with all result objects,
 * and also returning the rows of the ResultSet.
 * 
 * Use this when calling a stored procedure that utilizes both
 * output parameters and returns a single ResultSet.
 * 
 * Once created, the stored procedure can be called like this:
 * 
 * def first = 'Jeff'
 * def last = 'Sheets'
 * def rows = sql.callWithRows "{call Hemisphere2($first, $last, ${Sql.VARCHAR})}", { dwells ->
 *     println dwells
 * }
 * 
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param gstring A GString containing the SQL query with embedded params.
 * @param closure Called once with all out parameter results.
 * @return A list of GroovyRowResult objects.
 * @throws SQLException Thrown on a database manipulation error occurrence.
 */
static List<GroovyRowResult> callWithRows(JDBCDataStore ds, GString gstring,
                      @ClosureParams(value=SimpleType.class, options="java.lang.Object[]") Closure closure)
        throws SQLException {
    getSql(ds).callWithRows(gstring, closure)
}

/**
 * Performs a stored procedure call with the given parameters, calling the closure once with all result objects,
 * and also returning the rows of the ResultSet.
 * 
 * Use this when calling a stored procedure that utilizes both output parameters and returns a single ResultSet.
 * 
 * Once created, the stored procedure can be called like this:
 * 
 * def rows = sql.callWithRows '{call Hemisphere2(?, ?, ?)}', ['Guillaume', 'Laforge', Sql.VARCHAR], { dwells ->
 *     println dwells
 * }
 * 
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param sql     The sql statement.
 * @param params  A list of parameters.
 * @param closure Called once with all out parameter results.
 * @return A list of GroovyRowResult objects.
 * @throws SQLException Thrown on a database manipulation error occurrence.
 */
static List<GroovyRowResult> callWithRows(JDBCDataStore ds, String sql, List<Object> params,
                  @ClosureParams(value=SimpleType.class, options="java.lang.Object[]") Closure closure)
        throws SQLException {
    getSql(ds).callWithRows(sql, params, closure)
}

/**
 * Performs a stored procedure call with the given parameters, calling the closure once with all result objects,
 * and also returning a list of lists with the rows of the ResultSet(s).
 * 
 * Use this when calling a stored procedure that utilizes both output parameters and returns multiple ResultSets.
 * 
 * Once created, the stored procedure can be called like this:
 * 
 * def first = 'Jeff'
 * def last = 'Sheets'
 * def rowsList = sql.callWithAllRows "{call Hemisphere2($first, $last, ${Sql.VARCHAR})}", { dwells ->
 *     println dwells
 * }
 * 
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param gstring a GString containing the SQL query with embedded params
 * @param closure called once with all out parameter results
 * @return a list containing lists of GroovyRowResult objects
 * @throws SQLException Thrown on a database manipulation error occurrence.
 */
static List<List<GroovyRowResult>> callWithAllRows(JDBCDataStore ds, GString gstring,
                   @ClosureParams(value=SimpleType.class, options="java.lang.Object[]") Closure closure)
        throws SQLException {
    getSql(ds).callWithAllRows(gstring, closure)
}

/**
 * Performs a stored procedure call with the given parameters, calling the closure once with all result objects,
 * and also returning a list of lists with the rows of the ResultSet(s).
 * 
 * Use this when calling a stored procedure that utilizes both output parameters and returns multiple ResultSets.
 * 
 * Once created, the stored procedure can be called like this:
 * 
 * def rowsList = sql.callWithAllRows '{call Hemisphere2(?, ?, ?)}', ['Guillaume', 'Laforge', Sql.VARCHAR], { dwells ->
 *     println dwells
 * }
 * 
 * 
 * Resource handling is performed automatically where appropriate.
 *
 * @param ds      {@link JDBCDataStore} on which the query is performed.
 * @param sql     The sql statement.
 * @param params  A list of parameters.
 * @param closure Called once with all out parameter results.
 * @return A list containing lists of GroovyRowResult objects.
 * @throws SQLException Thrown on a database manipulation error occurrence.
 */
static List<List<GroovyRowResult>> callWithAllRows(JDBCDataStore ds, String sql, List<Object> params, 
                   @ClosureParams(value=SimpleType.class, options="java.lang.Object[]") Closure closure) 
        throws SQLException {
    getSql(ds).callWithAllRows(sql, params, closure)
}