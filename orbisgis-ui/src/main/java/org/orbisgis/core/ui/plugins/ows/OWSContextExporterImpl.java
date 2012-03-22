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
    private JAXBContext jc;
    private net.opengis.ows._2.ObjectFactory factoryOws;
    private ObjectFactory factoryOwsContext;
    
    public OWSContextExporterImpl(OwsService owsService) {
        this.owsService = owsService;
        try {
            jc = JAXBContext.newInstance("net.opengis.ows_context:net.opengis.wms._2");
        } catch (JAXBException ex) {
            Logger.getLogger(OWSContextExporterImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        factoryOws = new net.opengis.ows._2.ObjectFactory();
        factoryOwsContext = new ObjectFactory();
                
    }
    
    private LanguageStringType createLanguageString(String value) {
        LanguageStringType langString = factoryOws.createLanguageStringType();
        langString.setValue(value);
        return langString;
    }
    
    /**
     * Merges the last imported ows context (JAXB tree) with the given parameters.
     * WARNING, in this implementation, we recreate all children of
     * {@link ResourceListType} node. Therefore, any layer is re-created, even
     * if there has not had updates since the last import.
     * 
     * The destination depends on the {@link OwsService#saveOwsFileAs(java.lang.String) } implementation
     * 
     * @param workspace A valid workspace
     * @param owsContextElement The original ows context tree which was previously imported. (Null is not allowed)
     * @param title Project's title
     * @param description Project's description
     * @param crs project's CRS
     * @param boundingBox Bounding box
     * @param layers A list of layers belonging to the project
     * @throws NullPointerException If owsContext is null, you should consider
     * calling exportProjectAs() method.
     */
    @Override
    public void exportProject(OwsWorkspace workspace, JAXBElement<OWSContextType> owsContextElement, String title, 
        String description, String crs, Envelope boundingBox, ILayer[] layers) 
            throws NullPointerException {
        
        if (owsContextElement == null) {
            throw new NullPointerException("OWS context cannot be null.");
        }
        
        mergeJaxbWithOrbisModel(owsContextElement, title, description, crs, boundingBox, layers);
        
        try {
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
            StringWriter sw = new StringWriter();
            marshaller.marshal(owsContextElement, sw);
            
            owsService.saveOwsFile(workspace, sw.toString(), 
                    Integer.parseInt(owsContextElement.getValue().getId()));
        } catch (JAXBException ex) {
            Logger.getLogger(OWSContextExporterImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * If the given owsContextElementImported parameter is null, we simply build a new
     * JAXB tree based on this method's parameters.
     * 
     * Otherwise, we merge the given parameter into the owsContextElementImported
     * JAXB tree. WARNING, in this implementation, we recreate all children of
     * {@link ResourceListType} node. Therefore, any layer is re-created, even
     * if there has not had updates since the last import.
     * 
     * The destination depends on the {@link OwsService#saveOwsFileAs(java.lang.String) } implementation
     * 
     * @param workspace A valid workspace
     * @param owsContextElement The original ows context tree which was possibly imported. (Null allowed)
     * @param title Project's title
     * @param description Project's description
     * @param crs project's CRS
     * @param boundingBox Bounding box
     * @param layers A list of layers belonging to the project
     */
    @Override
    public void exportProjectAs(OwsWorkspace workspace, JAXBElement<OWSContextType> owsContextElementImported, 
        String title, String description, String crs, Envelope boundingBox, ILayer[] layers) {

        JAXBElement<OWSContextType> owsContextElement;
                
        if (owsContextElementImported != null) {
            owsContextElement = owsContextElementImported;
            mergeJaxbWithOrbisModel(owsContextElement, title, description, crs, boundingBox, layers);
        }
        else {
            owsContextElement = buildNewOwsContext(title, description, crs, boundingBox, layers);
        }
        
        
        
        try {
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
            StringWriter sw = new StringWriter();
            marshaller.marshal(owsContextElement, sw);
            
            owsService.saveOwsFileAs(workspace, sw.toString());
        } catch (JAXBException ex) {
            Logger.getLogger(OWSContextExporterImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    private void mergeJaxbWithOrbisModel(JAXBElement<OWSContextType> owsContextElement, 
            String title, String description, String crs, Envelope boundingBox, ILayer[] layers) {
        
        owsContextElement.getValue().getGeneral().getTitle().setValue(title);
        owsContextElement.getValue().getGeneral().getAbstract().setValue(description);
        owsContextElement.getValue().getGeneral().setBoundingBox(buildBoundingBox(boundingBox, crs));
        owsContextElement.getValue().setResourceList(buildResourceList(layers));
    }
    

    private ResourceListType buildResourceList(ILayer[] layers) {
        
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
        return resourceList;
    }
    
    private JAXBElement<BoundingBoxType> buildBoundingBox(Envelope boundingBox, String crs) {
        BoundingBoxType bbox = factoryOws.createBoundingBoxType();
        JAXBElement<BoundingBoxType> boundingBoxElement = factoryOws.createBoundingBox(bbox);
        bbox.setCrs(crs);
        bbox.getLowerCorner().add(boundingBox.getMinX());
        bbox.getLowerCorner().add(boundingBox.getMinY());
        bbox.getUpperCorner().add(boundingBox.getMaxX());
        bbox.getUpperCorner().add(boundingBox.getMaxY());
        
        return boundingBoxElement;
    }
    
    private JAXBElement<OWSContextType> buildNewOwsContext(String title, String description, 
            String crs, Envelope boundingBox, ILayer[] layers) {
        
        OWSContextType owsContext = factoryOwsContext.createOWSContextType();
        JAXBElement<OWSContextType> owsContextElement = factoryOwsContext.createOWSContext(owsContext);
        
        GeneralType general = factoryOwsContext.createGeneralType();
        
        general.setBoundingBox(buildBoundingBox(boundingBox, crs));
        general.setTitle(createLanguageString(title));
        general.setAbstract(createLanguageString(description));
        
        
        owsContext.setResourceList(buildResourceList(layers));
        owsContext.setGeneral(general);
        
        return owsContextElement;
        
    }
}
