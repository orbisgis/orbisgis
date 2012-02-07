/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package orb.orbisgis.core.ui.plugins.ows;

import java.util.List;

/**
 *
 * @author cleglaun
 */
public interface OwsService {
    
    /**
     * Loads all available ows files that depend on a default repository
     * @return 
     */
    public List<OwsFileBasic> getAllOwsFiles();
}
