/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.thematic.constant;

import java.awt.Color;

/**
 *
 * @author alexis
 */
interface IUniqueSymbolLine extends UniqueSymbol {

    /**
     * Get the width of the lines to be drawn.
     * @return
     */
    public Double getLineWidth();

    /**
     * Set the width of the lines to be drawn.
     * @param d
     */
    public void setLineWidth(Double d);

    /**
     * Get the {@code String} that represent the dash pattern for this unique
     * symbol. It is made of double values separated by spaces, stored in a
     * String...
     * @return
     */
    public String getDashArray();

    /**
     * Set the {@code String} that represent the dash pattern for this unique
     * symbol. It must be made of double values separated by spaces, stored in a
     * String...
     * @param dashes
     */
    public void setDashArray(String dashes);

    /**
     * Get the {@code Color} of that will be used to draw lines.
     * @return
     */
    public Color getLineColor();

    /**
     * Set the {@code Color} of that will be used to draw lines.
     * @param col
     */
    public void setLineColor(Color col);
}
