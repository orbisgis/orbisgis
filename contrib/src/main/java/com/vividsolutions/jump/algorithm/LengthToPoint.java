package com.vividsolutions.jump.algorithm;

import com.vividsolutions.jts.geom.*;

/**
 * Computes the length along a LineString to the point on the line nearest a given point.
 */
//Martin made a decision to create this duplicate of a class from JCS. 
//[Jon Aquino 2004-10-25]
public class LengthToPoint
{
  public static double lengthAlongSegment(LineSegment seg, Coordinate pt)
  {
    double projFactor = seg.projectionFactor(pt);
    double len = 0.0;
    if (projFactor <= 0.0)
      len = 0.0;
    else if (projFactor <= 1.0)
      len = projFactor * seg.getLength();
    else
      len = seg.getLength();
    return len;
  }

  /**
   * Computes the length along a LineString to the point on the line nearest a given point.
   */
  public static double length(LineString line, Coordinate inputPt)
  {
    LengthToPoint lp = new LengthToPoint(line, inputPt);
    return lp.getLength();
  }

  private double minDistanceToPoint;
  private double locationLength;

  public LengthToPoint(LineString line, Coordinate inputPt)
  {
    computeLength(line, inputPt);
  }

  public double getLength()
  {
    return locationLength;
  }

  private void computeLength(LineString line, Coordinate inputPt)
  {
    minDistanceToPoint = Double.MAX_VALUE;
    double baseLocationDistance = 0.0;
    Coordinate[] pts = line.getCoordinates();
    LineSegment seg = new LineSegment();
    for (int i = 0; i < pts.length - 1; i++) {
      seg.p0 = pts[i];
      seg.p1 = pts[i + 1];
      updateLength(seg, inputPt, baseLocationDistance);
      baseLocationDistance += seg.getLength();

    }
  }

  private void updateLength(LineSegment seg, Coordinate inputPt, double segStartLocationDistance)
  {
    double dist = seg.distance(inputPt);
    if (dist > minDistanceToPoint) return;
    minDistanceToPoint = dist;
    // found new minimum, so compute location distance of point
    double projFactor = seg.projectionFactor(inputPt);
    if (projFactor <= 0.0)
      locationLength = segStartLocationDistance;
    else if (projFactor <= 1.0)
      locationLength = segStartLocationDistance + projFactor * seg.getLength();
    else
      locationLength = segStartLocationDistance + seg.getLength();
  }
}