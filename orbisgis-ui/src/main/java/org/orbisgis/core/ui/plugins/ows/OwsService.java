/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.plugins.ows;

import java.util.List;
import org.w3c.dom.Node;

/**
 * Proxy for the remote services.
 * @author CŽdric Le Glaunec <cedric.leglaunec@gmail.com>
 */
public interface OwsService {
    
    /**
     * Loads all available ows files that depend on a default repository
     * @return 
     */
    public List<OwsFileBasic> getAllOwsFiles();
    
    /**
     * Loads the specified ows context file id from the repository
     * @param id The ows context file id in the repository
     * @return A DOM node repsenting the extracted ows context content.
     * WARNING: the implementation should take  into consideration namespaces
     * if the result is intended to be unmarshalled by JAXB.
     */
    public Node getOwsFile(int id);
    
    /**
     * Saves the project as a new ows context file in the repository.
     * @param data The ows context file as text. Do not forget to add the prefix
     * "owc=" before the actual ows context data.
     */
    public void saveOwsFileAs(String data);
}
