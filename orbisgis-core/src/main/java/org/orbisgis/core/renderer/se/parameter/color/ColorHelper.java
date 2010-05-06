/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.parameter.color;

import java.awt.Color;

/**
 *
 * This class provides some tools for managing colors
 *
 * @author maxence
 */
public final class ColorHelper {

    /**
     * return a new color based on the given color with the specified alpha value
     * @param c
     * @param alpha
     * @return new color with alpha channel
     * @todo is alpha [0;100] or [0.0;1.0] ? 
     */
    public static final Color getColorWithAlpha(Color c, double alpha) {
        int a = (int) (255.0 * alpha/100.0);

        if (a < 0) {
            a = 0;
        } else if (a > 255) {
            a = 255;
        }
        
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
    }
}
