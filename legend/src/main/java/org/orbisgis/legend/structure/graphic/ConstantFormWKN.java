/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.structure.graphic;

import java.awt.Color;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.structure.fill.ConstantSolidFillLegend;
import org.orbisgis.legend.structure.literal.StringLiteralLegend;
import org.orbisgis.legend.structure.stroke.ConstantPenStrokeLegend;

/**
 * This abstract class is a common {@code LegendStructure} description for all the {@code
 * MarkGraphic} instances where the only varying parameters are the dimensions
 * of the associated {@code ViewBox}. That means the {@code Fill}, the {@code
 * Stroke} and the {@code StringParameter} containing the well-known name
 * definition are constants.
 * @author alexis
 */
public abstract class ConstantFormWKN extends MarkGraphicLegend {
    /**
     * Build a default {@code LegendStructure} that describes a {@code MarkGraphic}
     * instance.
     * @param viewBoxLegend
     * @param fillLegend
     * @param strokeLegend
     */
    public ConstantFormWKN(MarkGraphic mark, StringLiteralLegend wknLegend,
            LegendStructure viewBoxLegend, ConstantSolidFillLegend fillLegend,
            ConstantPenStrokeLegend strokeLegend) {
        super(mark, wknLegend, viewBoxLegend, fillLegend, strokeLegend);
    }

    /**
     * Get the {@code Color} that is used to paint the {@code SolidFill}
     * associated to this {@code ConstantWKNLegend}.
     * @return
     */
    public Color getFillColor(){
        ConstantSolidFillLegend cfl = (ConstantSolidFillLegend)getFillLegend();
        return cfl.getColor();
    }

    /**
     * Sets the {@code Color} used to paint the {@code SolidFill}
     * associated to this {@code ConstantWKNLegend}.
     * @param col
     */
    public void setFillColor(Color col){
        ConstantSolidFillLegend cfl = (ConstantSolidFillLegend)getFillLegend();
        cfl.setColor(col);
    }

    /**
     * Get the width of the line used to outline the associated {@code
     * MarkGraphic}.
     * @return
     */
    public double getLineWidth() {
        return ((ConstantPenStrokeLegend) getStrokeLegend()).getLineWidth();
    }

    /**
     * Set the width of the line used to outline the associated
     * {@code MarkGraphic}.
     * @param width
     */
    public void setLineWidth(double width) {
        ((ConstantPenStrokeLegend) getStrokeLegend()).setLineWidth(width);
    }

    /**
     * Get the color of the line used to outline the associated {@code
     * MarkGraphic}
     * @return
     */
    public Color getLineColor() {
        return ((ConstantPenStrokeLegend) getStrokeLegend()).getLineColor();
    }

    /**
     * Set the color of the line used to outline the associated {@code
     * MarkGraphic}
     * @param col
     */
    public void setLineColor(Color col) {
        ((ConstantPenStrokeLegend) getStrokeLegend()).setLineColor(col);
    }

    /**
     * Gets the dash array used to draw the outer line of this PointSymbolizer.
     * @return
     */
    public String getDashArray(){
        return ((ConstantPenStrokeLegend) getStrokeLegend()).getDashArray();
    }

    /**
     * Sets the dash array used to draw the outer line of this PointSymbolizer.
     * @param s
     */
    public void setDashArray(String s){
        ((ConstantPenStrokeLegend) getStrokeLegend()).setDashArray(s);
    }
}
