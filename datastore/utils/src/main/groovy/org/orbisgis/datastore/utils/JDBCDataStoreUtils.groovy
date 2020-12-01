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

import groovy.sql.Sql
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import org.geotools.jdbc.JDBCDataStore

import java.sql.SQLException
/**
 * Utility script used as extension module adding methods to JDBCDataStore class.
 *
 * @author Erwan Bocher (CNRS 2020)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */

//TODO : not implemented yet, the dataSet() feature needs some testing to check compatibility with geotools API.
/*
DataSet dataSet(JDBCDataStore ds, String table) {
    return new DataSet(new Sql(ds.dataSource.getConnection()), table)
}

DataSet dataSet(JDBCDataStore ds, Class<?> type) {
    return new DataSet(new Sql(ds.dataSource.getConnection()), type)
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
    new Sql(ds.dataSource.getConnection()).query(sql, closure)
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
    new Sql(ds.dataSource.getConnection()).query(sql, params, closure)
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
    new Sql(ds.dataSource.getConnection()).query(sql, map, closure)
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
    new Sql(ds.dataSource.getConnection()).query(map, sql, closure)
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
    new Sql(ds.dataSource.getConnection()).query(gstring, closure)
}