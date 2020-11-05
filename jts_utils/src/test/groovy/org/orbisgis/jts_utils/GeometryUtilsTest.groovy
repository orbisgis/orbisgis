package org.orbisgis.jts_utils

import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory

/**
 * Abstract class containing common methods for Geometry utilities test.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */
abstract class GeometryUtilsTest {

    protected static final GeometryFactory FACTORY = new GeometryFactory()

    protected static void assertCoordinateEquals(Coordinate expected, Coordinate get) {
        assert expected.x == get.x
        assert expected.y == get.y
        assert expected.z == get.z
        assert expected.m == get.m
    }

    protected static void assertCoordinatesEquals(Coordinate[] expected, Coordinate[] get) {
        assert expected.length == get.length
        for(int i=0; i < expected.length; i++) {
            assertCoordinateEquals(expected[i], get[i])
        }
    }
}
