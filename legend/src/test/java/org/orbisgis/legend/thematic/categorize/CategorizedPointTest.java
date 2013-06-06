package org.orbisgis.legend.thematic.categorize;

import org.junit.Test;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.graphic.ViewBox;
import org.orbisgis.core.renderer.se.parameter.Categorize;
import org.orbisgis.core.renderer.se.parameter.Literal;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.thematic.PointParameters;
import org.orbisgis.legend.thematic.categorize.CategorizedPoint;

import java.awt.*;

import static org.junit.Assert.*;

/**
 * @author Alexis Guéganno
 */
public class CategorizedPointTest extends AnalyzerTest {

    @Test
    public void testInstanciation() throws Exception {
        PointSymbolizer ps = getPointSymbolizer();
        CategorizedPoint cp = new CategorizedPoint(ps);
        assertTrue(cp.getSymbolizer().equals(ps));
    }

    @Test
    public void testBadInstanciation() throws Exception {
        Style s = getStyle(PROPORTIONAL_POINT);
        PointSymbolizer ps = (PointSymbolizer) s.getRules().get(0).getCompositeSymbolizer().getChildren().get(0);
        try{
            CategorizedPoint cp = new CategorizedPoint(ps);
            fail();
        }catch (IllegalArgumentException iae){
            assertTrue(true);
        }
    }

    @Test
    public void testGet() throws Exception {
        CategorizedPoint ca = getCategorizedPoint();
        PointParameters tester = new PointParameters(Color.decode("#223344"),.2,2.0,"1 1",Color.decode("#113355"),.5,6.0,5.0,"SQUARE");
        assertTrue(ca.get(Double.NEGATIVE_INFINITY).equals(tester));
        tester = new PointParameters(Color.decode("#dd77ee"),.2,2.0,"1 1",Color.decode("#dd66ee"),.5,7.0,5.0,"STAR");
        assertTrue(ca.get(70000.0).equals(tester));
        tester = new PointParameters(Color.decode("#dd77ee"),.2,2.0,"1 1",Color.decode("#ffaa99"),.5,8.0,5.0,"CROSS");
        assertTrue(ca.get(100000.0).equals(tester));
        tester = new PointParameters(Color.decode("#ffaa00"),.2,2.0,"1 1",Color.decode("#ffaa99"),.5,8.0,5.0,"CROSS");
        assertTrue(ca.get(110000.0).equals(tester));
    }

    @Test
    public void testRemove() throws Exception {
        CategorizedPoint ca = getCategorizedPoint();
        PointParameters tester = new PointParameters(Color.decode("#dd77ee"),.2,2.0,"1 1",Color.decode("#dd66ee"),.5,7.0,5.0,"STAR");
        PointParameters rm = ca.remove(70000.0);
        assertTrue(tester.equals(rm));
        tester = new PointParameters(Color.decode("#223344"),.2,2.0,"1 1",Color.decode("#113355"),.5,6.0,5.0,"SQUARE");
        assertTrue(ca.get(Double.NEGATIVE_INFINITY).equals(tester));
        tester = new PointParameters(Color.decode("#dd77ee"),.2,2.0,"1 1",Color.decode("#ffaa99"),.5,8.0,5.0,"CROSS");
        assertTrue(ca.get(100000.0).equals(tester));
        assertFalse(ca.containsKey(75000.0));
    }

    @Test
    public void testRemoveInf() throws Exception {
        CategorizedPoint ca = getCategorizedPoint();
        PointParameters rm = ca.remove(Double.NEGATIVE_INFINITY);
        PointParameters tester = new PointParameters(Color.decode("#223344"),.2,2.0,"1 1",Color.decode("#113355"),.5,6.0,5.0,"SQUARE");
        assertTrue(tester.equals(rm));
        tester = new PointParameters(Color.decode("#dd77ee"),.2,2.0,"1 1",Color.decode("#dd66ee"),.5,7.0,5.0,"STAR");
        assertTrue(ca.get(Double.NEGATIVE_INFINITY).equals(tester));
        tester = new PointParameters(Color.decode("#dd77ee"),.2,2.0,"1 1",Color.decode("#ffaa99"),.5,8.0,5.0,"CROSS");
        assertTrue(ca.get(100000.0).equals(tester));
        assertFalse(ca.containsKey(70000.0));
        assertTrue(ca.containsKey(Double.NEGATIVE_INFINITY));
    }

    @Test
    public void testPutExisting() throws Exception {
        CategorizedPoint ca = getCategorizedPoint();
        PointParameters tester = new PointParameters(Color.decode("#ababab"),1.2,12.0,"11 1",Color.decode("#bcbcbc"),1.5,17.0,15.0,"X");
        PointParameters testRm = new PointParameters(Color.decode("#dd77ee"),.2,2.0,"1 1",Color.decode("#dd66ee"),.5,7.0,5.0,"STAR");
        PointParameters rm = ca.put(70000.0, new PointParameters(Color.decode("#ababab"),1.2,12.0,"11 1",Color.decode("#bcbcbc"),1.5,17.0,15.0,"X"));
        assertTrue(rm.equals(testRm));
        assertTrue(ca.get(70000.0).equals(tester));
        tester = new PointParameters(Color.decode("#223344"),.2,2.0,"1 1",Color.decode("#113355"),.5,6.0,5.0,"SQUARE");
        assertTrue(ca.get(Double.NEGATIVE_INFINITY).equals(tester));
        tester = new PointParameters(Color.decode("#dd77ee"),.2,2.0,"1 1",Color.decode("#ffaa99"),.5,8.0,5.0,"CROSS");
        assertTrue(ca.get(100000.0).equals(tester));
    }

    @Test
    public void testPutNotExisting() throws Exception {
        CategorizedPoint ca = getCategorizedPoint();
        PointParameters tester = new PointParameters(Color.decode("#ababab"),1.2,12.0,"11 1",Color.decode("#bcbcbc"),1.5,17.0,15.0,"X");
        ca.put(76000.0, new PointParameters(Color.decode("#ababab"), 1.2, 12.0, "11 1", Color.decode("#bcbcbc"), 1.5, 17.0, 15.0, "X"));
        assertTrue(ca.get(76000.0).equals(tester));
        tester = new PointParameters(Color.decode("#dd77ee"),.2,2.0,"1 1",Color.decode("#dd66ee"),.5,7.0,5.0,"STAR");
        assertTrue(ca.get(70000.0).equals(tester));
        tester = new PointParameters(Color.decode("#dd77ee"),.2,2.0,"1 1",Color.decode("#ffaa99"),.5,8.0,5.0,"CROSS");
        assertTrue(ca.get(100000.0).equals(tester));
    }

    @Test
    public void testPutInf() throws Exception {
        CategorizedPoint ca = getCategorizedPoint();
        PointParameters tester = new PointParameters(Color.decode("#ababab"),1.2,12.0,"11 1",Color.decode("#bcbcbc"),1.5,17.0,15.0,"X");
        ca.put(Double.NEGATIVE_INFINITY, new PointParameters(Color.decode("#ababab"),1.2,12.0,"11 1",Color.decode("#bcbcbc"),1.5,17.0,15.0,"X"));
        assertTrue(ca.get(Double.NEGATIVE_INFINITY).equals(tester));
        tester = new PointParameters(Color.decode("#dd77ee"),.2,2.0,"1 1",Color.decode("#dd66ee"),.5,7.0,5.0,"STAR");
        assertTrue(ca.get(70000.0).equals(tester));
    }

    @Test
    public void testInstanciationNoStroke() throws Exception {
        Style s = getStyle(CATEGORIZED_POINT_NO_STROKE);
        PointSymbolizer as = (PointSymbolizer) s.getRules().get(0).getCompositeSymbolizer().getChildren().get(0);
        CategorizedPoint ca = new CategorizedPoint(as);
        assertFalse(ca.isStrokeEnabled());
    }

    @Test
    public void testGetNoStroke() throws Exception {
        CategorizedPoint ca = getNoStroke();
        PointParameters tester = new PointParameters(Color.WHITE,.0,.0,"",Color.decode("#113355"),.5,6.0,5.0,"SQUARE");
        assertTrue(ca.get(Double.NEGATIVE_INFINITY).equals(tester));
    }

    @Test
    public void testPutNoStroke() throws Exception {
        CategorizedPoint ca = getNoStroke();
        ca.put(12.0,new PointParameters(Color.BLACK, 1.0 ,1.0, "1", Color.decode("#252525"),2.0, 17.0,15.0,"X"));
        PointParameters tester = new PointParameters(Color.WHITE,.0,.0,"",Color.decode("#113355"),.5,6.0,5.0,"SQUARE");
        assertTrue(ca.get(Double.NEGATIVE_INFINITY).equals(tester));
        tester = new PointParameters(Color.WHITE,.0,.0,"",Color.decode("#252525"),2.0, 17.0,15.0,"X");
        assertTrue(ca.get(12.0).equals(tester));
    }

    @Test
    public void testRemoveNoStroke() throws Exception {
        CategorizedPoint ca = getNoStroke();
        PointParameters rm = ca.remove(100000.0);
        PointParameters tester = new PointParameters(Color.WHITE,.0,.0,"", Color.decode("#ffaa99"),.5,8.0,5.0,"CROSS");
        assertTrue(rm.equals(tester));
    }

    @Test
    public void testDisableStroke() throws Exception {
        CategorizedPoint ca = getCategorizedPoint();
        ca.setStrokeEnabled(false);
        PointSymbolizer as = (PointSymbolizer) ca.getSymbolizer();
        MarkGraphic pg = (MarkGraphic) as.getGraphicCollection().getGraphic(0);
        assertNull(pg.getStroke());
        assertFalse(ca.isStrokeEnabled());
    }

    @Test
    public void testEnableStroke() throws Exception {
        CategorizedPoint ca = getNoStroke();
        ca.setStrokeEnabled(true);
        assertTrue(ca.isStrokeEnabled());
        PenStroke ps = new PenStroke();
        PointParameters ap = new PointParameters(
                ((SolidFill)ps.getFill()).getColor().getColor(null),
                ((SolidFill)ps.getFill()).getOpacity().getValue(null),
                ps.getWidth().getValue(null),
                ps.getDashArray().getValue(null),
                Color.decode("#113355"),
                .5,
                6.0,
                5.0,
                "SQUARE");
        assertTrue(ap.equals(ca.get(Double.NEGATIVE_INFINITY)));
    }

    @Test
    public void testNullViewBox() throws Exception {
        PointSymbolizer ps = getPointSymbolizer();
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        mg.setViewBox(null);
        CategorizedPoint cp = new CategorizedPoint(ps);
        PointParameters tester = new PointParameters(Color.decode("#223344"),.2,2.0,"1 1",Color.decode("#113355"),.5,
                MarkGraphic.DEFAULT_SIZE,MarkGraphic.DEFAULT_SIZE,"SQUARE");
        assertTrue(cp.get(Double.NEGATIVE_INFINITY).equals(tester));
    }

    @Test
    public void testViewBoxNullWidth() throws Exception {
        PointSymbolizer ps = getPointSymbolizer();
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        mg.getViewBox().setWidth(null);
        CategorizedPoint cp = new CategorizedPoint(ps);
        PointParameters tester = new PointParameters(Color.decode("#223344"),.2,2.0,"1 1",Color.decode("#113355"),.5,5.0,5.0,"SQUARE");
        assertTrue(cp.get(Double.NEGATIVE_INFINITY).equals(tester));
    }

    @Test
    public void testViewBoxNullHeight() throws Exception {
        PointSymbolizer ps = getPointSymbolizer();
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        mg.getViewBox().setHeight(null);
        CategorizedPoint cp = new CategorizedPoint(ps);
        PointParameters tester = new PointParameters(Color.decode("#223344"),.2,2.0,"1 1",Color.decode("#113355"),.5,6.0,6.0,"SQUARE");
        assertTrue(cp.get(Double.NEGATIVE_INFINITY).equals(tester));
    }

    @Test
    public void testParamsToCat() throws Exception {
        PointSymbolizer ps = new PointSymbolizer();
        CategorizedPoint cp = new CategorizedPoint(ps);
        cp.put(25.0, new PointParameters(Color.decode("#223344"), .2, 22.0, "1 1", Color.decode("#113355"), .5, 5.0, 5.0, "SQUARE"));
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        PenStroke str = (PenStroke) mg.getStroke();
        assertTrue(str.getWidth() instanceof Categorize);
        assertTrue(str.getDashArray() instanceof Categorize);
        assertTrue(((SolidFill)str.getFill()).getColor() instanceof Categorize);
        assertTrue(((SolidFill)str.getFill()).getOpacity() instanceof Categorize);
        assertTrue(((SolidFill)mg.getFill()).getColor() instanceof Categorize);
        assertTrue(((SolidFill)mg.getFill()).getOpacity() instanceof Categorize);
        assertTrue(mg.getViewBox().getWidth() instanceof Categorize);
        assertTrue(mg.getViewBox().getHeight() instanceof Categorize);
        assertTrue(mg.getWkn() instanceof Categorize);
    }

    @Test
    public void testParamsToCatAfterStrokeEnabling() throws Exception {
        Style s = getStyle(CATEGORIZED_POINT_NO_STROKE);
        PointSymbolizer ps = (PointSymbolizer) s.getRules().get(0).getCompositeSymbolizer().getChildren().get(0);
        CategorizedPoint cp = new CategorizedPoint(ps);
        assertFalse(cp.isStrokeEnabled());
        cp.setStrokeEnabled(true);
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        PenStroke str = (PenStroke) mg.getStroke();
        assertTrue(str.getWidth() instanceof Literal);
        assertTrue(str.getDashArray() instanceof Literal);
        assertTrue(((SolidFill)str.getFill()).getColor() instanceof Literal);
        assertTrue(((SolidFill)str.getFill()).getOpacity() instanceof Literal);
        cp.put(25.0, new PointParameters(Color.decode("#223344"), .2, 22.0, "1 1", Color.decode("#113355"), .5, 5.0, 5.0, "SQUARE"));
        assertTrue(str.getWidth() instanceof Categorize);
        assertTrue(str.getDashArray() instanceof Categorize);
        assertTrue(((SolidFill)str.getFill()).getColor() instanceof Categorize);
        assertTrue(((SolidFill)str.getFill()).getOpacity() instanceof Categorize);
    }

    private PointSymbolizer getPointSymbolizer() throws Exception {
        Style s = getStyle(CATEGORIZED_POINT);
        return (PointSymbolizer) s.getRules().get(0).getCompositeSymbolizer().getChildren().get(0);
    }

    private CategorizedPoint getCategorizedPoint() throws Exception {
        return new CategorizedPoint(getPointSymbolizer());
    }

    private CategorizedPoint getNoStroke() throws Exception {
        Style s = getStyle(CATEGORIZED_POINT_NO_STROKE);
        PointSymbolizer as = (PointSymbolizer) s.getRules().get(0).getCompositeSymbolizer().getChildren().get(0);
        return new CategorizedPoint(as);
    }
}
