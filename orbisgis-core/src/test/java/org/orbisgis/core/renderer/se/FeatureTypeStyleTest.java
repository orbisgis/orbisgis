/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.renderer.se;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.validation.Schema;
import net.opengis.se._2_0.core.StyleType;


import org.gdms.data.DataSourceFactory;

import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;

import org.junit.After;
import org.junit.Before;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.DefaultDataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import static org.junit.Assert.*;




import org.junit.Test;
import org.orbisgis.core.AbstractTest;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 *
 * @author Maxence Laurent
 */
public class FeatureTypeStyleTest {

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

    @Before
    public void setUp() throws Exception {
        registerDataManager();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAreaSymbolizer() throws ParameterException, IOException, DriverException, InvalidStyle {
        String xml = "src/test/resources/org/orbisgis/core/renderer/se/Districts/density_hatch_classif.se";

        JAXBContext jaxbContext;
        try {

            Unmarshaller u = Services.JAXBCONTEXT.createUnmarshaller();


            Schema schema = u.getSchema();
            ValidationEventCollector validationCollector = new ValidationEventCollector();
            u.setEventHandler(validationCollector);


            JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                    new FileInputStream(xml));


            for (ValidationEvent event : validationCollector.getEvents()) {
                String msg = event.getMessage();
                ValidationEventLocator locator = event.getLocator();
                int line = locator.getLineNumber();
                int column = locator.getColumnNumber();
                System.out.println("Error at line " + line + " column " + column);
                assertTrue(false);
            }
            assertTrue(true);

//            ILayer layer = getDataManager().createLayer(new File("/home/maxence/projects/datas2tests/shp/Swiss/g4districts98_region.shp"));

            //ILayer layer = getDataManager().createLayer(new File("/data/Cartes/France/communes2.shp"));

//            GdmsLayer gdmsLayer = (GdmsLayer) layer;
//
//
//            Style fts = new Style(ftsElem, layer);
//            gdmsLayer.setStyle(fts);
//
//            layer.getDataSource().open();
//
//            Envelope extent = layer.getEnvelope();


//            Style fts = new Style(ftsElem, layer);
//            gdmsLayer.setStyle(fts);
//
//            layer.getDataSource().open();
//
//            Envelope extent = layer.getEnvelope();


            // extent = new
            // GeometryFactory().createPoint(centerPOint).buffer(20000)
            // .getEnvelopeInternal();
//            BufferedImage img = new BufferedImage(1003, 646,
//                    BufferedImage.TYPE_INT_ARGB);
    
//            Renderer r = new ImageRenderer();
            
            // int size = 350;
            // extent = new Envelope(new Coordinate(extent.centre().x - size,
            // extent.centre().y - size), new Coordinate(extent.centre().x
            // + size, extent.centre().y + size));
            
//            r.draw(img, extent, layer);

            // ImageIO.write(img, "png", new File("/tmp/map.png"));

//            System.out.println ("End of rendering...");
//
//            layer.getDataSource().close();
//
//            JFrame frm = new JFrame();
//            frm.getContentPane().add(new JLabel(new ImageIcon(img)));
//            frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frm.pack();
//            frm.setLocationRelativeTo(null);
//            frm.setVisible(true);
//
//
//            Thread.sleep(20000);

//        } catch (InterruptedException ex) {
//            Logger.getLogger(FeatureTypeStyleTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DriverLoadException ex) {
            Logger.getLogger(FeatureTypeStyleTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(AreaSymbolizerTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error");
//        } catch (LayerException ex) {
//            Logger.getLogger(AreaSymbolizerTest.class.getName()).log(Level.SEVERE, null, ex);
//            System.out.println("error");
        }
    }
}
