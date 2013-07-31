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
package org.orbisgis.core.renderer.se;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.UsedAnalysis;
import org.orbisgis.core.renderer.se.visitors.UsedAnalysisVisitor;

/**
 *
 * @author Alexis Guéganno
 */
public class PointSymbolizerTest {

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
                assertTrue(mg.getUom() == Uom.MM);
                //We're dealing with the default symbolizer, so we obtain a MarKgraphic
                //where the UOM has been set to MM. Consequently, even if not
                //set from here, the own Uom of the MarkGraphic is not null.
                assertNotNull(mg.getOwnUom());
        }

        @Test
        public void testInterpolateUsedAnalysis() throws Exception {
            Style style = new Style(null, "../src/test/resources/org/orbisgis/core/renderer/se/symbol_prop_canton_interpol_lin.se");
            PointSymbolizer ps =(PointSymbolizer) style.getRules().get(1).getCompositeSymbolizer().getSymbolizerList().get(0);
            UsedAnalysisVisitor uv = new UsedAnalysisVisitor();
            uv.visitSymbolizerNode(ps);
            UsedAnalysis ua = uv.getUsedAnalysis();
            assertTrue(ua.isInterpolateUsed());
            assertTrue(ua.getAnalysis().size()==1);
        }

}
