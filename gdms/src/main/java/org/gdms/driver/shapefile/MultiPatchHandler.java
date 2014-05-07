/*
 *    Geotools - OpenSource mapping toolkit
 *    (C) 2002, Centre for Computational Geography
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.gdms.driver.shapefile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.vividsolutions.jts.algorithm.RobustCGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import org.gdms.driver.ReadBufferManager;
import org.gdms.driver.WriteBufferManager;
import org.gdms.geometryUtils.CoordinatesUtils;

/**
 * Wrapper for a Shapefile multipatch. Rings are supported, triangle strips
 * and triangle fans are left apart for the moment.
 * Be careful  : the JTS polygons obtained here are 3D geometries.
 * Most algorithms will use only the x and y values, and return wrong
 * results if called on these.
 * Since JTS lacks 3d support, a few geometry functions are coded below,
 * move or replace them whenever there is a place in JTS for these.
 *
 * @author sbitot
 * @author aaime
 * @author Ian Schneider
 */
public class MultiPatchHandler implements ShapeHandler {


    GeometryFactory geometryFactory = new GeometryFactory();

    final ShapeType shapeType;

    public MultiPatchHandler() {
        shapeType = ShapeType.MULTIPATCH;
    }

    public MultiPatchHandler(ShapeType type) throws ShapefileException {
        if (type != ShapeType.MULTIPATCH) {
            throw new ShapefileException(
                    "MultipatchHandler constructor - expected type to be 31.");
        }

        shapeType = type;
    }


    /**
     * Returns true if testPoint is a point in the pointList list.
     * Same as Polgonhandler method
     *
     * @param testPoint
     * @param pointList
     * @return
     */
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

    @Override
    public ShapeType getShapeType() {
        return shapeType;
    }

    @Override
    public int getLength(Object geometry) {
        MultiPolygon multi;

        if (geometry instanceof MultiPolygon) {
            multi = (MultiPolygon) geometry;
        } else {
            Geometry g = (Geometry) geometry;
            multi = geometryFactory.createMultiPolygon(new Polygon[]{(Polygon) geometry});
        }

        int nrings = 0;

        for (int t = 0; t < multi.getNumGeometries(); t++) {
            Polygon p;
            p = (Polygon) multi.getGeometryN(t);
            nrings = nrings + 1 + p.getNumInteriorRing();
        }

        int npoints = multi.getNumPoints();
        int length;

        if (shapeType == ShapeType.MULTIPATCH) {
            length = 44 + (4 * nrings) + (4 * nrings) + (16 * npoints) + (8 * npoints) + 16 + (8 * npoints) + 16;
        } else {
            throw new IllegalStateException("Expected ShapeType of Multipatch, got " + shapeType);
        }
        return length;
    }

    @Override
    public Geometry read(ReadBufferManager buffer, ShapeType type) throws IOException {

        if (type == ShapeType.NULL) {
            return createNull();
        }
        //bounds
        buffer.position(buffer.getPosition() + 4 * 8); // Byte 36

        int[] partOffsets;
        PartType[] partTypes;

        int numParts = buffer.getInt(); // Bute 40
        int numPoints = buffer.getInt();// Byte 44

        partOffsets = new int[numParts];
        partTypes = new PartType[numParts];

        for (int i = 0; i < numParts; i++) {
            partOffsets[i] = buffer.getInt();
        } // Byte W = 44+4*numParts
        for (int i = 0; i < numParts; i++) {
            partTypes[i] = PartType.forID(buffer.getInt());
        } //  Byte X = W+4*numParts

        Coordinate[] coords = readCoordinates(buffer, numPoints);

        // Jump over Zmin and Zmax and get the z coordinates
        buffer.position(buffer.getPosition() + 2 * 8); //Byte Y+16
        for (int t = 0; t < numPoints; t++) {
            coords[t].z = buffer.getDouble();
        } // Byte Z = Y + 16 + (8*numPoints)

        ArrayList polygons = new ArrayList();
        LinearRing shell;

        // Loop on the different parts, structured to be a loop on the different Polygons
        int part = 0;
        while (part < numParts) {

            switch (partTypes[part].id) {

                // New geometry : get the inner rings and create the polygon
                // case PartType.OUTER_RING.id:
                case 2:
                    shell = readRing(part, partOffsets, numParts, numPoints, coords);
                    part++;

                    ArrayList holes = new ArrayList();
                    while ((part < numParts) && (partTypes[part] == PartType.INNER_RING)) {
                        holes.add(readRing(part, partOffsets, numParts, numPoints, coords));
                        part++;
                    }
                    polygons.add(geometryFactory.createPolygon(shell, (LinearRing[]) holes.toArray(new LinearRing[0])));

                    break;

                // New geometry : check for RINGS and determine shell and holes
                // case PartType.FIRST_RING.id:
                case 4:
                    ArrayList<LinearRing> rings = new ArrayList<LinearRing>();
                    rings.add(readRing(part, partOffsets, numParts, numPoints, coords));
                    part++;

                    while ((part < numParts) && (partTypes[part].id == PartType.RING.id)) {
                        rings.add(readRing(part, partOffsets, numParts, numPoints, coords));
                        part++;
                    }

                    // We assume the different parts are coplanar, and define an acceptable polygon
                    // The shell is the ring with the largest area, cga.signedarea offers a not so
                    // slow solution.
                    LinearRing[] flatRings = toFloor(rings.toArray(new LinearRing[rings.size()]));
                    if (flatRings == null) {
                        return (null);
                    }
                    double maxArea = 0.0;
                    int index = 0;
                    for (int i = 0; i < rings.size(); i++) {
                        double area = 0.0;
                        area += Math.abs(RobustCGAlgorithms.signedArea(flatRings[i].getCoordinates()));
                        if (area > maxArea) {
                            index = i;
                            maxArea = area;
                        }
                    }

                    // Assumes toFloor is indexed same as rings
                    shell = (LinearRing) rings.get(index);
                    rings.remove(index);

                    polygons.add(geometryFactory.createPolygon(shell, (LinearRing[]) rings.toArray(new LinearRing[rings.size()])));
                    break;

                // Hole without Shell or Ring without First Ring : take as shell and carry on
                // case PartType.INNER_RING.id: case PartType.RING.id:
                case 3:
                case 5:
                    Logger.getLogger("org.geotools.data.shapefile").warning(
                            "only one hole in this polygon record");
                    shell = readRing(part, partOffsets, numParts, numPoints, coords);
                    part++;
                    polygons.add(geometryFactory.createPolygon(shell, null));
                    break;

                default:
                    Logger.getLogger("org.geotools.data.shapefile").warning(
                            "MultiPatchHandler - Triangle Strip and Triangle Fan not supported. Moving to next part");
                    part++;
                    break;
            }
        }

        Geometry g = geometryFactory.createMultiPolygon((Polygon[]) polygons.toArray(new Polygon[0]));
        return g;

    }

    /**
     * @param part parameters used to read the wanted coordinates
     * @return ring
     * TODO : This is quite a dirty way to push the reading of a ring off read(), if anyone finds something clean...
     */
    private LinearRing readRing
    (int part, int[] partOffsets, int numParts, int numPoints, Coordinate[] coords) {
        int start;
        int finish;
        int length;

        start = partOffsets[part];
        int offset = start;
        if (part == (numParts - 1)) {
            finish = numPoints;
        } else {
            finish = partOffsets[part + 1];
        }
        length = finish - start;
        Coordinate[] points = new Coordinate[length];

        for (int i = 0; i < length; i++) {
            points[i] = coords[offset++];
        }

        if (points.length == 0 || points.length > 3) {

            LinearRing g = geometryFactory.createLinearRing(points);

            return g;
        } else {
            return null;
        }
    }

    /**
     * @param buffer
     * @param numPoints
     */
    private Coordinate[] readCoordinates(final ReadBufferManager buffer, final int numPoints) throws IOException {
        Coordinate[] coords = new Coordinate[numPoints];

        for (int t = 0; t < numPoints; t++) {
            coords[t] = new Coordinate(buffer.getDouble(), buffer.getDouble());
        }

        return coords;
    }

    private MultiPolygon createNull() {
        return geometryFactory.createMultiPolygon(null);
    }

    /**
     * Writes the given geometry. No support of triangle fan or triangle strip here.
     *
     * @param buffer
     * @param geometry, supposed to be Polygon or MultiPolygon
     */
    @Override
    public void write(WriteBufferManager buffer, Object geometry) throws IOException {
        MultiPolygon multi;

        if (geometry instanceof MultiPolygon) {
            multi = (MultiPolygon) geometry;
        } else {
            Geometry g = (Geometry) geometry;
            multi = geometryFactory.createMultiPolygon(new Polygon[]{(Polygon) geometry});
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
        PartType[] types = new PartType[nrings];

        for (int t = 0; t < multi.getNumGeometries(); t++) {
            Polygon p;
            p = (Polygon) multi.getGeometryN(t);
            pointsPerRing[u] = p.getExteriorRing().getNumPoints();
            types[u] = PartType.OUTER_RING;
            u++;

            for (int v = 0; v < p.getNumInteriorRing(); v++) {
                pointsPerRing[u] = p.getInteriorRingN(v).getNumPoints();
                types[u] = PartType.INNER_RING;
                u++;
            }
        }

        int npoints = multi.getNumPoints();

        buffer.putInt(nrings);
        buffer.putInt(npoints);
        // Byte 44

        int count = 0;

        // Write index of first points
        for (int t = 0; t < nrings; t++) {
            buffer.putInt(count);
            count = count + pointsPerRing[t];
        } // Byte W = 44 + (4*nrings)

        // Write Part Types
        for (int t = 0; t < nrings; t++) {
            buffer.putInt(types[t].id);
        } // Byte X = W + (4*nrings)

        //write out points here!
        Coordinate[] coords = multi.getCoordinates();

        for (Coordinate coordinate : coords) {
            buffer.putDouble(coordinate.x);
            buffer.putDouble(coordinate.y);
        } // Byte Y = X + (16 * npoints)

        // shapeType == ShapeType.MULTIPATCH
        //z
        double[] zExtreame = CoordinatesUtils.zMinMax(multi.getCoordinates());

        if (Double.isNaN(zExtreame[0])) {
            buffer.putDouble(0.0);
            buffer.putDouble(0.0);
        } else {
            buffer.putDouble(zExtreame[0]);
            buffer.putDouble(zExtreame[1]);
        } // Byte Y + 16

        for (int t = 0; t < npoints; t++) {
            double z = coords[t].z;

            if (Double.isNaN(z)) {
                buffer.putDouble(0.0);
            } else {
                buffer.putDouble(z);
            }
        } // Byte Z = Y + 16 + (8 * npoints)

        //m
        buffer.putDouble(-10E40);
        buffer.putDouble(-10E40);

        for (int t = 0; t < npoints; t++) {
            buffer.putDouble(-10E40);
        } // Byte Z + 16 + (8 * npoints)
    }


    private double distance3D(Coordinate a, Coordinate b) {
        Coordinate u = new Coordinate(a.x - b.x, a.y - b.y, a.z - b.z);
        return (Math.sqrt(u.x * u.x + u.y * u.y + u.z * u.z));
    }


    /**
     * Computes the normal of the surface defined by the given coordinates, and returns the vector basis associated with the ring.
     * Coordinates are assumed coplanar. The sign of the normal vector is not taken in consideration here.
     *
     * @param coords Array of Coordinates
     * @return Coordinate (length = 1.0)
     */
    private Coordinate[] getBasis(Coordinate[] coords) {

        int numCoords = coords.length;
        Coordinate normal = new Coordinate(0, 0, 0);
        Coordinate v1 = new Coordinate(0, 0, 0);
        Coordinate v2 = new Coordinate(0, 0, 0);

        // Get 3 different points
        int i1 = 0; // Get the first point found
        int i2 = 0;

        double zmax = 0;
        double dist = 0;
        double nmax = 0;

        // Get the second point, as far as possible from the first
        for (int i = 1; i < numCoords; i++) {
            double d = distance3D(coords[i], coords[i1]);
            if (d > dist) {
                dist = d;
                i2 = i;
            }
        }
        if (dist == 0) {
            return null;
        } else {

            // Get the third point, maximizing the area

            for (int i = 1; i < numCoords; i++) {
                if (!coords[i].equals(coords[i2]) && !coords[i].equals3D(coords[i1])) {
                    double x1, x2, y1, y2, z1, z2; // coordinates of 2 vectors (0->1 and 0->2)

                    v1.x = coords[i1].x - coords[i].x;
                    v2.x = coords[i2].x - coords[i].x;
                    v1.y = coords[i1].y - coords[i].y;
                    v2.y = coords[i2].y - coords[i].y;
                    v1.z = coords[i1].z - coords[i].z;
                    v2.z = coords[i2].z - coords[i].z;

                    Coordinate norm = vectorProduct(v1, v2);
                    double n = Math.sqrt(norm.x * norm.x + norm.y * norm.y + norm.z * norm.z);
                    if (n > nmax) {
                        nmax = n;
                        normal = (Coordinate) norm.clone();
                    }
                }
            }

        }

        normalize(normal);
        normalize(v1);
        v2 = vectorProduct(normal, v1);
        if ((normal == null) || (v1 == null) || (v2 == null)) {
            return null;
        } else {
            return (new Coordinate[]{v1, v2, normal});
        }

    }


    /**
     * Set Vector's length to 1
     *
     * @param a
     * @return a
     */
    private Coordinate normalize(Coordinate a) {

        double length = distance3D(a, new Coordinate(0, 0, 0));
        if (length != 0) {
            a.x = a.x / length;
            a.y = a.y / length;
            a.z = a.z / length;
            return (a);
        } else {
            return null;
        }

    }


    /**
     * Returns a normal vector representative of the area and
     * orientation of the triangle defined by the 3 coordinates.
     *
     * @param a Coordinates a,b,c
     * @return normal Coordinate
     */
    private Coordinate vectorProduct(Coordinate a, Coordinate b) {

        double xn, yn, zn;

        xn = a.y * b.z - a.z * b.y;
        yn = a.z * b.x - a.x * b.z;
        zn = a.x * b.y - a.y * b.x;

        return (new Coordinate(xn, yn, zn));
    }


    /**
     * @param coords   Coordinates
     * @param rotation Rotation matrix
     * @return array of Coordinates
     */
    private Coordinate[] rotate3D(Coordinate[] coords, double[][] rotation) {
        Coordinate[] result = new Coordinate[coords.length];

        for (int i = 0; i < coords.length; i++) {
            result[i] = rotate3D(coords[i], rotation);
        }

        return result;
    }

    /**
     * Applies the given 3D rotation matrix to the given coordinate
     *
     * @param coord    Coordinate
     * @param rotation 3D Rotation matrix
     * @return moved coordinate
     */
    private Coordinate rotate3D(Coordinate coord, double[][] rotation) {


        double x = rotation[0][0] * coord.x + rotation[0][1] * coord.y + rotation[0][2] * coord.z;
        double y = rotation[1][0] * coord.x + rotation[1][1] * coord.y + rotation[1][2] * coord.z;
        double z = rotation[2][0] * coord.x + rotation[2][1] * coord.y + rotation[2][2] * coord.z;

        return (new Coordinate(x, y, z));
    }

    /**
     * Takes a sequence of Coordinates and returns the same sequence
     * projected on its normal, ie with normal pointing to z positive.
     * For all points z is the same so JTS algorithms can be applied
     * safely on the x and y values.
     * !! The function assumes Coordinates are coplanar !!
     *
     * @param coords Array of Coordinates
     */
    private Coordinate[] toFloor(Coordinate[] coords) {

        Coordinate basis[] = getBasis(coords);
        if (basis == null) {
            System.out.println("Geometry with no normal : is it really a ring ? return null...");
            return null;
        }

        double[][] rotation = {{basis[0].x, basis[0].y, basis[0].z},
                {basis[1].x, basis[1].y, basis[1].z},
                {basis[2].x, basis[2].y, basis[2].z}};
        coords = rotate3D(coords, rotation);
        // Close the ring
        coords[coords.length - 1] = (Coordinate) coords[0].clone();
        return coords;
    }


    /**
     * Applies the toFloor transformation on the ring's coordinates.
     *
     * @param ring Linear ring
     * @return ring in x,y,z=cst plan
     */
    private LinearRing toFloor(LinearRing ring) {

        Coordinate[] coords = ring.getCoordinates();
        coords = toFloor(coords);
        GeometryFactory factory = new GeometryFactory();
        return (factory.createLinearRing(coords));
    }

    /**
     * Takes an array of COPLANAR rings (forming a polygon) and applies
     * the same toFloor transformation to each of them.
     *
     * @param rings Array of coplanar rings
     * @return same rings in x,y plan
     */
    private LinearRing[] toFloor(LinearRing[] rings) {

        LinearRing[] flatRings = new LinearRing[rings.length];
        int numPoints = 0;
        for (LinearRing ring : rings) {
            numPoints += ring.getNumPoints();
        }

        // Concatenate rings in a coordinate sequence
        Coordinate[] sequence = new Coordinate[numPoints];
        int pointCount = 0;
        for (LinearRing ring : rings) {
            Coordinate[] coordinates = ring.getCoordinates();
            System.arraycopy(coordinates, 0, sequence, pointCount, ring.getNumPoints());
            pointCount += ring.getNumPoints();
        }

        // Apply transform
        sequence = toFloor(sequence);
        GeometryFactory factory = new GeometryFactory();

        // Break the sequence to create new rings
        pointCount = 0;
        for (int i = 0; i < rings.length; i++) {
            Coordinate[] coords = new Coordinate[rings[i].getNumPoints()];
            for (int j = 0; j < rings[i].getNumPoints(); j++) {
                coords[j] = sequence[pointCount + j];
            }
            // Get rid of approximations : close the ring
            coords[coords.length - 1] = coords[0];

            flatRings[i] = factory.createLinearRing(coords);
            pointCount += rings[i].getNumPoints();
        }

        return (flatRings);
    }
}