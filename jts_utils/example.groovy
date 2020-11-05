import org.locationtech.jts.geom.*
/** Geometry create **/

// Point
/// 2D (XY) point
assert [4, 5.0] as Point
/// 3D (XYZ) point
assert [4, 5.0, 6] as Point
/// 4D (XYZM) point
assert [4, 5.0, 6, 7] as Point

// LineString
/// 2D (XY) linestring
assert [[4.0, 2], [2, 3], [3.0, 5.0]] as LineString
/// 3D (XYZ) linestring
assert [[4.0, 2, 8], [2, 3, 7], [3.0, 5.0, 6]] as LineString
/// 4D (XYZM) linestring
assert [[4.0, 2, 8, 5], [2, 3, 7, 6], [3.0, 5.0, 6, 7]] as LineString

// Polygon
/// 2D (XY) polygon
assert [[[0,0], [3,0], [3,2], [1,3], [0,0]]] as Polygon
assert [[[0,0], [3,0], [3,2], [1,3], [0,0]], [[1,1], [2,1], [2,2], [1,1]]] as Polygon
/// 3D (XYZ) polygon
assert [[[0,0,8], [3,0,6], [3,2,4], [1,3,2], [0,0,0]]] as Polygon
assert [[[0,0,8], [3,0,7], [3,2,6], [1,3,5], [0,0,4]], [[1,1,1], [2,1,2], [2,2,2], [1,1,1]]] as Polygon
/// 4D (XYZM) polygon
assert [[[0,0,8,0], [3,0,6,1], [3,2,4,1], [1,3,2,0], [0,0,0,1]]] as Polygon
assert [[[0,0,8,0], [3,0,7,1], [3,2,6,1], [1,3,5,0], [0,0,4,1]], [[1,1,1,5], [2,1,2,4], [2,2,2,3], [1,1,1,2]]] as Polygon

// MultiPoint
///2D (XY) multiPoint
assert [[0,0], [3,0], [3,2], [1,3]] as MultiPoint
///3D (XY) multiPoint
assert [[0,0,8], [3,0,8], [3,2,8], [1,3,8]] as MultiPoint
///4D (XY) multiPoint
assert [[0,0,8,0], [3,0,8,0], [3,2,8,0], [1,3,8,0]] as MultiPoint

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


/** Geometry methods **/
def geom = [[10,10], [11,11], [10,11], [10,10]] as Polygon
assert geom.expandEnvelopeByMeters(10) instanceof Envelope