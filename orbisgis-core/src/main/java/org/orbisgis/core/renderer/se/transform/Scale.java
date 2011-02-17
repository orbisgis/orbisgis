/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.transform;

import java.awt.geom.AffineTransform;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.ScaleType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

/**
 *
 * @author maxence
 */
public final class Scale implements Transformation {

    public Scale(RealParameter x, RealParameter y) {
		setX(x);
		setY(y);
    }

    public Scale(RealParameter xy) {
        setX(xy);
        setY(xy);
    }

    Scale(ScaleType s) throws InvalidStyle {
        if (s.getXY() != null) {
            setX(SeParameterFactory.createRealParameter(s.getXY()));
            setY(SeParameterFactory.createRealParameter(s.getXY()));
        } else {
            if (s.getX() != null) {
                setX(SeParameterFactory.createRealParameter(s.getX()));
            }
            if (s.getY() != null) {
                setY(SeParameterFactory.createRealParameter(s.getY()));
            }
        }
    }

    public RealParameter getX() {
        return x;
    }

    public void setX(RealParameter x) {
        this.x = x;
		if (this.x != null){
			this.x.setContext(RealParameterContext.realContext);
		}
    }

    public RealParameter getY() {
        return y;
    }

    public void setY(RealParameter y) {
        this.y = y;
		if (this.y != null){
			this.y.setContext(RealParameterContext.realContext);
		}
    }

    @Override
    public boolean allowedForGeometries() {
        return false;
    }

    
    @Override
    public String dependsOnFeature(){
        String result = "";
        if (x!= null){
            result += x.dependsOnFeature();
        }
        if (y != null){
            result += " " + y.dependsOnFeature();
        }
        return result.trim();
    }

    @Override
    public AffineTransform getAffineTransform(SpatialDataSourceDecorator sds, long fid, Uom uom, MapTransform mt, Double width, Double height) throws ParameterException {
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

        if (y == null ^ x == null) {
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
