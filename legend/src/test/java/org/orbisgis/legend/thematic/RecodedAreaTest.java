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
import org.orbisgis.core.renderer.se.AreaSymbolizer;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.analyzer.symbolizers.AreaSymbolizerAnalyzer;
import org.orbisgis.legend.thematic.recode.RecodedArea;

import static org.junit.Assert.assertTrue;

/**
 * @author alexis
 */
public class RecodedAreaTest extends AnalyzerTest {

    @Test
    public void testInstanciation() throws Exception{
        AreaSymbolizer as = getAreaSymbolizer();
        AreaSymbolizerAnalyzer asa = new AreaSymbolizerAnalyzer(as);
        RecodedArea ra = (RecodedArea) asa.getLegend();
        assertTrue(true);
    }



    private AreaSymbolizer getAreaSymbolizer() throws Exception{
        Style s = getStyle(AREA_RECODE);
        return (AreaSymbolizer) s.getRules().get(0).getCompositeSymbolizer().getChildren().get(0);
    }

    private RecodedArea getRecodedLine() throws Exception {
        return new RecodedArea(getAreaSymbolizer());
    }
}
