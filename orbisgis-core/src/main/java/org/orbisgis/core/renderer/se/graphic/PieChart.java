package org.orbisgis.core.renderer.se.graphic;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;

import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.PieChartType;
import org.orbisgis.core.renderer.persistance.se.PieSubtypeType;
import org.orbisgis.core.renderer.persistance.se.SliceType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;

import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.label.StyledLabel;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.core.renderer.se.transform.Transform;
import org.orbisgis.core.renderer.se.StrokeNode;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

public final class PieChart extends Graphic implements StrokeNode {

    public enum PieChartSubType {
        WHOLE, HALF;
    }

    public PieChart() {
        slices = new ArrayList<Slice>();
    }


    PieChart(JAXBElement<PieChartType> pieE) throws InvalidStyle {
        this();

        PieChartType t = pieE.getValue();

        if (t.getUnitOfMeasure() != null){
            this.setUom(Uom.fromOgcURN(t.getUnitOfMeasure()));
        }

        if (t.getTransform() != null){
            this.setTransform(new Transform(t.getTransform()));
        }

        if (t.getRadius() != null){
            this.setRadius(SeParameterFactory.createRealParameter(t.getRadius()));
        }

        if (t.getHoleRadius() != null){
            this.setHoleRadius(SeParameterFactory.createRealParameter(t.getHoleRadius()));
        }

        if (t.getStroke() != null){
            this.setStroke(Stroke.createFromJAXBElement(t.getStroke()));
        }


        System.out.println ("Youpi Pie : " + t.getPieSubtype());
        if (t.getPieSubtype() != null){
            System.out.println ("XML Pie Type: " + t.getPieSubtype().value());
        }

        if (t.getPieSubtype() != null && t.getPieSubtype().value().equals("HALF")){
            this.setType(PieChartSubType.HALF);
        }
        else{
            this.setType(PieChartSubType.WHOLE);
        }

        for (SliceType st : t.getSlice()){
            Slice s = new Slice();

            s.setName(st.getName());
            
            if (st.getValue() != null){
                s.setValue(SeParameterFactory.createRealParameter(st.getValue()));
            }

            if (st.getFill() != null){
                s.setFill(Fill.createFromJAXBElement(st.getFill()));
            }

            if (st.getGap() != null){
                s.setGap(SeParameterFactory.createRealParameter(st.getGap()));
            }

            this.addSlice(s);
        }

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
		if (holeRadius != null){
			if (this.radius != null && !this.radius.dependsOnFeature()){
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
		if (radius != null){
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
        stroke.setParent(this);
    }

    public PieChartSubType getType() {
        return type;
    }

    public void setType(PieChartSubType type) {
        this.type = type;
    }

    /**
     *
     * @param ds
     * @param fid
     */
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
		/*
		 * Graphic is too small => return null
		 */
		if (bounds.isEmpty()){
			return null;
		}
        rg = Graphic.getNewRenderableGraphics(bounds, 0);

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

                StyledLabel label = new StyledLabel(Double.toString(p));
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
                stroke.draw(rg, sds, fid, shapes[i], selected, mt);
            }
        }

        return rg;
    }

    @Override
    public double getMaxWidth(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateGraphic() {
    }


    @Override
    public boolean dependsOnFeature() {
        return true;
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

        if (transform != null){
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
    private PieChartSubType type;
    private RealParameter radius;
    private RealParameter holeRadius;
    private boolean displayValue;
    private Stroke stroke;
    private ArrayList<Slice> slices;
}
