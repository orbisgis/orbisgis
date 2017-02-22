/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Maxence Laurent
 */
public class ColorHelperTest {

    /**
     * Test of getColorWithAlpha method, of class ColorHelper.
     */
        @Test
    public void testGetColorWithAlpha() {
        System.out.println("getColorWithAlpha");
        Color c = new Color(40, 40, 40);
        double alpha = 0.5;
        Color expResult = new Color(40, 40, 40, 127);
        Color result = ColorHelper.getColorWithAlpha(c, alpha);
        assertEquals(expResult, result);
        alpha = -1.0;
        result = ColorHelper.getColorWithAlpha(c, alpha);
        expResult = new Color(40, 40, 40, 0);
        alpha = 2.0;
        result = ColorHelper.getColorWithAlpha(c, alpha);
        expResult = new Color(40, 40, 40, 255);
    }

    /**
     * Test of invert method, of class ColorHelper.
     */
        @Test
    public void testInvert() {
        System.out.println("invert");
        Color c = new Color(40, 40, 40);
        Color expResult = new Color(215, 215, 215);
        Color result = ColorHelper.invert(c);
        assertEquals(expResult, result);
        //When the inverted color is too dark or too light, we are supposed to obtain some default yellow.
        c = new Color(5,5,5);
        expResult = new Color(ColorHelper.MAX_RGB_VALUE, ColorHelper.MAX_RGB_VALUE, 40);
        result = ColorHelper.invert(c);
        assertEquals(expResult, result);
        c = new Color(250,250,250);
        expResult = new Color(ColorHelper.MAX_RGB_VALUE, ColorHelper.MAX_RGB_VALUE, 40);
        result = ColorHelper.invert(c);
        assertEquals(expResult, result);
        
    }

        @Test
    public void testColorSpace() throws IOException{
        BufferedImage colorSpace = ColorHelper.getColorSpaceImage();
            File file = new File("/tmp/colorSpace.png");
            ImageIO.write(colorSpace, "png", file);
    }

}
