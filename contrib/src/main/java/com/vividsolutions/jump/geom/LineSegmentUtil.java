
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


/**
 * Utility functions for {@link LineSegment}s.
 * <p>
 * <i>Note:
 * Eventually some of these functions may be moved into the JTS LineSegment class.</i>
 */
public class LineSegmentUtil {
    /**
     * Projects one line segment onto another and returns the resulting
     * line segment.
     * The returned line segment will be a subset of
     * the target line line segment.  This subset may be null, if
     * the segments are oriented in such a way that there is no projection.
     *
     * @param tgt the line segment to be projected onto
     * @param seg the line segment to project
     * @return the projected line segment, or <code>null</code> if there is no overlap
     */
    public static LineSegment project(LineSegment tgt, LineSegment seg) {
        double pf0 = tgt.projectionFactor(seg.p0);
        double pf1 = tgt.projectionFactor(seg.p1);

        // check if segment projects at all
        if ((pf0 >= 1.0) && (pf1 >= 1.0)) {
            return null;
        }

        if ((pf0 <= 0.0) && (pf1 <= 0.0)) {
            return null;
        }

        Coordinate newp0 = tgt.project(seg.p0);

        if (pf0 < 0.0) {
            newp0 = tgt.p0;
        }

        if (pf0 > 1.0) {
            newp0 = tgt.p1;
        }

        Coordinate newp1 = tgt.project(seg.p1);

        if (pf1 < 0.0) {
            newp1 = tgt.p0;
        }

        if (pf1 > 1.0) {
            newp1 = tgt.p1;
        }

        return new LineSegment(newp0, newp1);
    }

    /**
     * Computes the Hausdorff distance between two LineSegments.
     * To compute the Hausdorff distance, it is sufficient to compute
     * the distance from one segment's endpoints to the other segment
     * and choose the maximum.
     *
     * @param seg0
     * @param seg1
     * @return the Hausdorff distance between the segments
     */
    public static double hausdorffDistance(LineSegment seg0, LineSegment seg1) {
        double hausdorffDist = seg0.distance(seg1.p0);
        double dist;
        dist = seg0.distance(seg1.p1);

        if (dist > hausdorffDist) {
            hausdorffDist = dist;
        }

        dist = seg1.distance(seg0.p0);

        if (dist > hausdorffDist) {
            hausdorffDist = dist;
        }

        dist = seg1.distance(seg0.p1);

        if (dist > hausdorffDist) {
            hausdorffDist = dist;
        }

        return hausdorffDist;
    }

    /**
     * Converts a LineSegment to a LineString.
     * @param factory a factory used to create the LineString
     * @param seg the LineSegment to convert
     * @return a new LineString based on the segment
     */
    public static LineString asGeometry(GeometryFactory factory, LineSegment seg) {
        Coordinate[] coord = { new Coordinate(seg.p0), new Coordinate(seg.p1) };
        LineString line = factory.createLineString(coord);

        return line;
    }

    //Is this the same as LineSegment's #closestPoint method? If so, this method
    //should be removed. [Jon Aquino]
    /*
    public static Coordinate OLDclosestPoint(LineSegment seg, Coordinate p) {
        double factor = seg.projectionFactor(p);

        if ((factor > 0) && (factor < 1)) {
            return seg.project(p);
        }

        double dist0 = seg.p0.distance(p);
        double dist1 = seg.p1.distance(p);

        if (dist0 < dist1) {
            return seg.p0;
        }

        return seg.p1;
    }
    */
}
