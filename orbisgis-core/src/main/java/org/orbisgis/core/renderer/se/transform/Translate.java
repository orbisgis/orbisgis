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
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

/**
 *
 * @author maxence
 */
public class Translate implements Transformation {

    public Translate(RealParameter x, RealParameter y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean allowedForGeometries() {
        return true;
    }

    @Override
    public AffineTransform getAffineTransform(DataSource ds, int fid, Uom uom) throws ParameterException {
        double tx = 0.0;
        if (x != null) {
            tx = Uom.toPixel(x.getValue(ds, fid), uom, 96, 25000);
        }

        double ty = 0.0;
        if (y != null) {
            ty = Uom.toPixel(y.getValue(ds, fid), uom, 96, 25000);
        }

        return AffineTransform.getTranslateInstance(tx, ty);
    }
    private RealParameter x;
    private RealParameter y;
}
