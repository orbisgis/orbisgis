package org.orbisgis.groovy_utils

import groovy.transform.Field
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.LinearRing
import org.locationtech.jts.geom.MultiLineString
import org.locationtech.jts.geom.MultiPoint
import org.locationtech.jts.geom.MultiPolygon
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.lang.reflect.Array

/**
 * Utility script used as extension module adding operator overloading for JTS Geometry.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */

private static final @Field GeometryFactory FACTORY = new GeometryFactory()
private static final @Field Logger LOGGER = LoggerFactory.getLogger(this.class)

//Plus overloading
/**
 * Add overloading for Point + Geometry.
 * If Geometry is a point, return a MultiPoint, otherwise return a GeometryCollection.
 * Example :
 *
 * Point + Point                = MultiPoint
 * Point + LineString           = GeometryCollection
 * Point + Polygon              = GeometryCollection
 * Point + MultiPoint           = GeometryCollection
 * Point + MultiLineString      = GeometryCollection
 * Point + MultiPolygon         = GeometryCollection
 * Point + GeometryCollection   = GeometryCollection
 *
 * @param self Targeted point.
 * @param geom Geometry to add to the point.
 * @return A MultiPoint if the Geometry is a Point, a GeometryCollection otherwise.
 */
static Geometry plus(Point self, Geometry geom) {
    if(geom instanceof Point) {
        return FACTORY.createMultiPoint(self, geom)
    }
    return FACTORY.createGeometryCollection(self, geom)
}

/**
 * Add overloading for LineString + Geometry.
 * If Geometry is a LineString, return a MultiLineString, otherwise return a GeometryCollection.
 * Example :
 *
 * LineString + Point               = GeometryCollection
 * LineString + LineString          = MultiLineString
 * LineString + Polygon             = GeometryCollection
 * LineString + MultiPoint          = GeometryCollection
 * LineString + MultiLineString     = GeometryCollection
 * LineString + MultiPolygon        = GeometryCollection
 * LineString + GeometryCollection  = GeometryCollection
 *
 * @param self Targeted LineString.
 * @param geom Geometry to add to the LineString.
 * @return A MultiLineString if the Geometry is a LineString, a GeometryCollection otherwise.
 */
static Geometry plus(LineString self, Geometry geom) {
    if(geom instanceof LineString) {
        return FACTORY.createMultiLineString(self, geom)
    }
    return FACTORY.createGeometryCollection(self, geom)
}

/**
 * Add overloading for Polygon + Geometry.
 * If Geometry is a Polygon, return a MultiPolygon, otherwise return a GeometryCollection.
 * Example :
 *
 * Polygon + Point               = GeometryCollection
 * Polygon + LineString          = GeometryCollection
 * Polygon + Polygon             = MultiPolygon
 * Polygon + MultiPoint          = GeometryCollection
 * Polygon + MultiLineString     = GeometryCollection
 * Polygon + MultiPolygon        = GeometryCollection
 * Polygon + GeometryCollection  = GeometryCollection
 *
 * @param self Targeted Polygon.
 * @param geom Geometry to add to the Polygon.
 * @return A MultiPolygon if the Geometry is a Polygon, a GeometryCollection otherwise.
 */
static Geometry plus(Polygon self, Geometry geom) {
    if(geom instanceof Polygon) {
        return FACTORY.createMultiPolygon(self, geom)
    }
    return FACTORY.createGeometryCollection(self, geom)
}

/**
 * Add overloading for MultiPoint + Geometry.
 * Example :
 *
 * MultiPoint + Point               = GeometryCollection
 * MultiPoint + LineString          = GeometryCollection
 * MultiPoint + Polygon             = GeometryCollection
 * MultiPoint + MultiPoint          = GeometryCollection
 * MultiPoint + MultiLineString     = GeometryCollection
 * MultiPoint + MultiPolygon        = GeometryCollection
 * MultiPoint + GeometryCollection  = GeometryCollection
 *
 * @param self Targeted MultiPoint.
 * @param geom Geometry to add to the MultiPoint.
 * @return A GeometryCollection.
 */
static Geometry plus(MultiPoint self, Geometry geom) {
    return FACTORY.createGeometryCollection(self, geom)
}

/**
 * Add overloading for MultiLineString + Geometry.
 * Example :
 *
 * MultiLineString + Point               = GeometryCollection
 * MultiLineString + LineString          = GeometryCollection
 * MultiLineString + Polygon             = GeometryCollection
 * MultiLineString + MultiPoint          = GeometryCollection
 * MultiLineString + MultiLineString     = GeometryCollection
 * MultiLineString + MultiPolygon        = GeometryCollection
 * MultiLineString + GeometryCollection  = GeometryCollection
 *
 * @param self Targeted MultiLineString.
 * @param geom Geometry to add to the MultiLineString.
 * @return A GeometryCollection.
 */
static Geometry plus(MultiLineString self, Geometry geom) {
    return FACTORY.createGeometryCollection(self, geom)
}

/**
 * Add overloading for MultiPolygon + Geometry.
 * Example :
 *
 * MultiPolygon + Point               = GeometryCollection
 * MultiPolygon + LineString          = GeometryCollection
 * MultiPolygon + Polygon             = GeometryCollection
 * MultiPolygon + MultiPoint          = GeometryCollection
 * MultiPolygon + MultiLineString     = GeometryCollection
 * MultiPolygon + MultiPolygon        = GeometryCollection
 * MultiPolygon + GeometryCollection  = GeometryCollection
 *
 * @param self Targeted MultiPolygon.
 * @param geom Geometry to add to the MultiPolygon.
 * @return A GeometryCollection.
 */
static Geometry plus(MultiPolygon self, Geometry geom) {
    return FACTORY.createGeometryCollection(self, geom)
}

//Minus overloading

/**
 * Add overloading for Polygon - Polygon.
 * Add the second Polygon as an hole of the first one.
 *
 * @param self Targeted Polygon.
 * @param hole Hole polygon.
 * @return The first Polygon with the second as hole.
 */
static Polygon minus(Polygon self, Polygon hole) {
    def shell = FACTORY.createLinearRing(self.exteriorRing.coordinates)
    LinearRing[] holes = []
    for (int i=0; i<self.getNumInteriorRing(); i++) {
        holes += FACTORY.createLinearRing(self.getInteriorRingN(i).coordinates)
    }
    holes += FACTORY.createLinearRing(hole.exteriorRing.coordinates)
    return FACTORY.createPolygon(shell, holes)
}

//LeftShift overloading

/**
 * Add overloading for Point << Point
 * Insert the second Point into the first Point in order to create a LineString.
 * @param self First Point of the future LineString.
 * @param pt Last Point of the LineString.
 * @return A LineString composed of the two points.
 */
static LineString leftShift(Point self, Point pt) {
    return FACTORY.createLineString(self.coordinate, pt.coordinate)
}

/**
 * Add overloading for LineString << LineString
 * Insert the second LineString at the end of the first LineString.
 * @param self First LineString of the future LineString.
 * @param pt Second LineString of the LineString.
 * @return A LineString composed of the two LineStrings.
 */
static LineString leftShift(LineString self, LineString lineString) {
    return FACTORY.createLineString(concatenate(self.coordinates, lineString.coordinates))
}

/**
 * Add overloading for LineString << Point
 * Insert the Point at the end of the LineString.
 * @param self LineString of the future LineString.
 * @param pt Point to add to the LineString.
 * @return A LineString extended with the given Point.
 */
static LineString leftShift(LineString self, Point pt) {
    return FACTORY.createLineString(concatenate(self.coordinates, pt.coordinate))
}

/**
 * Add overloading for Polygon << Point
 * Insert the Point at the end of the Polygon (Before looping with the first Point).
 * @param self Polygon of the future Polygon.
 * @param pt Point to add to the Polygon.
 * @return A Polygon extended with the given Point.
 */
static Polygon leftShift(Polygon self, Point pt) {
    Coordinate[] shellCoords = self.exteriorRing.coordinates
    LinearRing[] holes = []
    for (int i=0; i<self.getNumInteriorRing(); i++) {
        holes << FACTORY.createLinearRing(self.getInteriorRingN(i).coordinates)
    }
    def last = shellCoords.last()
    shellCoords = shellCoords[0..shellCoords.size()-2]
    shellCoords += pt.coordinate
    shellCoords += last
    return FACTORY.createPolygon(FACTORY.createLinearRing(shellCoords), holes)
}

/**
 * Add overloading for MultiPoint << Point
 * Insert the Point at the end of the MultiPoint.
 * @param self MultiPoint of the future MultiPoint.
 * @param pt Point to add to the MultiPoint.
 * @return A MultiPoint extended with the given Point.
 */
static MultiPoint leftShift(MultiPoint self, Point pt) {
    return FACTORY.createMultiPoint(
            Arrays.stream(concatenate(self.coordinates, pt.coordinates))
                    .map(it -> FACTORY.createPoint(it)).toArray() as Point[])
}

/**
 * Add overloading for MultiLineString << LineString
 * Insert the LineString at the end of the MultiLineString.
 * @param self LineString to add to the MultiLineString.
 * @param multi MultiLineString of the future MultiLineString.
 * @return A MultiLineString extended with the given LineString.
 */
static MultiLineString leftShift(MultiLineString self, LineString line) {
    LineString[] lineStrings = []
    for(int i=0; i<self.numGeometries; i++) {
        lineStrings += self.getGeometryN(i)
    }
    lineStrings += [line]
    return FACTORY.createMultiLineString(lineStrings)
}

/**
 * Add overloading for MultiPolygon << Polygon
 * Insert the Polygon at the en MultiPolygon.
 * @param self Polygon to add to the MultiPolygon.
 * @param multi MultiPolygon of the future MultiPolygon.
 * @return A MultiPolygon extended with the given Polygon.
 */
static MultiPolygon leftShift(MultiPolygon self, Polygon poly) {
    Polygon[] polygons = []
    for(int i=0; i<self.numGeometries; i++) {
        polygons += self.getGeometryN(i)
    }
    polygons += [poly]
    return FACTORY.createMultiPolygon(polygons)
}

//RightShift overloading

/**
 * Add overloading for Point >> Point
 * Insert the first Point at the end of the second Point to create a LineString.
 * @param self First Point of the future LineString.
 * @param pt Second Point of the future LineString.
 * @return A LineString composed of the two Point.
 */
static LineString rightShift(Point self, Point pt) {
    return FACTORY.createLineString(self.coordinate, pt.coordinate)
}

/**
 * Add overloading for Point >> LineString
 * Insert the Point at the end of the LineString.
 * @param self Point to add to the LineString.
 * @param pt LineString to extend.
 * @return A LineString extended with the given Point.
 */
static LineString rightShift(Point self, LineString lineString) {
    return FACTORY.createLineString(concatenate(self.coordinates, lineString.coordinates))
}

/**
 * Add overloading for Point >> Polygon
 * Insert the Point at the end of the Polygon.
 * @param self Point to add to the Polygon.
 * @param pt Polygon to extend.
 * @return A Polygon extended with the given Point.
 */
static Polygon rightShift(Point pt, Polygon polygon) {
    Coordinate[] shellCoords = polygon.exteriorRing.coordinates
    LinearRing[] holes = []
    for (int i=0; i<polygon.getNumInteriorRing(); i++) {
        holes << FACTORY.createLinearRing(polygon.getInteriorRingN(i).coordinates)
    }
    def last = shellCoords.last()
    shellCoords -= last
    shellCoords = pt.coordinates + [last] + shellCoords + pt.coordinates
    return FACTORY.createPolygon(FACTORY.createLinearRing(shellCoords), holes)
}

/**
 * Add overloading for Point >> MultiPoint
 * Insert the Point at the start of the MultiPoint.
 * @param self Point to add to the MultiPoint.
 * @param multi MultiPoint of the future MultiPoint.
 * @return A MultiPoint extended with the given Point.
 */
static MultiPoint rightShift(Point self, MultiPoint multi) {
    return FACTORY.createMultiPoint(
            Arrays.stream(concatenate(self.coordinates, multi.coordinates))
                    .map(it -> FACTORY.createPoint(it)).toArray() as Point[])
}

/**
 * Add overloading for LineString >> LineString
 * Insert the first LineString at the end of the second LineString to create a new LineString.
 * @param self First LineString of the future LineString.
 * @param pt Second LineString of the future LineString.
 * @return A LineString composed of the two LineString.
 */
static LineString rightShift(LineString self, LineString lineString) {
    return FACTORY.createLineString(concatenate(self.coordinates, lineString.coordinates))
}

/**
 * Add overloading for LineString >> MultiLineString
 * Insert the LineString at the start of the MultiLineString.
 * @param self LineString to add to the MultiLineString.
 * @param multi MultiLineString of the future MultiLineString.
 * @return A MultiLineString extended with the given LineString.
 */
static MultiLineString rightShift(LineString self, MultiLineString multi) {
    LineString[] lineStrings = [self]
    for(int i=0; i<multi.numGeometries; i++) {
        lineStrings += multi.getGeometryN(i)
    }
    return FACTORY.createMultiLineString(lineStrings)
}

/**
 * Add overloading for Polygon >> MultiPolygon
 * Insert the Polygon at the start MultiPolygon.
 * @param self Polygon to add to the MultiPolygon.
 * @param multi MultiPolygon of the future MultiPolygon.
 * @return A MultiPolygon extended with the given Polygon.
 */
static MultiPolygon rightShift(Polygon self, MultiPolygon multi) {
    Polygon[] polygons = [self]
    for(int i=0; i<multi.numGeometries; i++) {
        polygons += multi.getGeometryN(i)
    }
    return FACTORY.createMultiPolygon(polygons)
}

//Other
//TODO : can it be replace by groovy syntax like c = a + b
static <T> T[] concatenate(T[] a, T[] b) {
    int aLen = a.length
    int bLen = b.length

    T[] c = Array.newInstance(a.getClass().getComponentType(), aLen + bLen)
    System.arraycopy(a, 0, c, 0, aLen)
    System.arraycopy(b, 0, c, aLen, bLen)

    return c
}
