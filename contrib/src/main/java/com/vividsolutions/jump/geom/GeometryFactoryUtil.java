
/*
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI 
 * for visualizing and manipulating spatial features with geometry and attributes.
 *
 * Copyright (C) 2003 Vivid Solutions
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * For more information, contact:
 *
 * Vivid Solutions
 * Suite #1A
 * 2328 Government Street
 * Victoria BC  V8T 5G5
 * Canada
 *
 * (250)385-6040
 * www.vividsolutions.com
 */

package com.vividsolutions.jump.geom;

import java.util.*;

import com.vividsolutions.jts.geom.*;


/**
 * Some utility functions not present in the JTS version of this class
 */
public class GeometryFactoryUtil {
    public GeometryFactoryUtil() {
    }

    /**
     * Builds a geometry containing only the element geometries from the
     * input list which have the requested dimension.
     * The result is thus guaranteed to have the dimension requested.
     *
     * @param geomList
     * @param dimension
     */
    public static Geometry buildGeometry(Geometry geom, int dimension) {
        GeometryFactory factory = new GeometryFactory(geom.getPrecisionModel(),
                geom.getSRID());

        if (geom instanceof GeometryCollection) {
            List geomList = dimensionFilter((GeometryCollection) geom, dimension);

            if (geomList.isEmpty()) {
                return getEmptyDimensionalGeometry(factory, dimension);
            }

            return factory.buildGeometry(geomList);
        } else if (geom.getDimension() == dimension) {
            return geom;
        } else {
            return getEmptyDimensionalGeometry(factory, dimension);
        }
    }

    public static List dimensionFilter(GeometryCollection gc, int dimension) {
        List geomList = new ArrayList();

        for (Iterator i = new GeometryCollectionIterator(gc); i.hasNext();) {
            Geometry g = (Geometry) i.next();

            if (!(g instanceof GeometryCollection)) {
                if (g.getDimension() == dimension) {
                    geomList.add(g.clone());
                }
            }
        }

        return geomList;
    }

    public static Geometry getEmptyDimensionalGeometry(
        GeometryFactory factory, int dimension) {
        switch (dimension) {
        case 0:
            return factory.createMultiPoint(new Coordinate[0]);

        case 1:
            return factory.createMultiLineString(new LineString[0]);

        case 2:
            return factory.createMultiPolygon(new Polygon[0]);
        }

        return factory.createGeometryCollection(new Geometry[0]);
    }
}
