/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 * 
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
 * 
 * This file is part of Gdms.
 * 
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 * 
 * For more information, please consult: <http://www.orbisgis.org/>
 * 
 * or contact directly:
 * info@orbisgis.org
 */

package org.gdms.driver.geojson

import com.fasterxml.jackson.core.JsonGenerator
import com.vividsolutions.jts.geom._
import org.gdms.data.types.{Type, TypeFactory}
import org.gdms.data.values.Value
import org.gdms.driver.DriverException

/**
 * A simple geo-json writer.
 * 
 * This writer is completely stream-based and does not keep in memory more than a single feature
 * (i.e. an array of Value object) of the input table.
 * 
 * @see http://www.geojson.org/geojson-spec.html
 * @author Antoine Gourlay
 */
trait Writer {

  def write(g: JsonGenerator, it: Iterator[Array[Value]], props: Map[String, Int]) {
    // header
    g.writeStartObject
    g.writeStringField("type", "FeatureCollection")
    g.writeArrayFieldStart("features")
    
    val spatialIndex = props("the_geom")
    val others = props - "the_geom"
    
    it foreach { a => 
      // feature header
      g.writeStartObject
      g.writeStringField("type", "Feature")
      g.writeObjectFieldStart("geometry")
      writeGeometry(g, a(spatialIndex).getAsGeometry)
      g.writeEndObject
      
      g.writeObjectFieldStart("properties")
      others foreach {
        case (s, i) => writeValue(g, s, a(i))
      }
      
      // feature footer
      g.writeEndObject
      g.writeEndObject
    }
    
    // footer
    g.writeEndArray
    g.writeEndObject
    
    g.flush
    g.close
  }
  
  private def writeValue(g: JsonGenerator, name: String, v: Value) {
    if (v.isNull) g.writeNullField(name)
    else v.getType match {
      case Type.INT | Type.BYTE | Type.SHORT => g.writeNumberField(name, v.getAsInt)
      case Type.LONG => g.writeNumberField(name, v.getAsLong)
      case Type.DOUBLE => g.writeNumberField(name, v.getAsDouble)
      case Type.FLOAT => g.writeNumberField(name, v.getAsFloat)
      case Type.STRING => g.writeStringField(name, v.getAsString)
      case Type.BINARY => g.writeBinaryField(name, v.getAsBinary)
      case Type.BOOLEAN => g.writeBooleanField(name, v.getAsBoolean)
      case a => throw new DriverException("Unsupported type: " + TypeFactory.getTypeName(a))
    }
  }
  
  private def writeGeometry(g: JsonGenerator, a: Geometry) {
    // maybe we shoudn't depend on JTS's geometry types...?
    val typ = a.getGeometryType
    g.writeStringField("type", typ)
    
    // special case for GeometryCollection
    if (a.isInstanceOf[GeometryCollection]) {
      g.writeArrayFieldStart("geometries")
      writeGeometryCollection(g, a.asInstanceOf[GeometryCollection])
    } else {
      g.writeArrayFieldStart("coordinates")
      a match {
        case p :Point => writePoint(g, p)
        case p: LineString => writeLineString(g, p)
        case p: Polygon => writePolygon(g, p)
        case p: MultiPoint => writeMultiPoint(g, p)
        case p: MultiLineString => writeMultiLineString(g, p)
        case p: MultiPolygon => writeMultiPolygon(g, p)
        case a => throw new DriverException("Found unknown geometry type: " + a)
      }
    }
    g.writeEndArray
  }
  
  private def writePoint(g: JsonGenerator, p: Point) {
    val c = p.getCoordinate
    g.writeNumber(c.x)
    g.writeNumber(c.y)
    if (!c.z.isNaN) g.writeNumber(c.z)
  }
  
  private def writeMultiPoint(g: JsonGenerator, mp: MultiPoint) {
    (0 until mp.getNumGeometries) foreach { i=>
      g.writeStartArray
      // one more thing JTS forgets about: covariant return types
      // these casts should be unnecessary... :(
      writePoint(g, mp.getGeometryN(i).asInstanceOf[Point])
      g.writeEndArray
    }
  }
  
  private def writeLineString(g: JsonGenerator, li: LineString) {
    li.getCoordinates foreach (writeCoord(g, _))
  }
  
  private def writeMultiLineString(g: JsonGenerator, mli: MultiLineString) {
    (0 until mli.getNumGeometries) foreach { i=>
      g.writeStartArray
      writeLineString(g, mli.getGeometryN(i).asInstanceOf[LineString])
      g.writeEndArray
    }
  }
  
  private def writePolygon(g: JsonGenerator, p: Polygon) {
    g.writeStartArray
    writeLineString(g, p.getExteriorRing)
    g.writeEndArray
    (0 until p.getNumInteriorRing) foreach { i =>
      g.writeStartArray
      writeLineString(g, p.getInteriorRingN(i))
      g.writeEndArray
    }
  }
  
  private def writeMultiPolygon(g: JsonGenerator, mli: MultiPolygon) {
    (0 until mli.getNumGeometries) foreach { i=>
      g.writeStartArray
      writePolygon(g, mli.getGeometryN(i).asInstanceOf[Polygon])
      g.writeEndArray
    }
  }
  
  private def writeGeometryCollection(g: JsonGenerator, col: GeometryCollection) {
    (0 until col.getNumGeometries) foreach {i =>
      g.writeStartObject
      writeGeometry(g, col.getGeometryN(i))
      g.writeEndObject
    }
  }
  
  private def writeCoord(g: JsonGenerator, c: Coordinate) {
    g.writeStartArray
    g.writeNumber(c.x)
    g.writeNumber(c.y)
    if (!c.z.isNaN) g.writeNumber(c.z)
    g.writeEndArray
  }
}
