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
interface IUniqueSymbolArea extends IUniqueSymbolLine {

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

}
