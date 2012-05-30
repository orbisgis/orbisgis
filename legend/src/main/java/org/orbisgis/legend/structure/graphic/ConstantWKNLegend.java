/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.structure.graphic;

import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.legend.structure.fill.ConstantSolidFillLegend;
import org.orbisgis.legend.structure.literal.StringLiteralLegend;
import org.orbisgis.legend.structure.stroke.ConstantPenStrokeLegend;
import org.orbisgis.legend.structure.viewbox.ConstantViewBox;

/**
 * A Markgraphic, defined with a well-known name, whose all parameters are
 * constant, whatever the input data.
 * @author alexis
 */
public class ConstantWKNLegend extends ConstantFormWKN{

    /**
     * Build a new {@code ConstantWKNLegend}, associated to the given {@code
     * MarkGraphic}.
     * @param mark
     * @param wknLegend
     * @param viewBoxLegend
     * @param fillLegend
     * @param strokeLegend
     */
    public ConstantWKNLegend(MarkGraphic mark, StringLiteralLegend wknLegend,
            ConstantViewBox viewBoxLegend, ConstantSolidFillLegend fillLegend,
            ConstantPenStrokeLegend strokeLegend){
        super(mark, wknLegend, viewBoxLegend, fillLegend, strokeLegend);
    }

    /**
     * Get the height of the {@code ViewBox} used to define the size of the
     * associated {@code MarkGraphic}.
     * @return
     * A {@code Double} that can be null. A {@code ViewBox} can be defined with
     * only one dimension set.
     */
    public Double getViewBoxHeight() {
        return ((ConstantViewBox)getViewBoxLegend()).getHeight();
    }

    /**
     * Get the width of the {@code ViewBox} used to define the size of the
     * associated {@code MarkGraphic}.
     * @return
     * A {@code Double} that can be null. A {@code ViewBox} can be defined with
     * only one dimension set.
     */
    public Double getViewBoxWidth() {
        return ((ConstantViewBox)getViewBoxLegend()).getWidth();
    }

    /**
     * Set the height of the {@code ViewBox} used to define the size of the
     * associated {@code MarkGraphic}.
     * @param d
     * A {@code Double} that can be null. A {@code ViewBox} can be defined with
     * only one dimension set.
     */
    public void setViewBoxHeight(Double d) {
        ((ConstantViewBox)getViewBoxLegend()).setHeight(d);
    }

    /**
     * Set the width of the {@code ViewBox} used to define the size of the
     * associated {@code MarkGraphic}.
     * @param d
     * A {@code Double} that can be null. A {@code ViewBox} can be defined with
     * only one dimension set.
     */
    public void setViewBoxWidth(Double d) {
        ((ConstantViewBox)getViewBoxLegend()).setWidth(d);
    }
}
