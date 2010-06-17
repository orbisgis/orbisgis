/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.graphic;

import java.awt.Shape;
import java.io.IOException;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.renderer.persistance.se.MarkGraphicType;

import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 * This interface allow to fetch a mark graphic for many sources,
 *
 * @author maxence
 * @todo implement in InlineContent(for se InlineContent && GML), OnlineResource
 */
public interface MarkGraphicSource {
    public abstract Shape getShape(ViewBox viewBox, Feature feat, Double scale, Double dpi)
            throws ParameterException, IOException;

    public void setJAXBSource(MarkGraphicType m);
}
