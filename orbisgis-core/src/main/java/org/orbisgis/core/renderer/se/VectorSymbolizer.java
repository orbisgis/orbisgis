/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.renderer.se;

import com.vividsolutions.jts.geom.*;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.SymbolizerType;
import org.apache.log4j.Logger;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.geometry.GeometryAttribute;
import org.orbisgis.sputilities.SpatialResultSet;

/**
 * This class contains the common elements shared by <code>PointSymbolizer</code>,<code>LineSymbolizer</code>
 * ,<code>AreaSymbolizer</code> and <code>TextSymbolizer</code>. Those vector layers all contains 
 * the elements defined in <code>Symbolizer</code>, and :
 * <ul>
 *  <li> - a unit of measure (Uom)</li>
 *  <li> - an affine transformation def (transform)</li>
 * </ul>
 *
 * @author Maxence Laurent, Alexis Guéganno
 */
public abstract class VectorSymbolizer extends Symbolizer implements UomNode {
        private static final Logger LOGGER = Logger.getLogger(VectorSymbolizer.class);
        private Uom uom;
        private GeometryAttribute theGeom;

        /**
         * Default constructor for this abstract class. Only set the inner unit of
         * measure to {@code Uom.MM}.
         */
        protected VectorSymbolizer() {
                setUom(Uom.PX);
        }

        /**
         * Build a VectorSymbolizer from the inpur JAXB type.
         * @param st
         * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
         */
        protected VectorSymbolizer(JAXBElement<? extends SymbolizerType> st) throws InvalidStyle {
                super(st);
        }

        /**
         * Get the name of the column where the geometry data will be retrieved.
         * @return 
         */
        public final GeometryAttribute getGeometryAttribute() {
                return theGeom;
        }

        /**
         * Set the name of the column where the geometry data will be retrieved.
         * @param theGeom 
         */
        public final void setGeometryAttribute(GeometryAttribute theGeom) {
                this.theGeom = theGeom;
        }

        /**
         * Get the {@code Geometry} stored in {@code sds} at index {@code fid}.
         * @param rs
         * @param fid
         * @return
         * @throws ParameterException
         * @throws SQLException
         */
        public Geometry getGeometry(ResultSet rs, Long fid) throws ParameterException, SQLException {
                if (theGeom != null) {
                        return theGeom.getTheGeom(rs, fid);
                } else {
                        return rs.unwrap(SpatialResultSet.class).getGeometry();
                }
        }

        /**
         * If {@code theGeom} is null, get the {@code Geometry} stored in 
         * {@code sds} at index {@code fid}. Otherwise, return {@code theGeom}.
         * @param rs
         * @param fid
         * @return
         * @throws ParameterException
         * @throws SQLException
         */
        public Geometry getGeometry(ResultSet rs, Long fid, Geometry theGeom) throws ParameterException, SQLException {
                if (theGeom == null) {
                        return this.getGeometry(rs, fid);
                } else {
                        return theGeom;
                }
        }

        /**
         * Convert a spatial feature into a LiteShape, should add parameters to handle
         * the scale and to perform a scale dependent generalization !
         *
         * @param rs the data source
         * @param fid the feature id
         * @throws ParameterException
         * @throws IOException
         * @throws SQLException
         */
        public Shape getShape(ResultSet rs, long fid,
                MapTransform mt, Geometry theGeom, boolean generalize) throws ParameterException, IOException, SQLException {

                Geometry geom = getGeometry(rs, fid, theGeom);
                //ArrayList<Shape> shapes = new ArrayList<Shape>();

                //shapes.add();
                /* ArrayList<Geometry> geom2Process = new ArrayList<Geometry>();
                
                geom2Process.add(geom);
                
                while (!geom2Process.isEmpty()) {
                geom = geom2Process.remove(0);
                if (geom != null) {
                if (geom instanceof GeometryCollection) {
                int numGeom = geom.getNumGeometries();
                for (int i = 0; i < numGeom; i++) {
                geom2Process.add(geom.getGeometryN(i));
                }
                } else {
                Shape shape = mt.getShape(geom,generalize);
                if (shape != null) {
                shapes.add(shape);
                }
                }
                }
                }*/

                return mt.getShape(geom, generalize);
        }

        /**
         * Convert a spatial feature into a set of linear shape
         *
         * @param rs the data source
         * @param fid the feature id
         * @throws ParameterException
         * @throws IOException
         * @throws SQLException
         */
        public List<Shape> getLines(ResultSet rs, long fid,
                MapTransform mt, Geometry the_geom) throws ParameterException, IOException, SQLException {
                Geometry geom = getGeometry(rs, fid, the_geom);
                LinkedList<Shape> shapes = new LinkedList<Shape>();
                LinkedList<Geometry> geom2Process = new LinkedList<Geometry>();
                geom2Process.add(geom);
                AffineTransform at = null;
                while (!geom2Process.isEmpty()) {
                        geom = geom2Process.remove(0);

                        if (geom != null) {

                                if (geom instanceof GeometryCollection) {
                                        // Uncollectionize
                                        int numGeom = geom.getNumGeometries();
                                        for (int i = 0; i < numGeom; i++) {
                                                geom2Process.add(geom.getGeometryN(i));
                                        }
                                } else if (geom instanceof Polygon) {
                                        // Separate exterior and interior holes
                                        Polygon p = (Polygon) geom;

                                        shapes.add(mt.getShape(geom, true));

                                        Shape shape = mt.getShape(p.getExteriorRing(), true);
                                        if (shape != null) {
                                                if (at != null) {
                                                        shape = at.createTransformedShape(shape);
                                                }
                                                shapes.add(shape);
                                        }
                                        int i;
                                        // Be aware of polygon holes !
                                        int numRing = p.getNumInteriorRing();
                                        for (i = 0; i < numRing; i++) {
                                                shape = mt.getShape(p.getInteriorRingN(i), true);
                                                if (shape != null) {
                                                        if (at != null) {
                                                                shape = at.createTransformedShape(shape);
                                                        }
                                                        shapes.add(shape);
                                                }
                                        }
                                } else {
                                        Shape shape = mt.getShape(geom, false);

                                        if (shape != null) {
                                                if (at != null) {
                                                        shape = at.createTransformedShape(shape);
                                                }
                                                shapes.add(shape);
                                        }
                                }
                        }
                }

                return shapes;
        }

        /**
         * Return one point for each geometry
         *
         * @param rs
         * @param fid
         * @param mt
         * @return
         * @throws ParameterException
         * @throws IOException
         * @throws SQLException
         */
        public Point2D getPointShape(ResultSet rs, long fid, MapTransform mt, Geometry theGeom)
                        throws ParameterException, IOException, SQLException {

                Geometry geom = getGeometry(rs, fid, theGeom);
                AffineTransform at = mt.getAffineTransform();
                Point point;

                try {
                        point = geom.getInteriorPoint();
                } catch (TopologyException ex) {
                        LOGGER.error("getPointShape :: TopologyException: ", ex);
                        point = geom.getCentroid();
                }
                return at.transform(new Point2D.Double(point.getX(), point.getY()), null);
        }

        /**
         * Return only the first point
         * @param rs
         * @param fid
         * @param mt
         * @return
         * @throws ParameterException
         * @throws IOException
         * @throws SQLException
         */
        public Point2D getFirstPointShape(ResultSet rs, long fid, MapTransform mt,
                Geometry theGeom) throws ParameterException, IOException, SQLException {

                Geometry geom = getGeometry(rs, fid, theGeom);
                AffineTransform at = mt.getAffineTransform();

                Coordinate[] coordinates = geom.getCoordinates();

                return at.transform(new Point2D.Double(coordinates[0].x, coordinates[0].y), null);
        }

        /**
         * Return all vertices of the geometry
         *
         * @param rs
         * @param fid
         * @param mt
         * @param theGeom
         * @return
         * @throws ParameterException
         * @throws IOException
         * @throws SQLException
         */
        public List<Point2D> getPoints(ResultSet rs, long fid,
                MapTransform mt, Geometry theGeom) throws ParameterException, IOException, SQLException {

                Geometry geom = getGeometry(rs, fid, theGeom);
                //geom = ShapeHelper.clipToExtent(geom, mt.getAdjustedExtent());

                LinkedList<Point2D> points = new LinkedList<Point2D>();

                AffineTransform at = mt.getAffineTransform();

                Coordinate[] coordinates = geom.getCoordinates();


                for (Coordinate coord : coordinates) {
                        points.add(at.transform(new Point2D.Double(coord.x, coord.y), null));
                }

                return points;
        }

        @Override
        public final Uom getUom() {
                return uom == null ? Uom.PX : uom;
        }

        @Override
        public final Uom getOwnUom() {
                return uom;
        }

        @Override
        public final void setUom(Uom uom) {
                if (uom != null) {
                        this.uom = uom;
                } else {
                        this.uom = Uom.PX;
                }
        }
}
