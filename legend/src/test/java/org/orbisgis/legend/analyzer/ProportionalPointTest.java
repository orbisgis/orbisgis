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
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.analyzer.symbolizers.PointSymbolizerAnalyzer;
import org.orbisgis.legend.thematic.proportional.ProportionalPoint;

/**
 *
 * @author Alexis Guéganno
 */
public class ProportionalPointTest extends AnalyzerTest {
    @Test
    public void testProportionalPointConstructor() throws Exception {
                Style st = getStyle(PROPORTIONAL_POINT);
                PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                ProportionalPoint uvp = new ProportionalPoint(ps);
                assertTrue(true);
    }

    @Test
    public void testLegendFromAnalyzerProportionalPoint() throws Exception {
                Style st = getStyle(PROPORTIONAL_POINT);
                PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                PointSymbolizerAnalyzer psa = new PointSymbolizerAnalyzer(ps);
                assertTrue(psa.getLegend() instanceof ProportionalPoint);

    }

    @Test
    public void testProportionalPointConstructorFail() throws Exception {
                Style st = getStyle(CONSTANT_POINT);
                PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                try{
                    ProportionalPoint uvp = new ProportionalPoint(ps);
                    fail();
                } catch(IllegalArgumentException cce){
                    assertTrue(true);
                }
    }

    @Test
    public void testProportionalGetFirstData() throws Exception {
        Style st = getStyle(PROPORTIONAL_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        ProportionalPoint uvp = new ProportionalPoint(ps);
        assertTrue(uvp.getFirstData() == .0);
    }
    @Test
    public void testProportionalGetSecondData() throws Exception {
        Style st = getStyle(PROPORTIONAL_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        ProportionalPoint uvp = new ProportionalPoint(ps);
        double youhou = uvp.getSecondData();
        assertTrue(youhou == 1000.0);
    }

    @Test
    public void testProportionalSetFirstData() throws Exception {
        Style st = getStyle(PROPORTIONAL_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        ProportionalPoint uvp = new ProportionalPoint(ps);
        uvp.setFirstData(25.2);
        assertTrue(uvp.getFirstData() == 25.2);
    }
    @Test
    public void testProportionalSetSecondData() throws Exception {
        Style st = getStyle(PROPORTIONAL_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        ProportionalPoint uvp = new ProportionalPoint(ps);
        uvp.setSecondData(42.0);
        assertTrue(uvp.getSecondData() == 42.0);
    }

    @Test
    public void testProportionalGetFirstValue() throws Exception {
        Style st = getStyle(PROPORTIONAL_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        ProportionalPoint uvp = new ProportionalPoint(ps);
        assertTrue(uvp.getFirstValue() == .0);
    }
    @Test
    public void testProportionalGetSecondValue() throws Exception {
        Style st = getStyle(PROPORTIONAL_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        ProportionalPoint uvp = new ProportionalPoint(ps);
        assertTrue( uvp.getSecondValue()== 200.0);
    }

    @Test
    public void testProportionalSetFirstValue() throws Exception {
        Style st = getStyle(PROPORTIONAL_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        ProportionalPoint uvp = new ProportionalPoint(ps);
        uvp.setFirstValue(24.0);
        assertTrue(uvp.getFirstValue() == 24.0);
    }
    @Test
    public void testProportionalSetSecondValue() throws Exception {
        Style st = getStyle(PROPORTIONAL_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        ProportionalPoint uvp = new ProportionalPoint(ps);
        uvp.setSecondValue(250.0);
        assertTrue( uvp.getSecondValue()== 250.0);
    }

    @Test
    public void testAnalyzeBackDefaultPropPoint() throws Exception {
            ProportionalPoint pp = new ProportionalPoint();
            PointSymbolizer ps = (PointSymbolizer) pp.getSymbolizer();
            Legend leg = (Legend) new PointSymbolizerAnalyzer(ps).getLegend();
            assertTrue(leg instanceof ProportionalPoint);
    }
}
