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
package org.orbisgis.coreutils

import org.geotools.data.DataStore
import org.geotools.data.DataStoreFinder
import org.geotools.data.simple.SimpleFeatureSource
/**
 * Utility script used as extension module adding methods to {@link DataStore} class.
 *
 * @author Erwan Bocher (CNRS 2020)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */

/**
 * Method called when the asked property is missing and returns the SimpleFeatureSource corresponding to the given name.
 *
 * @param ds DataStore to use.
 * @param name Name of the property/SimpleFeatureSource to get.
 */
static SimpleFeatureSource propertyMissing(DataStore ds, String name) {
    return ds.getFeatureSource(name)
}

/**
 * Returns the DataStore from the given file path.
 *
 * @param path Path to the file to open as a {@link DataStore}
 * @return A {@link DataStore} created from the given path.
 */
static DataStore toDataStore(String path) {
    DataStoreFinder.getDataStore([url: new File(path).toURI().toURL()])
}

/**
 * Returns the DataStore from the given file url.
 *
 * @param url Url to the file to open as a {@link DataStore}
 * @return A {@link DataStore} created from the given url.
 */
static DataStore toDataStore(URL url) {
    DataStoreFinder.getDataStore([url: url])
}

/**
 * Returns the DataStore from the given file.
 *
 * @param file File to open as a {@link DataStore}
 * @return A {@link DataStore} created from the given file.
 */
static DataStore toDataStore(File file) {
    DataStoreFinder.getDataStore([url: file.toURI().toURL()])
}

/**
 * Returns the DataStore from the given URI.
 *
 * @param uri URI to the file to open as a {@link DataStore}
 * @return A {@link DataStore} created from the given URI.
 */
static DataStore toDataStore(URI uri) {
    DataStoreFinder.getDataStore([url: uri.toURL()])
}