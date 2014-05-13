/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.coremap.renderer.se.graphic;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.thematic.ObjectFactory;
import net.opengis.se._2_0.thematic.PieChartType;
import net.opengis.se._2_0.thematic.PieSubtypeType;
import net.opengis.se._2_0.thematic.SliceType;

import org.orbisgis.coremap.map.MapTransform;
import org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.coremap.renderer.se.StrokeNode;
import org.orbisgis.coremap.renderer.se.SymbolizerNode;
import org.orbisgis.coremap.renderer.se.UomNode;
import org.orbisgis.coremap.renderer.se.common.Uom;
import org.orbisgis.coremap.renderer.se.fill.Fill;
import org.orbisgis.coremap.renderer.se.label.StyledText;
import org.orbisgis.coremap.renderer.se.parameter.ParameterException;
import org.orbisgis.coremap.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.coremap.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.coremap.renderer.se.parameter.real.RealParameter;
import org.orbisgis.coremap.renderer.se.parameter.real.RealParameterContext;
import org.orbisgis.coremap.renderer.se.stroke.Stroke;
import org.orbisgis.coremap.renderer.se.transform.Transform;
import org.orbisgis.coremap.renderer.se.visitors.FeaturesVisitor;

/**
 * A PieChart is a way to render statistical informations directly in the map.
 * It is a circular chart, that is diveided into sectors. The arc length of
 * each sector is directly proportional to the value it represents - supposing
 * the diameter length is the sum of all the represented values. It is built
 * using the following attributes :
 * <ul>
 * <li>A unit of measure (as a {@code UomNode}).</li>
 * <li>A {@code PieChartSubType}. We can represent a pie chart in a whole circle
 * or in a half-circle (defautl being the whole circle).</li>
 * <li>A radius - default is 30 pixels</li>
 * <li>A hole radius - default is 0. By adding a hole radius, it becomes possible
 * to render a ring rather than a disc. It should be greater than the radius.
 * </li>
 * <li>A Stroke to render its boundary</li>
 * <li>A transform that can be applied on the graphic.</li>
 * <li>A list of slices, as described in {@link Slice}</li>
 * </ul>
 * </p>
 * <p>{@code Slices} can be organize in this {@code PieChart}, in order to
 * change their display order
 * @author Alexis Guéganno, Maxence Laurent
 */
public final class PieChart extends Graphic implements StrokeNode, UomNode,
        TransformNode {

    private ArrayList<SliceListener> listeners;
    private static final double DEFAULT_RADIUS_PX = 30;
    private Uom uom;
    private PieChartSubType type;
    private RealParameter radius;
    private RealParameter holeRadius;
    private boolean displayValue;
    private Stroke stroke;
    private Transform transform;
    private ArrayList<Slice> slices;

    /**
     * A {@code PieChart} can be drawn in a whole circle, or in a half circle.
     */
    public enum PieChartSubType {

        WHOLE, HALF;
    }

    /**
     * Build a new {@code PieChart}. It does not have any slice, is drawn in a
     * whole circle, and has a radius of 10.
     */
    public PieChart() {
        slices = new ArrayList<Slice>();
        type = PieChartSubType.WHOLE;
        radius = new RealLiteral(DEFAULT_RADIUS_PX);
        this.listeners = new ArrayList<SliceListener>();
    }

    /**
     * Build a new {@code PieChart} from the give {@code JAXBElement}.
     * @param pieE
     * @throws org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle
     */
    PieChart(JAXBElement<PieChartType> pieE) throws InvalidStyle {
        this();

        PieChartType t = pieE.getValue();

        if (t.getUom() != null) {
            this.setUom(Uom.fromOgcURN(t.getUom()));
        }

        if (t.getTransform() != null) {
            this.setTransform(new Transform(t.getTransform()));
        }

        if (t.getRadius() != null) {
            this.setRadius(SeParameterFactory.createRealParameter(t.getRadius()));
        }

        if (t.getHoleRadius() != null) {
            this.setHoleRadius(SeParameterFactory.createRealParameter(t.getHoleRadius()));
        }

        if (t.getStroke() != null) {
            this.setStroke(Stroke.createFromJAXBElement(t.getStroke()));
        }

        if (t.getPieSubtype() != null && t.getPieSubtype().value().equalsIgnoreCase("half")) {
            this.setType(PieChartSubType.HALF);
        } else {
            this.setType(PieChartSubType.WHOLE);
        }

        for (SliceType st : t.getSlice()) {
            Slice s = new Slice();

            s.setName(st.getName());

            if (st.getValue() != null) {
                s.setValue(SeParameterFactory.createRealParameter(st.getValue()));
            }

            if (st.getFill() != null) {
                s.setFill(Fill.createFromJAXBElement(st.getFill()));
            }

            if (st.getGap() != null) {
                s.setGap(SeParameterFactory.createRealParameter(st.getGap()));
            }

            this.addSlice(s);
        }

    }

    /**
     * Add a listener to this {@code PieChart}.
     * @param lstner
     */
    public void registerListerner(SliceListener lstner) {
        listeners.add(lstner);
    }

    public void fireSliceMoveDown(int i) {
        for (SliceListener l : listeners) {
            l.sliceMoveDown(i);
        }
    }

    public void fireSliceMoveUp(int i) {
        for (SliceListener l : listeners) {
            l.sliceMoveUp(i);
        }
    }

    public void fireSliceRemoved(int i) {
        for (SliceListener l : listeners) {
            l.sliceRemoved(i);
        }
    }

    @Override
    public Uom getUom() {
        if (uom != null) {
            return uom;
        } else if(getParent() instanceof UomNode){
            return ((UomNode)getParent()).getUom();
        } else {
            return Uom.PX;
        }
    }

    @Override
    public Uom getOwnUom() {
        return uom;
    }

    @Override
    public void setUom(Uom uom) {
        this.uom = uom;
    }

    /**
     * Retrieve the number of slices registered in this {@code PieChart}.
     * @return
     */
    public int getNumSlices() {
        return slices.size();
    }

    /**
     * Get the ith {@code Slice} registered in this {@code PieChart}.
     * @param i
     * @return
     * The ith {@code Slice}, or null if {@code i<0 || i>=getNumSlices())}.
     */
    public Slice getSlice(int i) {
        if (i >= 0 && i < getNumSlices()) {
            return slices.get(i);
        } else {
            return null;
        }
    }

    /**
     * Remove the ith element in the inner list of {@code Slice}s.
     * @param i
     */
    public void removeSlice(int i) {
        if (i >= 0 && i < slices.size()) {
            slices.remove(i);
            fireSliceRemoved(i);
        }
    }

    /**
     * Add an element in the inner list {@code Slice}s.
     * @param slice
     * If null, is not added.
     */
    public void addSlice(Slice slice) {
        if (slice != null) {
            slices.add(slice);
            slice.setParent(this);
        }
    }

    /**
     * Move the ith {@code Slice} up, ie swap the ith and (i+1)th elements.
     * @param i
     */
    public void moveSliceUp(int i) {
        // déplace i vers i-1
        if (slices.size() > 1) {
            if (i > 0 && i < slices.size()) {
                Slice tmp = slices.get(i);
                slices.set(i, slices.get(i - 1));
                slices.set(i - 1, tmp);
                fireSliceMoveUp(i);
            } else {
                // TODO throw
            }
        }
    }

    /**
     * Move the ith {@code Slice} down, ie swap the ith and (i-1)th elements.
     * @param i
     */
    public void moveSliceDown(int i) {
        // déplace i vers i+1
        if (slices.size() > 1) {
            if (i >= 0 && i < slices.size() - 1) {
                Slice tmp = slices.get(i);
                slices.set(i, slices.get(i + 1));
                slices.set(i + 1, tmp);
                fireSliceMoveDown(i);
            } else {
                // TODO throw
            }
        }
    }

    public boolean isDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(boolean displayValue) {
        this.displayValue = displayValue;
    }

    /**
     * Get the hole of the radius, if set. Can be null
     * @return
     * A {@link RealParameter} placed in a
     * {@link RealParameterContext#NON_NEGATIVE_CONTEXT}.
     */
    public RealParameter getHoleRadius() {
        return holeRadius;
    }

    /**
     * Set the radius of the hole that must be kept in this {@code PieChart}. If
     * greater than 0 and smaller than the {@code PieChart}'s radius, this
     * {@code PieChart} will be drawn as a crown rather than as a disc.
     * @param holeRadius
     */
    public void setHoleRadius(RealParameter holeRadius) {
        this.holeRadius = holeRadius;
        if (holeRadius != null) {
            if (this.radius != null){
                FeaturesVisitor fv = new FeaturesVisitor();
                this.radius.acceptVisitor(fv);
                if(fv.getResult().isEmpty()) {
                    try {
                        holeRadius.setContext(new RealParameterContext(0.0, radius.getValue(null, -1)));
                    } catch (ParameterException ex) {
                        // don't throw anything since radius does not depends on features
                    }
                }
            }
            holeRadius.setContext(RealParameterContext.NON_NEGATIVE_CONTEXT);
            holeRadius.setParent(this);
        }
    }

    /**
     * Get the radius of the {@code PieChart}.
     * @return
     * The radius of the {@code PieChart}, as a {@link RealParameter} placed in
     * a {@link RealParameterContext#NON_NEGATIVE_CONTEXT}.
     */
    public RealParameter getRadius() {
        return radius;
    }

    /**
     * Set the radius of the {@code PieChart}.
     * @param radius
     * A {@link RealParameter}, that is placed by this method in a
     * {@link RealParameterContext#NON_NEGATIVE_CONTEXT}.
     */
    public void setRadius(RealParameter radius) {
        this.radius = radius;
        if (radius != null) {
            radius.setContext(RealParameterContext.NON_NEGATIVE_CONTEXT);
            radius.setParent(this);
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

    /**
     * Get the type of this {@code PieChart}.
     * @return
     */
    public PieChartSubType getType() {
        return type;
    }

    /**
     * Set the type of this {@code PieChart}.
     * @param type
     */
    public void setType(PieChartSubType type) {
        this.type = type;
    }

    @Override
    public Transform getTransform() {
        return transform;
    }

    @Override
    public void setTransform(Transform transform) {
        this.transform = transform;
        if (transform != null) {
            transform.setParent(this);
        }
    }

    @Override
    public Rectangle2D getBounds(Map<String,Object> map, MapTransform mt) throws ParameterException, IOException {

        double r = DEFAULT_RADIUS_PX;
        if (radius != null) {
            r = Uom.toPixel(radius.getValue(map), getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
        }

        Rectangle2D bounds = new Rectangle2D.Double(-r, -r, 2 * r, 2 * r);
        /*if (transform != null) {
            AffineTransform at = transform.getGraphicalAffineTransform(false, map, mt, 2 * r, 2 * r);
            return at.createTransformedShape(bounds).getBounds2D();
        } else {
            return bounds;
        }*/
        return bounds;
    }

    @Override
    public void draw(Graphics2D g2, Map<String,Object> map,
            boolean selected, MapTransform mt, AffineTransform fat) throws ParameterException, IOException {

        AffineTransform at = new AffineTransform(fat);
        int nSlices = slices.size();

        double total = 0.0;
        double[] values = new double[nSlices];
        double[] stackedValues = new double[nSlices];
        double[] gaps = new double[nSlices];

        double r = PieChart.DEFAULT_RADIUS_PX; // 30px by default

        if (radius != null) {
            r = Uom.toPixel(this.getRadius().getValue(map), this.getUom(), mt.getDpi(), mt.getScaleDenominator(), null); // TODO 100%
        }

        double holeR = 0.0;

        Area hole = null;
        if (this.holeRadius != null) {
            holeR = Uom.toPixel(this.getHoleRadius().getValue(map), this.getUom(), mt.getDpi(), mt.getScaleDenominator(), r);
            hole = new Area(new Arc2D.Double(-holeR, -holeR, 2 * holeR, 2 * holeR, 0, 360, Arc2D.CHORD));
        }

        for (int i = 0; i < nSlices; i++) {
            Slice slc = slices.get(i);
            values[i] = slc.getValue().getValue(map);
            total += values[i];
            stackedValues[i] = total;
            RealParameter gap = slc.getGap();
            if (gap != null) {
                gaps[i] = Uom.toPixel(slc.getGap().getValue(map), this.getUom(), mt.getDpi(), mt.getScaleDenominator(), r);
            } else {
                gaps[i] = 0.0;
            }

        }

        if (this.getTransform() != null) {
            at.concatenate(this.getTransform().getGraphicalAffineTransform(false, map, mt, r, r));
        }

        // Now, the total is defines, we can compute percentages and slices begin/end angles
        double[] percentages = new double[nSlices];

        for (int i = 0; i < nSlices; i++) {
            if (i == 0) {
                percentages[i] = 0.0;
            } else {
                percentages[i] = stackedValues[(i - 1 + nSlices) % nSlices] / total;
            }
        }

        // Create BufferedImage imageWidth x imageWidth

        Shape[] shapes = new Shape[nSlices];

        double maxDeg = 360.0;

        if (this.getType() == PieChartSubType.HALF) {
            maxDeg = 180.0;
        }

        // Create slices
        for (int i = 0; i < nSlices; i++) {
            double aStart = percentages[i] * maxDeg;

            double aExtend;

            if (i < nSlices - 1) {
                aExtend = (percentages[(i + 1) % nSlices] - percentages[i]) * maxDeg;
            } else {
                aExtend = maxDeg - (percentages[i]) * maxDeg;
            }


            Area gSlc = new Area(new Arc2D.Double(-r, -r, 2 * r, 2 * r, aStart, aExtend, Arc2D.PIE));

            if (hole != null) {
                gSlc.subtract(hole);
            }


            double alphaMiddle = (aStart + aExtend / 2.0) * Math.PI / 180.0;

            // create AT = GraphicTransform + T(gap)
            AffineTransform gapAt = AffineTransform.getTranslateInstance(Math.cos(alphaMiddle) * gaps[i],
                    -Math.sin(alphaMiddle) * gaps[i]);

            gapAt.preConcatenate(at);

            Shape atShp = gapAt.createTransformedShape(gSlc);

            shapes[i] = atShp;

            Fill fill = getSlice(i).getFill();


            if (fill != null) {
                fill.draw(g2, map, atShp, selected, mt);
            }


            if (displayValue) {
                double p;
                if (i == nSlices - 1) {
                    p = 1.0 - percentages[i];
                } else {
                    p = percentages[i + 1] - percentages[i];
                }
                p *= 100;

                StyledText label = new StyledText(Integer.toString((int) Math.round(p)));
                AffineTransform labelAt = (AffineTransform) gapAt.clone();


                double labelPosRatio;
                if (this.holeRadius != null) {
                    labelPosRatio = (r - holeR) / 2.0 + holeR;
                } else {
                    labelPosRatio = r * 0.66;
                }

                labelAt.concatenate(AffineTransform.getTranslateInstance(Math.cos(alphaMiddle) * labelPosRatio,
                        -Math.sin(alphaMiddle) * labelPosRatio));

                Rectangle2D anchor = labelAt.createTransformedShape(new Rectangle2D.Double(0, 0, 1, 1)).getBounds2D();

                label.draw(g2, map, selected, mt,
                        AffineTransform.getTranslateInstance(anchor.getCenterX(), anchor.getCenterY()), null);
            }

        }

        // Stokes must be drawn after fills
        if (stroke != null) {
            for (int i = 0; i < nSlices; i++) {
                stroke.draw(g2, map, shapes[i], selected, mt, 0.0);
            }
        }
    }

    @Override
    public void updateGraphic() {
    }

    @Override
    public List<SymbolizerNode> getChildren() {
        List<SymbolizerNode> ls = new ArrayList<SymbolizerNode>();
        if (radius != null) {
            ls.add(radius);
        }
        if (holeRadius != null) {
            ls.add(holeRadius);
        }
        if (stroke != null) {
            ls.add(stroke);
        }
        if (transform != null) {
            ls.add(transform);
        }
        ls.addAll(slices);
        return ls;
    }

    @Override
    public JAXBElement<PieChartType> getJAXBElement() {

        PieChartType p = new PieChartType();

        if (type != null) {
            if (type == PieChartSubType.HALF) {
                p.setPieSubtype(PieSubtypeType.HALF);
            } else {
                p.setPieSubtype(PieSubtypeType.WHOLE);
            }
        }

        if (uom != null) {
            p.setUom(uom.toURN());
        }

        if (transform != null) {
            p.setTransform(this.transform.getJAXBType());
        }

        if (radius != null) {
            p.setRadius(radius.getJAXBParameterValueType());
        }

        if (holeRadius != null) {
            p.setHoleRadius(holeRadius.getJAXBParameterValueType());
        }

        if (stroke != null) {
            p.setStroke(stroke.getJAXBElement());
        }

        List<SliceType> slcs = p.getSlice();
        for (Slice s : slices) {
            slcs.add(s.getJAXBType());
        }

        ObjectFactory of = new ObjectFactory();
        return of.createPieChart(p);
    }
}
