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
import com.vividsolutions.jump.geom.LineSegmentUtil;

/**
 * Implements algorithm for computing a distance metric
 * which can be thought of as the "Vertex Hausdorff Distance".
 * This is the Hausdorff distance restricted to vertices for
 * one of the geometries.
 * Also computes two points of the Geometries which are separated by the computed distance.
 * <p>
 * <b>NOTE: This algorithm does NOT compute the full Hausdorff distance correctly, but
 * an approximation that is correct for a large subset of useful cases.
 * One important part of this subset is Linestrings that are roughly parallel to each other,
 * and roughly equal in length - just what is needed for line matching.
 * </b>
 */
public class VertexHausdorffDistance
{

  public static double distance(Geometry g0, Geometry g1)
  {
    VertexHausdorffDistance vhd = new VertexHausdorffDistance(g0, g1);
    return vhd.distance();
  }

  private PointPairDistance ptDist = new PointPairDistance();

  public VertexHausdorffDistance(Geometry g0, Geometry g1)
  {
    compute(g0, g1);
  }

  public VertexHausdorffDistance(LineSegment seg0, LineSegment seg1)
  {
    compute(seg0, seg1);
  }

  public double distance() { return ptDist.getDistance(); }

  public Coordinate[] getCoordinates() { return ptDist.getCoordinates(); }

  private void compute(LineSegment seg0, LineSegment seg1)
  {
    computeMaxPointDistance(seg0, seg1, ptDist);
    computeMaxPointDistance(seg1, seg0, ptDist);
  }

  /**
   * Computes the maximum oriented distance between two line segments,
   * as well as the point pair separated by that distance.
   *
   * @param seg0 the line segment containing the furthest point
   * @param seg1 the line segment containing the closest point
   * @param ptDist the point pair and distance to be updated
   */
  private void computeMaxPointDistance(LineSegment seg0, LineSegment seg1, PointPairDistance ptDist)
  {
    Coordinate closestPt0 = seg0.closestPoint(seg1.p0);
    ptDist.setMaximum(closestPt0, seg1.p0);
    Coordinate closestPt1 = seg0.closestPoint(seg1.p1);
    ptDist.setMaximum(closestPt1, seg1.p1);
  }

  private void compute(Geometry g0, Geometry g1)
  {
    computeMaxPointDistance(g0, g1, ptDist);
    computeMaxPointDistance(g1, g0, ptDist);
  }

  private void computeMaxPointDistance(Geometry pointGeom, Geometry geom, PointPairDistance ptDist)
  {
    MaxPointDistanceFilter distFilter = new MaxPointDistanceFilter(geom);
    pointGeom.apply(distFilter);
    ptDist.setMaximum(distFilter.getMaxPointDistance());
  }

  public static class MaxPointDistanceFilter
      implements CoordinateFilter
  {
    private PointPairDistance maxPtDist = new PointPairDistance();
    private PointPairDistance minPtDist = new PointPairDistance();
    private EuclideanDistanceToPoint euclideanDist = new EuclideanDistanceToPoint();
    private Geometry geom;

    public MaxPointDistanceFilter(Geometry geom)
    {
      this.geom = geom;
    }

    public void filter(Coordinate pt)
    {
      minPtDist.initialize();
      euclideanDist.computeDistance(geom, pt, minPtDist);
      maxPtDist.setMaximum(minPtDist);
    }

    public PointPairDistance getMaxPointDistance() { return maxPtDist; }
  }
}
