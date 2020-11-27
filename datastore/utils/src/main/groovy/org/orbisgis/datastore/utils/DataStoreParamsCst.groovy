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
import org.geotools.jdbc.JDBCDataStoreFactory

import javax.sql.DataSource

/**
 * Groovy script used to declare DataStore parameters constants
 *
 * @author Erwan Bocher (CNRS 2020)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */

/** Password used to login. */
public static final @Field String PASSWD = JDBCDataStoreFactory.PASSWD.key

/** Namespace prefix. */
public static final @Field String NAMESPACE = JDBCDataStoreFactory.NAMESPACE.key

/** Data Source. */
public static final @Field String DATASOURCE = JDBCDataStoreFactory.DATASOURCE.key

/** Maximum number of open connections. */
public static final @Field String MAXCONN = JDBCDataStoreFactory.MAXCONN.key

/** Minimum number of pooled connection. */
public static final @Field String MINCONN = JDBCDataStoreFactory.MINCONN.key

/** Check connection is alive before using it. */
public static final @Field String VALIDATECONN = JDBCDataStoreFactory.VALIDATECONN.key

/** Number of records read with each iteration with the dbms. */
public static final @Field String FETCHSIZE = JDBCDataStoreFactory.FETCHSIZE.key

/** Number of records inserted in the same batch (default, 1). For optimal performance, set to 100. */
public static final @Field String BATCH_INSERT_SIZE = JDBCDataStoreFactory.BATCH_INSERT_SIZE.key

/** Number of seconds the connection pool will wait before timing out attempting to get a new connection
 * (default, 20 seconds).*/
public static final @Field String MAXWAIT = JDBCDataStoreFactory.MAXWAIT.key

/** Periodically test the connections are still valid also while idle in the pool. */
public static final @Field String TEST_WHILE_IDLE = JDBCDataStoreFactory.TEST_WHILE_IDLE.key

/** Number of seconds between idle object evitor runs (default, 300 seconds). */
public static final @Field String TIME_BETWEEN_EVICTOR_RUNS = JDBCDataStoreFactory.TIME_BETWEEN_EVICTOR_RUNS.key

/** Number of seconds a connection needs to stay idle for the evictor to consider closing it. */
public static final @Field String MIN_EVICTABLE_TIME = JDBCDataStoreFactory.MIN_EVICTABLE_TIME.key

/** Number of connections checked by the idle connection evictor for each of its runs (defaults to 3). */
public static final @Field String EVICTOR_TESTS_PER_RUN = JDBCDataStoreFactory.EVICTOR_TESTS_PER_RUN.key

/** The optional table containing primary key structure and sequence associations. Can be expressed as 'schema.name'
 * or just 'name'. */
public static final @Field String PK_METADATA_TABLE = JDBCDataStoreFactory.PK_METADATA_TABLE.key

/** Maximum number of prepared statements kept open and cached for each connection in the pool. Set to 0 to have
 * unbounded caching, to -1 to disable caching. */
public static final @Field String MAX_OPEN_PREPARED_STATEMENTS = JDBCDataStoreFactory.MAX_OPEN_PREPARED_STATEMENTS.key

/** Expose primary key columns as attributes of the feature type. */
public static final @Field String EXPOSE_PK = JDBCDataStoreFactory.EXPOSE_PK.key

/** SQL statement executed when the connection is grabbed from the pool. */
public static final @Field String SQL_ON_BORROW = JDBCDataStoreFactory.SQL_ON_BORROW.key

/** SQL statement executed when the connection is released to the pool. */
public static final @Field String SQL_ON_RELEASE = JDBCDataStoreFactory.SQL_ON_RELEASE.key

/** Name of JDBCReaderCallbackFactory to enable on the data store. */
public static final @Field String CALLBACK_FACTORY = JDBCDataStoreFactory.CALLBACK_FACTORY.key

/** Host. */
public static final @Field String HOST = JDBCDataStoreFactory.HOST.key

/** Port. */
public static final @Field String PORT = JDBCDataStoreFactory.PORT.key

/** Database. */
public static final @Field String DATABASE = JDBCDataStoreFactory.DATABASE.key

/** Schema. */
public static final @Field String SCHEMA = JDBCDataStoreFactory.SCHEMA.key

/** User. */
public static final @Field String USER = JDBCDataStoreFactory.USER.key