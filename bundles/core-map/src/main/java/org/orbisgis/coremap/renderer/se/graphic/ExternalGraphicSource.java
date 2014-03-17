/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.coremap.renderer.se.graphic;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import net.opengis.se._2_0.core.ExternalGraphicType;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.coremap.renderer.se.SymbolizerNode;
import org.orbisgis.coremap.renderer.se.parameter.ParameterException;

/**
 *
 * @author Maxence Laurent
 * @author Alexis Guéganno
 * @todo implement in InlineContent
 */
public interface ExternalGraphicSource extends SymbolizerNode{


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
            Map<String,Object> map, MapTransform mt, String mimeType) throws ParameterException;

    /**
     * Draws the graphic on the map.
     * @param g2 The Graphics used to draw the symbol.
     * @param map The input parameters.
     * @param at The AffineTransform used on the input image
     * @param mt The MapTransform used to put the resulting image on the map.
     * @param opacity The opacity of the image.
     * @param mimeType The MIME type of the image.
     * @throws ParameterException
     */
    void draw(Graphics2D g2, Map<String,Object> map, AffineTransform at, MapTransform mt,
              double opacity, String mimeType) throws ParameterException;

    void setJAXBSource(ExternalGraphicType e);
}
