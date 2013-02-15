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
package org.orbisgis.legend.structure;

import org.junit.Assert;
import org.junit.Test;
import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.structure.fill.RecodedSolidFillLegend;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFill;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFillLegend;
import org.orbisgis.legend.structure.fill.constant.NullSolidFillLegend;
import org.orbisgis.legend.structure.recode.RecodedReal;
import org.orbisgis.legend.structure.recode.RecodedString;
import org.orbisgis.legend.structure.stroke.RecodedPenStroke;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author alexis
 */
public class RecodedPenStrokeTest extends AnalyzerTest {

    @Test
    public void testSetWidthLegendLiteral() throws Exception {
        RecodedPenStroke rps = new RecodedPenStroke(getPenStroke());
        RecodedReal rll = new RecodedReal(new RealLiteral(4));
        rps.setWidthLegend(rll);
        assertTrue(rps.getWidthLegend() == rll);
    }

    @Test
    public void testGetDashLegend() throws Exception {
        RecodedPenStroke rps = new RecodedPenStroke(getPenStroke());
        assertTrue(rps.getDashLegend().getFallbackValue().isEmpty());
    }

    @Test
    public void testSetDashLegend() throws Exception {
        PenStroke ps = getPenStroke();
        RecodedPenStroke rps = new RecodedPenStroke(ps);
        assertTrue(rps.getDashLegend().getFallbackValue().isEmpty());
        rps.setDashLegend(new RecodedString(new StringLiteral("sss")));
        assertTrue(ps.getDashArray() == rps.getDashLegend().getParameter());
    }

    @Test
    public void testSetNullFill() throws Exception {
        PenStroke ps = getPenStroke();
        RecodedPenStroke rps = new RecodedPenStroke(ps);
        rps.setFillLegend(null);
        assertTrue(ps.getFill() instanceof SolidFill);
        assertTrue(rps.getFillLegend() instanceof RecodedSolidFillLegend);
    }

    @Test
    public void testSetNullSolidFill() throws Exception {
        PenStroke ps = getPenStroke();
        RecodedPenStroke rps = new RecodedPenStroke(ps);
        rps.setFillLegend(new NullSolidFillLegend());
        assertTrue(ps.getFill() instanceof SolidFill);
        assertTrue(rps.getFillLegend() instanceof RecodedSolidFillLegend);
    }

    private PenStroke getPenStroke() throws Exception{
        Style s = getStyle(COLOR_RECODE);
        LineSymbolizer ls = (LineSymbolizer) s.getRules().get(0).getCompositeSymbolizer().getChildren().get(0);
        return (PenStroke) ls.getStroke();
    }
}
