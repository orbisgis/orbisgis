/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.coremap.renderer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.orbisgis.coremap.map.MapTransform;
import org.orbisgis.coremap.renderer.se.Symbolizer;

/**
 * ImageRender extends the renderer in order to produce an image
 * @author Maxence Laurent
 */
public class ImageRenderer extends Renderer {

    private List<BufferedImage> imgSymbs = new ArrayList<>();
    private List<Symbolizer> symbols = new ArrayList<>();
    private List<Graphics2D> graphics = new ArrayList<>();

    @Override
    protected void initGraphics2D(List<Symbolizer> symbs, Graphics2D g2, MapTransform mt) {
        imgSymbs = new ArrayList<>();
        symbols = symbs;
        graphics = new ArrayList<>();
        /**
         * Create one buffered image for each Symbolizer present in the style. This way allows
         * to render all symbolizer in one pass without encountering layer level issues
         */
        for (Symbolizer s : symbs) {
            BufferedImage bufImg = new BufferedImage(mt.getWidth(), mt.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D sG2 = bufImg.createGraphics();
            sG2.addRenderingHints(mt.getRenderingHints());
            graphics.add(sG2);
            imgSymbs.add(bufImg);
        }
    }

    @Override
    protected Graphics2D getGraphics2D(Symbolizer s) {
        return graphics.get(symbols.indexOf(s));
    }

    @Override
    protected void releaseGraphics2D(Graphics2D g2) {
    }

    /**
     * Apply drawn features of last layer to input graphic
     */
    public void updateImage(Graphics2D g2) {
        for (BufferedImage img : new ArrayList<>(imgSymbs)) {
            g2.drawImage(img, null, null);
        }
    }

    @Override
    protected void disposeLayer(Graphics2D g2) {
        for (Graphics2D get : graphics){
            get.dispose();
        }
        graphics.clear();
        updateImage(g2);
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
    protected void beginFeature(long id, ResultSet rs) {
    }

    @Override
    protected void endFeature(long id, ResultSet rs) {
    }
    
    

}
