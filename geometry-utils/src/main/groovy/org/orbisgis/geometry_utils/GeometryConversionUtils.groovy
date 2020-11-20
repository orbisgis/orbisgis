/*
 * Bundle Geomerty_Utils is part of the OrbisGIS platform
 *
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 *
 * OSM is distributed under LGPL 3 license.
 *
 * Copyright (C) 2020 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * OSM is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OSM is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * OSM. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.geometry_utils

import groovy.transform.Field
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.CoordinateXY
import org.locationtech.jts.geom.CoordinateXYM
import org.locationtech.jts.geom.CoordinateXYZM
import org.locationtech.jts.geom.Envelope
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.LinearRing
import org.locationtech.jts.geom.MultiLineString
import org.locationtech.jts.geom.MultiPoint
import org.locationtech.jts.geom.MultiPolygon
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon
import org.locationtech.jts.io.WKTReader
import org.locationtech.jts.io.WKTWriter
import org.orbisgis.geometry_utils.formats.EWKB
import org.orbisgis.geometry_utils.formats.EWKT
import org.orbisgis.geometry_utils.formats.WKB
import org.orbisgis.geometry_utils.formats.WKT

import java.util.regex.Pattern

/**
 * Utility script used as extension module adding the conversion of collection/array of number into JTS Geometry
 * or Coordinates.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */

private static final @Field GeometryFactory FACTORY = new GeometryFactory()
private static final @Field WKTWriter WKT_WRITER = new WKTWriter()
private static final @Field WKTReader WKT_READER = new WKTReader()
private static final @Field Pattern EWKT_SRID_PATTERN =
        Pattern.compile("^\\s*SRID\\s*=\\s*(\\d*)\\s*;(.*)\$", Pattern.CASE_INSENSITIVE)

/**
 * AsType method allowing to convert Collection/Array into a Geometry or Coordinates. If the class is not
 * supported, return null.
 *
 * Supported classes :
 *  - Coordinate, CoordinateXY, CoordinateXYM, CoordinateXYZM
 *  - Coordinate[], CoordinateXY[], CoordinateXYM[], CoordinateXYZM[]
 *  - Point, LineString, Polygon, MultiPoint, MultiLineString, MultiPolygon, GeometryCollection
 *
 * @param collection Collection/Array of Number to convert.
 * @param aClass Destination conversion class.
 * @return Instance of the given class from the given Collection/Array
 */
static def asType(Collection collection, Class aClass) {
    switch(aClass) {
        //Conversion to simple Coordinate (XY, XYZ, XYM, XYZM)
        case Coordinate:
        case CoordinateXY:
        case CoordinateXYM:
        case CoordinateXYZM:
            return asCoordinate(collection, aClass)

        //Conversion to Coordinate array (XY, XYZ, XYM, XYZM)
        case Coordinate[]:
        case CoordinateXY[]:
        case CoordinateXYM[]:
        case CoordinateXYZM[]:
            return asCoordinates(collection, aClass)

        //Conversion to Geometry
        case Point:
            return asPoint(collection)
        case LineString:
            return asLineString(collection)
        case Polygon:
            return asPolygon(collection)
        case MultiPoint:
            return asMultiPoint(collection)
        case MultiLineString:
            return asMultiLineString(collection)
        case MultiPolygon:
            return asMultiPolygon(collection)

        case Envelope:
            return EnvelopeUtils.toBbox(collection)
    }
    return null
}

/**
 * AsType methods allowing to convert a Geometry into an other class.
 *
 * @param geom Geometry to convert.
 * @param aClass Destination class.
 * @return An instance of the given class from the given geometry.
 */
static def asType(Geometry geom, Class aClass) {
    switch(aClass) {
        case Envelope:
            return geom.getEnvelopeInternal()
        case String:
            return WKT_WRITER.write(geom)
        case WKT:
            return new WKT(geom)
        case EWKT:
            return new EWKT(geom)
        case WKB:
            return new WKB(geom)
        case EWKB:
            return new EWKB(geom)
    }
    return geom
}

/**
 * AsType methods allowing to convert a String into a Geometry
 *
 * @param geom
 * @param aClass
 * @return
 */
static def asType(String str, Class aClass) {
    switch(aClass) {
        case Point:
        case LineString:
        case Polygon:
        case MultiPoint:
        case MultiLineString:
        case MultiPolygon:
        case Geometry:
            def matcher = EWKT_SRID_PATTERN.matcher(str)
            def wkt = str
            def srid = -1
            if(matcher.find()) {
                srid = Integer.parseInt(matcher.group(1))
                wkt = matcher.group(2)
            }
            def geom = WKT_READER.read(wkt)
            if(srid != -1) {
                geom.SRID = srid
            }
            if(aClass.isAssignableFrom(geom.class)) {
                return geom
            }
            else{
                error("Wrong geometry type, string is ${geom.class.simpleName} whereas the given class is ${aClass.simpleName}")
                return null
            }
    }
    return null
}

/**
 * Convert a collection/array to a point. The collection/array should be a collection/array of number.
 *
 * Example :
 * 2D Point :
 * [1, 2]
 *
 * 3D Point (XYZ) :
 * [1, 2, 3]
 *
 * 4D Point :
 * [1, 2, 3, 4]
 *
 * @param collection Collection/array of number.
 * @return A JTS Point or null if the collection/array is not supported.
 */
private static Point asPoint(Collection collection) {
    if(collection.isEmpty()) {
        return FACTORY.createPoint()
    }
    else if(collection.size() == 2) {
        return FACTORY.createPoint(collection as CoordinateXY)
    }
    else if(collection.size() == 3) {
        return FACTORY.createPoint(collection as Coordinate)
    }
    else if(collection.size() == 4) {
        return FACTORY.createPoint(collection as CoordinateXYZM)
    }
    else {
        error("Unable to create ${Point.getSimpleName()} with object : ${collection.toString()}")
        return null
    }
}

/**
 * Convert a collection/array to a lineString. The collection/array should be a collection/array of point.
 *
 * Example :
 * 2D LineString :
 * [[1, 2], [2, 3], [3, 4]]
 *
 * 3D LineString (XYZ) :
 * [[1, 2, 3], [2, 3, 4], [3, 4, 5]]
 *
 * 4D LineString :
 * [[1, 2, 3, 4], [2, 3, 4, 5], [3, 4, 5, 6]]
 *
 * @param collection Collection/array of points.
 * @return A JTS LineString or null if the collection/array is not supported.
 */
private static LineString asLineString(Collection collection) {
    if(collection.isEmpty())
        return FACTORY.createLineString()
    else if(collection[0] instanceof Collection) {
        return FACTORY.createLineString(asCoordinates(collection))
    }
    else {
        error("Unable to create ${LineString.getSimpleName()} with object : ${collection.toString()}")
        return null
    }
}

/**
 * Convert a collection/array to a polygon. The collection/array should be a closed collection/array of point or a 2D
 * array of point (array of array of polygon) for polygon with hole.
 *
 * Example :
 * 2D Polygon without hole (2D array) :
 * [[0,0], [3,0], [3,2], [1,3], [0,0]]
 * 2D Polygon without hole (3D array) :
 * [[[0,0], [3,0], [3,2], [1,3], [0,0]]]
 * 2D Polygon with hole (3D array) :
 * [
 *   [[0,0], [3,0], [3,2], [1,3], [0,0]],
 *   [[1,1], [2,1], [2,2], [1,1]]
 * ]
 *
 * 3D Polygon without hole (XYZ) (2D array) :
 * [[0,0,5], [3,0,5], [3,2,5], [1,3,5], [0,0,5]]
 * 3D Polygon without hole (XYZ) (3D array) :
 * [[[0,0,5], [3,0,5], [3,2,5], [1,3,5], [0,0,5]]]
 * 3D Polygon with hole (3D array) :
 * [
 *   [[0,0,5], [3,0,5], [3,2,5], [1,3,5], [0,0,5]],
 *   [[1,1,5], [2,1,5], [2,2,5], [1,1,5]]
 * ]
 *
 * 4D Polygon without hole (2D array) :
 * [[0,0,5,6], [3,0,5,6], [3,2,5,6], [1,3,5,6], [0,0,5,6]]
 * 4D Polygon without hole (3D array) :
 * [[0,0,5,6], [3,0,5,6], [3,2,5,6], [1,3,5,6], [0,0,5,6]]
 * 4D Polygon with hole (3D array) :
 * [
 *   [[0,0,5,6], [3,0,5,6], [3,2,5,6], [1,3,5,6], [0,0,5,6]],
 *   [[1,1,5], [2,1,5], [2,2,5], [1,1,5]]
 * ]
 *
 * @param collection Collection/array of points.
 * @return A JTS Polygon or null if the collection/array is not supported.
 */
private static Polygon asPolygon(Collection collection) {
    if(collection.isEmpty())
        return FACTORY.createPolygon()
    //Check that the collection is at least a 2D collection/array
    else if(collection instanceof Collection<Collection> && collection[0] instanceof Collection && collection[0]) {
        //First case : Polygon with hole
        if(collection[0][0] instanceof Collection && collection[0][0]) {
            def shell = FACTORY.createLinearRing(asCoordinates(collection[0]))
            if (collection.size() > 1) {
                def holes = collection.stream().skip(1)
                        .map(it -> FACTORY.createLinearRing(asCoordinates(it))).collect().toArray() as LinearRing[]
                return FACTORY.createPolygon(shell, holes)
            }
            else {
                return FACTORY.createPolygon(shell)
            }
        }
        //Second case : Polygon without hole
        else {
            return FACTORY.createPolygon(asCoordinates(collection))
        }
    }
    else {
        error("Unable to create ${LineString.getSimpleName()} with object : ${collection.toString()}")
        return null
    }
}

/**
 * Convert a collection/array to a MultiPoint. The collection/array should be an array of point.
 *
 * Example :
 * 2D MultiPoint :
 * [[0,0], [3,0], [3,2], [1,3]]
 *
 * 3D MultiPoint :
 * [[0,0,5], [3,0,5], [3,2,5], [1,3,5], [0,0,5]]
 *
 * 4D MultiPoint :
 * [[0,0,5,6], [3,0,5,6], [3,2,5,6], [1,3,5,6], [0,0,5,6]]
 *
 * @param collection Collection/array of points.
 * @return A JTS Polygon or null if the collection/array is not supported.
 */
private static MultiPoint asMultiPoint(Collection collection) {
    if(collection.isEmpty())
        return FACTORY.createMultiPoint()
    else if(collection[0] instanceof Collection) {
        def points = collection.stream().map(it -> it as Point).toArray() as Point[]
        return FACTORY.createMultiPoint(points)
    }
    else {
        error("Unable to create ${MultiPoint.getSimpleName()} with object : ${collection.toString()}")
        return null
    }
}

private static MultiLineString asMultiLineString(Collection collection) {
    if(collection.isEmpty())
        return FACTORY.createMultiLineString()
    else if(collection instanceof Collection<Collection<Collection>> &&
            collection[0] instanceof Collection && collection[0] &&
            collection[0][0] instanceof Collection && collection[0][0]) {
        def lineStrings = collection.stream().map(it -> asLineString(it)).toArray() as LineString[]
        return FACTORY.createMultiLineString(lineStrings)
    }
    else {
        error("Unable to create ${MultiLineString.getSimpleName()} with object : ${collection.toString()}")
        return null
    }
}

/**
 * Convert a collection/array to a multipolygon. The collection/array should be a collection/array of polygon.
 *
 * Example :
 * MultiPolygon with hole (4D collection/array) :
 * [
 *   [
 *     [
 *       [0,0], [3,0], [3,2], [1,3], [0,0]
 *     ], [
 *       [1,1], [2,1], [2,2], [1,1]
 *     ]
 *   ], [
 *     [14,16], [16,16], [16,18], [14,16]
 *   ]
 * ]
 * MultiPolygon without hole (4D collection/array) :
 * [
 *   [[
 *     [0,0], [3,0], [3,2], [1,3], [0,0]
 *   ]], [[
 *     [14,16], [16,16], [16,18], [14,16]
 *   ]]
 * ]
 * MultiPolygon without hole (3D collection/array) :
 * [
 *   [
 *     [0,0], [3,0], [3,2], [1,3], [0,0]
 *   ], [
 *     [14,16], [16,16], [16,18], [14,16]
 *   ]
 * ]
 *
 * @param collection
 * @return
 */
private static def asMultiPolygon(Collection collection) {
    if(collection.isEmpty())
        return FACTORY.createMultiLineString()
    if(collection instanceof Collection<Collection<Collection>> &&
            collection[0] instanceof Collection && collection[0] &&
            collection[0][0] instanceof Collection && collection[0][0]) {
        def polygons = collection.stream().map(it -> asPolygon(it) ).toArray() as Polygon[]
        return FACTORY.createMultiPolygon(polygons)
    }
    error("Unable to create ${MultiLineString.getSimpleName()} with object : ${collection.toString()}")
}

private static Coordinate asCoordinate(Collection<Number> collection, Class<? extends Coordinate> aClass = null) {
    if(aClass == null) {
        if (collection.size() == 2) {
            aClass = CoordinateXY
        }
        if (collection.size() == 3) {
            aClass = Coordinate
        }
        if (collection.size() == 4) {
            aClass = CoordinateXYZM
        }
    }
    switch(aClass) {
        case CoordinateXY:
            if(collection.isEmpty())
                return new CoordinateXY()
            if (collection.stream().allMatch(number -> number instanceof Number)) {
                if (collection.size() == 2) {
                    return new CoordinateXY(collection[0] as double, collection[1] as double)
                }
            }
            break

        case CoordinateXYZM:
            if(collection.isEmpty())
                return new CoordinateXYZM()
            if (collection.stream().allMatch(number -> number instanceof Number) && collection.size() == 4) {
                return new CoordinateXYZM(collection[0] as double, collection[1] as double,
                        collection[2] as double, collection[3] as double)
            }
            break

        case CoordinateXYM:
            if(collection.isEmpty())
                return new CoordinateXYM()
            if (collection.stream().allMatch(number -> number instanceof Number) && collection.size() == 3) {
                return new CoordinateXYM(collection[0] as double, collection[1] as double, collection[2] as double)
            }
            break

        case Coordinate:
            if(collection.isEmpty())
                return new Coordinate()
            if (collection.stream().allMatch(number -> number instanceof Number)) {
                if (collection.size() == 2) {
                    return new Coordinate(collection[0] as double, collection[1] as double)
                }
                if (collection.size() == 3) {
                    return new Coordinate(collection[0] as double, collection[1] as double, collection[2] as double)
                }
            }
            break
    }
    error("Unable to create ${aClass.getSimpleName()} with object : ${collection.toString()}")
    return null
}

private static Coordinate[] asCoordinates(Collection<Collection<Number>> collection, Class<? extends Coordinate[]> aClass = null) {
    if(aClass == null) {
        if (collection[0].size() == 2) {
            aClass = CoordinateXY[]
        }
        if (collection[0].size() == 3) {
            aClass = Coordinate[]
        }
        if (collection[0].size() == 4) {
            aClass = CoordinateXYZM[]
        }
    }
    switch(aClass) {
        case CoordinateXY[]:
            if(collection.isEmpty())
                return new CoordinateXY[]{}
            if (collection.stream().allMatch(it -> it instanceof Collection)) {
                int dim = collection[0].size()
                if(collection.stream().allMatch(it -> it.size() == dim)) {
                    return collection.stream().map(it -> it as CoordinateXY).toArray() as CoordinateXY[]
                }
                else {
                    error("Not consistent coordinate dimension.")
                }
            }
            break

        case CoordinateXYZM[]:
            if(collection.isEmpty())
                return new CoordinateXYZM[]{}
            if (collection.stream().allMatch(it -> it instanceof Collection)) {
                int dim = collection[0].size()
                if(collection.stream().allMatch(it -> it.size() == dim)) {
                    return collection.stream().map(it -> it as CoordinateXYZM).toArray() as CoordinateXYZM[]
                }
                else {
                    error("Not consistent coordinate dimension.")
                }
            }
            break

        case CoordinateXYM[]:
            if(collection.isEmpty())
                return new CoordinateXYM[]{}
            if (collection.stream().allMatch(it -> it instanceof Collection)) {
                int dim = collection[0].size()
                if(collection.stream().allMatch(it -> it.size() == dim)) {
                    return collection.stream().map(it -> it as CoordinateXYM).toArray() as CoordinateXYM[]
                }
                else {
                    error("Not consistent coordinate dimension.")
                }
            }
            break

        case Coordinate[]:
            if(collection.isEmpty())
                return new Coordinate[]{}
            if (collection.stream().allMatch(it -> it instanceof Collection)) {
                int dim = collection[0].size()
                if(collection.stream().allMatch(it -> it.size() == dim)) {
                    return collection.stream().map(it -> it as Coordinate).toArray() as Coordinate[]
                }
                else {
                    error("Not consistent coordinate dimension.")
                }
            }
            break
    }
    error("Unable to create ${aClass.getSimpleName()} with object : ${collection.toString()}")
    return null
}