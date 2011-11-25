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
package org.orbisgis.core.renderer.se.stroke;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import org.gdms.data.DataSource;
import net.opengis.se._2_0.core.PenStrokeType;

import org.orbisgis.core.map.MapTransform;
import net.opengis.se._2_0.core.ObjectFactory;
import net.opengis.se._2_0.core.ParameterValueType;
import org.orbisgis.core.renderer.se.FillNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.UomNode;
import org.orbisgis.core.renderer.se.common.ShapeHelper;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;

/**
 * Basic stroke for linear features. It is designed according to :
 * <ul><li>A {@link Fill} value</li>
 * <li>A width</li>
 * <li>A way to draw the extremities of the lines</li>
 * <li>A way to draw the joins between the segments of the lines</li>
 * <li>An array of dashes, that is used to draw the lines. The array is stored as a StringParamater,
 * that contains space separated double values. This double values are used to determine
 * the length of each opaque part (even elements of the array) and the length of 
 * each transparent part (odd elements of the array). If an odd number of values is given,
 * the pattern is expanded by repeating it twice to give an even number of values.</li>
 * <li>An offset used to know where to draw the line.</li>
 * </ul>
 * @author maxence, alexis
 */
public final class PenStroke extends Stroke implements FillNode, UomNode {

    private static double DEFAULT_WIDTH_PX = 1.0;
    private static LineCap DEFAULT_CAP = LineCap.BUTT;
    private static LineJoin DEFAULT_JOIN = LineJoin.ROUND;
    private Fill fill;
    //private RealParameter opacity;
    private RealParameter width;
    private LineJoin lineJoin;
    private LineCap lineCap;
    private StringParameter dashArray;
    private RealParameter dashOffset;
    private Uom uom;


    /**
     * There are three ways to draw the end of a line : butt, round and square.
     */
    public enum LineCap {

        BUTT, ROUND, SQUARE;

        /**
         * Build a {@link ParameterValueType} from this {@code LineCap}.
         * @return
         */
        public ParameterValueType getParameterValueType() {
            return SeParameterFactory.createParameterValueType(this.name().toLowerCase());
        }
    }

    /**
     * There are three ways to join the segments of a LineString : mitre, round, bevel.
     */
    public enum LineJoin {

        MITRE, ROUND, BEVEL;

        /**
         * Build a {@link ParameterValueType} from this {@code LineJoin}.
         * @return
         */
        public ParameterValueType getParameterValueType() {
            return SeParameterFactory.createParameterValueType(this.name().toLowerCase());
        }
    }

    /**
     * Create a standard undashed 0.1mm-wide opaque black stroke
     */
    public PenStroke() {
        super();
        setFill(new SolidFill(Color.BLACK, 1.0));
        setWidth(new RealLiteral(0.1));
        //setOpacity(new RealLiteral(100.0));

        setUom(null);

        setDashArray(null);
        setDashOffset(null);

        setLineCap(null);
        setLineJoin(null);

        //updateBasicStroke();
    }

    /**
     * Build a PenStroke from the Jaxb type given in argument.
     * @param t
     */
    public PenStroke(PenStrokeType t) throws InvalidStyle {
        super(t);

        if (t.getUom() != null) {
            setUom(Uom.fromOgcURN(t.getUom()));
        }

        if (t.getFill() != null) {
            this.setFill(Fill.createFromJAXBElement(t.getFill()));
        } else {
            this.setFill(new SolidFill(Color.BLACK));
        }

        if (t.getDashArray() != null) {
            this.setDashArray(SeParameterFactory.createStringParameter(t.getDashArray()));
        }

        if (t.getDashOffset() != null) {
            this.setDashOffset(SeParameterFactory.createRealParameter(t.getDashOffset()));
        }

        if (t.getWidth() != null) {
            this.setWidth(SeParameterFactory.createRealParameter(t.getWidth()));
        }

        if (t.getLineCap() != null) {
            try {
                StringParameter lCap = SeParameterFactory.createStringParameter(t.getLineCap());
                this.setLineCap(LineCap.valueOf(lCap.getValue(null, -1).toUpperCase()));
            } catch (Exception ex) {
                Logger.getLogger(PenStroke.class.getName()).log(Level.SEVERE, "Could not convert line cap", ex);
            }
        }

        if (t.getLineJoin() != null) {
            try {
                StringParameter lJoin = SeParameterFactory.createStringParameter(t.getLineJoin());
                this.setLineJoin(LineJoin.valueOf(lJoin.getValue(null, -1).toUpperCase()));
            } catch (Exception ex) {
                Logger.getLogger(PenStroke.class.getName()).log(Level.SEVERE, "Could not convert line join", ex);
            }
        }
    }

    /**
     * Build a {@code PenStroke} from the JAXBElement given in argument.
     * @param s
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    public PenStroke(JAXBElement<PenStrokeType> s) throws InvalidStyle {
        this(s.getValue());
    }


    @Override
    public Double getNaturalLength(DataSource sds, long fid, Shape shp, MapTransform mt) {

        if (dashArray != null) {
            // A dashed penstroke has a length
            // This is requiered to compute hatches tile but will break the compound stroke natural length logic 
            // for infinite PenStroke element ! For this reason, compound stroke use getNaturalLengthForCompound
            try {
                double sum = 0.0;
                String sDash = this.dashArray.getValue(sds, fid);
                String[] splitedDash = sDash.split(" ");
                for (int i = 0; i < splitedDash.length; i++) {
                    sum += Uom.toPixel(Double.parseDouble(splitedDash[i]), getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
                }

                if (splitedDash.length % 2 == 1) {
                    // # pattern item is odd -> 2* to close the pattern
                    sum *= 2;
                }
                return sum;
            } catch (ParameterException ex) {
                return Double.POSITIVE_INFINITY;
            }
        } else {
            return Double.POSITIVE_INFINITY;
        }
    }

    @Override
    public Double getNaturalLengthForCompound(DataSource sds, long fid,
            Shape shp, MapTransform mt) throws ParameterException, IOException {
        return Double.POSITIVE_INFINITY;
    }


    
    @Override
    public String dependsOnFeature() {
        String result = "";

        if (fill != null) {
            result = fill.dependsOnFeature();
        }
        if (dashOffset != null) {
            result += " " + dashOffset.dependsOnFeature();
        }
        if (dashArray != null) {
            result += " " + dashArray.dependsOnFeature();
        }
        if (width != null) {
            result += " " + width.dependsOnFeature();
        }

        return result.trim();
    }

    @Override
    public Fill getFill() {
        return fill;
    }

    @Override
    public void setFill(Fill fill) {
        if (fill != null) {
            fill.setParent(this);
        }
        this.fill = fill;
    }

    /**
     * Sets the way to draw the extremities of a line.
     * @param cap 
     */
    public void setLineCap(LineCap cap) {
        lineCap = cap;
        //updateBasicStroke();
    }

    /**
     * Gets the way used to draw the extremities of a line.
     * @return 
     */
    public LineCap getLineCap() {
        if (lineCap != null) {
            return lineCap;
        } else {
            return DEFAULT_CAP;
        }
    }

    /**
     * Sets the ways used to draw the join between line segments.
     * @param join 
     */
    public void setLineJoin(LineJoin join) {
        lineJoin = join;
        //updateBasicStroke();
    }

    /**
     * Gets the ways used to draw the join between line segments.
     * @return 
     */
    public LineJoin getLineJoin() {
        if (lineJoin != null) {
            return lineJoin;
        } else {
            return DEFAULT_JOIN;
        }

    }

    /**
     * Set the width used to draw the lines with this {@code PenStroke}.
     * @param width 
     */
    public void setWidth(RealParameter width) {
        this.width = width;

        if (width != null) {
            width.setContext(RealParameterContext.NON_NEGATIVE_CONTEXT);
        }
        //updateBasicStroke();
    }

    /**
     * Gets the width used to draw the lines with this PenStroke.
     * @return 
     */
    public RealParameter getWidth() {
        return this.width;
    }

    /**
     * 
     * @return 
     */
    public RealParameter getDashOffset() {
        return dashOffset;
    }

    public void setDashOffset(RealParameter dashOffset) {
        this.dashOffset = dashOffset;
        if (dashOffset != null) {
            dashOffset.setContext(RealParameterContext.REAL_CONTEXT);
        }
        //updateBasicStroke();
    }

    /**
     * Gets the array of double values that will be used to draw a dashed line. This "array" 
     * is in fact stored as a string parameter, filled with space separated double values.</p>
     * <p>These values represent the length (in the inner UOM) of the opaque (even elements of the array)
     * and transparent (odd elements of the array) parts of the lines to draw.
     * @return 
     */
    public StringParameter getDashArray() {
        return dashArray;
    }

    /**
     * Sets the array of double values that will be used to draw a dashed line. This "array" 
     * is in fact stored as a string parameter, filled with space separated double values.</p>
     * <p>These values represent the length (in the inner UOM) of the opaque (even elements of the array)
     * and transparent (odd elements of the array) parts of the lines to draw.
     * @param dashArray 
     */
    public void setDashArray(StringParameter dashArray) {
        this.dashArray = dashArray;
    }

    private BasicStroke createBasicStroke(DataSource sds, long fid,
            Shape shp, MapTransform mt, Double v100p, boolean useDash) throws ParameterException {

        int cap;
        if (this.lineCap == null) {
            cap = BasicStroke.CAP_BUTT;
        } else {
            switch (this.lineCap) {
                case ROUND:
                    cap = BasicStroke.CAP_ROUND;
                    break;
                case SQUARE:
                    cap = BasicStroke.CAP_SQUARE;
                    break;
                default:
                case BUTT:
                    cap = BasicStroke.CAP_BUTT;
                    break;
            }
        }

        int join;
        if (this.lineJoin == null) {
            join = BasicStroke.JOIN_ROUND;
        } else {
            switch (this.lineJoin) {
                case BEVEL:
                    join = BasicStroke.JOIN_BEVEL;
                    break;
                case MITRE:
                    join = BasicStroke.JOIN_MITER;
                    break;
                case ROUND:
                default:
                    join = BasicStroke.JOIN_ROUND;
                    break;
            }
        }

        double w = DEFAULT_WIDTH_PX;

        if (width != null) {
            w = width.getValue(sds, fid);
            w = Uom.toPixel(w, getUom(), mt.getDpi(), mt.getScaleDenominator(), null); // 100% based on view box height or width ? TODO
        }


        if (useDash && this.dashArray != null && !this.dashArray.getValue(sds, fid).isEmpty()) {

            double dashO = 0.0;
            double[] dashA;

            String sDash = this.dashArray.getValue(sds, fid);
            String[] splitedDash = sDash.split(" ");

            dashA = new double[splitedDash.length];
            for (int i = 0; i < splitedDash.length; i++) {
                dashA[i] = Uom.toPixel(Double.parseDouble(splitedDash[i]), getUom(),
                        mt.getDpi(), mt.getScaleDenominator(), v100p);
            }

            if (this.dashOffset != null) {
                dashO = Uom.toPixel(this.dashOffset.getValue(sds, fid), getUom(),
                        mt.getDpi(), mt.getScaleDenominator(), v100p);
            }

            if (this.isLengthRapport()) {
                scaleDashArrayLength(dashA, shp);
            }

            float[] dashes = new float[dashA.length];
            for (int i = 0; i < dashes.length; i++) {
                dashes[i] = (float) dashA[i];
            }


            return new BasicStroke((float) w, cap, join, 10.0f, dashes, (float) dashO);
        } else {
            return new BasicStroke((float) w, cap, join);
        }
    }

    /**
     * Get an AWT {@code BasicStroke} that is representative of this {@code 
     * PenStroke}
     * @param sds
     * @param fid
     * @param mt
     * @param v100p
     * @return
     * @throws ParameterException 
     */
    public BasicStroke getBasicStroke(DataSource sds, long fid, MapTransform mt, Double v100p) throws ParameterException {
        return this.createBasicStroke(sds, fid, null, mt, v100p, true);
    }

    private void scaleDashArrayLength(double[] dashes, Shape shp) {
        if (shp == null) {
            return;
        }

        double lineLength = ShapeHelper.getLineLength(shp);

        double sum = 0.0;
        for (double dash : dashes) {
            sum += dash;
        }

        // number of element is odd => x2
        if ((dashes.length % 2) == 1) {
            sum *= 2;
        }

        double nbPattern = (int) ((lineLength / sum));

        if (nbPattern > 0) {
            double f = lineLength / (sum * nbPattern);
            for (int i = 0; i < dashes.length; i++) {
                dashes[i] *= f;
            }
        }
    }

    /**
     * Draw a pen stroke, using the given Graphics2D.
     *
     * @todo DashOffset
     */
    @Override
    public void draw(Graphics2D g2, DataSource sds, long fid, Shape shape,
            boolean selected, MapTransform mt, double offset)
            throws ParameterException, IOException {

        if (this.fill != null) {

            List<Shape> shapes;
            // if not using offset rapport, compute perpendicular offset first
            if (!this.isOffsetRapport() && Math.abs(offset) > 0.0) {
                shapes = ShapeHelper.perpendicularOffset(shape, offset);
                // Setting offset to 0.0 let be sure the offset will never been applied twice!
                offset = 0.0;
            } else {
                shapes = new ArrayList<Shape>();
                shapes.add(shape);
            }

            Paint paint = fill.getPaint(fid, sds, selected, mt);

            for (Shape shp : shapes) {
                if (this.dashArray != null && !this.dashArray.getValue(sds, fid).isEmpty() && Math.abs(offset) > 0.0) {
                    String value = dashArray.getValue(sds, fid);
                    String[] split = value.split("\\s+");
                    //value.split("");

                    Shape chute = shp;
                    List<Shape> fragments = new ArrayList<Shape>();
                    BasicStroke bs = createBasicStroke(sds, fid, shp, mt, null, false);

                    double dashLengths[] = new double[split.length];
                    for (int i = 0; i < split.length; i++) {
                        dashLengths[i] = Uom.toPixel(Double.parseDouble(split[i]), getUom(),
                                mt.getDpi(), mt.getScaleDenominator(), null);
                    }

                    if (this.isLengthRapport()) {
                        scaleDashArrayLength(dashLengths, shp);
                    }

                    int i = 0;
                    int j = 0;

                    //while (ShapeHelper.getLineLength(chute) > 0) {
                    while (chute != null) {
                        List<Shape> splitLine = ShapeHelper.splitLine(chute, dashLengths[j]);
                        Shape seg = splitLine.remove(0);
                        if (splitLine.size() > 0) {
                            chute = splitLine.remove(0);
                        } else {
                            chute = null;
                        }
                        if (i % 2 == 0) {
                            // i.e seg to draw
                            fragments.add(seg);
                        } // else means blank space

                        j = (j + 1) % split.length;
                        i++;
                    }

                    if (paint != null) {
                        g2.setPaint(paint);
                        g2.setStroke(bs);
                    }

                    for (Shape seg : fragments) {
                        for (Shape oSeg : ShapeHelper.perpendicularOffset(seg, offset)) {
                            if (oSeg != null) {
                                if (paint != null) {
                                    g2.draw(oSeg);
                                } else {
                                    Shape outline = bs.createStrokedShape(oSeg);
                                    fill.draw(g2, sds, fid, outline, selected, mt);
                                }
                            }
                        }
                    }
                } else {

                    BasicStroke stroke = null;

                    stroke = this.createBasicStroke(sds, fid, shp, mt, null /*ShapeHelper.getAreaPerimeterLength(shp)*/, true);
                    g2.setPaint(paint);
                    g2.setStroke(stroke);

                    if (Math.abs(offset) > 0.0) {
                        for (Shape oShp : ShapeHelper.perpendicularOffset(shp, offset)) {
                            if (oShp != null) {

                                if (paint != null) {
                                    //g2.setStroke(stroke);
                                    //g2.setPaint(paint);
                                    g2.draw(oShp);
                                } else {
                                    Shape outline = stroke.createStrokedShape(oShp);
                                    fill.draw(g2, sds, fid, outline, selected, mt);
                                }
                            }
                        }
                    } else {
                        if (paint != null) {
                            // Some fill type can be converted to a texture paint or a solid color

                            //g2.setStroke(stroke);
                            //g2.setPaint(paint);
                            g2.draw(shp);
                        } else {
                            // Others can't -> create the ares to fill
                            Shape outline = stroke.createStrokedShape(shp);
                            fill.draw(g2, sds, fid, outline, selected, mt);
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets the width, in pixels, of the lines that will be drawn using this {@code PenStroke}.
     * @param sds
     * @param fid
     * @param mt
     * @return
     * @throws ParameterException 
     */
    public double getWidthInPixel(DataSource sds, long fid, MapTransform mt) throws ParameterException {
        if (this.width != null) {
            return Uom.toPixel(width.getValue(sds, fid), this.getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
        } else {
            return DEFAULT_WIDTH_PX;
        }
    }

    /**
     * Get the minimal length needed to display a complete dash pattern, including
     * the dash offset.
     * @param sds
     * @param fid
     * @param mt
     * @return
     * @throws ParameterException 
     */
    public double getMinLength(DataSource sds, long fid, MapTransform mt) throws ParameterException {
        double length = 0;
        if (dashArray != null) {
            String sDash = this.dashArray.getValue(sds, fid);
            String[] splitedDash = sDash.split(" ");

            for (int i = 0; i < splitedDash.length; i++) {
                length += Uom.toPixel(Double.parseDouble(splitedDash[i]), getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
            }
        }

        if (dashOffset != null) {
            length += dashOffset.getValue(sds, fid);
        }

        return length;
    }

    @Override
    public JAXBElement<PenStrokeType> getJAXBElement() {
        ObjectFactory of = new ObjectFactory();
        return of.createPenStroke(this.getJAXBType());
    }

    /**
     * Get a representation of this {@code PenStroke} as a jaxb type.
     * @return 
     */
    public PenStrokeType getJAXBType() {
        PenStrokeType s = new PenStrokeType();

        this.setJAXBProperties(s);

        if (this.uom != null) {
            s.setUom(uom.toURN());
        }

        if (this.fill != null) {
            s.setFill(fill.getJAXBElement());
        }

        if (this.dashArray != null) {
            //s.setDashArray(null);
            s.setDashArray(dashArray.getJAXBParameterValueType());
        }

        if (this.dashOffset != null) {
            s.setDashOffset(this.dashOffset.getJAXBParameterValueType());
        }

        if (this.lineCap != null) {
            s.setLineCap(this.lineCap.getParameterValueType());
        }

        if (this.lineJoin != null) {
            s.setLineJoin(this.lineJoin.getParameterValueType());
        }

        /*if (this.opacity != null) {
        s.setOpacity(this.opacity.getJAXBParameterValueType());
        }*/

        if (this.width != null) {
            s.setWidth(this.width.getJAXBParameterValueType());
        }

        return s;
    }

    @Override
    public Uom getUom() {
        if (uom != null) {
            return uom;
        } else {
            return parent.getUom();
        }
    }

    @Override
    public void setUom(Uom u) {
        uom = u;
    }

    @Override
    public Uom getOwnUom() {
        return uom;
    }
}
