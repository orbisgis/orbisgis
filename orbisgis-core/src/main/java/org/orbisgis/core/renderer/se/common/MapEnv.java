/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.common;

import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.renderable.RenderContext;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import org.orbisgis.core.map.MapTransform;

/**
 * Give access to current MapTransform and current RenderContext through
 * static method
 *
 * @author maxence
 */
public class MapEnv {

    private static MapTransform mt;
    private static final RenderContext screen;
    private static final RenderContext draft; // context for fast rendering
    private static RenderContext current;

    public static MapTransform getMapTransform() {
        return mt;
    }

    public static void setMapTransform(MapTransform mt) {
        MapEnv.mt = mt;
    }

    /**
     * Return the current scale denominator or 1.0
     *
     * @todo Throw ex when mt == null
     * @return
     */
    public static double getScaleDenominator(){
        if (mt != null){
            return mt.getScaleDenominator();
        }
        else{
            return 1.0;
        }
    }

    public static RenderContext getCurrentRenderContext() {
        return current;
    }

    public static void switchToDraft() {
        current = draft;
    }

    public static void switchToDefault() {
        current = screen;
    }

    static {
        ImageLayout layout = new ImageLayout();

        int[] bits = new int[4];

        bits[0] = 8;
        bits[1] = 8;
        bits[2] = 8;
        bits[3] = 8;

        ColorModel model = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), bits,
                true, true, Transparency.TRANSLUCENT,
                DataBuffer.TYPE_BYTE);
        layout.setColorModel(model);

        RenderingHints screenHints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, layout);
        screenHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        screenHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        screenHints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        screenHints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        screenHints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        screenHints.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

        screen = new RenderContext(AffineTransform.getTranslateInstance(0.0, 0.0), screenHints);

        RenderingHints draftHints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, layout);
        draft = new RenderContext(AffineTransform.getTranslateInstance(0.0, 0.0), draftHints);

        current = screen;
    }
}
