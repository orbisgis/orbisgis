/*
 * Bundle OSM is part of the OrbisGIS platform
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
 * Copyright (C) 2019 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.osm_utils

import org.h2gis.functions.factory.H2GISDBFactory
import org.h2gis.utilities.JDBCUtilities
import org.h2gis.utilities.TableLocation
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.locationtech.jts.geom.Polygon
import org.locationtech.jts.io.WKTReader
import org.orbisgis.osm_utils.utils.DataUtils
import org.orbisgis.osm_utils.utils.OverpassUtils

import java.sql.Connection

import static org.h2gis.utilities.JDBCUtilities.*
import static org.junit.jupiter.api.Assertions.fail

/**
 * Test class for the methods in {@link Extract}
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS LAB-STICC 2019)
 */
class ExtractTest {
    protected static def uuidRegex = "[0-9a-f]{8}_[0-9a-f]{4}_[0-9a-f]{4}_[0-9a-f]{4}_[0-9a-f]{12}"
    private static String DB_NAME
    private static Connection connection
    private static final Extract EXTRACT = new Extract();

    @BeforeAll
    static final void beforeAll(){
        DB_NAME = (this.simpleName.postfix()).toUpperCase()
        connection = H2GISDBFactory.createSpatialDataBase("./target/" + DB_NAME)
    }

    /**
     * Test the OSMTools.extract.fromArea() process with bad data.
     */
    @Test
    void badFromAreaTest(){
        def polygon = [[0, 0], [4, 8], [7, 5], [0, 0]] as Polygon
        assert !EXTRACT.fromArea(null, polygon)
        assert !EXTRACT.fromArea(connection, null)
        assert !EXTRACT.fromArea(connection, polygon, -1)
    }

    /**
     * Test the OSMTools.Loader.fromArea() process.
     */
    @Test
    @Disabled
    void fromAreaNoDistTest(){
        def polygon = [[0.000, 0.000], [0.004, 0.008], [0.007, 0.005], [0.000, 0.000]] as Polygon
        def env = polygon.getEnvelopeInternal()

        //With polygon
        def r = EXTRACT.fromArea(connection, polygon)
        assert !r.isEmpty()
        assert r.containsKey("zoneTableName")
        assert "ZONE_$uuidRegex".compileRegex().matcher(r.zoneTableName).matches()
        assert r.containsKey("zoneEnvelopeTableName")
        assert "ZONE_ENVELOPE_$uuidRegex".compileRegex().matcher(r.zoneEnvelopeTableName).matches()
        assert r.containsKey("osmTablesPrefix")
        assert "OSM_DATA_$uuidRegex".compileRegex().matcher(r.osmTablesPrefix).matches()
        assert r.containsKey("epsg")
        assert 4326, r.epsg

        def zone = TableLocation.parse(r.zoneTableName, isH2DataBase(connection))
        assert 1 == getRowCount(connection, zone)
        assert 1 == getColumnNames(connection, zone).size()
        assert getColumnNames(connection, zone).contains("THE_GEOM")
        def rs = connection.createStatement().executeQuery("SELECT THE_GEOM FROM "+r.zoneTableName)
        rs.next()
        assert "POLYGON ((0 0, 0.004 0.008, 0.007 0.005, 0 0))", rs.getObject(1).toText()

        def zoneEnv = TableLocation.parse(r.zoneEnvelopeTableName, isH2DataBase(connection))
        assert 1 == getRowCount(connection, zoneEnv)
        assert 1 == getColumnNames(connection, zoneEnv).size()
        assert getColumnNames(connection, zoneEnv).contains("THE_GEOM")
        rs = connection.createStatement().executeQuery("SELECT THE_GEOM FROM "+r.zoneEnvelopeTableName)
        rs.next()
        assert "POLYGON ((0 0, 0 0.008, 0.007 0.008, 0.007 0, 0 0))", rs.getObject(1).toText()

        //With Envelope
        r = EXTRACT.fromArea(connection, env)
        assert !r.isEmpty()
        assert r.containsKey("zoneTableName")
        assert "ZONE_$uuidRegex".compileRegex().matcher(r.zoneTableName).matches()
        assert r.containsKey("zoneEnvelopeTableName")
        assert "ZONE_ENVELOPE_$uuidRegex".compileRegex().matcher(r.zoneEnvelopeTableName).matches()
        assert r.containsKey("osmTablesPrefix")
        assert "OSM_DATA_$uuidRegex".compileRegex().matcher(r.osmTablesPrefix).matches()
        assert r.containsKey("epsg")
        assert 4326, r.epsg

        zone = TableLocation.parse(r.zoneTableName, isH2DataBase(connection))
        assert 1 == getRowCount(connection, zone)
        assert 1 == getColumnNames(connection, zone).size()
        assert getColumnNames(connection, zone).contains("THE_GEOM")
        rs = connection.createStatement().executeQuery("SELECT THE_GEOM FROM "+r.zoneTableName)
        rs.next()
        assert "POLYGON ((0 0, 0 0.008, 0.007 0.008, 0.007 0, 0 0))", rs.getObject(1).toText()

        zoneEnv = TableLocation.parse(r.zoneEnvelopeTableName, isH2DataBase(connection))
        assert 1 == getRowCount(connection, zoneEnv)
        assert 1 == getColumnNames(connection, zoneEnv).size()
        assert getColumnNames(connection, zoneEnv).contains("THE_GEOM")
        rs = connection.createStatement().executeQuery("SELECT THE_GEOM FROM "+r.zoneEnvelopeTableName)
        rs.next()
        assert "POLYGON ((0 0, 0 0.008, 0.007 0.008, 0.007 0, 0 0))", rs.getObject(1).toText()

    }

    /**
     * Test the OSMTools.Loader.fromArea() process.
     */
    @Test
    @Disabled
    void fromAreaWithDistTest(){
        def wktReader = new WKTReader()
        def dist = 1000
        def polygon = [[0, 0], [2, 0],[2, 2], [0, 2], [0, 0]] as Polygon
        def env = polygon.getEnvelopeInternal()

        //With polygon
        def r = EXTRACT.fromArea(connection, polygon, dist)
        assert !r.isEmpty()
        assert r.containsKey("zoneTableName")
        assert "ZONE_$uuidRegex".compileRegex().matcher(r.zoneTableName).matches()
        assert r.containsKey("zoneEnvelopeTableName")
        assert "ZONE_ENVELOPE_$uuidRegex".compileRegex().matcher(r.zoneEnvelopeTableName).matches()
        assert r.containsKey("osmTablesPrefix")
        assert "OSM_DATA_$uuidRegex".compileRegex().matcher(r.osmTablesPrefix).matches()

        def zone = TableLocation.parse(r.zoneTableName, isH2DataBase(connection))
        assert 1 == getRowCount(connection, zone)
        assert 1 == getColumnNames(connection, zone).size()
        assert getColumnNames(connection, zone).contains("THE_GEOM")
        def rs = connection.createStatement().executeQuery("SELECT THE_GEOM FROM "+r.zoneTableName)
        rs.next()
        assert wktReader.read("POLYGON ((0 0, 2 0, 2 2, 0 2, 0 0))"), rs.getGeometry(1)

        def zoneEnv = TableLocation.parse(r.zoneEnvelopeTableName, isH2DataBase(connection))
        assert 1 == getRowCount(connection, zoneEnv)
        assert 1 == getColumnNames(connection, zoneEnv).size()
        assert getColumnNames(connection, zoneEnv).contains("THE_GEOM")
        rs = connection.createStatement().executeQuery("SELECT THE_GEOM FROM "+r.zoneEnvelopeTableName)
        rs.next()
        assert wktReader.read("POLYGON ((-0.008988628470795 -0.0089831528411952, -0.008988628470795 2.008983152841195, 2.008988628470795 2.008983152841195, 2.008988628470795 -0.0089831528411952, -0.008988628470795 -0.0089831528411952))"), rs.getGeometry(1)


        //With envelope
        r = EXTRACT.fromArea(connection, env, dist)
        assert !r.isEmpty()
        assert r.containsKey("zoneTableName")
        assert "ZONE_$uuidRegex".compileRegex().matcher(r.zoneTableName).matches()
        assert r.containsKey("zoneEnvelopeTableName")
        assert "ZONE_ENVELOPE_$uuidRegex".compileRegex().matcher(r.zoneEnvelopeTableName).matches()
        assert r.containsKey("osmTablesPrefix")
        assert "OSM_DATA_$uuidRegex".compileRegex().matcher(r.osmTablesPrefix).matches()

        zone = TableLocation.parse(r.zoneTableName, isH2DataBase(connection))
        assert 1 == getRowCount(connection, zone)
        assert 1 == getColumnNames(connection, zone).size()
        assert getColumnNames(connection, zone).contains("THE_GEOM")
        rs = connection.createStatement().executeQuery("SELECT THE_GEOM FROM "+r.zoneTableName)
        rs.next()
        assert wktReader.read("POLYGON ((0 0, 0 2, 2 2, 2 0, 0 0))"), rs.getGeometry(1)

        zoneEnv = TableLocation.parse(r.zoneEnvelopeTableName, isH2DataBase(connection))
        assert 1 == getRowCount(connection, zoneEnv)
        assert 1 == getColumnNames(connection, zoneEnv).size()
        assert getColumnNames(connection, zoneEnv).contains("THE_GEOM")
        rs = connection.createStatement().executeQuery("SELECT THE_GEOM FROM "+r.zoneEnvelopeTableName)
        rs.next()
        assert wktReader.read("POLYGON ((-0.008988628470795 -0.0089831528411952, -0.008988628470795 2.008983152841195, 2.008988628470795 2.008983152841195, 2.008988628470795 -0.0089831528411952, -0.008988628470795 -0.0089831528411952))"), zoneEnv.getGeometry(1)
    }

    /**
     * Test the OSMTools.Loader.fromPlace() process.
     */
    @Test
    @Disabled
    void fromPlaceNoDistTest(){
        def placeName = "  Saint jean la poterie  "
        def formattedPlaceName = "Saint_jean_la_poterie_"

        def r = EXTRACT.fromPlace(connection, placeName)
        assert !r.isEmpty()
        assert r.containsKey("zoneTableName")
        assert "ZONE_$formattedPlaceName$uuidRegex".compileRegex().matcher(r.zoneTableName).matches()
        assert r.containsKey("zoneEnvelopeTableName")
        assert "ZONE_ENVELOPE_$formattedPlaceName$uuidRegex".compileRegex().matcher(r.zoneEnvelopeTableName).matches()
        assert r.containsKey("osmTablesPrefix")
        assert "OSM_DATA_$formattedPlaceName$uuidRegex".compileRegex().matcher(r.osmTablesPrefix).matches()

        def zone = TableLocation.parse(r.zoneTableName, isH2DataBase(connection))
        assert 1 == getRowCount(connection, zone)
        assert 2 == getColumnNames(connection, zone).size()
        assert getColumnNames(connection, zone).contains("THE_GEOM")
        assert getColumnNames(connection, zone).contains("ID_ZONE")
        def rs = connection.createStatement().executeQuery("SELECT THE_GEOM FROM "+r.zoneTableName)
        rs.next()
        assert rs.getObject(1) instanceof Polygon
        assert placeName, rs.getString(2)

        def zoneEnv = TableLocation.parse(r.zoneEnvelopeTableName, isH2DataBase(connection))
        assert 1 == getRowCount(connection, zoneEnv)
        assert 2 == getColumnNames(connection, zoneEnv).size()
        assert getColumnNames(connection, zoneEnv).contains("THE_GEOM")
        assert getColumnNames(connection, zoneEnv).contains("ID_ZONE")
        rs = connection.createStatement().executeQuery("SELECT THE_GEOM FROM "+r.zoneEnvelopeTableName)
        rs.next()
        assert rs.getObject(1) instanceof Polygon
        assert placeName, rs.getString(2)
    }

    /**
     * Test the OSMTools.Loader.fromPlace() process.
     */
    @Test
    @Disabled
    void fromPlaceWithDistTest(){
        def placeName = "  Saint jean la poterie  "
        def dist = 5
        def formattedPlaceName = "Saint_jean_la_poterie_"
        def r =  EXTRACT.fromPlace(connection, placeName, dist)
        assert !r.isEmpty()
        assert r.containsKey("zoneTableName")
        assert "ZONE_$formattedPlaceName$uuidRegex".compileRegex().matcher(r.zoneTableName).matches()
        assert r.containsKey("zoneEnvelopeTableName")
        assert "ZONE_ENVELOPE_$formattedPlaceName$uuidRegex".compileRegex().matcher(r.zoneEnvelopeTableName).matches()
        assert r.containsKey("osmTablesPrefix")
        assert "OSM_DATA_$formattedPlaceName$uuidRegex".compileRegex().matcher(r.osmTablesPrefix).matches()

        def zone = TableLocation.parse(r.zoneTableName, isH2DataBase(connection))
        assert 1 == getRowCount(connection, zone)
        assert 2 == getColumnNames(connection, zone).size()
        assert getColumnNames(connection, zone).contains("THE_GEOM")
        assert getColumnNames(connection, zone).contains("ID_ZONE")
        def rs = connection.createStatement().executeQuery("SELECT THE_GEOM FROM "+r.zoneTableName)
        rs.next()
        assert rs.getObject(1) instanceof Polygon
        assert placeName, rs.getString(2)

        def zoneEnv = TableLocation.parse(r.zoneEnvelopeTableName, isH2DataBase(connection))
        assert 1 == getRowCount(connection, zoneEnv)
        assert 2 == getColumnNames(connection, zoneEnv).size()
        assert getColumnNames(connection, zoneEnv).contains("THE_GEOM")
        assert getColumnNames(connection, zoneEnv).contains("ID_ZONE")
        rs = connection.createStatement().executeQuery("SELECT THE_GEOM FROM "+r.zoneEnvelopeTableName)
        rs.next()
        assert rs.getObject(1) instanceof Polygon
        assert placeName, rs.getString(2)
    }

    /**
     * Test the OSMTools.Loader.fromPlace() process with bad data.
     */
    @Test
    void badFromPlaceTest(){
        def placeName = "  The place Name -toFind  "
        def dist = -5

        def results = EXTRACT.fromPlace(connection, placeName, dist)
        assert !results

        results = EXTRACT.fromPlace(connection, null)
        assert !results

        results = EXTRACT.fromPlace(null, placeName)
        assert !results
    }

    /**
     * Test the OSMTools.Loader.load() process with bad data.
     */
    @Test
    void badLoadTest(){
        def url = ExtractTest.getResource("sample.osm")
        assert url
        def osmFile = new File(url.toURI())
        assert osmFile.exists()
        assert osmFile.isFile()
        def prefix = ("".prefix()-"_").toUpperCase()

        //Null dataSource
        assert !DataUtils.load(null, prefix, osmFile.absolutePath)

        //Null prefix
        assert !DataUtils.load(connection, null, osmFile.absolutePath)
        //Bad prefix
        assert !DataUtils.load(connection, "(╯°□°）╯︵ ┻━┻", osmFile.absolutePath)

        //Null path
        assert !DataUtils.load(connection, prefix, null)
        //Unexisting path
        assert !DataUtils.load(connection, prefix, "ᕕ(ᐛ)ᕗ")
    }

    /**
     * Test the OSMTools.Loader.load() process.
     */
    @Test
    void loadTest(){
        def url = ExtractTest.getResource("sample.osm")
        assert url
        def osmFile = new File(url.toURI())
        assert  osmFile.exists()
        assert  osmFile.isFile()
        def prefix = "OSM_".prefix().toUpperCase()

        assert DataUtils.load(connection, prefix, osmFile.absolutePath)

        //Test on DataSource
        def tableArray = ["${prefix}_NODE", "${prefix}_NODE_MEMBER", "${prefix}_NODE_TAG",
                          "${prefix}_WAY", "${prefix}_WAY_MEMBER","${prefix}_WAY_TAG", "${prefix}_WAY_NODE",
                          "${prefix}_RELATION", "${prefix}_RELATION_MEMBER", "${prefix}_RELATION_TAG"]
        tableArray.each { name ->
            assert !getTableNames(connection, null, null, null, null).contains(name), "The table named $name is not in the datasource"
        }

        //NODE
        //Test on NODE table
        def nodeTable = TableLocation.parse(tableArray[0], isH2DataBase(connection))
        assert nodeTable
        assert 5 == getRowCount(connection, nodeTable)
        def arrayNode = ["ID_NODE", "THE_GEOM", "ELE", "USER_NAME", "UID", "VISIBLE", "VERSION", "CHANGESET",
                     "LAST_UPDATE", "NAME"] as String[]
        assert arrayNode == getColumnNames(connection, nodeTable) as String[]
        def rs = connection.rows("SELECT * FROM ${nodeTable.toString(isH2DataBase(connection))}")
        rs.eachWithIndex { row, i ->
            switch(i){
                case 0:
                    assert "256001" == row.ID_NODE.toString()
                    assert "POINT (32.8545692 57.0465758)" == row.THE_GEOM.toString()
                    assert !row.ELE
                    assert "UserTest" == row.USER_NAME.toString()
                    assert "5001" == row.UID.toString()
                    assert "true" == row.VISIBLE.toString()
                    assert "1" == row.VERSION.toString()
                    assert "6001" == row.CHANGESET.toString()
                    //assertEquals "2012-01-10T23:02:55Z", row.LAST_UPDATE.toString()
                    assert "2012-01-10 00:00:00.0" == row.LAST_UPDATE.toString()
                    assert "" == row.NAME.toString()
                    break
                case 1:
                    assert "256002" == row.ID_NODE.toString()
                    assert "POINT (32.8645692 57.0565758)" == row.THE_GEOM.toString()
                    assert !row.ELE
                    assert "UserTest" == row.USER_NAME.toString()
                    assert "5001" == row.UID.toString()
                    assert "true" == row.VISIBLE.toString()
                    assert "1" == row.VERSION.toString()
                    assert "6001" == row.CHANGESET.toString()
                    //assertEquals "2012-01-10T23:02:55Z", row.LAST_UPDATE
                    assert "2012-01-10 00:00:00.0" == row.LAST_UPDATE.toString()
                    assert "" == row.NAME.toString()
                    break
                case 2:
                    assert "256003" == row.ID_NODE.toString()
                    assert "POINT (32.8745692 57.0665758)" == row.THE_GEOM.toString()
                    assert !row.ELE
                    assert "UserTest" == row.USER_NAME.toString()
                    assert "5001" == row.UID.toString()
                    assert "true" == row.VISIBLE.toString()
                    assert "1" == row.VERSION.toString()
                    assert "6001" == row.CHANGESET.toString()
                    //assertEquals "2012-01-10T23:02:55Z", row.LAST_UPDATE.toString()
                    assert "2012-01-10 00:00:00.0" == row.LAST_UPDATE.toString()
                    assert "" == row.NAME.toString()
                    break
                case 3:
                    assert "256004" == row.ID_NODE.toString()
                    assert "POINT (32.8845692 57.0765758)" == row.THE_GEOM.toString()
                    assert !row.ELE
                    assert "UserTest" == row.USER_NAME.toString()
                    assert "5001" == row.UID.toString()
                    assert "true" == row.VISIBLE.toString()
                    assert "1" == row.VERSION.toString()
                    assert "6001" == row.CHANGESET.toString()
                    //assertEquals "2012-01-10T23:02:55Z", row.LAST_UPDATE.toString()
                    assert "2012-01-10 00:00:00.0" == row.LAST_UPDATE.toString()
                    assert "Just a house node" == row.NAME.toString()
                    break
                case 4:
                    assert "256005" == row.ID_NODE.toString()
                    assert "POINT (32.8945692 57.0865758)" == row.THE_GEOM.toString()
                    assert !row.ELE
                    assert "UserTest" == row.USER_NAME.toString()
                    assert "5001" == row.UID.toString()
                    assert "true" == row.VISIBLE.toString()
                    assert "1" == row.VERSION.toString()
                    assert "6001" == row.CHANGESET.toString()
                    //assertEquals "2012-01-10T23:02:55Z", row.LAST_UPDATE.toString()
                    assert "2012-01-10 00:00:00.0" == row.LAST_UPDATE.toString()
                    assert "Just a tree" == row.NAME.toString()
                    break
                default:
                    fail()
            }
        }

        //Test on NODE_MEMBER table
        def nodeMemberTable = TableLocation.parse(tableArray[1], isH2DataBase(connection))
        assert nodeMemberTable
        assert 2 == getRowCount(connection, nodeMemberTable)
        def arrayNodeMember = ["ID_RELATION", "ID_NODE", "ROLE", "NODE_ORDER"] as String[]
        assert arrayNodeMember == getColumnNames(connection, nodeMemberTable) as String[]
        rs = connection.rows("SELECT * FROM ${nodeMemberTable.toString(isH2DataBase(connection))}")
        rs.eachWithIndex { row, i ->
            switch(i){
                case 0:
                    assert "259001" == row.ID_RELATION.toString()
                    assert "256004" == row.ID_NODE.toString()
                    assert "center" == row.ROLE.toString()
                    assert "2" == row.NODE_ORDER.toString()
                    break
                case 1:
                    assert "259001" == row.ID_RELATION.toString()
                    assert "256005" == row.ID_NODE.toString()
                    assert "barycenter" == row.ROLE.toString()
                    assert "3" == row.NODE_ORDER.toString()
                    break
                default:
                    fail()
            }
        }

        //Test on NODE_TAG table
        def nodeTagTable = TableLocation.parse(tableArray[2], isH2DataBase(connection))
        assert nodeTagTable
        assert 2 == getRowCount(connection, nodeTagTable)
        def arrayNodeTag = ["ID_NODE", "TAG_KEY", "TAG_VALUE"] as String[]
        assert arrayNodeTag == getColumnNames(connection, nodeTagTable) as String[]
        rs = connection.rows("SELECT * FROM ${nodeTagTable.toString(isH2DataBase(connection))}")
        rs.eachWithIndex { row, i ->
            switch(i){
                case 0:
                    assert "256004" == row.ID_NODE.toString()
                    assert "building" == row.TAG_KEY.toString()
                    assert "house" == row.TAG_VALUE.toString()
                    break
                case 1:
                    assert "256005" == row.ID_NODE.toString()
                    assert "natural" == row.TAG_KEY.toString()
                    assert "tree" == row.TAG_VALUE.toString()
                    break
                default:
                    fail()
            }
        }

        //WAY
        //Test on WAY table
        def wayTable = TableLocation.parse(tableArray[3], isH2DataBase(connection))
        assert wayTable
        assert 1 == getRowCount(connection, wayTable)
        def arrayWay = ["ID_WAY", "USER_NAME", "UID", "VISIBLE", "VERSION", "CHANGESET",
                         "LAST_UPDATE", "NAME"] as String[]
        assert arrayWay == getColumnNames(connection, wayTable) as String[]
        rs = connection.rows("SELECT * FROM ${wayTable.toString(isH2DataBase(connection))}")
        rs.eachWithIndex { row, i ->
            switch(i){
                case 0:
                    assert "258001" == row.ID_WAY.toString()
                    assert "UserTest" == row.USER_NAME.toString()
                    assert "5001" == row.UID.toString()
                    assert "true" == row.VISIBLE.toString()
                    assert "1" == row.VERSION.toString()
                    assert "6001" == row.CHANGESET.toString()
                    assert "2012-01-10 23:02:55.0" == row.LAST_UPDATE.toString()
                    assert "" == row.NAME.toString()
                    break
                default:
                    fail()
            }
        }

        //Test on WAY_MEMBER table
        def wayMemberTable = TableLocation.parse(tableArray[4], isH2DataBase(connection))
        assert wayMemberTable
        assert 1 == getRowCount(connection, wayMemberTable)
        def arrayWayMember = ["ID_RELATION", "ID_WAY", "ROLE", "WAY_ORDER"] as String[]
        assert arrayWayMember == getColumnNames(connection, wayMemberTable) as String[]
        rs = connection.rows("SELECT * FROM ${wayMemberTable.toString(isH2DataBase(connection))}")
        rs.eachWithIndex { row, i ->
            switch(i){
                case 0:
                    assert "259001" == row.ID_RELATION.toString()
                    assert "258001" == row.ID_WAY.toString()
                    assert "outer" == row.ROLE.toString()
                    assert "1" == row.WAY_ORDER.toString()
                    break
                default:
                    fail()
            }
        }

        //Test on WAY_TAG table
        def wayTagTable = TableLocation.parse(tableArray[5], isH2DataBase(connection))
        assert wayTagTable
        assert 1 == getRowCount(connection, wayTagTable)
        def arrayWayTag = ["ID_WAY", "TAG_KEY", "TAG_VALUE"] as String[]
        assert arrayWayTag == getColumnNames(connection, wayTagTable) as String[]
        rs = connection.rows("SELECT * FROM ${wayTagTable.toString(isH2DataBase(connection))}")
        rs.eachWithIndex { row, i ->
            switch(i){
                case 0:
                    assert "258001" == row.ID_WAY.toString()
                    assert "highway" == row.TAG_KEY.toString()
                    assert "primary" == row.TAG_VALUE.toString()
                    break
                default:
                    fail()
            }
        }

        //Test on WAY_NODE table
        def wayNodeTable = TableLocation.parse(tableArray[6], isH2DataBase(connection))
        assert wayNodeTable
        assert 3 == getRowCount(connection, wayNodeTable)
        def arrayWayNode = ["ID_WAY", "ID_NODE", "NODE_ORDER"] as String[]
        assert arrayWayNode == getColumnNames(connection, wayNodeTable) as String[]
        rs = connection.rows("SELECT * FROM ${wayNodeTable.toString(isH2DataBase(connection))}")
        rs.eachWithIndex { row, i ->
            switch(i){
                case 0:
                    assert "258001" == row.ID_WAY.toString()
                    assert "256001" == row.ID_NODE.toString()
                    assert "1" == row.NODE_ORDER.toString()
                    break
                case 1:
                    assert "258001" == row.ID_WAY.toString()
                    assert "256002" == row.ID_NODE.toString()
                    assert "2" == row.NODE_ORDER.toString()
                    break
                case 2:
                    assert "258001" == row.ID_WAY.toString()
                    assert "256003" == row.ID_NODE.toString()
                    assert "3" == row.NODE_ORDER.toString()
                    break
                default:
                    fail()
            }
        }

        //RELATION
        //Test on RELATION table
        def relationTable = TableLocation.parse(tableArray[7], isH2DataBase(connection))
        assert relationTable
        assert 1 == getRowCount(connection, relationTable)
        def arrayRelation = ["ID_RELATION", "USER_NAME", "UID", "VISIBLE", "VERSION", "CHANGESET",
                        "LAST_UPDATE"] as String[]
        assert arrayRelation == getColumnNames(connection, relationTable) as String[]
        rs = connection.rows("SELECT * FROM ${relationTable.toString(isH2DataBase(connection))}")
        rs.eachWithIndex { row, i ->
            switch(i){
                case 0:
                    assert "259001" == row.ID_RELATION.toString()
                    assert "UserTest" == row.USER_NAME.toString()
                    assert "5001" == row.UID.toString()
                    assert "true" == row.VISIBLE.toString()
                    assert "1" == row.VERSION.toString()
                    assert "6001" == row.CHANGESET.toString()
                    assert "2012-01-10 23:02:55.0" == row.LAST_UPDATE.toString()
                    break
                default:
                    fail()
            }
        }

        //Test on RELATION_MEMBER table
        def relationMemberTable = TableLocation.parse(tableArray[8], isH2DataBase(connection))
        assert relationMemberTable
        assert 0 == getRowCount(connection, relationMemberTable)
        def arrayRelationMember = ["ID_RELATION", "ID_SUB_RELATION", "ROLE", "RELATION_ORDER"] as String[]
        assert arrayRelationMember == getColumnNames(connection, relationMemberTable) as String[]

        //Test on RELATION_TAG table
        def relationTagTable = TableLocation.parse(tableArray[9], isH2DataBase(connection))
        assert relationTagTable
        assert 2 == getRowCount(connection, relationTagTable)
        def arrayRelationTag = ["ID_RELATION", "TAG_KEY", "TAG_VALUE"] as String[]
        assert arrayRelationTag == getColumnNames(connection, relationTagTable) as String[]
        rs = connection.rows("SELECT * FROM ${relationTagTable.toString(isH2DataBase(connection))}")
        rs.eachWithIndex { row, i ->
            switch(i){
                case 0:
                    assert "259001" == row.ID_RELATION.toString()
                    assert "ref" == row.TAG_KEY.toString()
                    assert "123456" == row.TAG_VALUE.toString()
                    break
                case 1:
                    assert "259001" == row.ID_RELATION.toString()
                    assert "route" == row.TAG_KEY.toString()
                    assert "bus" == row.TAG_VALUE.toString()
                    break
                default:
                    fail()
            }
        }
    }


    /**
     * Test the {@link org.orbisgis.osm_utils.Extract#nodesAsPoints(java.sql.Connection, java.lang.String, java.lang.String, int, java.lang.Object, java.lang.Object)}
     * method with bad data.
     */
    @Test
    void badExtractNodesAsPointsTest(){
        def prefix = "prefix".postfix()
        def epsgCode  = 2456
        def outTable = "output"
        def tags = [building:["toto", "house", null], material:["concrete"], road:null]
        tags.put(null, null)
        tags.put(null, ["value1", "value2"])
        tags.put('key', null)
        tags.put('key1', null)
        def columnsToKeep = []

        loadDataForNodeExtraction(connection, prefix)

        assert !EXTRACT.nodesAsPoints(null, prefix, outTable, epsgCode, tags, columnsToKeep)
        assert !EXTRACT.nodesAsPoints(connection, null, outTable, epsgCode, tags, columnsToKeep)
        assert !EXTRACT.nodesAsPoints(connection, prefix, outTable, -1, tags, columnsToKeep)
        assert !EXTRACT.nodesAsPoints(connection, prefix, null, epsgCode, tags, columnsToKeep)

        assert !OSMTools.Extract.nodesAsPoints(connection, prefix, outTable, epsgCode, [house:"false", path:'false'], null)
    }

    private static loadDataForNodeExtraction(def connection, def prefix){
        connection.execute "CREATE TABLE ${prefix}_node (id_node int, the_geom geometry)"
        connection.execute "INSERT INTO ${prefix}_node VALUES (1, 'POINT(0 0)')"
        connection.execute "INSERT INTO ${prefix}_node VALUES (2, 'POINT(1 1)')"
        connection.execute "INSERT INTO ${prefix}_node VALUES (3, 'POINT(2 2)')"
        connection.execute "INSERT INTO ${prefix}_node VALUES (4, 'POINT(56.23 78.23)')"
        connection.execute "INSERT INTO ${prefix}_node VALUES (5, 'POINT(-5.3 -45.23)')"
        connection.execute "INSERT INTO ${prefix}_node VALUES (6, 'POINT(-5.3 -45.23)')"
        connection.execute "INSERT INTO ${prefix}_node VALUES (7, 'POINT(-5.3 -45.23)')"
        connection.execute "INSERT INTO ${prefix}_node VALUES (8, 'POINT(-5.3 -45.23)')"
        connection.execute "INSERT INTO ${prefix}_node VALUES (9, 'POINT(-5.3 -45.23)')"
        connection.execute "INSERT INTO ${prefix}_node VALUES (10, 'POINT(-5.3 -45.23)')"
        connection.execute "INSERT INTO ${prefix}_node VALUES (11, 'POINT(-5.3 -45.23)')"
        connection.execute "INSERT INTO ${prefix}_node VALUES (12, 'POINT(-5.3 -45.23)')"
        connection.execute "INSERT INTO ${prefix}_node VALUES (13, 'POINT(-5.3 -45.23)')"
        connection.execute "INSERT INTO ${prefix}_node VALUES (14, 'POINT(-5.3 -45.23)')"
        connection.execute "INSERT INTO ${prefix}_node VALUES (15, 'POINT(-5.3 -45.23)')"

        connection.execute "CREATE TABLE ${prefix}_node_tag (id_node int, tag_key varchar, tag_value varchar)"
        connection.execute "INSERT INTO ${prefix}_node_tag VALUES (1, 'building', 'house')"
        connection.execute "INSERT INTO ${prefix}_node_tag VALUES (1, 'house', 'true')"
        connection.execute "INSERT INTO ${prefix}_node_tag VALUES (1, 'material', 'concrete')"
        connection.execute "INSERT INTO ${prefix}_node_tag VALUES (2, 'water', 'pound')"
        connection.execute "INSERT INTO ${prefix}_node_tag VALUES (3, 'material', 'concrete')"
        connection.execute "INSERT INTO ${prefix}_node_tag VALUES (4, 'build', 'house')"
        connection.execute "INSERT INTO ${prefix}_node_tag VALUES (5, 'material', 'brick')"
        connection.execute "INSERT INTO ${prefix}_node_tag VALUES (6, 'material', null)"
        connection.execute "INSERT INTO ${prefix}_node_tag VALUES (7, null, 'value1')"
        connection.execute "INSERT INTO ${prefix}_node_tag VALUES (8, 'key', null)"
        connection.execute "INSERT INTO ${prefix}_node_tag VALUES (8, 'key1', null)"
        connection.execute "INSERT INTO ${prefix}_node_tag VALUES (9, 'key2', null)"
        connection.execute "INSERT INTO ${prefix}_node_tag VALUES (10, 'values', 'value1')"
        connection.execute "INSERT INTO ${prefix}_node_tag VALUES (11, 'key3', null)"
        connection.execute "INSERT INTO ${prefix}_node_tag VALUES (12, 'key3', 'val1')"
        connection.execute "INSERT INTO ${prefix}_node_tag VALUES (13, 'road', 'service')"
        connection.execute "INSERT INTO ${prefix}_node_tag VALUES (14, 'key4', 'service')"
        connection.execute "INSERT INTO ${prefix}_node_tag VALUES (15, 'road', 'service')"
        connection.execute "INSERT INTO ${prefix}_node_tag VALUES (16, 'material', 'concrete')"
    }

    /**
     * Test the {@link org.orbisgis.osm_utils.Extract#nodesAsPoints(java.sql.Connection, java.lang.String, java.lang.String, int, java.lang.Object, java.lang.Object)}
     * method.
     */
    @Test
    void extractNodesAsPointsTest(){
        def prefix = "prefix".postfix()
        def epsgCode  = 2456
        def outTable = "output"
        def tags = [building:["toto", "house", null], material:["concrete"], road:null]
        tags.put(null, null)
        tags.put(null, ["value1", "value2"])
        tags.put('key', null)
        tags.put('key1', null)
        tags.put('key3', null)
        tags.put('key4', ["value1", "value2"])
        def columnsToKeep = ["key1"]

        loadDataForNodeExtraction(connection, prefix)

        //With tags
        assert EXTRACT.nodesAsPoints(connection, prefix, outTable, epsgCode, tags, columnsToKeep)
        def loc = TableLocation.parse("output", isH2DataBase(connection))
        assert tableExists(connection, loc)

        assert 9 == getColumnNames(connection, loc).size()
        assert getColumnNames(connection, loc).contains("ID_NODE")
        assert getColumnNames(connection, loc).contains("THE_GEOM")
        assert getColumnNames(connection, loc).contains("BUILDING")
        assert getColumnNames(connection, loc).contains("MATERIAL")
        assert getColumnNames(connection, loc).contains("ROAD")
        assert getColumnNames(connection, loc).contains("KEY")
        assert getColumnNames(connection, loc).contains("KEY1")
        assert getColumnNames(connection, loc).contains("KEY3")
        assert getColumnNames(connection, loc).contains("KEY4")
        assert !getColumnNames(connection, loc).contains("WATER")
        assert !getColumnNames(connection, loc).contains("KEY2")
        assert !getColumnNames(connection, loc).contains("HOUSE")
        assert !getColumnNames(connection, loc).contains("VALUES")

        assert 9 == getRowCount(connection, loc)
        def rs = connection.rows("SELECT * FROM ${loc.toString(isH2DataBase(connection))}")
        rs.eachWithIndex {it, i ->
            switch(i){
                case 0:
                    assert 1 == it."id_node"
                    assert it."the_geom"
                    assert "house", it."building"
                    assert "concrete", it."material"
                    assert !it."road"
                    assert !it."key"
                    assert !it."key1"
                    assert !it."key1"
                    assert !it."key3"
                    assert !it."key4"
                    break
                case 1:
                    assert 3 == it."id_node"
                    assert it."the_geom"
                    assert !it."building"
                    assert "concrete" == it."material"
                    assert !it."road"
                    assert !it."key"
                    assert !it."key1"
                    assert !it."key3"
                    assert !it."key4"
                    break
                case 2:
                    assert 7 == it."id_node"
                    assert it."the_geom"
                    assert !it."building"
                    assert !it."material"
                    assert !it."road"
                    assert !it."key"
                    assert !it."key1"
                    assert !it."key3"
                    assert !it."key4"
                    break
                case 3:
                    assert 8 == it."id_node"
                    assert it."the_geom"
                    assert !it."building"
                    assert !it."material"
                    assert !it."road"
                    assert !it."key"
                    assert !it."key1"
                    assert !it."key3"
                    assert !it."key4"
                    break
                case 4:
                    assert 10 == it."id_node"
                    assert it."the_geom"
                    assert !it."building"
                    assert !it."material"
                    assert! it."road"
                    assert !it."key"
                    assert !it."key1"
                    assert !it."key3"
                    assert !it."key4"
                    break
                case 5:
                    assert 11 == it."id_node"
                    assert it."the_geom"
                    assert !it."building"
                    assert !it."material"
                    assert !it."road"
                    assert !it."key"
                    assert !it."key1"
                    assert !it."key3"
                    assert !it."key4"
                    break
                case 6:
                    assert 12 == it."id_node"
                    assert it."the_geom"
                    assert !it."building"
                    assert !it."material"
                    assert !it."road"
                    assert !it."key"
                    assert !it."key1"
                    assert "val1" == it."key3"
                    assert !it."key4"
                    break
                case 7:
                    assert 13 == it."id_node"
                    assert it."the_geom"
                    assert !it."building"
                    assert !it."material"
                    assert "service" == it."road"
                    assert !it."key"
                    assert !it."key1"
                    assert !it."key3"
                    assert !it."key4"
                    break
                case 8:
                    assert 15 == it."id_node"
                    assert it."the_geom"
                    assert !it."building"
                    assert !it."material"
                    assert "service" == it."road"
                    assert !it."key"
                    assert !it."key1"
                    assert !it."key3"
                    assert !it."key4"
                    break
            }
        }

        //Without tags and with column to keep
        assert EXTRACT.nodesAsPoints(connection, prefix, outTable, epsgCode, null, ["key1", "build"])
        loc = TableLocation.parse("output", isH2DataBase(connection))
        assert tableExists(connection, loc)

        assert 4 == getColumnNames(connection, loc).size()
        assert getColumnNames(connection, loc).contains("ID_NODE")
        assert getColumnNames(connection, loc).contains("THE_GEOM")
        assert getColumnNames(connection, loc).contains("BUILD")
        assert getColumnNames(connection, loc).contains("KEY1")
        assert !getColumnNames(connection, loc).contains("WATER")
        assert !getColumnNames(connection, loc).contains("MATERIAL")
        assert !getColumnNames(connection, loc).contains("ROAD")
        assert !getColumnNames(connection, loc).contains("KEY")
        assert !getColumnNames(connection, loc).contains("KEY2")
        assert !getColumnNames(connection, loc).contains("KEY3")
        assert !getColumnNames(connection, loc).contains("KEY4")
        assert !getColumnNames(connection, loc).contains("HOUSE")
        assert !getColumnNames(connection, loc).contains("BUILDING")
        assert !getColumnNames(connection, loc).contains("VALUES")

        //Without tags and columns to keep
        assert EXTRACT.nodesAsPoints(connection, prefix, outTable, epsgCode, null, [])
        loc = TableLocation.parse("output", isH2DataBase(connection))
        assert tableExists(connection, loc)

        assert 14 == getColumnNames(connection, loc).size()
        assert getColumnNames(connection, loc).contains("ID_NODE")
        assert getColumnNames(connection, loc).contains("THE_GEOM")
        assert getColumnNames(connection, loc).contains("BUILD")
        assert getColumnNames(connection, loc).contains("BUILDING")
        assert getColumnNames(connection, loc).contains("HOUSE")
        assert getColumnNames(connection, loc).contains("KEY")
        assert getColumnNames(connection, loc).contains("KEY1")
        assert getColumnNames(connection, loc).contains("KEY2")
        assert getColumnNames(connection, loc).contains("KEY3")
        assert getColumnNames(connection, loc).contains("KEY4")
        assert getColumnNames(connection, loc).contains("MATERIAL")
        assert getColumnNames(connection, loc).contains("ROAD")
        assert getColumnNames(connection, loc).contains("VALUES")
        assert getColumnNames(connection, loc).contains("WATER")

        assert 15 == getRowCount(connection, loc)
        rs = connection.rows("SELECT * FROM ${loc.toString(isH2DataBase(connection))}")
        rs.eachWithIndex {it, i ->
            switch(i){
                case 0:
                    assert 1 == it."id_node"
                    assert it."the_geom"
                    assert "house" == it."building"
                    assert !it."build"
                    assert "true" == it."house"
                    assert !it."key"
                    assert !it."key1"
                    assert !it."key2"
                    assert !it."key3"
                    assert !it."key4"
                    assert "concrete" == it."material"
                    assert !it."road"
                    assert !it."values"
                    assert !it."water"
                    break
                case 1:
                    assert 2 == it."id_node"
                    assert it."the_geom"
                    assert !it."building"
                    assert !it."build"
                    assert !it."house"
                    assert !it."key"
                    assert !it."key1"
                    assert !it."key2"
                    assert !it."key3"
                    assert !it."key4"
                    assert !it."material"
                    assert !it."road"
                    assert !it."values"
                    assert "pound" == it."water"
                    break
                case 2:
                    assert 3 == it."id_node"
                    assert it."the_geom"
                    assert !it."building"
                    assert !it."build"
                    assert !it."house"
                    assert !it."key"
                    assert !it."key1"
                    assert !it."key2"
                    assert !it."key3"
                    assert !it."key4"
                    assert "concrete" == it."material"
                    assert !it."road"
                    assert !it."values"
                    assert !it."water"
                    break
                case 3:
                    assert 4 == it."id_node"
                    assert it."the_geom"
                    assert !it."building"
                    assert "house" == it."build"
                    assert !it."house"
                    assert !it."key"
                    assert !it."key1"
                    assert !it."key2"
                    assert !it."key3"
                    assert !it."key4"
                    assert !it."material"
                    assert !it."road"
                    assert !it."values"
                    assert !it."water"
                    break
                case 4:
                    assert 5 == it."id_node"
                    assert it."the_geom"
                    assert !it."building"
                    assert !it."build"
                    assert !it."house"
                    assert !it."key"
                    assert !it."key1"
                    assert !it."key2"
                    assert !it."key3"
                    assert !it."key4"
                    assert "brick" == it."material"
                    assert !it."road"
                    assert !it."values"
                    assert !it."water"
                    break
                case 5:
                    assert 6 == it."id_node"
                    assert it."the_geom"
                    assert !it."building"
                    assert !it."build"
                    assert !it."house"
                    assert !it."key"
                    assert !it."key1"
                    assert !it."key2"
                    assert !it."key3"
                    assert !it."key4"
                    assert !it."material"
                    assert !it."road"
                    assert !it."values"
                    assert !it."water"
                    break
                case 6:
                    assert 7 == it."id_node"
                    assert it."the_geom"
                    assert !it."building"
                    assert !it."build"
                    assert !it."house"
                    assert !it."key"
                    assert !it."key1"
                    assert !it."key2"
                    assert !it."key3"
                    assert !it."key4"
                    assert !it."material"
                    assert !it."road"
                    assert !it."values"
                    assert !it."water"
                    break
                case 7:
                    assert 8 == it."id_node"
                    assert it."the_geom"
                    assert !it."building"
                    assert !it."build"
                    assert !it."house"
                    assert !it."key"
                    assert !it."key1"
                    assert !it."key2"
                    assert !it."key3"
                    assert !it."key4"
                    assert !it."material"
                    assert !it."road"
                    assert !it."values"
                    assert !it."water"
                    break
                case 8:
                    assert 9 == it."id_node"
                    assert it."the_geom"
                    assert !it."building"
                    assert !it."build"
                    assert !it."house"
                    assert !it."key"
                    assert !it."key1"
                    assert !it."key2"
                    assert !it."key3"
                    assert !it."key4"
                    assert !it."material"
                    assert !it."road"
                    assert !it."values"
                    assert !it."water"
                    break
                case 9:
                    assert 10 == it."id_node"
                    assert it."the_geom"
                    assert !it."building"
                    assert !it."build"
                    assert !it."house"
                    assert !it."key"
                    assert !it."key1"
                    assert !it."key2"
                    assert !it."key3"
                    assert !it."key4"
                    assert !it."material"
                    assert !it."road"
                    assert "value1" == it."values"
                    assert !it."water"
                    break
                case 10:
                    assert 11 == it."id_node"
                    assert it."the_geom"
                    assert !it."building"
                    assert !it."build"
                    assert !it."house"
                    assert !it."key"
                    assert !it."key1"
                    assert !it."key2"
                    assert !it."key3"
                    assert !it."key4"
                    assert !it."material"
                    assert !it."road"
                    assert !it."values"
                    assert !it."water"
                    break
                case 11:
                    assert 12 == it."id_node"
                    assert it."the_geom"
                    assert !it."building"
                    assert !it."build"
                    assert !it."house"
                    assert !it."key"
                    assert !it."key1"
                    assert !it."key2"
                    assert "val1" == it."key3"
                    assert !it."key4"
                    assert !it."material"
                    assert !it."road"
                    assert !it."values"
                    assert !it."water"
                    break
                case 12:
                    assert 13 == it."id_node"
                    assert it."the_geom"
                    assert !it."building"
                    assert !it."build"
                    assert !it."house"
                    assert !it."key"
                    assert !it."key1"
                    assert !it."key2"
                    assert !it."key3"
                    assert !it."key4"
                    assert !it."material"
                    assert "service" == it."road"
                    assert !it."values"
                    assert !it."water"
                    break
                case 13:
                    assert 14 == it."id_node"
                    assert it."the_geom"
                    assert !it."building"
                    assert !it."build"
                    assert !it."house"
                    assert !it."key"
                    assert !it."key1"
                    assert !it."key2"
                    assert !it."key3"
                    assert "service" == it."key4"
                    assert !it."material"
                    assert !it."road"
                    assert !it."values"
                    assert !it."water"
                    break
                case 14:
                    assert 15 == it."id_node"
                    assert it."the_geom"
                    assert !it."building"
                    assert !it."build"
                    assert !it."house"
                    assert !it."key"
                    assert !it."key1"
                    assert !it."key2"
                    assert !it."key3"
                    assert !it."key4"
                    assert !it."material"
                    assert "service" == it."road"
                    assert !it."values"
                    assert !it."water"
                    break
            }
        }
    }
}
