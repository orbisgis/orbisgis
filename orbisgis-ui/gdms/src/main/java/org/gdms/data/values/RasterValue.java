package org.gdms.data.values;

import org.grap.model.GeoRaster;

/**
 *
 * @author Antoine Gourlay
 */
public interface RasterValue extends Value {

    /**
     * @param geoRaster the geoRaster to set
     */
    void setValue(GeoRaster value);

}
