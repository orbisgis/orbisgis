/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.structure.stroke;

import java.awt.Color;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.structure.fill.ConstantSolidFillLegend;


/**
 * Represents a {@code PenStroke} where the Color and the dash pattern are
 * constant.
 * @author alexis
 */
public abstract class ConstantColorAndDashesPSLegend extends PenStrokeLegend {

    /**
     * Build an instance of {@code ConstantColorAndDashesPSLegend} using the
     * given parameters.
     * @param ps
     * @param width
     * @param fill
     * @param dash 
     */
    public ConstantColorAndDashesPSLegend(PenStroke ps, LegendStructure width,
            LegendStructure fill, LegendStructure dash) {
        super(ps, width, fill, dash);
    }

    /**
     * Gets the {@code Color} of the associated {@code PenStroke}.
     * @return
     */
    public Color getLineColor() {
        return ((ConstantSolidFillLegend)getFillAnalysis()).getColor();
    }

    /**
     * Sets the {@code Color} of the associated {@code PenStroke}.
     * @param col
     */
    public void setLineColor(Color col) {
        ((ConstantSolidFillLegend)getFillAnalysis()).setColor(col);
    }



}
