/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.plugins.ows;

import java.util.List;
import javax.xml.bind.JAXBElement;
import net.opengis.ows_context.OWSContextType;
import org.orbisgis.core.layerModel.ILayer;
import org.w3c.dom.Node;


/**
 *
 * @author Cedric Le Glaunec <cedric.leglaunec@gmail.com>
 */
public interface OWSContextImporter {
    
    /**
     * Transforms an ows context 
     * @param owsContextNode The ows context file we want to unmarshall
     * @return 
     */
    public JAXBElement<OWSContextType> unmarshallOwsContext(Node owsContextNode);
    
    /**
     * Extracts every layer from the given file
     * @param owsContext The ows context
     * @return The layers with related SE Styles
     */
    public List<ILayer> extractLayers(JAXBElement<OWSContextType> owsContext);
    
    /**
     * Extracts every data source that has not been registered.
     * @param owsContext The ows context
     * @return A list of data sources attributes (without username and password)
     */
    public List<DbConnectionString> extractUndefinedDataSources(JAXBElement<OWSContextType> owsContext);
}
