package org.orbisgis.core.renderer.se.raster;

import ij.process.ColorProcessor;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;

/**
 *
 * @author ebocher
 */
public class RasterUtils {

        /**
         * Method to change bands order only on the BufferedImage.
         *
         * @param bufferedImage
         * @return new bufferedImage
         */
        public Image invertRGB(BufferedImage bufferedImage, String bands) {

                ColorModel colorModel = bufferedImage.getColorModel();

                if (colorModel instanceof DirectColorModel) {
                        DirectColorModel directColorModel = (DirectColorModel) colorModel;
                        int red = directColorModel.getRedMask();
                        int blue = directColorModel.getBlueMask();
                        int green = directColorModel.getGreenMask();
                        int alpha = directColorModel.getAlphaMask();

                        int[] components = new int[3];
                        String bds = bands.toLowerCase();
                        components[0] = getComponent(bds.charAt(0), red, green, blue);
                        components[1] = getComponent(bds.charAt(1), red, green, blue);
                        components[2] = getComponent(bds.charAt(2), red, green, blue);

                        directColorModel = new DirectColorModel(32, components[0],
                                components[1], components[2], alpha);
                        ColorProcessor colorProcessor = new ColorProcessor(bufferedImage);
                        colorProcessor.setColorModel(directColorModel);

                        return colorProcessor.createImage();
                }
                return bufferedImage;
        }

        /**
         * Gets the component specified by the char between the int components
         * passed as parameters in red, green blue
         *
         * @param rgbChar
         * @param red
         * @param green
         * @param blue
         * @return
         */
        private int getComponent(char rgbChar, int red, int green, int blue) {
                if (rgbChar == 'r') {
                        return red;
                } else if (rgbChar == 'g') {
                        return green;
                } else if (rgbChar == 'b') {
                        return blue;
                } else {
                        return -1;
                        //throw new IllegalArgumentException(
                              //  I18N.tr("The RGB code doesn't contain RGB codes"));
                }
        }
}
