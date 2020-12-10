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
package org.orbisgis.datastore.datastoreutils

import org.geotools.data.FeatureSource
import org.geotools.data.Query
import org.geotools.feature.FeatureCollection
import org.opengis.feature.Feature
import org.opengis.feature.type.FeatureType
import org.opengis.filter.Filter


/**
 * Utility script used as extension module adding methods to {@link org.geotools.data.FeatureSource} class.
 *
 * @author Erwan Bocher (CNRS 2020)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */

/**
 * Give to {@link FeatureSource#getFeatures()} a more readable name.
 * @param fs FeatureSource to query.
 * @return A FeatureCollection.
 */
static <T extends FeatureType, F extends Feature> FeatureCollection<T, F> getFeatureCollection(FeatureSource<T, F> fs) {
    fs.getFeatures()
}

/**
 * Give to {@link FeatureSource#getFeatures(Query)} a more readable name.
 * @param fs    FeatureSource to query.
 * @param query Query used to get the features.
 * @return A FeatureCollection.
 */
static <T extends FeatureType, F extends Feature> FeatureCollection<T, F> getFeatureCollection(FeatureSource<T, F> fs, Query query) {
    fs.getFeatures(query)
}

/**
 * Give to {@link FeatureSource#getFeatures(Filter)} a more readable name.
 * @param fs     FeatureSource to query.
 * @param filter Filter used to get the features.
 * @return A FeatureCollection.
 */
static <T extends FeatureType, F extends Feature> FeatureCollection<T, F>  getFeatureCollection(FeatureSource<T, F> fs, Filter filter) {
    fs.getFeatures(filter)
}