/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se;


import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import javax.swing.JPanel;
import javax.xml.bind.Marshaller;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.se.parameter.UsedAnalysis;

/**
 *
 * @author maxence
 */
public class AreaSymbolizerTest {

    private class ImagePanel extends JPanel {

        private BufferedImage img;

        ImagePanel(BufferedImage img) {
            super();
            this.img = img;
        }

        @Override
        public void paintComponent(Graphics g) {
            //super.paintComponent(g);
            g.drawImage(img, 0, 0, null);
            //   ((Graphics2D) g).setStroke(new BasicStroke(10.0f));
            // g.drawLine(0, 0, 1000, 1000);
        }
    }

    @Test
    public void testAreaSymbolizer() throws Exception {
            Style style = new Style(null, "src/test/resources/org/orbisgis/core/renderer/se/Districts/choro.se");
            Marshaller marshaller = Services.JAXBCONTEXT.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(style.getJAXBElement(), new FileOutputStream("target/output.se"));
            assertTrue(true);
    }

    @Test
    public void testCategorizeUsedAnalysis() throws Exception {
        Style style = new Style(null, "src/test/resources/org/orbisgis/core/renderer/se/colorCategorize.se");
        AreaSymbolizer ps =(AreaSymbolizer) style.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        UsedAnalysis ua = ps.getUsedAnalysis();
        assertTrue(ua.isCategorizeUsed());
    }
}
