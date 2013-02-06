/*
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
package org.orbisgis.legend.analyzer.symbolizers;

import static org.junit.Assert.*;
import org.junit.Test;
import org.orbisgis.core.renderer.se.AreaSymbolizer;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.fill.GraphicFill;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.graphic.PieChart;
import org.orbisgis.core.renderer.se.stroke.GraphicStroke;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.AnalyzerTest;

/**
 * Tests made on the class AbstractSymbolizerAnalyzer.
 * @author Alexis Guéganno
 */
public class SymbolizerTypeAnalyzerTest extends AnalyzerTest {

        @Test
        public void testValidStroke() throws Exception {
                Style s = getStyle(COLOR_CATEGORIZE);
                AreaSymbolizer as = (AreaSymbolizer) s.getRules().get(0).getCompositeSymbolizer()
                        .getSymbolizerList().get(0);
                SymbolizerTypeAnalyzer sta = new SymbolizerTypeAnalyzer();
                assertTrue(sta.validateStroke(as.getStroke()));
        }

        @Test
        public void testValidFill() throws Exception {
                Style s = getStyle(COLOR_CATEGORIZE);
                AreaSymbolizer as = (AreaSymbolizer) s.getRules().get(0).getCompositeSymbolizer()
                        .getSymbolizerList().get(0);
                SymbolizerTypeAnalyzer sta = new SymbolizerTypeAnalyzer();
                assertTrue(sta.validateFill(as.getFill()));
        }

        @Test
        public void testInvalidFill() throws Exception {
                Style s = getStyle(DENSITY_FILL);
                AreaSymbolizer as = (AreaSymbolizer) s.getRules().get(0).getCompositeSymbolizer()
                        .getSymbolizerList().get(0);
                SymbolizerTypeAnalyzer sta = new SymbolizerTypeAnalyzer();
                assertFalse(sta.validateFill(as.getFill()));
        }

        @Test
        public void testInvalidStroke() throws Exception {
                SymbolizerTypeAnalyzer sta = new SymbolizerTypeAnalyzer();
                assertFalse(sta.validateStroke(new GraphicStroke()));
        }

        @Test
        public void testInvalidStrokeBis() throws Exception {
                SymbolizerTypeAnalyzer sta = new SymbolizerTypeAnalyzer();
                PenStroke s = new PenStroke();
                s.setFill(new GraphicFill());
                assertFalse(sta.validateStroke(s));
        }

        @Test
        public void testValidGraphic() throws Exception {
                SymbolizerTypeAnalyzer sta = new SymbolizerTypeAnalyzer();
                MarkGraphic mg = new MarkGraphic();
                assertTrue(sta.validateGraphic(mg));
        }

        @Test
        public void testInvalidGraphic() throws Exception {
                SymbolizerTypeAnalyzer sta = new SymbolizerTypeAnalyzer();
                MarkGraphic mg = new MarkGraphic();
                mg.setFill(new GraphicFill());
                assertFalse(sta.validateGraphic(mg));
        }

        @Test
        public void testInvalidGraphic2() throws Exception {
                SymbolizerTypeAnalyzer sta = new SymbolizerTypeAnalyzer();
                MarkGraphic mg = new MarkGraphic();
                mg.setStroke(new GraphicStroke());
                assertFalse(sta.validateGraphic(mg));
        }

        @Test
        public void testInvalidGraphic3() throws Exception {
                SymbolizerTypeAnalyzer sta = new SymbolizerTypeAnalyzer();
                MarkGraphic mg = new MarkGraphic();
                PenStroke ps = new PenStroke();
                ps.setFill(new GraphicFill());
                mg.setStroke(ps);
                assertFalse(sta.validateGraphic(mg));
        }

        @Test
        public void testInvalidGraphic4() throws Exception {
                SymbolizerTypeAnalyzer sta = new SymbolizerTypeAnalyzer();
                assertFalse(sta.validateGraphic(new PieChart()));
        }
}
