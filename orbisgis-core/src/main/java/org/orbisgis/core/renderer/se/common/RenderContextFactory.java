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

/**
 *
 *
 * @author maxence
 */
public class RenderContextFactory {


    public static RenderContext getContext(){
        return current;
    }

    public static void switchToDraft(){
        current = draft;
    }

    private static RenderContext screen;
    private static RenderContext current;
    private static RenderContext draft; // context for fast rendering

    static{
        ImageLayout layout = new ImageLayout();

        int[] bits = new int[4];

        bits[0] = 8;
        bits[1] = 8;
        bits[2] = 8;
        bits[3] = 8;

        ColorModel model = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), bits,
                true, false, Transparency.TRANSLUCENT,
                DataBuffer.TYPE_BYTE);
        layout.setColorModel(model);

        RenderingHints hints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT, layout);
        screen = new RenderContext(AffineTransform.getTranslateInstance(0.0, 0.0), hints);

        current = screen;
    }
}
