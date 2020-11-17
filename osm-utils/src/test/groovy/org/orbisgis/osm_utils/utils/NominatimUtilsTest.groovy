package org.orbisgis.osm_utils.utils

import org.junit.jupiter.api.Test
import org.locationtech.jts.geom.Envelope
import org.locationtech.jts.geom.Polygon

/**
 * Test class dedicated to {@link NominatimUtils}
 */
class NominatimUtilsTest {

    @Test
    void geometryFromNominatimTest(){
        def coords = [10, 11, 12, 13]
        assert ((coords as Envelope)as Polygon).toString() == NominatimUtils.geometryFromNominatim(coords).toString()
    }
}
