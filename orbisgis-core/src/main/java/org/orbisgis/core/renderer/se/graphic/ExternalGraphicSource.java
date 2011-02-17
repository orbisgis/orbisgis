/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.graphic;

import java.awt.image.RenderedImage;
import java.io.IOException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.persistance.se.ExternalGraphicType;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 *
 * @author maxence
 * @todo implement in InlineContent
 */
public interface ExternalGraphicSource {
    public abstract RenderedImage getPlanarImage(ViewBox viewBox, SpatialDataSourceDecorator sds, long fid, MapTransform mt, String mimeType) throws IOException, ParameterException;

    public void setJAXBSource(ExternalGraphicType e);
}
