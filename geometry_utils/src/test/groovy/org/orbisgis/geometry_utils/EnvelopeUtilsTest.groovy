package org.orbisgis.geometry_utils

import org.junit.jupiter.api.Test
import org.locationtech.jts.geom.Envelope
import org.locationtech.jts.geom.Polygon

/**
 * Test class dedicated to {@link org.orbisgis.geometry_utils.EnvelopeUtils}.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */
class EnvelopeUtilsTest extends GeometryUtilsTest {

    @Test
    void asTypeTest() {
        def poly = [[10,10], [11,11], [10,11], [10,10]] as Polygon
        def expected = [[10,10], [10,11], [11,11], [11,10], [10,10]] as Polygon
        def env = poly as Envelope
        def get = env as Polygon

        assert env === env

        assert expected == get
        assertCoordinatesEquals(expected.coordinates, get.coordinates)
    }
}
