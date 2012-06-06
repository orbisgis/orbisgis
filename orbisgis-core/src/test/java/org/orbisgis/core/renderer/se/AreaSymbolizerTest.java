/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se;


import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JPanel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import net.opengis.se._2_0.core.StyleType;
import org.junit.Test;
import org.orbisgis.core.Services;
import static org.junit.Assert.*;

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
//            MapTransform mt = new MapTransform();
//            mt.resizeImage(1200, 800);
//
//            Envelope extent = new Envelope(472212.0, 843821.0, 68786.0, 293586.0);
//
//            mt.setExtent(extent);
//
//            extent = mt.getAdjustedExtent();

//            BufferedImage img = mt.getImage();
//            Graphics2D g2 = (Graphics2D) img.getGraphics();

//            g2.setRenderingHints(mt.getCurrentRenderContext().getRenderingHints());

//            DataSourceFactory dsf = new DataSourceFactory();
//            DataSource ds = dsf.getDataSource(new File("/home/maxence/data/Geodata/Swiss/g4districts98_region.shp"));
//            ds.open();
            
//            ILayer layer = new Layer("swiss", ds);

            Style style = new Style(null, "src/test/resources/org/orbisgis/core/renderer/se/Districts/choro.se");
//            layer.setStyle(style);
//
//            Renderer renderer = new ImageRenderer();
//
//            renderer.draw(img, extent, layer);

//
//            System.out.println("Creation JFrame");
//
//            JFrame frame = new JFrame("Test AreaSymbolizer");

            // Create an instance of DisplayJAI.
//            ImagePanel panel = new ImagePanel(img);
//
//            frame.getContentPane().add(panel);

            // Set the closing operation so the application is finished.
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frame.setSize(1200, 800); // adjust the frame size.
//            frame.setVisible(true); // show the frame.


            System.out.println("Marshall");

            JAXBContext jaxbContext;

            jaxbContext = JAXBContext.newInstance(StyleType.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            //Validator validator = jaxbContext.createValidator();

            //System.out.println("Validator returned " + validator.validate(collection));
            System.out.println("Created a content tree " +
               "and marshalled it to jaxbOutput2.xml");

            marshaller.marshal(style.getJAXBElement(), new FileOutputStream("target/output.se"));
            assertTrue(true);
            //We've read and write this symbolizer, and even the global style"

    }
}
