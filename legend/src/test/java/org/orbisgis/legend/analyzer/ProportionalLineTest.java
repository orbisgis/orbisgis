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
import org.junit.Test;
import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.real.Interpolate2Real;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.structure.stroke.PenStrokeLegend;
import org.orbisgis.legend.structure.stroke.ProportionalStrokeLegend;
import org.orbisgis.legend.thematic.proportional.ProportionalLine;

/**
 *
 * @author Alexis Guéganno
 */
public class ProportionalLineTest extends AnalyzerTest {

        @Test
        public void testDefaultInstanciation() throws Exception {
                ProportionalLine pl = new ProportionalLine();
                assertTrue(pl.getFirstData() == 0);
                assertTrue(pl.getFirstValue() == 0);
                assertTrue(pl.getSecondData() == 1);
                assertTrue(pl.getSecondValue() == 1);
                assertTrue(pl.getStrokeLegend() instanceof ProportionalStrokeLegend);
                LineSymbolizer ls = (LineSymbolizer) pl.getSymbolizer();
                PenStroke ps = (PenStroke) ls.getStroke();
                assertTrue(ps.getWidth() instanceof Interpolate2Real);
                assertTrue(ps == ((PenStrokeLegend)pl.getStrokeLegend()).getStroke());
        }

        @Test
        public void testGetUom() throws Exception {
                ProportionalLine pl = new ProportionalLine();
                assertTrue(pl.getStrokeUom() == Uom.PX);
        }

        @Test
        public void testSetUom() throws Exception {
                ProportionalLine pl = new ProportionalLine();
                pl.setStrokeUom(Uom.IN);
                assertTrue(pl.getStrokeUom() == Uom.IN);

        }
}
