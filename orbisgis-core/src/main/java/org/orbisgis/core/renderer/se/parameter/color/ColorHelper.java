/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.parameter.color;

import java.awt.Color;

/**
 *
 * @author maxence
 */
public final class ColorHelper {

    public static final Color getColorWithAlpha(Color c, double alpha) {
        int a = (int) (255.0 * alpha);

        if (a < 0) {
            a = 0;
        } else if (a > 255) {
            a = 255;
        }
        
        Color ac = new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
        
        return ac;
    }
}
