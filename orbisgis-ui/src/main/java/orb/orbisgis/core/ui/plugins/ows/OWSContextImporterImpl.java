/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package orb.orbisgis.core.ui.plugins.ows;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.opengis.ows_context.LayerType;
import net.opengis.ows_context.OWSContextType;
import net.opengis.ows_context.StyleType;
import net.opengis.se._2_0.core.RuleType;
import org.gdms.data.SQLDataSourceFactory;
import org.gdms.data.db.DBSource;
import org.gdms.source.SourceManager;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.Style;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author CŽdric Le Glaunec <cedric.leglaunec@gmail.com>
 */
public class OWSContextImporterImpl implements OWSContextImporter {
    
    private DocumentBuilder builder;
    static SQLDataSourceFactory dsf = new SQLDataSourceFactory();
    
    public OWSContextImporterImpl() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        // In order to get a xerces implementation which takes namespaces
        // into account
        dbf.setNamespaceAware(true); 
        builder = dbf.newDocumentBuilder();
    }
    
    private JAXBElement<OWSContextType> unmarshalDocument(Node node) throws JAXBException {
        
        JAXBContext jc = JAXBContext.newInstance("net.opengis.ows_context:net.opengis.wms._2");
        Unmarshaller unmarshaller = jc.createUnmarshaller();

        JAXBElement<OWSContextType> context = (JAXBElement<OWSContextType>) unmarshaller.unmarshal(node);
        return context;
    }

    @Override
    public List<ILayer> extractLayers(InputStream owsInput) {
        
        List<ILayer> layers = null;
        DataManager dm = Services.getService(DataManager.class);
        SourceManager sm = dm.getSourceManager();
        
            
        try {
            Document doc = builder.parse(owsInput);
            doc.getDocumentElement().normalize();
            Node node = doc.getElementsByTagName("OWSContext").item(0);
            
            JAXBElement<OWSContextType> owsContext;
            try {
                owsContext = unmarshalDocument(node);
                
                layers = new ArrayList<ILayer>();
                // Extracts each ows layer and adds it to the local data model
                Iterator<LayerType> itOwsLayers = owsContext.getValue().getResourceList().getLayer().iterator();
                while (itOwsLayers.hasNext()) {

                    LayerType owsLayer = itOwsLayers.next();


                    DbConnectionString dbConnectionString =
                            OwsContextUtils.extractDbConnectionString(owsLayer.getDataURL().getOnlineResource().getHref());

                    DBSource newDbSource = new DBSource(dbConnectionString.getHost(), dbConnectionString.getPort(),
                            dbConnectionString.getDb(), "postgres", "ieniiNg3", dbConnectionString.getTable(), "jdbc:postgresql");

                    String idLayer = dbConnectionString.getTable() + "_" + dbConnectionString.getDb();
                    sm.register(idLayer, newDbSource);

                    ILayer newLayer = dm.createLayer(idLayer);
                    newLayer.setName(owsLayer.getTitle().get(0).getValue());
                    

                    // Set current layer's styles
                    List<StyleType> styles = owsLayer.getStyleList().getStyle();
                    for (StyleType style : styles) {

                        // Todo: Use Adapter design pattern instead of this...
                        net.opengis.se._2_0.core.StyleType st = new net.opengis.se._2_0.core.StyleType();
                        st.setName(style.getSLD().getStyle().getName());
                        st.setDescription(style.getSLD().getStyle().getDescription());
                        st.setDataTypeName(style.getSLD().getStyle().getDataTypeName());
                        st.setVersion(style.getSLD().getStyle().getVersion());

                        st.getRule().clear();
                        for (RuleType rule : style.getSLD().getStyle().getRule()) {
                            st.getRule().add(rule);
                        }
                        try {
                            Style s = new Style(st, newLayer);
                            if (style.isCurrent()) {
                                newLayer.setStyle(s);

                            }
                        } catch (InvalidStyle ex) {
                            Logger.getLogger(OWSContextImporterImpl.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    layers.add(newLayer);
                }
            } catch (JAXBException ex) {
                Logger.getLogger(OWSContextImporterImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (LayerException ex) {
                Logger.getLogger(OWSContextImporterImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SAXException ex) {
            Logger.getLogger(OWSContextImporterImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OWSContextImporterImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return layers;
        
    }
}
