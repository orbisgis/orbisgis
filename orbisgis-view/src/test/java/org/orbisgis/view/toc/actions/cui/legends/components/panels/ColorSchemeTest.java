package org.orbisgis.view.toc.actions.cui.legends.panels;

import org.junit.Test;
import org.orbisgis.view.toc.actions.cui.legends.components.ColorScheme;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Tests things from ColorScheme
 * @author Alexis Guéganno
 */
public class ColorSchemeTest {

    @Test
    public void testSubset() throws Exception {
        List<Color> colors = new ArrayList<Color>();
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        ColorScheme cs = new ColorScheme("gradient",colors);
        assertTrue(cs.getSubset(3).size()==3);
    }
}
