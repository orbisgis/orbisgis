/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.scapc2;

import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.ui.plugins.views.output.OutputManager;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.common.Uom;
import java.util.logging.Level;
import com.sun.media.jai.widget.DisplayJAI;
import com.vividsolutions.jts.geom.Envelope;
import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.JFrame;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import net.opengis.ows_context.OWSContextType;
import orb.orbisgis.core.ui.plugins.ows.DbConnectionString;
import orb.orbisgis.core.ui.plugins.ows.OWSContextImporter;
import orb.orbisgis.core.ui.plugins.ows.OWSContextImporterImpl;
import orb.orbisgis.core.ui.plugins.ows.OwsContextUtils;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.SQLDataSourceFactory;
import org.gdms.data.db.DBSource;
import org.gdms.driver.DriverException;
import org.junit.Before;
import org.junit.Test;
import org.orbisgis.core.ConsoleErrorManager;
import org.orbisgis.core.ConsoleOutputManager;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.DefaultDataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.DefaultMapContext;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.progress.DefaultProgressMonitor;
import static org.junit.Assert.*;

/**
 *
 * @author CŽdric Le Glaunec <cedric.leglaunec@gmail.com>
 */
public class ImportFromOwsContextDemo {

    private static final String SERVICE_TEST_URL = "http://poulpe.heig-vd.ch/scapc2/serviceapi/web/index.php/context/10";
    static SQLDataSourceFactory dsf = new SQLDataSourceFactory();

    protected ConsoleErrorManager failErrorManager;
    protected ConsoleOutputManager failOutput;
    
    private OWSContextImporter importer;
    
    public ImportFromOwsContextDemo() throws ParserConfigurationException {
        importer = new OWSContextImporterImpl();
    }

    @Before
    public void setUp() throws Exception {

        failErrorManager = new ConsoleErrorManager();
        Services.registerService(ErrorManager.class, "", failErrorManager);
        failOutput = new ConsoleOutputManager();
        Services.registerService(OutputManager.class, "", failOutput);
    }
    
    @Test
    public void testUnmarshallingOwsContext() throws LayerException, JAXBException, DataSourceCreationException, DriverException, ParameterException {

        InputStream is = OwsContextUtils.callService(SERVICE_TEST_URL);
        List<ILayer> layers = importer.extractLayers(is);
        
        assertTrue(layers.size() == 1);
    }
    
    
    @Test
    public void testExtractDbUrn() {
        String url = "pgsql://poulpe.heig-vd.ch:5432/scapdata/g4eleccn2007_ct";
        
        DbConnectionString db = OwsContextUtils.extractDbConnectionString(url);
        
        assertTrue(db.getHost().equals("poulpe.heig-vd.ch"));
        assertTrue(db.getPort() == 5432);
        assertTrue(db.getDb().equals("scapdata"));
        assertTrue(db.getTable().equals("g4eleccn2007_ct"));
    }
    
    private void showGraphics(List<ILayer> layers) throws ParameterException {
        JFrame frame = new JFrame();
        frame.setTitle("Test ows context import");

        // Get the JFrameâ€™s ContentPane.
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        // Create an instance of DisplayJAI.
        DisplayJAI dj = new DisplayJAI();
        
        
        MapTransform mt = new MapTransform();
        double width = Uom.toPixel(370, Uom.MM, mt.getDpi(), null, null);
        double height = Uom.toPixel(260, Uom.MM, mt.getDpi(), null, null);
        
        
        BufferedImage img = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D rg = img.createGraphics();
        rg.setRenderingHints(mt.getRenderingHints());
        rg.setPaint(Color.BLUE);
        
        MapContext mapContext = new DefaultMapContext();
        try {
            mapContext.open(new DefaultProgressMonitor("task test", 100));
            
            for (ILayer layer : layers) {
                mapContext.getLayerModel().addLayer(layer);
            }
            
            Envelope env;
            try {
                env = layers.get(0).getDataSource().getFullExtent();
                mt.setExtent(env);
            } catch (DriverException ex) {
                Logger.getLogger(ImportFromOwsContextDemo.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        
            mt.setImage(img);
            mapContext.draw(mt, new DefaultProgressMonitor("task test", 100));
        } catch (LayerException ex) {
            Logger.getLogger(ImportFromOwsContextDemo.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        dj.setBounds(0, 0, (int)width, (int)height);
        dj.set(img, 0, 0);
        
        contentPane.add(dj, BorderLayout.CENTER);
        
        // Set the closing operation so the application is finished.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize((int)width, (int)height+24); // adjust the frame size.
        frame.setVisible(true); // show the frame.

    }
    
    private static DataSource createNewDataSource(DbConnectionString db) throws 
            DataSourceCreationException, DriverException {
        DBSource newDbSource = new DBSource(db.getHost(), db.getPort(),
                db.getDb(), "postgres", "ieniiNg3", db.getTable(), "jdbc:postgresql");
        
        DataSource ds = dsf.getDataSource(newDbSource);
        ds.open();
        
        return ds;
        
    }
    
    /**
     * According to the unit test in org.orbisgis.core.layerModel.LayerModelTest,
     * we can group a list of layers. It's actually a layer of type
     * LayerCollection.
     * @param layers The layers we want to group together
     * @return A layer representing a group of layers
     * @throws LayerException 
     */
    private static ILayer createLayerGroup(List<ILayer> layers) throws LayerException {
        ILayer layerGroup = getDataManager().createLayerCollection("my group");
        
        for (ILayer layer : layers) {
            layerGroup.addLayer(layer);
        }

        return layerGroup;
    }

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

    private static JAXBElement<OWSContextType> unmarshalDocument(File file) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance("net.opengis.ows_context:net.opengis.wms._2");
        Unmarshaller unmarshaller = jc.createUnmarshaller();

        JAXBElement<OWSContextType> context = (JAXBElement<OWSContextType>) unmarshaller.unmarshal(file);
        return context;
    }
    
    private void runDemo() throws ParameterException {
        InputStream is = OwsContextUtils.callService(SERVICE_TEST_URL);
        List<ILayer> layers = importer.extractLayers(is);
        showGraphics(layers);
    }
    
    public static void main(String[] args) throws LayerException, JAXBException, 
            DataSourceCreationException, DriverException, ParameterException, Exception {
        
        ImportFromOwsContextDemo demo = new ImportFromOwsContextDemo();
        demo.setUp();
        demo.runDemo();
        
    }
}
