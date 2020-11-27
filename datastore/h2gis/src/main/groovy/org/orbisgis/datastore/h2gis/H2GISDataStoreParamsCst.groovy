/*
 * Bundle datastore/h2gis is part of the OrbisGIS platform
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
package org.orbisgis.datastore.h2gis

import groovy.transform.BaseScript
import groovy.transform.Field
import org.orbisgis.datastore.utils.DataStoreParamsCst

/**
 * Groovy script used as Closure DelegateTo on opening a H2GIS JDBCDataStore.
 *
 * @author Erwan Bocher (CNRS 2020)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */

@BaseScript DataStoreParamsCst baseScript

/** Associations. */
public static final @Field String ASSOCIATIONS = "Associations"

/** Activate AUTO_SERVER mode to share the database access. */
public static final @Field String AUTO_SERVER = "autoserver"

/** Use the spatial index information to quickly get an estimate of the data bounds. */
public static final @Field String ESTIMATED_EXTENTS = "Estimated extends"

/** Use prepared statements. */
public static final @Field String PREPARED_STATEMENTS = "preparedStatements"

/** Set to true to have a set of filter functions be translated directly in SQL. Due to differences in the type systems
 * the result might not be the same as evaluating them in memory, including the SQL failing with errors while the in
 * memory version works fine. However this allows to push more of the filter into the database, increasing performance. */
public static final @Field String ENCODE_FUNCTIONS = "encode functions"