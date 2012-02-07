/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package orb.orbisgis.core.ui.plugins.ows;

import java.util.List;
import org.orbisgis.core.layerModel.ILayer;

/**
 *
 * @author cleglaun
 */
public interface OwsFileImportListener {
    
    /**
     * Called when the layers of an ows context file have been
     * extracted.
     * @param layers The collection of layers which have been extracted.
     */
    public void fireOwsExtracted(List<ILayer> layers);
}
