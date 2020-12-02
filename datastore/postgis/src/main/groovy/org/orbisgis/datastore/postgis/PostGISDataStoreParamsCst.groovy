/*
 * Bundle datastore/postgis is part of the OrbisGIS platform
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
package org.orbisgis.datastore.postgis

import groovy.transform.BaseScript
import groovy.transform.Field
import org.geotools.data.postgis.PostgisNGDataStoreFactory
import org.orbisgis.datastore.utils.JDBCDataStoreParamsCst

/**
 * Groovy script used as Closure DelegateTo on opening a POSTGIS JDBCDataStore.
 *
 * @author Erwan Bocher (CNRS 2020)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */

@BaseScript JDBCDataStoreParamsCst baseScript

/** Perform only primary filter on bbox. */
public static final @Field String LOOSEBBOX = PostgisNGDataStoreFactory.LOOSEBBOX.key

/** Use the spatial index information to quickly get an estimate of the data bounds. */
public static final @Field String ESTIMATED_EXTENTS = PostgisNGDataStoreFactory.ESTIMATED_EXTENTS.key

/** Creates the database if it does not exist yet. */
public static final @Field String CREATE_DB_IF_MISSING = PostgisNGDataStoreFactory.CREATE_DB_IF_MISSING.key

/** Extra specifications appeneded to the CREATE DATABASE command. */
public static final @Field String CREATE_PARAMS = PostgisNGDataStoreFactory.CREATE_PARAMS.key

/** Use prepared statements. */
public static final @Field String PREPARED_STATEMENTS = PostgisNGDataStoreFactory.PREPARED_STATEMENTS.key

/** Set to true to have a set of filter functions be translated directly in SQL. Due to differences in the type systems
 * the result might not be the same as evaluating them in memory, including the SQL failing with errors while the in
 * memory version works fine. However this allows to push more of the filter into the database, increasing performance. */
public static final @Field String ENCODE_FUNCTIONS = PostgisNGDataStoreFactory.ENCODE_FUNCTIONS.key

/** Support on the fly geometry simplification. */
public static final @Field String SIMPLIFY = PostgisNGDataStoreFactory.SIMPLIFY.key

/** Method used to simplify geometries. */
public static final @Field String SIMPLIFICATION_METHOD = PostgisNGDataStoreFactory.SIMPLIFICATION_METHOD.key

/** The connection SSL mode. */
public static final @Field String SSL_MODE = PostgisNGDataStoreFactory.SSL_MODE.key