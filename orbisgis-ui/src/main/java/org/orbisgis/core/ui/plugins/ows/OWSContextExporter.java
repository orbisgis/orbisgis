/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.plugins.ows;

import com.vividsolutions.jts.geom.Envelope;
import javax.xml.bind.JAXBElement;
import net.opengis.ows_context.OWSContextType;
import org.orbisgis.core.layerModel.ILayer;

/**
 *
 * @author cleglaun
 */
public interface OWSContextExporter {

    /**
     * Exports a project to an ows context stream. The project with the specified
     * id (in owsContextElement) should be replaced by the new one in the data repository.
     * The destination is defined by the related implementation.
     * @param owsContextElement The original ows context tree which was previously imported. (Null is not allowed)
     * @param title Project's title
     * @param description Project's description
     * @param crs project's CRS
     * @param boundingBox Bounding box
     * @param layers A list of layers belonging to the project
     * @throws NullPointerException If owsContext is null, you should consider
     * calling exportProjectAs() method.
     */
    public void exportProject(JAXBElement<OWSContextType> owsContextElement, 
            String title, String description, String crs, Envelope boundingBox, 
            ILayer[] layers) throws NullPointerException;

    /**
     * Exports a new project to an ows context stream.
     * The destination is defined by the related implementation.
     * @param owsContextElement The original ows context tree which was possibly imported. (Null allowed)
     * @param title Project's title
     * @param description Project's description
     * @param crs project's CRS
     * @param boundingBox Bounding box
     * @param layers A list of layers belonging to the project
     */
    public void exportProjectAs(JAXBElement<OWSContextType> owsContextElementImported, 
            String title, String description, String crs, Envelope boundingBox, ILayer[] layers);
}
