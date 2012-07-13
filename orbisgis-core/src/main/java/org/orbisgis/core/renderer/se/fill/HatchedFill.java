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
import java.util.HashSet;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.FillType;
import net.opengis.se._2_0.core.HatchedFillType;
import net.opengis.se._2_0.core.ObjectFactory;
import org.gdms.data.values.Value;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.StrokeNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.UsedAnalysis;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;

/**
 * A {@code HatchedFill} will fill a shape with hatches. It is configured according
 * to an angle (the orientation of the hatches), a distance (between the hatches),
 * an offset (from the default location of the hatches) and a stroke (to determine how to draw the
 * hatches).</p>
 * <p>The <b>offset</b> value is used to shift hatches from the location where they are printed
 * by default. That means that, for a given geometry, it becomes possible to paint multiple
 * hatches that do not overlap by using the same orientation, and using an offset
 * so that each hatch of the second {@code HatchedFill} is drawn between two hatches
 * of the first {@code HatchedFill}.</p>
 * <p>The meaning of distance and offset is of course UOM dependant.
 * @author maxence, alexis
 */
public final class HatchedFill extends Fill implements StrokeNode {

    //Useful constants.
    private static final double EPSILON = 0.01; // todo Eval, and use an external EPSILON value.


    private static final double TWO_PI_DEG = 360.0;


    private static final double PI_DEG = 180.0;


    /**
     * The default perpendicular distance between two hatches.
     */
    public static final double DEFAULT_PDIST = 10.0;


    /**
     * Default orientation value for hatches.
     */
    public static final double DEFAULT_ALPHA = 45.0;


    /**
     * 
     */
    public static final double DEFAULT_NATURAL_LENGTH = 100;


    private RealParameter angle;


    private RealParameter distance;


    private RealParameter offset;


    private Stroke stroke;


    /**
     * Creates a default {@code HatchedFill} with default values and a default penstroke.
     */
    public HatchedFill() {
        setStroke(new PenStroke());
    }


    /**
     * Creates a new {@code HatchedFill} using the JAXBElement given in argument.
     * @param sf
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
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
    public HashSet<String> dependsOnFeature() {
        HashSet<String> ret = new HashSet<String>();
        if (angle != null) {
            ret.addAll(angle.dependsOnFeature());
        }
        if (distance != null) {
            ret.addAll(distance.dependsOnFeature());
        }
        if (offset != null) {
            ret.addAll(offset.dependsOnFeature());
        }
        if (stroke != null) {
            ret.addAll(stroke.dependsOnFeature());
        }

        return ret;

    }

    @Override
    public UsedAnalysis getUsedAnalysis() {
        UsedAnalysis ua = new UsedAnalysis();
        ua.include(angle);
        ua.include(distance);
        ua.include(offset);
        if(stroke != null){
            ua.merge(stroke.getUsedAnalysis());
        }
        return ua;
    }

    @Override
    public void draw(Graphics2D g2, Map<String,Value> map, Shape shp, boolean selected, MapTransform mt) throws ParameterException, IOException {

        if (this.stroke != null) {
            // Perpendicular distance between two lines

            try {
                double pDist;
                pDist = DEFAULT_PDIST;
                if (this.distance != null) {
                    pDist = Uom.toPixel(this.distance.getValue(map), this.getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
                }

                double alpha = DEFAULT_ALPHA;
                if (this.angle != null) {
                    alpha = this.angle.getValue(map);
                }

                double hOffset = 0.0;
                if (this.offset != null) {
                    hOffset = Uom.toPixel(this.offset.getValue(map), this.getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
                }

                drawHatch(g2, map, shp, selected, mt, alpha, pDist, stroke, hOffset);
                
            } catch (RuntimeException eee) {
                System.out.println("Error " + eee);
                eee.printStackTrace(System.out);
            }
        }


    }


    /**
     * Static method that draw hatches within provided shp
     * 
     * @param g2  the g2 to write on
     * @param sds the spatial data source
     * @param fidfeature if within sds
     * @param shp the shape to hatch
     * @param selected is the feature selected ? will emphasis hatches
     * @param mt the well known map transform
     * @param alph hatches orientation
     * @param pDist perpendicular distance between two hatch line (stroke width 
     *         not taken into account so a 10mm wide black PenStroke + pDist=10mm
     *         will produce a full black behaviour...)
     * @param stroke the stroke to use to draw hatches
     * @param hOffset offset between the references point and the reference hatch
     * @throws ParameterException
     * @throws IOException 
     */
    public static void drawHatch(Graphics2D g2, Map<String,Value> map, Shape shp,
            boolean selected, MapTransform mt, double alph, double pDist, Stroke stroke,
            double hOffset) throws ParameterException, IOException {
        double alpha = alph;
        while (alpha < 0.0) {
            alpha += TWO_PI_DEG;
        }   // Make sure alpha is > 0
        while (alpha > TWO_PI_DEG) {
            alpha -= TWO_PI_DEG;
        } // and < 360.0
        alpha = alpha * Math.PI / PI_DEG; // and finally convert in radian
        double beta = Math.PI / 2.0 + alpha;
        double deltaOx = Math.cos(beta) * hOffset;
        double deltaOy = Math.sin(beta) * hOffset;
        Double naturalLength = stroke.getNaturalLength(map, shp, mt);
        if (naturalLength.isInfinite()) {
            naturalLength = DEFAULT_NATURAL_LENGTH;
        }

        // the first hatch to generate is the reference one : it crosses the reference point
        Point2D.Double geoRef = new Point2D.Double(0, 0);
        // Map geo ref point within g2 space
        Point2D ref = mt.getAffineTransform().transform(geoRef, null);
        // Apply hatch offset to ref point
        ref.setLocation(ref.getX() + deltaOx, ref.getY() + deltaOy);

        // Compute some var
        double cosAlpha = Math.cos(alpha);
        double sinAlpha = Math.sin(alpha);

        if (Math.abs(sinAlpha) < EPSILON) {
            sinAlpha = 0.0;
        }

        boolean vertical = false;

        if (Math.abs(cosAlpha) < EPSILON) {
            cosAlpha = 0.0;
            vertical = true;
        }

        double deltaHx = cosAlpha * naturalLength;
        double deltaHy = sinAlpha * naturalLength;

        double deltaDx = pDist / sinAlpha;
        double deltaDy = pDist / cosAlpha;

        Rectangle2D fbox = shp.getBounds2D();


        /* the following block compute the number of times the hatching pattern shall be drawn */

        int nb2start; // how many pattern to skip from the ref point to the begining of the shape ?
        int nb2end; // how many pattern to skip from the ref point to the end of the shape ?

        if (vertical) {
            if (deltaDx >= 0.0) {
                nb2start = (int) Math.ceil((fbox.getMinX() - ref.getX()) / deltaDx);
                nb2end = (int) Math.floor(((fbox.getMaxX() - ref.getX()) / deltaDx));
            } else {
                nb2start = (int) Math.floor((fbox.getMinX() - ref.getX()) / deltaDx);
                nb2end = (int) Math.ceil(((fbox.getMaxX() - ref.getX()) / deltaDx));
            }
        } else {
            if (cosAlpha < 0) {
                nb2start = (int) Math.ceil((fbox.getMinX() - ref.getX()) / deltaHx);
                nb2end = (int) Math.floor(((fbox.getMaxX() - ref.getX()) / deltaHx));
            } else {
                nb2start = (int) Math.floor((fbox.getMinX() - ref.getX()) / deltaHx);
                nb2end = (int) Math.ceil(((fbox.getMaxX() - ref.getX()) / deltaHx));
            }
        }

        int nb2draw = nb2end - nb2start;

        double ref_yXmin;
        double ref_yXmax;

        double cos_sin = cosAlpha * sinAlpha;

        ref_yXmin = ref.getY() + nb2start * deltaHy;
        ref_yXmax = ref.getY() + nb2end * deltaHy;

        double hxmin;
        double hxmax;
        if (vertical) {
            hxmin = nb2start * deltaDx + ref.getX();
            hxmax = nb2end * deltaDx + ref.getX();
        } else {
            hxmin = nb2start * deltaHx + ref.getX();
            hxmax = nb2end * deltaHx + ref.getX();
        }

        double hymin;
        double hymax;
        double nb2drawDeltaY = nb2draw * deltaHy;

        // Compute hatches sub-set to draw (avoid all pattern which not stands within the clip area...)

        if (vertical) {
            if (deltaHy < 0.0) {
                hymin = Math.ceil((fbox.getMinY() - ref.getY()) / deltaHy) * deltaHy + ref.getY();
                hymax = Math.floor((fbox.getMaxY() - ref.getY()) / deltaHy) * deltaHy + ref.getY();
            } else {
                hymin = Math.floor((fbox.getMinY() - ref.getY()) / deltaHy) * deltaHy + ref.getY();
                hymax = Math.ceil((fbox.getMaxY() - ref.getY()) / deltaHy) * deltaHy + ref.getY();
            }
        } else {
            if (cos_sin < 0) {
                hymin = Math.floor((fbox.getMinY() - ref_yXmin) / (deltaDy)) * deltaDy + ref_yXmin;
                hymax = Math.ceil((fbox.getMaxY() - ref_yXmax) / (deltaDy)) * deltaDy + ref_yXmax - nb2drawDeltaY;
            } else {
                hymin = Math.floor((fbox.getMinY() - nb2drawDeltaY - ref_yXmin) / (deltaDy)) * deltaDy + ref_yXmin;

                if (deltaDy < 0) {
                    hymax = Math.floor((fbox.getMaxY() + nb2drawDeltaY - ref_yXmax) / (deltaDy)) * deltaDy + ref_yXmax - nb2drawDeltaY;
                } else {
                    hymax = Math.ceil((fbox.getMaxY() + nb2drawDeltaY - ref_yXmax) / (deltaDy)) * deltaDy + ref_yXmax - nb2drawDeltaY;
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
                if (deltaDx < 0) {
                    deltaDx *= -1;
                }
                for (x = hxmin; x < hxmax + deltaDx / 2.0; x += deltaDx) {
                    if (sinAlpha > 0) {
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

                    stroke.draw(g2, map, l, selected, mt, 0.0);

                    //g2.fillOval((int)(l.getX1() - 2),(int)(l.getY1() -2) , 4, 4);
                    //g2.fillOval((int)(l.getX2() - 2),(int)(l.getY2() -2) , 4, 4);
                }
            } else {

                // Seems to been unreachable !
                for (x = hxmin; x > hxmax - deltaDx / 2.0; x += deltaDx) {
                    l.x1 = x;
                    l.y1 = hymin;
                    l.x2 = x;
                    l.y2 = hymax;

                    stroke.draw(g2, map, l, selected, mt, 0.0);

                    //g2.fillOval((int)(l.getX1() - 2),(int)(l.getY1() -2) , 4, 4);
                    //g2.fillOval((int)(l.getX2() - 2),(int)(l.getY2() -2) , 4, 4);
                }
            }

        } else {
            if (hymin < hymax) {
                if (deltaDy < 0.0) {
                    deltaDy *= -1;
                }

                for (y = hymin; y < hymax + deltaDy / 2.0; y += deltaDy) {

                    if (cosAlpha > 0) {
                        // Line goes from the left to the right
                        l.x1 = hxmin;
                        l.y1 = y;
                        l.x2 = hxmax;
                        l.y2 = y + nb2draw * deltaHy;
                    } else {
                        // Line goes from the right to the left
                        l.x1 = hxmax;
                        l.y1 = y + nb2draw * deltaHy;
                        l.x2 = hxmin;
                        l.y2 = y;
                    }

                    stroke.draw(g2, map, l, selected, mt, 0.0);
                    //g2.fillOval((int)(l.getX1() - 2),(int)(l.getY1() -2) , 4, 4);
                    //g2.fillOval((int)(l.getX2() - 2),(int)(l.getY2() -2) , 4, 4);
                }
            } else {

                if (deltaDy > 0.0) {
                    deltaDy *= -1;
                }


                for (y = hymin; y > hymax - deltaDy / 2.0; y += deltaDy) {


                    if (cosAlpha > 0) {
                        // Line goes from the left to the right
                        l.x1 = hxmin;
                        l.y1 = y;
                        l.x2 = hxmax;
                        l.y2 = y + nb2draw * deltaHy;
                    } else {
                        // Line goes from the right to the left
                        l.x1 = hxmax;
                        l.y1 = y + nb2draw * deltaHy;
                        l.x2 = hxmin;
                        l.y2 = y;
                    }

                    stroke.draw(g2, map, l, selected, mt, 0.0);

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
    public Paint getPaint(Map<String,Value> map,
            boolean selected, MapTransform mt) throws ParameterException {
        return null;
    }


    /**
     * Get the orientation of the hatches.
     * @return 
     */
    public RealParameter getAngle() {
        return angle;
    }


    /**
     * Set the orientation of the hatches.
     * @return 
     */
    public void setAngle(RealParameter angle) {
        this.angle = angle;
        if (angle != null) {
            angle.setContext(RealParameterContext.REAL_CONTEXT);
        }
    }


    /**
     * Get the perpendicular distance between two hatches
     * @return 
     */
    public RealParameter getDistance() {
        return distance;
    }


    /**
     * Set the perpendicular distance between two hatches
     * @return 
     */
    public void setDistance(RealParameter distance) {
        this.distance = distance;
        if (distance != null) {
            this.distance.setContext(RealParameterContext.NON_NEGATIVE_CONTEXT);
        }

    }


    /**
     * Get the offset of the hatches.
     * @return 
     */
    public RealParameter getOffset() {
        return offset;
    }


    /**
     * Set the offset of the hatches.
     * @param offset 
     */
    public void setOffset(RealParameter offset) {
        this.offset = offset;
        if (offset != null) {
            offset.setContext(RealParameterContext.REAL_CONTEXT);
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
