/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.orbisgis.core.AbstractTest;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.UsedAnalysis;

/**
 *
 * @author alexis
 */
public class PointSymbolizerTest extends AbstractTest {

        /**
         * When building a PointSymbolizer without argument, we want it to
         * contain a single default MarkGraphic.
         */
        @Test
        public void testDefaultSymbolizer() throws ParameterException {
                PointSymbolizer ps = new PointSymbolizer();
                MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
                assertTrue(mg.getWkn().getValue(null, 0).equalsIgnoreCase("circle"));
                assertTrue(mg.getViewBox().getWidth().getValue(null, 0) == 3.0);
                assertTrue(mg.getViewBox().getUom() == Uom.MM);
        }

        @Test
        public void testInterpolateUsedAnalysis() throws Exception {
            Style style = new Style(null, "src/test/resources/org/orbisgis/core/renderer/se/symbol_prop_canton_interpol_lin.se");
            PointSymbolizer ps =(PointSymbolizer) style.getRules().get(1).getCompositeSymbolizer().getSymbolizerList().get(0);
            UsedAnalysis ua = ps.getUsedAnalysis();
            assertTrue(ua.isInterpolateUsed());
            assertTrue(ua.getAnalysis().size()==1);
        }

}
