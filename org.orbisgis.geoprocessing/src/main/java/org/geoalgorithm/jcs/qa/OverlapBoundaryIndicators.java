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

package org.geoalgorithm.jcs.qa;

import java.util.*;

import org.geoutils.GeometryFactoryUtil;
import org.geoutils.VertexHausdorffDistance;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.precision.EnhancedPrecisionOp;



public class OverlapBoundaryIndicators {
  private static GeometryFactory factory = new GeometryFactory();

  /**
   * Creates a geometry representing the portion of the boundary of a geometry
   * which bounds the the overlap
   * area of two areal geometries which overlap.
   * This algorithm attempts to filter out parts of the intersection
   * which are line segments as opposed to areas.
   * The algorithm attempts to provide heuristics to avoid robustness problems with JTS.
   *
   * @param f0 an overlapping geometry
   * @param f1 the other one of the pair of overlapping geometries
   * @return the boundary of the
   */
  public static Geometry overlappingBoundary(Geometry f0, Geometry f1)
  {
    // get the portions of the intersection which are lines only
    Geometry intersectLines = null;
    try {
      Geometry intersect = EnhancedPrecisionOp.intersection(f0, f1);
      intersectLines = GeometryFactoryUtil.buildGeometry(intersect, 1);
//System.out.println("intersectLines");
//System.out.println(intersectLines);
    }
    catch (Exception ex) {
      // this should be a TopologyException - can ignore it
    }

    Geometry overlapBdy = EnhancedPrecisionOp.intersection(f0, f1.getBoundary());
    Geometry overlapBdyLines = GeometryFactoryUtil.buildGeometry(overlapBdy, 1);
//System.out.println("overlapBdyLines");
//System.out.println(overlapBdyLines);
    Geometry indAll = null;
    if (intersectLines != null) {
      indAll = EnhancedPrecisionOp.difference(overlapBdyLines, intersectLines);
    }
    else {
      indAll = EnhancedPrecisionOp.difference(overlapBdyLines, f0.getBoundary());
    }
    // return only the lines found
    Geometry indLines = GeometryFactoryUtil.buildGeometry(indAll, 1);
      /*
    }
    catch (Exception ex) {
      System.out.println(f0);
      System.out.println(f1);
    }
      */
    return indLines;
  }

  private List overlapIndicators = new ArrayList();// a list of Geometry's
  private List overlapSizeIndicators = new ArrayList();// a list of Geometry's

  public OverlapBoundaryIndicators(Geometry g1, Geometry g2)
  {
    compute(g2, g1);
  }

  public List getOverlapIndicators()
  {
    return overlapIndicators;
  }

  public List getSizeIndicators()
  {
    return overlapSizeIndicators;
  }

  private void compute(Geometry g0, Geometry g1)
  {
    // if we can't compute the intersection robustly, don't bother computing anything
    // user should use OverlapSegmentIndicator instead
    try {
      g0.intersection(g1);
    }
    catch (Exception ex) {
      return;
    }
    try {
      // create indicators showing overlapping boundary portion
      Geometry ob0 = overlappingBoundary(g0, g1);
      Geometry ob1 = overlappingBoundary(g1, g0);

      // don't add an invalid indicator
      if (! ob0.isEmpty()) overlapIndicators.add(ob0);
      if (! ob1.isEmpty()) overlapIndicators.add(ob1);

      if (! ob0.isEmpty() && ! ob1.isEmpty()) {
        // create indicator showing size of maximum overlap
        // assert: ind0 and ind are not null
        VertexHausdorffDistance hDist = new VertexHausdorffDistance(ob0, ob1);
        Geometry overlapSizeInd = factory.createLineString(hDist.getCoordinates());
        overlapSizeIndicators.add(overlapSizeInd);
      }
      else {
/*
System.out.println("Empty overlapping boundary found");
System.out.println(f.getGeometry());
System.out.println(closeF.getGeometry());
      */
      }
    }
    catch (Exception ex) {
      // can't compute geometry - don't do anything
      // print out geometries for debugging purposes
      System.out.println("ERROR - JTS failure in InternalOverlapFinder#createIndicators");
      System.out.println(ex.getMessage());
      System.out.println(g0);
      System.out.println(g1);
    }
  }

}
