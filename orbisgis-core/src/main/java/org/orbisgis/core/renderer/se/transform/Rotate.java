/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.transform;

import java.awt.geom.AffineTransform;
import javax.xml.bind.JAXBElement;
import org.gdms.data.DataSource;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.RotateType;
import org.orbisgis.core.renderer.se.common.MapEnv;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

/**
 *
 * @author maxence
 */
public class Rotate implements Transformation {

    public Rotate(RealParameter rotation) {
        this.rotation = rotation;
        this.x = null;
        this.y = null;
    }

    public Rotate(RealParameter rotation, RealParameter ox, RealParameter oy) {
        this.rotation = rotation;
        this.x = ox;
        this.y = oy;
    }

    Rotate(RotateType r) {
        if (r.getAngle() != null) {
            this.rotation = SeParameterFactory.createRealParameter(r.getAngle());
        }

        if (r.getX() != null) {
            this.x = SeParameterFactory.createRealParameter(r.getX());
        }

        if (r.getY() != null) {
            this.y = SeParameterFactory.createRealParameter(r.getY());
        }
    }

    public RealParameter getRotation() {
        return rotation;
    }

    public void setRotation(RealParameter rotation) {
        this.rotation = rotation;
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
    public AffineTransform getAffineTransform(Feature feat, Uom uom) throws ParameterException {
        double ox = 0.0;

        if (x != null) {
            ox = Uom.toPixel(x.getValue(feat), uom, MapEnv.getScaleDenominator());


        }
        double oy = 0.0;
        if (y != null) {
            oy = Uom.toPixel(y.getValue(feat), uom, MapEnv.getScaleDenominator());



        }
        double theta = 0.0;
        if (rotation != null) {
            theta = rotation.getValue(feat) * Math.PI / 180.0; // convert to rad


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
    private RealParameter x;
    private RealParameter y;
    private RealParameter rotation;
}
