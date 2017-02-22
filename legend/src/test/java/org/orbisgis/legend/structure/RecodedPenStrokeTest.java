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
package org.orbisgis.legend.structure;

import org.junit.Test;
import org.orbisgis.coremap.renderer.se.LineSymbolizer;
import org.orbisgis.coremap.renderer.se.Style;
import org.orbisgis.coremap.renderer.se.fill.SolidFill;
import org.orbisgis.coremap.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.coremap.renderer.se.parameter.real.Recode2Real;
import org.orbisgis.coremap.renderer.se.parameter.string.Recode2String;
import org.orbisgis.coremap.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.coremap.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.structure.fill.constant.NullSolidFillLegend;
import org.orbisgis.legend.structure.recode.RecodedReal;
import org.orbisgis.legend.structure.recode.RecodedString;
import org.orbisgis.legend.structure.stroke.RecodedPenStroke;

import static org.junit.Assert.assertTrue;

/**
 * @author alexis
 */
public class RecodedPenStrokeTest extends AnalyzerTest {

    @Test
    public void testDashChangePropagation() throws Exception {
        PenStroke ps = getPenStroke();
        RecodedPenStroke rps = new RecodedPenStroke(ps);
        RecodedString dashes = rps.getDashLegend();
        assertTrue(rps.getDashLegend().getParameter() instanceof StringLiteral);
        assertTrue(ps.getDashArray() instanceof StringLiteral);
        assertTrue(ps.getDashArray() == dashes.getParameter());
        dashes.addItem("bonjour","2 5 8 5");
        assertTrue(rps.getDashLegend().getParameter() instanceof Recode2String);
        assertTrue(ps.getDashArray() instanceof Recode2String);
        assertTrue(ps.getDashArray() == dashes.getParameter());
    }

    @Test
    public void testDashChangePropagationBis() throws Exception {
        PenStroke ps = getPenStrokeDashes();
        RecodedPenStroke rps = new RecodedPenStroke(ps);
        RecodedString dashes = rps.getDashLegend();
        assertTrue(dashes.getParameter() instanceof Recode2String);
        assertTrue(ps.getDashArray() instanceof Recode2String);
        assertTrue(ps.getDashArray() == dashes.getParameter());
        dashes.removeItem("2");
        dashes.removeItem("5");
        dashes.removeItem("7");
        //We should have a literal from now on.
        assertTrue(rps.getDashLegend().getParameter() instanceof StringLiteral);
        assertTrue(ps.getDashArray() instanceof StringLiteral);
        assertTrue(ps.getDashArray() == dashes.getParameter());
    }

    @Test
    public void testWidthChangePropagation() throws Exception {
        PenStroke ps = getPenStroke();
        RecodedPenStroke rps = new RecodedPenStroke(ps);
        RecodedReal widthLegend = rps.getWidthLegend();
        assertTrue(rps.getWidthLegend().getParameter() instanceof RealLiteral);
        assertTrue(ps.getWidth() instanceof RealLiteral);
        assertTrue(ps.getWidth() == widthLegend.getParameter());
        widthLegend.addItem("bonjour", 50.0);
        assertTrue(rps.getWidthLegend().getParameter() instanceof Recode2Real);
        assertTrue(ps.getWidth() instanceof Recode2Real);
        assertTrue(ps.getWidth() == widthLegend.getParameter());
    }

    @Test
    public void testWidthChangePropagationBis() throws Exception {
        PenStroke ps = getPenStroke();
        RecodedPenStroke rps = new RecodedPenStroke(ps);
        RecodedReal widthLegend = rps.getWidthLegend();
        assertTrue(rps.getWidthLegend().getParameter() instanceof RealLiteral);
        assertTrue(ps.getWidth() instanceof RealLiteral);
        assertTrue(ps.getWidth() == widthLegend.getParameter());
        widthLegend.addItem("bonjour", 50.0);
        assertTrue(rps.getWidthLegend().getParameter() instanceof Recode2Real);
        assertTrue(ps.getWidth() instanceof Recode2Real);
        assertTrue(ps.getWidth() == widthLegend.getParameter());
        widthLegend.removeItem("bonjour");
        assertTrue(rps.getWidthLegend().getParameter() instanceof RealLiteral);
        assertTrue(ps.getWidth() instanceof RealLiteral);
        assertTrue(ps.getWidth() == widthLegend.getParameter());
    }

    private PenStroke getPenStroke() throws Exception{
        Style s = getStyle(COLOR_RECODE);
        LineSymbolizer ls = (LineSymbolizer) s.getRules().get(0).getCompositeSymbolizer().getChildren().get(0);
        return (PenStroke) ls.getStroke();
    }

    private PenStroke getPenStrokeDashes() throws Exception{
        Style s = getStyle(DASH_RECODE);
        LineSymbolizer ls = (LineSymbolizer) s.getRules().get(0).getCompositeSymbolizer().getChildren().get(0);
        return (PenStroke) ls.getStroke();
    }
}
