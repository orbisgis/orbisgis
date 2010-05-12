package org.orbisgis.core.renderer.se.graphic;


import java.io.IOException;
import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.persistance.se.AxisChartType;
import org.orbisgis.core.renderer.persistance.se.NormalizeType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.PolarChartType;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.stroke.Stroke;

/**
 *
 * @author maxence
 * @todo Implements drawGraphic
 */
public class AxisChart extends Graphic {

    public enum AxisType {

        POLAR, DISCRETE;
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

    public AxisType getAxisType() {
        return axisType;
    }

    public void setAxisType(AxisType axisType) {
        this.axisType = axisType;
    }

    public RealParameter getCategoryGap() {
        return categoryGap;
    }

    public void setCategoryGap(RealParameter categoryGap) {
        this.categoryGap = categoryGap;
    }

    public RealParameter getCategoryWidth() {
        return categoryWidth;
    }

    public void setCategoryWidth(RealParameter categoryWidth) {
        this.categoryWidth = categoryWidth;
    }

    public Stroke getLineStroke() {
        return lineStroke;
    }

    public void setLineStroke(Stroke lineStroke) {
        this.lineStroke = lineStroke;
        lineStroke.setParent(this);
    }

    public boolean isNormalizedToPercent() {
        return normalizeToPercent;
    }

    public void setNormalizedToPercent(boolean normalizedToPercent) {
        this.normalizeToPercent = normalizedToPercent;
    }

    @Override
    public RenderableGraphics getRenderableGraphics(DataSource ds, long fid) throws ParameterException, IOException {
        return null; // TODO implements
    }

    @Override
    public double getMaxWidth(DataSource ds, long fid) throws ParameterException, IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JAXBElement<AxisChartType> getJAXBInstance() {
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
            a.setFill(areaFill.getJAXBInstance());
        }

        if (normalizeToPercent) {
            a.setNormalize(new NormalizeType());
        }

        if (1 == 0) {
            a.setPolarChart(new PolarChartType());
        }

        if (lineStroke != null) {
            a.setStroke(lineStroke.getJAXBInstance());
        }

        if (transform != null) {
            a.setTransform(transform.getJAXBType());
        }

        if (uom != null) {
            a.setUnitOfMeasure(uom.toString());
        }

        ObjectFactory of = new ObjectFactory();
        return of.createAxisChart(a);
    }
    private boolean normalizeToPercent;
    private AxisScale axisScale;
    private RealParameter categoryWidth;
    private RealParameter categoryGap;
    private Fill areaFill;
    private Stroke lineStroke;
    private AxisType axisType;
    // TODO  Other style parameters.... to be defined
    //
    // TODO Add stacked bars
}
