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
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.analyzer.symbolizers.PointSymbolizerAnalyzer;
import org.orbisgis.legend.thematic.recode.RecodedPoint;

import java.awt.*;

import static org.junit.Assert.*;

/**
 * @author alexis
 */
public class RecodedPointTest extends AnalyzerTest {

    @Test
    public void testDefaultConstructor() throws Exception {
        RecodedPoint rp = new RecodedPoint();
        assertNotNull(rp);
    }
    
    @Test
    public void testInstanciation() throws Exception{
        PointSymbolizer as = getPointSymbolizer();
        PointSymbolizerAnalyzer asa = new PointSymbolizerAnalyzer(as);
        RecodedPoint ra = (RecodedPoint) asa.getLegend();
        assertTrue(true);
    }      

    @Test
    public void testGetParams() throws Exception {
        RecodedPoint ra = getRecodedPoint();
        assertTrue(ra.size() == 6);
        assertNull(ra.get("potato"));
        PointParameters ap = new PointParameters(new Color(34,51,68), 1.0, 1.0, "", new Color(34,51,68), 0.9, 2.0, 3.0, "SQUARE");
        PointParameters ret = ra.get("1");
        assertTrue(ret.equals(ap));
    }

    @Test
    public void testGetParamFallbackVal() throws Exception {
        RecodedPoint ra = getRecodedPoint();
        PointParameters ap = new PointParameters(new Color(51,85, 103), 1.0, 0.4, "", new Color(51,85, 102), 0.9, 5.0, 8.0, "STAR");
        PointParameters ret = ra.get("10");
        assertTrue(ret.equals(ap));
        ap = new PointParameters(new Color(170,23,180), 1.0, 0.4, "", new Color(51,85,102), .5, 5.0, 4.0, "CIRCLE");
        ret = ra.get("25");
        assertTrue(ret.equals(ap));
    }

    @Test
    public void testGetFallback() throws Exception {
        RecodedPoint ra = getRecodedPoint();
        PointParameters fb = ra.getFallbackParameters();
        PointParameters t = new PointParameters(new Color(51, 85, 103),1.0,0.4,"",new Color(51,85,102),.9, 5.0, 4.0, "CIRCLE");
        assertTrue(t.equals(fb));
    }

    @Test
    public void testPutNonExistingKey() throws Exception {
        RecodedPoint ra = getRecodedPoint();
        assertTrue(ra.size()==6);
        PointParameters ap = new PointParameters(new Color(88,74, 235), 1.0, 0.4, "2", new Color(0xEC,0x44, 5), 0.9, 5.4, 4.4, "SQUARE");
        String newKey = "I am new !";
        PointParameters ret = ra.put(newKey, ap);
        assertNull(ret);
        assertTrue(ra.get(newKey).equals(new PointParameters(new Color(88,74, 235), 1.0, 0.4, "2", new Color(0xEC,0x44, 5), 0.9, 5.4, 4.4, "SQUARE")));
        assertTrue(ra.size() == 7);
    }

    @Test
    public void testPutExistingKey() throws Exception {
        RecodedPoint ra = getRecodedPoint();
        assertTrue(ra.size()==6);
        PointParameters expected = new PointParameters(new Color(88,174, 35), 1.0, 0.4, "", new Color(88,172, 35), 0.9, 5.0, 4.0, "CIRCLE");
        PointParameters ap = new PointParameters(new Color(88,74, 235), 1.0, 0.4, "2", new Color(0xEC,0x44, 5), 0.9, 5.4, 4.4, "SQUARE");
        PointParameters ret = ra.put("9999",ap);
        assertTrue(ret.equals(expected));
        assertTrue(ra.get("9999").equals(new PointParameters(new Color(88, 74, 235), 1.0, 0.4, "2", new Color(0xEC, 0x44, 5), 0.9, 5.4, 4.4, "SQUARE")));
        assertTrue(ra.size()==6);
    }

    @Test
    public void testPutNull() throws Exception{
        RecodedPoint ra = getRecodedPoint();
        try{
            ra.put(null, new PointParameters());
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

    @Test
    public void testRemove() throws Exception{
        RecodedPoint ra = getRecodedPoint();
        assertTrue(ra.size() == 6);
        PointParameters expected = new PointParameters(new Color(34,51,68), 1.0, 1.0, "", new Color(34,51,68), 0.9, 2.0, 3.0, "SQUARE");
        PointParameters ap = ra.remove("1");
        assertTrue(ap.equals(expected));
        assertTrue(ra.size()==5);
    }

    @Test
    public void testRemoveNull() throws Exception {
        RecodedPoint ra = getRecodedPoint();
        try{
            ra.remove(null);
            fail();
        } catch (NullPointerException npe){
        }
        assertTrue(true);
    } 

    @Test
    public void testSetFallback() throws Exception {
        RecodedPoint ra = getRecodedPoint();
        PointParameters ap = new PointParameters();
        ra.setFallbackParameters(ap);
        assertTrue(ra.getFallbackParameters().equals(new PointParameters()));
    }

    @Test
    public void testRemoveNotExisting() throws Exception {
        RecodedPoint ra = getRecodedPoint();
        assertNull(ra.remove("I do not exist :-("));
    }

    @Test
    public void testNullStroke() throws Exception {
        RecodedPoint ra = getNullStrokeRecodedPoint();
        assertFalse(ra.isStrokeEnabled());
    } 

    @Test
    public void testSetEnabled() throws Exception {
        PointSymbolizer as = getPointSymbolizer();
        MarkGraphic mg = (MarkGraphic) as.getGraphicCollection().getChildren().get(0);
        mg.setStroke(null);
        RecodedPoint ra = new RecodedPoint(as);
        ra.setStrokeEnabled(true);
        assertNotNull(mg.getStroke());
        assertTrue(ra.isStrokeEnabled());
        assertTrue(ra.getFallbackParameters().getLineColor().equals(Color.BLACK));
        assertTrue(ra.getFallbackParameters().getLineWidth().equals(PenStroke.DEFAULT_WIDTH));
        assertTrue(ra.getFallbackParameters().getLineDash().isEmpty());
        assertTrue(ra.getFallbackParameters().getLineOpacity().equals(1.0));
        ra.setStrokeEnabled(false);
        assertFalse(ra.isStrokeEnabled());
        assertTrue(ra.getFallbackParameters().getLineColor().equals(Color.WHITE));
        assertTrue(ra.getFallbackParameters().getLineWidth().equals(0.0));
        assertTrue(ra.getFallbackParameters().getLineDash().isEmpty());
        assertTrue(ra.getFallbackParameters().getLineOpacity().equals(0.0));
    }

    private PointSymbolizer getPointSymbolizer() throws Exception{
        Style s = getStyle(POINT_RECODE);
        return (PointSymbolizer) s.getRules().get(0).getCompositeSymbolizer().getChildren().get(0);
    }

    private RecodedPoint getRecodedPoint() throws Exception {
        return new RecodedPoint(getPointSymbolizer());
    }

    private RecodedPoint getNullStrokeRecodedPoint() throws Exception {
        PointSymbolizer ps = getPointSymbolizer();
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getChildren().get(0);
        mg.setStroke(null);
        return new RecodedPoint(ps);
    }
}
