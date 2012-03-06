package org.gdms.data.values;

import com.vividsolutions.jts.geom.Geometry;
import org.jproj.CoordinateReferenceSystem;

/**
 *
 * @author Antoine Gourlay
 */
public interface GeometryValue extends Value {

    void setValue(Geometry value);
    
    CoordinateReferenceSystem getCRS();

}
