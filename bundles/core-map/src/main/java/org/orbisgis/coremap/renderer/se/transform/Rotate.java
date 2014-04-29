/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.coremap.renderer.se.transform;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.ObjectFactory;
import net.opengis.se._2_0.core.RotateType;

import org.orbisgis.coremap.map.MapTransform;
import org.orbisgis.coremap.renderer.se.AbstractSymbolizerNode;
import org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.coremap.renderer.se.SymbolizerNode;
import org.orbisgis.coremap.renderer.se.common.Uom;
import org.orbisgis.coremap.renderer.se.parameter.ParameterException;
import org.orbisgis.coremap.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.coremap.renderer.se.parameter.real.RealParameter;
import org.orbisgis.coremap.renderer.se.parameter.real.RealParameterContext;

/**
 * {@code Rotate} is a transformation that performs a rotation of the affected
 * object. It is built using : 
 * <ul><li>The X-coordinate of the rotation center. This value takes place in 
 * the coordinate system of the graphic this {@code Rotate} is used on.</li>
 * <li>The Y-coordinate of the rotation center. This value takes place in 
 * the coordinate system of the graphic this {@code Rotate} is used on.</li>
 * <li>The rotation angle, in clockwise degrees.</li></ul>
 * @author Maxence Laurent
 */
public final class Rotate extends AbstractSymbolizerNode implements Transformation {

        private RealParameter x;
        private RealParameter y;
        private RealParameter rotation;

        /**
         * Build a new {@code Rotate} with angle value set to {@code rotation}.
         * @param rotation 
         */
        public Rotate(RealParameter rotation) {
                setRotation(rotation);
                setX(null);
                setY(null);
        }

        /**
         * Build a new {@code Rotate} with angle value set to {@code rotation}, 
         * and with the rotation center placed at (ox, oy) (in the containing
         * graphic coordinate system).
         * @param rotation
         * @param ox
         * @param oy 
         */
        public Rotate(RealParameter rotation, RealParameter ox, RealParameter oy) {
                setRotation(rotation);
                setX(ox);
                setY(oy);
        }

        /**
         * Build a new {@code Rotate}, retrieving informations in the given
         * {@code RotateType} instance.
         * @param r
         * @throws org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle
         */
        Rotate(RotateType r) throws InvalidStyle {
                if (r.getAngle() != null) {
                        setRotation(SeParameterFactory.createRealParameter(r.getAngle()));
                }

                if (r.getX() != null) {
                        setX(SeParameterFactory.createRealParameter(r.getX()));
                }

                if (r.getY() != null) {
                        setY(SeParameterFactory.createRealParameter(r.getY()));
                }
        }

        /**
         * Get the rotation defined in this {@code Rotate} instance.
         * @return 
         * The rotation, in clockwise degrees. Indeed, as the Y axis is oriented
         * bottom up, it is the direct rotation sense...
         */
        public RealParameter getRotation() {
                return rotation;
        }

        /**
         * Set the rotation defined in this {@code Rotate} instance.
         * @param rotation 
         */
        public void setRotation(RealParameter rotation) {
                this.rotation = rotation;
                if (rotation != null) {
                        rotation.setContext(RealParameterContext.REAL_CONTEXT);
                        this.rotation.setParent(this);
                }
        }

        /**
         * Get the x-coordinate of the rotation center.
         * @return 
         * The x-coordinate as a {@code RealParameter} instance. Note that the 
         * returned coordinate is placed in the coordinate system associated to
         * the graphic this {@code Rotate} operation is applied on.
         */
        public RealParameter getX() {
                return x;
        }

        /**
         * Set the x-coordinate of this {@code Rotate} center.
         * Note that this coordinate is placed in the coordinate system 
         * associated to the graphic this {@code Rotate} operation is applied on.
         * @param x 
         * A {@code RealParameter} that is placed by this method in a 
         * {@link RealParameterContext#REAL_CONTEXT}
         */
        public void setX(RealParameter x) {
                this.x = x;
                if (this.x != null) {
                        this.x.setContext(RealParameterContext.REAL_CONTEXT);
                        this.x.setParent(this);
                }
        }

        /**
         * Get the y-coordinate of the rotation center.
         * @return 
         * The y-coordinate as a {@code RealParameter} instance. Note that the 
         * returned coordinate is placed in the coordinate system associated to
         * the graphic this {@code Rotate} operation is applied on.
         */
        public RealParameter getY() {
                return y;
        }

        /**
         * Set the y-coordinate of this {@code Rotate} center.
         * Note that this coordinate is placed in the coordinate system 
         * associated to the graphic this {@code Rotate} operation is applied on.
         * @param y 
         * A {@code RealParameter} that is placed by this method in a 
         * {@link RealParameterContext#REAL_CONTEXT}
         */
        public void setY(RealParameter y) {
                this.y = y;
                if (this.y != null) {
                        this.y.setContext(RealParameterContext.REAL_CONTEXT);
                        this.y.setParent(this);
                }
        }

        @Override
        public boolean allowedForGeometries() {
                return false;
        }

        @Override
        public List<SymbolizerNode> getChildren() {
                List<SymbolizerNode> ls = new ArrayList<SymbolizerNode>();
                if (x != null) {
                        ls.add(x);
                }
                if (y != null) {
                        ls.add(y);
                }
                if (rotation != null) {
                        ls.add(rotation);
                }
                return ls;
        }

        @Override
        public AffineTransform getAffineTransform(Map<String,Object> map, Uom uom,
                        MapTransform mt, Double width, Double height) throws ParameterException {
                double ox = 0.0;
                if (x != null) {
                        ox = Uom.toPixel(x.getValue(map), uom, mt.getDpi(), mt.getScaleDenominator(), width);
                }

                double oy = 0.0;
                if (y != null) {
                        oy = Uom.toPixel(y.getValue(map), uom, mt.getDpi(), mt.getScaleDenominator(), height);
                }

                double theta = 0.0;
                if (rotation != null) {
                        theta = rotation.getValue(map) * Math.PI / 180.0; // convert to rad
                }
                return AffineTransform.getRotateInstance(theta, ox, oy);
        }

        @Override
        public JAXBElement<?> getJAXBElement() {
                RotateType r = this.getJAXBType();
                ObjectFactory of = new ObjectFactory();
                return of.createRotate(r);
        }

        @Override
        public RotateType getJAXBType() {
                RotateType r = new RotateType();

                if (rotation != null) {
                        r.setAngle(rotation.getJAXBParameterValueType());
                }

                if (x != null) {
                        r.setX(x.getJAXBParameterValueType());
                }

                if (y != null) {
                        r.setY(y.getJAXBParameterValueType());
                }

                return r;
        }

        @Override
        public String toString() {
                return "Rotate";
        }
}
