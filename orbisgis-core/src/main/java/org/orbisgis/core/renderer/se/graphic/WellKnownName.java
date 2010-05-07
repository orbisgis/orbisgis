/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.graphic;

import java.awt.Dimension;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 * Represent Marks 
 *
 * @author maxence
 */
public enum WellKnownName implements MarkGraphicSource {

    SQUARE, CIRCLE, HALFCIRCLE, TRIANGLE, STAR, CROSS, X;

    /**
     *
     * @param viewBox
     * @param ds
     * @param fid
     * @return
     * @throws ParameterException
     */
    @Override
    public Shape getShape(ViewBox viewBox, DataSource ds, long fid) throws ParameterException {
        double x, y;

        if (viewBox != null) {
            Dimension box = viewBox.getDimension(ds, fid, 1.0);

            x = box.getWidth();
            y = box.getHeight();;
            
        } else {
            x = 10.0;
            y = 10.0;
        }

        double hx = x / 2.0;
        double hy = y / 2.0;

        switch (this.valueOf(this.name())) {
            // TODO Implement other well known name !
            case HALFCIRCLE:
                return new Arc2D.Double(-hx, -hy, x, y, 90, 180, Arc2D.PIE);
            case CIRCLE:
                return new Arc2D.Double(-hx, -hy, x, y, 0, 360, Arc2D.CHORD);
            case TRIANGLE: {
                int h3 = (int) (x / 3);
                Polygon polygon = new Polygon();
                polygon.addPoint((int) hx, h3);
                polygon.addPoint(0, -2 * h3);
                polygon.addPoint((int) -hx, h3);
                return polygon;
            }
            case STAR: // 5 branches
            {
                double crx = hx * (2.0 / 5.0);
                double cry = hy * (2.0 / 5.0);

                Polygon polygon = new Polygon();

                double cos1 = 0.587785252292472915058851867798;
                double cos2 = 0.951056516295153531181938433292;
                double sin1 = 0.809016994374947562285171898111;
                double sin2 = 0.309016994374947617796323129369;

                // alpha = 270
                polygon.addPoint(0, (int) (-cry + 0.5));

                // alpha = 306
                polygon.addPoint((int) (cos1 * hx + 0.5), (int) (-sin1 * hy + 0.5));

                // alpha = 342
                polygon.addPoint((int) (cos2 * crx + 0.5), (int) (-sin2 * cry + 0.5));

                // alpha = 18
                polygon.addPoint((int) (cos2 * hx + 0.5), (int) (sin2 * hy + 0.5));

                // alpha = 54
                polygon.addPoint((int) (cos1 * crx + 0.5), (int) (sin1 * cry + 0.5));

                // alpha = 90
                polygon.addPoint(0, (int) (hy + 0.5));

                // alpha = 126
                polygon.addPoint((int) (-cos1 * crx + 0.5), (int) (sin1 * cry + 0.5));

                // alpha = 162
                polygon.addPoint((int) (-cos2 * hx + 0.5), (int) (sin2 * hy + 0.5));

                // alpha = 198
                polygon.addPoint((int) (-cos2 * crx + 0.5), (int) (-sin2 * cry + 0.5));

                // alpha = 234
                polygon.addPoint((int) (-cos1 * hx + 0.5), (int) (-sin1 * hy + 0.5));

                return polygon;
            }
            case CROSS:// TODO IMPLEMENT
            case X: // TODO IMPLEMENT
            case SQUARE:
            default:
                return new Rectangle2D.Double(-hx, -hy, x, y);
        }
    }
}
