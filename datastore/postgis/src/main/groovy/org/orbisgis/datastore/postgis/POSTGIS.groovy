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

import org.geotools.data.postgis.PostgisNGDataStoreFactory
import org.geotools.jdbc.JDBCDataStore

import groovy.transform.Field

/**
 * Class simplifying the opening of a POSTGIS DataSource.
 *
 * @author Erwan Bocher (CNRS 2020)
 * @author Sylvain Palominos (UBS chaire GEOTERA 2020)
 */

/** Cached H2GISDataStoreFactory. */
private static final @Field PostgisNGDataStoreFactory H2GIS_DATA_STORE_FACTORY = new PostgisNGDataStoreFactory()

/**
 * Open a POSTGIS DataStore with the given params map.
 * @param params Parameters for the opening of the DataStore.
 * @return An POSTGIS DataStore.
 */
static JDBCDataStore open(def params = [:]) {
    return H2GIS_DATA_STORE_FACTORY.createDataStore(params)
}

/**
 * Open a POSTGIS DataStore with the given closure which will be delegated to DataStoreParams.
 * @param cl Closure delegated to DataStoreParams.
 * @return An POSTGIS DataStore.
 */
static JDBCDataStore open(@DelegatesTo(PostGISDataStoreParams) Closure cl) {
    def params = new PostGISDataStoreParams()
    def code = cl.rehydrate(params, this, this)
    code.resolveStrategy = Closure.DELEGATE_ONLY
    code()
    H2GIS_DATA_STORE_FACTORY.createDataStore(params.params())
}