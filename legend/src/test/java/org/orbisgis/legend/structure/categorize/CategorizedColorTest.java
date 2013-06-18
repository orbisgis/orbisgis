package org.orbisgis.legend.structure.categorize;

import org.junit.Test;
import org.orbisgis.core.renderer.se.parameter.color.Categorize2Color;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.color.ColorParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.legend.structure.recode.type.TypeEvent;
import org.orbisgis.legend.structure.recode.type.TypeListener;

import java.awt.*;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Alexis Guéganno
 */
public class CategorizedColorTest {

    /**
     * Let's check we fire the expected event when explicitly setting a new Parameter in our CategorizedColor.
     * @throws Exception
     */
    @Test
    public void testFiredTypeEvent() throws Exception{
        RealAttribute vf = new RealAttribute("height");
        Categorize2Color c2s = new Categorize2Color(new ColorLiteral("#012345"),new ColorLiteral("#001122"), vf);
        CategorizedColor cs = new CategorizedColor(c2s);
        DummyTypeListener tl = new DummyTypeListener();
        cs.addListener(tl);
        cs.setParameter(new ColorLiteral("#AEBECE"));
        assertTrue(tl.count == 1);
        cs.setParameter(c2s);
        assertTrue(tl.count == 2);
    }

    @Test
    public void testGetFallback() throws Exception{
        CategorizedColor cs = new CategorizedColor(getCategorize2Color());
        assertTrue(cs.getFallbackValue().equals(Color.decode("#001122")));
        cs = new CategorizedColor(new ColorLiteral("#EDCBA0"));
        assertTrue(cs.getFallbackValue().equals(Color.decode("#EDCBA0")));
    }

    @Test
    public void testSetFallback() throws Exception{
        CategorizedColor cs = new CategorizedColor(getCategorize2Color());
        cs.setFallbackValue(Color.decode("#02468A"));
        assertTrue(cs.getFallbackValue().equals(Color.decode("#02468A")));
        assertTrue(((Categorize2Color)cs.getParameter()).getFallbackValue().getColor(null).equals(Color.decode("#02468A")));
        cs = new CategorizedColor(new ColorLiteral("#EDCBA0"));
        cs.setFallbackValue(Color.decode("#13579B"));
        assertTrue(cs.getFallbackValue().equals(Color.decode("#13579B")));
        assertTrue(((ColorLiteral)cs.getParameter()).getColor(null).equals(Color.decode("#13579B")));
    }

    @Test
    public void testGetFromCategorize(){
        CategorizedColor cs = new CategorizedColor(getCategorize2Color());
        assertTrue(cs.get(20.0).equals(Color.decode("#332133")));
        assertTrue(cs.get(Double.NEGATIVE_INFINITY).equals(Color.decode("#012345")));
        assertNull(cs.get(25.0));
        assertTrue(cs.get(30.0).equals(Color.decode("#A8B9C0")));
        cs = new CategorizedColor(new ColorLiteral("#EDCBA0"));
        assertNull(cs.get(25.0));
        assertTrue(cs.get(Double.NEGATIVE_INFINITY).equals(Color.decode("#EDCBA0")));
    }

    @Test
    public void testPutInfLiteral() throws Exception{
        CategorizedColor cs = new CategorizedColor(new ColorLiteral("#EDCBA0"));
        //The only mapping in cs is -INF -> yo
        cs.put(Double.NEGATIVE_INFINITY, new Color(0XDE,0XDE,0XCA));
        assertTrue(cs.get(Double.NEGATIVE_INFINITY).equals(Color.decode("#DEDECA")));
        assertTrue(cs.getParameter() instanceof ColorLiteral);
        assertTrue(((ColorLiteral)cs.getParameter()).getColor(null).equals(Color.decode("#DEDECA")));
    }

    @Test
    public void testPutNotInfLiteral() throws Exception{
        CategorizedColor cs = new CategorizedColor(new ColorLiteral("#EDCBA0"));
        cs.setField("hi");
        //The only mapping in cs is -INF -> yo
        cs.put(8.0, Color.decode("#DEDECA"));
        assertTrue(cs.get(Double.NEGATIVE_INFINITY).equals(Color.decode("#EDCBA0")));
        assertTrue(cs.get(8.0).equals(Color.decode("#DEDECA")));
        assertTrue(cs.getParameter() instanceof Categorize2Color);
    }

    @Test
    public void testPutInfCategorize()throws Exception {
        CategorizedColor cs = new CategorizedColor(getCategorize2Color());
        cs.put(Double.NEGATIVE_INFINITY, Color.decode("#DEDECA"));
        assertTrue(cs.get(Double.NEGATIVE_INFINITY).equals(Color.decode("#DEDECA")));
        assertTrue(cs.get(20.0).equals(Color.decode("#332133")));
        assertTrue(cs.get(30.0).equals(Color.decode("#A8B9C0")));
    }

    @Test
    public void testPutNotInfCategorize()throws Exception {
        CategorizedColor cs = new CategorizedColor(getCategorize2Color());
        cs.put(20.0, Color.decode("#DEDECA"));
        assertTrue(cs.get(Double.NEGATIVE_INFINITY).equals(Color.decode("#012345")));
        assertTrue(cs.get(20.0).equals(Color.decode("#DEDECA")));
        assertTrue(cs.get(30.0).equals(Color.decode("#A8B9C0")));
    }

    @Test
    public void testPutNewMappingCategorize()throws Exception {
        CategorizedColor cs = new CategorizedColor(getCategorize2Color());
        cs.put(25.0, Color.decode("#DEDECA"));
        assertTrue(cs.get(Double.NEGATIVE_INFINITY).equals(Color.decode("#012345")));
        assertTrue(cs.get(20.0).equals(Color.decode("#332133")));
        assertTrue(cs.get(25.0).equals(Color.decode("#DEDECA")));
        assertTrue(cs.get(30.0).equals(Color.decode("#A8B9C0")));
    }

    @Test
    public void testRemoveFromLiteral(){
        CategorizedColor cs = new CategorizedColor(new ColorLiteral("#EDCBA0"));
        assertNull(cs.remove(50.0));
        assertNull(cs.remove(Double.NEGATIVE_INFINITY));
    }

    @Test
    public void testRemoveFromCat(){
        CategorizedColor cs = new CategorizedColor(getCategorize2Color());
        assertTrue(cs.remove(20.0).equals(Color.decode("#332133")));
        assertNull(cs.remove(20.0));
    }

    @Test
    public void testRemoveInfFromCat(){
        CategorizedColor cs = new CategorizedColor(getCategorize2Color());
        assertTrue(cs.remove(Double.NEGATIVE_INFINITY).equals(Color.decode("#012345")));
        assertTrue(cs.get(Double.NEGATIVE_INFINITY).equals(Color.decode("#332133")));
    }

    /**
     * Here the fallback value and -INF mapping are different, we don't get a literal in the end.
     */
    @Test
    public void testRemoveAll1(){
        CategorizedColor cs = new CategorizedColor(getCategorize2Color());
        cs.remove(20.0);
        cs.remove(30.0);
        assertNull(cs.remove(Double.NEGATIVE_INFINITY));
        assertTrue(cs.getParameter() instanceof Categorize2Color);
    }

    /**
     * Here the fallback value and -INF mapping are equal, we get a literal in the end.
     */
    @Test
    public void testRemoveAll2() throws Exception{
        CategorizedColor cs = new CategorizedColor(getCategorize2Color());
        Color n = cs.get(Double.NEGATIVE_INFINITY);
        cs.setFallbackValue(n);
        assertTrue(cs.getFallbackValue().equals(cs.get(Double.NEGATIVE_INFINITY)));
        cs.remove(20.0);
        cs.remove(30.0);
        assertTrue(cs.getParameter() instanceof ColorLiteral);

    }

    @Test
    public void testGetFromLower() throws Exception {
        CategorizedColor cs = new CategorizedColor(getCategorize2Color());
        assertTrue(cs.getFromLower(25.0).equals(Color.decode("#332133")));
    }


    public class DummyTypeListener implements TypeListener {
        public int count = 0;
        @Override
        public void typeChanged(TypeEvent te) {
            count++;
        }
    }

    private  Categorize2Color getCategorize2Color(){
        RealAttribute vf = new RealAttribute("height");
        Categorize2Color c2s = new Categorize2Color(new ColorLiteral("#012345"),new ColorLiteral("#001122"), vf);
        c2s.put(new RealLiteral(20),new ColorLiteral("#332133"));
        c2s.put(new RealLiteral(30), new ColorLiteral("#A8B9C0"));
        return c2s;
    }

}
