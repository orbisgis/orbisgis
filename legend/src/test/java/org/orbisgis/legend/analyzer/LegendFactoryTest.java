/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.analyzer;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.thematic.constant.UniqueSymbolLine;
import org.orbisgis.legend.thematic.constant.UniqueSymbolPoint;
import org.orbisgis.legend.thematic.factory.LegendFactory;

/**
 *
 * @author alexis
 */
public class LegendFactoryTest extends AnalyzerTest {

        @Test
        public void testGetLSLegend() throws Exception {
                Style st = getStyle("src/test/resources/org/orbisgis/legend/uniqueLineSymbol.se");
                Legend leg = LegendFactory.getLegend(st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                assertTrue(leg instanceof UniqueSymbolLine);
        }
        
        @Test
        public void testGetPSLegend() throws Exception {
                Style st = getStyle("src/test/resources/org/orbisgis/legend/constantWKN.se");
                Legend leg = LegendFactory.getLegend(st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                assertTrue(leg instanceof UniqueSymbolPoint);
        }

}
