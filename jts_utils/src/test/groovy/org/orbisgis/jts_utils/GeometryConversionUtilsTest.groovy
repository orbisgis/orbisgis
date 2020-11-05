package org.orbisgis.jts_utils

import org.junit.jupiter.api.Test
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.CoordinateXY
import org.locationtech.jts.geom.CoordinateXYM
import org.locationtech.jts.geom.CoordinateXYZM
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.MultiLineString
import org.locationtech.jts.geom.MultiPoint
import org.locationtech.jts.geom.MultiPolygon
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon

/**
 * Test class dedicated to {@link org.orbisgis.jts_utils.GeometryConversionUtils}.
 * 
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */
class GeometryConversionUtilsTest extends GeometryUtilsTest {

    @Test
    void asTypePointTest() {
        def ptGet = [] as Point
        def ptExpected = FACTORY.createPoint()
        assert ptExpected.coordinates == ptGet.coordinates

        ptGet = [1.0, 2] as Point
        ptExpected = FACTORY.createPoint(new CoordinateXY(1, 2.0))
        assert ptExpected == ptGet
        assertCoordinateEquals(ptExpected.coordinate, ptGet.coordinate)
        
        ptGet = [1.0, 2, 4f] as Point
        ptExpected = FACTORY.createPoint(new Coordinate(1f, 2.0, 4))
        assert ptExpected == ptGet
        assertCoordinateEquals(ptExpected.coordinate, ptGet.coordinate)

        ptGet = [1.0, 2, 4f, 5] as Point
        ptExpected = FACTORY.createPoint(new CoordinateXYZM(1f, 2.0, 4, 5))
        assert ptExpected == ptGet
        assertCoordinateEquals(ptExpected.coordinate, ptGet.coordinate)

        ptGet = [1.0, 2, 4f, "5"] as Point
        assert FACTORY.createPoint() == ptGet
    }

    @Test
    void asTypeLineString() {
        def get = [] as LineString
        def expected = FACTORY.createLineString()
        assert get == expected

        get = [[1.0, 2], [2, 1], [2, 5]] as LineString
        expected = FACTORY.createLineString(
                new CoordinateXY(1.0, 2),
                new CoordinateXY(2, 1),
                new CoordinateXY(2, 5)
        )
        assert expected == get
        assertCoordinatesEquals(expected.coordinates, get.coordinates)

        get = [[1.0, 2, 5], [2, 1, 6], [2, 5, 7]] as LineString
        expected = FACTORY.createLineString(
                new Coordinate(1.0, 2, 5),
                new Coordinate(2, 1, 6),
                new Coordinate(2, 5, 7)
        )
        assert expected == get
        assertCoordinatesEquals(expected.coordinates, get.coordinates)

        get = [[1.0, 2, 5, 8], [2, 1, 6, 9], [2, 5, 7, 10]] as LineString
        expected = FACTORY.createLineString(
                new CoordinateXYZM(1.0, 2, 5, 8),
                new CoordinateXYZM(2, 1, 6, 9),
                new CoordinateXYZM(2, 5, 7, 10)
        )
        assert expected == get
        assertCoordinatesEquals(expected.coordinates, get.coordinates)
    }

    @Test
    void asTypePolygon() {
        assert FACTORY.createPolygon() == [] as Polygon

        def shell = FACTORY.createLinearRing(
                new CoordinateXY(0,0),
                new CoordinateXY(3,0),
                new CoordinateXY(3,2),
                new CoordinateXY(1,3),
                new CoordinateXY(0,0))
        def holes = FACTORY.createLinearRing(
                new CoordinateXY(1,1),
                new CoordinateXY(2,1),
                new CoordinateXY(2,2),
                new CoordinateXY(1,1))
        def polyExpected = FACTORY.createPolygon(shell)
        def polyGet1 = [[[0,0], [3,0], [3,2], [1,3], [0,0]]] as Polygon
        def polyGet2 = [[0,0], [3,0], [3,2], [1,3], [0,0]] as Polygon
        def polyHoleExpected = FACTORY.createPolygon(shell, holes)
        def polyHoleGet = [[[0,0], [3,0], [3,2], [1,3], [0,0]], [[1,1], [2,1], [2,2], [1,1]]] as Polygon

        assert polyExpected == polyGet1
        assertCoordinatesEquals(polyExpected.coordinates, polyGet1.coordinates)
        assert polyExpected == polyGet2
        assertCoordinatesEquals(polyExpected.coordinates, polyGet2.coordinates)

        assert polyHoleExpected == polyHoleGet
        assertCoordinatesEquals(polyHoleExpected.coordinates, polyHoleGet.coordinates)


        shell = FACTORY.createLinearRing(
                new Coordinate(0,0, 1),
                new Coordinate(3,0, 2),
                new Coordinate(3,2, 3),
                new Coordinate(1,3, 4),
                new Coordinate(0,0, 5))
        holes = FACTORY.createLinearRing(
                new Coordinate(1,1, 7),
                new Coordinate(2,1, 6),
                new Coordinate(2,2, 5),
                new Coordinate(1,1, 4))
        polyExpected = FACTORY.createPolygon(shell)
        polyGet1 = [[[0,0,1], [3,0,2], [3,2,3], [1,3,4], [0,0,5]]] as Polygon
        polyGet2 = [[0,0,1], [3,0,2], [3,2,3], [1,3,4], [0,0,5]] as Polygon
        polyHoleExpected = FACTORY.createPolygon(shell, holes)
        polyHoleGet = [[[0,0,1], [3,0,2], [3,2,3], [1,3,4], [0,0,5]], [[1,1,7], [2,1,6], [2,2,5], [1,1,4]]] as Polygon

        assert polyExpected == polyGet1
        assertCoordinatesEquals(polyExpected.coordinates, polyGet1.coordinates)
        assert polyExpected == polyGet2
        assertCoordinatesEquals(polyExpected.coordinates, polyGet2.coordinates)

        assert polyHoleExpected == polyHoleGet
        assertCoordinatesEquals(polyHoleExpected.coordinates, polyHoleGet.coordinates)


        shell = FACTORY.createLinearRing(
                new CoordinateXYZM(0,0,7,1),
                new CoordinateXYZM(3,0,6,2),
                new CoordinateXYZM(3,2,5,3),
                new CoordinateXYZM(1,3,4,4),
                new CoordinateXYZM(0,0,3,5))
        holes = FACTORY.createLinearRing(
                new CoordinateXYZM(1,1,7,0),
                new CoordinateXYZM(2,1,6,1),
                new CoordinateXYZM(2,2,5,2),
                new CoordinateXYZM(1,1,4,3))
        polyExpected = FACTORY.createPolygon(shell)
        polyGet1 = [[[0,0,7,1], [3,0,6,2], [3,2,5,3], [1,3,4,4], [0,0,3,5]]] as Polygon
        polyGet2 = [[0,0,7,1], [3,0,6,2], [3,2,5,3], [1,3,4,4], [0,0,3,5]] as Polygon
        polyHoleExpected = FACTORY.createPolygon(shell, holes)
        polyHoleGet = [[[0,0,7,1], [3,0,6,2], [3,2,5,3], [1,3,4,4], [0,0,3,5]], [[1,1,7,0], [2,1,6,1], [2,2,5,2], [1,1,4,3]]] as Polygon

        assert polyExpected == polyGet1
        assertCoordinatesEquals(polyExpected.coordinates, polyGet1.coordinates)
        assert polyExpected == polyGet2
        assertCoordinatesEquals(polyExpected.coordinates, polyGet2.coordinates)

        assert polyHoleExpected == polyHoleGet
        assertCoordinatesEquals(polyHoleExpected.coordinates, polyHoleGet.coordinates)
    }

    @Test
    void asTypeMultiPoint() {
        assert FACTORY.createMultiPoint() == [] as MultiPoint

        def multiExpected = FACTORY.createMultiPoint(
                FACTORY.createPoint(new CoordinateXY(2, 2)),
                FACTORY.createPoint(new CoordinateXY(3, 4)),
                FACTORY.createPoint(new CoordinateXY(4, 6)),
                FACTORY.createPoint(new CoordinateXY(5, 8))
        )
        def multiGet = [[2,2],[3,4],[4,6],[5,8]] as MultiPoint

        assert multiExpected == multiGet
        assertCoordinatesEquals(multiExpected.coordinates, multiGet.coordinates)


        multiExpected = FACTORY.createMultiPoint(
                FACTORY.createPoint(new Coordinate(2, 2, 2)),
                FACTORY.createPoint(new Coordinate(3, 4, 0)),
                FACTORY.createPoint(new Coordinate(4, 6, 2)),
                FACTORY.createPoint(new Coordinate(5, 8, 0))
        )
        multiGet = [[2,2,2],[3,4,0],[4,6,2],[5,8,0]] as MultiPoint

        assert multiExpected == multiGet
        assertCoordinatesEquals(multiExpected.coordinates, multiGet.coordinates)


        multiExpected = FACTORY.createMultiPoint(
                FACTORY.createPoint(new CoordinateXYZM(2, 2, 2, 8)),
                FACTORY.createPoint(new CoordinateXYZM(3, 4, 0, 8)),
                FACTORY.createPoint(new CoordinateXYZM(4, 6, 2, 8)),
                FACTORY.createPoint(new CoordinateXYZM(5, 8, 0, 8))
        )
        multiGet = [[2,2,2,8],[3,4,0,8],[4,6,2,8],[5,8,0,8]] as MultiPoint

        assert multiExpected == multiGet
        assertCoordinatesEquals(multiExpected.coordinates, multiGet.coordinates)
    }

    @Test
    void asTypeMultiLineString() {
        assert FACTORY.createMultiLineString() == [] as MultiLineString

        def multiExpected = FACTORY.createMultiLineString(
                        FACTORY.createLineString(
                                new CoordinateXY(2, 2),
                                new CoordinateXY(3, 4),
                                new CoordinateXY(4, 6),
                                new CoordinateXY(5, 8)
                        ),
                        FACTORY.createLineString(
                                new CoordinateXY(12, 12),
                                new CoordinateXY(13, 14),
                                new CoordinateXY(14, 16),
                                new CoordinateXY(15, 18)
                        )
                )
        def multiGet = [[[2,2], [3,4], [4,6], [5,8]], [[12,12], [13,14], [14,16], [15,18]]] as MultiLineString

        assert multiExpected == multiGet
        assertCoordinatesEquals(multiExpected.coordinates, multiGet.coordinates)


        multiExpected = FACTORY.createMultiLineString(
                FACTORY.createLineString(
                        new Coordinate(2, 2, 2),
                        new Coordinate(3, 4, 0),
                        new Coordinate(4, 6, 2),
                        new Coordinate(5, 8, 0)
                ),
                FACTORY.createLineString(
                        new Coordinate(12, 12, 12),
                        new Coordinate(13, 14, 10),
                        new Coordinate(14, 16, 12),
                        new Coordinate(15, 18, 10)
                )
        )
        multiGet = [[[2,2,2],[3,4,0],[4,6,2],[5,8,0]], [[12,12,12],[13,14,10],[14,16,12],[15,18,10]]] as MultiLineString

        assert multiExpected == multiGet
        assertCoordinatesEquals(multiExpected.coordinates, multiGet.coordinates)


        multiExpected = FACTORY.createMultiLineString(
                FACTORY.createLineString(
                        new CoordinateXYZM(2, 2, 2, 8),
                        new CoordinateXYZM(3, 4, 0, 8),
                        new CoordinateXYZM(4, 6, 2, 8),
                        new CoordinateXYZM(5, 8, 0, 8)
                ),
                FACTORY.createLineString(
                        new CoordinateXYZM(12, 12, 12, 18),
                        new CoordinateXYZM(13, 14, 10, 18),
                        new CoordinateXYZM(14, 16, 12, 18),
                        new CoordinateXYZM(15, 18, 10, 18)
                )
        )
        multiGet = [[[2,2,2,8],[3,4,0,8],[4,6,2,8],[5,8,0,8]], [[12,12,12,18],[13,14,10,18],[14,16,12,18],[15,18,10,18]]] as MultiLineString

        assert multiExpected == multiGet
        assertCoordinatesEquals(multiExpected.coordinates, multiGet.coordinates)

    }

    @Test
    void asTypeMultiPolygon() {
        assert FACTORY.createMultiLineString() == [] as MultiLineString
        assert FACTORY.createMultiLineString() == [] as MultiLineString

        def shell = FACTORY.createLinearRing(
                new CoordinateXY(0,0),
                new CoordinateXY(3,0),
                new CoordinateXY(3,2),
                new CoordinateXY(1,3),
                new CoordinateXY(0,0))
        def holes = FACTORY.createLinearRing(
                new CoordinateXY(1,1),
                new CoordinateXY(2,1),
                new CoordinateXY(2,2),
                new CoordinateXY(1,1))
        def poly1 = FACTORY.createPolygon(shell, holes)
        def poly2 = FACTORY.createPolygon(
                new CoordinateXY(14, 16),
                new CoordinateXY(16, 16),
                new CoordinateXY(16, 18),
                new CoordinateXY(14, 16),
        )
        def multiExpected = FACTORY.createMultiPolygon(poly1, poly2)
        def multiGet = [[[[0,0], [3,0], [3,2], [1,3], [0,0]], [[1,1], [2,1], [2,2], [1,1]]],
                        [[14,16], [16,16], [16,18],[14,16]]] as MultiPolygon

        assert multiExpected == multiGet
        assertCoordinatesEquals(multiExpected.coordinates, multiGet.coordinates)


        shell = FACTORY.createLinearRing(
                new Coordinate(0,0, 1),
                new Coordinate(3,0, 2),
                new Coordinate(3,2, 3),
                new Coordinate(1,3, 4),
                new Coordinate(0,0, 5))
        holes = FACTORY.createLinearRing(
                new Coordinate(1,1, 7),
                new Coordinate(2,1, 6),
                new Coordinate(2,2, 5),
                new Coordinate(1,1, 4))
        poly1 = FACTORY.createPolygon(shell, holes)
        poly2 = FACTORY.createPolygon(
                new Coordinate(14, 16, 0),
                new Coordinate(16, 16, 0),
                new Coordinate(16, 18, 0),
                new Coordinate(14, 16, 0),
        )
        multiExpected = FACTORY.createMultiPolygon(poly1, poly2)
        multiGet = [[[[0,0,1], [3,0,2], [3,2,3], [1,3,4], [0,0,5]], [[1,1,7], [2,1,6], [2,2,5], [1,1,4]]],
                    [[14,16,0], [16,16,0], [16,18,0],[14,16,0]]] as MultiPolygon

        assert multiExpected == multiGet
        assertCoordinatesEquals(multiExpected.coordinates, multiGet.coordinates)


        shell = FACTORY.createLinearRing(
                new CoordinateXYZM(0,0, 1, 9),
                new CoordinateXYZM(3,0, 2, 8),
                new CoordinateXYZM(3,2, 3, 7),
                new CoordinateXYZM(1,3, 4, 6),
                new CoordinateXYZM(0,0, 5, 5))
        holes = FACTORY.createLinearRing(
                new CoordinateXYZM(1,1, 7, 8),
                new CoordinateXYZM(2,1, 6, 9),
                new CoordinateXYZM(2,2, 5, 10),
                new CoordinateXYZM(1,1, 4, 11))
        poly1 = FACTORY.createPolygon(shell, holes)
        poly2 = FACTORY.createPolygon(
                new CoordinateXYZM(14, 16, 0, 1),
                new CoordinateXYZM(16, 16, 0, 1),
                new CoordinateXYZM(16, 18, 0, 1),
                new CoordinateXYZM(14, 16, 0, 1),
        )
        multiExpected = FACTORY.createMultiPolygon(poly1, poly2)
        multiGet = [[[[0,0,1,9], [3,0,2,8], [3,2,3,7], [1,3,4,6], [0,0,5,5]], [[1,1,7,8], [2,1,6,9], [2,2,5,10], [1,1,4,11]]],
                    [[14,16,0,1], [16,16,0,1], [16,18,0,1],[14,16,0,1]]] as MultiPolygon

        assert multiExpected == multiGet
        assertCoordinatesEquals(multiExpected.coordinates, multiGet.coordinates)
    }

    @Test
    void coordinateTest() {
        def coordGet = [] as CoordinateXY
        def coordExpected = new CoordinateXY()
        assert coordExpected == coordGet
        assertCoordinateEquals(coordExpected, coordGet)

        coordGet = [] as Coordinate
        coordExpected = new Coordinate()
        assert coordExpected == coordGet
        assertCoordinateEquals(coordExpected, coordGet)

        coordGet = [] as CoordinateXYM
        coordExpected = new CoordinateXYM()
        assert coordExpected == coordGet
        assertCoordinateEquals(coordExpected, coordGet)

        coordGet = [] as CoordinateXYZM
        coordExpected = new CoordinateXYZM()
        assert coordExpected == coordGet
        assertCoordinateEquals(coordExpected, coordGet)


        coordGet = [1, 2.0] as CoordinateXY
        coordExpected = new CoordinateXY(1, 2.0)
        assert coordExpected == coordGet
        assertCoordinateEquals(coordExpected, coordGet)

        coordGet = [1, 2.0] as Coordinate
        coordExpected = new Coordinate(1, 2.0)
        assert coordExpected == coordGet
        assertCoordinateEquals(coordExpected, coordGet)

        coordGet = [1, 2.0, 5f] as Coordinate
        coordExpected = new Coordinate(1, 2.0, 5f)
        assert coordExpected == coordGet
        assertCoordinateEquals(coordExpected, coordGet)

        coordGet = [1, 2.0, 5f] as CoordinateXYM
        coordExpected = new CoordinateXYM(1, 2.0, 5f)
        assert coordExpected == coordGet
        assertCoordinateEquals(coordExpected, coordGet)

        coordGet = [1, 2.0, 5f, 6] as CoordinateXYZM
        coordExpected = new CoordinateXYZM(1, 2.0, 5f, 6)
        assert coordExpected == coordGet
        assertCoordinateEquals(coordExpected, coordGet)


        coordGet = [1, "toto"] as CoordinateXY
        assert !coordGet

        coordGet = [1, "toto"] as Coordinate
        assert !coordGet

        coordGet = [1, "toto", 5f] as Coordinate
        assert !coordGet

        coordGet = [1, "toto", 5f] as CoordinateXYM
        assert !coordGet

        coordGet = [1, "toto", 5f, 6] as CoordinateXYZM
        assert !coordGet
    }

    @Test
    void coordinatesTest() {
        def coordsGet = [] as CoordinateXY[]
        assert !coordsGet

        coordsGet = [] as Coordinate[]
        assert !coordsGet

        coordsGet = [] as CoordinateXYM[]
        assert !coordsGet

        coordsGet = [] as CoordinateXYZM[]
        assert !coordsGet


        coordsGet = [[1, 2.0], [2.0, 3], [3.0, 4]] as CoordinateXY[]
        def coordsExpected = [new CoordinateXY(1, 2.0), new CoordinateXY(2, 3.0), new CoordinateXY(3, 4.0)].toArray() as CoordinateXY[]
        assert coordsExpected == coordsGet
        assertCoordinatesEquals(coordsExpected, coordsGet)

        coordsGet = [[1, 2.0], [2.0, 3], [3.0, 4]] as Coordinate[]
        coordsExpected = [new Coordinate(1, 2.0), new Coordinate(2, 3.0), new Coordinate(3, 4.0)].toArray() as Coordinate[]
        assert coordsExpected == coordsGet
        assertCoordinatesEquals(coordsExpected, coordsGet)

        coordsGet = [[1, 2.0, 5f], [2.0, 3, 6f], [3.0, 4, 7f]] as Coordinate[]
        coordsExpected = [new Coordinate(1, 2.0, 5f), new Coordinate(2.0, 3, 6f), new Coordinate(3.0, 4, 7f)].toArray() as Coordinate[]
        assert coordsExpected == coordsGet
        assertCoordinatesEquals(coordsExpected, coordsGet)

        coordsGet = [[1, 2.0, 5f], [2.0, 3, 6f], [3.0, 4, 7f]] as CoordinateXYM[]
        coordsExpected = [new CoordinateXYM(1, 2.0, 5f), new CoordinateXYM(2.0, 3, 6f), new CoordinateXYM(3.0, 4, 7f)].toArray() as CoordinateXYM[]
        assert coordsExpected == coordsGet
        assertCoordinatesEquals(coordsExpected, coordsGet)

        coordsGet = [[1, 2.0, 5f, 6], [2.0, 3, 6f, 7], [3.0, 4, 7f, 8]] as CoordinateXYZM[]
        coordsExpected = [new CoordinateXYZM(1, 2.0, 5f, 6), new CoordinateXYZM(2.0, 3, 6f, 7), new CoordinateXYZM(3.0, 4, 7f, 8)].toArray() as CoordinateXYZM[]
        assert coordsExpected == coordsGet
        assertCoordinatesEquals(coordsExpected, coordsGet)


        coordsGet = [[1, 2, 3], [2, 3]] as CoordinateXY[]
        assert !coordsGet

        coordsGet = [[1, 2], [2, 3, 4]] as Coordinate[]
        assert !coordsGet

        coordsGet = [[1, 2, 5f], [2, 3, 45, 5]] as Coordinate[]
        assert !coordsGet

        coordsGet = [[1, 2.0, 5f], [2, 3, 4, 5]] as CoordinateXYM[]
        assert !coordsGet

        coordsGet = [[1, 2.0, 5f, 6], [1]] as CoordinateXYZM[]
        assert !coordsGet
    }

    private static void assertGeomEqualsWithSrid(Geometry expected, Geometry get){
        assert expected == get
        assertCoordinatesEquals(expected.coordinates, get.coordinates)
        assert expected.SRID == get.SRID
    }

    @Test
    void asTypeStringToPointTest() {
        def expected = [1, 2] as Point
        def get = "POINT(1 2)" as Point
        assertGeomEqualsWithSrid(expected, get)
        expected = [1, 2, 3] as Point
        get = "POINTZ(1 2 3)" as Point
        assertGeomEqualsWithSrid(expected, get)
        expected = [1, 2, 3, 4] as Point
        get = "POINTZM(1 2 3 4)" as Point
        assertGeomEqualsWithSrid(expected, get)

        expected = [1, 2] as Point
        expected.setSRID(4326)
        get = "SRID=4326;POINT(1 2)" as Point
        assertGeomEqualsWithSrid(expected, get)
        expected = [1, 2, 3] as Point
        expected.setSRID(4326)
        get = "SRID=4326;POINTZ(1 2 3)" as Point
        assertGeomEqualsWithSrid(expected, get)
        expected = [1, 2, 3, 4] as Point
        expected.setSRID(4326)
        get = "SRID=4326;POINTZM(1 2 3 4)" as Point
        assertGeomEqualsWithSrid(expected, get)
    }

    @Test
    void asTypeStringToLineStringTest() {
        def expected = [[4.0, 2], [2, 3], [3.0, 5.0]] as LineString
        def get = "LINESTRING(4.0 2, 2 3, 3.0 5.0)" as LineString
        assertGeomEqualsWithSrid(expected, get)
        expected = [[4.0, 2, 8], [2, 3, 7], [3.0, 5.0, 6]] as LineString
        get = "LINESTRINGZ(4.0 2 8, 2 3 7, 3.0 5.0 6)" as LineString
        assertGeomEqualsWithSrid(expected, get)
        expected = [[4.0, 2, 8, 5], [2, 3, 7, 6], [3.0, 5.0, 6, 7]] as LineString
        get = "LINESTRINGZM(4.0 2 8 5, 2 3 7 6, 3.0 5.0 6 7)" as LineString
        assertGeomEqualsWithSrid(expected, get)

        expected = [[4.0, 2], [2, 3], [3.0, 5.0]] as LineString
        expected.setSRID(4326)
        get = "SRID=4326;LINESTRING(4.0 2, 2 3, 3.0 5.0)" as LineString
        assertGeomEqualsWithSrid(expected, get)
        expected = [[4.0, 2, 8], [2, 3, 7], [3.0, 5.0, 6]] as LineString
        expected.setSRID(4326)
        get = "SRID=4326;LINESTRINGZ(4.0 2 8, 2 3 7, 3.0 5.0 6)" as LineString
        assertGeomEqualsWithSrid(expected, get)
        expected = [[4.0, 2, 8, 5], [2, 3, 7, 6], [3.0, 5.0, 6, 7]] as LineString
        expected.setSRID(4326)
        get = "SRID=4326;LINESTRINGZM(4.0 2 8 5, 2 3 7 6, 3.0 5.0 6 7)" as LineString
        assertGeomEqualsWithSrid(expected, get)
    }

    @Test
    void asTypeStringToPolygonTest() {
        //Without hole
        def expected = [[[0,0], [3,0], [3,2], [1,3], [0,0]]] as Polygon
        def get = "POLYGON((0 0, 3 0, 3 2, 1 3, 0 0))" as Polygon
        assertGeomEqualsWithSrid(expected, get)
        expected = [[[0,0,8], [3,0,6], [3,2,4], [1,3,2], [0,0,0]]] as Polygon
        get = "POLYGONZ((0 0 8, 3 0 6, 3 2 4, 1 3 2, 0 0 0))" as Polygon
        assertGeomEqualsWithSrid(expected, get)
        expected = [[[0,0,8,0], [3,0,6,1], [3,2,4,1], [1,3,2,0], [0,0,0,1]]] as Polygon
        get = "POLYGONZM((0 0 8 0, 3 0 6 1, 3 2 4 1, 1 3 2 0, 0 0 0 1))" as Polygon
        assertGeomEqualsWithSrid(expected, get)

        expected = [[[0,0], [3,0], [3,2], [1,3], [0,0]]] as Polygon
        expected.setSRID(4326)
        get = "SRID=4326;POLYGON((0 0, 3 0, 3 2, 1 3, 0 0))" as Polygon
        assertGeomEqualsWithSrid(expected, get)
        expected = [[[0,0,8], [3,0,6], [3,2,4], [1,3,2], [0,0,0]]] as Polygon
        expected.setSRID(4326)
        get = "SRID=4326;POLYGONZ((0 0 8, 3 0 6, 3 2 4, 1 3 2, 0 0 0))" as Polygon
        assertGeomEqualsWithSrid(expected, get)
        expected =  [[[0,0,8,0], [3,0,6,1], [3,2,4,1], [1,3,2,0], [0,0,0,1]]] as Polygon
        expected.setSRID(4326)
        get = "SRID=4326;POLYGONZM((0 0 8 0, 3 0 6 1, 3 2 4 1, 1 3 2 0, 0 0 0 1))" as Polygon
        assertGeomEqualsWithSrid(expected, get)

        //With hole
        expected = [[[0,0], [3,0], [3,2], [1,3], [0,0]], [[1,1], [2,1], [2,2], [1,1]]] as Polygon
        get = "POLYGON((0 0, 3 0, 3 2, 1 3, 0 0), (1 1, 2 1, 2 2, 1 1))" as Polygon
        assertGeomEqualsWithSrid(expected, get)
        expected = [[[0,0,8], [3,0,6], [3,2,4], [1,3,2], [0,0,0]], [[1,1,1], [2,1,2], [2,2,2], [1,1,1]]] as Polygon
        get = "POLYGONZ((0 0 8, 3 0 6, 3 2 4, 1 3 2, 0 0 0), (1 1 1, 2 1 2, 2 2 2, 1 1 1))" as Polygon
        assertGeomEqualsWithSrid(expected, get)
        expected = [[[0,0,8,0], [3,0,6,1], [3,2,4,1], [1,3,2,0], [0,0,0,1]], [[1,1,1,5], [2,1,2,4], [2,2,2,3], [1,1,1,2]]] as Polygon
        get = "POLYGONZM((0 0 8 0, 3 0 6 1, 3 2 4 1, 1 3 2 0, 0 0 0 1), (1 1 1 5, 2 1 2 4, 2 2 2 3, 1 1 1 2))" as Polygon
        assertGeomEqualsWithSrid(expected, get)

        expected = [[[0,0], [3,0], [3,2], [1,3], [0,0]], [[1,1], [2,1], [2,2], [1,1]]] as Polygon
        expected.setSRID(4326)
        get = "SRID=4326;POLYGON((0 0, 3 0, 3 2, 1 3, 0 0), (1 1, 2 1, 2 2, 1 1))" as Polygon
        assertGeomEqualsWithSrid(expected, get)
        expected = [[[0,0,8], [3,0,6], [3,2,4], [1,3,2], [0,0,0]], [[1,1,1], [2,1,2], [2,2,2], [1,1,1]]] as Polygon
        expected.setSRID(4326)
        get = "SRID=4326;POLYGONZ((0 0 8, 3 0 6, 3 2 4, 1 3 2, 0 0 0), (1 1 1, 2 1 2, 2 2 2, 1 1 1))" as Polygon
        assertGeomEqualsWithSrid(expected, get)
        expected =  [[[0,0,8,0], [3,0,6,1], [3,2,4,1], [1,3,2,0], [0,0,0,1]], [[1,1,1,5], [2,1,2,4], [2,2,2,3], [1,1,1,2]]] as Polygon
        expected.setSRID(4326)
        get = "SRID=4326;POLYGONZM((0 0 8 0, 3 0 6 1, 3 2 4 1, 1 3 2 0, 0 0 0 1), (1 1 1 5, 2 1 2 4, 2 2 2 3, 1 1 1 2))" as Polygon
        assertGeomEqualsWithSrid(expected, get)
    }

    @Test
    void asTypeStringToMultiPointTest() {
        def expected = [[0,0], [3,0], [3,2], [1,3]] as MultiPoint
        def get = "MULTIPOINT(0 0, 3 0, 3 2, 1 3)" as MultiPoint
        assertGeomEqualsWithSrid(expected, get)
        expected = [[0,0,8], [3,0,8], [3,2,8], [1,3,8]] as MultiPoint
        get = "MULTIPOINTZ(0 0 8, 3 0 8, 3 2 8, 1 3 8)" as MultiPoint
        assertGeomEqualsWithSrid(expected, get)
        expected = [[0,0,8,0], [3,0,8,0], [3,2,8,0], [1,3,8,0]] as MultiPoint
        get = "MULTIPOINTZM(0 0 8 0, 3 0 8 0, 3 2 8 0, 1 3 8 0)" as MultiPoint
        assertGeomEqualsWithSrid(expected, get)

        expected = [[0,0], [3,0], [3,2], [1,3]] as MultiPoint
        expected.setSRID(4326)
        get = "SRID=4326;MULTIPOINT(0 0, 3 0, 3 2, 1 3)" as MultiPoint
        assertGeomEqualsWithSrid(expected, get)
        expected = [[0,0,8], [3,0,8], [3,2,8], [1,3,8]] as MultiPoint
        expected.setSRID(4326)
        get = "SRID=4326;MULTIPOINTZ(0 0 8, 3 0 8, 3 2 8, 1 3 8)" as MultiPoint
        assertGeomEqualsWithSrid(expected, get)
        expected = [[0,0,8,0], [3,0,8,0], [3,2,8,0], [1,3,8,0]] as MultiPoint
        expected.setSRID(4326)
        get = "SRID=4326;MULTIPOINTZM(0 0 8 0, 3 0 8 0, 3 2 8 0, 1 3 8 0)" as MultiPoint
        assertGeomEqualsWithSrid(expected, get)
    }

    @Test
    void asTypeStringToMultiLineStringTest() {
        def expected = [[[0,0], [3,0]], [[3,2], [1,3]]] as MultiLineString
        def get = "MULTILINESTRING((0 0, 3 0), (3 2, 1 3))" as MultiLineString
        assertGeomEqualsWithSrid(expected, get)
        expected = [[[0,0,8], [3,0,8]], [[3,2,8], [1,3,8]]] as MultiLineString
        get = "MULTILINESTRINGZ((0 0 8, 3 0 8), (3 2 8, 1 3 8))" as MultiLineString
        assertGeomEqualsWithSrid(expected, get)
        expected = [[[0,0,8,0], [3,0,8,0]], [[3,2,8,0], [1,3,8,0]]] as MultiLineString
        get = "MULTILINESTRINGZM((0 0 8 0, 3 0 8 0), (3 2 8 0, 1 3 8 0))" as MultiLineString
        assertGeomEqualsWithSrid(expected, get)

        expected = [[[0,0], [3,0]], [[3,2], [1,3]]] as MultiLineString
        expected.setSRID(4326)
        get = "SRID=4326;MULTILINESTRING((0 0, 3 0), (3 2, 1 3))" as MultiLineString
        assertGeomEqualsWithSrid(expected, get)
        expected = [[[0,0,8], [3,0,8]], [[3,2,8], [1,3,8]]] as MultiLineString
        expected.setSRID(4326)
        get = "SRID=4326;MULTILINESTRINGZ((0 0 8, 3 0 8), (3 2 8, 1 3 8))" as MultiLineString
        assertGeomEqualsWithSrid(expected, get)
        expected = [[[0,0,8,0], [3,0,8,0]], [[3,2,8,0], [1,3,8,0]]] as MultiLineString
        expected.setSRID(4326)
        get = "SRID=4326;MULTILINESTRINGZM((0 0 8 0, 3 0 8 0), (3 2 8 0, 1 3 8 0))" as MultiLineString
        assertGeomEqualsWithSrid(expected, get)
    }

    @Test
    void asTypeStringToMultiPolygonTest() {
        //Without hole
        def expected = [[[[0,0], [3,0], [3,2], [1,3], [0,0]]], [[[10, 10], [11, 10], [11, 11], [10, 10]]]] as MultiPolygon
        def get = "MULTIPOLYGON(((0 0, 3 0, 3 2, 1 3, 0 0)), ((10 10, 11 10, 11 11, 10 10)))" as MultiPolygon
        assertGeomEqualsWithSrid(expected, get)
        expected = [[[[0,0,8], [3,0,6], [3,2,4], [1,3,2], [0,0,4]]], [[[10, 10, 1], [11, 10, 2], [11, 11, 3], [10, 10, 4]]]] as MultiPolygon
        get = "MULTIPOLYGONZ(((0 0 8, 3 0 6, 3 2 4, 1 3 2, 0 0 4)), ((10 10 1, 11 10 2, 11 11 3, 10 10 4)))" as MultiPolygon
        assertGeomEqualsWithSrid(expected, get)
        expected = [[[[0,0,8,0], [3,0,6,1], [3,2,4,1], [1,3,2,0], [0,0,4,1]]], [[[10, 10, 1, 4], [11, 10, 2, 3], [11, 11, 3, 2], [10, 10, 4, 1]]]] as MultiPolygon
        get = "MULTIPOLYGONZM(((0 0 8 0, 3 0 6 1, 3 2 4 1, 1 3 2 0, 0 0 4 1)), ((10 10 1 4, 11 10 2 3, 11 11 3 2, 10 10 4 1)))" as MultiPolygon
        assertGeomEqualsWithSrid(expected, get)

        expected = [[[[0,0], [3,0], [3,2], [1,3], [0,0]]], [[[10, 10], [11, 10], [11, 11], [10, 10]]]] as MultiPolygon
        expected.setSRID(4326)
        get = "SRID=4326;MULTIPOLYGON(((0 0, 3 0, 3 2, 1 3, 0 0)), ((10 10, 11 10, 11 11, 10 10)))" as MultiPolygon
        assertGeomEqualsWithSrid(expected, get)
        expected = [[[[0,0,8], [3,0,6], [3,2,4], [1,3,2], [0,0,4]]], [[[10, 10, 1], [11, 10, 2], [11, 11, 3], [10, 10, 4]]]] as MultiPolygon
        expected.setSRID(4326)
        get = "SRID=4326;MULTIPOLYGONZ(((0 0 8, 3 0 6, 3 2 4, 1 3 2, 0 0 4)), ((10 10 1, 11 10 2, 11 11 3, 10 10 4)))" as MultiPolygon
        assertGeomEqualsWithSrid(expected, get)
        expected = [[[[0,0,8,0], [3,0,6,1], [3,2,4,1], [1,3,2,0], [0,0,4,1]]], [[[10, 10, 1, 4], [11, 10, 2, 3], [11, 11, 3, 2], [10, 10, 4, 1]]]] as MultiPolygon
        expected.setSRID(4326)
        get = "SRID=4326;MULTIPOLYGONZM(((0 0 8 0, 3 0 6 1, 3 2 4 1, 1 3 2 0, 0 0 4 1)), ((10 10 1 4, 11 10 2 3, 11 11 3 2, 10 10 4 1)))" as MultiPolygon
        assertGeomEqualsWithSrid(expected, get)

        //With holes
        expected = [[[[0,0], [3,0], [3,2], [1,3], [0,0]], [[1,1], [2,1], [2,2], [1,1]]], [[[10, 10], [11, 10], [11, 11], [10, 10]]]] as MultiPolygon
        get = "MULTIPOLYGON(((0 0, 3 0, 3 2, 1 3, 0 0), (1 1, 2 1, 2 2, 1 1)), ((10 10, 11 10, 11 11, 10 10)))" as MultiPolygon
        assertGeomEqualsWithSrid(expected, get)
        expected = [[[[0,0,8], [3,0,6], [3,2,4], [1,3,2], [0,0,4]], [[1,1,1], [2,1,2], [2,2,2], [1,1,1]]], [[[10, 10, 1], [11, 10, 2], [11, 11, 3], [10, 10, 4]]]] as MultiPolygon
        get = "MULTIPOLYGONZ(((0 0 8, 3 0 6, 3 2 4, 1 3 2, 0 0 4), (1 1 1, 2 1 2, 2 2 2, 1 1 1)), ((10 10 1, 11 10 2, 11 11 3, 10 10 4)))" as MultiPolygon
        assertGeomEqualsWithSrid(expected, get)
        expected = [[[[0,0,8,0], [3,0,6,1], [3,2,4,1], [1,3,2,0], [0,0,4,1]], [[1,1,1,5], [2,1,2,4], [2,2,2,3], [1,1,1,2]]], [[[10, 10, 1, 4], [11, 10, 2, 3], [11, 11, 3, 2], [10, 10, 4, 1]]]] as MultiPolygon
        get = "MULTIPOLYGONZM(((0 0 8 0, 3 0 6 1, 3 2 4 1, 1 3 2 0, 0 0 4 1), (1 1 1 5, 2 1 2 4, 2 2 2 3, 1 1 1 2)), ((10 10 1 4, 11 10 2 3, 11 11 3 2, 10 10 4 1)))" as MultiPolygon
        assertGeomEqualsWithSrid(expected, get)

        expected = [[[[0,0], [3,0], [3,2], [1,3], [0,0]], [[1,1], [2,1], [2,2], [1,1]]], [[[10, 10], [11, 10], [11, 11], [10, 10]]]] as MultiPolygon
        expected.setSRID(4326)
        get = "SRID=4326;MULTIPOLYGON(((0 0, 3 0, 3 2, 1 3, 0 0), (1 1, 2 1, 2 2, 1 1)), ((10 10, 11 10, 11 11, 10 10)))" as MultiPolygon
        assertGeomEqualsWithSrid(expected, get)
        expected = [[[[0,0,8], [3,0,6], [3,2,4], [1,3,2], [0,0,4]], [[1,1,1], [2,1,2], [2,2,2], [1,1,1]]], [[[10, 10, 1], [11, 10, 2], [11, 11, 3], [10, 10, 4]]]] as MultiPolygon
        expected.setSRID(4326)
        get = "SRID=4326;MULTIPOLYGONZ(((0 0 8, 3 0 6, 3 2 4, 1 3 2, 0 0 4), (1 1 1, 2 1 2, 2 2 2, 1 1 1)), ((10 10 1, 11 10 2, 11 11 3, 10 10 4)))" as MultiPolygon
        assertGeomEqualsWithSrid(expected, get)
        expected = [[[[0,0,8,0], [3,0,6,1], [3,2,4,1], [1,3,2,0], [0,0,4,1]], [[1,1,1,5], [2,1,2,4], [2,2,2,3], [1,1,1,2]]], [[[10, 10, 1, 4], [11, 10, 2, 3], [11, 11, 3, 2], [10, 10, 4, 1]]]] as MultiPolygon
        expected.setSRID(4326)
        get = "SRID=4326;MULTIPOLYGONZM(((0 0 8 0, 3 0 6 1, 3 2 4 1, 1 3 2 0, 0 0 4 1), (1 1 1 5, 2 1 2 4, 2 2 2 3, 1 1 1 2)), ((10 10 1 4, 11 10 2 3, 11 11 3 2, 10 10 4 1)))" as MultiPolygon
        assertGeomEqualsWithSrid(expected, get)
    }
}
