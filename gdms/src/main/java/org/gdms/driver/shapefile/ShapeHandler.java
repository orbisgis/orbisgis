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

import com.vividsolutions.jts.geom.Geometry;

/** A ShapeHandler defines what is needed to construct and persist geometries
 * based upon the shapefile specification.
 * @author aaime
 * @author Ian Schneider
 * @source $URL: http://svn.geotools.org/geotools/tags/2.3.1/plugin/shapefile/src/org/geotools/data/shapefile/shp/ShapeHandler.java $
 *
 */
public interface ShapeHandler {
  /** Get the ShapeType of this handler.
   * @return The ShapeType.
   */
  public ShapeType getShapeType();

  /** Read a geometry from the ByteBuffer. The buffer's position, byteOrder, and limit
   * are set to that which is needed. The record has been read as well as the shape
   * type integer. The handler need not worry about reading unused information as
   * the ShapefileReader will correctly adjust the buffer position after this call.
   * @param buffer The ByteBuffer to read from.
   * @return A geometry object.
 * @throws IOException
   */
  public Geometry read(ReadBufferManager buffer,ShapeType type) throws IOException;

  /** Write the geometry into the ByteBuffer. The position, byteOrder, and limit are
   * all set. The handler is not responsible for writing the record or
   * shape type integer.
   * @param shapeBuffer The ByteBuffer to write to.
   * @param geometry The geometry to write.
   */
  public void write(WriteBufferManager shapeBuffer, Object geometry) throws IOException;

  /** Get the length of the given geometry Object in <b>bytes</b> not 16-bit words.
   * This is easier to keep track of, since the ByteBuffer deals with bytes. <b>Do
   * not include the 8 bytes of record.</b>
   * @param geometry The geometry to analyze.
   * @return The number of <b>bytes</b> the shape will take up.
   */
  public int getLength(Object geometry);
}
