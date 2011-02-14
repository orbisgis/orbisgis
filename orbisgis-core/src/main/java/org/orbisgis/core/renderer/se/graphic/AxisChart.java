package org.orbisgis.core.renderer.se.graphic;

import java.io.IOException;
import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.persistance.se.AxisChartType;
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
    private boolean isPolarChart;
    private AxisScale axisScale;
    private RealParameter categoryWidth;
    private RealParameter categoryGap;
    private Fill areaFill;
    private Stroke lineStroke;

    // TODO  Other style parameters.... to be defined
    //
    // TODO Add stacked bars
    public AxisChart(){
        
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

		if (t.getNormalization() != null){
			this.setNormalizedTo(SeParameterFactory.createRealParameter(t.getNormalization()));
		}

        //this.setAxisType(t.getPolarChart() != null);
		//t.get
/*
        if (t.getCategoryWidth() != null) {
            this.setCategoryWidth(SeParameterFactory.createRealParameter(t.getCategoryWidth()));
        }

        if (t.getCategoryGap() != null) {
            this.setCategoryGap(SeParameterFactory.createRealParameter(t.getCategoryGap()));
        }

        if (t.getFill() != null) {
            this.setAreaFill(Fill.createFromJAXBElement(t.getFill()));
        }

        if (t.getStroke() != null) {
            this.setLineStroke(Stroke.createFromJAXBElement(t.getStroke()));
        }*/

        if (t.getAxisScale() != null){
            this.setAxisScale(new AxisScale(t.getAxisScale()));
        }
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

    public boolean isPolarChart() {
        return isPolarChart;
    }

    public void switchToPolarChart(){
        isPolarChart = true;
    }

    
    public void switchToAxisChart(){
        isPolarChart = true;
    }

    public void setAxisType(boolean isPolar){
        this.isPolarChart = isPolar;
    }


    public RealParameter getCategoryGap() {
        return categoryGap;
    }

    public void setCategoryGap(RealParameter categoryGap) {
        this.categoryGap = categoryGap;
		if (this.categoryGap != null){
			this.categoryGap.setContext(RealParameterContext.nonNegativeContext);
		}
    }

    public RealParameter getCategoryWidth() {
        return categoryWidth;
    }

    public void setCategoryWidth(RealParameter categoryWidth) {
        this.categoryWidth = categoryWidth;
		if (categoryWidth != null){
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

    @Override
    public RenderableGraphics getRenderableGraphics(SpatialDataSourceDecorator sds, long fid, boolean selected, MapTransform mt) throws ParameterException, IOException {
        return null; // TODO implements
    }

    @Override
    public double getMaxWidth(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JAXBElement<AxisChartType> getJAXBElement() {
		return null;
		/*
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

        if (normalizeToPercent) {
            a.setNormalize(new NormalizeType());
        }

        if (this.isPolarChart) {
            a.setPolarChart(new PolarChartType());
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

        ObjectFactory of = new ObjectFactory();
        return of.createAxisChart(a);
	*/
    }


    @Override
    public String dependsOnFeature() {
        return "";
    }


}
