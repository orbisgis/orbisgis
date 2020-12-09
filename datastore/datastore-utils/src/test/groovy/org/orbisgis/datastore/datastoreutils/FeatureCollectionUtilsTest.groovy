package org.orbisgis.datastore.datastoreutils

import org.geotools.data.shapefile.ShapefileDataStore
import org.junit.jupiter.api.Test
/**
 * Test class dedicated to {@link FeatureCollectionUtils}.
 *
 * @author Erwan Bocher (CNRS 2020)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */
class FeatureCollectionUtilsTest {

    @Test
    void eachTest() {
        def ds = new ShapefileDataStore(this.getClass().getResource("landcover2000.shp"))
        ds.landcover2000.features.each
                { assert it.toString().startsWith("SimpleFeatureImpl:landcover2000=[SimpleFeatureImpl.Attribute: ")}
    }
}
