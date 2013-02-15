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
package org.orbisgis.legend.thematic;

import org.junit.Test;
import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.structure.recode.RecodedColor;
import org.orbisgis.legend.thematic.recode.RecodedLine;

import java.awt.*;

import static org.junit.Assert.assertTrue;

/**
 * @author alexis
 */
public class RecodedLineTest extends AnalyzerTest {

    @Test
    public void testGetFillOpacity() throws Exception{
        LineSymbolizer ls = getLineSymbolizer();
        RecodedLine  rl = new RecodedLine(ls);
        assertTrue(rl.getLineOpacity().getFallbackValue() == 1.0);
    }

    @Test
    public void testGetOpacitySetNullFill() throws Exception{
        LineSymbolizer ls = getLineSymbolizer();
        PenStroke ps = (PenStroke) ls.getStroke();
        ps.setFill(null);
        RecodedLine  rl = new RecodedLine(ls);
        assertTrue(rl.getLineOpacity().getFallbackValue() == 1.0);
    }

    @Test
    public void testGetColor() throws Exception {
        LineSymbolizer lineSymbolizer = getLineSymbolizer();
        PenStroke ps = (PenStroke) lineSymbolizer.getStroke();
        RecodedLine rl = new RecodedLine(lineSymbolizer);
        RecodedColor rc = rl.getLineColor();
        assertTrue(rc.getParameter() == ((SolidFill)ps.getFill()).getColor());
    }

    @Test
    public void testGetColorSetNullFill() throws Exception {
        LineSymbolizer lineSymbolizer = getLineSymbolizer();
        PenStroke ps = (PenStroke) lineSymbolizer.getStroke();
        ps.setFill(null);
        RecodedLine rl = new RecodedLine(lineSymbolizer);
        assertTrue(rl.getLineColor().getFallbackValue().equals(Color.BLACK));
    }

    private LineSymbolizer getLineSymbolizer() throws Exception{
        Style s = getStyle(COLOR_RECODE);
        return (LineSymbolizer) s.getRules().get(0).getCompositeSymbolizer().getChildren().get(0);
    }
}
