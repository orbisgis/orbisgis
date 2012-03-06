/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.plugins.ows;

import com.vividsolutions.jts.geom.Envelope;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import net.opengis.ows._2.BoundingBoxType;
import net.opengis.ows._2.KeywordsType;
import net.opengis.ows._2.LanguageStringType;
import net.opengis.ows_context.GeneralType;
import net.opengis.ows_context.LayerType;
import net.opengis.ows_context.OWSContextType;
import net.opengis.ows_context.ObjectFactory;
import net.opengis.ows_context.OnlineResourceType;
import net.opengis.ows_context.ResourceListType;
import net.opengis.ows_context.SLDType;
import net.opengis.ows_context.StyleListType;
import net.opengis.ows_context.StyleType;
import net.opengis.ows_context.URLType;
import org.gdms.data.db.DBSource;
import org.orbisgis.core.layerModel.ILayer;

/**
 *
 * @author cleglaun
 */
public class OWSContextExporterImpl implements OWSContextExporter {

    private final OwsService owsService;
    
    public OWSContextExporterImpl(OwsService owsService) {
        this.owsService = owsService;
    }
    
    private LanguageStringType createLanguageString(String value) {
        net.opengis.ows._2.ObjectFactory factoryOws = new net.opengis.ows._2.ObjectFactory();
        LanguageStringType langString = factoryOws.createLanguageStringType();
        langString.setValue(value);
        return langString;
    }
    
    @Override
    public void exportProject(int id, String title, String description, String crs, Envelope boundingBox, ILayer[] layers) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void exportProjectAs(String title, String description, String crs, Envelope boundingBox, ILayer[] layers) {
        net.opengis.ows._2.ObjectFactory factoryOws = new net.opengis.ows._2.ObjectFactory();
        ObjectFactory factoryOwsContext = new ObjectFactory();
        
        OWSContextType owsContext = factoryOwsContext.createOWSContextType();
        JAXBElement<OWSContextType> owsContextElement = factoryOwsContext.createOWSContext(owsContext);
        
        GeneralType general = factoryOwsContext.createGeneralType();
        
        KeywordsType keywords = factoryOws.createKeywordsType();
        keywords.getKeyword().add(createLanguageString("Keyword1"));
        keywords.getKeyword().add(createLanguageString("Keyword2"));
        
        BoundingBoxType bbox = factoryOws.createBoundingBoxType();
        JAXBElement<BoundingBoxType> boundingBoxElement = factoryOws.createBoundingBox(bbox);
        bbox.setCrs(crs);
        bbox.getLowerCorner().add(boundingBox.getMinX());
        bbox.getLowerCorner().add(boundingBox.getMinY());
        bbox.getUpperCorner().add(boundingBox.getMaxX());
        bbox.getUpperCorner().add(boundingBox.getMaxY());
        
        general.setBoundingBox(boundingBoxElement);
        general.setKeywords(keywords);
        general.setTitle(createLanguageString(title));
        general.setAbstract(createLanguageString(description));
        
        ResourceListType resourceList = factoryOwsContext.createResourceListType();
        
        for (int i = 0; i < layers.length; i++) {
            
            if (layers[i].isVisible()) {
                DBSource dbs = layers[i].getDataSource().getSource().getDBSource();


                String href = String.format("%s://%s:%d/%s/%s", "pgsql", dbs.getHost(), 
                        dbs.getPort(), dbs.getDbName(), dbs.getTableName());

                LayerType layer = factoryOwsContext.createLayerType();

                URLType dataUrl = factoryOwsContext.createURLType();
                OnlineResourceType onlineResource = factoryOwsContext.createOnlineResourceType();
                onlineResource.setHref(href);
                dataUrl.setOnlineResource(onlineResource);

                StyleListType styleList = factoryOwsContext.createStyleListType();
                StyleType styleSldWrap = factoryOwsContext.createStyleType();
                SLDType sldType = factoryOwsContext.createSLDType();

                sldType.setStyle(layers[i].getStyle().getJAXBElement().getValue());
                styleSldWrap.setCurrent(Boolean.TRUE); // Note: The current model only supports 1 style per layer
                styleSldWrap.setSLD(sldType);
                styleList.getStyle().add(styleSldWrap);

                layer.setHidden(Boolean.FALSE);
                layer.getTitle().add(createLanguageString(layers[i].getName()));
                layer.setDataURL(dataUrl);
                layer.setStyleList(styleList);


                resourceList.getLayer().add(layer);
            }
        }
        
        owsContext.setResourceList(resourceList);
        owsContext.setGeneral(general);
        
        
        JAXBContext jc;
        try {
            jc = JAXBContext.newInstance("net.opengis.ows_context:net.opengis.wms._2");
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
            //marshaller.marshal(owsContextElement, System.out);
            StringWriter sw = new StringWriter();
            marshaller.marshal(owsContextElement, sw);
            System.out.println(sw.toString()); // DEBUG
            
            owsService.saveOwsFileAs(sw.toString());
        } catch (JAXBException ex) {
            Logger.getLogger(OWSContextExporterImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
}
