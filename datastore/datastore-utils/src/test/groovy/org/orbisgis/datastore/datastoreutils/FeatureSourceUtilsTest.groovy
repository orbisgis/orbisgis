package org.orbisgis.datastore.datastoreutils

import org.geotools.data.Query
import org.geotools.data.shapefile.ShapefileDataStore
import org.geotools.feature.FeatureCollection
import org.junit.jupiter.api.Test
import org.opengis.filter.Filter

/**
 * Test class dedicated to {@link FeatureSourceUtils}.
 *
 * @author Erwan Bocher (CNRS 2020)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */
class FeatureSourceUtilsTest {

    @Test
    void featureCollectionTest() {
        def ds = new ShapefileDataStore(this.getClass().getResource("landcover2000.shp"))
        assert ds.landcover2000.featureCollection in FeatureCollection
        assert ds.landcover2000.getFeatureCollection() in FeatureCollection
        assert ds.landcover2000.getFeatureCollection(Query.ALL) in FeatureCollection
        assert ds.landcover2000.getFeatureCollection(Filter.INCLUDE) in FeatureCollection
    }
}
