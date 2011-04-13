/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.graphic;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import net.opengis.se._2_0.core.ExternalGraphicType;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 *
 * @author maxence
 * @todo implement in InlineContent
 */
public interface ExternalGraphicSource {


    public Rectangle2D.Double updateCacheAndGetBounds(ViewBox viewBox, SpatialDataSourceDecorator sds, long fid, MapTransform mt, String mimeType) throws ParameterException;
    public void draw(Graphics2D g2, AffineTransform at, MapTransform mt, double opacity, String mimeType);

    //public abstract RenderedImage getPlanarImage(ViewBox viewBox, SpatialDataSourceDecorator sds, long fid, MapTransform mt, String mimeType) throws IOException, ParameterException;

    public void setJAXBSource(ExternalGraphicType e);
}
