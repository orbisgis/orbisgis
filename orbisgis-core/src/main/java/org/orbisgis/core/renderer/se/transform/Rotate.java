/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.transform;

import java.awt.geom.AffineTransform;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.RotateType;
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
public final class Rotate implements Transformation {

    private RealParameter x;
    private RealParameter y;
    private RealParameter rotation;

    public Rotate(RealParameter rotation) {
        setRotation(rotation);
		setX(null);
		setY(null);
    }

    public Rotate(RealParameter rotation, RealParameter ox, RealParameter oy) {
		setRotation(rotation);
        setX(ox);
        setY(oy);
    }

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

    public RealParameter getRotation() {
        return rotation;
    }

    public void setRotation(RealParameter rotation) {
        this.rotation = rotation;
		if (rotation != null){
			rotation.setContext(RealParameterContext.realContext);
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
    public boolean dependsOnFeature() {
        if (this.x != null && x.dependsOnFeature()) {
            return true;
        }
        if (this.y != null && y.dependsOnFeature()) {
            return true;
        }
        if (this.rotation != null && rotation.dependsOnFeature()) {
            return true;
        }
        return false;
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
}
