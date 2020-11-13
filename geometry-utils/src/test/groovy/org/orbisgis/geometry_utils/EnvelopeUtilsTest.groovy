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
    void envelopeAsTypeTest() {
        def poly = [[10,10], [11,11], [10,11], [10,10]] as Polygon
        def expected = [[10,10], [10,11], [11,11], [11,10], [10,10]] as Polygon
        def env = poly as Envelope
        def get = env as Polygon

        assert env === env

        assert expected == get
        assertCoordinatesEquals(expected.coordinates, get.coordinates)
    }

    @Test
    void toBboxTest() {
        def coords = [10,12,11,13]
        assert "Env[10.0 : 11.0, 12.0 : 13.0]" == coords.toEnvelope().toString()
        coords = [13,12,10,11]
        assert "Env[10.0 : 11.0, 12.0 : 13.0]" == coords.toEnvelope().toString()
        coords = [13,12,10,11, 14]
        assert !coords.toEnvelope()
        coords = []
        assert !coords.toEnvelope()
    }
}
