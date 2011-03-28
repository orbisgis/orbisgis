/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se;

import com.vividsolutions.jts.geom.Envelope;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import junit.framework.TestCase;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.ConsoleErrorManager;
import org.orbisgis.core.ConsoleOutputManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.Layer;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.Renderer;
import net.opengis.se._2_0.core.SymbolizerType;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.ui.plugins.views.output.OutputManager;

/**
 *
 * @author maxence
 */
public class AreaSymbolizerTest extends TestCase {

    ConsoleOutputManager output;
    ConsoleErrorManager error;

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


    public AreaSymbolizerTest(String testName) throws IOException {
        super(testName);

        error = new ConsoleErrorManager();
        Services.registerService(ErrorManager.class, "", error);
        output = new ConsoleOutputManager();
        Services.registerService(OutputManager.class, "", output);

    }

    public void testAreaSymbolizer() throws ParameterException, IOException {
        try {
            MapTransform mt = new MapTransform();
            mt.resizeImage(1200, 800);

            Envelope extent = new Envelope(472212.0, 843821.0, 68786.0, 293586.0);

            mt.setExtent(extent);

            extent = mt.getAdjustedExtent();

            BufferedImage img = mt.getImage();
            Graphics2D g2 = (Graphics2D) img.getGraphics();

            g2.setRenderingHints(mt.getCurrentRenderContext().getRenderingHints());

            DataSourceFactory dsf = new DataSourceFactory();
            DataSource ds = dsf.getDataSource(new File("/home/maxence/data/Geodata/Swiss/g4districts98_region.shp"));
            ds.open();

            SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);

            ILayer layer = new Layer("swiss", sds);

            FeatureTypeStyle style = new FeatureTypeStyle(layer, "/home/maxence/projects/SCAP-c²/SE/choro.se");
            layer.setFeatureTypeStyle(style);

            Renderer renderer = new Renderer();

            renderer.draw(img, extent, layer);


            System.out.println("Creation JFrame");

            JFrame frame = new JFrame("Test AreaSymbolizer");

            // Create an instance of DisplayJAI.
            ImagePanel panel = new ImagePanel(img);

            frame.getContentPane().add(panel);

            // Set the closing operation so the application is finished.
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800); // adjust the frame size.
            frame.setVisible(true); // show the frame.


            System.out.print("Marshall");

            JAXBContext jaxbContext;

            jaxbContext = JAXBContext.newInstance(SymbolizerType.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            //Validator validator = jaxbContext.createValidator();

            //System.out.println("Validator returned " + validator.validate(collection));
            System.out.println("Created a content tree " +
               "and marshalled it to jaxbOutput2.xml");
/*
            marshaller.marshal(((Symbolizer)(aSymb)).getJAXBElement(),
                    new FileOutputStream("/tmp/aSymb.xml"));

            marshaller.marshal(((Symbolizer)(aSymb2)).getJAXBElement(),
                  new FileOutputStream("/tmp/aSymb2.xml"));


            marshaller.marshal(((Symbolizer)(aSymb3)).getJAXBElement(),
                  new FileOutputStream("/tmp/aSymb3.xml"));

            marshaller.marshal(((Symbolizer)(pSymb)).getJAXBElement(),
                  new FileOutputStream("/tmp/pSymb.xml"));


            System.out.println("See output in /tmp/symbolizer.xml " ) ;
*/

            Thread.sleep(20000);
        } catch (Exception ex) {
            Logger.getLogger(AreaSymbolizerTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error");
        }
    }
}
