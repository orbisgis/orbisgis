package org.gdms.data.values;

import com.vividsolutions.jts.geom.Geometry;

/**
 *
 * @author Antoine Gourlay
 */
public interface GeometryValue extends Value {

    void setValue(Geometry value);

}
