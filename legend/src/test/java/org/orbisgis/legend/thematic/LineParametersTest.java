/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.legend.thematic;

import org.junit.Test;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.AnalyzerTest;

import java.awt.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author alexis
 */
public class LineParametersTest extends AnalyzerTest {

    @Test
    public void testNullInput() throws Exception {
        LineParameters lp = new LineParameters(null,null,null,null);
        assertTrue(lp.getLineColor().equals(Color.BLACK));
        assertTrue(lp.getLineDash().isEmpty());
        assertTrue(lp.getLineOpacity() == 1.0);
        assertTrue(lp.getLineWidth() == PenStroke.DEFAULT_WIDTH);
    }

    @Test
    public void testValidInput() throws Exception {
        LineParameters lp = new LineParameters(Color.BLUE,.4,2.0,"2 2");
        assertTrue(lp.getLineColor().equals(Color.BLUE));
        assertTrue(lp.getLineDash().equals("2 2"));
        assertTrue(lp.getLineOpacity() == .4);
        assertTrue(lp.getLineWidth() == 2.0);
    }

    @Test
    public void testInvalidDash() throws Exception {
        LineParameters lp = new LineParameters(Color.BLUE,.4,2.0,"2 2 bonjour");
        assertTrue(lp.getLineColor().equals(Color.BLUE));
        assertTrue(lp.getLineDash().isEmpty());
        assertTrue(lp.getLineOpacity() == .4);
        assertTrue(lp.getLineWidth() == 2.0);
    }

    @Test
    public void testEquality() throws Exception {
        LineParameters lp = new LineParameters(Color.BLUE,.4,2.0,"2 2");
        LineParameters lp2 = new LineParameters(Color.BLUE,.4,2.0,"2 2");
        assertTrue(lp.equals(lp2));
        lp2 = new LineParameters(Color.BLUE,.4,2.0,"2 1 2");
        assertFalse(lp.equals(lp2));
        lp2 = new LineParameters(Color.BLUE,.4,3.0,"2 2");
        assertFalse(lp.equals(lp2));
        lp2 = new LineParameters(Color.BLUE,.5,2.0,"2 2");
        assertFalse(lp.equals(lp2));
        lp2 = new LineParameters(Color.YELLOW,.4,2.0,"2 2");
        assertFalse(lp.equals(lp2));
    }
}
