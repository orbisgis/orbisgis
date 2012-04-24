/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.HashSet;
import javax.swing.JPanel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.validation.Schema;
import net.opengis.se._2_0.core.SymbolizerType;
import org.junit.Test;
import static org.junit.Assert.*;
import org.orbisgis.core.AbstractTest;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.color.Recode2Color;
import org.orbisgis.core.renderer.se.parameter.string.StringAttribute;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;

/**
 *
 * @author maxence
 */
public class SymbolizerTest extends AbstractTest {

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
    public void testMarshallInvalidSeFile() throws Exception {
            //The following file contains an invalid markup that MUST NOT be recognized.
        String xml = "src/test/resources/org/orbisgis/core/renderer/se/invalidCategorize.se";

        JAXBContext jaxbContext;

            jaxbContext = JAXBContext.newInstance(SymbolizerType.class);

            Unmarshaller u = jaxbContext.createUnmarshaller();


            Schema schema = u.getSchema();
            ValidationEventCollector validationCollector = new ValidationEventCollector();
            u.setEventHandler(validationCollector);




            JAXBElement<? extends SymbolizerType> symb = (JAXBElement<? extends SymbolizerType>) u.unmarshal(
                    new FileInputStream(xml));

            if(validationCollector.getEvents().length == 0){
                    fail();
            }

            for (ValidationEvent event : validationCollector.getEvents()) {
                String msg = event.getMessage();
                ValidationEventLocator locator = event.getLocator();
                int line = locator.getLineNumber();
                int column = locator.getColumnNumber();
                System.out.println("Error at line " + line + " column " + column);
                //We've encountered an error - everything's normal.
                assertTrue(true);
            }
    }
    
    @Test 
    public void testDependsOnFeature() throws Exception {
        String xml = "src/test/resources/org/orbisgis/core/renderer/se/symbol_prop_canton_interpol_lin.se";
        Style fts = new Style(null, xml);
        HashSet<String> feat = fts.dependsOnFeature();
        assertTrue(feat.size() == 1);
        assertTrue(feat.contains("PTOT99"));
        AreaSymbolizer as = (AreaSymbolizer)fts.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        SolidFill fill = (SolidFill) as.getFill();
        StringAttribute sa = new StringAttribute("PTOT99");
        Recode2Color rc = new Recode2Color(new ColorLiteral("#887766"), sa);
        rc.addMapItem("bonjour", new ColorLiteral("#546576"));
        fill.setColor(rc);
        feat = fts.dependsOnFeature();
        assertTrue(feat.size() == 1);
        assertTrue(feat.contains("PTOT99"));
        sa.setColumnName("ohhai");
        feat = fts.dependsOnFeature();
        assertTrue(feat.size() == 2);
        assertTrue(feat.contains("PTOT99"));
        assertTrue(feat.contains("ohhai"));
        
    }
}
