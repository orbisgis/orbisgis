/*
 * The JCS Conflation Suite (JCS) is a library of Java classes that
 * can be used to build automated or semi-automated conflation solutions.
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

package com.vividsolutions.jcs.qa;

import java.util.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.algorithm.PointLocator;
import com.vividsolutions.jump.util.CoordinateArrays;

/**
 * Creates overlap indicators for two geometries by finding all segments which
 * overlap the other Geometry.  This is more expensive than using
 * {@link OverlapBoundaryIndicators} but it is more robust.
 * One case where this is useful is where an overlap is a linestring (i.e. where the overlap is
 * too small to be computed as an area).  {@link OverlapBoundaryIndicators} will fail in this case.
 * Note that it is possible that there is an overlap in which no vertices
 * lie inside the other geometry (e.g. a star-of-david).  In this case
 * the Overlap indicators will be computed correctly, but no Overlap Size indicators will be computed.
 * However, this case should be handled by {@link OverlapBoundaryIndicators}.
 */
public class OverlapSegmentIndicators {

  private static List getCoordinates(Geometry g)
  {
    List coordList = new ArrayList();
    List coordArrays = CoordinateArrays.toCoordinateArrays(g, false);
    for (Iterator i = coordArrays.iterator(); i.hasNext(); ) {
      Coordinate[] coord = (Coordinate[]) i.next();
      for (int j = 0; j < coord.length - 1; j++) {
        coordList.add(coord[j]);
      }
    }
    return coordList;
  }

  /**
   * Tests whether the interiors of two Geometries intersect.
   * @param a a Geometry
   * @param b another Geometry
   * @return <code>true</code> if the interiors of a and b intersect
   */
  private static boolean interiorsIntersect(Geometry a, Geometry b)
  {
    IntersectionMatrix im = a.relate(b);
    return im.get(Location.INTERIOR, Location.INTERIOR) >= 0;
  }

  private PointLocator ptLoc = new PointLocator();
  private List insideCoords = new ArrayList();// a list of Coordinates
  private List overlapInd = new ArrayList();// a list of Geometry's
  private GeometryFactory geomFact = new GeometryFactory();

  public OverlapSegmentIndicators(Geometry g1, Geometry g2)
  {
    compute(g1, g2);
    compute(g2, g1);
  }

  public List getOverlapIndicators()
  {
    return overlapInd;
  }

  public List getSizeIndicators()
  {
    List sizeGeom = new ArrayList();
    for (Iterator i = insideCoords.iterator(); i.hasNext(); ) {
      Coordinate coord = (Coordinate) i.next();
      Geometry ptGeom = geomFact.createPoint(coord);
      sizeGeom.add(ptGeom);
    }
    return sizeGeom;
  }

  private void compute(Geometry g1, Geometry g2)
  {
    computeSizeIndicators(g1, g2);
    computeOverlapIndicators(g1, g2);
  }
  private void computeSizeIndicators(Geometry g1, Geometry g2)
  {
    List overlappingCoords = getCoordinates(g1);
    for (Iterator i = overlappingCoords.iterator(); i.hasNext(); ) {
      Coordinate coord = (Coordinate) i.next();
      if (isInside(coord, g2))
        insideCoords.add(coord);
    }
  }

  private void computeOverlapIndicators(Geometry g1, Geometry g2)
  {
    List coordArrays = CoordinateArrays.toCoordinateArrays(g1, true);
    for (Iterator i = coordArrays.iterator(); i.hasNext(); ) {
      Coordinate[] coord = (Coordinate[]) i.next();
      for (int j = 0; j < coord.length - 1; j++) {
        Coordinate[] lineCoords = new Coordinate[]
        { new Coordinate(coord[j]), new Coordinate(coord[j + 1]) };
        Geometry lineGeom = geomFact.createLineString(lineCoords);
        if (interiorsIntersect(g2, lineGeom))
          overlapInd.add(lineGeom);
      }
    }
  }

  private boolean isInside(Coordinate coord, Geometry g)
  {
    if (! g.getEnvelopeInternal().contains(coord))
      return false;
    return ptLoc.locate(coord, g) == Location.INTERIOR;
  }

}
