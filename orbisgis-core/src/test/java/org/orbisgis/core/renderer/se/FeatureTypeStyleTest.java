/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se;

import com.vividsolutions.jts.geom.Envelope;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.validation.Schema;
import junit.framework.TestCase;


import org.gdms.data.DataSourceFactory;

import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;

import org.orbisgis.core.DataManager;
import org.orbisgis.core.DefaultDataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.GdmsLayer;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.renderer.Renderer;
import org.orbisgis.core.renderer.persistance.se.FeatureTypeStyleType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;




import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 *
 * @author maxence
 */
public class FeatureTypeStyleTest extends TestCase {

    private static DataSourceFactory dsf = new DataSourceFactory();

    public static void registerDataManager() {
        // Installation of the service
        Services.registerService(
                DataManager.class,
                "Access to the sources, to its properties (indexes, etc.) and its contents, either raster or vectorial",
                new DefaultDataManager(dsf));
    }

    private static DataManager getDataManager() {
        return (DataManager) Services.getService(DataManager.class);
    }

    public FeatureTypeStyleTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        registerDataManager();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAreaSymbolizer() throws ParameterException, IOException, DriverException, InvalidStyle {
        String xml = "../../datas2tests/xmlse/fts_r_asps.xml";

        JAXBContext jaxbContext;
        try {

            jaxbContext = JAXBContext.newInstance(FeatureTypeStyleType.class);

            Unmarshaller u = jaxbContext.createUnmarshaller();


            Schema schema = u.getSchema();
            ValidationEventCollector validationCollector = new ValidationEventCollector();
            u.setEventHandler(validationCollector);


            JAXBElement<FeatureTypeStyleType> ftsElem = (JAXBElement<FeatureTypeStyleType>) u.unmarshal(
                    new FileInputStream(xml));


            for (ValidationEvent event : validationCollector.getEvents()) {
                String msg = event.getMessage();
                ValidationEventLocator locator = event.getLocator();
                int line = locator.getLineNumber();
                int column = locator.getColumnNumber();
                System.out.println("Error at line " + line + " column " + column);
                assertTrue(false);
            }

            ILayer layer = getDataManager().createLayer(new File("/home/maxence/projects/datas2tests/shp/Swiss/g4districts98_region.shp"));

            //ILayer layer = getDataManager().createLayer(new File("/data/Cartes/France/communes2.shp"));

            GdmsLayer gdmsLayer = (GdmsLayer) layer;


            FeatureTypeStyle fts = new FeatureTypeStyle(ftsElem, layer);
            gdmsLayer.setFeatureTypeStyle(fts);

            layer.getSpatialDataSource().open();

            Envelope extent = layer.getEnvelope();


            // extent = new
            // GeometryFactory().createPoint(centerPOint).buffer(20000)
            // .getEnvelopeInternal();
            BufferedImage img = new BufferedImage(1003, 646,
                    BufferedImage.TYPE_INT_ARGB);
    
            Renderer r = new Renderer();
            
            // int size = 350;
            // extent = new Envelope(new Coordinate(extent.centre().x - size,
            // extent.centre().y - size), new Coordinate(extent.centre().x
            // + size, extent.centre().y + size));
            
            r.draw(img, extent, layer);

            // ImageIO.write(img, "png", new File("/tmp/map.png"));

            System.out.println ("End of rendering...");

            layer.getSpatialDataSource().close();

            JFrame frm = new JFrame();
            frm.getContentPane().add(new JLabel(new ImageIcon(img)));
            frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frm.pack();
            frm.setLocationRelativeTo(null);
            frm.setVisible(true);


            Thread.sleep(20000);

        } catch (InterruptedException ex) {
            Logger.getLogger(FeatureTypeStyleTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DriverLoadException ex) {
            Logger.getLogger(FeatureTypeStyleTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(AreaSymbolizerTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error");
        } catch (LayerException ex) {
            Logger.getLogger(AreaSymbolizerTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error");
        }
    }
}
