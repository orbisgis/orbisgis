/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2002-2006, Geotools Project Managment Committee (PMC)
 *    (C) 2002, Centre for Computational Geography
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.gdms.driver.shapefile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.gdms.driver.ReadBufferManager;
import org.gdms.driver.WriteBufferManager;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;



/**
 * Wrapper for a Shapefile polygon.
 * @author aaime
 * @author Ian Schneider
 * @source $URL: http://svn.geotools.org/geotools/tags/2.3.1/plugin/shapefile/src/org/geotools/data/shapefile/shp/PolygonHandler.java $
 * @version $Id: PolygonHandler.java 22264 2006-10-19 10:10:35Z acuster $
 */
public class PolygonHandler implements ShapeHandler {
  GeometryFactory geometryFactory = new GeometryFactory();
  CGAlgorithms cga = new CGAlgorithms();

  final ShapeType shapeType;


  public PolygonHandler() {
    shapeType = ShapeType.POLYGON;
  }

  public PolygonHandler(ShapeType type) throws ShapefileException {
    if ((type != ShapeType.POLYGON) && (type != ShapeType.POLYGONM) && (type != ShapeType.POLYGONZ)) {
      throw new ShapefileException(
      "PolygonHandler constructor - expected type to be 5, 15, or 25.");
    }

    shapeType = type;
  }

  //returns true if testPoint is a point in the pointList list.
  boolean pointInList(Coordinate testPoint, Coordinate[] pointList) {
    Coordinate p;

    for (int t = pointList.length - 1; t >= 0; t--) {
      p = pointList[t];

      if ((testPoint.x == p.x) && (testPoint.y == p.y) &&
      ((testPoint.z == p.z) || (!(testPoint.z == testPoint.z))) //nan test; x!=x iff x is nan
      ) {
        return true;
      }
    }

    return false;
  }


  public ShapeType getShapeType() {
    return shapeType;
  }

  public int getLength(Object geometry) {
    MultiPolygon multi;

    if (geometry instanceof MultiPolygon) {
      multi = (MultiPolygon) geometry;
    } else {
      multi = geometryFactory.createMultiPolygon(new Polygon[] { (Polygon) geometry });
    }

    int nrings = 0;

    for (int t = 0; t < multi.getNumGeometries(); t++) {
      Polygon p;
      p = (Polygon) multi.getGeometryN(t);
      nrings = nrings + 1 + p.getNumInteriorRing();
    }

    int npoints = multi.getNumPoints();
    int length;

    if (shapeType == ShapeType.POLYGONZ) {
      length = 44 + (4 * nrings) + (16 * npoints) + (8 * npoints) + 16 + (8 * npoints) + 16;
    } else if (shapeType == ShapeType.POLYGONM) {
      length = 44 + (4 * nrings) + (16 * npoints) + (8 * npoints) + 16;
    } else if (shapeType == ShapeType.POLYGON) {
      length = 44 + (4 * nrings) + (16 * npoints);
    } else {
      throw new IllegalStateException("Expected ShapeType of Polygon, got " + shapeType);
    }
    return length;
  }



  public Geometry read(ReadBufferManager buffer, ShapeType type) throws IOException {
    if (type == ShapeType.NULL) {
      return null;
    }
    //bounds
    buffer.skip(4 * 8);

    int[] partOffsets;

    int numParts = buffer.getInt();
    int numPoints = buffer.getInt();

    partOffsets = new int[numParts];

    for (int i = 0; i < numParts; i++) {
      partOffsets[i] = buffer.getInt();
    }

    ArrayList<LinearRing> shells = new ArrayList<LinearRing>();
    ArrayList<LinearRing> holes = new ArrayList<LinearRing>();
    Coordinate[] coords = readCoordinates(buffer, numPoints);

    if (shapeType == ShapeType.POLYGONZ) {
      //z
      buffer.skip(2 * 8);

      for (int t = 0; t < numPoints; t++) {
        coords[t].z = buffer.getDouble();
      }
    }

    int offset = 0;
    int start;
    int finish;
    int length;

    for (int part = 0; part < numParts; part++) {
      start = partOffsets[part];

      if (part == (numParts - 1)) {
        finish = numPoints;
      } else {
        finish = partOffsets[part + 1];
      }

      length = finish - start;

      // Use the progressive CCW algorithm.
      // basically the area algorithm for polygons
      // which also tells us vertex order based upon the
      // sign of the area.
      Coordinate[] points = new Coordinate[length];
      //double area = 0;
      //int sx = offset;
      for (int i = 0; i < length; i++) {
        points[i] = coords[offset++];
        //int j = sx + (i + 1) % length;
        //area += points[i].x * coords[j].y;
        //area -= points[i].y * coords[j].x;
      }
      //area = -area / 2;
      //REVISIT: polyons with only 1 or 2 points are not polygons - geometryFactory will bomb so we skip if we find one.
      if(points.length == 0 || points.length > 3){
          LinearRing ring = geometryFactory.createLinearRing(points);

          if (CGAlgorithms.isCCW(points)) {
            // counter-clockwise
            holes.add(ring);
          } else {
            // clockwise
            shells.add(ring);
          }
      }
    }

    // quick optimization: if there's only one shell no need to check
    // for holes inclusion
    if(shells.size() == 1) {
      return createMulti( (LinearRing) shells.get(0) , holes );
    }
    // if for some reason, there is only one hole, we just reverse it and carry on.
    else if (holes.size() == 1 && shells.size() == 0) {
      Logger.getLogger("org.geotools.data.shapefile").warning(
      "only one hole in this polygon record");
      return createMulti(
        JTSUtilities.reverseRing( (LinearRing) holes.get(0) )
      );
    } else {

      // build an association between shells and holes
      final ArrayList<ArrayList<LinearRing>> holesForShells = assignHolesToShells(shells, holes);

      Geometry g = buildGeometries(shells, holes, holesForShells);

      return g;
    }
  }

  /**
   * @param buffer
   * @param numPoints
 * @throws IOException
   */
  private Coordinate[] readCoordinates(final ReadBufferManager buffer, final int numPoints) throws IOException {
      Coordinate[] coords = new Coordinate[numPoints];

      for (int t = 0; t < numPoints; t++) {
        coords[t] = new Coordinate(buffer.getDouble(), buffer.getDouble());
      }

      return coords;
  }

  /**
   * @param shells
   * @param holes
   * @param holesForShells
   */
  private Geometry buildGeometries(final List<LinearRing> shells, final List<LinearRing> holes, final List<ArrayList<LinearRing>> holesForShells) {
      Polygon[] polygons;

      // if we have shells, lets use them
      if (shells.size() > 0) {
        polygons = new Polygon[shells.size()];
      // oh, this is a bad record with only holes
      } else {
        polygons = new Polygon[holes.size()];
      }

      // this will do nothing for the "only holes case"
      for (int i = 0; i < shells.size(); i++) {
        polygons[i] = geometryFactory.createPolygon((LinearRing) shells.get(i),
        (LinearRing[]) ((ArrayList<LinearRing>) holesForShells.get(i)).toArray(new LinearRing[0]));
      }

      // this will take care of the "only holes case"
      // we just reverse each hole
      if (shells.size() == 0) {
        for (int i = 0, ii = holes.size(); i < ii; i++) {
          LinearRing hole = (LinearRing) holes.get(i);
          polygons[i] = geometryFactory.createPolygon(
            JTSUtilities.reverseRing(hole),
            new LinearRing[0]
          );
        }
      }

      Geometry g = geometryFactory.createMultiPolygon(polygons);

      return g;
  }

  /**
   * <b>Package private for testing</b>
   * @param shells
   * @param holes
   */
  ArrayList<ArrayList<LinearRing>> assignHolesToShells(final ArrayList<LinearRing> shells, final ArrayList<LinearRing> holes) {
	  ArrayList<ArrayList<LinearRing>> holesForShells = new ArrayList<ArrayList<LinearRing>>(shells.size());
      for (int i = 0; i < shells.size(); i++) {
        holesForShells.add(new ArrayList<LinearRing>());
      }

      //find homes
      for (int i = 0; i < holes.size(); i++) {
         LinearRing testRing = (LinearRing) holes.get(i);
        LinearRing minShell = null;
        Envelope minEnv = null;
        Envelope testEnv = testRing.getEnvelopeInternal();
        Coordinate testPt = testRing.getCoordinateN(0);
        LinearRing tryRing;


        for (int j = 0; j < shells.size(); j++) {
          tryRing = (LinearRing) shells.get(j);

          Envelope tryEnv = tryRing.getEnvelopeInternal();
          if (minShell != null) {
            minEnv = minShell.getEnvelopeInternal();
          }

          boolean isContained = false;
          Coordinate[] coordList = tryRing.getCoordinates();

          if ( tryEnv.contains(testEnv) &&
            (CGAlgorithms.isPointInRing(testPt, coordList) || (pointInList(testPt, coordList)))) {
            isContained = true;
          }

          // check if this new containing ring is smaller than the current minimum ring
          if (isContained) {
            if ((minShell == null) || minEnv.contains(tryEnv)) {
              minShell = tryRing;
            }
          }
        }

        if (minShell == null) {
          Logger.getLogger("org.geotools.data.shapefile").warning(
          "polygon found with a hole thats not inside a shell"
          );
          // now reverse this bad "hole" and turn it into a shell
          shells.add(JTSUtilities.reverseRing(testRing));
          holesForShells.add(new ArrayList<LinearRing>());
        } else {
          ((ArrayList<LinearRing>) holesForShells.get(shells.indexOf(minShell))).add(testRing);
        }
      }

      return holesForShells;
  }

  private MultiPolygon createMulti(LinearRing single) {
    return createMulti(single, new ArrayList<LinearRing>(0));
  }

  private MultiPolygon createMulti(LinearRing single,List<LinearRing> holes) {
    return geometryFactory.createMultiPolygon(
      new Polygon[] {
        geometryFactory.createPolygon(single, (LinearRing[]) holes.toArray(new LinearRing[holes.size()]))
      }
    );
  }

  public void write(WriteBufferManager buffer, Object geometry) throws IOException {
    MultiPolygon multi;

    if (geometry instanceof MultiPolygon) {
      multi = (MultiPolygon) geometry;
    } else {
      multi = geometryFactory.createMultiPolygon(new Polygon[] { (Polygon) geometry });
    }

    Envelope box = multi.getEnvelopeInternal();
    buffer.putDouble(box.getMinX());
    buffer.putDouble(box.getMinY());
    buffer.putDouble(box.getMaxX());
    buffer.putDouble(box.getMaxY());

    //need to find the total number of rings and points
    int nrings = 0;

    for (int t = 0; t < multi.getNumGeometries(); t++) {
      Polygon p;
      p = (Polygon) multi.getGeometryN(t);
      nrings = nrings + 1 + p.getNumInteriorRing();
    }

    int u = 0;
    int[] pointsPerRing = new int[nrings];

    for (int t = 0; t < multi.getNumGeometries(); t++) {
      Polygon p;
      p = (Polygon) multi.getGeometryN(t);
      pointsPerRing[u] = p.getExteriorRing().getNumPoints();
      u++;

      for (int v = 0; v < p.getNumInteriorRing(); v++) {
        pointsPerRing[u] = p.getInteriorRingN(v).getNumPoints();
        u++;
      }
    }

    int npoints = multi.getNumPoints();

    buffer.putInt(nrings);
    buffer.putInt(npoints);

    int count = 0;

    for (int t = 0; t < nrings; t++) {
      buffer.putInt(count);
      count = count + pointsPerRing[t];
    }

    //write out points here!
    Coordinate[] coords = multi.getCoordinates();

    for (int t = 0; t < coords.length; t++) {
      buffer.putDouble(coords[t].x);
      buffer.putDouble(coords[t].y);
    }

    if (shapeType == ShapeType.POLYGONZ) {
      //z
      double[] zExtreame = JTSUtilities.zMinMax(multi.getCoordinates());

      if (Double.isNaN(zExtreame[0])) {
        buffer.putDouble(0.0);
        buffer.putDouble(0.0);
      } else {
        buffer.putDouble(zExtreame[0]);
        buffer.putDouble(zExtreame[1]);
      }

      for (int t = 0; t < npoints; t++) {
        double z = coords[t].z;

        if (Double.isNaN(z)) {
          buffer.putDouble(0.0);
        } else {
          buffer.putDouble(z);
        }
      }
    }

    if (shapeType == ShapeType.POLYGONM || shapeType == ShapeType.POLYGONZ) {
      //m
      buffer.putDouble(-10E40);
      buffer.putDouble(-10E40);

      for (int t = 0; t < npoints; t++) {
        buffer.putDouble(-10E40);
      }
    }
  }





}


/*
 * $Log: PolygonHandler.java,v $
 * Revision 1.9  2004/02/17 18:10:23  ianschneider
 * changed to use GeometryFactory for Geometry creation
 *
 * Revision 1.8  2003/07/24 19:10:02  ianschneider
 * *** empty log message ***
 *
 * Revision 1.7  2003/07/24 18:32:10  ianschneider
 * more test updates, fixed Z type writing
 *
 * Revision 1.6  2003/07/23 23:41:09  ianschneider
 * more testing updates
 *
 * Revision 1.5  2003/07/23 00:59:59  ianschneider
 * Lots of PMD fix ups
 *
 * Revision 1.4  2003/07/21 21:15:29  jmacgill
 * small fix for shapefiles with an invalid hole (only 1 or 2 points)
 *
 * Revision 1.3  2003/05/19 21:38:55  jmacgill
 * refactored read method to break it up a little
 *
 * Revision 1.2  2003/05/19 20:51:30  ianschneider
 * removed System.out print statements
 *
 * Revision 1.1  2003/05/14 17:51:21  ianschneider
 * migrated packages
 *
 * Revision 1.3  2003/04/30 23:19:46  ianschneider
 * Added construction of multi geometries for default return values, even if only one geometry.
 * This could have effects through system.
 *
 * Revision 1.2  2003/03/30 20:21:09  ianschneider
 * Moved buffer branch to main
 *
 * Revision 1.1.2.5  2003/03/29 22:30:09  ianschneider
 * For case of hole without shell - reverse hole, add to shell list
 *
 * Revision 1.1.2.4  2003/03/26 19:30:30  ianschneider
 * Made hack to reverse polygon records if they contains only holes
 *
 * Revision 1.1.2.3  2003/03/12 15:30:18  ianschneider
 * made ShapeType final for handlers - once they're created, it won't change.
 *
 * Revision 1.1.2.2  2003/03/07 00:36:41  ianschneider
 *
 * Added back the additional ShapeType parameter in ShapeHandler.read. ShapeHandler's need
 * return their own special "null" shape if needed. Fixed the ShapefileReader to not throw
 * exceptions for "null" shapes. Fixed ShapefileReader to accomodate junk after the last valid
 * record. The theory goes, if the shape number is proper, that is, one greater than the
 * previous, we consider that a valid record and attempt to read it. I suppose, by chance, the
 * junk could coincide with the next record number. Stupid ESRI. Fixed some record-length
 * calculations which resulted in writing of bad shapefiles.
 *
 * Revision 1.1.2.1  2003/03/06 01:16:34  ianschneider
 *
 * The initial changes for moving to java.nio. Added some documentation and improved
 * exception handling. Works for reading, may work for writing as of now.
 *
 * Revision 1.1  2003/02/27 22:35:50  aaime
 * New shapefile module, initial commit
 *
 * Revision 1.2  2003/01/22 18:31:05  jaquino
 * Enh: Make About Box configurable
 *
 * Revision 1.2  2002/09/09 20:46:22  dblasby
 * Removed LEDatastream refs and replaced with EndianData[in/out]putstream
 *
 * Revision 1.1  2002/08/27 21:04:58  dblasby
 * orginal
 *
 * Revision 1.3  2002/03/05 10:51:01  andyt
 * removed use of factory from write method
 *
 * Revision 1.2  2002/03/05 10:23:59  jmacgill
 * made sure geometries were created using the factory methods
 *
 * Revision 1.1  2002/02/28 00:38:50  jmacgill
 * Renamed files to more intuitve names
 *
 * Revision 1.4  2002/02/13 00:23:53  jmacgill
 * First semi working JTS version of Shapefile code
 *
 * Revision 1.3  2002/02/11 18:44:22  jmacgill
 * replaced geometry constructions with calls to geometryFactory.createX methods
 *
 * Revision 1.2  2002/02/11 18:28:41  jmacgill
 * rewrote to have static read and write methods
 *
 * Revision 1.1  2002/02/11 16:54:43  jmacgill
 * added shapefile code and directories
 *
 */
