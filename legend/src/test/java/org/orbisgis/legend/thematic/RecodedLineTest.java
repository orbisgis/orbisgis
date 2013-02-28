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
import org.orbisgis.legend.structure.recode.RecodedLegend;
import org.orbisgis.legend.structure.recode.RecodedParameterVisitor;
import org.orbisgis.legend.thematic.recode.RecodedLine;

import java.awt.*;
import java.util.*;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author alexis
 */
public class RecodedLineTest extends AnalyzerTest {

    @Test
    public void testGetFillOpacity() throws Exception{
        RecodedLine rl = getRecodedLine();
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

    @Test
    public void testGetRecodedLegends() throws Exception {
        RecodedLine rl = getRecodedLine();
        List<RecodedLegend>  legs= rl.getRecodedLegends();
        assertTrue(legs.size() == 4);
        assertTrue(legs.contains(rl.getLineWidth()));
        assertTrue(legs.contains(rl.getLineOpacity()));
        assertTrue(legs.contains(rl.getLineDash()));
        assertTrue(legs.contains(rl.getLineColor()));
    }

    @Test
    public void testSetFieldGlobally() throws Exception {
        RecodedLine rl = getRecodedLine();
        String field = "chewbidouah";
        rl.setAnalysisField(field);
        List<RecodedLegend> legs= rl.getRecodedLegends();
        for(RecodedLegend rec : legs){
            assertTrue(rec.field().equals(field));
        }

    }

    @Test
    public void testGetKeys() throws Exception {
        RecodedLine rl = getRecodedLine();
        Set<String> keys = rl.keySet();
        assertTrue(rl.size() == 4);
        assertTrue(keys.contains("1"));
        assertTrue(keys.contains("2.5"));
        assertTrue(keys.contains("20"));
        assertTrue(keys.contains("9999"));
    }

    @Test
    public void testGet() throws Exception {
        RecodedLine rl = getRecodedLine();
        LineParameters lp = rl.get("1");
        LineParameters t = new LineParameters(new Color(0x22,0x33,0x44),1.0,.5,"");
        assertTrue(lp.equals(t));
        lp = rl.get("2.5");
        t = new LineParameters(new Color(0xAA,0x17,0xB4),1.0,.5,"");
        assertTrue(lp.equals(t));
        lp = rl.get("bonjour");
        t = new LineParameters(new Color(0x33,0x55,0x66),1.0,.5,"");
        assertTrue(lp.equals(t));
    }

    @Test
    public void testPut() throws  Exception {
        RecodedLine rl = getRecodedLine();
        assertNull(rl.put("zen",new LineParameters(new Color(130, 180, 113),1.0,.5,"")));
        LineParameters lp = new LineParameters(new Color(130, 180, 113),1.0,.5,"");
        assertTrue(lp.equals(rl.get("zen")));
        LineParameters ret = rl.put("zen", new LineParameters(new Color(140, 140, 140), 1.0, .5, ""));
        assertTrue(ret.equals(lp));
    }

    @Test
    public void testPutNPE() throws Exception {
        RecodedLine rl = getRecodedLine();
        try{
            rl.put(null,new LineParameters(new Color(130, 180, 113),1.0,.5,""));
            fail();
        } catch (NullPointerException npe){
            assertTrue(true);
        }
        try{
            rl.put("patate",null);
            fail();
        } catch (NullPointerException npe){
            assertTrue(true);
        }
        try{
            rl.put(null,null);
            fail();
        } catch (NullPointerException npe){
            assertTrue(true);
        }
    }

    @Test
    public void testRemove() throws Exception {
        RecodedLine rl = getRecodedLine();
        LineParameters rem = rl.remove("1");
        assertTrue(rem.equals(new LineParameters(new Color(0x22, 0x33, 0x44), 1.0, .5, "")));
    }

    @Test
    public void testRemoveNPE() throws Exception {
        RecodedLine rl = getRecodedLine();
        try{
            rl.remove(null);
            fail();
        } catch (NullPointerException npe){
            assertTrue(true);
        }

    }

    @Test
    public void testGlobalFieldSetConstructor() throws Exception {
        RecodedLine rl = getRecodedLine();
        ValidateFieldNotNull vfnn = new ValidateFieldNotNull();
        rl.applyGlobalVisitor(vfnn);
        assertTrue(true);
    }

    @Test
    public void testGetAnalysisField() throws Exception {
        RecodedLine rl = getRecodedLine();
        assertTrue(rl.getAnalysisField().equals("PREC_ALTI"));
    }

    @Test
    public void testClear() throws Exception {
        RecodedLine rl = getRecodedLine();
        rl.clear();
        assertTrue(rl.keySet().isEmpty());
        assertTrue(rl.isEmpty());
    }

    @Test
    public void testIsEmpty() throws Exception {
        RecodedLine rl = new RecodedLine(new LineSymbolizer());
        assertTrue(rl.isEmpty());
        rl.clear();
        assertTrue(rl.isEmpty());
    }

    @Test
    public void testContainsKey() throws  Exception{
        RecodedLine rl = getRecodedLine();
        assertTrue(rl.containsKey("2.5"));
        assertFalse(rl.containsKey("2.50"));
        assertFalse(rl.containsKey("patate"));
        try{
            assertFalse(rl.containsKey((Double) 2.0));
            fail();
        } catch(ClassCastException cce){
            assertTrue(true);
        }
    }

    @Test
    public void testContainValue() throws Exception{
        RecodedLine rl = getRecodedLine();
        LineParameters lps = new LineParameters(new Color(34, 51, 68),1.0,0.5,"");
        assertTrue(rl.containsValue(lps));
        lps = new LineParameters(new Color(204, 0, 153),1.0,0.5,"");
        assertTrue(rl.containsValue(lps));
        lps = new LineParameters(new Color(204, 0, 153),1.0,2.5,"");
        assertFalse(rl.containsValue(lps));

    }

    @Test
    public void testEquals() throws Exception {
        RecodedLine rl = getRecodedLine();
        RecodedLine rl2 = getRecodedLine();
        assertTrue(rl.equals(rl2));
        rl2.put("patate", new LineParameters(new Color(204, 0, 153),1.0,0.5,"2"));
        assertFalse(rl.equals(rl2));
    }

    @Test
    public void testHashCode() throws Exception {
        RecodedLine rl = getRecodedLine();
        RecodedLine rl2 = getRecodedLine();
        assertTrue(rl.hashCode() == rl2.hashCode());
        rl2.put("patate", new LineParameters(new Color(204, 0, 153), 1.0, 0.5, "2"));
        assertFalse(rl.hashCode() == rl2.hashCode());
    }

    @Test
    public void testValues() throws Exception {
        RecodedLine rl = getRecodedLine();
        Collection<LineParameters> vals = rl.values();
        assertTrue(vals.size() == 4);
        assertTrue(vals.contains(new LineParameters(new Color(88, 174, 35),1.0,0.5,"")));
        assertTrue(vals.contains(new LineParameters(new Color(204, 0, 153),1.0,0.5,"")));
        assertTrue(vals.contains(new LineParameters(new Color(170, 23, 180),1.0,0.5,"")));
        assertTrue(vals.contains(new LineParameters(new Color(34, 51, 68),1.0,0.5,"")));
        assertFalse(vals.contains(new LineParameters(new Color(34, 51, 68),1.0,0.5,"20")));
    }

    @Test
    public void testMapEntrySetValue() throws Exception {
        RecodedLine rl = getRecodedLine();
        Set<Map.Entry<String, LineParameters>> entries = rl.entrySet();
        Iterator<Map.Entry<String, LineParameters>> it = entries.iterator();
        Map.Entry<String, LineParameters> me = it.next();
        String key = me.getKey();
        me.setValue(new LineParameters(new Color(124,15,64), 2.0,1.4,"2"));
        assertTrue(rl.get(key).equals(new LineParameters(new Color(124,15,64), 2.0,1.4,"2")));

    }

    private LineSymbolizer getLineSymbolizer() throws Exception{
        Style s = getStyle(COLOR_RECODE);
        return (LineSymbolizer) s.getRules().get(0).getCompositeSymbolizer().getChildren().get(0);
    }

    private RecodedLine getRecodedLine() throws Exception {
        return new RecodedLine(getLineSymbolizer());
    }

    private static class ValidateFieldNotNull implements RecodedParameterVisitor{

        @Override
        public void visit(RecodedLegend legend) {
            assertTrue(legend.field() != null);
        }
    }
}
