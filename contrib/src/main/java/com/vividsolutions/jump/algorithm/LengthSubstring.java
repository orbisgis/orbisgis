package com.vividsolutions.jump.algorithm;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.util.Assert;

/**
 * Computes a substring of a {@link LineString}
 * between given distances along the line.
 * <ul>
 * <li>The distances are clipped to the actual line length
 * <li>If the start distance is equal to the end distance,
 * a zero-length line with two identical points is returned
 * <li>FUTURE: If the start distance is greater than the end distance,
 * an inverted section of the line is returned
 * </ul>
 * <p>
 * FUTURE: should handle startLength > endLength, and flip the returned
 * linestring. Also should handle negative lengths (they are measured from end
 * of line backwards).
 */
// Martin made a decision to create this duplicate of a class from JCS. 
// [Jon Aquino 2004-10-25]
public class LengthSubstring
{
  public static LineString getSubstring(LineString line, double startLength,
                                        double endLength) {
    LengthSubstring ls = new LengthSubstring(line);
    return ls.getSubstring(startLength, endLength);
  }

  private LineString line;

  public LengthSubstring(LineString line) {
    this.line = line;
  }

  public LineString getSubstring(double startDistance, double endDistance)
  {
    // future: if start > end, flip values and return an inverted line
    Assert.isTrue(startDistance <= endDistance, "inverted distances not currently supported");

    Coordinate[] coordinates = line.getCoordinates();
    // check for a zero-length segment and handle appropriately
    if (endDistance <= 0.0) {
      return line.getFactory().createLineString(
          new Coordinate[] { coordinates[0], coordinates[0]});
    }
    if (startDistance >= line.getLength()) { return line.getFactory()
                       .createLineString(
                       new Coordinate[] { coordinates[coordinates.length - 1],
    coordinates[coordinates.length - 1]}); }
    if (startDistance < 0.0) {
      startDistance = 0.0;
    }
    return computeSubstring(startDistance, endDistance);
  }

  /**
   * Assumes input is strictly valid (e.g. startDist < endDistance)
   *
   * @param startDistance
   * @param endDistance
   * @return
   */
  private LineString computeSubstring(double startDistance, double endDistance)
  {
    Coordinate[] coordinates = line.getCoordinates();
    CoordinateList newCoordinates = new CoordinateList();
    double segmentStartDistance = 0.0;
    double segmentEndDistance = 0.0;
    boolean started = false;
    int i = 0;
    LineSegment segment = new LineSegment();
    while (i < coordinates.length - 1 && endDistance > segmentEndDistance) {
      segment.p0 = coordinates[i];
      segment.p1 = coordinates[i + 1];
      i++;
      segmentStartDistance = segmentEndDistance;
      segmentEndDistance = segmentStartDistance + segment.getLength();

      if (startDistance > segmentEndDistance)
        continue;
      if (startDistance >= segmentStartDistance
          && startDistance < segmentEndDistance) {
        newCoordinates.add(LocatePoint.pointAlongSegment(segment.p0, segment.p1,
            startDistance - segmentStartDistance), false);
      }
      /*
      if (startDistance >= segmentStartDistance
          && startDistance == segmentEndDistance) {
        newCoordinates.add(new Coordinate(segment.p1), false);
      }
      */
      if (endDistance >= segmentEndDistance) {
        newCoordinates.add(new Coordinate(segment.p1), false);
      }
      if (endDistance >= segmentStartDistance
          && endDistance < segmentEndDistance) {
        newCoordinates.add(LocatePoint.pointAlongSegment(segment.p0, segment.p1,
            endDistance - segmentStartDistance), false);
      }
    }
    Coordinate[] newCoordinateArray = newCoordinates.toCoordinateArray();
    /**
     * Ensure there is enough coordinates to build a valid line. Make a
     * 2-point line with duplicate coordinates, if necessary There will
     * always be at least one coordinate in the coordList.
     */
    if (newCoordinateArray.length <= 1) {
      newCoordinateArray = new Coordinate[] { newCoordinateArray[0], newCoordinateArray[0]};
    }
    return line.getFactory().createLineString(newCoordinateArray);
  }
}