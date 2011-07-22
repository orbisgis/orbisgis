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

import com.itextpdf.text.pdf.PdfGraphics2D;
import com.itextpdf.text.pdf.PdfTemplate;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.Symbolizer;

/**
 * This renderer is a prototype. The aim is to generate rendered-layers in a way
 * that a GeoPDF can use to offer interactivity
 * @author maxence
 */
public class PdfRenderer extends Renderer {

    private HashMap<Rule, ArrayList<PdfGraphics2D>> ruleGraphics;
    private PdfTemplate pdfTemplate;
    private float height;
    private float width;
    private MapTransform mt;
    private HashMap<Integer, Graphics2D> g2Levels;

    public PdfRenderer(PdfTemplate pdfTemplate, float width, float height) {
        super();
        this.height = height;
        this.width = width;
        this.pdfTemplate = pdfTemplate;
    }

    @Override
    protected Graphics2D getGraphics2D(Symbolizer s) {
        return g2Levels.get(s.getLevel());
    }

    @Override
    protected void initGraphics2D(ArrayList<Symbolizer> symbs, Graphics2D g2, MapTransform mt) {
        this.mt = mt;
        g2Levels = new HashMap<Integer, Graphics2D>();

        //HashMap<Integer, Graphics2D> g2Level = new HashMap<Integer, Graphics2D>();
        List<Integer> levels = new LinkedList<Integer>();

        /**
         * Create one buffered image for each level present in the style. This way allows
         * to render all symbolizer in one pass without encountering layer level issues
         */
        for (Symbolizer s : symbs) {

            Graphics2D sG2;
            // Does the level of the current symbolizer already have a graphic2s ?
            if (!levels.contains(s.getLevel())) {
                // It's a new level => register level
                levels.add(s.getLevel());
            }
        }

        Collections.sort(levels);

        for (Integer level : levels) {
            Graphics2D sg2 = pdfTemplate.createGraphics(width, height);
            sg2.addRenderingHints(mt.getRenderingHints());
            g2Levels.put(level, sg2);
        }
    }

    @Override
    public void disposeLayer(Graphics2D g2) {
        for (Graphics2D sg2 : g2Levels.values()){
            sg2.dispose();
        }
    }

    @Override
    protected void releaseGraphics2D(Graphics2D g2) {
    }
    
    @Override
    public void beginLayer(String name) {
    }

    @Override
    public void endLayer(String name) {
    }

    @Override
    protected void beginFeature(long id, SpatialDataSourceDecorator sds) {
    }

    @Override
    protected void endFeature(long id, SpatialDataSourceDecorator sds) {
    }
}
