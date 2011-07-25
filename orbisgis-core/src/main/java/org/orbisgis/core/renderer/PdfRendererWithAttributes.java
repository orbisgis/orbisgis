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

import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfGraphics2D;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfString;
import com.itextpdf.text.pdf.PdfStructureElement;
import com.itextpdf.text.pdf.PdfTemplate;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.Symbolizer;

/**
 * This renderer is a prototype. The aim is to generate rendered-layers in a way
 * that a GeoPDF can use to offer interactivity
 * @author maxence
 */
public class PdfRendererWithAttributes extends Renderer {

    private Map<Rule, ArrayList<PdfGraphics2D>> ruleGraphics;
    private PdfContentByte cb;
    private PdfStructureElement top;
    private float height;
    private float width;
    private float lx;
    private float ly;
    private PdfTemplate pTemp;
    private MapTransform mt;

    public PdfRendererWithAttributes(PdfContentByte container, PdfStructureElement top, float width, float height, float lx, float ly) {
        super();
        this.cb = container;
        this.top = top;
        this.height = height;
        this.width = width;
        this.lx = lx;
        this.ly = ly;
    }

    @Override
    protected Graphics2D getGraphics2D(Symbolizer s) {
        Graphics2D g2 = pTemp.createGraphics(width, height);
        g2.addRenderingHints(mt.getRenderingHints());
        return g2;
    }

    @Override
    protected void releaseGraphics2D(Graphics2D g2) {
        g2.dispose();
    }

    @Override
    protected void initGraphics2D(List<Symbolizer> symbs, Graphics2D g2, MapTransform mt) {
        this.mt = mt;
    }

    @Override
    public void disposeLayer(Graphics2D g2) {
    }

    @Override
    public void beginFeature(long id, SpatialDataSourceDecorator sds) {

        try {
            PdfStructureElement e = new PdfStructureElement(top, new PdfName("feature" + id));
            PdfDictionary userProperties = new PdfDictionary();
            userProperties.put(PdfName.O, PdfName.USERPROPERTIES);
            PdfArray properties = new PdfArray();

            for (int i = 0; i < sds.getFieldCount(); i++) {
                if (sds.getFieldType(i).getTypeCode() != Type.GEOMETRY) {
                    PdfDictionary property = new PdfDictionary();
                    property.put(PdfName.N, new PdfString(sds.getFieldName(i)));
                    Value v = sds.getFieldValue(id, i);
                    property.put(PdfName.V, new PdfString(v.toString()));
                    properties.add(property);
                }
            }

            userProperties.put(PdfName.P, properties);
            e.put(PdfName.A, userProperties);
            
            pTemp = cb.createTemplate(width, height);
            cb.beginMarkedContentSequence(e);

        } catch (DriverException ex) {
            Logger.getLogger(PdfRendererWithAttributes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void endFeature(long id, SpatialDataSourceDecorator sds) {
        cb.addTemplate(pTemp, lx, ly);
        cb.endMarkedContentSequence();
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
