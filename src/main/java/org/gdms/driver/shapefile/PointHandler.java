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

import org.gdms.driver.ReadBufferManager;
import org.gdms.driver.WriteBufferManager;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;


/**
 * Wrapper for a Shapefile point.
 *
 * @author aaime
 * @author Ian Schneider
 * @source $URL: http://svn.geotools.org/geotools/tags/2.3.1/plugin/shapefile/src/org/geotools/data/shapefile/shp/PointHandler.java $
 *
 */
public class PointHandler implements ShapeHandler {

  final ShapeType shapeType;
  GeometryFactory geometryFactory = new GeometryFactory();

  public PointHandler(ShapeType type) throws ShapefileException {
    if ((type != ShapeType.POINT) && (type != ShapeType.POINTM) && (type != ShapeType.POINTZ)) { // 2d, 2d+m, 3d+m
      throw new ShapefileException(
      "PointHandler constructor: expected a type of 1, 11 or 21");
    }

    shapeType = type;
  }

  public PointHandler() {
    shapeType = ShapeType.POINT; //2d
  }

  /**
   * Returns the shapefile shape type value for a point
   * @return int Shapefile.POINT
   */
  public ShapeType getShapeType() {
    return shapeType;
  }


  public int getLength(Object geometry) {
    int length;
    if (shapeType == ShapeType.POINT) {
      length = 20;
    } else if (shapeType == ShapeType.POINTM) {
      length = 28;
    } else if (shapeType == ShapeType.POINTZ) {
      length = 36;
    } else {
      throw new IllegalStateException("Expected ShapeType of Point, got" + shapeType);
    }
    return length;
  }

  public Geometry read(ReadBufferManager buffer, ShapeType type) throws IOException {
    if (type == ShapeType.NULL) {
      return null;
    }

    double x = buffer.getDouble();
    double y = buffer.getDouble();
    double z = Double.NaN;

    if (shapeType == ShapeType.POINTM) {
      buffer.getDouble();
    }

    if (shapeType == ShapeType.POINTZ) {
      z = buffer.getDouble();
    }

    return geometryFactory.createPoint(new Coordinate(x, y, z));
  }

  public void write(WriteBufferManager buffer, Object geometry) throws IOException {
    Coordinate c = ((Point) geometry).getCoordinate();

    buffer.putDouble(c.x);
    buffer.putDouble(c.y);

    if (shapeType == ShapeType.POINTZ) {
      if (Double.isNaN(c.z)) { // nan means not defined
        buffer.putDouble(0.0);
      } else {
        buffer.putDouble(c.z);
      }
    }

    if ((shapeType == ShapeType.POINTZ) || (shapeType == ShapeType.POINTM)) {
      buffer.putDouble(-10E40); //M
    }
  }

}
