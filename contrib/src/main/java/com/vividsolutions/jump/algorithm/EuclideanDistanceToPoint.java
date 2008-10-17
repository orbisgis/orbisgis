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

package com.vividsolutions.jump.algorithm;

import com.vividsolutions.jts.geom.*;

/**
 * Computes the Euclidean distance (L2 metric) from a Point to a Geometry.
 * Also computes two points which are separated by the distance.
 */
public class EuclideanDistanceToPoint {

  // used for point-line distance calculation
  private static LineSegment tempSegment = new LineSegment();

  public EuclideanDistanceToPoint() {
  }

  public static void computeDistance(Geometry geom, Coordinate pt, PointPairDistance ptDist)
  {
    if (geom instanceof LineString) {
      computeDistance((LineString) geom, pt, ptDist);
    }
    else if (geom instanceof Polygon) {
      computeDistance((Polygon) geom, pt, ptDist);
    }
    else if (geom instanceof GeometryCollection) {
      GeometryCollection gc = (GeometryCollection) geom;
      for (int i = 0; i < gc.getNumGeometries(); i++) {
        Geometry g = gc.getGeometryN(i);
        computeDistance(g, pt, ptDist);
      }
    }
    else { // assume geom is Point
      ptDist.setMinimum(geom.getCoordinate(), pt);
    }
  }
  public static void computeDistance(LineString line, Coordinate pt, PointPairDistance ptDist)
  {
    Coordinate[] coords = line.getCoordinates();
    for (int i = 0; i < coords.length - 1; i++) {
      tempSegment.setCoordinates(coords[i], coords[i + 1]);
      // this is somewhat inefficient - could do better
      Coordinate closestPt = tempSegment.closestPoint(pt);
      ptDist.setMinimum(closestPt, pt);
    }
  }

  public static void computeDistance(LineSegment segment, Coordinate pt, PointPairDistance ptDist)
  {
    Coordinate closestPt = segment.closestPoint(pt);
    ptDist.setMinimum(closestPt, pt);
  }

  public static void computeDistance(Polygon poly, Coordinate pt, PointPairDistance ptDist)
  {
    computeDistance(poly.getExteriorRing(), pt, ptDist);
    for (int i = 0; i < poly.getNumInteriorRing(); i++) {
      computeDistance(poly.getInteriorRingN(i), pt, ptDist);
    }
  }
}
