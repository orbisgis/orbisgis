/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.transform;

import java.awt.geom.AffineTransform;
import java.util.HashSet;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.ObjectFactory;
import net.opengis.se._2_0.core.ScaleType;
import org.gdms.data.DataSource;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

/**
 * {@code Scale} is used to apply an homothetic transformation on a Graphic. It
 * depends on the following parameter :
 * <ul><li>X : The horizontal multiplication factor</li>
 * <li>Y : The vertical multiplication factor</li></ul>
 *
 * @author maxence, alexis
 */
public final class Scale implements Transformation {

    private RealParameter x;
    private RealParameter y;

    /**
     * Build a new {@code Scale} with the given horizontal and vertical factors.
     * @param x The horizontal factor.
     * @param y The vertical factor.
     */
    public Scale(RealParameter x, RealParameter y) {
        setX(x);
        setY(y);
    }

    /**
     * Build a new {@code Scale} with the given factor, that will be used for
     * both vertical and horizontal values.
     * @param xy
     */
    public Scale(RealParameter xy) {
        setX(xy);
        setY(xy);
    }

    /**
     * Build a new {@code Scale} with the given JAXB {@code ScaleType}.
     * @param s
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle
     */
    Scale(ScaleType s) throws InvalidStyle {
        /*if (s.getXY() != null) {
        setX(SeParameterFactory.createRealParameter(s.getXY()));
        setY(SeParameterFactory.createRealParameter(s.getXY()));
        } else {*/
        if (s.getX() != null) {
            setX(SeParameterFactory.createRealParameter(s.getX()));
        }
        if (s.getY() != null) {
            setY(SeParameterFactory.createRealParameter(s.getY()));
        }
        //}
    }

    /**
     * Get the horizontal multiplication factor.
     * @return
     * A {@code RealParameter} in a {@link RealParameterContext#REAL_CONTEXT}.
     */
    public RealParameter getX() {
        return x;
    }

    /**
     * Set the horizontal multiplication factor.
     * @param x
     * A {@code RealParameter} that is placed by this method in a
     * {@link RealParameterContext#REAL_CONTEXT}.
     */
    public void setX(RealParameter x) {
        this.x = x;
        if (this.x != null) {
            this.x.setContext(RealParameterContext.REAL_CONTEXT);
        }
    }

    /**
     * Get the vertical multiplication factor.
     * @return
     * A {@code RealParameter} in a {@link RealParameterContext#REAL_CONTEXT}.
     */
    public RealParameter getY() {
        return y;
    }

    /**
     * Set the vertical multiplication factor.
     * @param y
     * A {@code RealParameter} that is placed by this method in a
     * {@link RealParameterContext#REAL_CONTEXT}.
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
    public HashSet<String> dependsOnFeature() {
        HashSet<String> result = null;
        if (x != null) {
            result = x.dependsOnFeature();
        }
        if (y != null) {
            if(result == null){
            result = y.dependsOnFeature();
            } else {
                result.addAll(y.dependsOnFeature());
                
            }
        }
        return result;
    }


    @Override
    public AffineTransform getAffineTransform(DataSource sds, long fid, Uom uom, MapTransform mt, Double width, Double height) throws ParameterException {
        double sx = 1.0;
        if (x != null) {
            //sx = Uom.toPixel(x.getValue(feat), uom, mt.getDpi(), mt.getScaleDenominator(), null);
            sx = x.getValue(sds, fid);
        }

        double sy = 1.0;
        if (y != null) {
            //sy = Uom.toPixel(y.getValue(feat), uom, mt.getDpi(), mt.getScaleDenominator(), null);
            sy = y.getValue(sds, fid);
        }

        //AffineTransform.getTranslateInstance(A;, sy);

        return AffineTransform.getScaleInstance(sx, sy);
    }


    @Override
    public JAXBElement<?> getJAXBElement() {
        ScaleType s = this.getJAXBType();

        ObjectFactory of = new ObjectFactory();
        return of.createScale(s);
    }


    @Override
    public ScaleType getJAXBType() {
        ScaleType s = new ScaleType();

        if (x != null) {
            s.setX(x.getJAXBParameterValueType());
        }

        if (y != null) {
            s.setY(y.getJAXBParameterValueType());
        }
        return s;
    }


    @Override
    public String toString() {
        return "Scale";
    }



}
