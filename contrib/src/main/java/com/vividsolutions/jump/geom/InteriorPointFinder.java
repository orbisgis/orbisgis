
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

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jump.util.MathUtil;


/**
 * Find a reasonable point at which to label a Geometry.
 * <p>
 * Algorithm is:
 * <ul>
 *   <li>Find the intersections between the geometry and a line halfway
 *       down the envelope
 *   <li>Pick the midpoint of the largest intersection (the intersections
 *       will be lines and points)
 * </ul>
 */
public class InteriorPointFinder {
    private GeometryFactory factory = new GeometryFactory();

    //<<TODO:REFACTORING>> Move this class to JTS [Jon Aquino]
    public InteriorPointFinder() {
    }

    /**
     * Finds a reasonable point at which to label a Geometry.
     * @param geometry the geometry to analyze
     * @return the midpoint of the largest intersection between the geometry and
     * a line halfway down its envelope
     */
    public Coordinate findPoint(Geometry geometry) {
        if (geometry.isEmpty()) {
            //Can't use geometry#getPoint because it returns null [Jon Aquino]
            return new Coordinate(0, 0);
        }

        if (geometry.getDimension() == 0) {
            //Points and multipoints [Jon Aquino]
            return geometry.getCoordinate();
        }

        if (geometry instanceof GeometryCollection) {
            return findPoint(((GeometryCollection) geometry).getGeometryN(0));
        }

        Geometry envelopeMiddle = envelopeMiddle(geometry);

        if (envelopeMiddle instanceof Point) {
            return envelopeMiddle.getCoordinate();
        }

        Geometry intersections = envelopeMiddle.intersection(geometry);
        Geometry widestIntersection = widestGeometry(intersections);

        return centre(widestIntersection.getEnvelopeInternal());
    }

    //@return if geometry is a collection, the widest sub-geometry; otherwise,
    //the geometry itself
    protected Geometry widestGeometry(Geometry geometry) {
        if (!(geometry instanceof GeometryCollection)) {
            return geometry;
        }

        return widestGeometry((GeometryCollection) geometry);
    }

    private Geometry widestGeometry(GeometryCollection gc) {
        if (gc.isEmpty()) {
            return gc;
        }

        Geometry widestGeometry = gc.getGeometryN(0);

        for (int i = 1; i < gc.getNumGeometries(); i++) { //Start at 1

            if (gc.getGeometryN(i).getEnvelopeInternal().getWidth() > widestGeometry.getEnvelopeInternal()
                                                                                        .getWidth()) {
                widestGeometry = gc.getGeometryN(i);
            }
        }

        return widestGeometry;
    }

    protected Geometry envelopeMiddle(Geometry geometry) {
        Envelope envelope = geometry.getEnvelopeInternal();

        if (envelope.getWidth() == 0) {
            return factory.createPoint(centre(envelope));
        }

        return factory.createLineString(new Coordinate[] {
                new Coordinate(envelope.getMinX(),
                    MathUtil.avg(envelope.getMinY(), envelope.getMaxY())),
                new Coordinate(envelope.getMaxX(),
                    MathUtil.avg(envelope.getMinY(), envelope.getMaxY()))
            });
    }

    /**
     * Returns the centre-of-mass of the envelope.
     * @param envelope the envelope to analyze
     * @return the centre of the envelope
     */
    public Coordinate centre(Envelope envelope) {
        //<<TODO:REFACTORING>> Move #avg from GUIUtilities to a core JCS util class [Jon Aquino]
        return new Coordinate(MathUtil.avg(envelope.getMinX(),
                envelope.getMaxX()),
            MathUtil.avg(envelope.getMinY(), envelope.getMaxY()));
    }
}
