/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.thematic.choropleth;

import java.awt.Color;
import org.orbisgis.core.renderer.se.AreaSymbolizer;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.analyzer.FillAnalyzer;
import org.orbisgis.legend.structure.fill.CategorizedSolidFillLegend;
import org.orbisgis.legend.thematic.ConstantStrokeArea;

/**
 * Represents an {@code AreaSymbolizer} that is a {@link ConstantStrokeArea},
 * and whose interior is a choropleth. That means the {@code Fill} associated to
 * this {@code AreaSymbolizer} can be recognized as a {@link
 * RecodedSolidFillLegend}.
 *
 * @author alexis
 */
public class ChoroplethArea extends ConstantStrokeArea {

    private CategorizedSolidFillLegend fillLegend;

    /**
     * Build a new {@code ChoroplethArea} from the given {@code AreaSymbolizer}.
     *
     * @param symbolizer
     */
    public ChoroplethArea(AreaSymbolizer symbolizer) {
        super(symbolizer);
        Fill fill = symbolizer.getFill();
        if (fill != null) {
            LegendStructure leg = new FillAnalyzer(fill).getLegend();
            if (leg instanceof CategorizedSolidFillLegend) {
                fillLegend = (CategorizedSolidFillLegend) leg;
            } else {
                throw new IllegalArgumentException("The given symbolizer can't be"
                        + " recognized as a choropleth.");
            }
        } else {
            throw new IllegalArgumentException("You can't draw a choropleth "
                    + "without a fill !");
        }
    }

    /**
     * Get the {@code Color} that is returned when an input data can't be
     * processed.
     * @return
     */
    public Color getFallBackColor(){
        return fillLegend.getFallBackColor();
    }

    /**
     * Set the {@code Color} that must be returned when an input data can't be
     * processed.
     * @param col
     */
    public void setFallBackColor(Color col) {
        fillLegend.setFallBackColor(col);
    }

    /**
     * Get the {@code Color} that is returned for input values that are inferior
     * to the first threshold.
     * @param i
     * The index of the class we want to retrieve the {@code Color}.
     * @return
     */
    public Object getColor(int i) {
        return fillLegend.getColor(i);
    }

    /**
     * Set the {@code Color} that is returned for input values that are inferior
     * to the first threshold.
     * @param i
     * The index of the class we want to set the {@code Color}.
     * @param Col
     */
    public void setColor(int i, Color col) {
        fillLegend.setColor(i, col);
    }

    /**
     * Get the number of classes that are used in this choropleth.
     * @return
     */
    public int getNumClass() {
        return fillLegend.getNumClass();
    }

    /**
     * Get the value of the ith threshold.
     * @param i
     * @return
     */
    public double getThreshold(int i) {
        return fillLegend.getThreshold(i);
    }

    /**
     * Set the value of the ith threshold.
     * @param i
     * @param d
     */
    public void setThreshold(int i, double d) {
        fillLegend.setThreshold(i, d);
    }

    /**
     * Add a class to this choropleth.
     * @param threshold
     * @param col
     */
    public void addClass(double threshold, Color col){
        fillLegend.addClass(threshold, col);
    }

    /**
     * Remove the ith class of this choropleth
     * @param i
     */
    public void removeClass(int i) {
        fillLegend.removeClass(i);
    }

    @Override
    public String getLegendTypeName() {
        return "Choropleth";
    }


}
