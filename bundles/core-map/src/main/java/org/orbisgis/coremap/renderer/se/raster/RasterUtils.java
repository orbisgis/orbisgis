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
package org.orbisgis.coremap.renderer.se.raster;

import ij.process.ColorProcessor;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;

/**
 *
 * @author Erwan Bocher
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
