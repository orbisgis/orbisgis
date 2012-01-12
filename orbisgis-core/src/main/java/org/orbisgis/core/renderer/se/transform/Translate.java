/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.transform;

import java.awt.geom.AffineTransform;
import java.util.HashSet;
import javax.xml.bind.JAXBElement;
import org.gdms.data.DataSource;
import org.orbisgis.core.map.MapTransform;
import net.opengis.se._2_0.core.ObjectFactory;
import net.opengis.se._2_0.core.TranslateType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

/**
 * Represents a translation in an euclidean plane. As it can be represented with
 * a 2D vector, it is defined by two <code>RealParameter</code>s.
 * @author maxence
 */
public class Translate implements Transformation {

        private RealParameter x;
        private RealParameter y;

        /**
         * Create a new <code>Translate</code>
         * @param x The translation about X-axis
         * @param y The translation about Y-axis
         */
        public Translate(RealParameter x, RealParameter y) {
                setX(x);
                setY(y);
        }

        /**
         * Create an new empty <code>Translate</code>
         */
        public Translate(){
        }

        /**
         * Create a new <code>Translate</code>, using the informations contained in 
         * <code>t</code>
         * @param t
         * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
         */
        public Translate(TranslateType t) throws InvalidStyle {
                if (t.getX() != null) {
                        setX(SeParameterFactory.createRealParameter(t.getX()));
                }
                if (t.getY() != null) {
                        setY(SeParameterFactory.createRealParameter(t.getY()));
                }
        }

        @Override
        public boolean allowedForGeometries() {
                return true;
        }

        @Override
        public AffineTransform getAffineTransform(DataSource sds, long fid, Uom uom, MapTransform mt, Double width100p, Double height100p) throws ParameterException {
                double tx = 0.0;
                if (x != null) {
                        tx = Uom.toPixel(x.getValue(sds, fid), uom, mt.getDpi(), mt.getScaleDenominator(), width100p);
                }

                double ty = 0.0;
                if (y != null) {
                        ty = Uom.toPixel(y.getValue(sds, fid), uom, mt.getDpi(), mt.getScaleDenominator(), height100p);
                }

                return AffineTransform.getTranslateInstance(tx, ty);
        }

        @Override
        public HashSet<String> dependsOnFeature() {
            HashSet<String> result = null;
            if (x != null) {
                result = x.dependsOnFeature();
            }
            if (y != null) {
                if(result == null) {
                    result = y.dependsOnFeature();
                } else {
                    result.addAll(y.dependsOnFeature());
                }
            }
            return result;
    }

        @Override
        public JAXBElement<?> getJAXBElement() {
                TranslateType t = this.getJAXBType();
                ObjectFactory of = new ObjectFactory();
                return of.createTranslate(t);
        }

        @Override
        public TranslateType getJAXBType() {
                TranslateType t = new TranslateType();

                if (x != null) {
                        t.setX(x.getJAXBParameterValueType());
                }

                if (y != null) {
                        t.setY(y.getJAXBParameterValueType());
                }

                return t;
        }

        /**
         * Get the translation about the X-axis
         * @return The translation about the X-axis
         */
        public RealParameter getX() {
                return x;
        }

        /**
         * Get the translation about the Y-axis
         * @return The translation about the Y-axis
         */
        public RealParameter getY() {
                return y;
        }

        /**
         * Set the translation about the Y-axis
         * @param y 
         */
        public final void setY(RealParameter y) {
                this.y = y;
                if (y != null) {
                        y.setContext(RealParameterContext.REAL_CONTEXT);
                }
        }

        /**
         * Set the translation about the X-axis
         * @param y 
         */
        public final void setX(RealParameter x) {
                this.x = x;
                if (x != null) {
                        x.setContext(RealParameterContext.REAL_CONTEXT);
                }
        }

        @Override
        public String toString() {
                return "Translate";
        }
}
