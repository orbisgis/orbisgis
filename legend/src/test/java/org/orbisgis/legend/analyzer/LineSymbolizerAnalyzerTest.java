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
package org.orbisgis.legend.analyzer;

import java.awt.Color;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.real.Interpolate2Real;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.analyzer.symbolizers.LineSymbolizerAnalyzer;
import org.orbisgis.legend.thematic.constant.UniqueSymbolLine;
import org.orbisgis.legend.thematic.proportional.ProportionalLine;

/**
 * Test that we recognize known configurations for LineSymbolizer instances.
 * @author Alexis Guéganno
 */
public class LineSymbolizerAnalyzerTest extends AnalyzerTest {


    /**************************************************************************/
    /* Let's test the constant lines. We will try to retrieve each of the     */
    /* possible parameters, including dashes. There will be two cases, both   */
    /* made with a PenStroke. One used a DashArray in its definition, but the */
    /* other does not.                                                        */
    /**************************************************************************/

    @Test
    public void testInitializationUniqueSymbol() throws Exception {
        Style st = getStyle(CONSTANT_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        UniqueSymbolLine usl = new UniqueSymbolLine(ls);
        assertTrue(true);
    }

    @Test
    public void testAnalyzerUniqueSymbol() throws Exception {
        Style st = getStyle(CONSTANT_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        LineSymbolizerAnalyzer lsa = new LineSymbolizerAnalyzer(ls);
        assertTrue(lsa.getLegend() instanceof UniqueSymbolLine);
    }

    @Test
    public void testAnalyzerUniqueSymbolDash() throws Exception {
        Style st = getStyle(CONSTANT_DASHED_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        LineSymbolizerAnalyzer lsa = new LineSymbolizerAnalyzer(ls);
        assertTrue(lsa.getLegend() instanceof UniqueSymbolLine);
    }

    @Test
    public void testInitializationFailUniqueSymbol() throws Exception {
        Style st = getStyle(PROP_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        try{
            UniqueSymbolLine usl = new UniqueSymbolLine(ls);
            fail();
        } catch (ClassCastException iae){
            assertTrue(true);
        }
    }

    @Test
    public void testGetUniqueValueColor() throws Exception{
        Style st = getStyle(CONSTANT_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        UniqueSymbolLine usl = new UniqueSymbolLine(ls);
        Color col = usl.getLineColor();
        assertTrue(col.equals(new Color((int)SolidFill.GRAY50,(int)SolidFill.GRAY50,(int)SolidFill.GRAY50)));

    }

    @Test
    public void testSetUniqueValueColor() throws Exception{
        Style st = getStyle(CONSTANT_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        UniqueSymbolLine usl = new UniqueSymbolLine(ls);
        usl.setLineColor(Color.BLUE);
        assertTrue(usl.getLineColor().equals(Color.BLUE));
    }

    @Test
    public void testGetUniqueValueWidth() throws Exception{
        Style st = getStyle(CONSTANT_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        UniqueSymbolLine usl = new UniqueSymbolLine(ls);
        assertTrue(usl.getPenStroke().getLineWidth() == 8.0);

    }

    @Test
    public void testSetUniqueValueWidth() throws Exception{
        Style st = getStyle(CONSTANT_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        UniqueSymbolLine usl = new UniqueSymbolLine(ls);
        usl.getPenStroke().setLineWidth(15.0);
        assertTrue(usl.getPenStroke().getLineWidth() == 15.0);
    }

    @Test
    public void testUniqueValueGetDash() throws Exception {
        Style st = getStyle(CONSTANT_DASHED_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        UniqueSymbolLine usl = new UniqueSymbolLine(ls);
        assertTrue(usl.getPenStroke().getDashArray().equals("1 2 1 3"));
    }

    @Test
    public void testUniqueValueSetDash() throws Exception {
        Style st = getStyle(CONSTANT_DASHED_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        UniqueSymbolLine usl = new UniqueSymbolLine(ls);
        usl.getPenStroke().setDashArray("1 5 3");
        assertTrue(usl.getPenStroke().getDashArray().equals("1 5 3"));
    }

    @Test
    public void testUniqueValueRemoveDash() throws Exception {
        Style st = getStyle(CONSTANT_DASHED_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        UniqueSymbolLine usl = new UniqueSymbolLine(ls);
        usl.getPenStroke().setDashArray("");
        PenStroke ps = (PenStroke) ls.getStroke();
        StringLiteral sl = (StringLiteral) ps.getDashArray();
        assertTrue(sl == null);
        assertTrue(usl.getPenStroke().getDashArray().equals(""));
    }

    @Test
    public void testUniqueValueAddDash() throws Exception {
        Style st = getStyle(CONSTANT_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        UniqueSymbolLine usl = new UniqueSymbolLine(ls);
        PenStroke ps = (PenStroke) ls.getStroke();
        StringLiteral sl = (StringLiteral) ps.getDashArray();
        assertTrue(sl == null);
        assertTrue(usl.getPenStroke().getDashArray().equals(""));
        usl.getPenStroke().setDashArray("1 5 3");
        assertTrue(usl.getPenStroke().getDashArray().equals("1 5 3"));
        
    }

    @Test
    public void testUniqueValueAddWrongDash() throws Exception {
        Style st = getStyle(CONSTANT_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        UniqueSymbolLine usl = new UniqueSymbolLine(ls);
        PenStroke ps = (PenStroke) ls.getStroke();
        StringLiteral sl = (StringLiteral) ps.getDashArray();
        assertTrue(sl == null);
        assertTrue(usl.getPenStroke().getDashArray().equals(""));
        usl.getPenStroke().setDashArray("bonjour bonjour !");
        assertTrue(sl == null);
        assertTrue(usl.getPenStroke().getDashArray().equals(""));

    }

    @Test
    public void testUniqueValueSetWrongDash() throws Exception {
        Style st = getStyle(CONSTANT_DASHED_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        UniqueSymbolLine usl = new UniqueSymbolLine(ls);
        usl.getPenStroke().setDashArray("bonjour bonjour !");
        PenStroke ps = (PenStroke) ls.getStroke();
        StringLiteral sl = (StringLiteral) ps.getDashArray();
        assertTrue(sl == null);
        assertTrue(usl.getPenStroke().getDashArray().equals(""));
    }

    @Test
    public void testUniqueValueSetWrongDashBis() throws Exception {
        Style st = getStyle(CONSTANT_DASHED_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        UniqueSymbolLine usl = new UniqueSymbolLine(ls);
        usl.getPenStroke().setDashArray("1 -5 3");
        assertTrue(usl.getPenStroke().getDashArray().equals(""));
    }

    @Test
    public void testDefaultConstructorUniqueSymbol() throws Exception {
        UniqueSymbolLine usl = new UniqueSymbolLine();
        LineSymbolizerAnalyzer lsa = new LineSymbolizerAnalyzer((LineSymbolizer)usl.getSymbolizer());
        assertTrue(lsa.getLegend() instanceof UniqueSymbolLine);
        assertTrue(usl.getPenStroke().getLineWidth() == 0.1);
        assertTrue(usl.getLineColor().equals(Color.BLACK));
    }

    @Test
    public void testGetUom() throws Exception {
        Style st = getStyle(PROP_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        ProportionalLine usl = new ProportionalLine(ls);
        assertTrue(usl.getStrokeUom() == Uom.MM);
    }

    @Test
    public void testSetUom() throws Exception {
        Style st = getStyle(PROP_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        ProportionalLine usl = new ProportionalLine(ls);
        assertTrue(usl.getStrokeUom() == Uom.MM);
        usl.setStrokeUom(Uom.IN);
        assertTrue(usl.getStrokeUom() == Uom.IN);
        usl.setStrokeUom(null);
        assertTrue(usl.getStrokeUom() == Uom.MM);
    }

    @Test
    public void testInterpolateValidation() throws Exception {
        Style st = getStyle(PROP_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        LineSymbolizerAnalyzer lsa = new LineSymbolizerAnalyzer(new LineSymbolizer());
        RealParameter rp= ((PenStroke)ls.getStroke()).getWidth();
        assertTrue(lsa.validateLinearInterpolate(rp));
    }

    @Test
    public void testInterpolateValidation2() throws Exception {
        Style st = getStyle(PROP_LINE);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        LineSymbolizerAnalyzer lsa = new LineSymbolizerAnalyzer(new LineSymbolizer());
        Interpolate2Real rp= (Interpolate2Real) ((PenStroke)ls.getStroke()).getWidth();
        rp.setLookupValue(new RealLiteral(2.0));
        assertFalse(lsa.validateLinearInterpolate(rp));
    }
}
