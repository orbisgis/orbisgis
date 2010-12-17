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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.validation.Schema;
import junit.framework.TestCase;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.persistance.se.SymbolizerType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 *
 * @author maxence
 */
public class SymbolizerTest extends TestCase {

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

    public SymbolizerTest(String testName) {
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

    public void testMarshall() throws ParameterException, InvalidStyle {

        //String xml = "../../datas2tests/xmlse/as_gf.xml";
        //String xml = "../../datas2tests/xmlse/pSymb.xml";
        String xml = "../../datas2tests/xmlse/point.xml";

        JAXBContext jaxbContext;
        try {

            jaxbContext = JAXBContext.newInstance(SymbolizerType.class);

            Unmarshaller u = jaxbContext.createUnmarshaller();


            Schema schema = u.getSchema();
            ValidationEventCollector validationCollector = new ValidationEventCollector();
            u.setEventHandler(validationCollector);




            JAXBElement<? extends SymbolizerType> symb = (JAXBElement<? extends SymbolizerType>) u.unmarshal(
                    new FileInputStream(xml));


            for (ValidationEvent event : validationCollector.getEvents()) {
                String msg = event.getMessage();
                ValidationEventLocator locator = event.getLocator();
                int line = locator.getLineNumber();
                int column = locator.getColumnNumber();
                System.out.println("Error at line " + line + " column " + column);
                assertTrue(false);
            }


            CompositeSymbolizer cs = new CompositeSymbolizer(symb);


            MapTransform mt = new MapTransform();
            mt.resizeImage(1400, 1000);

            Envelope extent = new Envelope(472212.0, 843821.0, 68786.0, 293586.0);

            mt.setExtent(extent);

            extent = mt.getAdjustedExtent();

            BufferedImage img = mt.getImage();
            Graphics2D g2 = img.createGraphics();

            g2.setRenderingHints(mt.getCurrentRenderContext().getRenderingHints());

            DataSourceFactory dsf = new DataSourceFactory();
            DataSource ds;
            //ds = dsf.getDataSource(new File("../../datas2tests/shp/Swiss/g4districts98_region.shp"));

            ds = dsf.getDataSource(new File("/data/Cartes/Europe/EUcountries_them.shp"));

            ds.open();

            SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);



            System.out.println("Avant Symbolizers");


            long fid;
            for (fid = 0; fid < ds.getRowCount(); fid++) {
                cs.draw(g2, sds, fid, false, mt);
            }

            System.out.println("Creation JFrame");

            JFrame frame = new JFrame("Test AreaSymbolizer");

            // Create an instance of DisplayJAI.
            ImagePanel panel = new ImagePanel(img);

            frame.getContentPane().add(panel);

            // Set the closing operation so the application is finished.
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400, 1000); // adjust the frame size.
            frame.setVisible(true); // show the frame.

            try {
                Thread.sleep(20000);
            } catch (InterruptedException ex) {
                Logger.getLogger(SymbolizerTest.class.getName()).log(Level.SEVERE, null, ex);
            }


            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            marshaller.marshal(cs.getJAXBElement(),
                    new FileOutputStream("/tmp/resSymb.xml"));

        //} catch (ParameterException ex) {
        //    Logger.getLogger(SymbolizerTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DataSourceCreationException ex) {
            Logger.getLogger(SymbolizerTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DriverException ex) {
            Logger.getLogger(SymbolizerTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SymbolizerTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DriverLoadException ex) {
            Logger.getLogger(SymbolizerTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(AreaSymbolizerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
