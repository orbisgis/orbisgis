/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.thematic.constant;

import java.awt.Color;
import java.util.List;

/**
 * Represents the symbols that contains parameters associated to the management
 * of a PenStroke. This interface provides a link to all the given parameters.
 * @author alexis
 */
public interface IUniqueSymbolLine extends UniqueSymbol {

    /**
     * Get the width of the lines to be drawn.
     * @return
     */
    Double getLineWidth();

    /**
     * Set the width of the lines to be drawn.
     * @param d
     */
    void setLineWidth(Double d);

    /**
     * Get the {@code String} that represent the dash pattern for this unique
     * symbol. It is made of double values separated by spaces, stored in a
     * String...
     * @return
     */
    String getDashArray();

    /**
     * Set the {@code String} that represent the dash pattern for this unique
     * symbol. It must be made of double values separated by spaces, stored in a
     * String...
     * @param dashes
     */
    void setDashArray(String dashes);

    /**
     * Get the {@code Color} of that will be used to draw lines.
     * @return
     */
    Color getLineColor();

    /**
     * Set the {@code Color} of that will be used to draw lines.
     * @param col
     */
    void setLineColor(Color col);

    /**
     * Gets the list of {@code USParameter} that can be used to configure the
     * line contained in this {@code IUniqueSymbolLine}.
     * @return
     */
    List<USParameter<?>> getParametersLine();

}
