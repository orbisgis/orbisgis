/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.transform;

import java.awt.geom.AffineTransform;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.map.MapTransform;
import net.opengis.se._2_0.core.ObjectFactory;
import net.opengis.se._2_0.core.RotateType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

/**
 * {@code Rotate} is a transformation that performs a rotation of the affected
 * object. It is built using : 
 * <ul><li>The X-coordinate of the rotation center. This value takes place in 
 * the coordinate system of the graphic this {@code Rotate} is used on.</li>
 * <li>The Y-coordinate of the rotation center. This value takes place in 
 * the coordinate system of the graphic this {@code Rotate} is used on.</li>
 * <li>The rotation angle, in clockwise degrees.</li></ul>
 * @author maxence
 */
public final class Rotate implements Transformation {

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
         * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
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
                }
        }

        @Override
        public boolean allowedForGeometries() {
                return false;
        }

        @Override
        public String dependsOnFeature() {
                String result = "";
                if (x != null) {
                        result = x.dependsOnFeature();
                }
                if (y != null) {
                        result += " " + y.dependsOnFeature();
                }

                if (rotation != null) {
                        result += " " + rotation.dependsOnFeature();
                }

                return result.trim();
        }

        @Override
        public AffineTransform getAffineTransform(SpatialDataSourceDecorator sds, long fid, Uom uom, MapTransform mt, Double width, Double height) throws ParameterException {
                double ox = 0.0;
                if (x != null) {
                        ox = Uom.toPixel(x.getValue(sds, fid), uom, mt.getDpi(), mt.getScaleDenominator(), width);
                }

                double oy = 0.0;
                if (y != null) {
                        oy = Uom.toPixel(y.getValue(sds, fid), uom, mt.getDpi(), mt.getScaleDenominator(), height);
                }

                double theta = 0.0;
                if (rotation != null) {
                        theta = rotation.getValue(sds, fid) * Math.PI / 180.0; // convert to rad
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
