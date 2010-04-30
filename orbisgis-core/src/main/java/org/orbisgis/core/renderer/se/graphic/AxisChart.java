package org.orbisgis.core.renderer.se.graphic;

import java.awt.Graphics2D;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.stroke.Stroke;

/**
 *
 * @author maxence
 * @todo Implements drawGraphic
 */
public class AxisChart extends Graphic{

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
        return normalizedToPercent;
    }

    public void setNormalizedToPercent(boolean normalizedToPercent) {
        this.normalizedToPercent = normalizedToPercent;
    }

        /**
     * @param g2
     * @param ds
     * @param fid
     * @todo implements !
     */
    @Override
    public void drawGraphic(Graphics2D g2, DataSource ds, int fid){
    }




    private boolean normalizedToPercent;
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
