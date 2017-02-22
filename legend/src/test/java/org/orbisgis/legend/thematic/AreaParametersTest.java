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
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.thematic.constant.UniqueSymbolArea;

import java.awt.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author alexis
 */
public class AreaParametersTest extends AnalyzerTest {

    @Test
    public void testInstanciation() throws Exception {
        AreaParameters ap = new AreaParameters(Color.BLUE, .25, 42.0, "2", Color.YELLOW, .46);
        assertTrue(ap.getFillColor().equals(Color.YELLOW));
        assertTrue(ap.getFillOpacity() - .46 < EPS);
        assertTrue(ap.getLineDash().equals("2"));
        assertTrue(ap.getLineColor().equals(Color.BLUE));
        assertTrue(ap.getLineOpacity() - .25 < EPS);
        assertTrue(ap.getLineWidth() - 42.0 < EPS);
    }

    @Test
    public void testDefaultValue() throws Exception {
        AreaParameters ap = new AreaParameters();
        assertTrue(ap.getFillColor().equals(new Color((int)SolidFill.GRAY50,(int)SolidFill.GRAY50,(int)SolidFill.GRAY50)));
        assertTrue(ap.getFillOpacity() - 1 < EPS);
        assertTrue(ap.getLineDash().isEmpty());
        assertTrue(ap.getLineColor().equals(Color.BLACK));
        assertTrue(ap.getLineOpacity() - 1 < EPS);
        assertTrue(ap.getLineWidth() - .25 < EPS);
    }

    @Test
    public void testEquality() throws Exception {
        AreaParameters ap = new AreaParameters(Color.BLUE, .25, 42.0, "2", Color.YELLOW, .46);
        AreaParameters ap2 = new AreaParameters(Color.BLUE, .25, 42.0, "2", Color.YELLOW, .46);
        assertTrue(ap.equals(ap2));
        assertFalse(ap.equals("ahoi"));
    }

    @Test
    public void testNullInput() throws Exception {
        AreaParameters ap = new AreaParameters(Color.BLUE, .25, 42.0, "2", null, .46);
        assertTrue(ap.getFillColor().equals(new Color(SolidFill.GRAY50_INT,SolidFill.GRAY50_INT,SolidFill.GRAY50_INT)));
        ap = new AreaParameters(Color.BLUE, .25, 42.0, "2", Color.YELLOW, null);
        assertTrue(ap.getFillOpacity() - 1.0 < EPS);
    }

    @Test
    public void testUniqueSymbolAreaInstanciation() throws Exception {
        AreaParameters ap = new AreaParameters(Color.BLUE, .25, 42.0, "2", Color.CYAN, .46);
        UniqueSymbolArea usa = new UniqueSymbolArea(ap);
        assertTrue(usa.getFillLegend().getColor().equals(Color.CYAN));
        assertTrue(usa.getFillLegend().getOpacity() - .46 < EPS);
        assertTrue(usa.getStrokeLegend().getDashArray().equals("2"));
        assertTrue(usa.getStrokeLegend().getLineWidth() - 42.0 < EPS);
        assertTrue(usa.getStrokeLegend().getLineColor().equals(Color.BLUE));
        assertTrue(usa.getStrokeLegend().getLineOpacity() - .25 < EPS);
    }

    @Test
    public void testFromUniqueSymbolArea() throws Exception {
        AreaParameters ap = new AreaParameters(Color.BLUE, .25, 42.0, "2", Color.CYAN, .46);
        AreaParameters ap2 = new AreaParameters(Color.BLUE, .25, 42.0, "2", Color.CYAN, .46);
        UniqueSymbolArea usa = new UniqueSymbolArea(ap);
        assertTrue(ap2.equals(usa.getAreaParameters()));
    }

}
