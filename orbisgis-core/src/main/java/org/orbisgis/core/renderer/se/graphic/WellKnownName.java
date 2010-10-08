/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */

package org.orbisgis.core.renderer.se.graphic;

import java.awt.Dimension;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import org.gdms.data.feature.Feature;

import org.orbisgis.core.renderer.persistance.se.MarkGraphicType;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 * Represent Marks 
 *
 * @author maxence
 */
public enum WellKnownName implements MarkGraphicSource {

    SQUARE, CIRCLE, HALFCIRCLE, TRIANGLE, STAR, CROSS, X;

    public static WellKnownName fromString(String token){
        if (token.equals("SQUARE")){
            return SQUARE;
        }
        else if (token.equals("HALFCIRCLE")){
            return HALFCIRCLE;
        }
        else if (token.equals("TRIANGLE")){
            return TRIANGLE;
        }
        else if (token.equals("STAR")){
            return STAR;
        }
        else if (token.equals("CROSS")){
            return CROSS;
        }
        else if (token.equals("X")){
            return X;
        }
        else{
            return CIRCLE;
        }
    }

    /**
     *
     * @param viewBox
     * @param ds
     * @param fid
     * @return
     * @throws ParameterException
     */
    @Override
    public Shape getShape(ViewBox viewBox, Feature feat, Double scale, Double dpi) throws ParameterException {
        double x=10.0, y=10.0; // The size of the shape, [final unit] => [px]

        if (feat == null && viewBox != null && viewBox.dependsOnFeature()){
            return null;
        }

        if (viewBox != null) {
            Dimension box = viewBox.getDimensionInPixel(feat, 1.0, scale, dpi);
            x = box.getWidth();
            y = box.getHeight();
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

    public void setJAXBSource(MarkGraphicType m){
        m.setWellKnownName(this.toString());
    }
}
