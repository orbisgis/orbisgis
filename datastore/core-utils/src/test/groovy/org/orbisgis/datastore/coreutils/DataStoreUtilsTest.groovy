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
package org.orbisgis.datastore.coreutils

import org.geotools.data.DataStore
import org.geotools.data.DataStoreFinder
import org.geotools.data.Query
import org.geotools.data.shapefile.ShapefileDataStore
import org.junit.jupiter.api.Test

/**
 * Test class dedicated to {@link DataStoreUtils}.
 *
 * @author Erwan Bocher (CNRS 2020)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */
class DataStoreUtilsTest {

    @Test
    void missingPropertyTest() {
        def name = "landcover2000.shp"
        assert this.class.getResource(name)
        def shapefile = new ShapefileDataStore(this.class.getResource(name))
        assert shapefile
        def contents = shapefile.landcover2000
        assert contents
        assert 1234 == contents.getCount( Query.ALL )
    }

    @Test
    void toDataStoreTest() {
        def url = this.class.getResource("landcover2000.shp")
        def uri = url.toURI()
        def file = new File(uri)
        def path = file.absolutePath

        def ds = url.toDataStore()
        assert ds
        assert ds in DataStore
        assert ds.landcover2000

        ds = uri.toDataStore()
        assert ds
        assert ds in DataStore
        assert ds.landcover2000

        ds = file.toDataStore()
        assert ds
        assert ds in DataStore
        assert ds.landcover2000

        ds = path.toDataStore()
        assert ds
        assert ds in DataStore
        assert ds.landcover2000
    }
}
