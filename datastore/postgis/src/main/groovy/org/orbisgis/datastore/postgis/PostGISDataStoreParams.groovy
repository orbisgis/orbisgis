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
import org.orbisgis.datastore.utils.JDBCDataStoreParams

/**
 * Groovy script used as Closure DelegateTo on opening a POSTGIS JDBCDataStore.
 *
 * @author Erwan Bocher (CNRS 2020)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */

@BaseScript JDBCDataStoreParams baseScript

/**
 * Perform only primary filter on bbox.
 * @param associations Perform only primary filter on bbox.
 */
void looseBox(String looseBox) {
    params.put(PostGISDataStoreParamsCst.LOOSEBBOX, looseBox)
}

/**
 * Use the spatial index information to quickly get an estimate of the data bounds.
 * @param estimatedExtends Use the spatial index information to quickly get an estimate of the data bounds.
 */
void estimatedExtends(boolean estimatedExtends) {
    params.put(PostGISDataStoreParamsCst.ESTIMATED_EXTENTS, estimatedExtends)
}

/**
 * Creates the database if it does not exist yet.
 * @param createDbIfMissing Creates the database if it does not exist yet.
 */
void createDbIfMissing(boolean createDbIfMissing) {
    params.put(PostGISDataStoreParamsCst.CREATE_DB_IF_MISSING, createDbIfMissing)
}

/**
 * Extra specifications appeneded to the CREATE DATABASE command.
 * @param createParams Extra specifications appeneded to the CREATE DATABASE command.
 */
void createParams(boolean createParams) {
    params.put(PostGISDataStoreParamsCst.CREATE_PARAMS, createParams)
}

/**
 * Use prepared statements.
 * @param preparedStatements Use prepared statements.
 */
void preparedStatements(boolean preparedStatements) {
    params.put(PostGISDataStoreParamsCst.PREPARED_STATEMENTS, preparedStatements)
}

/**
 * Set to true to have a set of filter functions be translated directly in SQL. Due to differences in the type systems
 * the result might not be the same as evaluating them in memory, including the SQL failing with errors while the in
 * memory version works fine. However this allows to push more of the filter into the database, increasing performance.
 * @param encodeFunctions Set to true to have a set of filter functions be translated directly in SQL.
 */
void encodeFunctions(boolean encodeFunctions) {
    params.put(PostGISDataStoreParamsCst.ENCODE_FUNCTIONS, encodeFunctions)
}

/**
 * Support on the fly geometry simplification.
 * @param simplify Support on the fly geometry simplification.
 */
void simplify(String simplify) {
    params.put(PostGISDataStoreParamsCst.SIMPLIFY, simplify)
}

/**
 * Method used to simplify geometries.
 * @param simplificationMethod Method used to simplify geometries.
 */
void simplificationMethod(String simplificationMethod) {
    params.put(PostGISDataStoreParamsCst.SIMPLIFICATION_METHOD, simplificationMethod)
}

/**
 * The connection SSL mode.
 * @param sslMode The connection SSL mode.
 */
void sslMode(String sslMode) {
    params.put(PostGISDataStoreParamsCst.SSL_MODE, sslMode)
}