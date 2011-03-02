/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.parameter.color;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

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


    /**
     *
     * @param h [0°;360°]
     * @param s [0;1]
     * @param l [0;1]
     * @return
     */
    public static Color getColorFromHSL(double h, double s, double l){
        float chroma = (float) ((1 - Math.abs(2 * l - 1)) * s);

        double h2 = h / 60;
        float x = (float) (chroma * (1 - Math.abs((h2 % 2) - 1)));
        float r=0f, g=0f, b=0f;

        //System.out.println ("h2: " + h2);

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

        if (rm < 0.0)
            rm = 0f;
        if (gm < 0.0)
            gm = 0f;
        if (bm < 0.0)
            bm = 0f;

        //System.out.println ("" + (r+m) + "   " + (g+m) + "   " + (b+m));
        return new Color(rm, gm, bm);
    }

    public static BufferedImage getColorSpaceImage(){
        BufferedImage colorSpace = new BufferedImage(300, 500, BufferedImage.TYPE_INT_RGB);
        Graphics g2 = colorSpace.getGraphics();

        double dh = 360.0 / (colorSpace.getHeight() - 1);
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
            return (60* (g-b)/(max-min) + 360) % 360;
        } else if (max == g){
            return 60*(b-r)/(max-min) + 120;
        } else { // max == b
            return 60*(r-g)/(max-min) + 240;
        }
    }

    public static float getLightness(Color color){
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        int max = Math.max(r, Math.max(g, b));
        int min = Math.min(r, Math.min(g, b));

        return ((max + min)*0.5f) / 255;
    }
}