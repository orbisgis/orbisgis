package org.orbisgis.legend.thematic.categorize;

import org.junit.Test;
import org.orbisgis.core.renderer.se.AreaSymbolizer;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.Categorize;
import org.orbisgis.core.renderer.se.stroke.*;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.thematic.AreaParameters;
import org.orbisgis.legend.thematic.LineParameters;

import java.awt.*;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Alexis Guéganno
 */
public class CategorizedAreaTest extends AnalyzerTest {

    @Test
    public void testInstanciation() throws Exception {
        AreaSymbolizer as = getAreaSymbolizer();
        CategorizedArea categorizedArea = new CategorizedArea(as);
        assertTrue(categorizedArea.getSymbolizer() == as);
    }

    @Test
    public void testBadInstanciation() throws Exception {
        Style s = getStyle(AREA_RECODE);
        AreaSymbolizer as = (AreaSymbolizer) s.getRules().get(0).getCompositeSymbolizer().getChildren().get(0);
        try{
            CategorizedArea ca = new CategorizedArea(as);
            fail();
        } catch (IllegalArgumentException iae){
            assertTrue(true);
        }
    }

    @Test
    public void testGetFallback() throws Exception {
        CategorizedArea ca = getCategorizedArea();
        AreaParameters ap = new AreaParameters(Color.decode("#111111"),.2,1.0,"1 1",Color.decode("#111111"),.5);
        assertTrue(ca.getFallbackParameters().equals(ap));
    }

    @Test
    public void testSetFallback() throws Exception {
        CategorizedArea ca = getCategorizedArea();
        AreaParameters ap1 = new AreaParameters(Color.decode("#211111"),.4,22.0,"21 1",Color.decode("#211111"),.4);
        AreaParameters ap2 = new AreaParameters(Color.decode("#211111"),.4,22.0,"21 1",Color.decode("#211111"),.4);
        ca.setFallbackParameters(ap1);
        assertTrue(ca.getFallbackParameters().equals(ap2));
    }

    @Test
    public void testGet() throws Exception {
        CategorizedArea ca = getCategorizedArea();
        AreaParameters tester = new AreaParameters(Color.decode("#223344"),.2,2.0,"1 2 1",Color.decode("#113355"),.5);
        assertTrue(ca.get(Double.NEGATIVE_INFINITY).equals(tester));
        tester = new AreaParameters(Color.decode("#dd77ee"),.2,2.0,"1 3 1 2 5",Color.decode("#dd66ee"),.5);
        assertTrue(ca.get(70000.0).equals(tester));
        tester = new AreaParameters(Color.decode("#dd77ee"),.2,7.0,"1 6 8 1",Color.decode("#dd66ee"),.5);
        assertTrue(ca.get(75000.0).equals(tester));
        tester = new AreaParameters(Color.decode("#dd77ee"),.2,10.0,"5 5",Color.decode("#ffaa99"),.5);
        assertTrue(ca.get(100000.0).equals(tester));
        tester = new AreaParameters(Color.decode("#ffaa00"),.2,10.0,"5 5",Color.decode("#ffaa99"),.5);
        assertTrue(ca.get(110000.0).equals(tester));
    }

    @Test
    public void testRemove() throws Exception {
        CategorizedArea ca = getCategorizedArea();
        AreaParameters tester = ca.remove(75000.0);
        assertTrue(tester.equals(new AreaParameters(Color.decode("#dd77ee"),.2,7.0,"1 6 8 1",Color.decode("#dd66ee"),.5)));
        tester = new AreaParameters(Color.decode("#dd77ee"),.2,2.0,"1 3 1 2 5",Color.decode("#dd66ee"),.5);
        assertTrue(ca.get(70000.0).equals(tester));
        tester = new AreaParameters(Color.decode("#dd77ee"),.2,10.0,"5 5",Color.decode("#ffaa99"),.5);
        assertTrue(ca.get(100000.0).equals(tester));
        assertFalse(ca.containsKey(75000.0));
    }

    @Test
    public void testRemoveInf() throws Exception {
        CategorizedArea ca = getCategorizedArea();
        AreaParameters tester = ca.remove(Double.NEGATIVE_INFINITY);
        assertTrue(tester.equals(new AreaParameters(Color.decode("#223344"),.2,2.0,"1 2 1",Color.decode("#113355"),.5)));
        tester = new AreaParameters(Color.decode("#dd77ee"),.2,2.0,"1 3 1 2 5",Color.decode("#dd66ee"),.5);
        assertTrue(ca.get(Double.NEGATIVE_INFINITY).equals(tester));
        tester = new AreaParameters(Color.decode("#dd77ee"),.2,7.0,"1 6 8 1",Color.decode("#dd66ee"),.5);
        assertTrue(ca.get(75000.0).equals(tester));
        assertFalse(ca.containsKey(70000.0));
        assertTrue(ca.containsKey(Double.NEGATIVE_INFINITY));
    }

    @Test
    public void testPutExisting() throws Exception {
        CategorizedArea ca = getCategorizedArea();
        AreaParameters tester = new AreaParameters(Color.decode("#ababab"),1.2,12.0,"11 2 1",Color.decode("#bcbcbc"),1.5);
        ca.put(75000.0, new AreaParameters(Color.decode("#ababab"),1.2,12.0,"11 2 1",Color.decode("#bcbcbc"),1.5));
        assertTrue(ca.get(75000.0).equals(tester));
        tester = new AreaParameters(Color.decode("#dd77ee"),.2,2.0,"1 3 1 2 5",Color.decode("#dd66ee"),.5);
        assertTrue(ca.get(70000.0).equals(tester));
        tester = new AreaParameters(Color.decode("#dd77ee"),.2,10.0,"5 5",Color.decode("#ffaa99"),.5);
        assertTrue(ca.get(100000.0).equals(tester));
    }

    @Test
    public void testPutNotExisting() throws Exception {
        CategorizedArea ca = getCategorizedArea();
        AreaParameters tester = new AreaParameters(Color.decode("#ababab"),1.2,12.0,"11 2 1",Color.decode("#bcbcbc"),1.5);
        ca.put(76000.0, new AreaParameters(Color.decode("#ababab"),1.2,12.0,"11 2 1",Color.decode("#bcbcbc"),1.5));
        assertTrue(ca.get(76000.0).equals(tester));
        tester = new AreaParameters(Color.decode("#dd77ee"),.2,7.0,"1 6 8 1",Color.decode("#dd66ee"),.5);
        assertTrue(ca.get(75000.0).equals(tester));
        tester = new AreaParameters(Color.decode("#dd77ee"),.2,10.0,"5 5",Color.decode("#ffaa99"),.5);
        assertTrue(ca.get(100000.0).equals(tester));
    }

    @Test
    public void testPutInf() throws Exception {
        CategorizedArea ca = getCategorizedArea();
        AreaParameters tester = new AreaParameters(Color.decode("#ababab"),1.2,12.0,"11 2 1",Color.decode("#bcbcbc"),1.5);
        ca.put(Double.NEGATIVE_INFINITY, new AreaParameters(Color.decode("#ababab"),1.2,12.0,"11 2 1",Color.decode("#bcbcbc"),1.5));
        assertTrue(ca.get(Double.NEGATIVE_INFINITY).equals(tester));
        tester = new AreaParameters(Color.decode("#dd77ee"),.2,2.0,"1 3 1 2 5",Color.decode("#dd66ee"),.5);
        assertTrue(ca.get(70000.0).equals(tester));
    }

    @Test
    public void testInstanciationNoStroke() throws Exception {
        Style s = getStyle(CATEGORIZED_AREA_NO_STROKE);
        AreaSymbolizer as = (AreaSymbolizer) s.getRules().get(0).getCompositeSymbolizer().getChildren().get(0);
        CategorizedArea ca = new CategorizedArea(as);
        assertFalse(ca.isStrokeEnabled());
    }

    @Test
    public void testGetNoStroke() throws Exception {
        CategorizedArea ca = getNoStroke();
        AreaParameters tester = new AreaParameters(Color.WHITE,.0,.0,"",Color.decode("#113355"),.5);
        assertTrue(ca.get(Double.NEGATIVE_INFINITY).equals(tester));
    }

    @Test
    public void testPutNoStroke() throws Exception {
        CategorizedArea ca = getNoStroke();
        ca.put(12.0,new AreaParameters(Color.BLACK, 1.0 ,1.0, "1", Color.decode("#252525"),2.0 ));
        AreaParameters tester = new AreaParameters(Color.WHITE,.0,.0,"",Color.decode("#113355"),.5);
        assertTrue(ca.get(Double.NEGATIVE_INFINITY).equals(tester));
        tester = new AreaParameters(Color.WHITE,.0,.0,"",Color.decode("#252525"),2.0);
        assertTrue(ca.get(12.0).equals(tester));
    }

    @Test
    public void testRemoveNoStroke() throws Exception {
        CategorizedArea ca = getNoStroke();
        AreaParameters rm = ca.remove(100000.0);
        AreaParameters tester = new AreaParameters(Color.WHITE,.0,.0,"",Color.decode("#ffaa99"),.5);
        assertTrue(rm.equals(tester));
    }

    @Test
    public void testDisableStroke() throws Exception {
        CategorizedArea ca = getCategorizedArea();
        ca.setStrokeEnabled(false);
        AreaSymbolizer as = (AreaSymbolizer) ca.getSymbolizer();
        assertNull(as.getStroke());
        assertFalse(ca.isStrokeEnabled());
    }

    @Test
    public void testEnableStroke() throws Exception {
        CategorizedArea ca = getNoStroke();
        ca.setStrokeEnabled(true);
        assertTrue(ca.isStrokeEnabled());
        PenStroke ps = new PenStroke();
        AreaParameters ap = new AreaParameters(
                ((SolidFill)ps.getFill()).getColor().getColor(null),
                ((SolidFill)ps.getFill()).getOpacity().getValue(null),
                ps.getWidth().getValue(null),
                ps.getDashArray().getValue(null),
                Color.decode("#113355"),
                .5);
        assertTrue(ap.equals(ca.get(Double.NEGATIVE_INFINITY)));
    }

    @Test
    public void testTransformToCategorize() throws Exception {
        AreaSymbolizer as = new AreaSymbolizer();
        CategorizedArea ca = new CategorizedArea(as);
        ca.put(76000.0, new AreaParameters(Color.decode("#ababab"),1.2,12.0,"11 2 1",Color.decode("#bcbcbc"),1.5));
        SolidFill sfi = (SolidFill) as.getFill();
        PenStroke ps = (PenStroke) as.getStroke();
        SolidFill sfp = (SolidFill) ps.getFill();
        assertTrue(sfi.getColor() instanceof Categorize);
        assertTrue(sfi.getOpacity() instanceof Categorize);
        assertTrue(sfp.getColor() instanceof Categorize);
        assertTrue(sfp.getOpacity() instanceof Categorize);
        assertTrue(ps.getDashArray() instanceof Categorize);
        assertTrue(ps.getWidth() instanceof Categorize);
    }

    private AreaSymbolizer getAreaSymbolizer() throws Exception {
        Style s = getStyle(CATEGORIZED_AREA);
        return (AreaSymbolizer) s.getRules().get(0).getCompositeSymbolizer().getChildren().get(0);
    }

    private CategorizedArea getCategorizedArea() throws Exception {
        return new CategorizedArea(getAreaSymbolizer());
    }

    private CategorizedArea getNoStroke() throws Exception {
        Style s = getStyle(CATEGORIZED_AREA_NO_STROKE);
        AreaSymbolizer as = (AreaSymbolizer) s.getRules().get(0).getCompositeSymbolizer().getChildren().get(0);
        return new CategorizedArea(as);
    }

}
