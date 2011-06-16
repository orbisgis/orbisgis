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

import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfGraphics2D;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
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
    private PdfContentByte container;
    private PdfWriter writer;

    public PdfRenderer(PdfContentByte container, PdfWriter writer){
        super();
        this.container = container;
        this.writer = writer;
    }


    @Override
    public HashMap<Symbolizer, Graphics2D> getGraphics2D(ArrayList<Symbolizer> symbs, Graphics2D g2, MapTransform mt) {
        ruleGraphics = new HashMap<Rule, ArrayList<PdfGraphics2D>>();
        HashMap<Symbolizer, Graphics2D> g2Symbs = new HashMap<Symbolizer, Graphics2D>();

        for (Symbolizer s : symbs){
            Rule rule = s.getRule();

            // make sure the rule list exists
            ArrayList<PdfGraphics2D> list = ruleGraphics.get(rule);
            if (list == null){
                list = new ArrayList<PdfGraphics2D>();
                ruleGraphics.put(rule, list);
            }

            // create the new PdfGraphcis
            PdfGraphics2D ng2 = (PdfGraphics2D) g2.create();

            list.add(ng2);

            g2Symbs.put(s, ng2);
        }
        return g2Symbs;
    }

    @Override
    public void disposeLayer(Graphics2D g2) {
    }


    @Override
    public void beginFeature(String name) {
        // Testing... need input from Toinon
        //container.beginMarkedContentSequence(new PdfName("FEAT_" + name));
        PdfDictionary dico = new PdfDictionary();

        PdfDictionary dico2 = new PdfDictionary();
        dico2.put(PdfName.O, PdfName.USERPROPERTIES);
        dico.put(PdfName.A, dico2);

        container.beginMarkedContentSequence(new PdfName("FEAT_" + name), dico, false);
    }

    @Override
    public void endFeature(String name) {
        container.endMarkedContentSequence();
    }

    @Override
    public void beginLayer(String name) {
        //container.beginLayer(new PdfLayer(name, writer));
    }

    @Override
    public void endLayer(String name) {
        //container.endLayer();
    }
}
