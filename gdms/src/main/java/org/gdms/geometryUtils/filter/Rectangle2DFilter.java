package org.gdms.geometryUtils.filter;

import java.awt.geom.Rectangle2D;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;

/**
 * A coordinate filter to get a Rectangle2D from a geometry
 * Source : http://docs.codehaus.org/display/GEOTDOC/04+Using+CoordinateFilter+to+implement+operations
 * Usage :
 * 
 * 	Rectangle2DFilter bf = new Rectangle2DFilter();
 *  myGeometry.apply(bf);
 *  Rectangle2D bounds = bf.getBounds();
 * 
 * @author Michael Bedward,
 *
 */
public class Rectangle2DFilter implements CoordinateFilter {

    double minx,  miny,  maxx,  maxy;
    boolean first = true;

    /**
     * First coordinate visited initializes the min and max fields.
     * Subsequent coordinates are compared to current bounds.
     */
    public void filter(Coordinate c) {
        if (first) {
            minx = maxx = c.x;
            miny = maxy = c.y;
            first = false;
        } else {
            minx = Math.min(minx, c.x);
            miny = Math.min(miny, c.y);
            maxx = Math.max(maxx, c.x);
            maxy = Math.max(maxy, c.y);
        }
    }

    /**
     * Return bounds as a Rectangle2D object
     */
    Rectangle2D getBounds() {
        return new Rectangle2D.Double(minx, miny, maxx - minx, maxy - miny);
    }
}
