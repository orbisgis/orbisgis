/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.coremap.renderer.se.parameter.color;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 *
 * This class provides some tools for managing colors
 *
 * @author Maxence Laurent
 */
public final class ColorHelper {

        /**
         * The greatest possible value foa a RGB component.
         */
        public static final int MAX_RGB_VALUE = 255;
        
        private static final float INTERVAL_SIZE = 60.0f;
        private static final float DEGREES = 360.0f;
        /**
         * Set constructor to private, as we're not supposed to instanciate any instance
         * of this class.
         */
        private ColorHelper(){}
        
    /**
     * return a new color based on the given color with the specified alpha value
     * @param c
     * @param alpha [0;1]
     * @return new color with alpha channel
     */
    public static Color getColorWithAlpha(Color c, double alpha) {
        int a = (int) (MAX_RGB_VALUE * alpha);

        if (a < 0) {
            a = 0;
        } else if (a > MAX_RGB_VALUE) {
            a = MAX_RGB_VALUE;
        }

		if (c != null){
        	return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
		}else{
        	return new Color(MAX_RGB_VALUE, MAX_RGB_VALUE, 
                        MAX_RGB_VALUE, a); // WhiteTrans
		}

    }

    /*
     * Return a new inverted color with same alpha.
     * If the resulting color is too dark or too light, a standard yellow is returned
     */
    public static Color invert(Color c){
        //  TODO : improve
        int a = c.getAlpha();
        int r = MAX_RGB_VALUE - c.getRed();
        int g = MAX_RGB_VALUE - c.getGreen();
        int b = MAX_RGB_VALUE - c.getBlue();

        // if the resulting color is to light (e.g. initial color is black, resulting color is white...)
        if ((r + g + b > 740) || (r + g + b < 20)){
            // return a standard yellow
            return new Color(MAX_RGB_VALUE, MAX_RGB_VALUE, 40, a);
        }
        else{
            return new Color(r,g,b,a);
        }
    }


    /**
     * HSL stands for hue, saturation, and lightness, and is a cylindrical representation
     * of RGB values.
     * @param h [0°;360°]
     * @param s [0;1]
     * @param l [0;1]
     * @return
     */
    public static Color getColorFromHSL(double h, double s, double l){
        float chroma = (float) ((1 - Math.abs(2 * l - 1)) * s);

        final double h2 = h / INTERVAL_SIZE;
        final float x = (float) (chroma * (1 - Math.abs((h2 % 2) - 1)));
        float r=0f, g=0f, b=0f;

        if (h2 < 1.0){
            r = chroma;
            g = x;
            b = 0f;
        } else if (h2 < 2.0){
            r = x;
            g = chroma;
            b = 0f;
        } else if (h2 < 3.0){
            r = 0;
            g = chroma;
            b = x;
        } else if (h2 < 4.0){
            r = 0;
            g = x;
            b = chroma;
        } else if (h2 < 5.0){
            r = x;
            g = 0f;
            b = chroma;
        } else if (h2 < 6.0){
            r = chroma;
            g = 0f;
            b = x;
        }

        float m = (float) (l - 0.5 * chroma);

        float rm = r+m;
        float gm  = g+m;
        float bm = b+m;

        if (rm < 0.0) {
            rm = 0f;
        }
        if (gm < 0.0) {
            gm = 0f;
        }
        if (bm < 0.0) {
            bm = 0f;
        }

        return new Color(rm, gm, bm);
    }

    /**
     * This method can be used  to retrieve a rectangular image filled with a
     * continuous colour variation. Can be used to pick a colour, for instance.
     * @return
     *
     */
    public static BufferedImage getColorSpaceImage(){
        BufferedImage colorSpace = new BufferedImage(150,250, BufferedImage.TYPE_INT_RGB);
        Graphics g2 = colorSpace.getGraphics();

        double dh = (double) DEGREES / (colorSpace.getHeight() - 1);
        double dl = 1.0 / (colorSpace.getWidth() - 1);

        g2.setColor(Color.red);
        g2.fillRect(0, 0, colorSpace.getWidth(), colorSpace.getHeight());
        int x, y;
        double h, s, l;
        s = 0.5f;
        for (x = 0; x < colorSpace.getWidth(); x++) {
            l = (x * dl);
            for (y = 0; y < colorSpace.getHeight(); y++) {
                h = dh*y;
                Color hslColor = ColorHelper.getColorFromHSL(h, s, l);
                g2.setColor(hslColor);
                g2.fillRect(x, y, 1, 1);
            }
        }

        return colorSpace;
    }

    /**
     * Extract the hue component (HSL color space) from a RGB color
     * @param color  the color the extract the hue from
     * @return hue [0;360]
     */
    public static float getHue(Color color){
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        int max = Math.max(r, Math.max(g, b));
        int min = Math.min(r, Math.min(g, b));

        if (max == min){
            return 0f;
        } else if (max == r){
            return (INTERVAL_SIZE* (g-b)/(max-min) + (int)DEGREES) % (int)DEGREES;
        } else if (max == g){
            return INTERVAL_SIZE*(b-r)/(max-min) + 120;
        } else { // max == b
            return INTERVAL_SIZE*(r-g)/(max-min) + 240;
        }
    }

    /**
     * Get the lightness associated to the given {@code Color}.
     *
     * @param color
     * @return
     * The lightness, as a float between 0 and 1.
     */
    public static float getLightness(Color color){
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        int max = Math.max(r, Math.max(g, b));
        int min = Math.min(r, Math.min(g, b));

        return ((max + min)*0.5f) / (float) MAX_RGB_VALUE;
    }
}