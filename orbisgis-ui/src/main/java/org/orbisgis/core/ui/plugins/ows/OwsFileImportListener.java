/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.plugins.ows;

import javax.xml.bind.JAXBElement;
import net.opengis.ows_context.OWSContextType;

/**
 *
 * @author cleglaun
 */
public interface OwsFileImportListener {
    
    /**
     * Called when the ows context file has been unmarshalled
     * @param layers The collection of layers which have been extracted.
     */
    public void fireOwsExtracted(JAXBElement<OWSContextType> owsContext);
}
