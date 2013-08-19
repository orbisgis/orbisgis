/*
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
package org.orbisgis.legend.analyzer;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.real.Interpolate2Real;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.visitors.FeaturesVisitor;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.analyzer.symbolizers.LineSymbolizerAnalyzer;
import org.orbisgis.legend.structure.stroke.PenStrokeLegend;
import org.orbisgis.legend.structure.stroke.ProportionalStrokeLegend;
import org.orbisgis.legend.thematic.proportional.ProportionalLine;

/**
 *
 * @author Alexis Guéganno
 */
public class ProportionalLineTest extends AnalyzerTest {


        @Test
        public void testDefaultInstanciation() throws Exception {
                ProportionalLine pl = new ProportionalLine();
                assertTrue(pl.getFirstData() == 0);
                assertTrue(pl.getFirstValue() == 0);
                assertTrue(pl.getSecondData() == 1);
                assertTrue(pl.getSecondValue() == 1);
                LineSymbolizer ls = (LineSymbolizer) pl.getSymbolizer();
                PenStroke ps = (PenStroke) ls.getStroke();
                assertTrue(ps.getWidth() instanceof Interpolate2Real);
                assertTrue(ps == pl.getStrokeLegend().getStroke());
        }

        @Test
        public void testGetUom() throws Exception {
                ProportionalLine pl = new ProportionalLine();
                assertTrue(pl.getStrokeUom() == Uom.PX);
        }

        @Test
        public void testSetUom() throws Exception {
                ProportionalLine pl = new ProportionalLine();
                pl.setStrokeUom(Uom.IN);
                assertTrue(pl.getStrokeUom() == Uom.IN);

        }

    /**************************************************************************/
    /* Let's test the interpolated lines. We won't test color and dashes, as  */
    /* they are already tested in UniqueSymbol instances                      */
    /*                                                                        */
    /**************************************************************************/

    @Test
    public void testInitializationProportional() throws Exception {
        Style st = getStyle(PROP_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        ProportionalLine usl = new ProportionalLine(ls);
        assertTrue(true);

    }

    @Test
    public void testAnalyzerProportional() throws Exception {
        Style st = getStyle(PROP_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        LineSymbolizerAnalyzer lsa = new LineSymbolizerAnalyzer(ls);
        assertTrue(lsa.getLegend() instanceof ProportionalLine);
    }

    @Test
    public void testInitializationProportionalFail() throws Exception {
        Style st = getStyle(CONSTANT_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        try{
            ProportionalLine usl = new ProportionalLine(ls);
            fail();
        } catch (ClassCastException iae){
            assertTrue(true);
        }

    }

    @Test
    public void testGetFirstInterpolationData() throws Exception {
        Style st = getStyle(PROP_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        ProportionalLine usl = new ProportionalLine(ls);
        assertTrue(usl.getFirstData() == 0.0);
    }

    @Test
    public void testGetSecondInterpolationData() throws Exception {
        Style st = getStyle(PROP_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        ProportionalLine usl = new ProportionalLine(ls);
        assertTrue(usl.getSecondData() == 10.0);
    }

    @Test
    public void testSetFirstInterpolationData() throws Exception {
        Style st = getStyle(PROP_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        ProportionalLine usl = new ProportionalLine(ls);
        usl.setFirstData(22.2);
        assertTrue(usl.getFirstData() == 22.2);
    }

    @Test
    public void testSetSecondInterpolationData() throws Exception {
        Style st = getStyle(PROP_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        ProportionalLine usl = new ProportionalLine(ls);
        usl.setSecondData(56.3);
        assertTrue(usl.getSecondData() == 56.3);
    }

    @Test
    public void testGetFirstInterpolationValue() throws Exception{
        Style st = getStyle(PROP_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        ProportionalLine usl = new ProportionalLine(ls);
        assertTrue(usl.getFirstValue() == 0.0);
    }

    @Test
    public void testGetSecondInterpolationValue() throws Exception{
        Style st = getStyle(PROP_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        ProportionalLine usl = new ProportionalLine(ls);
        assertTrue(usl.getSecondValue() == 2.0);
    }

    @Test
    public void testSetFirstInterpolationValue() throws Exception{
        Style st = getStyle(PROP_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        ProportionalLine usl = new ProportionalLine(ls);
        usl.setFirstValue(68.9);
        assertTrue(usl.getFirstValue() == 68.9);
    }

    @Test
    public void testSetSecondInterpolationValue() throws Exception{
        Style st = getStyle(PROP_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        ProportionalLine usl = new ProportionalLine(ls);
        usl.setSecondValue(24.3);
        assertTrue(usl.getSecondValue() == 24.3);
    }

    @Test
    public void testGetLookupFieldName() throws Exception{
        Style st = getStyle(PROP_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        ProportionalLine usl = new ProportionalLine(ls);
        assertTrue(usl.getLookupFieldName().equals("LARGEUR"));
        //We check we have the same value in the raw symbolizer
        PenStroke ps = (PenStroke) ls.getStroke();
        Interpolate2Real ir = (Interpolate2Real) ps.getWidth();
        RealAttribute ra = (RealAttribute) ir.getLookupValue();
        assertTrue(ra.getColumnName().equals("LARGEUR"));
        FeaturesVisitor fv = new FeaturesVisitor();
        ls.acceptVisitor(fv);
        assertTrue(fv.getResult().contains("LARGEUR"));
    }

    @Test
    public void testSetLookupFieldName() throws Exception{
        Style st = getStyle(PROP_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        ProportionalLine usl = new ProportionalLine(ls);
        usl.setLookupFieldName("longueur");
        assertTrue(usl.getLookupFieldName().equals("longueur"));
        PenStroke ps = (PenStroke) ls.getStroke();
        Interpolate2Real ir = (Interpolate2Real) ps.getWidth();
        RealAttribute ra = (RealAttribute) ir.getLookupValue();
        assertTrue(ra.getColumnName().equals("longueur"));
        FeaturesVisitor fv = new FeaturesVisitor();
        ls.acceptVisitor(fv);
        assertTrue(fv.getResult().contains("longueur"));
    }

}
