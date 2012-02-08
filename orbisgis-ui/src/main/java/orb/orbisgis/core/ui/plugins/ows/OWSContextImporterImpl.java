/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package orb.orbisgis.core.ui.plugins.ows;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import net.opengis.ows_context.LayerType;
import net.opengis.ows_context.OWSContextType;
import net.opengis.ows_context.StyleType;
import net.opengis.se._2_0.core.RuleType;
import org.gdms.data.db.DBSource;
import org.gdms.source.SourceManager;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.Style;
import org.w3c.dom.Node;

/**
 *
 * @author CŽdric Le Glaunec <cedric.leglaunec@gmail.com>
 */
public class OWSContextImporterImpl implements OWSContextImporter {
    
    public OWSContextImporterImpl() {
    }
    
    @Override
    public JAXBElement<OWSContextType> unmarshallOwsContext(Node owsContextNode) {
        try {
            JAXBContext jc = JAXBContext.newInstance("net.opengis.ows_context:net.opengis.wms._2");
            Unmarshaller unmarshaller = jc.createUnmarshaller();

            JAXBElement<OWSContextType> context = 
                    (JAXBElement<OWSContextType>) unmarshaller.unmarshal(owsContextNode);
            return context;
        } catch (JAXBException ex) {
            Logger.getLogger(OWSContextImporterImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    

    @Override
    public List<ILayer> extractLayers(JAXBElement<OWSContextType> owsContext) {
        
        List<ILayer> layers = null;
        DataManager dm = Services.getService(DataManager.class);
        SourceManager sm = dm.getSourceManager();
        
            
        layers = new ArrayList<ILayer>();
        // Extracts each ows layer and adds it to the local data model
        Iterator<LayerType> itOwsLayers = owsContext.getValue().getResourceList().getLayer().iterator();
        while (itOwsLayers.hasNext()) {
            try {
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
            } catch (LayerException ex) {
                Logger.getLogger(OWSContextImporterImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return layers;
        
    }

    @Override
    public List<ILayer> extractDataSources(JAXBElement<OWSContextType> owsContext) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
