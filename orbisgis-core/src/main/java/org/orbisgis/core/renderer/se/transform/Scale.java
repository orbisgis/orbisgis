/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.transform;

import java.awt.geom.AffineTransform;
import javax.xml.bind.JAXBElement;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.ScaleType;
import org.orbisgis.core.renderer.se.common.MapEnv;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

/**
 *
 * @author maxence
 */
public class Scale implements Transformation {

    public Scale(RealParameter x, RealParameter y) {
        this.x = x;
        this.y = y;
    }

    public Scale(RealParameter xy) {
        this.x = xy;
        this.y = xy;
    }

    Scale(ScaleType s) {
        if (s.getXY() != null) {
            this.x = SeParameterFactory.createRealParameter(s.getXY());
            this.y = SeParameterFactory.createRealParameter(s.getXY());
        } else {
            if (s.getX() != null) {
                this.x = SeParameterFactory.createRealParameter(s.getX());
            }
            if (s.getY() != null) {
                this.y = SeParameterFactory.createRealParameter(s.getY());
            }
        }
    }

    public RealParameter getX() {
        return x;
    }

    public void setX(RealParameter x) {
        this.x = x;
    }

    public RealParameter getY() {
        return y;
    }

    public void setY(RealParameter y) {
        this.y = y;
    }

    @Override
    public boolean allowedForGeometries() {
        return false;
    }

    @Override
    public AffineTransform getAffineTransform(DataSource ds, long fid, Uom uom) throws ParameterException {
        double sx = 0.0;
        if (x != null) {
            sx = Uom.toPixel(x.getValue(ds, fid), uom, MapEnv.getScaleDenominator());
        }

        double sy = 0.0;
        if (y != null) {
            sy = Uom.toPixel(y.getValue(ds, fid), uom, MapEnv.getScaleDenominator());
        }

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

        if (y == null || x == null) {
            RealParameter xy;
            if (x != null) {
                xy = x;
            } else {
                xy = y;
            }
            s.setXY(xy.getJAXBParameterValueType());
        } else {
            if (x != null) {
                s.setX(x.getJAXBParameterValueType());
            }

            if (y != null) {
                s.setY(y.getJAXBParameterValueType());
            }
        }
        return s;
    }
    private RealParameter x;
    private RealParameter y;
}
