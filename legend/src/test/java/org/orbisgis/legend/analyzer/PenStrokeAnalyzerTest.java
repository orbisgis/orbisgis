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

import org.orbisgis.legend.structure.recode.Recode2StringLegend;
import org.orbisgis.legend.structure.stroke.RecodedDashesPSLegend;
import org.orbisgis.legend.structure.categorize.Categorize2StringLegend;
import org.orbisgis.legend.structure.stroke.CategorizedDashesPSLegend;
import org.orbisgis.legend.structure.interpolation.LinearInterpolationLegend;
import org.orbisgis.legend.structure.stroke.PenStrokeLegend;
import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.Style;
import javax.xml.bind.JAXBElement;
import java.io.File;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBContext;
import net.opengis.se._2_0.core.StyleType;
import org.junit.Test;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.structure.stroke.ProportionalStrokeLegend;
import static org.junit.Assert.*;
/**
 * test that we're able to understand what people are doing with PenStrokes.
 * @author Alexis Guéganno
 */
public class PenStrokeAnalyzerTest extends AnalyzerTest {

        @Test
        public void testProportionalStroke() throws Exception {
                //We have some XML where a proportional line is defined :
                Style st = getStyle("src/test/resources/org/orbisgis/legend/linearProportional.se");
                //I want to work on the PenStroke.
                 LineSymbolizer ls =(LineSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                 PenStroke ps = (PenStroke) (ls.getStroke());
                 PenStrokeAnalyzer psa = new PenStrokeAnalyzer(ps);
                 PenStrokeLegend psl = (PenStrokeLegend) (psa.getLegend());
                 assertTrue(psl.getWidthAnalysis() instanceof LinearInterpolationLegend);
                 assertTrue(psl instanceof ProportionalStrokeLegend);
        }

        @Test
        public void testCategorizedDashArray() throws Exception {
                //We have some XML where a categorized dash array is defined :
                Style st = getStyle("src/test/resources/org/orbisgis/legend/dashClassif.se");
                //I want to work on the PenStroke.
                 LineSymbolizer ls =(LineSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                 PenStroke ps = (PenStroke) (ls.getStroke());
                 PenStrokeAnalyzer psa = new PenStrokeAnalyzer(ps);
                 PenStrokeLegend psl = (PenStrokeLegend) (psa.getLegend());
                 assertTrue(psl.getDashAnalysis() instanceof Categorize2StringLegend);
                 assertTrue(psl instanceof CategorizedDashesPSLegend);
        }

        @Test
        public void testRecodedDashArray() throws Exception {
                //We have some XML where a recoded dash array is defined :
                Style st = getStyle("src/test/resources/org/orbisgis/legend/dashRecode.se");
                //I want to work on the PenStroke.
                 LineSymbolizer ls =(LineSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                 PenStroke ps = (PenStroke) (ls.getStroke());
                 PenStrokeAnalyzer psa = new PenStrokeAnalyzer(ps);
                 PenStrokeLegend psl = (PenStrokeLegend) (psa.getLegend());
                 assertTrue(psl.getDashAnalysis() instanceof Recode2StringLegend);
                 assertTrue(psl instanceof RecodedDashesPSLegend);
        }

        @Test
        public void testTooComplexPenStroke() throws Exception {
                //We have some XML where the pen stroke is too complex to be analyzed :
                Style st = getStyle("src/test/resources/org/orbisgis/legend/complexPenStroke.se");
                //I want to work on the PenStroke.
                 LineSymbolizer ls =(LineSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                 PenStroke ps = (PenStroke) (ls.getStroke());
                 PenStrokeAnalyzer psa = new PenStrokeAnalyzer(ps);
                 PenStrokeLegend psl = (PenStrokeLegend) (psa.getLegend());
                 assertTrue(psl.getDashAnalysis() instanceof Categorize2StringLegend);
                 assertTrue(psl.getWidthAnalysis() instanceof LinearInterpolationLegend);
                 assertFalse(psl instanceof RecodedDashesPSLegend);
                 assertFalse(psl instanceof CategorizedDashesPSLegend);
                 assertFalse(psl instanceof ProportionalStrokeLegend);
                 assertTrue(psl instanceof PenStrokeLegend);
        }

}
