package org.orbisgis.geometry_utils

import org.junit.jupiter.api.Test
import org.locationtech.jts.geom.*

/**
 * Test class dedicated to {@link org.orbisgis.geometry_utils.GeometryOperatorOverloadUtils}.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */
class GeometryOperatorOverloadTest extends GeometryUtilsTest {

    @Test
    void pointOperatorTest() {
        def pt1 = [1, 1] as Point
        def pt2 = [2, 2] as Point
        def multi = FACTORY.createMultiPoint(pt1, pt2)
        def line1 = [[1, 1], [2, 2]] as LineString
        def line3 = [[2, 2], [1, 1]] as LineString
        def line4 = [[1, 1], [1, 1], [2, 2]] as LineString

        assert multi == pt1 + pt2
        assertCoordinatesEquals(multi.coordinates, (pt1 + pt2).coordinates)

        assert line1 == pt1 << pt2
        assertCoordinatesEquals(line1.coordinates, (pt1 << pt2).coordinates)
        assert line3 == pt2 >> pt1
        assertCoordinatesEquals(line3.coordinates, (pt2 >> pt1).coordinates)
        assert line4 == pt1 >> line1
        assertCoordinatesEquals(line4.coordinates, (pt1 >> line1).coordinates)

        assert pt1+line1 instanceof GeometryCollection
        assert pt1+multi instanceof GeometryCollection
    }

    @Test
    void lineStringOperatorTest() {
        def pt = [0, 0] as Point
        def line1 = [[111, 112], [121, 122], [131, 132]] as LineString
        def line2 = [[211, 212], [221, 222], [231, 232]] as LineString
        def multi = FACTORY.createMultiLineString(line1, line2)
        def longLine1 = [[111, 112], [121, 122], [131, 132], [211, 212], [221, 222], [231, 232]] as LineString
        def longLine2 = [[211, 212], [221, 222], [231, 232], [111, 112], [121, 122], [131, 132]] as LineString
        def longLine10 = [[111, 112], [121, 122], [131, 132], [0, 0]] as LineString
        def longLine20 = [[0, 0], [211, 212], [221, 222], [231, 232]] as LineString

        assert multi == line1 + line2
        assertCoordinatesEquals(multi.coordinates, (line1 + line2).coordinates)

        assert longLine1 == line1 << line2
        assertCoordinatesEquals(longLine1.coordinates, (line1<<line2).coordinates)
        assert longLine1 == line1 >> line2
        assertCoordinatesEquals(longLine1.coordinates, (line1>>line2).coordinates)

        assert longLine10 == line1 << pt
        assertCoordinatesEquals(longLine10.coordinates, (line1<<pt).coordinates)
        assert longLine20 == pt >> line2
        assertCoordinatesEquals(longLine20.coordinates, (pt>>line2).coordinates)

        assert line1+pt instanceof GeometryCollection
        assert line1+multi instanceof GeometryCollection
    }

    @Test
    void polygonOperatorTest() {
        def pt = [0, 1] as Point
        def poly1 = [[0,0], [3,0], [3,2], [1,3], [0,0]] as Polygon
        def poly2 = [[1,1], [2,1], [2,2], [1,1]] as Polygon
        def multi = FACTORY.createMultiPolygon(poly1, poly2)
        def polyWithHole = [[[0,0], [3,0], [3,2], [1,3], [0,0]], [[1,1], [2,1], [2,2], [1,1]]] as Polygon
        def bigPoly = [[[0,0], [3,0], [3,2], [1,3], [0,1], [0,0]]] as Polygon

        assert poly1-poly2 == [[[0,0], [3,0], [3,2], [1,3], [0,0]], [[1,1], [2,1], [2,2], [1,1]]] as Polygon

        assert poly1<<pt == [[0,0], [3,0], [3,2], [1,3], [0,1], [0,0]] as Polygon
        assert pt>>poly1 == [[0,1], [0,0], [3,0], [3,2], [1,3], [0,1]] as Polygon

        assert multi == poly1 + poly2
        assertCoordinatesEquals(multi.coordinates, (poly1 + poly2).coordinates)

        assert bigPoly == poly1 << pt
        assertCoordinatesEquals(bigPoly.coordinates, (poly1<<pt).coordinates)

        assert polyWithHole == poly1 - poly2
        assertCoordinatesEquals(polyWithHole.coordinates, (poly1-poly2).coordinates)
        assert polyWithHole.numInteriorRing == (poly1-poly2).numInteriorRing
        assertCoordinatesEquals(polyWithHole.getInteriorRingN(0).coordinates,
                (poly1-poly2).getInteriorRingN(0).coordinates)

        assert poly1+pt instanceof GeometryCollection
        assert poly1+multi instanceof GeometryCollection
    }

    @Test
    void multiPointOperatorTest() {
        def pt = [0, 1] as Point
        def multi = [[0,0], [3,0], [3,2], [1,3]] as MultiPoint
        def expected1 = [[0,0], [3,0], [3,2], [1,3], [0, 1]] as MultiPoint
        def expected2 = [[0,1], [0,0], [3,0], [3,2], [1,3]] as MultiPoint

        def get = multi<<pt
        assert expected1 == get
        assertCoordinatesEquals(expected1.coordinates, get.coordinates)

        get = pt>>multi
        assert expected2 == get
        assertCoordinatesEquals(expected2.coordinates, get.coordinates)
    }

    @Test
    void multiLineStringOperatorTest() {
        def pt = [0, 1] as Point
        def multi = [[0,0], [3,0], [3,2], [1,3]] as MultiPoint
        def expected1 = [[0,0], [3,0], [3,2], [1,3], [0, 1]] as MultiPoint
        def expected2 = [[0,1], [0,0], [3,0], [3,2], [1,3]] as MultiPoint

        def get = multi<<pt
        assert expected1 == get
        assertCoordinatesEquals(expected1.coordinates, get.coordinates)

        get = pt>>multi
        assert expected2 == get
        assertCoordinatesEquals(expected2.coordinates, get.coordinates)
    }

    @Test
    void multiPolygonOperatorTest() {
        def poly = [[10,10], [11,11], [10,11], [10,10]] as Polygon
        def multi = [[[[0,0], [3,0], [3,2], [1,3], [0,0]]], [[[1,1], [2,1], [2,2], [1,1]]]] as MultiPolygon
        def expected1 = [[[[0,0], [3,0], [3,2], [1,3], [0,0]]], [[[1,1], [2,1], [2,2], [1,1]]], [[10,10], [11,11], [10,11], [10,10]]] as MultiPolygon
        def expected2 = [[[10,10], [11,11], [10,11], [10,10]], [[[0,0], [3,0], [3,2], [1,3], [0,0]]], [[[1,1], [2,1], [2,2], [1,1]]]] as MultiPolygon

        def get = multi<<poly
        assert expected1 == get
        assertCoordinatesEquals(expected1.coordinates, get.coordinates)

        get = poly>>multi
        assert expected2 == get
        assertCoordinatesEquals(expected2.coordinates, get.coordinates)
    }
}
