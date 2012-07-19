/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.renderer;

import com.itextpdf.text.pdf.PdfTemplate;
import java.awt.Graphics2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.gdms.data.DataSource;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.Symbolizer;

/**
 * This renderer is a prototype. The aim is to generate rendered-layers in a way
 * that a GeoPDF can use to offer interactivity
 * @author maxence
 */
public class PdfRenderer extends Renderer {

    private PdfTemplate pdfTemplate;
    private float height;
    private float width;
    //private MapTransform mt;
    private Map<Integer, Graphics2D> g2Levels;

    private Graphics2D baseG2;

    //private Graphics2D tg;

    public PdfRenderer(PdfTemplate pdfTemplate, float width, float height) {
        super();
        this.height = height;
        this.width = width;
        this.pdfTemplate = pdfTemplate;
        g2Levels = null;
        baseG2 = null;
    }

    @Override
    protected Graphics2D getGraphics2D(Symbolizer s) {
        //return tg;
        Graphics2D get = g2Levels.get(s.getLevel());
        //pdfTemplate.saveState();
        return get;
    }

    @Override
    protected void initGraphics2D(List<Symbolizer> symbs, Graphics2D g2, MapTransform mt) {
        //this.mt = mt;
        g2Levels = new HashMap<Integer, Graphics2D>();

        baseG2 = pdfTemplate.createGraphics(width, height);

        //HashMap<Integer, Graphics2D> g2Level = new HashMap<Integer, Graphics2D>();
        List<Integer> levels = new LinkedList<Integer>();

        /**
         * Create one buffered image for each level present in the style. This way allows
         * to render all symbolizer in one pass without encountering layer level issues
         */
        for (Symbolizer s : symbs) {
            //Graphics2D sG2;
            // Does the level of the current symbolizer already have a graphic2s ?
            if (!levels.contains(s.getLevel())) {
                // It's a new level => register level
                levels.add(s.getLevel());
            }
        }

        Collections.sort(levels);

        for (Integer level : levels) {
            Graphics2D sg2 = (Graphics2D) baseG2.create();
            sg2.addRenderingHints(mt.getRenderingHints());
            g2Levels.put(level, sg2);
        }

        //tg = pdfTemplate.createGraphics(width, height);
    }

    @Override
    public void disposeLayer(Graphics2D g2) {
        baseG2.dispose();
        //for (Graphics2D sg2 : g2Levels.values()){
        //    sg2.dispose();
        //}

        g2Levels.clear();
        //tg.dispose();
    }

    @Override
    protected void releaseGraphics2D(Graphics2D g2) {
        //g2.setPaint(null);
        //g2.setStroke(null);
        //pdfTemplate.restoreState();
    }
    
    @Override
    public void beginLayer(String name) {
    }

    @Override
    public void endLayer(String name) {
    }

    @Override
    protected void beginFeature(long id, DataSource sds) {
    }

    @Override
    protected void endFeature(long id, DataSource sds) {
    }
}
