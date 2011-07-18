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

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.map.MapTransform;
import net.opengis.se._2_0.core.FillType;
import net.opengis.se._2_0.core.HatchedFillType;
import net.opengis.se._2_0.core.ObjectFactory;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.StrokeNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;

/**
 *
 * @author maxence
 */
public final class HatchedFill extends Fill implements StrokeNode {

    private final static double EPSILON = 0.01; // todo Eval !
    private RealParameter angle;
    private RealParameter distance;
    private RealParameter offset;
    private Stroke stroke;

    public HatchedFill() {
        setStroke(new PenStroke());
    }

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
            // Perpendicular distance between two lines

            try {
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

                drawHatch(g2, sds, fid, shp, selected, mt, alpha, pDist, stroke, hOffset);
            } catch (RuntimeException eee) {
                System.out.println("Suck " + eee);
                eee.printStackTrace(System.out);
            }
        }


    }

    public static void drawHatch(Graphics2D g2, SpatialDataSourceDecorator sds,
            long fid, Shape shp, boolean selected, MapTransform mt,
            double alpha, double pDist, Stroke stroke, double hOffset) throws ParameterException, IOException {

        while (alpha < 0.0) {
            alpha += 360.0;
        }   // Make sure alpha is > 0
        while (alpha > 360.0) {
            alpha -= 360.0;
        } // and < 360.0
        alpha = alpha * Math.PI / 180.0;         // and finally convert in radian

        double delta_ox = 0.0;
        double delta_oy = 0.0;

        double beta = Math.PI / 2.0 + alpha;
        delta_ox = Math.cos(beta) * hOffset;
        delta_oy = Math.sin(beta) * hOffset;



        Double naturalLength = stroke.getNaturalLength(sds, fid, shp, mt);
        if (naturalLength.isInfinite()) {
            naturalLength = 100.0;
        }

        Point2D.Double geoRef = new Point2D.Double(0, 0);
        Point2D ref = mt.getAffineTransform().transform(geoRef, null);

        ref.setLocation(ref.getX() + delta_ox, ref.getY() + delta_oy);

        double cos_alpha = Math.cos(alpha);
        double sin_alpha = Math.sin(alpha);

        if (Math.abs(sin_alpha) < EPSILON) {
            sin_alpha = 0.0;
        }

        boolean vertical = false;

        if (Math.abs(cos_alpha) < EPSILON) {
            cos_alpha = 0.0;
            vertical = true;
        }

        double delta_hx = cos_alpha * naturalLength;
        double delta_hy = sin_alpha * naturalLength;

        double delta_dx = pDist / sin_alpha;
        double delta_dy = pDist / cos_alpha;

        Rectangle2D fbox = shp.getBounds2D();


        int nb2start;
        int nb2end;

        if (vertical) {
            if (delta_dx >= 0.0) {
                nb2start = (int) Math.ceil((fbox.getMinX() - ref.getX()) / delta_dx);
                nb2end = (int) Math.floor(((fbox.getMaxX() - ref.getX()) / delta_dx));
            } else {
                nb2start = (int) Math.floor((fbox.getMinX() - ref.getX()) / delta_dx);
                nb2end = (int) Math.ceil(((fbox.getMaxX() - ref.getX()) / delta_dx));
            }
        } else {
            if (cos_alpha < 0) {
                nb2start = (int) Math.ceil((fbox.getMinX() - ref.getX()) / delta_hx);
                nb2end = (int) Math.floor(((fbox.getMaxX() - ref.getX()) / delta_hx));
            } else {
                nb2start = (int) Math.floor((fbox.getMinX() - ref.getX()) / delta_hx);
                nb2end = (int) Math.ceil(((fbox.getMaxX() - ref.getX()) / delta_hx));
            }
        }

        int nb2draw = nb2end - nb2start;

        double ref_yXmin;
        double ref_yXmax;

        double cos_sin = cos_alpha * sin_alpha;

        ref_yXmin = ref.getY() + nb2start * delta_hy;
        ref_yXmax = ref.getY() + nb2end * delta_hy;

        double hxmin;
        double hxmax;
        if (vertical) {
            hxmin = nb2start * delta_dx + ref.getX();
            hxmax = nb2end * delta_dx + ref.getX();
        } else {
            hxmin = nb2start * delta_hx + ref.getX();
            hxmax = nb2end * delta_hx + ref.getX();
        }

        double hymin;
        double hymax;
        double nb2draw_delta_y = nb2draw * delta_hy;

        if (vertical) {
            if (delta_hy < 0.0) {
                hymin = Math.ceil((fbox.getMinY() - ref.getY()) / delta_hy) * delta_hy + ref.getY();
                hymax = Math.floor((fbox.getMaxY() - ref.getY()) / delta_hy) * delta_hy + ref.getY();
            } else {
                hymin = Math.floor((fbox.getMinY() - ref.getY()) / delta_hy) * delta_hy + ref.getY();
                hymax = Math.ceil((fbox.getMaxY() - ref.getY()) / delta_hy) * delta_hy + ref.getY();
            }
        } else {
            if (cos_sin < 0) {
                hymin = Math.floor((fbox.getMinY() - ref_yXmin) / (delta_dy)) * delta_dy + ref_yXmin;
                hymax = Math.ceil((fbox.getMaxY() - ref_yXmax) / (delta_dy)) * delta_dy + ref_yXmax - nb2draw_delta_y;
            } else {
                hymin = Math.floor((fbox.getMinY() - nb2draw_delta_y - ref_yXmin) / (delta_dy)) * delta_dy + ref_yXmin;

                if (delta_dy < 0) {
                    hymax = Math.floor((fbox.getMaxY() + nb2draw_delta_y - ref_yXmax) / (delta_dy)) * delta_dy + ref_yXmax - nb2draw_delta_y;
                } else {
                    hymax = Math.ceil((fbox.getMaxY() + nb2draw_delta_y - ref_yXmax) / (delta_dy)) * delta_dy + ref_yXmax - nb2draw_delta_y;
                }
            }
        }

        double y;
        double x;

        Line2D.Double l = new Line2D.Double();


        // Inform graphic2g to only draw hatches within the shape !
        g2.clip(shp);

        if (vertical) {

            if (hxmin < hxmax) {
                if (delta_dx < 0) {
                    delta_dx *= -1;
                }
                for (x = hxmin; x < hxmax + delta_dx / 2.0; x += delta_dx) {
                    if (sin_alpha > 0) {
                        l.x1 = x;
                        l.y1 = hymin;
                        l.x2 = x;
                        l.y2 = hymax;
                    } else {
                        l.x1 = x;
                        l.y1 = hymax;
                        l.x2 = x;
                        l.y2 = hymin;
                    }

                    stroke.draw(g2, sds, fid, l, selected, mt, 0.0);

                    //g2.fillOval((int)(l.getX1() - 2),(int)(l.getY1() -2) , 4, 4);
                    //g2.fillOval((int)(l.getX2() - 2),(int)(l.getY2() -2) , 4, 4);
                }
            } else {

                // Seems to been unreachable !
                for (x = hxmin; x > hxmax - delta_dx / 2.0; x += delta_dx) {
                    l.x1 = x;
                    l.y1 = hymin;
                    l.x2 = x;
                    l.y2 = hymax;

                    stroke.draw(g2, sds, fid, l, selected, mt, 0.0);

                    //g2.fillOval((int)(l.getX1() - 2),(int)(l.getY1() -2) , 4, 4);
                    //g2.fillOval((int)(l.getX2() - 2),(int)(l.getY2() -2) , 4, 4);
                }
            }

        } else {
            if (hymin < hymax) {
                if (delta_dy < 0.0) {
                    delta_dy *= -1;
                }

                for (y = hymin; y < hymax + delta_dy / 2.0; y += delta_dy) {

                    if (cos_alpha > 0) {
                        // Line goes from the left to the right
                        l.x1 = hxmin;
                        l.y1 = y;
                        l.x2 = hxmax;
                        l.y2 = y + nb2draw * delta_hy;
                    } else {
                        // Line goes from the right to the left
                        l.x1 = hxmax;
                        l.y1 = y + nb2draw * delta_hy;
                        l.x2 = hxmin;
                        l.y2 = y;
                    }

                    stroke.draw(g2, sds, fid, l, selected, mt, 0.0);
                    //g2.fillOval((int)(l.getX1() - 2),(int)(l.getY1() -2) , 4, 4);
                    //g2.fillOval((int)(l.getX2() - 2),(int)(l.getY2() -2) , 4, 4);
                }
            } else {

                if (delta_dy > 0.0) {
                    delta_dy *= -1;
                }


                for (y = hymin; y > hymax - delta_dy / 2.0; y += delta_dy) {


                    if (cos_alpha > 0) {
                        // Line goes from the left to the right
                        l.x1 = hxmin;
                        l.y1 = y;
                        l.x2 = hxmax;
                        l.y2 = y + nb2draw * delta_hy;
                    } else {
                        // Line goes from the right to the left
                        l.x1 = hxmax;
                        l.y1 = y + nb2draw * delta_hy;
                        l.x2 = hxmin;
                        l.y2 = y;
                    }

                    stroke.draw(g2, sds, fid, l, selected, mt, 0.0);

                    //g2.fillOval((int)(l.getX1() - 2),(int)(l.getY1() -2) , 4, 4);
                    //g2.fillOval((int)(l.getX2() - 2),(int)(l.getY2() -2) , 4, 4);

                }
            }
        }
        g2.setClip(null);
    }

    /**
     * Hatched fill cannot be converted to a native java fill
     * @param fid
     * @param sds
     * @param selected
     * @param mt
     * @return null
     * @throws ParameterException
     */
    @Override
    public Paint getPaint(long fid, SpatialDataSourceDecorator sds,
            boolean selected, MapTransform mt) throws ParameterException {
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

    @Override
    public Stroke getStroke() {
        return stroke;
    }

    @Override
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
