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
package org.orbisgis.coremap.renderer.se.common;

import java.awt.Color;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.orbisgis.coremap.renderer.se.fill.Fill;
import org.orbisgis.coremap.renderer.se.fill.SolidFill;
import org.orbisgis.coremap.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.coremap.renderer.se.parameter.real.RealParameterContext;

/**
 *
 * @author Alexis Guéganno
 */
public class HaloTest {

        @Test
        public void testHaloInitialization() throws Exception {
                testDefaultHalo(new Halo());
        }

        @Test
        public void testHaloInitializationWithNullInput() throws Exception {
                testDefaultHalo(new Halo(null,null));
        }

        @Test
        public void testHaloSetters() throws Exception {
                Halo halo = new Halo();
                Fill f = new SolidFill();
                halo.setFill(f);
                assertTrue(halo.getFill() == f);
                RealLiteral rl = new RealLiteral(.5);
                rl.setContext(RealParameterContext.NON_NEGATIVE_CONTEXT);
                halo.setRadius(rl);
                assertEquals(halo.getRadius().getValue(null), (Double) .5);
                assertTrue(halo.getRadius().getContext() == RealParameterContext.REAL_CONTEXT);
                halo.setFill(null);
                halo.setRadius(null);
                testDefaultHalo(halo);
        }

        @Test
        public void testGetChildren() throws Exception {
                Halo h = new Halo();
                assertTrue(h.getChildren().size() == 2);
        }

        private void testDefaultHalo(Halo halo) throws Exception {
                assertTrue(halo.getFill() instanceof SolidFill);
                SolidFill fill = (SolidFill) halo.getFill();
                assertTrue(fill.getColor().getColor(null).equals(Color.WHITE));
                assertTrue(fill.getOpacity().getValue(null) == 1);
                assertTrue(halo.getRadius().getValue(null) == 1);
        }
}
