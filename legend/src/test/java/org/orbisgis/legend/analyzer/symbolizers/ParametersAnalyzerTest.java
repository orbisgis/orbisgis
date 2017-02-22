/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.orbisgis.coremap.renderer.se.AreaSymbolizer;
import org.orbisgis.coremap.renderer.se.Style;
import org.orbisgis.coremap.renderer.se.Symbolizer;
import org.orbisgis.coremap.renderer.se.SymbolizerNode;
import org.orbisgis.coremap.renderer.se.fill.DensityFill;
import org.orbisgis.legend.AnalyzerTest;

/**
 * Basic test file to test the class AbstractParametersAnalyzer.
 * @author Alexis Guéganno
 */
public class ParametersAnalyzerTest extends AnalyzerTest {

        @Test
        public void testConstant() throws Exception {
                Style s = getStyle(CONSTANT_POINT);
                Symbolizer sym = s.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
                ParametersAnalyzer pa = new ParametersAnalyzer();
                pa.analyzeParameters(sym);
                assertTrue(pa.isAnalysisLight());
                assertTrue(pa.isFieldUnique());
                assertTrue(pa.isAnalysisUnique());
        }


        @Test
        public void testRecode() throws Exception {
                Style s = getStyle(REAL_RECODE);
                AreaSymbolizer sy = (AreaSymbolizer) s.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
                DensityFill df = (DensityFill) sy.getFill();
                SymbolizerNode orientation = df.getHatchesOrientation();
                ParametersAnalyzer pa = new ParametersAnalyzer();
                pa.analyzeParameters(orientation);
                assertTrue(pa.isAnalysisLight());
                assertTrue(pa.isFieldUnique());
                assertTrue(pa.isAnalysisUnique());
                s = getStyle(STRING_RECODE);
                Symbolizer sym = s.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
                pa = new ParametersAnalyzer();
                pa.analyzeParameters(sym);
                assertTrue(pa.isAnalysisLight());
                assertTrue(pa.isFieldUnique());
                assertTrue(pa.isAnalysisUnique());
                s = getStyle(COLOR_RECODE);
                sym = s.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
                pa = new ParametersAnalyzer();
                pa.analyzeParameters(sym);
                assertTrue(pa.isAnalysisLight());
                assertTrue(pa.isFieldUnique());
                assertTrue(pa.isAnalysisUnique());
        }

        @Test
        public void testAnalysisNotUnique() throws Exception {
                Style s = getStyle(REAL_RECODE);
                Symbolizer sym = s.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
                ParametersAnalyzer pa = new ParametersAnalyzer();
                pa.analyzeParameters(sym);
                assertTrue(pa.isAnalysisLight());
                assertTrue(pa.isFieldUnique());
                assertFalse(pa.isAnalysisUnique());
        }

        @Test
        public void testDoubleCategorize() throws Exception {
                Style s = getStyle(DOUBLE_CATEGORIZE);
                Symbolizer sym = s.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
                ParametersAnalyzer pa = new ParametersAnalyzer();
                pa.analyzeParameters(sym);
                assertTrue(pa.isAnalysisLight());
                assertTrue(pa.isFieldUnique());
                assertTrue(pa.isAnalysisUnique());
        }

        @Test
        public void testDoubleFields() throws Exception {
                Style s = getStyle(DOUBLE_CATEGORIZE_FIELD);
                Symbolizer sym = s.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
                ParametersAnalyzer pa = new ParametersAnalyzer();
                pa.analyzeParameters(sym);
                assertTrue(pa.isAnalysisLight());
                assertFalse(pa.isFieldUnique());
                assertTrue(pa.isAnalysisUnique());
        }

        @Test
        public void testNestedAnalysis() throws Exception {
                Style s = getStyle(NESTED);
                Symbolizer sym = s.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
                ParametersAnalyzer pa = new ParametersAnalyzer();
                pa.analyzeParameters(sym);
                assertFalse(pa.isAnalysisLight());
                assertTrue(pa.isFieldUnique());
                assertTrue(pa.isAnalysisUnique());
        }
}
