/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.thematic;

import java.awt.Color;
import org.orbisgis.core.renderer.se.AreaSymbolizer;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.analyzer.PenStrokeAnalyzer;
import org.orbisgis.legend.structure.stroke.ConstantPenStrokeLegend;

/**
 * Represent an {@code AreaSymbolizer} of which the {@code Stroke} is a constant
 * {@code PenStroke} instance, that can be recognized as a {@code
 * ConstantPenStrokeLegend}.
 * @author alexis
 */
public abstract class ConstantStrokeArea extends SymbolizerLegend {

    private AreaSymbolizer areaSymbolizer;

    private ConstantPenStrokeLegend strokeLegend;

    /**
     * Build a new default {@code ConstantStrokeArea} from scratch. It contains a
     * default {@code AreaSymbolizer}, which is associated to a constant {@code
     * PenStroke}.
     */
    public ConstantStrokeArea() {
        areaSymbolizer = new AreaSymbolizer();
        Stroke stroke = areaSymbolizer.getStroke();
        strokeLegend = (ConstantPenStrokeLegend) new PenStrokeAnalyzer((PenStroke) stroke).getLegend();
    }

    /**
     * Build a new {@code ConstantStrokeArea} directly from the given {@code
     * AreaSymbolizer}.
     * @param symbolizer
     */
    public ConstantStrokeArea(AreaSymbolizer symbolizer){
        areaSymbolizer=symbolizer;
        Stroke stroke = symbolizer.getStroke();
        if(stroke instanceof PenStroke){
            LegendStructure strokeLgd = new PenStrokeAnalyzer((PenStroke) stroke).getLegend();
            if(strokeLgd instanceof ConstantPenStrokeLegend){
                strokeLegend = (ConstantPenStrokeLegend) strokeLgd;
            } else {
                throw new IllegalArgumentException("The stroke of this AreaSymbolizer "
                        + "can't be recognized as a constant PenStroke.");
            }
        } else if (stroke != null){
            throw new IllegalArgumentException("We are not able to process Stroke"
                    + "that are not PenStroke.");
        }
    }

    /**
     * Get the symbolizer associated to this {@code ConstantStrokeArea}.
     * @return
     */
    @Override
    public Symbolizer getSymbolizer() {
        return areaSymbolizer;
    }

    /**
     * Get the width of the line that outlines the inner {@code AreaSymbolizer}.
     * @return
     */
    public Double getLineWidth(){
        return strokeLegend.getLineWidth();
    }

    /**
     * Set the width of the line that outlines the inner {@code AreaSymbolizer}.
     * @param width
     */
    public void setLineWidth(Double width){
        strokeLegend.setLineWidth(width);
    }

    /**
     * Get the colour of the line that outlines the inner {@code AreaSymbolizer}.
     * @return
     */
    public Color getLineColor() {
        return strokeLegend.getLineColor();
    }

    /**
     * Set the colour of the line that outlines the inner {@code AreaSymbolizer}.
     * @param col
     */
    public void setLineColor(Color col) {
        strokeLegend.setLineColor(col);
    }

    /**
     * Gets the dash array used to draw the outer line of this AreaSymbolizer.
     * @return
     */
    public String getDashArray(){
        return strokeLegend.getDashArray();
    }

    /**
     * Sets the dash array used to draw the outer line of this AreaSymbolizer.
     * @param s
     */
    public void setDashArray(String s){
        strokeLegend.setDashArray(s);
    }

}
