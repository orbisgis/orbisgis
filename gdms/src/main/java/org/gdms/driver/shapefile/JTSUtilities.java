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
 *    (C) 2003-2006, Geotools Project Managment Committee (PMC)
 *    (C) 2003, Centre for Computational Geography
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
 *
 *    Created on March 5, 2003, 11:18 AM
 */
package org.gdms.driver.shapefile;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/** A collection of utility methods for use with JTS and the shapefile package.
 * @author aaime
 * @author Ian Schneider
 * @source $URL: http://svn.geotools.org/geotools/tags/2.3.1/plugin/shapefile/src/org/geotools/data/shapefile/shp/JTSUtilities.java $
 */
public class JTSUtilities {

  static final CGAlgorithms cga = new CGAlgorithms();
  static final GeometryFactory factory = new GeometryFactory();

  private JTSUtilities() {
  }

  /** Determine the min and max "z" values in an array of Coordinates.
   * @param cs The array to search.
   * @return An array of size 2, index 0 is min, index 1 is max.
   */
  public static final double[] zMinMax(final Coordinate[] cs) {
    double zmin;
    double zmax;
    boolean validZFound = false;
    double[] result = new double[2];

    zmin = Double.NaN;
    zmax = Double.NaN;

    double z;

    for (int t = cs.length - 1; t >= 0; t--) {
      z = cs[t].z;

      if (!(Double.isNaN(z))) {
        if (validZFound) {
          if (z < zmin) {
            zmin = z;
          }

          if (z > zmax) {
            zmax = z;
          }
        } else {
          validZFound = true;
          zmin = z;
          zmax = z;
        }
      }
    }

    result[0] = (zmin);
    result[1] = (zmax);


    return result;
  }

  /** Determine the best ShapeType for a given Geometry.
   * @param geom The Geometry to analyze.
   * @return The best ShapeType for the Geometry.
   */
  public static final ShapeType findBestGeometryType(Geometry geom) {
    ShapeType type = ShapeType.UNDEFINED;

    if(geom instanceof Point) {
      type = ShapeType.POINT;
    } else if(geom instanceof MultiPoint) {
      type = ShapeType.MULTIPOINT;
    } else if(geom instanceof Polygon) {
      type = ShapeType.POLYGON;
    } else if(geom instanceof MultiPolygon) {
      type = ShapeType.POLYGON;
    } else if(geom instanceof LineString) {
      type = ShapeType.ARC;
    } else if(geom instanceof MultiLineString) {
      type = ShapeType.ARC;
    }
    return type;
  }

  /** Does what it says, reverses the order of the Coordinates in the ring.
   * @param lr The ring to reverse.
   * @return A new ring with the reversed Coordinates.
   */
  public static final LinearRing reverseRing(LinearRing lr) {
    int numPoints = lr.getNumPoints() - 1;
    Coordinate[] newCoords = new Coordinate[numPoints + 1];

    for(int t = numPoints; t >= 0; t--) {
      newCoords[t] = lr.getCoordinateN(numPoints - t);
    }

    return factory.createLinearRing(newCoords);
  }

  /** Create a nice Polygon from the given Polygon. Will ensure that shells are
   * clockwise and holes are counter-clockwise. Capiche?
   * @param p The Polygon to make "nice".
   * @return The "nice" Polygon.
   */
  public static final Polygon makeGoodShapePolygon(Polygon p) {
    LinearRing outer;
    LinearRing[] holes = new LinearRing[p.getNumInteriorRing()];
    Coordinate[] coords;

    coords = p.getExteriorRing().getCoordinates();

    if(CGAlgorithms.isCCW(coords)) {
      outer = reverseRing((LinearRing) p.getExteriorRing());
    } else {
      outer = (LinearRing) p.getExteriorRing();
    }

    for(int t = 0,tt = p.getNumInteriorRing(); t < tt; t++) {
      coords = p.getInteriorRingN(t).getCoordinates();

      if(!(CGAlgorithms.isCCW(coords))) {
        holes[t] = reverseRing((LinearRing) p.getInteriorRingN(t));
      } else {
        holes[t] = (LinearRing) p.getInteriorRingN(t);
      }
    }

    return factory.createPolygon(outer, holes);
  }

  /** Like makeGoodShapePolygon, but applied towards a multi polygon.
   * @param mp The MultiPolygon to "niceify".
   * @return The "nicified" MultiPolygon.
   */
  public static final MultiPolygon makeGoodShapeMultiPolygon(MultiPolygon mp) {
    MultiPolygon result;
    Polygon[] ps = new Polygon[mp.getNumGeometries()];

    //check each sub-polygon
    for(int t = 0; t < mp.getNumGeometries(); t++) {
      ps[t] = makeGoodShapePolygon((Polygon) mp.getGeometryN(t));
    }

    result = factory.createMultiPolygon(ps);

    return result;
  }

  /** Returns: <br>
   * 2 for 2d (default) <br>
   * 4 for 3d  - one of the oordinates has a non-NaN z value <br>
   * (3 is for x,y,m but thats not supported yet) <br>
   * @param cs The array of Coordinates to search.
   * @return The dimension.
   */
  public static final int guessCoorinateDims(final Coordinate[] cs) {
    int dims = 2;

    for(int t = cs.length - 1; t >= 0; t--) {
      if(!(Double.isNaN(cs[t].z))) {
        dims = 4;
        break;
      }
    }

    return dims;
  }

  public static Geometry convertToCollection(Geometry geom,ShapeType type) {
      Geometry retVal = null;

      if (type.isPointType()) {
        if((geom instanceof Point)) {
          retVal = geom;
        } else {
          Point[] pNull = null;
          retVal = factory.createMultiPoint(pNull);
        }
      } else if (type.isLineType()) {
        if((geom instanceof LineString)) {
          retVal = factory.createMultiLineString(new LineString[] {(LineString) geom});
        } else if(geom instanceof MultiLineString) {
          retVal = geom;
        } else {
          retVal = factory.createMultiLineString(null);
        }
      } else if (type.isPolygonType()) {
        if(geom instanceof Polygon) {
          Polygon p = makeGoodShapePolygon( (Polygon) geom);
          retVal = factory.createMultiPolygon(new Polygon[] {p});
        } else if(geom instanceof MultiPolygon) {
          retVal = JTSUtilities.makeGoodShapeMultiPolygon((MultiPolygon) geom);
        } else {
          retVal = factory.createMultiPolygon(null);
        }
      }  else if (type.isMultiPointType()) {
        if((geom instanceof Point)) {
          retVal = factory.createMultiPoint(new Point[] { (Point) geom});
        } else if(geom instanceof MultiPoint) {
          retVal = geom;
        } else {
          Point[] pNull = null;
          retVal = factory.createMultiPoint(pNull);
        }
      } else throw new RuntimeException("Could not convert " + geom.getClass() + " to " + type);

      return retVal;
  }

  /** Determine the best ShapeType for a geometry with the given dimension.
   * @param geom The Geometry to examine.
   * @param shapeFileDimentions The dimension 2,3 or 4.
   * @throws ShapefileException If theres a problem, like a bogus Geometry.
   * @return The best ShapeType.
   */
  public static final ShapeType getShapeType(Geometry geom, int shapeFileDimentions)
  throws ShapefileException {

    ShapeType type = null;

    if (geom instanceof Point) {
      switch (shapeFileDimentions) {
        case 2:
          type = ShapeType.POINT;
          break;
        case 3:
          type = ShapeType.POINTM;
          break;
        case 4:
          type = ShapeType.POINTZ;
          break;
        default:
          throw new ShapefileException("Too many dimensions for shapefile : " + shapeFileDimentions);
      }
    } else if (geom instanceof MultiPoint) {
      switch (shapeFileDimentions) {
        case 2:
          type = ShapeType.MULTIPOINT;
          break;
        case 3:
          type = ShapeType.MULTIPOINTM;
          break;
        case 4:
          type = ShapeType.MULTIPOINTZ;
          break;
        default:
          throw new ShapefileException("Too many dimensions for shapefile : " + shapeFileDimentions);
      }
    } else if ((geom instanceof Polygon) || (geom instanceof MultiPolygon)) {
      switch (shapeFileDimentions) {
        case 2:
          type = ShapeType.POLYGON;
          break;
        case 3:
          type = ShapeType.POLYGONM;
          break;
        case 4:
          type = ShapeType.POLYGONZ;
          break;
        default:
          throw new ShapefileException("Too many dimensions for shapefile : " + shapeFileDimentions);
      }
    } else if ((geom instanceof LineString) || (geom instanceof MultiLineString)) {
      switch (shapeFileDimentions) {
        case 2:
          type = ShapeType.ARC;
          break;
        case 3:
          type = ShapeType.ARCM;
          break;
        case 4:
          type = ShapeType.ARCZ;
          break;
        default:
          throw new ShapefileException("Too many dimensions for shapefile : " + shapeFileDimentions);
      }
    }

    if (type == null) {
      throw new ShapefileException("Cannot handle geometry type : " + (geom == null ? "null" : geom.getClass().getName()));
    }
    return type;
  }

}
