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

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.gdms.data.SpatialDataSourceDecorator;

import org.orbisgis.core.renderer.persistance.se.MarkGraphicType;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

/**
 * Represent Marks 
 *
 * @author maxence
 */
public enum WellKnownName implements MarkGraphicSource {

    SQUARE, CIRCLE, HALFCIRCLE, TRIANGLE, STAR, CROSS, X;

    public static WellKnownName fromString(String token){
        if (token.equalsIgnoreCase("SQUARE")){
            return SQUARE;
        }
        else if (token.equalsIgnoreCase("HALFCIRCLE")){
            return HALFCIRCLE;
        }
        else if (token.equalsIgnoreCase("TRIANGLE")){
            return TRIANGLE;
        }
        else if (token.equalsIgnoreCase("STAR")){
            return STAR;
        }
        else if (token.equalsIgnoreCase("CROSS")){
            return CROSS;
        }
        else if (token.equalsIgnoreCase("X")){
            return X;
        }
        else{
            return CIRCLE;
        }
    }

    @Override
    public Shape getShape(ViewBox viewBox, SpatialDataSourceDecorator sds, 
            long fid, Double scale, Double dpi, RealParameter markIndex,
            String mimeType) throws ParameterException {
        if (sds == null && viewBox != null && !viewBox.dependsOnFeature().isEmpty()){
            return null;
        }

        double x=10.0, y=10.0; // The size of the shape, [final unit] => [px]

        if (viewBox != null && viewBox.usable()) {
            Point2D box = viewBox.getDimensionInPixel(sds, fid, MarkGraphic.defaultSize, MarkGraphic.defaultSize, scale, dpi);
            x = box.getX();
            y = box.getY();
        }

        int x2 = (int)(x / 2.0);
        int y2 = (int)(y / 2.0);
		int minxy6 = (int)Math.min(x/6, y/6);

        switch (this.valueOf(this.name())) {
            // TODO Implement other well known name !
            case HALFCIRCLE:
                Path2D.Double p = new Path2D.Double();
                p.moveTo(0, 0);
                Arc2D.Double arc = new Arc2D.Double(-x2, -y2, x, y, -90, -180, Arc2D.CHORD);
                p.append(arc, true);
                p.moveTo(0, 0);
                p.closePath();
                return p;
            case CIRCLE:
                return new Arc2D.Double(-x2, -y2, x, y, 0, -360, Arc2D.CHORD);
            case TRIANGLE: {
                int h3 = (int) (y / 3);
                Polygon polygon = new Polygon();
                polygon.addPoint((int) x2, h3);
                polygon.addPoint((int) -x2, h3);
                polygon.addPoint(0, -2 * h3);
                polygon.addPoint((int) x2, h3);
                return polygon;
            }
            case STAR: // 5 branches
            {
                double crx = x2 * (2.0 / 5.0);
                double cry = y2 * (2.0 / 5.0);

                Polygon star = new Polygon();

                double cos1 = 0.587785252292472915058851867798;
                double cos2 = 0.951056516295153531181938433292;
                double sin1 = 0.809016994374947562285171898111;
                double sin2 = 0.309016994374947617796323129369;

                star.addPoint(0, (int) (cry - 0.5));
                // alpha = 234
                star.addPoint((int) (-cos1 * x2 + 0.5), (int) (sin1 * y2 - 0.5));

                // alpha = 198
                star.addPoint((int) (-cos2 * crx + 0.5), (int) (sin2 * cry - 0.5));

                // alpha = 162
                star.addPoint((int) (-cos2 * x2 + 0.5), (int) (-sin2 * y2 - 0.5));

                // alpha = 126
                star.addPoint((int) (-cos1 * crx + 0.5), (int) (-sin1 * cry - 0.5));

                // alpha = 90
                star.addPoint(0, (int) (-y2 - 0.5));

                // alpha = 54
                star.addPoint((int) (cos1 * crx + 0.5), (int) (-sin1 * cry - 0.5));

                // alpha = 18
                star.addPoint((int) (cos2 * x2 + 0.5), (int) (-sin2 * y2 - 0.5));

                // alpha = 342
                star.addPoint((int) (cos2 * crx + 0.5), (int) (sin2 * cry - 0.5));

                // alpha = 306
                star.addPoint((int) (cos1 * x2 + 0.5), (int) (sin1 * y2 - 0.5));

                // alpha = 270
                star.addPoint(0, (int) (cry - 0.5));

                return star;
            }
            case CROSS:// TODO IMPLEMENT

                Polygon cross = new Polygon();

				cross.addPoint(-minxy6, -y2);
				cross.addPoint(minxy6, -y2);

				cross.addPoint(minxy6, -minxy6);

				cross.addPoint(x2, -minxy6);
				cross.addPoint(x2, minxy6);

				cross.addPoint(minxy6, minxy6);

				cross.addPoint(minxy6, y2);
				cross.addPoint(-minxy6, y2);

				cross.addPoint(-minxy6, minxy6);

				cross.addPoint(-x2, minxy6);
				cross.addPoint(-x2, -minxy6);

				cross.addPoint(-minxy6, -minxy6);

				cross.addPoint(-minxy6, -y2);

				return cross;

            case X: // TODO IMPLEMENT

                Polygon xShape = new Polygon();

				xShape.addPoint(0, -minxy6);

				xShape.addPoint(x2 - minxy6, - y2);
				xShape.addPoint(x2, - y2 +minxy6);

				xShape.addPoint(minxy6, 0);

				xShape.addPoint(x2, y2 - minxy6);
				xShape.addPoint(x2 - minxy6, y2);

				xShape.addPoint(0, minxy6);

				xShape.addPoint(- x2 + minxy6, y2);
				xShape.addPoint(- x2, y2 - minxy6);

				xShape.addPoint(-minxy6, 0);

				xShape.addPoint(- x2, - y2 +minxy6);
				xShape.addPoint(- x2 + minxy6, - y2);

				xShape.addPoint(0, -minxy6);

				return xShape;
            case SQUARE:
            default:
                return new Rectangle2D.Double(-x2, -y2, x, y);
        }
    }

	@Override
    public void setJAXBSource(MarkGraphicType m){
        m.setWellKnownName(this.toString());
    }
}
