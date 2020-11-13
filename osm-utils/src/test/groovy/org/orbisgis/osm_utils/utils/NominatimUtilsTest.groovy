package org.orbisgis.osm_utils.utils

import org.junit.jupiter.api.Test
import org.locationtech.jts.geom.Envelope

/**
 * Test class dedicated to {@link NominatimUtils}
 */
class NominatimUtilsTest {

    @Test
    void geometryFromNominatimTest(){
        def coords = [10, 11, 12, 13]
        assert "Env[10.0 : 12.0, 11.0 : 13.0]" == (coords as Envelope).toString()
    }
}
