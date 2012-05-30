/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.thematic.constant;

import java.awt.Color;
import java.util.List;

/**
 * Represents the symbols that contains parameters associated to the management
 * of a solid fill. This interface provides a link to all the given parameters.
 * @author alexis
 */
public interface IUniqueSymbolArea extends IUniqueSymbolLine {

    /**
     * Get the {@code Color} that will be used to fill the area to be displayed.
     * @return
     */
    Color getFillColor();

    /**
     * Set the {@code Color} that will be used to fill the area to be displayed.
     * @param col
     */
    void setFillColor(Color col);

    /**
     * Gets the list of {@code USParameter} that can be used to configure the
     * line contained in this {@code IUniqueSymbolArea}.
     * @return
     */
    List<USParameter<?>> getParametersArea();

}
