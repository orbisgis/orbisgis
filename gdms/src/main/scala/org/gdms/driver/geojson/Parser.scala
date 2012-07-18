/**
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

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.GeometryCollection
import com.vividsolutions.jts.geom.GeometryFactory
import com.vividsolutions.jts.geom.LineString
import com.vividsolutions.jts.geom.MultiLineString
import com.vividsolutions.jts.geom.MultiPoint
import com.vividsolutions.jts.geom.MultiPolygon
import com.vividsolutions.jts.geom.Point
import com.vividsolutions.jts.geom.Polygon
import org.gdms.data.schema.DefaultMetadata
import org.gdms.data.schema.Metadata
import org.gdms.data.types.Type
import org.gdms.data.types.Type._
import org.gdms.data.types.TypeFactory
import org.gdms.data.values.GeometryValue
import org.gdms.data.values.Value
import org.gdms.data.values.ValueFactory.{createValue => value, createNullValue => nullV}
import org.gdms.driver.DriverException
import scala.collection.immutable.SortedMap

/**
 * A simple geo-json parser.
 * 
 * This parser is completely stream-based and does not keep in memory more than a single feature
 * (i.e. an array of Value object).
 * 
 * @see http://www.geojson.org/geojson-spec.html
 * @author Antoine Gourlay
 */
trait Parser {
  
  // internal geometry factory
  private val gf = new GeometryFactory
  
  /**
   * Parses a GeoJson input and writes it.
   * 
   * @param rw a consumer of rows to be provided with the elements found in the input
   * @param jp a parser (expected to be at the very beginning of a valid geojson top-level element)
   */
  def parse(rw: Array[Value] => Unit, m: Metadata)(implicit jp: JsonParser) {
    next // start of object
    
    next // field_name (type)
    next // value_string
    val geomType = jp.getText
    
    next // field_name
    val firstParam = jp.getText
    
    next // value of field (start object / start array)
      
    geomType match {
      case "FeatureCollection" =>
        featureCollection(firstParam, m) map (_.toArray) foreach (rw)
      case "Feature" => 
        // all columns (but without the_geom first)
        val vals = SortedMap(feature(firstParam): _*)
        // reorder to get the_geom first
        val orderedvals = vals.get("the_geom") ++ (vals - "the_geom").values
        
        // write in this order
        rw(orderedvals.toArray)
      case "Point" => rw(Array(value(point)))
      case "LineString" => rw(Array(value(lineString)))
      case "Polygon" => rw(Array(value(polygon)))
      case "MultiPoint" => rw(Array(value(multiPoint)))
      case "MultiLineString" => rw(Array(value(multiLineString)))
      case "MultiPolygon" => rw(Array(value(multiPolygon)))
      case "GeometryCollection" => rw(Array(value(geometryCollection)))
    }
  }
  
  /**
   * Parses the metadata for a GeoJson input.
   * 
   * @param jp a parser (expected to be at the very beginning of a valid geojson top-level element)
   * @return the Gdms metadata of this geojson
   */
  def parseMetadata(implicit jp: JsonParser): Metadata = {
    
    next // start of object
    
    val met = new DefaultMetadata
    
    next // field_name (type)
    next // value_string
    val geomType = jp.getText
    
    next // field_name
    val firstParam = jp.getText
    
    next // value of field (start object / start array)
    
    geomType match {
      case "FeatureCollection" => 
        val m = featureCollectionMetadata(firstParam)
        buildMetadata(m, met)
      case "Feature" => 
        val vals = SortedMap(featureMetadata(firstParam): _*)
        buildMetadata(vals, met)
      case "Point" => 
        met.addField("the_geom", POINT)
      case "LineString" => 
        met.addField("the_geom", LINESTRING)
      case "Polygon" => 
        met.addField("the_geom", POLYGON)
      case "MultiPoint" => 
        met.addField("the_geom", MULTIPOINT)
      case "MultiLineString" => 
        met.addField("the_geom", MULTILINESTRING)
      case "MultiPolygon" => 
        met.addField("the_geom", MULTIPOLYGON)
      case "GeometryCollection" => 
        met.addField("the_geom", GEOMETRYCOLLECTION)
    }
    
    met
  }
  
  /**
   * Utility method: cleans up and build a Gdms metadata object from a Map.
   */
  private def buildMetadata(vals: Map[String, Int], met: DefaultMetadata) {
    // change NULL type to String type (with NULL values)
    val cvals = vals map { 
      case (n, v) if v == NULL => (n, STRING)
      case a => a
    }
    
    // reorder to get 'the_geom' first
    met.addField("the_geom", cvals.head._2)
    cvals.tail foreach { case (n, v) => met.addField(n, v) }
    
  }
    
  /**
   * Utility method: iterates over the parser itself until a specific token is reached.
   */
  private def until(end: JsonToken)(implicit jp: JsonParser) = {
    def next0: Stream[JsonParser] = {
      if (jp.getCurrentToken == end) {
        Stream.empty
      } else {
        Stream.cons(jp, next0)
      }
    }
    
    next0
  }
  
  /**
   * Utility method: move on token ahead.
   * 
   * This is just here for clarity.
   */
  private def next(implicit jp: JsonParser) = jp.nextToken
  
  /**
   * Parses a coordinate array [X Y Z].
   * 
   * @return a JTS Coordinate
   */
  private[geojson] def coord(implicit jp: JsonParser): Coordinate = {
      
    next // first value
    val x = jp.getDoubleValue
    
    next // second value
    val y = jp.getDoubleValue
    
    val coord = next match { // third value or exit array
      case JsonToken.END_ARRAY => new Coordinate(x, y) // no z
      case _ => 
        val z = jp.getDoubleValue
        next // exit array
        new Coordinate(x, y, z)
    }
    
    next // move to next
    
    coord
  }
  
    
  /**
   * Parses a Point.
   * 
   * @return a JTS Point
   */
  private def point(implicit jp: JsonParser): Point = gf.createPoint(coord(jp))
    
  /**
   * Parses a MultiPoint.
   * 
   * @return a JTS MultiPoint
   */
  private def multiPoint(implicit jp: JsonParser): MultiPoint = {
    
    next // enter outer array
    gf.createMultiPoint(until(JsonToken.END_ARRAY).map(coord(_)).toArray)
  }
  
  /**
   * Parses a LineString.
   * 
   * @return a JTS LineString
   */
  private def lineString(implicit jp: JsonParser): LineString = {
        
    next // enter outer array
    gf.createLineString(until(JsonToken.END_ARRAY).map(coord(_)).toArray)
  }
  
  /**
   * Parses a MultiLineString.
   * 
   * @return a JTS MultiLineString
   */
  private def multiLineString(implicit jp: JsonParser): MultiLineString = {
     
    next // enter outer array
    val lns = until(JsonToken.END_ARRAY).map{ _ =>
      val li = lineString(jp)
      next // next linestring
      li
    }
    
    gf.createMultiLineString(lns.toArray)
  }
  
  /**
   * Parses a Polygon.
   * 
   * @return a JTS Polygon
   */
  private def polygon(implicit jp: JsonParser): Polygon = {
      
    next // enter outer array
    val rings = until(JsonToken.END_ARRAY).map { _ =>
      next // enter inner array
      val lr = gf.createLinearRing(until(JsonToken.END_ARRAY).map(coord(_)).toArray)
      next // next ring
      lr
    }
    
    // head is outer ring
    // tail is inner rings
    gf.createPolygon(rings.head, rings.tail.toArray)
  }
  
  /**
   * Parses a MultiPolygon.
   * 
   * @return a JTS MultiPolygon
   */
  private def multiPolygon(implicit jp: JsonParser): MultiPolygon = {
      
    next // enter outer array
    val ps = until(JsonToken.END_ARRAY).map{ _ =>
      val p = polygon(jp)
      next // next polygon
      p
    }
    
    gf.createMultiPolygon(ps.toArray)
  }
  
  
  /**
   * Parses a geometry that is part of a feature.
   * 
   * @return a Gdms GeometryValue
   */
  private def geometry(implicit jp: JsonParser): GeometryValue = {
      
    next // field_name (type)
    next // value_string
    val geomType = jp.getText
    
    next // field_name
    val firstParam = jp.getText
    
    next // value of field (start object / start array)
    
    geomType match {
      case "Point" => value(point(jp))
      case "LineString" => 
        val v = value(lineString)
        next // next value
        v
      case "Polygon" => 
        val v = value(polygon)
        next // next value
        v
      case "MultiPoint" => 
        val v = value(multiPoint)
        next // next value
        v
      case "MultiLineString" => 
        val v = value(multiLineString)
        next // next value
        v
      case "MultiPolygon" => 
        val v = value(multiPolygon)
        next // next value
        v
      case "GeometryCollection" => 
        val v = value(geometryCollection)
        next  // next value
        v
    }
  }
  
  /**
   * Parses the metadata of a geometry that is part of a feature.
   * 
   * This skips the content of the geometry, only parsing the type.
   * 
   * @return a Gdms type code for the geometry
   */
  private def geometryMetadata(implicit jp: JsonParser): Int = {
      
    next // field_name (type)
    next // value_string
    val geomType = jp.getText
    
    next // field_name    
    next // value of field (start object / start array)
    
    geomType match {
      case "Point" => 
        jp.skipChildren
        next // next value
        POINT
      case "LineString" => 
        jp.skipChildren
        next // next value
        LINESTRING
      case "Polygon" => 
        jp.skipChildren
        next // next value
        POLYGON
      case "MultiPoint" => 
        jp.skipChildren
        next // next value
        MULTIPOINT
      case "MultiLineString" => 
        jp.skipChildren
        next // next value
        MULTILINESTRING
      case "MultiPolygon" => 
        jp.skipChildren
        next // next value
        MULTIPOLYGON
      case "GeometryCollection" => 
        jp.skipChildren
        next  // next value
        GEOMETRYCOLLECTION
    }
  }
  
  /**
   * Parses the 'properties' part of a feature.
   * 
   * Supported Gdms types are:
   *  - StringValue
   *  - IntValue
   *  - DoubleValue
   *  - BooleanValue
   *  - NullValue
   *  
   *  @return a sequence of tuples (field name, value)
   */
  private def properties(implicit jp: JsonParser): Seq[(String, Value)] = {
      
    next // enter object
    until(JsonToken.END_OBJECT).map { _ =>
      val name = jp.getText
      
      val gVal: Value = next match {
        case JsonToken.VALUE_STRING => value(jp.getText)
        case JsonToken.VALUE_NUMBER_INT => value(jp.getIntValue)
        case JsonToken.VALUE_NUMBER_FLOAT => value(jp.getDoubleValue)
        case JsonToken.VALUE_NULL => nullV()
        case JsonToken.VALUE_TRUE => value(true)
        case JsonToken.VALUE_FALSE => value(false)
        case _ => nullV() // ignore any other unknown types
      }
      next // next param / end
      
      (name, gVal)
    }.force
  }
  
  /**
   * Parses the metadata of the 'properties' part of a feature.
   * 
   * Supported Gdms types are:
   *  - StringValue
   *  - IntValue
   *  - DoubleValue
   *  - BooleanValue
   *  - NullValue
   *  
   *  @return a sequence of tuples (field name, gdms type code)
   */
  private def propertiesMetadata(implicit jp: JsonParser): Seq[(String, Int)] = {
      
    next // enter object
    until(JsonToken.END_OBJECT).map { _ =>
      val name = jp.getText
      
      val gVal = next match {
        case JsonToken.VALUE_STRING => STRING
        case JsonToken.VALUE_NUMBER_INT => INT
        case JsonToken.VALUE_NUMBER_FLOAT => DOUBLE
        case JsonToken.VALUE_NULL => NULL
        case JsonToken.VALUE_TRUE => BOOLEAN
        case JsonToken.VALUE_FALSE => BOOLEAN
        case _ => NULL // ignore any other unknown types
      }
      next // next param / end
      
      (name, gVal)
    }.force
  }
  
  /**
   * Parses a feature.
   * 
   * @return a sequence of tuples (field name, value)
   */
  private def feature(firstParam: String)(implicit jp: JsonParser): Seq[(String, Value)] = {
    
    val first = firstParam match {
      case "geometry" => Seq(("the_geom", geometry))
      case "properties" => properties
    }
        
    next // field name
    val secondParam = jp.getText
    
    next // next element
    val second = secondParam match {
      case "geometry" => Seq(("the_geom", geometry))
      case "properties" => properties
    }
        
    next // exit object
        
    first ++ second
  }
  
  /**
   * Parses the metadata for a feature.
   * 
   * @return a map of (field name, gdms type code) for the current feature
   */
  private def featureMetadata(firstParam: String)(implicit jp: JsonParser): List[(String, Int)] = {
    def parse(param: String) = {
      param match {
        case "geometry" => List(("$", geometryMetadata))
        case "properties" => propertiesMetadata toList
      }}
    
    val first = parse(firstParam)
        
    next // field name
    val secondParam = jp.getText
    next // next element
        
    val second = parse(secondParam)
        
    next // exit object
        
    first ++ second
  }
  
  /**
   * Parses a feature collection.
   * 
   * @return a sequence of rows (each represented as a Seq[Value])
   */
  private def featureCollection(firstParam: String, met: Metadata)(implicit jp: JsonParser): Seq[Seq[Value]] = {
    
    next // enter object
    
    until(JsonToken.END_ARRAY).map { _ =>
      
      next // field_name (type) 
      val name = jp.getCurrentName
      
      next // value_string
      val geomType = jp.getText
    
      next // field_name
      val firstParam = jp.getText
    
      next // value of field (start object / start array)
    
      val row = geomType match {
        case "Feature" => 
          // input map of (field index, value)
          val cols = Map(feature(firstParam).map(a => (met.getFieldIndex(a._1), a._2)): _*)
          
          // add null values when a column is not specified
          (0 until met.getFieldCount) map { i =>
            cols.get(i) match {
              case None => nullV
              case Some(a) => a
            }
          }
        case a => throw new DriverException("Bim: " + a)
      }
      
      next // next feature
      
      row
    }
  }
  
  /**
   * Parses the metadata for a feature collection.
   * 
   * @return an ordered map of (field name, gdms type code)
   */
  private def featureCollectionMetadata(firstParam: String)(implicit jp: JsonParser): SortedMap[String, Int] = {
      
    // metadata
    var met = SortedMap[String, Int]()
    
    def add(m: SortedMap[String, Int]) {
      if (met.isEmpty) {
        // first feature ever
        met = m
      } else {
        // a new feature
        m foreach { case (k, v) =>
            met.get(k) match {
              case None => met = met + ((k, v))
              case Some(e) if e != v => met = met.updated(k, TypeFactory.getBroaderType(e, v))
              case _ =>
            }
        }
      }
    }
    
    next // enter object
    
    until(JsonToken.END_ARRAY).map { _ =>
      val name = jp.getCurrentName
      
      next // field_name (type)
      next // value_string
      val geomType = jp.getText
    
      next // field_name
      val firstParam = jp.getText
    
      next // value of field (start object / start array)
    
      geomType match {
        case "Feature" => add(SortedMap(featureMetadata(firstParam): _*))
        case a => throw new DriverException("Bim: " + a)
      }
      
      next // next feature
    }.force
    
    met
  }
  
  /**
   * Parses a geometry collection.
   * 
   * @return a JTS GeometryCollection
   */
  private def geometryCollection(implicit jp: JsonParser): GeometryCollection = {
    
    next // enter array
    val geoms = until(JsonToken.END_ARRAY).map { _ =>
      val g = geometry(jp).getAsGeometry
      next // next geometry
      g
    }
    
    gf.createGeometryCollection(geoms.toArray)
  }
}

