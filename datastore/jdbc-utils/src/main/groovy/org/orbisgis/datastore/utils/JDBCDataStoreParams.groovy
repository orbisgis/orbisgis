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

import groovy.transform.Field

import javax.sql.DataSource

/**
 * Groovy script used as Closure DelegateTo on opening a JDBCDataStore.
 *
 * @author Erwan Bocher (CNRS 2020)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */

/** Parameters for the DataSTore creation. */
protected final @Field Map params = [:]

/**
 * Returns the parameters for the DataStore creation.
 * @return The Map of the parameters for the DataStore creation.
 */
Map params(){
    return params
}

/**
 * Password used to login.
 * @param password Password used to login.
 */
void password(String password) {
    params.put(JDBCDataStoreParamsCst.PASSWD, password)
}

/**
 * Namespace prefix.
 * @param namespace Namespace prefix.
 */
void namespace(String namespace) {
    params.put(JDBCDataStoreParamsCst.NAMESPACE, namespace)
}

/**
 * Data Source.
 * @param dataSource Data Source.
 */
void dataSource(DataSource dataSource) {
    params.put(JDBCDataStoreParamsCst.DATASOURCE, dataSource)
}

/**
 * Maximum number of open connections.
 * @param maxConn Maximum number of open connections.
 */
void maxConn(int maxConn) {
    params.put(JDBCDataStoreParamsCst.MAXCONN, maxConn)
}

/**
 * Minimum number of pooled connection.
 * @param minConn Minimum number of pooled connection.
 */
void minConn(int minConn) {
    params.put(JDBCDataStoreParamsCst.MINCONN, minConn)
}

/**
 * Check connection is alive before using it.
 * @param validateConn True to check connection is alive before using it.
 */
void validateConn(boolean validateConn) {
    params.put(JDBCDataStoreParamsCst.VALIDATECONN, validateConn)
}

/**
 * Number of records read with each iteration with the dbms.
 * @param fetchSize Number of records read with each iteration with the dbms.
 */
void fetchSize(int fetchSize) {
    params.put(JDBCDataStoreParamsCst.FETCHSIZE, fetchSize)
}

/**
 * Number of records inserted in the same batch (default, 1). For optimal performance, set to 100.
 * @param batchInsertSize Number of records inserted in the same batch (default, 1). For optimal performance, set to 100.
 */
void batchInsertSize(int batchInsertSize) {
    params.put(JDBCDataStoreParamsCst.BATCH_INSERT_SIZE, batchInsertSize)
}

/**
 * Number of seconds the connection pool will wait before timing out attempting to get a new connection (default, 20 seconds).
 * @param maxWait Number of seconds the connection pool will wait before timing out attempting to get a new connection (default, 20 seconds).
 */
void maxWait(int maxWait) {
    params.put(JDBCDataStoreParamsCst.MAXWAIT, maxWait)
}

/**
 * Periodically test the connections are still valid also while idle in the pool.
 * @param testWhileIdle Periodically test the connections are still valid also while idle in the pool.
 */
void testWhileIdle(boolean testWhileIdle) {
    params.put(JDBCDataStoreParamsCst.TEST_WHILE_IDLE, testWhileIdle  )
}

/**
 * Number of seconds between idle object evitor runs (default, 300 seconds).
 * @param timeBetweenEvicotrRuns Number of seconds between idle object evitor runs (default, 300 seconds).
 */
void timeBetweenEvicotrRuns(int timeBetweenEvicotrRuns) {
    params.put(JDBCDataStoreParamsCst.TIME_BETWEEN_EVICTOR_RUNS, timeBetweenEvicotrRuns)
}

/**
 * Number of seconds a connection needs to stay idle for the evictor to consider closing it.
 * @param minEvictaleTime Number of seconds a connection needs to stay idle for the evictor to consider closing it.
 */
void minEvictaleTime(int minEvictaleTime) {
    params.put(JDBCDataStoreParamsCst.MIN_EVICTABLE_TIME, minEvictaleTime)
}

/**
 * Number of connections checked by the idle connection evictor for each of its runs (defaults to 3).
 * @param evictorTestsPerRun Number of connections checked by the idle connection evictor for each of its runs
 * (defaults to 3).
 */
void evictorTestsPerRun(int evictorTestsPerRun) {
    params.put(JDBCDataStoreParamsCst.EVICTOR_TESTS_PER_RUN, evictorTestsPerRun)
}

/**
 * The optional table containing primary key structure and sequence associations. Can be expressed as 'schema.name'
 * or just 'name'.
 * @param pkMetadataTable The optional table containing primary key structure and sequence associations. Can be
 * expressed as 'schema.name' or just 'name'.
 */
void pkMetadataTable(String pkMetadataTable) {
    params.put(JDBCDataStoreParamsCst.PK_METADATA_TABLE, pkMetadataTable)
}

/**
 * Maximum number of prepared statements kept open and cached for each connection in the pool. Set to 0 to have
 * unbounded caching, to -1 to disable caching.
 * @param maxOpenPreparedStatement Maximum number of prepared statements kept open and cached for each connection in
 * the pool. Set to 0 to have unbounded caching, to -1 to disable caching.
 */
void maxOpenPreparedStatement(int maxOpenPreparedStatement) {
    params.put(JDBCDataStoreParamsCst.MAX_OPEN_PREPARED_STATEMENTS, maxOpenPreparedStatement)
}

/**
 * Expose primary key columns as attributes of the feature type.
 * @param exposePk Expose primary key columns as attributes of the feature type.
 */
void exposePk(boolean exposePk) {
    params.put(JDBCDataStoreParamsCst.EXPOSE_PK, exposePk)
}

/**
 * SQL statement executed when the connection is grabbed from the pool.
 * @param sqlOnBorrow SQL statement executed when the connection is grabbed from the pool.
 */
void sqlOnBorrow(String sqlOnBorrow) {
    params.put(JDBCDataStoreParamsCst.SQL_ON_BORROW, sqlOnBorrow)
}

/**
 * SQL statement executed when the connection is released to the pool.
 * @param sqlOnRelease SQL statement executed when the connection is released to the pool.
 */
void sqlOnRelease(String sqlOnRelease) {
    params.put(JDBCDataStoreParamsCst.SQL_ON_RELEASE, sqlOnRelease)
}

/**
 * Name of JDBCReaderCallbackFactory to enable on the data store.
 * @param callbackFactory Name of JDBCReaderCallbackFactory to enable on the data store.
 */
void callbackFactory(String callbackFactory) {
    params.put(JDBCDataStoreParamsCst.CALLBACK_FACTORY, callbackFactory)
}

/**
 * Host.
 * @param host Host.
 */
void host(String host) {
    params.put(JDBCDataStoreParamsCst.HOST, host)
}

/**
 * Port.
 * @param port Port.
 */
void port(int port) {
    params.put(JDBCDataStoreParamsCst.PORT, port)
}

/**
 * Database.
 * @param database Database.
 */
void database(String database) {
    params.put(JDBCDataStoreParamsCst.DATABASE, database)
}

/**
 * Schema.
 * @param schema Schema.
 */
void schema(String schema) {
    params.put(JDBCDataStoreParamsCst.SCHEMA, schema)
}

/**
 * User.
 * @param user User.
 */
void user(String user) {
    params.put(JDBCDataStoreParamsCst.USER, user)
}