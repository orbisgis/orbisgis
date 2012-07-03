/**
 * TANATO  is a library dedicated to the modelling of water pathways based on 
 * triangulate irregular network. TANATO takes into account anthropogenic and 
 * natural artifacts to evaluate their impacts on the watershed response. 
 * It ables to compute watershed, main slope directions and water flow pathways.
 * 
 * This library has been originally created  by Erwan Bocher during his thesis 
 * “Impacts des activités humaines sur le parcours des écoulements de surface dans 
 * un bassin versant bocager : essai de modélisation spatiale. Application au 
 * Bassin versant du Jaudy-Guindy-Bizien (France)”. It has been funded by the 
 * Bassin versant du Jaudy-Guindy-Bizien and Syndicat d’Eau du Trégor.
 * 
 * The new version is developed at French IRSTV institut as part of the 
 * AvuPur project, funded by the French Agence Nationale de la Recherche 
 * (ANR) under contract ANR-07-VULN-01.
 * 
 * TANATO is distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2010-2012 IRSTV FR CNRS 2488
 * 
 * TANATO is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * TANATO is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * TANATO. If not, see <http://www.gnu.org/licenses/>.
 * 
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.gdms.sql.function.spatial.tin.create;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import org.jdelaunay.delaunay.ConstrainedMesh;
import org.jdelaunay.delaunay.error.DelaunayError;
import org.jdelaunay.delaunay.geometries.DEdge;
import org.jdelaunay.delaunay.geometries.DPoint;
import org.jdelaunay.delaunay.geometries.DTriangle;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DataSet;
import org.gdms.driver.DiskBufferDriver;
import org.gdms.driver.DriverException;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.spatial.tin.model.TINMetadataFactory;

/**
 *
 * @author Erwan Bocher, Alexis Guéganno
 */
public class TinBuilder {

        private final DataSet ds;
        private boolean intersection = true;
        private boolean flatTriangles = false;
        private ConstrainedMesh mesh;

        public TinBuilder(DataSet ds) {
                this.ds = ds;
        }

        public void build() throws DriverException, FunctionException {
                int geomFieldIndex = ds.getSpatialFieldIndex();
                if (geomFieldIndex != -1) {
                        Geometry geom;
                        long count = ds.getRowCount();
                        //We prepare our input structures.
                        List<DPoint> pointsToAdd = new ArrayList<DPoint>();
                        ArrayList<DEdge> edges = new ArrayList<DEdge>();
                        //We fill the input structures with our table.
                        for (long i = 0; i < count; i++) {

                                geom = ds.getGeometry(i, geomFieldIndex);

                                if (geom instanceof Point) {
                                        addPoint(pointsToAdd, (Point) geom);
                                } else if (geom instanceof MultiPoint) {
                                        addMultiPoint(pointsToAdd, (MultiPoint) geom);
                                } else if (geom instanceof GeometryCollection) {
                                        addGeometryCollection(edges, (GeometryCollection) geom);
                                } else {
                                        addGeometry(edges, geom);
                                }
                        }
                        //We have filled the input of our mesh. We can close our source.
                        Collections.sort(edges);

                        //Build triangulation                        
                        mesh = new ConstrainedMesh();
                        mesh.setVerbose(true);
                        try {
                                //We actually fill the mesh
                                mesh.setPoints(pointsToAdd);
                                mesh.setConstraintEdges(edges);
                                if (isIntersection()) {
                                        //If needed, we use the intersection algorithm
                                        mesh.forceConstraintIntegrity();
                                }
                                //we process delaunay
                                mesh.processDelaunay();
                                if (isFlatTriangles()) {
                                        //If needed, we remove flat triangles.
                                        mesh.removeFlatTriangles();
                                }
                        } catch (DelaunayError ex) {
                                throw new FunctionException("Generation of the mesh failed.\n", ex);
                        }

                } else {
                        throw new FunctionException("The datasource must contain a geometry field.\n");
                }
        }

        public boolean isFlatTriangles() {
                return flatTriangles;
        }

        public boolean isIntersection() {
                return intersection;
        }

        public void setFlatTriangles(boolean flatTriangles) {
                this.flatTriangles = flatTriangles;
        }

        public void setIntersection(boolean intersection) {
                this.intersection = intersection;
        }

        /**
         * We add a point to the given list
         * @param points
         * @param geom
         * @throws FunctionException
         */
        private void addPoint(List<DPoint> points, Point geom) throws FunctionException {
                Coordinate pt = geom.getCoordinate();
                double z = Double.isNaN(pt.z) ? 0 : pt.z;
                try {
                        points.add(new DPoint(pt.x, pt.y, z));
                } catch (DelaunayError ex) {
                        throw new FunctionException("You're trying to create a 3D point with a NaN value.\n", ex);
                }

        }

        /**
         * Add a MultiPoint geometry.
         * @param points
         * @param pts
         * @throws FunctionException
         */
        private void addMultiPoint(List<DPoint> points, MultiPoint pts) throws FunctionException {
                Coordinate[] coords = pts.getCoordinates();
                for (Coordinate coordinate : coords) {
                        try {
                                points.add(new DPoint(
                                        coordinate.x,
                                        coordinate.y,
                                        Double.isNaN(coordinate.z) ? 0 : coordinate.z));
                        } catch (DelaunayError ex) {
                                throw new FunctionException("You're trying to create a 3D point with a NaN value.\n", ex);
                        }
                }
        }

        /**
         * add a geometry to the input.
         * @param edges
         * @param geom
         * @throws FunctionException
         */
        private void addGeometry(List<DEdge> edges, Geometry geom) throws FunctionException {
                if (geom.isValid()) {
                        Coordinate[] coords = geom.getCoordinates();
                        Coordinate c1 = coords[0];
                        c1.z = Double.isNaN(c1.z) ? 0 : c1.z;
                        Coordinate c2;
                        for (int k = 1; k < coords.length; k++) {
                                c2 = coords[k];
                                c2.z = Double.isNaN(c2.z) ? 0 : c2.z;
                                try {
                                        edges.add(new DEdge(new DPoint(c1), new DPoint(c2)));
                                } catch (DelaunayError d) {
                                        throw new FunctionException("You're trying to create a 3D point with a NaN value.\n", d);
                                }
                                c1 = c2;
                        }
                }
        }

        /**
         * Add a GeometryCollection
         * @param edges
         * @param geomcol
         * @throws FunctionException
         */
        private void addGeometryCollection(List<DEdge> edges, GeometryCollection geomcol) throws FunctionException {
                int num = geomcol.getNumGeometries();
                for (int i = 0; i < num; i++) {
                        addGeometry(edges, geomcol.getGeometryN(i));
                }
        }

        public DiskBufferDriver getTriangles(DataSourceFactory dsf) throws DriverException {
                DiskBufferDriver triangles = new DiskBufferDriver(dsf, TINMetadataFactory.createTrianglesMetadata());
                GeometryFactory gf = new GeometryFactory();
                for (DTriangle dt : mesh.getTriangleList()) {
                        Coordinate[] coords = new Coordinate[DTriangle.PT_NB + 1];
                        coords[0] = dt.getPoint(0).getCoordinate();
                        coords[1] = dt.getPoint(1).getCoordinate();
                        coords[2] = dt.getPoint(2).getCoordinate();
                        coords[3] = dt.getPoint(0).getCoordinate();
                        CoordinateSequence cs = new CoordinateArraySequence(coords);
                        LinearRing lr = new LinearRing(cs, gf);
                        Polygon poly = new Polygon(lr, null, gf);
                        triangles.addValues(new Value[]{ValueFactory.createValue(poly),
                                        ValueFactory.createValue(dt.getGID()),
                                        ValueFactory.createValue(dt.getEdge(0).getGID()),
                                        ValueFactory.createValue(dt.getEdge(1).getGID()),
                                        ValueFactory.createValue(dt.getEdge(2).getGID())});
                }
                triangles.writingFinished();
                triangles.close();
                return triangles;
        }

        public DiskBufferDriver getEdges(DataSourceFactory dsf) throws DriverException {

                DiskBufferDriver edges = new DiskBufferDriver(dsf, TINMetadataFactory.createEdgesMetadata());

                GeometryFactory gf = new GeometryFactory();
                for (DEdge dt : mesh.getEdges()) {
                        Coordinate[] coords = new Coordinate[2];
                        coords[0] = dt.getPointLeft().getCoordinate();
                        coords[1] = dt.getPointRight().getCoordinate();
                        CoordinateSequence cs = new CoordinateArraySequence(coords);

                        LineString mp = new LineString(cs, gf);
                        edges.addValues(new Value[]{ValueFactory.createValue(mp),
                                        ValueFactory.createValue(dt.getGID()),
                                        ValueFactory.createValue(dt.getStartPoint().getGID()),
                                        ValueFactory.createValue(dt.getEndPoint().getGID()),
                                        ValueFactory.createValue(dt.getLeft() == null ? -1 : dt.getLeft().getGID()),
                                        ValueFactory.createValue(dt.getRight() == null ? -1 : dt.getRight().getGID())});
                }

                edges.writingFinished();
                edges.close();

                return edges;
        }

        public DiskBufferDriver getPoints(DataSourceFactory dsf) throws DriverException {
                DiskBufferDriver points = new DiskBufferDriver(dsf, TINMetadataFactory.createPointsMetadata());
                GeometryFactory gf = new GeometryFactory();
                for (DPoint dt : mesh.getPoints()) {
                        Coordinate[] coords = new Coordinate[1];
                        coords[0] = dt.getCoordinate();
                        CoordinateSequence cs = new CoordinateArraySequence(coords);

                        Point mp = new Point(cs, gf);

                        points.addValues(new Value[]{ValueFactory.createValue(mp),
                                        ValueFactory.createValue(dt.getGID()),
                                        ValueFactory.createValue(dt.getExternalGID())});
                }

                points.writingFinished();
                points.close();
                return points;
        }

        public void close() {
                mesh = null;
        }
}
