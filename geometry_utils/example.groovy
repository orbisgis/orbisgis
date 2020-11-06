import org.locationtech.jts.geom.*
import org.orbisgis.geometry_utils.formats.WKB
import org.orbisgis.geometry_utils.formats.WKT

/** Geometry create **/

// Point
/// 2D (XY) point
assert [4, 5.0] as Point
assert "POINT(4 5.0)" as Point
assert "SRID=4326;POINT(4 5.0)" as Point
assert "POINT(4 5.0)" as Geometry instanceof Point
/// 3D (XYZ) point
assert [4, 5.0, 6] as Point
assert "POINTZ(1 2 3)" as Point
assert "SRID=4326;POINTZ(1 2 3)" as Point
assert "POINTZ(1 2 3)" as Geometry instanceof Point
/// 4D (XYZM) point
assert [4, 5.0, 6, 7] as Point
assert "POINTZM(1 2 3 4)" as Point
assert "SRID=4326;POINTZM(1 2 3 4)" as Point
assert "POINTZM(1 2 3 4)" as Geometry instanceof Point

// LineString
/// 2D (XY) linestring
assert [[4.0, 2], [2, 3], [3.0, 5.0]] as LineString
assert "LINESTRING(4.0 2, 2 3, 3.0 5.0)" as LineString
assert "SRID=4326;LINESTRING(4.0 2, 2 3, 3.0 5.0)" as LineString
assert "LINESTRING(4.0 2, 2 3, 3.0 5.0)" as Geometry instanceof LineString
/// 3D (XYZ) linestring
assert [[4.0, 2, 8], [2, 3, 7], [3.0, 5.0, 6]] as LineString
assert "LINESTRINGZ(4.0 2 8, 2 3 7, 3.0 5.0 6)" as LineString
assert "SRID=4326;LINESTRINGZ(4.0 2 8, 2 3 7, 3.0 5.0 6)" as LineString
assert "LINESTRINGZ(4.0 2 8, 2 3 7, 3.0 5.0 6)" as Geometry instanceof LineString
/// 4D (XYZM) linestring
assert [[4.0, 2, 8, 5], [2, 3, 7, 6], [3.0, 5.0, 6, 7]] as LineString
assert "LINESTRINGZM(4.0 2 8 5, 2 3 7 6, 3.0 5.0 6 7)" as LineString
assert "SRID=4326;LINESTRINGZM(4.0 2 8 5, 2 3 7 6, 3.0 5.0 6 7)" as LineString
assert "LINESTRINGZM(4.0 2 8 5, 2 3 7 6, 3.0 5.0 6 7)" as Geometry instanceof LineString

// Polygon
/// 2D (XY) polygon
assert [[[0,0], [3,0], [3,2], [1,3], [0,0]]] as Polygon
assert "POLYGON((0 0, 3 0, 3 2, 1 3, 0 0))" as Polygon
assert "SRID=4326;POLYGON((0 0, 3 0, 3 2, 1 3, 0 0))" as Polygon
assert "POLYGON((0 0, 3 0, 3 2, 1 3, 0 0))" as Geometry instanceof Polygon
assert [[[0,0], [3,0], [3,2], [1,3], [0,0]], [[1,1], [2,1], [2,2], [1,1]]] as Polygon
assert "POLYGON((0 0, 3 0, 3 2, 1 3, 0 0), (1 1, 2 1, 2 2, 1 1))" as Polygon
assert "SRID=4326;POLYGON((0 0, 3 0, 3 2, 1 3, 0 0), (1 1, 2 1, 2 2, 1 1))" as Polygon
assert "POLYGON((0 0, 3 0, 3 2, 1 3, 0 0), (1 1, 2 1, 2 2, 1 1))" as Geometry instanceof Polygon
/// 3D (XYZ) polygon
assert [[[0,0,8], [3,0,6], [3,2,4], [1,3,2], [0,0,0]]] as Polygon
assert "POLYGONZ((0 0 8, 3 0 6, 3 2 4, 1 3 2, 0 0 0), (1 1 1, 2 1 2, 2 2 2, 1 1 1))" as Polygon
assert "SRID=4326;POLYGONZ((0 0 8, 3 0 6, 3 2 4, 1 3 2, 0 0 0), (1 1 1, 2 1 2, 2 2 2, 1 1 1))" as Polygon
assert "POLYGONZ((0 0 8, 3 0 6, 3 2 4, 1 3 2, 0 0 0), (1 1 1, 2 1 2, 2 2 2, 1 1 1))" as Geometry instanceof Polygon
assert [[[0,0,8], [3,0,7], [3,2,6], [1,3,5], [0,0,4]], [[1,1,1], [2,1,2], [2,2,2], [1,1,1]]] as Polygon
assert "POLYGONZM((0 0 8 0, 3 0 6 1, 3 2 4 1, 1 3 2 0, 0 0 0 1))" as Polygon
assert "SRID=4326;POLYGONZM((0 0 8 0, 3 0 6 1, 3 2 4 1, 1 3 2 0, 0 0 0 1))" as Polygon
assert "POLYGONZM((0 0 8 0, 3 0 6 1, 3 2 4 1, 1 3 2 0, 0 0 0 1))" as Geometry instanceof Polygon
/// 4D (XYZM) polygon
assert [[[0,0,8,0], [3,0,6,1], [3,2,4,1], [1,3,2,0], [0,0,0,1]]] as Polygon
assert "POLYGONZM((0 0 8 0, 3 0 6 1, 3 2 4 1, 1 3 2 0, 0 0 0 1))" as Polygon
assert "SRID=4326;POLYGONZM((0 0 8 0, 3 0 6 1, 3 2 4 1, 1 3 2 0, 0 0 0 1))" as Polygon
assert "POLYGONZM((0 0 8 0, 3 0 6 1, 3 2 4 1, 1 3 2 0, 0 0 0 1))" as Geometry instanceof Polygon
assert [[[0,0,8,0], [3,0,7,1], [3,2,6,1], [1,3,5,0], [0,0,4,1]], [[1,1,1,5], [2,1,2,4], [2,2,2,3], [1,1,1,2]]] as Polygon
assert "POLYGONZM((0 0 8 0, 3 0 6 1, 3 2 4 1, 1 3 2 0, 0 0 0 1), (1 1 1 5, 2 1 2 4, 2 2 2 3, 1 1 1 2))" as Polygon
assert "SRID=4326;POLYGONZM((0 0 8 0, 3 0 6 1, 3 2 4 1, 1 3 2 0, 0 0 0 1), (1 1 1 5, 2 1 2 4, 2 2 2 3, 1 1 1 2))" as Polygon
assert "POLYGONZM((0 0 8 0, 3 0 6 1, 3 2 4 1, 1 3 2 0, 0 0 0 1), (1 1 1 5, 2 1 2 4, 2 2 2 3, 1 1 1 2))" as Geometry instanceof Polygon

// MultiPoint
///2D (XY) multiPoint
assert [[0,0], [3,0], [3,2], [1,3]] as MultiPoint
assert "MULTIPOINT(0 0, 3 0, 3 2, 1 3)" as MultiPoint
assert "SRID=4326;MULTIPOINT(0 0, 3 0, 3 2, 1 3)" as MultiPoint
assert "MULTIPOINT(0 0, 3 0, 3 2, 1 3)" as Geometry instanceof MultiPoint
///3D (XY) multiPoint
assert [[0,0,8], [3,0,8], [3,2,8], [1,3,8]] as MultiPoint
assert "MULTIPOINTZ(0 0 8, 3 0 8, 3 2 8, 1 3 8)" as MultiPoint
assert "SRID=4326;MULTIPOINTZ(0 0 8, 3 0 8, 3 2 8, 1 3 8)" as MultiPoint
assert "MULTIPOINTZ(0 0 8, 3 0 8, 3 2 8, 1 3 8)" as Geometry instanceof MultiPoint
///4D (XY) multiPoint
assert [[0,0,8,0], [3,0,8,0], [3,2,8,0], [1,3,8,0]] as MultiPoint
assert "MULTIPOINTZM(0 0 8 0, 3 0 8 0, 3 2 8 0, 1 3 8 0)" as MultiPoint
assert "SRID=4326;MULTIPOINTZM(0 0 8 0, 3 0 8 0, 3 2 8 0, 1 3 8 0)" as MultiPoint
assert "MULTIPOINTZM(0 0 8 0, 3 0 8 0, 3 2 8 0, 1 3 8 0)" as Geometry instanceof MultiPoint

// MultiLineString
///2D (XY) MultiLineString
assert [[[0,0], [3,0]], [[3,2], [1,3]]] as MultiLineString
assert "MULTILINESTRING((0 0, 3 0), (3 2, 1 3))" as MultiLineString
assert "SRID=4326;MULTILINESTRING((0 0, 3 0), (3 2, 1 3))" as MultiLineString
assert "MULTILINESTRING((0 0, 3 0), (3 2, 1 3))" as Geometry instanceof MultiLineString
///3D (XY) MultiLineString
assert [[[0,0,8], [3,0,8]], [[3,2,8], [1,3,8]]] as MultiLineString
assert "MULTILINESTRINGZ((0 0 8, 3 0 8), (3 2 8, 1 3 8))" as MultiLineString
assert "SRID=4326;MULTILINESTRINGZ((0 0 8, 3 0 8), (3 2 8, 1 3 8))" as MultiLineString
assert "MULTILINESTRINGZ((0 0 8, 3 0 8), (3 2 8, 1 3 8))" as Geometry instanceof MultiLineString
///4D (XY) MultiLineString
assert [[[0,0,8,0], [3,0,8,0]], [[3,2,8,0], [1,3,8,0]]] as MultiLineString
assert "MULTILINESTRINGZM((0 0 8 0, 3 0 8 0), (3 2 8 0, 1 3 8 0))" as MultiLineString
assert "SRID=4326;MULTILINESTRINGZM((0 0 8 0, 3 0 8 0), (3 2 8 0, 1 3 8 0))" as MultiLineString
assert "MULTILINESTRINGZM((0 0 8 0, 3 0 8 0), (3 2 8 0, 1 3 8 0))" as Geometry instanceof MultiLineString

// MultiPolygon
///2D (XY) MultiPolygon
assert [[[[0,0], [3,0], [3,2], [1,3], [0,0]]], [[[10, 10], [11, 10], [11, 11], [10, 10]]]] as MultiPolygon
assert "MULTIPOLYGON(((0 0, 3 0, 3 2, 1 3, 0 0)), ((10 10, 11 10, 11 11, 10 10)))" as MultiPolygon
assert "SRID=4326;MULTIPOLYGON(((0 0, 3 0, 3 2, 1 3, 0 0)), ((10 10, 11 10, 11 11, 10 10)))" as MultiPolygon
assert "MULTIPOLYGON(((0 0, 3 0, 3 2, 1 3, 0 0)), ((10 10, 11 10, 11 11, 10 10)))" as Geometry instanceof MultiPolygon
assert [[[[0,0], [3,0], [3,2], [1,3], [0,0]], [[1,1], [2,1], [2,2], [1,1]]], [[[10, 10], [11, 10], [11, 11], [10, 10]]]] as MultiPolygon
assert "MULTIPOLYGON(((0 0, 3 0, 3 2, 1 3, 0 0), (1 1, 2 1, 2 2, 1 1)), ((10 10, 11 10, 11 11, 10 10)))" as MultiPolygon
assert "SRID=4326;MULTIPOLYGON(((0 0, 3 0, 3 2, 1 3, 0 0), (1 1, 2 1, 2 2, 1 1)), ((10 10, 11 10, 11 11, 10 10)))" as MultiPolygon
assert "MULTIPOLYGON(((0 0, 3 0, 3 2, 1 3, 0 0), (1 1, 2 1, 2 2, 1 1)), ((10 10, 11 10, 11 11, 10 10)))" as Geometry instanceof MultiPolygon
///3D (XY) MultiPolygon
assert [[[[0,0,8], [3,0,6], [3,2,4], [1,3,2], [0,0,4]]], [[[10, 10, 1], [11, 10, 2], [11, 11, 3], [10, 10, 4]]]] as MultiPolygon
assert "MULTIPOLYGONZ(((0 0 8, 3 0 6, 3 2 4, 1 3 2, 0 0 4)), ((10 10 1, 11 10 2, 11 11 3, 10 10 4)))" as MultiPolygon
assert "SRID=4326;MULTIPOLYGONZ(((0 0 8, 3 0 6, 3 2 4, 1 3 2, 0 0 4)), ((10 10 1, 11 10 2, 11 11 3, 10 10 4)))" as MultiPolygon
assert "MULTIPOLYGONZ(((0 0 8, 3 0 6, 3 2 4, 1 3 2, 0 0 4)), ((10 10 1, 11 10 2, 11 11 3, 10 10 4)))" as Geometry instanceof MultiPolygon
assert [[[[0,0,8], [3,0,6], [3,2,4], [1,3,2], [0,0,4]], [[1,1,1], [2,1,2], [2,2,2], [1,1,1]]], [[[10, 10, 1], [11, 10, 2], [11, 11, 3], [10, 10, 4]]]] as MultiPolygon
assert "MULTIPOLYGONZ(((0 0 8, 3 0 6, 3 2 4, 1 3 2, 0 0 4), (1 1 1, 2 1 2, 2 2 2, 1 1 1)), ((10 10 1, 11 10 2, 11 11 3, 10 10 4)))" as MultiPolygon
assert "SRID=4326;MULTIPOLYGONZ(((0 0 8, 3 0 6, 3 2 4, 1 3 2, 0 0 4), (1 1 1, 2 1 2, 2 2 2, 1 1 1)), ((10 10 1, 11 10 2, 11 11 3, 10 10 4)))" as MultiPolygon
assert "MULTIPOLYGONZ(((0 0 8, 3 0 6, 3 2 4, 1 3 2, 0 0 4), (1 1 1, 2 1 2, 2 2 2, 1 1 1)), ((10 10 1, 11 10 2, 11 11 3, 10 10 4)))" as Geometry instanceof MultiPolygon
///4D (XY) MultiPolygon
assert [[[[0,0,8,0], [3,0,6,1], [3,2,4,1], [1,3,2,0], [0,0,4,1]]], [[[10, 10, 1, 4], [11, 10, 2, 3], [11, 11, 3, 2], [10, 10, 4, 1]]]] as MultiPolygon
assert "MULTIPOLYGONZM(((0 0 8 0, 3 0 6 1, 3 2 4 1, 1 3 2 0, 0 0 4 1)), ((10 10 1 4, 11 10 2 3, 11 11 3 2, 10 10 4 1)))" as MultiPolygon
assert "SRID=4326;MULTIPOLYGONZM(((0 0 8 0, 3 0 6 1, 3 2 4 1, 1 3 2 0, 0 0 4 1)), ((10 10 1 4, 11 10 2 3, 11 11 3 2, 10 10 4 1)))" as MultiPolygon
assert "MULTIPOLYGONZM(((0 0 8 0, 3 0 6 1, 3 2 4 1, 1 3 2 0, 0 0 4 1)), ((10 10 1 4, 11 10 2 3, 11 11 3 2, 10 10 4 1)))" as Geometry instanceof MultiPolygon
assert [[[[0,0,8,0], [3,0,6,1], [3,2,4,1], [1,3,2,0], [0,0,4,1]], [[1,1,1,5], [2,1,2,4], [2,2,2,3], [1,1,1,2]]], [[[10, 10, 1, 4], [11, 10, 2, 3], [11, 11, 3, 2], [10, 10, 4, 1]]]] as MultiPolygon
assert "MULTIPOLYGONZM(((0 0 8 0, 3 0 6 1, 3 2 4 1, 1 3 2 0, 0 0 4 1), (1 1 1 5, 2 1 2 4, 2 2 2 3, 1 1 1 2)), ((10 10 1 4, 11 10 2 3, 11 11 3 2, 10 10 4 1)))" as MultiPolygon
assert "SRID=4326;MULTIPOLYGONZM(((0 0 8 0, 3 0 6 1, 3 2 4 1, 1 3 2 0, 0 0 4 1), (1 1 1 5, 2 1 2 4, 2 2 2 3, 1 1 1 2)), ((10 10 1 4, 11 10 2 3, 11 11 3 2, 10 10 4 1)))" as MultiPolygon
assert "MULTIPOLYGONZM(((0 0 8 0, 3 0 6 1, 3 2 4 1, 1 3 2 0, 0 0 4 1), (1 1 1 5, 2 1 2 4, 2 2 2 3, 1 1 1 2)), ((10 10 1 4, 11 10 2 3, 11 11 3 2, 10 10 4 1)))" as Geometry instanceof MultiPolygon


// Envelope
assert [[[0,0], [3,0], [3,2], [1,3], [0,0]]] as Polygon as Envelope instanceof Envelope
def str = [[[0,0], [3,0], [3,2], [1,3], [0,0]]] as Polygon as String
assert "POLYGON ((0 0, 3 0, 3 2, 1 3, 0 0))" == str


/** Geometry operator **/
// Point
def pt1 = [11, 12] as Point
def pt2 = [21, 22] as Point

assert pt1+pt2 == [[11, 12], [21, 22]] as MultiPoint // MultiPoint = pt1:pt2

assert pt1<<pt2 == [[11, 12], [21, 22]] as LineString //LineString = pt1-pt2
assert pt1>>pt2 == [[11, 12], [21, 22]] as LineString //LineString = pt2-pt1

// LineString
def pt = [0, 0] as Point
def line1 = [[111, 112], [121, 122]] as LineString
def line2 = [[211, 212], [221, 222]] as LineString

assert line1+line2 == [[[111, 112], [121, 122]], [[211, 212], [221, 222]]] as MultiLineString

assert line1<<line2 == [[111, 112], [121, 122], [211, 212], [221, 222]] as LineString
assert line1>>line2 == [[111, 112], [121, 122], [211, 212], [221, 222]] as LineString

assert line1<<pt == [[111, 112], [121, 122], [0, 0]] as LineString
assert pt>>line1 == [[0, 0], [111, 112], [121, 122]] as LineString

// Polygon
pt = [0, 1] as Point
def poly1 = [[0,0], [3,0], [3,2], [1,3], [0,0]] as Polygon
def poly2 = [[1,1], [2,1], [2,2], [1,1]] as Polygon

assert poly1+poly2 == [[[[0,0], [3,0], [3,2], [1,3], [0,0]]], [[[1,1], [2,1], [2,2], [1,1]]]] as MultiPolygon

assert poly1-poly2 == [[[0,0], [3,0], [3,2], [1,3], [0,0]], [[1,1], [2,1], [2,2], [1,1]]] as Polygon

assert poly1<<pt == [[0,0], [3,0], [3,2], [1,3], [0,1], [0,0]] as Polygon
assert pt>>poly1 == [[0,1], [0,0], [3,0], [3,2], [1,3], [0,1]] as Polygon

// MultiPoint
pt = [0, 1] as Point
def multi = [[0,0], [3,0], [3,2], [1,3]] as MultiPoint

assert multi<<pt == [[0,0], [3,0], [3,2], [1,3], [0,1]] as MultiPoint
assert pt>>multi == [[0,1], [0,0], [3,0], [3,2], [1,3]] as MultiPoint

assert pt + multi instanceof GeometryCollection

// MultiLineString
multi = [[[111, 112], [121, 122]], [[211, 212], [221, 222]]] as MultiLineString
def line = [[311, 312], [321, 322]] as LineString

assert multi<<line == [[[111, 112], [121, 122]], [[211, 212], [221, 222]], [[311, 312], [321, 322]]] as MultiLineString
assert line>>multi == [[[311, 312], [321, 322]], [[111, 112], [121, 122]], [[211, 212], [221, 222]]] as MultiLineString

assert line + multi instanceof GeometryCollection

// MultiPolygon
def poly = [[10,10], [11,11], [10,11], [10,10]] as Polygon
multi = [[[[0,0], [3,0], [3,2], [1,3], [0,0]]], [[[1,1], [2,1], [2,2], [1,1]]]] as MultiPolygon

assert multi<<poly == [[[[0,0], [3,0], [3,2], [1,3], [0,0]]], [[[1,1], [2,1], [2,2], [1,1]]], [[10,10], [11,11], [10,11], [10,10]]] as MultiPolygon
assert poly>>multi == [[[10,10], [11,11], [10,11], [10,10]], [[[0,0], [3,0], [3,2], [1,3], [0,0]]], [[[1,1], [2,1], [2,2], [1,1]]]] as MultiPolygon

assert poly + multi instanceof GeometryCollection

/** WKT / WKB **/
geom = [4, 5.0] as Point
def wkt = "POINT (4 5)"
def wkb = [0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x10, 0x40, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x14, 0x40]
assert geom as WKT == wkt
assert geom as WKB == wkb

/** Geometry methods **/
def geom = [[10,10], [11,11], [10,11], [10,10]] as Polygon
assert geom.expandEnvelopeByMeters(10) instanceof Envelope