package org.orbisgis.core.renderer.se.parameter.color;

import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;

/**
 *
 * @author maxence
 */
public class Recode2ColorTest extends TestCase {
    
    public Recode2ColorTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        key1 = "k1";
        key2 = "k2";
        key3 = "k3";
        key4 = "k4";
        key5 = "k5";

        c1 = new ColorLiteral();
        c2 = new ColorLiteral();
        c3 = new ColorLiteral();
        c4 = new ColorLiteral();
        c5 = new ColorLiteral();
        
        fb = new ColorLiteral();

        lookup = new StringLiteral("hello");

        recode = new Recode2Color(fb, lookup);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAddMapItems(){
        try {
            recode.addMapItem(new StringLiteral(key1), c1);
            assertTrue(recode.getNumMapItem() == 1);
            assertTrue(recode.getMapItemKey(0).equals(new StringLiteral("k1")));
            assertTrue(recode.getMapItemValue(0).getColor(null, -1) == c1.getColor(null, -1));
            assertTrue(recode.getMapItemValue(new StringLiteral("k1")).getColor(null, -1) == c1.getColor(null, -1));
            recode.addMapItem(new StringLiteral(key2), c2);
            assertTrue(recode.getNumMapItem() == 2);
            assertTrue(recode.getMapItemKey(0).equals(new StringLiteral("k1")));
            assertTrue(recode.getMapItemKey(1).equals(new StringLiteral("k2")));
            assertTrue(recode.getMapItemValue(1).getColor(null, -1) == c2.getColor(null, -1));
            assertTrue(recode.getMapItemValue(new StringLiteral("k2")).getColor(null, -1) == c2.getColor(null, -1));
            recode.addMapItem(new StringLiteral(key3), c3);
            assertTrue(recode.getNumMapItem() == 3);
            assertTrue(recode.getMapItemKey(0).equals(new StringLiteral("k1")));
            assertTrue(recode.getMapItemKey(1).equals(new StringLiteral("k2")));
            assertTrue(recode.getMapItemKey(2).equals(new StringLiteral("k3")));
            assertTrue(recode.getMapItemValue(2).getColor(null, -1) == c3.getColor(null, -1));
            assertTrue(recode.getMapItemValue(new StringLiteral("k3")).getColor(null, -1) == c3.getColor(null, -1));
        } catch (ParameterException ex) {
            Logger.getLogger(Recode2ColorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void testGetParameter(){
        try {
            testAddMapItems();
            recode.setLookupValue(new StringLiteral("k1"));

            assertTrue(recode.getColor(null, -1) == c1.getColor(null, -1));
            recode.setLookupValue(new StringLiteral("k2"));
            assertTrue(recode.getColor(null, -1) == c2.getColor(null, -1));
            recode.setLookupValue(new StringLiteral("k3"));
            assertTrue(recode.getColor(null, -1) == c3.getColor(null, -1));
        } catch (ParameterException ex) {
            Logger.getLogger(Recode2ColorTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(false);
        }
    }

    public void testRemoveMapItem(){
        testAddMapItems();
        assertTrue(recode.getNumMapItem() == 3);
        recode.removeMapItem(new StringLiteral("k2"));

        assertTrue(recode.getNumMapItem() == 2);
        recode.removeMapItem(new StringLiteral("k1"));

        assertTrue(recode.getNumMapItem() == 1);

        recode.removeMapItem(new StringLiteral("k3"));
        assertTrue(recode.getNumMapItem() == 0);
    }

    protected Recode2Color recode;

    String key1;
    String key2;
    String key3;
    String key4;
    String key5;

    ColorParameter c1;
    ColorLiteral c2;
    ColorLiteral c3;
    ColorLiteral c4;
    ColorLiteral c5;
 
    ColorLiteral fb;

    StringParameter lookup;


}
