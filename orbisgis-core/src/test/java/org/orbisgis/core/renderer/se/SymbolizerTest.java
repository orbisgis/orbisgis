/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
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

/**
 *
 * @author maxence
 */
public class SymbolizerTest {

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
}
