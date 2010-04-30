/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.graphic;

import java.awt.Shape;
import java.io.IOException;
import org.gdms.data.DataSource;

import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 * This interface allow to fetch a mark graphic for many sources,
 *
 * @author maxence
 * @todo implement in InlineContent(for se InlineContent && GML), OnlineResource
 */
public interface MarkGraphicSource {
    Shape getShape(ViewBox viewBox, DataSource ds, int fid)
            throws ParameterException, IOException;
}
