/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.parameter.color;

import com.sun.media.jai.widget.DisplayJAI;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import junit.framework.TestCase;

/**
 *
 * @author maxence
 */
public class ColorHelperTest extends TestCase {
    
    public ColorHelperTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of getColorWithAlpha method, of class ColorHelper.
     */
    public void testGetColorWithAlpha() {
        System.out.println("getColorWithAlpha");
        Color c = null;
        double alpha = 0.0;
        Color expResult = null;
        Color result = ColorHelper.getColorWithAlpha(c, alpha);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of invert method, of class ColorHelper.
     */
    public void testInvert() {
        System.out.println("invert");
        Color c = null;
        Color expResult = null;
        Color result = ColorHelper.invert(c);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    public void testColorSpace(){
        BufferedImage colorSpace = ColorHelper.getColorSpaceImage();

        try {
            File file = new File("/tmp/colorSpace.png");
            ImageIO.write(colorSpace, "png", file);
        } catch (IOException ex) {
            System.out.println ("Error !");
            assertTrue(false);
        }
    }

}
