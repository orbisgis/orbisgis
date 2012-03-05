/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.plugins.ows;

import com.vividsolutions.jts.geom.Envelope;
import org.orbisgis.core.layerModel.ILayer;

/**
 *
 * @author cleglaun
 */
public interface OWSContextExporter {

    /**
     * Exports a project to an ows context stream. The project with the specified
     * id should be replaced by the new one in the datastore.
     * The destination is defined by the related implementation.
     * @param id
     * @param title
     * @param description
     * @param crs
     * @param boundingBox
     * @param layers 
     */
    public void exportProject(int id, String title, String description, String crs, 
            Envelope boundingBox, ILayer[] layers);

    /**
     * Exports a new project to an ows context stream.
     * The destination is defined by the related implementation.
     * @param id
     * @param title
     * @param description
     * @param crs
     * @param boundingBox
     * @param layers 
     */
    public void exportProjectAs(String title, String description, String crs, 
            Envelope boundingBox, ILayer[] layers);
}
