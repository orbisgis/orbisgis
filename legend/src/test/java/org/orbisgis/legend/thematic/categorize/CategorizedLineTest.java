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
package org.orbisgis.legend.thematic.categorize;

import junit.framework.Assert;
import org.junit.Test;
import org.orbisgis.coremap.renderer.se.LineSymbolizer;
import org.orbisgis.coremap.renderer.se.Style;
import org.orbisgis.coremap.renderer.se.fill.SolidFill;
import org.orbisgis.coremap.renderer.se.parameter.Categorize;
import org.orbisgis.coremap.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.thematic.LineParameters;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Alexis Guéganno
 */
public class CategorizedLineTest extends AnalyzerTest{

    @Test
    public void testInstanciation() throws Exception{
        CategorizedLine cl = new CategorizedLine(getLineSymbolizer());
        assertTrue(true);
    }

    @Test
    public void testImpossibleInstanciation() throws Exception {
        Style s = getStyle(DASH_RECODE);
        LineSymbolizer ls = (LineSymbolizer) s.getRules().get(0).getCompositeSymbolizer().getChildren().get(0);
        try{
            CategorizedLine cl = new CategorizedLine(ls);
            fail();
        } catch (IllegalArgumentException uoe){
            assertTrue(true);
        }

    }

    @Test
    public void testGetFallback() throws Exception {
        CategorizedLine ca = getCategorizedLine();
        LineParameters ap = new LineParameters(Color.decode("#111111"),.75,1.0,"2 2");
        assertTrue(ca.getFallbackParameters().equals(ap));
    }

    @Test
    public void testSetFallback() throws Exception {
        CategorizedLine ca = getCategorizedLine();
        LineParameters ap1 = new LineParameters(Color.decode("#211111"),2.2,22.0,"21 1");
        LineParameters ap2 = new LineParameters(Color.decode("#211111"),2.2,22.0,"21 1");
        ca.setFallbackParameters(ap1);
        assertTrue(ca.getFallbackParameters().equals(ap2));
    }

    @Test
    public void testGet() throws Exception {
        CategorizedLine cl = getCategorizedLine();
        assertTrue(cl.get(Double.NEGATIVE_INFINITY).equals(new LineParameters(Color.decode("#113355"),.75,.5,"2 2")));
        assertTrue(cl.get(70000.0 ).equals(new LineParameters(Color.decode("#dd66ee"),.75,1.0 ,"2 2")));
        assertTrue(cl.get(80000.0 ).equals(new LineParameters(Color.decode("#dd66ee"),.75,1.25,"2 2")));
        assertTrue(cl.get(100000.0).equals(new LineParameters(Color.decode("#ffaa99"),.75,1.5 ,"2 2")));
    }

    @Test
    public void testKeySet() throws Exception {
        CategorizedLine cl = getCategorizedLine();
        SortedSet<Double> doubles = cl.keySet();
        assertTrue(doubles.contains(Double.NEGATIVE_INFINITY));
        assertTrue(doubles.contains(70000.0));
        assertTrue(doubles.contains(80000.0));
        assertTrue(doubles.contains(100000.0));
    }

    @Test
    public void testContainsValue() throws Exception {
        CategorizedLine cl = getCategorizedLine();
        assertTrue(cl.containsValue(new LineParameters(Color.decode("#113355"),.75,  .5,"2 2")));
        assertTrue(cl.containsValue(new LineParameters(Color.decode("#dd66ee"),.75, 1.0,"2 2")));
        assertTrue(cl.containsValue(new LineParameters(Color.decode("#dd66ee"),.75,1.25,"2 2")));
        assertTrue(cl.containsValue(new LineParameters(Color.decode("#ffaa99"),.75, 1.5,"2 2")));
    }

    @Test
    public void testContainsKey() throws Exception {
        CategorizedLine cl = getCategorizedLine();
        assertTrue(cl.containsKey(Double.NEGATIVE_INFINITY));
        assertTrue(cl.containsKey( 70000.0));
        assertTrue(cl.containsKey( 80000.0));
        assertTrue(cl.containsKey(100000.0));
    }

    @Test
    public void testPutNotIn() throws Exception {
        CategorizedLine cl = getCategorizedLine();
        assertNull(cl.put(25.0, new LineParameters(Color.decode("#dd6643"), .725, 10.0, "2 2 5")));
        assertTrue(cl.get(25.0).equals(new LineParameters(Color.decode("#dd6643"),.725, 10.0,"2 2 5")));
    }

    @Test
    public void testPutAlreadyIn() throws Exception {
        CategorizedLine cl = getCategorizedLine();
        LineParameters lp = cl.put(70000.0,new LineParameters(Color.decode("#dd6643"),.725, 10.0,"2 2 5"));
        assertTrue(lp.equals(new LineParameters(Color.decode("#dd66ee"),.75,1.0 ,"2 2")));
        assertTrue(cl.get(70000.0).equals(new LineParameters(Color.decode("#dd6643"),.725, 10.0,"2 2 5")));
        assertTrue(cl.get(80000.0 ).equals(new LineParameters(Color.decode("#dd66ee"),.75,1.25,"2 2")));
        assertTrue(cl.get(100000.0).equals(new LineParameters(Color.decode("#ffaa99"),.75,1.5 ,"2 2")));
        lp = cl.put(Double.NEGATIVE_INFINITY,new LineParameters(Color.decode("#ad6643"),.225, 20.0,"2 2 6"));
        assertTrue(lp.equals(new LineParameters(Color.decode("#113355"),.75,.5,"2 2")));
        assertTrue(cl.get(Double.NEGATIVE_INFINITY).equals(new LineParameters(Color.decode("#ad6643"),.225, 20.0,"2 2 6")));
    }

    @Test
    public void testGetNextThreshold() throws Exception {
        CategorizedLine cl = getCategorizedLine();
        assertTrue(cl.getNextThreshold(50000.0).equals(70000.0));
        assertTrue(cl.getNextThreshold(70000.0).equals(80000.0));
        assertTrue(cl.getNextThreshold(75000.0).equals(80000.0));
        assertTrue(cl.getNextThreshold(100000.0).equals(Double.POSITIVE_INFINITY));
        assertTrue(cl.getNextThreshold(150000.0).equals(Double.POSITIVE_INFINITY));
    }


    @Test
    public void testRemove() throws Exception {
        CategorizedLine cl = getCategorizedLine();
        LineParameters lp = cl.remove(70000.0);
        assertTrue(lp.equals(new LineParameters(Color.decode("#dd66ee"),.75,1.0 ,"2 2")));
        assertTrue(cl.get(Double.NEGATIVE_INFINITY).equals(new LineParameters(Color.decode("#113355"),.75,.5,"2 2")));
        assertTrue(cl.get(80000.0 ).equals(new LineParameters(Color.decode("#dd66ee"),.75,1.25,"2 2")));
        assertTrue(cl.get(100000.0).equals(new LineParameters(Color.decode("#ffaa99"),.75,1.5 ,"2 2")));
    }


    @Test
    public void testRemoveInf() throws Exception {
        CategorizedLine cl = getCategorizedLine();
        cl.remove(Double.NEGATIVE_INFINITY);
        assertTrue(cl.get(Double.NEGATIVE_INFINITY).equals(new LineParameters(Color.decode("#dd66ee"),.75,1.0 ,"2 2")));
        assertTrue(cl.get(80000.0 ).equals(new LineParameters(Color.decode("#dd66ee"),.75,1.25,"2 2")));
        assertTrue(cl.get(100000.0).equals(new LineParameters(Color.decode("#ffaa99"),.75,1.5 ,"2 2")));
    }


    @Test
    public void testRemoveInfAlone() throws Exception {
        CategorizedLine cl = getCategorizedLine();
        cl.remove(Double.NEGATIVE_INFINITY);
        cl.remove(Double.NEGATIVE_INFINITY);
        cl.remove(Double.NEGATIVE_INFINITY);
        assertTrue(cl.get(Double.NEGATIVE_INFINITY).equals(new LineParameters(Color.decode("#ffaa99"),.75,1.5 ,"2 2")));
        assertTrue(cl.size()==1);
        cl.remove(Double.NEGATIVE_INFINITY);
        assertTrue(cl.get(Double.NEGATIVE_INFINITY).equals(new LineParameters(Color.decode("#ffaa99"),.75,1.5 ,"2 2")));
        assertTrue(cl.size()==1);
    }

    @Test
    public void testPutAll() throws Exception {
        CategorizedLine cl = getCategorizedLine();
        Map<Double, LineParameters> in = new HashMap<Double, LineParameters>();
        in.put(70000.0,new LineParameters(Color.decode("#dd6643"),.725, 10.0,"2 2 5"));
        in.put(25.0, new LineParameters(Color.decode("#dd6643"), .725, 10.0, "2 2 5"));
        in.put(Double.NEGATIVE_INFINITY,new LineParameters(Color.decode("#ad6643"),.225, 20.0,"2 2 6"));
        cl.putAll(in);
        assertTrue(cl.get(25.0).equals(new LineParameters(Color.decode("#dd6643"), .725, 10.0, "2 2 5")));
        assertTrue(cl.get(70000.0).equals(new LineParameters(Color.decode("#dd6643"),.725, 10.0,"2 2 5")));
        assertTrue(cl.get(Double.NEGATIVE_INFINITY).equals(new LineParameters(Color.decode("#ad6643"),.225, 20.0,"2 2 6")));
    }

    @Test
    public void testTransformToCategorize() throws Exception {
        LineSymbolizer ls = new LineSymbolizer();
        CategorizedLine cl = new CategorizedLine(ls);
        cl.put(25.0, new LineParameters(Color.decode("#dd6643"), .725, 10.0, "2 2 5"));
        PenStroke ps = (PenStroke) ls.getStroke();
        assertTrue(ps.getWidth() instanceof Categorize);
        assertTrue(ps.getDashArray() instanceof Categorize);
        assertTrue(((SolidFill)ps.getFill()).getColor() instanceof Categorize);
        assertTrue(((SolidFill)ps.getFill()).getOpacity() instanceof Categorize);
    }

    private LineSymbolizer getLineSymbolizer() throws Exception {
        Style s = getStyle(CATEGORIZED_LINE);
        return (LineSymbolizer) s.getRules().get(0).getCompositeSymbolizer().getChildren().get(0);
    }

    private CategorizedLine getCategorizedLine() throws Exception {
        return new CategorizedLine(getLineSymbolizer());
    }
}
