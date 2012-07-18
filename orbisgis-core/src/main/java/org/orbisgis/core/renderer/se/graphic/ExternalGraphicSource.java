/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.graphic;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import net.opengis.se._2_0.core.ExternalGraphicType;
import org.gdms.data.values.Value;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 *
 * @author maxence
 * @todo implement in InlineContent
 */
public interface ExternalGraphicSource {


        /**
         * {@code ExternalGraphicSource} realizations are dependant upon a cache.
         * Using this method, this cache is updated, and the image contained in
         * the {@code ExternalGraphicSource} may have changed - and it is the
         * same for its boundaries. This method is intended to update the cache
         * and will return the bounding box of the associated image as a
         * {@code Rectangle2D.Double} instance.
         * @param viewBox
         * @param map
         * @param mt
         * @param mimeType
         * @return
         * @throws ParameterException
         */
    Rectangle2D.Double updateCacheAndGetBounds(ViewBox viewBox, 
            Map<String,Value> map, MapTransform mt, String mimeType) throws ParameterException;
    
    void draw(Graphics2D g2, AffineTransform at, MapTransform mt, double opacity, String mimeType);

    //public abstract RenderedImage getPlanarImage(ViewBox viewBox, DataSource sds,
    //long fid, MapTransform mt, String mimeType) throws IOException, ParameterException;

    void setJAXBSource(ExternalGraphicType e);
}
