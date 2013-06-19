package org.orbisgis.legend.structure.categorize;

import org.junit.Test;
import org.orbisgis.core.renderer.se.parameter.real.Categorize2Real;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.legend.structure.recode.type.TypeEvent;
import org.orbisgis.legend.structure.recode.type.TypeListener;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Alexis Guéganno
 */
public class CategorizedRealTest {

    /**
     * Let's check we fire the expected event when explicitly setting a new Parameter in our CategorizedReal.
     * @throws Exception
     */
    @Test
    public void testFiredTypeEvent() throws Exception{
        RealAttribute vf = new RealAttribute("height");
        Categorize2Real c2s = new Categorize2Real(new RealLiteral(100.0),new RealLiteral(-15.0), vf);
        CategorizedReal cs = new CategorizedReal(c2s);
        DummyTypeListener tl = new DummyTypeListener();
        cs.addListener(tl);
        cs.setParameter(new RealLiteral(252.252));
        assertTrue(tl.count == 1);
        cs.setParameter(c2s);
        assertTrue(tl.count == 2);
    }

    @Test
    public void testGetFallback() throws Exception{
        CategorizedReal cs = new CategorizedReal(getCategorize2Real());
        assertTrue(cs.getFallbackValue().equals(-15.0));
        cs = new CategorizedReal(new RealLiteral(16.8));
        assertTrue(cs.getFallbackValue().equals(16.8));
    }

    @Test
    public void testSetFallback() throws Exception{
        CategorizedReal cs = new CategorizedReal(getCategorize2Real());
        cs.setFallbackValue(16.0);
        assertTrue(cs.getFallbackValue().equals(16.0));
        assertTrue(((Categorize2Real)cs.getParameter()).getFallbackValue().getValue(null).equals(16.0));
        cs = new CategorizedReal(new RealLiteral(16.8));
        cs.setFallbackValue(-1000.0);
        assertTrue(cs.getFallbackValue().equals(-1000.0));
        assertTrue(((RealLiteral)cs.getParameter()).getValue(null).equals(-1000.0));
    }

    @Test
    public void testGetFromCategorize(){
        CategorizedReal cs = new CategorizedReal(getCategorize2Real());
        assertTrue(cs.get(20.0).equals(24.0));
        assertTrue(cs.get(Double.NEGATIVE_INFINITY).equals(100.0));
        assertNull(cs.get(25.0));
        assertTrue(cs.get(30.0).equals(0.1));
        cs = new CategorizedReal(new RealLiteral(16.8));
        assertNull(cs.get(25.0));
        assertTrue(cs.get(Double.NEGATIVE_INFINITY).equals(16.8));
    }

    @Test
    public void testPutInfLiteral() throws Exception{
        CategorizedReal cs = new CategorizedReal(new RealLiteral(16.8));
        //The only mapping in cs is -INF -> yo
        cs.put(Double.NEGATIVE_INFINITY, 3.14);
        assertTrue(cs.get(Double.NEGATIVE_INFINITY).equals(3.14));
        assertTrue(cs.getParameter() instanceof RealLiteral);
        assertTrue(((RealLiteral)cs.getParameter()).getValue(null).equals(3.14));
    }

    @Test
    public void testPutNotInfLiteral() throws Exception{
        CategorizedReal cs = new CategorizedReal(new RealLiteral(16.8));
        cs.setField("hi");
        //The only mapping in cs is -INF -> yo
        cs.put(8.0, 3.14);
        assertTrue(cs.get(Double.NEGATIVE_INFINITY).equals(16.8));
        assertTrue(cs.get(8.0).equals(3.14));
        assertTrue(cs.getParameter() instanceof Categorize2Real);
    }

    @Test
    public void testPutInfCategorize()throws Exception {
        CategorizedReal cs = new CategorizedReal(getCategorize2Real());
        cs.put(Double.NEGATIVE_INFINITY, 3.14);
        assertTrue(cs.get(Double.NEGATIVE_INFINITY).equals(3.14));
        assertTrue(cs.get(20.0).equals(24.0));
        assertTrue(cs.get(30.0).equals(0.1));
    }

    @Test
    public void testPutNotInfCategorize()throws Exception {
        CategorizedReal cs = new CategorizedReal(getCategorize2Real());
        cs.put(20.0, 3.14);
        assertTrue(cs.get(Double.NEGATIVE_INFINITY).equals(100.0));
        assertTrue(cs.get(20.0).equals(3.14));
        assertTrue(cs.get(30.0).equals(0.1));
    }

    @Test
    public void testPutNewMappingCategorize()throws Exception {
        CategorizedReal cs = new CategorizedReal(getCategorize2Real());
        cs.put(25.0, 3.14);
        assertTrue(cs.get(Double.NEGATIVE_INFINITY).equals(100.0));
        assertTrue(cs.get(20.0).equals(24.0));
        assertTrue(cs.get(25.0).equals(3.14));
        assertTrue(cs.get(30.0).equals(0.1));
    }

    @Test
    public void testRemoveFromLiteral(){
        CategorizedReal cs = new CategorizedReal(new RealLiteral(16.8));
        assertNull(cs.remove(50.0));
        assertNull(cs.remove(Double.NEGATIVE_INFINITY));
    }

    @Test
    public void testRemoveFromCat(){
        CategorizedReal cs = new CategorizedReal(getCategorize2Real());
        assertTrue(cs.remove(20.0).equals(24.0));
        assertNull(cs.remove(20.0));
    }

    @Test
    public void testRemoveInfFromCat(){
        CategorizedReal cs = new CategorizedReal(getCategorize2Real());
        assertTrue(cs.remove(Double.NEGATIVE_INFINITY).equals(100.0));
        assertTrue(cs.get(Double.NEGATIVE_INFINITY).equals(24.0));
    }

    /**
     * Here the fallback value and -INF mapping are different, we don't get a literal in the end.
     */
    @Test
    public void testRemoveAll1(){
        CategorizedReal cs = new CategorizedReal(getCategorize2Real());
        cs.remove(20.0);
        cs.remove(30.0);
        assertNull(cs.remove(Double.NEGATIVE_INFINITY));
        assertTrue(cs.getParameter() instanceof Categorize2Real);
    }

    /**
     * Here the fallback value and -INF mapping are equal, we get a literal in the end.
     */
    @Test
    public void testRemoveAll2(){
        CategorizedReal cs = new CategorizedReal(getCategorize2Real());
        cs.setFallbackValue(cs.get(Double.NEGATIVE_INFINITY));
        cs.remove(20.0);
        cs.remove(30.0);
        assertTrue(cs.getParameter() instanceof RealLiteral);

    }

    @Test
    public void testGetFromLower() throws Exception {
        CategorizedReal cs = new CategorizedReal(getCategorize2Real());
        assertTrue(cs.getFromLower(25.0).equals(24.0));
    }


    public class DummyTypeListener implements TypeListener {
        public int count = 0;
        @Override
        public void typeChanged(TypeEvent te) {
            count++;
        }
    }

    private Categorize2Real getCategorize2Real(){
        RealAttribute vf = new RealAttribute("height");
        Categorize2Real c2s = new Categorize2Real(new RealLiteral(100.0),new RealLiteral(-15.0), vf);
        c2s.put(new RealLiteral(20),new RealLiteral(24.0));
        c2s.put(new RealLiteral(30), new RealLiteral(0.1));
        return c2s;
    }

}
