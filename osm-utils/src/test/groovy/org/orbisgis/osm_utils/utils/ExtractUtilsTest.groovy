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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test

import java.sql.Connection

/**
 * Test class dedicated to {@link ExtractUtils}.
 *
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */
class ExtractUtilsTest {

    private static final ExtractUtils extractUtils = new ExtractUtils()
    private static Connection CONNECTION
    private static String DB_NAME

    @BeforeAll
    static void beforeAll() {
        DB_NAME = (this.simpleName.postfix()).toUpperCase()
        CONNECTION = H2GISDBFactory.createSpatialDataBase("./target/" + DB_NAME)
    }

    /**
     * Test the {@link org.orbisgis.orbisanalysis.osm.utils.TransformUtils#createWhereFilter(java.lang.Object)}
     * method.
     */
    @Test
    void createWhereFilterTest(){
        def tags = [:]
        tags["material"] = ["concrete"]
        assert "(tag_key = 'material' AND tag_value IN ('concrete'))" ==
                extractUtils.createWhereFilter(tags)

        tags = [:]
        tags[null] = ["tata", "tutu"]
        assert "(tag_value IN ('tata','tutu'))" ==
                extractUtils.createWhereFilter(tags)

        tags = [:]
        tags[null] = "toto"
        assert "(tag_value IN ('toto'))" ==
                extractUtils.createWhereFilter(tags)

        tags = [:]
        tags["null"] = null
        assert "(tag_key = 'null')" ==
                extractUtils.createWhereFilter(tags)

        tags = [:]
        tags["empty"] = []
        assert "(tag_key = 'empty')" ==
                extractUtils.createWhereFilter(tags)

        tags = [:]
        tags["road"] = [null, "highway"]
        assert "(tag_key = 'road' AND tag_value IN ('highway'))" ==
                extractUtils.createWhereFilter(tags)

        tags = [:]
        tags["water"] = "pound"
        assert "(tag_key = 'water' AND tag_value IN ('pound'))" ==
                extractUtils.createWhereFilter(tags)

        tags = [:]
        tags["pound"] = ["emilie", "big", "large"]
        assert "(tag_key = 'pound' AND tag_value IN ('emilie','big','large'))" ==
                extractUtils.createWhereFilter(tags)

        tags = [:]
        tags[["river", "song"]] = ["big", "large"]
        assert "(tag_key IN ('river','song') AND tag_value IN ('big','large'))" ==
                extractUtils.createWhereFilter(tags)

        tags = [:]
        tags["material"] = ["concrete"]
        tags[null] = ["tata", "tutu"]
        tags[null] = "toto"
        tags["null"] = null
        tags["empty"] = []
        tags["road"] = [null, "highway"]
        tags["water"] = "pound"
        tags["pound"] = ["emilie", "big", "large"]
        tags[["river", "song"]] = ["big", "large"]
        assert "(tag_key = 'material' AND tag_value IN ('concrete')) OR " +
                "(tag_value IN ('toto')) OR " +
                "(tag_key = 'null') OR " +
                "(tag_key = 'empty') OR " +
                "(tag_key = 'road' AND tag_value IN ('highway')) OR " +
                "(tag_key = 'water' AND tag_value IN ('pound')) OR " +
                "(tag_key = 'pound' AND tag_value IN ('emilie','big','large')) OR " +
                "(tag_key IN ('river','song') AND tag_value IN ('big','large'))" == extractUtils.createWhereFilter(tags)

        assert "tag_key IN ('emilie','big','large')" == extractUtils.createWhereFilter(["emilie", "big", "large", null])
    }

    /**
     * Test the {@link org.orbisgis.orbisanalysis.osm.utils.TransformUtils#createWhereFilter(java.lang.Object)}
     * method with bad data.
     */
    @Test
    void badCreateWhereFilterTest(){
        assert "" == extractUtils.createWhereFilter(null)
        assert "" == extractUtils.createWhereFilter(new HashMap())
        assert "" == extractUtils.createWhereFilter([])
        assert "" == extractUtils.createWhereFilter([null])
    }

    /**
     * Test the {@link org.orbisgis.orbisanalysis.osm.utils.TransformUtils#getColumnSelector(java.lang.Object, java.lang.Object, java.lang.Object)}
     * method with bad data.
     */
    @Test
    void badGetColumnSelectorTest(){
        def validTags = [toto:"tata"]
        def columnsToKeep = ["col1", "col2", "col5"]
        assert !extractUtils.getColumnSelectorQuery(null, validTags, columnsToKeep)
        assert !extractUtils.getColumnSelectorQuery("", validTags, columnsToKeep)
    }

    /**
     * Test the {@link org.orbisgis.orbisanalysis.osm.utils.TransformUtils#getColumnSelector(java.lang.Object, java.lang.Object, java.lang.Object)}
     * method.
     */
    @Test
    void getColumnSelectorTest(){
        def validTableName = "tutu"
        def validTags = [toto:"tata"]
        def columnsToKeep = ["col1", "col2", "col5"]
        assert "SELECT distinct tag_key FROM tutu WHERE tag_key IN ('toto','col1','col2','col5')" ==
                extractUtils.getColumnSelectorQuery(validTableName, validTags, columnsToKeep)
        assert "SELECT distinct tag_key FROM tutu WHERE tag_key IN ('toto')" ==
                extractUtils.getColumnSelectorQuery(validTableName, validTags, null)
        assert "SELECT distinct tag_key FROM tutu WHERE tag_key IN ('toto')" ==
                extractUtils.getColumnSelectorQuery(validTableName, validTags, [])
        assert "SELECT distinct tag_key FROM tutu WHERE tag_key IN ('toto')" ==
                extractUtils.getColumnSelectorQuery(validTableName, validTags, [null, null])
        assert "SELECT distinct tag_key FROM tutu WHERE tag_key IN ('toto','tutu')" ==
                extractUtils.getColumnSelectorQuery(validTableName, validTags, "tutu")
        assert "SELECT distinct tag_key FROM tutu WHERE tag_key IN ('toto','tutu')" ==
                extractUtils.getColumnSelectorQuery(validTableName, validTags, "tutu")
        assert "SELECT distinct tag_key FROM tutu WHERE tag_key IN ('col1','col2','col5')" ==
                extractUtils.getColumnSelectorQuery(validTableName, null, columnsToKeep)
        assert "SELECT distinct tag_key FROM tutu" == extractUtils.getColumnSelectorQuery(validTableName, null, null)
    }

    /**
     * Test the {@link org.orbisgis.orbisanalysis.osm.utils.TransformUtils#getCountTagsQuery(java.lang.Object, java.lang.Object)}
     * method.
     */
    @Test
    void getCountTagQueryTest(){
        def osmTable = "tutu"
        assert "SELECT count(*) AS count FROM tutu WHERE tag_key IN ('titi','tata')" ==
                extractUtils.getCountTagsQuery(osmTable, ["titi", "tata"])
        assert "SELECT count(*) AS count FROM tutu" ==
                extractUtils.getCountTagsQuery(osmTable, null)
        assert "SELECT count(*) AS count FROM tutu" ==
                extractUtils.getCountTagsQuery(osmTable, [])
        assert "SELECT count(*) AS count FROM tutu" ==
                extractUtils.getCountTagsQuery(osmTable, [null])
        assert "SELECT count(*) AS count FROM tutu WHERE tag_key IN ('toto')" ==
                extractUtils.getCountTagsQuery(osmTable, "toto")
    }

    /**
     * Test the {@link org.orbisgis.orbisanalysis.osm.utils.TransformUtils#getCountTagsQuery(java.lang.Object, java.lang.Object)}
     * method with bad data.
     */
    @Test
    void badGetCountTagQueryTest(){
        assert !extractUtils.getCountTagsQuery(null, ["titi", "tata"])
        assert !extractUtils.getCountTagsQuery("", ["titi", "tata"])
    }

    /**
     * Test the {@link org.orbisgis.orbisanalysis.osm.utils.TransformUtils#createTagList(java.lang.Object, java.lang.Object)}
     * method.
     */
    @Test
    void createTagListTest(){
        def osmTable = "toto"

        CONNECTION.execute("CREATE TABLE toto (id int, tag_key varchar, tag_value array[255])")
        CONNECTION.execute("INSERT INTO toto VALUES (0, 'material', ('concrete', 'brick'))")
        assert ", MAX(CASE WHEN b.tag_key = 'material' THEN b.tag_value END) AS \"MATERIAL\"" ==
                extractUtils.createTagList(CONNECTION, "SELECT tag_key FROM $osmTable")
        CONNECTION.execute("DROP TABLE IF EXISTS toto")

        CONNECTION.execute("CREATE TABLE toto (id int, tag_key varchar, tag_value array[255])")
        CONNECTION.execute("INSERT INTO toto VALUES (1, 'water', null)")
        assert ", MAX(CASE WHEN b.tag_key = 'water' THEN b.tag_value END) AS \"WATER\"" ==
                extractUtils.createTagList(CONNECTION, "SELECT tag_key FROM $osmTable")
        CONNECTION.execute("DROP TABLE IF EXISTS toto")

        CONNECTION.execute("CREATE TABLE toto (id int, tag_key varchar, tag_value array[255])")
        CONNECTION.execute("INSERT INTO toto VALUES (2, 'road', '{}')")
        assert ", MAX(CASE WHEN b.tag_key = 'road' THEN b.tag_value END) AS \"ROAD\"" ==
                extractUtils.createTagList(CONNECTION, "SELECT tag_key FROM $osmTable")
        CONNECTION.execute("DROP TABLE IF EXISTS toto")

        CONNECTION.execute("CREATE TABLE toto (id int, tag_key varchar, tag_value array[255])")
        CONNECTION.execute("INSERT INTO toto VALUES (0, 'material', ('concrete', 'brick'))")
        assert !extractUtils.createTagList(null, "SELECT tag_key FROM $osmTable")
        CONNECTION.execute("DROP TABLE IF EXISTS toto")
    }

    /**
     * Test the {@link org.orbisgis.orbisanalysis.osm.utils.TransformUtils#createTagList(java.lang.Object, java.lang.Object)}
     * method with bad data.
     */
    @Test
    void badCreateTagListTest(){
        def osmTable = "toto"

        CONNECTION.execute("CREATE TABLE toto (id int, tag_key varchar, tag_value array[255])")
        CONNECTION.execute("INSERT INTO toto VALUES (3, null, ('lake', 'pound'))")
        assert "" == extractUtils.createTagList(CONNECTION, "SELECT tag_key FROM $osmTable")
        CONNECTION.execute("DROP TABLE IF EXISTS toto")

        CONNECTION.execute("CREATE TABLE toto (id int, tag_key varchar, tag_value array[255])")
        CONNECTION.execute("INSERT INTO toto VALUES (4, null, null)")
        assert "" == extractUtils.createTagList(CONNECTION, "SELECT tag_key FROM $osmTable")
        CONNECTION.execute("DROP TABLE IF EXISTS toto")
    }
}
