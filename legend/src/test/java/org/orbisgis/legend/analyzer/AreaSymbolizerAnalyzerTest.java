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

import org.junit.Test;
import org.orbisgis.core.renderer.se.AreaSymbolizer;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.analyzer.symbolizers.AreaSymbolizerAnalyzer;
import org.orbisgis.legend.thematic.constant.UniqueSymbolArea;

import java.awt.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author Alexis Guéganno
 */
public class AreaSymbolizerAnalyzerTest extends AnalyzerTest {

    @Test
    public void testConstantConstructor() throws Exception {
        AreaSymbolizer ls = getConstantSymbolizer();
        UniqueSymbolArea usl = new UniqueSymbolArea(ls);
        assertTrue(true);
    }

    @Test
    public void testConstantAnalyzer() throws Exception {
        AreaSymbolizer ls = getConstantSymbolizer();
        UniqueSymbolArea usl =(UniqueSymbolArea) new AreaSymbolizerAnalyzer(ls).getLegend();
        assertTrue(true);
    }
    
    @Test
    public void testConstantConstructorFail() throws Exception {
        AreaSymbolizer ls = getChoroSymbolizer();
        try{
            UniqueSymbolArea usl = new UniqueSymbolArea(ls);
            fail();
        } catch(ClassCastException iae){
            assertTrue(true);
        }
    }

    @Test
    public void testConstantGetFillColor() throws Exception {
        AreaSymbolizer ls = getConstantSymbolizer();
        UniqueSymbolArea usl = new UniqueSymbolArea(ls);
        assertTrue(usl.getFillLegend().getColor().equals(new Color(0x12, 0x34, 0x56)));
    }

    @Test
    public void testConstantSetFillColor() throws Exception {
        AreaSymbolizer ls = getConstantSymbolizer();
        UniqueSymbolArea usl = new UniqueSymbolArea(ls);
        usl.getFillLegend().setColor(Color.red);
        assertTrue(usl.getFillLegend().getColor().equals(Color.red));
    }

    @Test
    public void testConstantGetStrokeColor() throws Exception {
        AreaSymbolizer ls = getConstantSymbolizer();
        UniqueSymbolArea usl = new UniqueSymbolArea(ls);
        assertTrue(usl.getLineColor().equals(new Color(0x88, 0x88, 0x88)));
    }

    @Test
    public void testConstantSetStrokeColor() throws Exception {
        AreaSymbolizer ls = getConstantSymbolizer();
        UniqueSymbolArea usl = new UniqueSymbolArea(ls);
        usl.setLineColor(Color.red);
        assertTrue(usl.getLineColor().equals(Color.red));
    }

    @Test
    public void testGetLineDash() throws Exception {
        AreaSymbolizer ls = getConstantSymbolizer();
        UniqueSymbolArea uvp = new UniqueSymbolArea(ls);
        assertTrue(uvp.getDashArray().isEmpty());
    }

    @Test
    public void testSetLineDash() throws Exception {
        AreaSymbolizer ls = getConstantSymbolizer();
        UniqueSymbolArea uvp = new UniqueSymbolArea(ls);
        uvp.setDashArray("2 2");
        assertTrue(uvp.getDashArray().equals("2 2"));
    }

    @Test
    public void testConstantGetLineWidth() throws Exception {
        AreaSymbolizer ls = getConstantSymbolizer();
        UniqueSymbolArea usl = new UniqueSymbolArea(ls);
        assertTrue(usl.getLineWidth() == 0.15);
    }

    @Test
    public void testConstantSetLineWidth() throws Exception {
        AreaSymbolizer ls = getConstantSymbolizer();
        UniqueSymbolArea usl = new UniqueSymbolArea(ls);
        usl.setLineWidth(12.2);
        assertTrue(usl.getLineWidth() == 12.2);
    }

    @Test
    public void testConstantDefaultConstructor() throws Exception {
        UniqueSymbolArea usa = new UniqueSymbolArea();
        assertTrue(usa.getLineWidth() == PenStroke.DEFAULT_WIDTH);
        assertTrue(usa.getLineColor().equals(Color.black));
    }

    @Test
    public void testNullFill() throws Exception {
        AreaSymbolizer as = getConstantSymbolizer();
        as.setFill(null);
        AreaSymbolizerAnalyzer asa = new AreaSymbolizerAnalyzer(as);
        assertTrue(asa.getLegend() instanceof UniqueSymbolArea);
    }

    @Test
    public void testNullFillBis() throws Exception {
        AreaSymbolizer as = getConstantSymbolizer();
        as.setFill(null);
        UniqueSymbolArea usa = new UniqueSymbolArea(as);
        assertTrue(true);
    }

    @Test
    public void testNullStroke() throws Exception {
        AreaSymbolizer as = getConstantSymbolizer();
        as.setStroke(null);
        AreaSymbolizerAnalyzer asa = new AreaSymbolizerAnalyzer(as);
        assertTrue(asa.getLegend() instanceof UniqueSymbolArea);
    }

    @Test
    public void testNullStrokeBis() throws Exception {
        AreaSymbolizer as = getConstantSymbolizer();
        as.setStroke(null);
        UniqueSymbolArea usa = new UniqueSymbolArea(as);
        assertTrue(true);
    }

    @Test
    public void testGetUom() throws Exception {
        AreaSymbolizer as = getConstantSymbolizer();
        UniqueSymbolArea usa = new UniqueSymbolArea(as);
        assertTrue(usa.getStrokeUom() == Uom.PX);
    }

    @Test
    public void testSetUom() throws Exception {
        AreaSymbolizer as = getConstantSymbolizer();
        UniqueSymbolArea usa = new UniqueSymbolArea(as);
        assertTrue(usa.getStrokeUom() == Uom.PX);
        usa.setStrokeUom(Uom.IN);
        assertTrue(usa.getStrokeUom() == Uom.IN);
        usa.setStrokeUom(null);
        assertTrue(usa.getStrokeUom() == Uom.PX);
    }

    private AreaSymbolizer getChoroSymbolizer() throws Exception {
        Style st = getStyle(COLOR_CATEGORIZE);
        return (AreaSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
    }
    private AreaSymbolizer getConstantSymbolizer() throws Exception {
        String constant = "src/test/resources/org/orbisgis/legend/constantArea.se";
        Style st = getStyle(constant);
        return (AreaSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
    }

}
