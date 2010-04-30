/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.graphic;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

/**
 *
 * @author maxence
 */
public enum WellKnownName implements MarkGraphicSource {
    SQUARE, CIRCLE, HALFCIRCLE;


    /**
     *
     * @param viewBox
     * @param ds
     * @param fid
     * @return
     * @throws ParameterException
     */
    @Override
    public Shape getShape(ViewBox viewBox, DataSource ds, int fid) throws ParameterException {

        RealParameter width = viewBox.getWidth();
        RealParameter height = viewBox.getHeight();

        double x, y;

        if (width != null && height != null){
            x = width.getValue(ds, fid);
            y = width.getValue(ds, fid);
        }
        else if (width != null){
            x = width.getValue(ds, fid);
            y = x;
        }
        else if(height != null){
            y = width.getValue(ds, fid);
            x = y;
        }
        else{ // nothing is defined => 10x10uom
            x = 10.0;
            y = 10.0;
        }

        x = Uom.toPixel(x, viewBox.getUom(), 96, 25000); // TODO DPI SCAPE !
        y = Uom.toPixel(y, viewBox.getUom(), 96, 25000);

        double hx = x / 2.0;
        double hy = y / 2.0;

        switch (this.valueOf(this.name())){
            // TODO Implement other well known name !
            case HALFCIRCLE:
                return new Arc2D.Double(-hx, -hy, x, y, 0, 180, Arc2D.PIE);
            case CIRCLE:
                return new Arc2D.Double(-hx, -hy, x, y, 0, 360, Arc2D.CHORD);
            case SQUARE:
            default:
                return new Rectangle2D.Double(-hx, -hy, x, y);
        }
    }
}
