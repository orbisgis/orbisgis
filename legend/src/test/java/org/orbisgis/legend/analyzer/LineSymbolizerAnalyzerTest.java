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
package org.orbisgis.legend.analyzer;

import java.awt.Color;
import java.util.List;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.analyzer.symbolizers.LineSymbolizerAnalyzer;
import org.orbisgis.legend.thematic.constant.USParameter;
import org.orbisgis.legend.thematic.constant.UniqueSymbolLine;
import org.orbisgis.legend.thematic.proportional.ProportionalLine;

/**
 * Test that we recognize known configurations for LineSymbolizer instances.
 * @author Alexis Guéganno
 */
public class LineSymbolizerAnalyzerTest extends AnalyzerTest {
    private String constant = "src/test/resources/org/orbisgis/legend/uniqueLineSymbol.se";
    private String constantDash = "src/test/resources/org/orbisgis/legend/uniqueLineSymbolDash.se";
    private String proportional = "src/test/resources/org/orbisgis/legend/linearProportional.se";


    /**************************************************************************/
    /* Let's test the constant lines. We will try to retrieve each of the     */
    /* possible parameters, including dashes. There will be two cases, both   */
    /* made with a PenStroke. One used a DashArray in its definition, but the */
    /* other does not.                                                        */
    /**************************************************************************/

    @Test
    public void testInitializationUniqueSymbol() throws Exception {
        Style st = getStyle(constant);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        UniqueSymbolLine usl = new UniqueSymbolLine(ls);
        assertTrue(true);
    }

    @Test
    public void testAnalyzerUniqueSymbol() throws Exception {
        Style st = getStyle(constant);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        LineSymbolizerAnalyzer lsa = new LineSymbolizerAnalyzer(ls);
        assertTrue(lsa.getLegend() instanceof UniqueSymbolLine);
    }

    @Test
    public void testAnalyzerUniqueSymbolDash() throws Exception {
        Style st = getStyle(constantDash);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        LineSymbolizerAnalyzer lsa = new LineSymbolizerAnalyzer(ls);
        assertTrue(lsa.getLegend() instanceof UniqueSymbolLine);
    }

    @Test
    public void testInitializationFailUniqueSymbol() throws Exception {
        Style st = getStyle(proportional);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        try{
            UniqueSymbolLine usl = new UniqueSymbolLine(ls);
            fail();
        } catch (IllegalArgumentException iae){
            assertTrue(true);
        }
    }

    @Test
    public void testGetUniqueValueColor() throws Exception{
        Style st = getStyle(constant);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        UniqueSymbolLine usl = new UniqueSymbolLine(ls);
        Color col = usl.getLineColor();
        assertTrue(col.equals(new Color((int)SolidFill.GRAY50,(int)SolidFill.GRAY50,(int)SolidFill.GRAY50)));

    }

    @Test
    public void testSetUniqueValueColor() throws Exception{
        Style st = getStyle(constant);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        UniqueSymbolLine usl = new UniqueSymbolLine(ls);
        usl.setLineColor(Color.BLUE);
        assertTrue(usl.getLineColor().equals(Color.BLUE));
    }

    @Test
    public void testGetUniqueValueWidth() throws Exception{
        Style st = getStyle(constant);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        UniqueSymbolLine usl = new UniqueSymbolLine(ls);
        assertTrue(usl.getLineWidth() == 8.0);

    }

    @Test
    public void testSetUniqueValueWidth() throws Exception{
        Style st = getStyle(constant);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        UniqueSymbolLine usl = new UniqueSymbolLine(ls);
        usl.setLineWidth(15.0);
        assertTrue(usl.getLineWidth() == 15.0);
    }

    @Test
    public void testUniqueValueGetDash() throws Exception {
        Style st = getStyle(constantDash);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        UniqueSymbolLine usl = new UniqueSymbolLine(ls);
        assertTrue(usl.getDashArray().equals("1 2 1 3"));
    }

    @Test
    public void testUniqueValueSetDash() throws Exception {
        Style st = getStyle(constantDash);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        UniqueSymbolLine usl = new UniqueSymbolLine(ls);
        usl.setDashArray("1 5 3");
        assertTrue(usl.getDashArray().equals("1 5 3"));
    }

    @Test
    public void testUniqueValueRemoveDash() throws Exception {
        Style st = getStyle(constantDash);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        UniqueSymbolLine usl = new UniqueSymbolLine(ls);
        usl.setDashArray("");
        PenStroke ps = (PenStroke) ls.getStroke();
        StringLiteral sl = (StringLiteral) ps.getDashArray();
        assertTrue(sl == null);
        assertTrue(usl.getDashArray().equals(""));
    }

    @Test
    public void testUniqueValueAddDash() throws Exception {
        Style st = getStyle(constant);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        UniqueSymbolLine usl = new UniqueSymbolLine(ls);
        PenStroke ps = (PenStroke) ls.getStroke();
        StringLiteral sl = (StringLiteral) ps.getDashArray();
        assertTrue(sl == null);
        assertTrue(usl.getDashArray().equals(""));
        usl.setDashArray("1 5 3");
        assertTrue(usl.getDashArray().equals("1 5 3"));
        
    }

    @Test
    public void testUniqueValueAddWrongDash() throws Exception {
        Style st = getStyle(constant);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        UniqueSymbolLine usl = new UniqueSymbolLine(ls);
        PenStroke ps = (PenStroke) ls.getStroke();
        StringLiteral sl = (StringLiteral) ps.getDashArray();
        assertTrue(sl == null);
        assertTrue(usl.getDashArray().equals(""));
        usl.setDashArray("bonjour bonjour !");
        assertTrue(sl == null);
        assertTrue(usl.getDashArray().equals(""));

    }

    @Test
    public void testUniqueValueSetWrongDash() throws Exception {
        Style st = getStyle(constantDash);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        UniqueSymbolLine usl = new UniqueSymbolLine(ls);
        usl.setDashArray("bonjour bonjour !");
        PenStroke ps = (PenStroke) ls.getStroke();
        StringLiteral sl = (StringLiteral) ps.getDashArray();
        assertTrue(sl == null);
        assertTrue(usl.getDashArray().equals(""));
    }

    @Test
    public void testDefaultConstructorUniqueSymbol() throws Exception {
        UniqueSymbolLine usl = new UniqueSymbolLine();
        LineSymbolizerAnalyzer lsa = new LineSymbolizerAnalyzer((LineSymbolizer)usl.getSymbolizer());
        assertTrue(lsa.getLegend() instanceof UniqueSymbolLine);
        assertTrue(usl.getLineWidth() == 0.1);
        assertTrue(usl.getLineColor().equals(Color.BLACK));
    }

    @Test
    public void testGetParameters() throws Exception {
        Style st = getStyle(constantDash);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        UniqueSymbolLine usl = new UniqueSymbolLine(ls);
        List<USParameter<?>> params = usl.getParameters();
        assertTrue(params.get(0).getValue().equals(usl.getLineWidth()));
        assertTrue(params.get(1).getValue().equals(usl.getLineColor()));
        assertTrue(params.get(2).getValue().equals(usl.getDashArray()));
    }

    @Test
    public void testSetParameters() throws Exception {
        Style st = getStyle(constantDash);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        UniqueSymbolLine usl = new UniqueSymbolLine(ls);
        List<USParameter<?>> params = usl.getParameters();
        ((USParameter<Double>)params.get(0)).setValue(1000.0);
        assertTrue(params.get(0).getValue().equals(usl.getLineWidth()));
        assertTrue(params.get(0).getValue().equals(1000.0));
        ((USParameter<Color>)params.get(1)).setValue(new Color(20, 45, 32));
        assertTrue(params.get(1).getValue().equals(usl.getLineColor()));
        assertTrue(params.get(1).getValue().equals(new Color(20, 45, 32)));
        ((USParameter<String>)params.get(2)).setValue("2 5 3 9");
        assertTrue(params.get(2).getValue().equals(usl.getDashArray()));
        assertTrue(params.get(2).getValue().equals("2 5 3 9"));
    }
    
    /**************************************************************************/
    /* Let's test the interpolated lines. We won't test color and dashes, as  */
    /* they are already tested in UniqueSymbol instances                      */
    /*                                                                        */
    /**************************************************************************/

    @Test
    public void testInitializationProportional() throws Exception {
        Style st = getStyle(proportional);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        ProportionalLine usl = new ProportionalLine(ls);
        assertTrue(true);

    }

    @Test
    public void testAnalyzerProportional() throws Exception {
        Style st = getStyle(proportional);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        LineSymbolizerAnalyzer lsa = new LineSymbolizerAnalyzer(ls);
        assertTrue(lsa.getLegend() instanceof ProportionalLine);
    }

    @Test
    public void testInitializationProportionalFail() throws Exception {
        Style st = getStyle(constant);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        try{
            ProportionalLine usl = new ProportionalLine(ls);
            fail();
        } catch (IllegalArgumentException iae){
            assertTrue(true);
        }

    }

    @Test
    public void testGetFirstInterpolationData() throws Exception {
        Style st = getStyle(proportional);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        ProportionalLine usl = new ProportionalLine(ls);
        assertTrue(usl.getFirstData() == 0.0);
    }

    @Test
    public void testGetSecondInterpolationData() throws Exception {
        Style st = getStyle(proportional);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        ProportionalLine usl = new ProportionalLine(ls);
        assertTrue(usl.getSecondData() == 10.0);
    }

    @Test
    public void testSetFirstInterpolationData() throws Exception {
        Style st = getStyle(proportional);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        ProportionalLine usl = new ProportionalLine(ls);
        usl.setFirstData(22.2);
        assertTrue(usl.getFirstData() == 22.2);
    }

    @Test
    public void testSetSecondInterpolationData() throws Exception {
        Style st = getStyle(proportional);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        ProportionalLine usl = new ProportionalLine(ls);
        usl.setSecondData(56.3);
        assertTrue(usl.getSecondData() == 56.3);
    }

    @Test
    public void testGetFirstInterpolationValue() throws Exception{
        Style st = getStyle(proportional);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        ProportionalLine usl = new ProportionalLine(ls);
        assertTrue(usl.getFirstValue() == 0.0);
    }

    @Test
    public void testGetSecondInterpolationValue() throws Exception{
        Style st = getStyle(proportional);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        ProportionalLine usl = new ProportionalLine(ls);
        assertTrue(usl.getSecondValue() == 2.0);
    }

    @Test
    public void testSetFirstInterpolationValue() throws Exception{
        Style st = getStyle(proportional);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        ProportionalLine usl = new ProportionalLine(ls);
        usl.setFirstValue(68.9);
        assertTrue(usl.getFirstValue() == 68.9);
    }

    @Test
    public void testSetSecondInterpolationValue() throws Exception{
        Style st = getStyle(proportional);
        LineSymbolizer ls = (LineSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        ProportionalLine usl = new ProportionalLine(ls);
        usl.setSecondValue(24.3);
        assertTrue(usl.getSecondValue() == 24.3);
    }

}
