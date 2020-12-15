/*
 * Bundle osm-utils is part of the OrbisGIS platform
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
package org.orbisgis.osm_utils.utils

import org.h2gis.functions.factory.H2GISDBFactory
import org.h2gis.utilities.JDBCUtilities
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import java.sql.Connection

import static org.h2gis.utilities.JDBCUtilities.*

class DataUtilsTest {
    private static Connection CONNECTION
    private static String DB_NAME

    @BeforeAll
    static void beforeAll() {
        DB_NAME = (this.simpleName.postfix()).toUpperCase()
        CONNECTION = H2GISDBFactory.createSpatialDataBase("./target/" + DB_NAME)
    }

    @Test
    void loadTest() {
        def prefix = "A".postfix().toUpperCase()
        def filePath = new File(this.class.getResource("sample.osm").toURI()).absolutePath
        assert DataUtils.load(CONNECTION, prefix, filePath)
        def tables = getTableNames(CONNECTION, null, null, null, null)
        def tablePrefix = DB_NAME + ".PUBLIC." + prefix

        assert tables.contains(tablePrefix + "_NODE")
        assert 5 == getRowCount(CONNECTION, prefix + "_NODE")

        assert tables.contains(tablePrefix + "_NODE_TAG")
        assert 2 == getRowCount(CONNECTION, prefix + "_NODE_TAG")

        assert tables.contains(tablePrefix + "_WAY")
        assert 1 == getRowCount(CONNECTION, prefix + "_WAY")

        assert tables.contains(tablePrefix + "_WAY_TAG")
        assert 1 == getRowCount(CONNECTION, prefix + "_WAY_TAG")

        assert tables.contains(tablePrefix + "_WAY_NODE")
        assert 3 == getRowCount(CONNECTION, prefix + "_WAY_NODE")

        assert tables.contains(tablePrefix + "_RELATION")
        assert 1 == getRowCount(CONNECTION, prefix + "_RELATION")

        assert tables.contains(tablePrefix + "_RELATION_TAG")
        assert 2 == getRowCount(CONNECTION, prefix + "_RELATION_TAG")

        assert tables.contains(tablePrefix + "_NODE_MEMBER")
        assert 2 == getRowCount(CONNECTION, prefix + "_NODE_MEMBER")

        assert tables.contains(tablePrefix + "_WAY_MEMBER")
        assert 1 == getRowCount(CONNECTION, prefix + "_WAY_MEMBER")

        assert tables.contains(tablePrefix + "_RELATION_MEMBER")
        assert 0 == getRowCount(CONNECTION, prefix + "_RELATION_MEMBER")
    }

    @Test
    void badLoadTest() {
        def prefix = "A".postfix()
        def filePath = new File(this.class.getResource("sample.osm").toURI()).absolutePath
        assert !DataUtils.load(null, prefix, filePath)
        assert !DataUtils.load(CONNECTION, null, filePath)
        assert !DataUtils.load(null, null, filePath)
        assert !DataUtils.load(CONNECTION, prefix, null)
        assert !DataUtils.load(null, prefix, null)
        assert !DataUtils.load(CONNECTION, null, null)
        assert !DataUtils.load(null, null, null)

        assert !DataUtils.load(CONNECTION, "é#%ø£µ**/", filePath)
    }
}