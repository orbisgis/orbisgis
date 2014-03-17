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
package org.orbisgis.legend.analyzer;

import java.awt.Color;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.orbisgis.coremap.renderer.se.LineSymbolizer;
import org.orbisgis.coremap.renderer.se.Style;
import org.orbisgis.coremap.renderer.se.fill.SolidFill;
import org.orbisgis.coremap.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.coremap.renderer.se.parameter.color.Recode2Color;
import org.orbisgis.coremap.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.coremap.renderer.se.parameter.real.Recode2Real;
import org.orbisgis.coremap.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.legend.analyzer.symbolizers.LineSymbolizerAnalyzer;
import org.orbisgis.legend.structure.fill.RecodedSolidFillLegend;
import org.orbisgis.legend.structure.recode.RecodedColor;
import org.orbisgis.legend.structure.recode.RecodedReal;
import org.orbisgis.legend.thematic.recode.RecodedLine;

/**
 *
 * @author Alexis Guéganno
 */
public class LineSymbolizerRecodedTest extends AnalyzerTest{

        @Test
        public void testRecognition() throws Exception {
                LineSymbolizer ls = getLineSymbolizer();
                LegendStructure l = new LineSymbolizerAnalyzer(ls).getLegend();
                assertTrue(l instanceof RecodedLine);
        }

        @Test
        public void testListenersOnStroke() throws Exception {
                LineSymbolizer ls = getLineSymbolizer();
                PenStroke ps = (PenStroke) ls.getStroke();
                SolidFill sf = (SolidFill) ps.getFill();
                assertTrue(sf.getColor() instanceof Recode2Color);
                RecodedSolidFillLegend l = new RecodedSolidFillLegend(sf);
                RecodedColor rc = (RecodedColor) l.getFillColorLegend();
                rc.removeItem(0);
                rc.removeItem(0);
                rc.removeItem(0);
                rc.removeItem(0);
                assertTrue(sf.getColor() instanceof ColorLiteral);
                rc.addItem("12", new Color(0,0,0));
                assertTrue(sf.getColor() instanceof Recode2Color);
                assertTrue(sf.getOpacity() instanceof RealLiteral);
                RecodedReal rr = (RecodedReal) l.getFillOpacityLegend();
                rr.addItem("rlal",20.0);
                assertTrue(sf.getOpacity() instanceof Recode2Real);
                rr.removeItem(0);
                assertTrue(sf.getOpacity() instanceof RealLiteral);
        }

        private LineSymbolizer getLineSymbolizer() throws Exception {
                Style st = getStyle(COLOR_RECODE);
                return (LineSymbolizer) st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        }

}
