package org.orbisgis.groovy_utils

import org.junit.jupiter.api.Test
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.CoordinateXY
import org.locationtech.jts.geom.CoordinateXYM
import org.locationtech.jts.geom.CoordinateXYZM
import org.locationtech.jts.geom.Coordinates
import org.locationtech.jts.geom.GeometryCollection
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.MultiLineString
import org.locationtech.jts.geom.MultiPoint
import org.locationtech.jts.geom.MultiPolygon
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon

/**
 * Test class dedicated to {@link org.orbisgis.groovy_utils.GeometryConversionUtils}.
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

}
