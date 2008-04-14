package org.gdms.triangulation.jts;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.PrecisionModel;

public class LineSegment3D extends LineSegment {

  public LineSegment3D() {
  }

  public LineSegment3D(Coordinate p0, Coordinate p1) {
    super(p0, p1);
  }

  public LineSegment3D(LineSegment ls) {
    super(ls);
  }

  public Coordinate pointAlong3D(double segmentLengthFraction) {
    Coordinate coord = new Coordinate();
    coord.x = p0.x + segmentLengthFraction * (p1.x - p0.x);
    coord.y = p0.y + segmentLengthFraction * (p1.y - p0.y);
    addElevation(coord);
    return coord;
  }

  public Coordinate intersection3D(final LineSegment line) {
    Coordinate intersection = super.intersection(line);
    if (intersection != null) {
      addElevation(intersection);
    }
    return intersection;
  }

  /**
   * Add a evelation (z) value for a coordinate that is on this line segment.
   * 
   * @param coordinate The Coordinate.
   * @param line The line segment the coordinate is on.
   */
  public void addElevation(final Coordinate coordinate) {
    double z0 = p0.z;
    double z1 = p1.z;
    if (!Double.isNaN(z0) && !Double.isNaN(z0)) {
      double fraction = coordinate.distance(p0) / getLength();
      coordinate.z = z0 + (z1 - z0) * (fraction);
    }
  }

  /**
   * Add a evelation (z) value for a coordinate that is on this line segment.
   * 
   * @param coordinate The Coordinate.
   * @param line The line segment the coordinate is on.
   */
  public void addElevation(final Coordinate coordinate, final PrecisionModel model) {
    double z0 = p0.z;
    double z1 = p1.z;
    if (!Double.isNaN(z0) && !Double.isNaN(z0)) {
      double fraction = coordinate.distance(p0) / getLength();
      coordinate.z = model.makePrecise(z0 + (z1 - z0) * (fraction));
    }
  }
}
