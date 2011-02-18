package org.orbisgis.core.renderer.se.graphic;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Path2D.Double;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.persistance.se.AxisChartSubtypeType;
import org.orbisgis.core.renderer.persistance.se.AxisChartType;
import org.orbisgis.core.renderer.persistance.se.CategoryType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.core.renderer.se.transform.Transform;

/**
 *
 * @author maxence
 * @todo Implements drawGraphic
 */
public final class AxisChart extends Graphic {

    private RealParameter normalizeTo;
    //private boolean isPolarChart;
    private AxisScale axisScale;
    private RealParameter categoryWidth;
    private RealParameter categoryGap;
    private Fill areaFill;
    private Stroke lineStroke;
    private ArrayList<Category> categories;
    private AxisChartSubType subtype;
    public static final double DEFAULT_GAP_PX = 5; //px
    public static final double DEFAULT_WIDTH_PX = 15; //px

    //private Categories stakc;
    public static enum AxisChartSubType {

        ORTHO, POLAR, STACKED;
    };

    public AxisChart() {
        subtype = AxisChartSubType.ORTHO;
        categories = new ArrayList<Category>();
    }

    AxisChart(JAXBElement<AxisChartType> chartE) throws InvalidStyle {
        this();
        AxisChartType t = chartE.getValue();

        if (t.getUnitOfMeasure() != null) {
            this.setUom(Uom.fromOgcURN(t.getUnitOfMeasure()));
        }

        if (t.getTransform() != null) {
            this.setTransform(new Transform(t.getTransform()));
        }

        if (t.getNormalization() != null) {
            this.setNormalizedTo(SeParameterFactory.createRealParameter(t.getNormalization()));
        }

        if (t.getCategoryWidth() != null) {
            this.setCategoryWidth(SeParameterFactory.createRealParameter(t.getCategoryWidth()));
        }

        if (t.getCategoryGap() != null) {
            this.setCategoryGap(SeParameterFactory.createRealParameter(t.getCategoryGap()));
        }

        if (t.getAxisChartSubtype() != null) {
            String type = t.getAxisChartSubtype().value();
            if (type.equalsIgnoreCase("polar")) {
                subtype = AxisChartSubType.POLAR;
            } else if (type.equalsIgnoreCase("stacked")) {
                subtype = AxisChartSubType.STACKED;
            } else {
                subtype = AxisChartSubType.ORTHO;
            }
        }

        if (t.getFill() != null) {
            this.setAreaFill(Fill.createFromJAXBElement(t.getFill()));
        }

        if (t.getStroke() != null) {
            this.setLineStroke(Stroke.createFromJAXBElement(t.getStroke()));
        }

        if (t.getAxisScale() != null) {
            this.setAxisScale(new AxisScale(t.getAxisScale()));
        }

        for (CategoryType ct : t.getCategory()) {
           addCategory(new Category(ct));
        }
    }

    public void addCategory(Category c){
        categories.add(c);
        c.setParent(this);
    }

    public Fill getAreaFill() {
        return areaFill;
    }

    public void setAreaFill(Fill areaFill) {
        this.areaFill = areaFill;
        areaFill.setParent(this);
    }

    public AxisScale getAxisScale() {
        return axisScale;
    }

    public void setAxisScale(AxisScale axisScale) {
        this.axisScale = axisScale;
    }

    public RealParameter getCategoryGap() {
        return categoryGap;
    }

    public void setCategoryGap(RealParameter categoryGap) {
        this.categoryGap = categoryGap;
        if (this.categoryGap != null) {
            this.categoryGap.setContext(RealParameterContext.nonNegativeContext);
        }
    }

    public RealParameter getCategoryWidth() {
        return categoryWidth;
    }

    public void setCategoryWidth(RealParameter categoryWidth) {
        this.categoryWidth = categoryWidth;
        if (categoryWidth != null) {
            categoryWidth.setContext(RealParameterContext.nonNegativeContext);
        }
    }

    public Stroke getLineStroke() {
        return lineStroke;
    }

    public void setLineStroke(Stroke lineStroke) {
        this.lineStroke = lineStroke;
        lineStroke.setParent(this);
    }

    public RealParameter getNormalizedTo() {
        return normalizeTo;
    }

    public void setNormalizedTo(RealParameter normalizedTo) {
        this.normalizeTo = normalizedTo;
    }

    @Override
    public void updateGraphic() {
    }

    private double[] getMeasuresInPixel(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException {
        System.out.println ("SDS:" + sds);
        System.out.println ("axisScale:" + axisScale);
        System.out.println ("axisScale length:" + axisScale.getAxisLength());
        System.out.println ("axisScale measure:" + axisScale.getMeasureValue());
        System.out.println ("UOM:" + getUom());


        double rLength = Uom.toPixel(axisScale.getAxisLength().getValue(sds, fid), getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
        double rMesure = axisScale.getMeasureValue().getValue(sds, fid);

        double[] heights = new double[categories.size()];

        int i = 0;
        for (Category c : categories) {
            heights[i] = c.getMeasure().getValue(sds, fid) * rLength / rMesure;
            i++;
        }

        return heights;
    }

    private RenderableGraphics getOrthoChart(SpatialDataSourceDecorator sds, long fid,
            boolean selected, MapTransform mt) throws ParameterException, IOException {

        int nCat = categories.size();
        double heights[] = getMeasuresInPixel(sds, fid, mt);

        double maxHeight = 0;
        double minHeight = 0;

        for (double h : heights) {
            if (h > maxHeight) {
                maxHeight = h;
            }
            if (h < minHeight) {
                minHeight = h;
            }
        }
        double cGap = DEFAULT_GAP_PX;
        if (categoryGap != null) {
            Uom.toPixel(categoryGap.getValue(sds, fid), getUom(), mt.getDpi(),
                    mt.getScaleDenominator(), null);
        }

        double cWidth = DEFAULT_WIDTH_PX;
        if (categoryWidth != null) {
            Uom.toPixel(categoryWidth.getValue(sds, fid), getUom(), mt.getDpi(),
                    mt.getScaleDenominator(), null);
        }

        double width = (nCat - 1) * cGap + nCat * cWidth;


        Rectangle2D.Double bounds = new Rectangle2D.Double(-width / 2, -maxHeight, width, maxHeight + -1 * minHeight);

        AffineTransform at = null;
        if (transform != null){
            at = transform.getGraphicalAffineTransform(false, sds, fid, mt, minHeight, minHeight);
            Shape shp = at.createTransformedShape(bounds);
            bounds.setRect(shp.getBounds2D());
        }

        RenderableGraphics g2 = Graphic.getNewRenderableGraphics(bounds, 0);

        double currentX = -width / 2;

        double xOffset[] = new double[nCat];

        int i;
        for (i = 0; i < nCat; i++) {
            //Category c = categories.get(i);
            xOffset[i] = currentX;
            currentX += cGap + cWidth;
        }

        if (areaFill != null) {
            Path2D area = new Path2D.Double();

            area.moveTo(xOffset[0] + cWidth / 2, 0);
            for (i = 0; i < nCat; i++) {
                area.lineTo(xOffset[i] + cWidth / 2, -heights[i]);
            }
            area.lineTo(xOffset[nCat - 1] + cWidth / 2, 0);
            area.closePath();

            Shape shp = area;
            if (at != null){
                shp = at.createTransformedShape(area);
            }
            areaFill.draw(g2, sds, fid, shp, selected, mt);
        }

        if (lineStroke != null) {
            System.out.println ("lineStroke");
            Path2D line = new Path2D.Double();
            line.moveTo(xOffset[0] + cWidth / 2, -heights[0]);
            for (i = 0; i < nCat; i++) {
                line.lineTo(xOffset[i] + cWidth / 2, -heights[i]);
            }
            //area.lineTo(xOffset[nCat-1]+cWidth/2, 0);
            //area.closePath();
            Shape shp = line;
            if (at != null)
                shp = at.createTransformedShape(line);
            lineStroke.draw(g2, sds, fid, shp, selected, mt);
        }

        for (i = 0; i < nCat; i++) {
            Category c = categories.get(i);
            if (c.getFill() != null || c.getStroke() != null) {
                Path2D.Double bar = new Path2D.Double();
                bar.moveTo(xOffset[i], 0);
                bar.lineTo(xOffset[i], -heights[i]);
                bar.lineTo(xOffset[i]+cWidth, -heights[i]);
                bar.lineTo(xOffset[i]+cWidth, 0);
                bar.closePath();
                Shape shp = bar;
                if (at != null){
                    shp = at.createTransformedShape(bar);
                }
                if (c.getFill() != null){
                    c.getFill().draw(g2, sds, fid, shp, selected, mt);
                }
                if (c.getStroke() != null){
                    c.getStroke().draw(g2, sds, fid, shp, selected, mt);
                }
            }
            if (c.getGraphicCollection() != null) {
                RenderableGraphics gr = c.getGraphicCollection().getGraphic(sds, fid, selected, mt);

                AffineTransform at2 = AffineTransform.getTranslateInstance(xOffset[i] + cWidth / 2, -heights[i]);
                if (at != null){
                   at2.concatenate(at);
                }
                g2.drawRenderedImage(gr.createRendering(mt.getCurrentRenderContext()), at2);
            }
        }

        return g2;
    }

    private RenderableGraphics getStackedChart(SpatialDataSourceDecorator sds, long fid, boolean selected, MapTransform mt) throws ParameterException, IOException {
        return null;
    }

    private RenderableGraphics getPolarChart(SpatialDataSourceDecorator sds, long fid, boolean selected, MapTransform mt) throws ParameterException, IOException {
        return null;
    }

    @Override
    public RenderableGraphics getRenderableGraphics(SpatialDataSourceDecorator sds, long fid, boolean selected, MapTransform mt) throws ParameterException, IOException {
        switch (subtype) {
            case POLAR:
                return getPolarChart(sds, fid, selected, mt);
            case STACKED:
                return getStackedChart(sds, fid, selected, mt);
            case ORTHO:
            default:
                return getOrthoChart(sds, fid, selected, mt);
        }
    }

    @Override
    public double getMaxWidth(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JAXBElement<AxisChartType> getJAXBElement() {

        AxisChartType a = new AxisChartType();

        if (axisScale != null) {
            a.setAxisScale(axisScale.getJAXBType());
        }

        if (categoryGap != null) {
            a.setCategoryGap(categoryGap.getJAXBParameterValueType());
        }

        if (categoryWidth != null) {
            a.setCategoryWidth(categoryWidth.getJAXBParameterValueType());
        }

        if (areaFill != null) {
            a.setFill(areaFill.getJAXBElement());
        }

        if (normalizeTo != null) {
            a.setNormalization(normalizeTo.getJAXBParameterValueType());
        }


        if (lineStroke != null) {
            a.setStroke(lineStroke.getJAXBElement());
        }

        if (transform != null) {
            a.setTransform(transform.getJAXBType());
        }

        if (uom != null) {
            a.setUnitOfMeasure(uom.toString());
        }

        switch (subtype) {
            case ORTHO:
                a.setAxisChartSubtype(AxisChartSubtypeType.ORTHO);
                break;
            case POLAR:
                a.setAxisChartSubtype(AxisChartSubtypeType.POLAR);
                break;
            case STACKED:
                a.setAxisChartSubtype(AxisChartSubtypeType.STACKED);
                break;
        }

        ObjectFactory of = new ObjectFactory();
        return of.createAxisChart(a);

    }

    @Override
    public String dependsOnFeature() {
        // TODO
        return "";
    }
}
