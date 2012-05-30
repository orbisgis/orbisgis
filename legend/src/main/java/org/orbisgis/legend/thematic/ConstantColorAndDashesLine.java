/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.thematic;

import java.awt.Color;
import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.structure.stroke.ConstantColorAndDashesPSLegend;

/**
 * Abstract a representation of line whose {@code Color} and dash array are
 * constant, but where the management of the width is unknown.
 * @author alexis
 */
public abstract class ConstantColorAndDashesLine extends SymbolizerLegend {

    private LineSymbolizer lineSymbolizer;

    /**
     * Basically sets the inner {@code LineSymbolizer}.
     * @param symbolizer
     */
    public ConstantColorAndDashesLine(LineSymbolizer symbolizer){
        lineSymbolizer = symbolizer;
    }

    /**
     * Get the associated {@code LineSymbolizer};
     * @return
     */
    @Override
    public Symbolizer getSymbolizer() {
        return lineSymbolizer;
    }

    /**
     * Get the {@code LegendStructure} associated to the {@code Stroke} of this
     * {@code ConstantColorAndDashesLine}. This method is abstract, as it is the
     * only particularity of the implementations.
     * @return
     */
    public abstract LegendStructure getStrokeLegend();


    /**
     * Get the {@code Color} of that will be used to draw lines.
     * @return
     */
    public Color getLineColor() {
        return ((ConstantColorAndDashesPSLegend) getStrokeLegend()).getLineColor();
    }

    /**
     * Set the {@code Color} of that will be used to draw lines.
     * @param col
     */
    public void setLineColor(Color col) {
        ((ConstantColorAndDashesPSLegend) getStrokeLegend()).setLineColor(col);
    }
}
