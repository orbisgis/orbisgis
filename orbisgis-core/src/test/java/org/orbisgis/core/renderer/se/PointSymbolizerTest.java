/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se;

import org.orbisgis.core.AbstractTest;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 *
 * @author alexis
 */
public class PointSymbolizerTest extends AbstractTest {

        /**
         * When building a PointSymbolizer without argument, we want it to
         * contain a single default MarkGraphic.
         */
        public void testDefaultSymbolizer() throws ParameterException {
                PointSymbolizer ps = new PointSymbolizer();
                MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
                assertTrue(mg.getWkn().getValue(null, 0).equalsIgnoreCase("circle"));
                assertTrue(mg.getViewBox().getHeight().getValue(null, 0) == 3.0);
                assertTrue(mg.getViewBox().getUom() == Uom.MM);
        }

}
