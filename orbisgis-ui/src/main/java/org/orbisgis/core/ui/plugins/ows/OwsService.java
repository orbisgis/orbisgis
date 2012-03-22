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
     * Loads all available ows contexts belonging to the given workspace from
     * the data repository.
     * @param workspace A workspace identified by its name
     * @return A list of ows contexts
     */
    public List<OwsFileBasic> getAllOwsFiles(OwsWorkspace workspace);
    
    /**
     * Loads the specified ows context file id in the specified workspace
     * from the repository
     * @param workspace A valid workspace
     * @param id The ows context file id in the repository
     * @return A DOM node repsenting the extracted ows context content.
     * WARNING: the JAXB implementation should take  into consideration namespaces
     * if the result is intended to be unmarshalled by JAXB.
     */
    public Node getOwsFile(OwsWorkspace workspace, int id);
    
    /**
     * Saves the project as a new ows context file in the repository.
     * @param data The ows context file as text. Do not forget to add the prefix
     * "owc=" before the actual ows context data.
     */
    public void saveOwsFileAs(String data);
    
    /**
     * Saves the project.
     * 
     * @param data The ows context file as text.
     * @param projectId The project's id
     */
    public void saveOwsFile(String data, int projectId);
    

    /**
     * Loads all available workspaces available in the data repository
     * @return A list of all available workspaces.
     */
    public List<OwsWorkspace> getAllOwsWorkspaces();
}
