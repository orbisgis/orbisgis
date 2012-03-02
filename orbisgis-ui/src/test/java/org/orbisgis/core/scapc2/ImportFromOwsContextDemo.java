/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.scapc2;

import org.w3c.dom.Node;
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
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import net.opengis.ows._2.BoundingBoxType;
import net.opengis.ows._2.KeywordsType;
import net.opengis.ows._2.LanguageStringType;
import net.opengis.ows_context.GeneralType;
import net.opengis.ows_context.LayerType;
import net.opengis.ows_context.MethodType;
import net.opengis.ows_context.OWSContextType;
import net.opengis.ows_context.ObjectFactory;
import net.opengis.ows_context.OnlineResourceType;
import net.opengis.ows_context.ResourceListType;
import net.opengis.ows_context.URLType;
import org.orbisgis.core.ui.plugins.ows.DbConnectionString;
import org.orbisgis.core.ui.plugins.ows.OWSContextImporter;
import org.orbisgis.core.ui.plugins.ows.OWSContextImporterImpl;
import org.orbisgis.core.ui.plugins.ows.OwsContextUtils;
import org.orbisgis.core.ui.plugins.ows.OwsService;
import org.orbisgis.core.ui.plugins.ows.OwsServiceImpl;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.SQLDataSourceFactory;
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
import org.xml.sax.SAXException;

/**
 *
 * @author CŽdric Le Glaunec <cedric.leglaunec@gmail.com>
 */
public class ImportFromOwsContextDemo {

    static SQLDataSourceFactory dsf = new SQLDataSourceFactory();

    protected ConsoleErrorManager failErrorManager;
    protected ConsoleOutputManager failOutput;
    
    private OWSContextImporter importer;
    private OwsService owsService;
    
    public ImportFromOwsContextDemo() throws ParserConfigurationException, SAXException {
        importer = new OWSContextImporterImpl();
        owsService = new OwsServiceImpl();
    }

    @Before
    public void setUp() throws Exception {

        registerDataManager();
        failErrorManager = new ConsoleErrorManager();
        Services.registerService(ErrorManager.class, "", failErrorManager);
        failOutput = new ConsoleOutputManager();
        Services.registerService(OutputManager.class, "", failOutput);
    }
    
    @Test
    public void testUnmarshallingOwsContext() throws LayerException, JAXBException, DataSourceCreationException, DriverException, ParameterException {
        Node owsContextNode = owsService.getOwsFile(10);
        JAXBElement<OWSContextType> owsContext = importer.unmarshallOwsContext(owsContextNode);
        List<ILayer> layers = importer.extractLayers(owsContext);
        
        assertTrue(layers.size() == 1);
    }
    
    private LanguageStringType createLanguageString(String value) {
        net.opengis.ows._2.ObjectFactory factoryOws = new net.opengis.ows._2.ObjectFactory();
        LanguageStringType langString = factoryOws.createLanguageStringType();
        langString.setValue(value);

        return langString;
    }
    
    @Test
    public void testCreateBasicOwsContextFile() throws JAXBException, ParserConfigurationException {
        net.opengis.ows._2.ObjectFactory factoryOws = new net.opengis.ows._2.ObjectFactory();
        ObjectFactory factoryOwsContext = new ObjectFactory();
        
        OWSContextType owsContext = factoryOwsContext.createOWSContextType();
        JAXBElement<OWSContextType> owsContextElement = factoryOwsContext.createOWSContext(owsContext);
        
        GeneralType general = factoryOwsContext.createGeneralType();
        
        KeywordsType keywords = factoryOws.createKeywordsType();
        keywords.getKeyword().add(createLanguageString("Switzerland"));
        keywords.getKeyword().add(createLanguageString("France"));
        
        BoundingBoxType boundingBox = factoryOws.createBoundingBoxType();
        JAXBElement<BoundingBoxType> boundingBoxElement = factoryOws.createBoundingBox(boundingBox);
        boundingBox.setCrs("EPSG:21781");
        boundingBox.getLowerCorner().add(new Double(485472));
        boundingBox.getLowerCorner().add(new Double(75285));
        boundingBox.getUpperCorner().add(new Double(833838));
        boundingBox.getUpperCorner().add(new Double(295935));
        
        general.setBoundingBox(boundingBoxElement);
        general.setKeywords(keywords);
        general.setTitle(createLanguageString("This is a title"));
        general.setAbstract(createLanguageString("This is an abstract"));
        
        
        LayerType layer = factoryOwsContext.createLayerType();

        URLType dataUrl = factoryOwsContext.createURLType();
        OnlineResourceType onlineResource = factoryOwsContext.createOnlineResourceType();
        onlineResource.setHref("pgsql://127.0.0.1:5432/scapdata/g4districts98");
        dataUrl.setOnlineResource(onlineResource);
        
        layer.setHidden(Boolean.FALSE);
        layer.getTitle().add(createLanguageString("Layer's title"));
        layer.setDataURL(dataUrl);
        
        ResourceListType resourceList = factoryOwsContext.createResourceListType();
        resourceList.getLayer().add(layer);
        
        owsContext.setResourceList(resourceList);
        owsContext.setGeneral(general);
        
        
        JAXBContext jc = JAXBContext.newInstance("net.opengis.ows_context:net.opengis.wms._2");
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
        marshaller.marshal(owsContextElement, System.out);
        
        
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Node nodeOwsContext = documentBuilder.newDocument();
        marshaller.marshal(owsContextElement, nodeOwsContext);
        
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        JAXBElement<OWSContextType> owsContextImported = importer.unmarshallOwsContext(nodeOwsContext);

        assertTrue(owsContextImported.getValue().getResourceList().getLayer().size() == 1);
        assertTrue(owsContextImported.getValue().getGeneral().getTitle().getValue().equals("This is a title"));
        assertTrue(owsContextImported.getValue().getGeneral().getAbstract().getValue().equals("This is an abstract"));
        assertTrue(owsContextElement.getValue().getResourceList().getLayer().get(0).getDataURL().getOnlineResource().getHref().equals("pgsql://127.0.0.1:5432/scapdata/g4districts98"));
        
    }
    
    @Test
    public void testExportOwsProject() {
        
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
    
//    private void runDemo() throws ParameterException {
//        InputStream is = OwsContextUtils.callService(SERVICE_TEST_URL);
//        List<ILayer> layers = importer.extractLayers(is);
//        showGraphics(layers);
//    }
//    
//    public static void main(String[] args) throws LayerException, JAXBException, 
//            DataSourceCreationException, DriverException, ParameterException, Exception {
//        
//        ImportFromOwsContextDemo demo = new ImportFromOwsContextDemo();
//        demo.setUp();
//        demo.runDemo();
//        
//    }
}
