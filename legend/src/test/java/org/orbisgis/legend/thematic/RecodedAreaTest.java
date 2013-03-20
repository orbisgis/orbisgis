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

import java.awt.*;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

    @Test
    public void testGetParams() throws Exception {
        RecodedArea ra = getRecodedArea();
        assertTrue(ra.size() == 5);
        assertNull(ra.get("potato"));
        AreaParameters ap = new AreaParameters(new Color(34,51,68), 1.0, 1.0, "", new Color(34,51,68), 0.9);
        AreaParameters ret = ra.get("1");
        assertTrue(ret.equals(ap));
    }

    @Test
    public void testGetParamFallbackVal() throws Exception {
        RecodedArea ra = getRecodedArea();
        AreaParameters ap = new AreaParameters(new Color(88,174, 35), 1.0, 0.4, "", new Color(88,172, 35), 0.9);
        AreaParameters ret = ra.get("9999");
        assertTrue(ret.equals(ap));
        ap = new AreaParameters(new Color(170,23,180), 1.0, 0.4, "", new Color(51,85,102), .5);
        ret = ra.get("25");
        assertTrue(ret.equals(ap));
    }

    @Test
    public void testGetFallback() throws Exception {
        RecodedArea ra = getRecodedArea();
        AreaParameters fb = ra.getFallbackParameters();
        AreaParameters t = new AreaParameters(new Color(51, 85, 103),1.0,0.4,"",new Color(51,85,102),.9);
        assertTrue(t.equals(fb));
    }

    @Test
    public void testPutNonExistingKey() throws Exception {
        RecodedArea ra = getRecodedArea();
        AreaParameters ap = new AreaParameters(new Color(88,74, 235), 1.0, 0.4, "2", new Color(0xEC,0x44, 5), 0.9);
        String newKey = "I am new !";
        AreaParameters ret = ra.put(newKey, ap);
        assertNull(ret);
        assertTrue(ra.get(newKey).equals(new AreaParameters(new Color(88,74, 235), 1.0, 0.4, "2", new Color(0xEC,0x44, 5), 0.9)));
    }

    @Test
    public void testPutExistingKey() throws Exception {
        RecodedArea ra = getRecodedArea();
        AreaParameters expected = new AreaParameters(new Color(88,174, 35), 1.0, 0.4, "", new Color(88,172, 35), 0.9);
        AreaParameters ap = new AreaParameters(new Color(88,74, 235), 1.0, 0.4, "2", new Color(0xEC,0x44, 5), 0.9);
        AreaParameters ret = ra.put("9999",ap);
        assertTrue(ret.equals(expected));
        assertTrue(ra.get("9999").equals(new AreaParameters(new Color(88, 74, 235), 1.0, 0.4, "2", new Color(0xEC, 0x44, 5), 0.9)));
    }

    @Test
    public void testPutNull() throws Exception{
        RecodedArea ra = getRecodedArea();
        try{
            ra.put(null, new AreaParameters());
            fail();
        } catch (NullPointerException npe){
        }
        try{
            ra.put("yo",null);
            fail();
        } catch (NullPointerException npe){
        }
        assertTrue(true);
    }

    private AreaSymbolizer getAreaSymbolizer() throws Exception{
        Style s = getStyle(AREA_RECODE);
        return (AreaSymbolizer) s.getRules().get(0).getCompositeSymbolizer().getChildren().get(0);
    }

    private RecodedArea getRecodedArea() throws Exception {
        return new RecodedArea(getAreaSymbolizer());
    }
}
