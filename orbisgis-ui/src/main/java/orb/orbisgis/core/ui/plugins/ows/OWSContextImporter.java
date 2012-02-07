/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package orb.orbisgis.core.ui.plugins.ows;

import java.io.InputStream;
import java.util.List;
import org.orbisgis.core.layerModel.ILayer;


/**
 *
 * @author CŽdric Le Glaunec <cedric.leglaunec@gmail.com>
 */
public interface OWSContextImporter {
    
    /**
     * Extracts every layer from the given file
     * @param file An OWS Context file
     * @return The layers with related SE Styles
     */
    public List<ILayer> extractLayers(InputStream file);
}
