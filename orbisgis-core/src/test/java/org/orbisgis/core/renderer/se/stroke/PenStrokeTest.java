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
package org.orbisgis.core.renderer.se.stroke;

import org.junit.Test;
import org.orbisgis.core.AbstractTest;
import org.orbisgis.core.renderer.se.fill.GraphicFill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;

import java.awt.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Check the behavior of PenStroke.
 * @author alexis
 */
public class PenStrokeTest extends AbstractTest {

    /**
     * null values must be replaced by the default ones.
     * @throws Exception
     */
    @Test
    public void testSetWidth() throws Exception {
        PenStroke ps = new PenStroke();
        assertEquals(ps.getWidth().getValue(null), (Double) PenStroke.DEFAULT_WIDTH);
        ps.setWidth(null);
        assertEquals(ps.getWidth().getValue(null), (Double) PenStroke.DEFAULT_WIDTH);
        ps.setWidth(new RealLiteral(0.4));
        assertEquals(ps.getWidth().getValue(null),(Double) 0.4);
    }

    @Test
    public void testSetArray() throws Exception {
        PenStroke ps = new PenStroke();
        assertTrue(ps.getDashArray().getValue(null).isEmpty());
        ps.setDashArray(null);
        assertTrue(ps.getDashArray().getValue(null).isEmpty());
        ps.setDashArray(new StringLiteral("1 1 1"));
        assertTrue(ps.getDashArray().getValue(null).equals("1 1 1"));
    }

    @Test
    public void testSetLineCap() throws Exception {
        PenStroke ps = new PenStroke();
        assertEquals(ps.getLineCap(), PenStroke.LineCap.BUTT);
        ps.setLineCap(null);
        assertEquals(ps.getLineCap(), PenStroke.LineCap.BUTT);
        ps.setLineCap(PenStroke.LineCap.SQUARE);
        assertEquals(ps.getLineCap(), PenStroke.LineCap.SQUARE);
    }

    @Test
    public void testSetLineJoin() throws Exception {
        PenStroke ps = new PenStroke();
        assertEquals(ps.getLineJoin(), PenStroke.LineJoin.MITRE);
        ps.setLineJoin(null);
        assertEquals(ps.getLineJoin(), PenStroke.LineJoin.MITRE);
        ps.setLineJoin(PenStroke.LineJoin.BEVEL);
        assertEquals(ps.getLineJoin(), PenStroke.LineJoin.BEVEL);
    }

    @Test
    public void testSetOffset() throws Exception {
        PenStroke ps = new PenStroke();
        assertEquals(ps.getDashOffset().getValue(null),(Double) 0.0);
        ps.setDashOffset(null);
        assertEquals(ps.getDashOffset().getValue(null), (Double) 0.0);
        ps.setDashOffset(new RealLiteral(2.0));
        assertEquals(ps.getDashOffset().getValue(null), (Double) 2.0);
    }

    @Test
    public void testSetFill() throws Exception {
        PenStroke ps = new PenStroke();
        assertTrue(ps.getFill() instanceof SolidFill);
        SolidFill sf = (SolidFill) ps.getFill();
        assertEquals(sf.getColor().getColor(null),Color.BLACK);
        assertEquals(sf.getOpacity().getValue(null), (Double)1.0);
        ps.setFill(null);
        assertTrue(ps.getFill() instanceof SolidFill);
        sf = (SolidFill) ps.getFill();
        assertEquals(sf.getColor().getColor(null),Color.BLACK);
        assertEquals(sf.getOpacity().getValue(null), (Double)1.0);
        ps.setFill(new GraphicFill());
        assertFalse(ps.getFill() instanceof SolidFill);
    }

}
