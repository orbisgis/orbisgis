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
package org.orbisgis.core.renderer.se.fill;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.persistance.se.FillType;
import org.orbisgis.core.renderer.persistance.se.HatchedFillType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.ShapeHelper;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.core.ui.editors.map.tool.Rectangle2DDouble;

/**
 *
 * @author maxence
 */
public final class HatchedFill extends Fill {

    private RealParameter angle;
    private RealParameter distance;
    private RealParameter offset;
    private Stroke stroke;

    public HatchedFill(JAXBElement<HatchedFillType> sf) throws InvalidStyle {
        if (sf.getValue().getAngle() != null) {
            setAngle(SeParameterFactory.createRealParameter(sf.getValue().getAngle()));
        }
        if (sf.getValue().getDistance() != null) {
            setDistance(SeParameterFactory.createRealParameter(sf.getValue().getDistance()));
        }
        if (sf.getValue().getOffset() != null) {
            setOffset(SeParameterFactory.createRealParameter(sf.getValue().getOffset()));
        }
        if (sf.getValue().getStroke() != null) {
            setStroke(Stroke.createFromJAXBElement(sf.getValue().getStroke()));
        } else {
            throw new InvalidStyle("Hatched Field request a stroke ");
        }
    }

    @Override
    public String dependsOnFeature() {

        String a = "";
        String d = "";
        String s = "";
        String o = "";

        if (angle != null) {
            a = angle.dependsOnFeature();
        }
        if (distance != null) {
            d = distance.dependsOnFeature();
        }
        if (offset != null) {
            o = offset.dependsOnFeature();
        }
        if (stroke != null) {
            s = stroke.dependsOnFeature();
        }

        return (a + " " + d + " " + o + " " + s).trim();

    }

    @Override
    public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid, Shape shp, boolean selected, MapTransform mt) throws ParameterException, IOException {

        if (this.stroke != null) {
            //Logger logger = Logger.getLogger(HatchedFill.class.getName());
            // Perpendicular distance between two lines
            double pDist;
            pDist = 10; // TODO DEFAULT VALUE
            if (this.distance != null) {
                pDist = Uom.toPixel(this.distance.getValue(sds, fid), this.getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
            }
            double alpha = 45.0;
            if (this.angle != null) {
                alpha = this.angle.getValue(sds, fid);
            }
            double hOffset = 0.0;
            if (this.offset != null) {
                hOffset = Uom.toPixel(this.offset.getValue(sds, fid), this.getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
            }

            //logger.log(Level.INFO, "Hatches Params: pDist={0}; angle={1}; offset={2}", new Object[]{pDist, alpha, hOffset});

            // Make sure alpha is > 0
            while (alpha < 0.0) {
                alpha += 360.0;
            }

            // < 360.0
            while (alpha > 360.0) {
                alpha -= 360.0;
            }

            if (alpha > 180.0)
                alpha -= 180.0;

            // Adjust offset to be in [0;pDist[ intervall
            if (hOffset > 0.0) {
                hOffset -= pDist * ((int) (hOffset / pDist));
            }


            double minX = 0;
            double minY = 0;

            double width = mt.getWidth();
            double height = mt.getHeight();

            if (g2 instanceof RenderableGraphics) {
                System.out.println("GRAPHIC CONTEXT!");
                RenderableGraphics rg = (RenderableGraphics) g2;
                minX = Math.round(rg.getMinX());
                minY = Math.round(rg.getMinY());
                width = Math.round(rg.getWidth());
                height = Math.round(rg.getHeight());
                throw  new ParameterException("HatchedFill is not currently supported to style marks");
            }


            double xDist;
            double yDist;

            double xDistRef;
            double yDistRef;

            double xStart;
            double yStart;

            // Starting point
            double xRef;
            double yRef;

            if (alpha < 0.01){
                // Horizontal
                xDist = 0;
                xDistRef = 0;

                yDist = pDist;
                yDistRef = pDist;

                xStart = minX;
                xRef = minX + width;

                yStart = minY + hOffset;
                yRef = minY + hOffset;
            }
            else if (alpha>89.9 && alpha < 90.1){
                // Vertical
                xDist = pDist;
                xDistRef = pDist;

                yDist = 0;
                yDistRef = 0;

                xStart = minX + hOffset;
                xRef = minX + hOffset;

                yStart = minY;
                yRef = minY + height;
            }
            else if(alpha > 0.0 && alpha < 90.0) {
                xDist = pDist / Math.cos((90 - alpha) * ShapeHelper._0_0175);
                yDist = -pDist / Math.cos((alpha) * ShapeHelper._0_0175);

                xDistRef = 0.0;
                yDistRef = 0.0;

                xRef = (int) minX;
                yRef = minY + height;

                xStart = xRef;
                yStart = yRef;

                if (hOffset > 0.0) {
                    xStart += hOffset / Math.cos((90 - alpha) * ShapeHelper._0_0175);
                    yStart -= hOffset / Math.cos((alpha) * ShapeHelper._0_0175);
                }
            } else if (alpha > 90.0 && alpha < 180.0){
                xDist = - pDist / Math.sin((180 - alpha) * ShapeHelper._0_0175);
                yDist = - pDist / Math.sin((alpha - 90.0) * ShapeHelper._0_0175);

                xRef = (int) minX + width;
                yRef = minY + height;

                xDistRef = 0.0;
                yDistRef = 0.0;

                xStart = xRef;
                yStart = yRef;

                if (hOffset > 0.0) {
                    xStart -= hOffset / Math.sin((180 - alpha) * ShapeHelper._0_0175);
                    yStart -= hOffset / Math.sin((alpha - 90.0) * ShapeHelper._0_0175);
                }
            } else {
                throw new UnsupportedOperationException("There is an issue somewhere in HatchedFill...");
            }

            double x;
            double y;
            //g2.setCI
            //g2.setClip(shp);


            //Rectangle clip = new Rectangle((int)minX, (int)(minY + height/3), (int)(width), (int)(height/2));
            g2.clip(shp);

            //g2.setPaint(Color.yellow);
            //g2.fill(clip);

            //System.out.println ("  Clip is : " + clip);
            //System.out.println ("  Clip is : " + g2.getClip());

            Rectangle2D.Double bounds = new Rectangle2DDouble((int)minX, (int)minY, width, height);
            //System.out.println ("B0ounds : " + bounds);

            for (x = xStart, y = yStart; ; x += xDist, y += yDist, xRef += xDistRef, yRef += yDistRef) {
                if (bounds.intersectsLine(xRef, y, x, yRef)){
                    Line2D.Double l = new Line2D.Double(xRef, y, x, yRef);
                    Line2D.Double intersection = ShapeHelper.intersection(l, bounds);
                    if (intersection != null) {
                        stroke.draw(g2, sds, fid, intersection, selected, mt, 0.0);
                    } else {
                        stroke.draw(g2, sds, fid, l, selected, mt, 0.0);
                    }
                } else {
                    break;
                }
            }
            g2.setClip(null);
        }
    }

    @Override
    public Paint getPaint(long fid, SpatialDataSourceDecorator sds,
            boolean selected, MapTransform mt)
            throws ParameterException {
        return null;
    }

    public RealParameter getAngle() {
        return angle;
    }

    public void setAngle(RealParameter angle) {
        this.angle = angle;
        if (angle != null) {
            angle.setContext(RealParameterContext.realContext);
        }
    }

    public RealParameter getDistance() {
        return distance;
    }

    public void setDistance(RealParameter distance) {
        this.distance = distance;
        if (distance != null) {
            this.distance.setContext(RealParameterContext.nonNegativeContext);
        }

    }

    public RealParameter getOffset() {
        return offset;
    }

    public void setOffset(RealParameter offset) {
        this.offset = offset;
        if (offset != null) {
            offset.setContext(RealParameterContext.realContext);
        }
    }

    public Stroke getStroke() {
        return stroke;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
        if (stroke != null) {
            stroke.setParent(this);
        }
    }

    @Override
    public JAXBElement<? extends FillType> getJAXBElement() {
        ObjectFactory of = new ObjectFactory();


        return of.createHatchedFill(this.getJAXBType());
    }

    @Override
    public HatchedFillType getJAXBType() {
        ObjectFactory of = new ObjectFactory();
        HatchedFillType hf = of.createHatchedFillType();

        if (angle != null) {
            hf.setAngle(angle.getJAXBParameterValueType());
        }

        if (distance != null) {
            hf.setDistance(distance.getJAXBParameterValueType());
        }

        if (offset != null) {
            hf.setOffset(offset.getJAXBParameterValueType());
        }

        if (stroke != null) {
            hf.setStroke(stroke.getJAXBElement());
        }

        return hf;
    }
}
