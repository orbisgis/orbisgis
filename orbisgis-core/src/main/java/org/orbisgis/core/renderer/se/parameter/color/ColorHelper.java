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
    public static Color getColorWithAlpha(Color c, double alpha) {
        int a = (int) (255.0 * alpha/100.0);

        if (a < 0) {
            a = 0;
        } else if (a > 255) {
            a = 255;
        }

		if (c != null){
        	return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
		}else{
        	return new Color(255, 255, 255, a); // WhiteTrans
		}

    }

    /*
     * Return a new inverted color with same alpha.
     * If the resulting color is too dark or too light, a standard yellow is returned
     */
    public static Color invert(Color c){
        //  TODO : improve
        int a = c.getAlpha();
        int r = 255 - c.getRed();
        int g = 255 - c.getGreen();
        int b = 255 - c.getBlue();

        // if the resulting color is to light (e.g. initial color is black, resulting color is white...)
        if ((r + g + b > 740) || (r + g + b < 20)){
            // return a standard yellow
            return new Color(255, 255, 40, a);
        }
        else{
            return new Color(r,g,b,a);
        }
    }
}