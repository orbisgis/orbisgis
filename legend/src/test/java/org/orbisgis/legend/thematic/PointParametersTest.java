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
package org.orbisgis.legend.thematic;

import org.junit.Test;
import org.orbisgis.coremap.renderer.se.fill.SolidFill;
import org.orbisgis.coremap.renderer.se.graphic.WellKnownName;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.thematic.constant.UniqueSymbolPoint;

import java.awt.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author alexis
 */
public class PointParametersTest extends AnalyzerTest {

    @Test
    public void testInstanciation() throws Exception {
        PointParameters pp = new PointParameters(Color.BLUE, .25, 42.0, "2", Color.YELLOW, .46, 1.0, 4.0, "SQUARE");
        assertTrue(pp.getWkn().equalsIgnoreCase("SQUARE"));
        assertTrue(pp.getWidth() - 1.0 < EPS);
        assertTrue(pp.getHeight() - 4.0 < EPS);
        assertTrue(pp.getFillColor().equals(Color.YELLOW));
        assertTrue(pp.getFillOpacity() - .46 < EPS);
        assertTrue(pp.getLineDash().equals("2"));
        assertTrue(pp.getLineColor().equals(Color.BLUE));
        assertTrue(pp.getLineOpacity() - .25 < EPS);
        assertTrue(pp.getLineWidth() - 42.0 < EPS);
    }

    @Test
    public void testDefaultValue() throws Exception {
        PointParameters pp = new PointParameters();
        assertTrue(pp.getWidth() - 3.0 < EPS);
        assertTrue(pp.getHeight() - 3.0 < EPS);
        assertTrue(pp.getWkn().equals(WellKnownName.CIRCLE.toString()));
        assertTrue(pp.getFillColor().equals(new Color((int) SolidFill.GRAY50, (int) SolidFill.GRAY50, (int) SolidFill.GRAY50)));
        assertTrue(pp.getFillOpacity() - 1 < EPS);
        assertTrue(pp.getLineDash().isEmpty());
        assertTrue(pp.getLineColor().equals(Color.BLACK));
        assertTrue(pp.getLineOpacity() - 1 < EPS);
        assertTrue(pp.getLineWidth() - .25 < EPS);
    }

    @Test
    public void testEquality() throws Exception {
        PointParameters pp = new PointParameters(Color.BLUE, .25, 42.0, "2", Color.YELLOW, .46, 1.0, 4.0, "SQUARE");
        PointParameters pp2 = new PointParameters(Color.BLUE, .25, 42.0, "2", Color.YELLOW, .46, 1.0, 4.0, "SQUARE");
        assertTrue(pp.equals(pp2));
        pp = new PointParameters(Color.CYAN, .25, 42.0, "2", Color.YELLOW, .46, 1.0, 4.0, "SQUARE");
        pp2 = new PointParameters(Color.BLUE, .25, 42.0, "2", Color.YELLOW, .46, 1.0, 4.0, "SQUARE");
        assertFalse(pp.equals(pp2));
        pp = new PointParameters(Color.BLUE, .25, 42.0, "2", Color.YELLOW, .8, 1.0, 4.0, "SQUARE");
        pp2 = new PointParameters(Color.BLUE, .25, 42.0, "2", Color.YELLOW, .46, 1.0, 4.0, "SQUARE");
        assertFalse(pp.equals(pp2));
        pp = new PointParameters(Color.BLUE, .25, 42.0, "2", Color.YELLOW, .46, null, 4.0, "SQUARE");
        pp2 = new PointParameters(Color.BLUE, .25, 42.0, "2", Color.YELLOW, .46, 3.0, 4.0, "SQUARE");
        assertFalse(pp.equals(pp2));
        pp = new PointParameters(Color.BLUE, .25, 42.0, "2", Color.YELLOW, .46, null, 5.0, "SQUARE");
        pp2 = new PointParameters(Color.BLUE, .25, 42.0, "2", Color.YELLOW, .46, 5.0, 5.0, "SQUARE");
        assertTrue(pp.equals(pp2));
        pp = new PointParameters(Color.BLUE, .25, 42.0, "2", Color.YELLOW, .46, 1.0, null, "SQUARE");
        pp2 = new PointParameters(Color.BLUE, .25, 42.0, "2", Color.YELLOW, .46, 1.0, 3.0, "SQUARE");
        assertFalse(pp.equals(pp2));
        pp = new PointParameters(Color.BLUE, .25, 42.0, "2", Color.YELLOW, .46, 1.0, null, "SQUARE");
        pp2 = new PointParameters(Color.BLUE, .25, 42.0, "2", Color.YELLOW, .46, 1.0, 1.0, "SQUARE");
        assertTrue(pp.equals(pp2));
        pp = new PointParameters(Color.BLUE, .25, 42.0, "2", Color.YELLOW, .46, null, null, "SQUARE");
        pp2 = new PointParameters(Color.BLUE, .25, 42.0, "2", Color.YELLOW, .46, 3.0, 3.0, "SQUARE");
        assertTrue(pp.equals(pp2));
        pp = new PointParameters(Color.BLUE, .25, 42.0, "2", Color.YELLOW, .46, 1.0, 4.0, null);
        pp2 = new PointParameters(Color.BLUE, .25, 42.0, "2", Color.YELLOW, .46, 1.0, 4.0, "CIRCLE");
        assertTrue(pp.equals(pp2));
    }

    @Test
    public void testUniqueSymbolPointInstanciation() throws Exception {
        PointParameters ap = new PointParameters(Color.BLUE, .25, 42.0, "2", Color.CYAN, .46, 1.0, 4.0, "SQUARE");
        UniqueSymbolPoint usp = new UniqueSymbolPoint(ap);
        assertTrue(usp.getFillLegend().getColor().equals(Color.CYAN));
        assertTrue(usp.getFillLegend().getOpacity() - .46 < EPS);
        assertTrue(usp.getPenStroke().getDashArray().equals("2"));
        assertTrue(usp.getPenStroke().getLineWidth() - 42.0 < EPS);
        assertTrue(usp.getPenStroke().getLineColor().equals(Color.BLUE));
        assertTrue(usp.getPenStroke().getLineOpacity() - .25 < EPS);
        assertTrue(usp.getWellKnownName().equalsIgnoreCase("SQUARE"));
        assertTrue(usp.getViewBoxWidth() - 1.0 < EPS);
        assertTrue(usp.getViewBoxHeight() - 4.0 < EPS);
    }

    @Test
    public void testFromUniqueSymbolArea() throws Exception {
        PointParameters ap = new PointParameters(Color.BLUE, .25, 42.0, "2", Color.CYAN, .46, 1.0, 4.0, "SQUARE");
        PointParameters ap2 = new PointParameters(Color.BLUE, .25, 42.0, "2", Color.CYAN, .46, 1.0, 4.0, "SQUARE");
        UniqueSymbolPoint usa = new UniqueSymbolPoint(ap);
        assertTrue(ap2.equals(usa.getPointParameters()));
    }
}
