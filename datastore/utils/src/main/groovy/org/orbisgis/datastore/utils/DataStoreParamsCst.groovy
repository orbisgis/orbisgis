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
 * Groovy script used to declare DataStore parameters constants
 *
 * @author Erwan Bocher (CNRS 2020)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */

/** Password used to login. */
public static final @Field String PASSWD = "passwd"

/** Namespace prefix. */
public static final @Field String NAMESPACE = "namespace"

/** Data Source. */
public static final @Field String DATASOURCE = "Data Source"

/** Maximum number of open connections. */
public static final @Field String MAXCONN = "max connections"

/** Minimum number of pooled connection. */
public static final @Field String MINCONN = "min connections"

/** Check connection is alive before using it. */
public static final @Field String VALIDATECONN = "validate connections"

/** Number of records read with each iteration with the dbms. */
public static final @Field String FETCHSIZE = "fetch size"

/** Number of records inserted in the same batch (default, 1). For optimal performance, set to 100. */
public static final @Field String BATCH_INSERT_SIZE = "Batch insert size"

/** Number of seconds the connection pool will wait before timing out attempting to get a new connection
 * (default, 20 seconds).*/
public static final @Field String MAXWAIT = "Connection timeout"

/** Periodically test the connections are still valid also while idle in the pool. */
public static final @Field String TEST_WHILE_IDLE = "Test while idle"

/** Number of seconds between idle object evitor runs (default, 300 seconds). */
public static final @Field String TIME_BETWEEN_EVICTOR_RUNS = "Evictor run periodicity"

/** Number of seconds a connection needs to stay idle for the evictor to consider closing it. */
public static final @Field String MIN_EVICTABLE_TIME = "Max connection idle time"

/** Number of connections checked by the idle connection evictor for each of its runs (defaults to 3). */
public static final @Field String EVICTOR_TESTS_PER_RUN = "Evictor tests per run"

/** The optional table containing primary key structure and sequence associations. Can be expressed as 'schema.name'
 * or just 'name'. */
public static final @Field String PK_METADATA_TABLE = "Primary key metadata table"

/** Maximum number of prepared statements kept open and cached for each connection in the pool. Set to 0 to have
 * unbounded caching, to -1 to disable caching. */
public static final @Field String MAX_OPEN_PREPARED_STATEMENTS = "Max open prepared statements"

/** Expose primary key columns as attributes of the feature type. */
public static final @Field String EXPOSE_PK = "Expose primary keys"

/** SQL statement executed when the connection is grabbed from the pool. */
public static final @Field String SQL_ON_BORROW = "Session startup SQL"
/** SQL statement executed when the connection is released to the pool. */
public static final @Field String SQL_ON_RELEASE = "Session close-up SQL"

/** Name of JDBCReaderCallbackFactory to enable on the data store. */
public static final @Field String CALLBACK_FACTORY = "Callback factory"

/** Host. */
public static final @Field String HOST = "host"

/** Port. */
public static final @Field String PORT = "port"

/** Database. */
public static final @Field String DATABASE = "database"

/** Schema. */
public static final @Field String SCHEMA = "schema"

/** User. */
public static final @Field String USER = "user"