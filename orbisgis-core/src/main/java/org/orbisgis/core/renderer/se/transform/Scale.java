/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.transform;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

/**
 *
 * @author maxence
 */
public class Scale implements Transformation {

    public Scale(RealParameter x, RealParameter y){
        this.x = x;
        this.y = y;
    }
    
    public Scale(RealParameter xy){
        this.x = xy;
        this.y = xy;
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
    public boolean allowedForGeometries(){
        return false;
    }

    @Override
    public AffineTransform getAffineTransform(DataSource ds, int fid, Uom uom) throws ParameterException {
        double sx = 0.0;
        if (x != null)
            sx = Uom.toPixel(x.getValue(ds, fid), uom, 96, 25000);

        double sy = 0.0;
        if (y != null)
            sy = Uom.toPixel(y.getValue(ds, fid), uom, 96, 25000);

        return AffineTransform.getScaleInstance(sx, sy);
    }


    private RealParameter x;
    private RealParameter y;
}
