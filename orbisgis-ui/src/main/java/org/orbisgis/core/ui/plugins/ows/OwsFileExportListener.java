/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.plugins.ows;

/**
 *
 * @author cleglaun
 */
public interface OwsFileExportListener {
    
    
    /**
     * Called when an ows file has been exported
     */
    public void fireOwsFileExported();
    
}
