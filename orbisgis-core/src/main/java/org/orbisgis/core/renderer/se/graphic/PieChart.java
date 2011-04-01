package org.orbisgis.core.renderer.se.graphic;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;

import org.orbisgis.core.map.MapTransform;
import net.opengis.se._2_0.thematic.ObjectFactory;
import net.opengis.se._2_0.thematic.PieChartType;
import net.opengis.se._2_0.thematic.PieSubtypeType;
import net.opengis.se._2_0.thematic.SliceType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;

import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.label.StyledText;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.core.renderer.se.transform.Transform;
import org.orbisgis.core.renderer.se.StrokeNode;
import org.orbisgis.core.renderer.se.UomNode;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

public final class PieChart extends Graphic implements StrokeNode, UomNode, TransformNode {

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

    public enum PieChartSubType {

        WHOLE, HALF;
    }

    public PieChart() {
        slices = new ArrayList<Slice>();
        type = PieChartSubType.WHOLE;
        radius = new RealLiteral(10);
        this.listeners = new ArrayList<SliceListener>();
    }

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
            return this.uom;
        } else {
            return parent.getUom();
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

    public int getNumSlices() {
        return slices.size();
    }

    public Slice getSlice(int i) {
        if (i >= 0 && i < getNumSlices()) {
            return slices.get(i);
        } else {
            return null;
        }
    }

    public void removeSlice(int i) {
        if (i >= 0 && i < slices.size()) {
            slices.remove(i);
            fireSliceRemoved(i);
        }
    }

    public void addSlice(Slice slice) {
        if (slice != null) {
            slices.add(slice);
            slice.setParent(this);
        }
    }

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

    public RealParameter getHoleRadius() {
        return holeRadius;
    }

    public void setHoleRadius(RealParameter holeRadius) {
        this.holeRadius = holeRadius;
        if (holeRadius != null) {
            if (this.radius != null && this.radius.dependsOnFeature().isEmpty()) {
                try {
                    holeRadius.setContext(new RealParameterContext(0.0, radius.getValue(null, -1)));
                } catch (ParameterException ex) {
                    // don't throw anything since radius does not depends on features
                }
            }
            holeRadius.setContext(RealParameterContext.nonNegativeContext);
        }
    }

    public RealParameter getRadius() {
        return radius;
    }

    public void setRadius(RealParameter radius) {
        this.radius = radius;
        if (radius != null) {
            radius.setContext(RealParameterContext.nonNegativeContext);
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

    public PieChartSubType getType() {
        return type;
    }

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
    public Rectangle2D getBounds(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException {

        double r = DEFAULT_RADIUS_PX;
        if (radius != null) {
            r = Uom.toPixel(radius.getValue(sds, fid), getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
        }

        Rectangle2D bounds = new Rectangle2D.Double(-r, -r, 2 * r, 2 * r);
        if (transform != null) {
            AffineTransform at = transform.getGraphicalAffineTransform(false, sds, fid, mt, 2 * r, 2 * r);
            return at.createTransformedShape(bounds).getBounds2D();
        } else {
            return bounds;
        }
    }

    @Override
    public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid,
            boolean selected, MapTransform mt, AffineTransform fat) throws ParameterException, IOException {

        AffineTransform at = new AffineTransform(fat);
        int nSlices = slices.size();

        double total = 0.0;
        double[] values = new double[nSlices];
        double[] stackedValues = new double[nSlices];
        double[] gaps = new double[nSlices];

        double r = PieChart.DEFAULT_RADIUS_PX; // 30px by default

        if (radius != null) {
            r = Uom.toPixel(this.getRadius().getValue(sds, fid), this.getUom(), mt.getDpi(), mt.getScaleDenominator(), null); // TODO 100%
        }

        double holeR = 0.0;

        Area hole = null;
        if (this.holeRadius != null) {
            holeR = Uom.toPixel(this.getHoleRadius().getValue(sds, fid), this.getUom(), mt.getDpi(), mt.getScaleDenominator(), r);
            hole = new Area(new Arc2D.Double(-holeR, -holeR, 2 * holeR, 2 * holeR, 0, 360, Arc2D.CHORD));
        }

        for (int i = 0; i < nSlices; i++) {
            Slice slc = slices.get(i);
            values[i] = slc.getValue().getValue(sds, fid);
            total += values[i];
            stackedValues[i] = total;
            RealParameter gap = slc.getGap();
            if (gap != null) {
                gaps[i] = Uom.toPixel(slc.getGap().getValue(sds, fid), this.getUom(), mt.getDpi(), mt.getScaleDenominator(), r);
            } else {
                gaps[i] = 0.0;
            }

        }

        if (this.getTransform() != null) {
            at.concatenate(this.getTransform().getGraphicalAffineTransform(false, sds, fid, mt, r, r));
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
                fill.draw(g2, sds, fid, atShp, selected, mt);
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

                label.draw(g2, sds, fid, selected, mt,
                        AffineTransform.getTranslateInstance(anchor.getCenterX(), anchor.getCenterY()), null);
            }

        }

        // Stokes must be drawn after fills
        if (stroke != null) {
            for (int i = 0; i < nSlices; i++) {
                stroke.draw(g2, sds, fid, shapes[i], selected, mt, 0.0);
            }
        }
    }

    /**
     *
     * @param ds
     * @param fid
     */
    /*
    @Override
    public RenderableGraphics getRenderableGraphics(SpatialDataSourceDecorator sds, long fid, boolean selected, MapTransform mt) throws ParameterException, IOException {

    int nSlices = slices.size();

    double total = 0.0;
    double[] values = new double[nSlices];
    double[] stackedValues = new double[nSlices];
    double[] gaps = new double[nSlices];

    double r = 30; // 30px by default

    if (radius != null) {
    r = Uom.toPixel(this.getRadius().getValue(sds, fid), this.getUom(), mt.getDpi(), mt.getScaleDenominator(), null); // TODO 100%
    }

    double holeR = 0.0;

    Area hole = null;
    if (this.holeRadius != null) {
    holeR = Uom.toPixel(this.getHoleRadius().getValue(sds, fid), this.getUom(), mt.getDpi(), mt.getScaleDenominator(), r);
    hole = new Area(new Arc2D.Double(-holeR, -holeR, 2 * holeR, 2 * holeR, 0, 360, Arc2D.CHORD));
    }

    double maxGap = 0.0;

    for (int i = 0; i < nSlices; i++) {
    Slice slc = slices.get(i);
    values[i] = slc.getValue().getValue(sds, fid);
    total += values[i];
    stackedValues[i] = total;
    RealParameter gap = slc.getGap();
    if (gap != null) {
    gaps[i] = Uom.toPixel(slc.getGap().getValue(sds, fid), this.getUom(), mt.getDpi(), mt.getScaleDenominator(), r);
    } else {
    gaps[i] = 0.0;
    }

    maxGap = Math.max(gaps[i], maxGap);
    }

    double pieMaxR = r + maxGap;

    if (stroke != null) {
    pieMaxR += stroke.getMaxWidth(sds, fid, mt);
    }


    Rectangle2D bounds = new Rectangle2D.Double(-pieMaxR, -pieMaxR, 2 * pieMaxR, 2 * pieMaxR);

    RenderableGraphics rg;

    AffineTransform at = null;
    if (this.getTransform() != null) {
    at = this.getTransform().getGraphicalAffineTransform(false, sds, fid, mt, r, r);

    // Apply the AT to the bbox
    Shape newBounds = at.createTransformedShape(bounds);

    bounds = newBounds.getBounds2D();
    } else {
    at = new AffineTransform();
    }
    // Graphic is too small => return null
    if (bounds.isEmpty()){
    return null;
    }
    rg = Graphic.getNewRenderableGraphics(bounds, 0, mt);

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
    fill.draw(rg, sds, fid, atShp, selected, mt);
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


    rg.drawRenderedImage(label.getImage(sds, fid, selected, mt).createRendering(mt.getCurrentRenderContext()), AffineTransform.getTranslateInstance(anchor.getCenterX(), anchor.getCenterY()));
    }

    }

    // Stokes must be drawn after fills
    if (stroke != null) {
    for (int i = 0; i < nSlices; i++) {
    stroke.draw(rg, sds, fid, shapes[i], selected, mt, 0.0);
    }
    }

    return rg;
    }
     */

    /*@Override
    public double getMaxWidth(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException {
    throw new UnsupportedOperationException("Not supported yet.");
    }*/
    @Override
    public void updateGraphic() {
    }

    @Override
    public String dependsOnFeature() {
        String result = "";
        if (radius != null) {
            result += " " + radius.dependsOnFeature();
        }
        if (holeRadius != null) {
            result += " " + holeRadius.dependsOnFeature();
        }
        if (stroke != null) {
            result += " " + stroke.dependsOnFeature();
        }
        if (this.transform != null) {
            result += " " + transform.dependsOnFeature();
        }
        for (Slice s : slices) {
            result += s.dependsOnFeature();
        }

        return result.trim();
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
