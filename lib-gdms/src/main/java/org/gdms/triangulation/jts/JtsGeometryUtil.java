package org.gdms.triangulation.jts;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.PrecisionModel;

public final class JtsGeometryUtil {
	  
	  private JtsGeometryUtil() {
	  }
	  
	  /**
	   * Add a evelation (z) value for a coordinate that is on a line segment.
	   * 
	   * @param coordinate The Coordinate.
	   * @param line The line segment the coordinate is on.
	   */
	  public static void addElevation(final Coordinate coordinate,
	    final LineSegment line) {
	    double z = getElevation(line, coordinate);
	    coordinate.z = z;
	  }

	  public static double getElevation(final LineSegment line,
	    final Coordinate coordinate) {
	    Coordinate c0 = line.p0;
	    Coordinate c1 = line.p1;
	    double fraction = coordinate.distance(c0) / line.getLength();
	    double z = c0.z + (c1.z - c0.z) * (fraction);
	    return z;
	  }
	  
	  public static void addElevation(PrecisionModel precisionModel,
			    Coordinate coordinate, LineSegment3D line) {
			    addElevation(coordinate, line);
			    coordinate.z = precisionModel.makePrecise(coordinate.z);

			  }
	  
	  public static LineSegment addLength(final LineSegment line,
			    final double startDistance, final double endDistance) {
			    double angle = line.angle();
			    Coordinate c1 = offset(line.p0, angle, -startDistance);
			    Coordinate c2 = offset(line.p1, angle, endDistance);
			    return new LineSegment(c1, c2);

			  }
	  
	  public static Coordinate offset(final Coordinate coordinate,
			    final double angle, final double distance) {
			    double newX = coordinate.x + distance * Math.cos(angle);
			    double newY = coordinate.y + distance * Math.sin(angle);
			    Coordinate newCoordinate = new Coordinate(newX, newY);
			    return newCoordinate;

			  }
	  
}

