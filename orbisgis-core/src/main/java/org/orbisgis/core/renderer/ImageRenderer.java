/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.renderer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.Symbolizer;

/**
 * ImageRender extends the renderer in order to produce an image
 * @author maxence
 */
public class ImageRenderer extends Renderer {

    private List<BufferedImage> imgSymbs = null;
    private Map<Integer, Graphics2D> g2Level = null;

    @Override
    protected void initGraphics2D(List<Symbolizer> symbs, Graphics2D g2, MapTransform mt) {
        imgSymbs = new ArrayList<BufferedImage>();
        
        g2Level = new HashMap<Integer, Graphics2D>();

        /**
         * Create one buffered image for each level present in the style. This way allows
         * to render all symbolizer in one pass without encountering layer level issues
         */
        for (Symbolizer s : symbs) {

            Graphics2D sG2;
            // Does the level of the current symbolizer already have a graphic2s ?
            if (!g2Level.containsKey(s.getLevel())){
                // It's a new level => create a new graphics2D
                BufferedImage bufImg = new BufferedImage(mt.getWidth(), mt.getHeight(), BufferedImage.TYPE_INT_ARGB);
                sG2 = bufImg.createGraphics();
                sG2.addRenderingHints(mt.getRenderingHints());
                imgSymbs.add(bufImg);
                // Map the graphics with its level
                g2Level.put(s.getLevel(), sG2);
            }
        }
    }

    @Override
    protected Graphics2D getGraphics2D(Symbolizer s) {
        return g2Level.get(s.getLevel());
    }


    @Override
    protected void releaseGraphics2D(Graphics2D g2) {
    }

    @Override
    protected void disposeLayer(Graphics2D g2) {
        for (Integer key : g2Level.keySet()){
            Graphics2D get = g2Level.get(key);
            get.dispose();
        }

        g2Level.clear();
        
        for (BufferedImage img : imgSymbs) {
            g2.drawImage(img, null, null);
        }
    }

    @Override
    protected void beginLayer(String name) {
        // nothing to do
    }

    @Override
    protected void endLayer(String name) {
        // nothing to do
    }

    @Override
    protected void beginFeature(long id, SpatialDataSourceDecorator sds) {
    }

    @Override
    protected void endFeature(long id, SpatialDataSourceDecorator sds) {
    }

}
