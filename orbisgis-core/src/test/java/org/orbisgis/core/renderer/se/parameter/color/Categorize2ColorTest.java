package org.orbisgis.core.renderer.se.parameter.color;

import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;

/**
 *
 * @author maxence
 */
public class Categorize2ColorTest extends TestCase {

    public Categorize2ColorTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        fallback = new ColorLiteral();

        class1 = new ColorLiteral();
        class2 = new ColorLiteral();
        class3 = new ColorLiteral();
        class4 = new ColorLiteral();

        t1 = new RealLiteral(100.0);
        t2 = new RealLiteral(200.0);
        t3 = new RealLiteral(50.0);
        t4 = new RealLiteral(75.0);
        t5 = new RealLiteral(300.0);

        categorize = new Categorize2Color(class1, fallback, new RealLiteral());
        categorize.addClass(t1, class2);
        categorize.addClass(t2, class3);
        categorize.addClass(t3, class4);

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testConstructor(){
        assertTrue(categorize.getNumClasses() == 4);
        try {
            assertTrue(categorize.getClassValue(0).getColor(null) == class1.getColor(null));
        } catch (ParameterException ex) {
            Logger.getLogger(Categorize2ColorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void testAddClasses(){
        /*try {
            categorize.addClass(t1, class2);

            assertTrue(categorize.getNumClasses() == 2);
            assertTrue(categorize.getClassValue(0).getColor(null) == class1.getColor(null));
            assertTrue(categorize.getClassValue(1).getColor(null) == class2.getColor(null));
            categorize.addClass(t2, class3);
            assertTrue(categorize.getNumClasses() == 3);
            assertTrue(categorize.getClassValue(0).getColor(null) == class1.getColor(null));
            assertTrue(categorize.getClassValue(1).getColor(null) == class2.getColor(null));
            assertTrue(categorize.getClassValue(2).getColor(null) == class3.getColor(null));
            categorize.addClass(t3, class4);
            assertTrue(categorize.getNumClasses() == 4);
            assertTrue(categorize.getClassValue(0).getColor(null) == class1.getColor(null));
            assertTrue(categorize.getClassValue(1).getColor(null) == class4.getColor(null));
            assertTrue(categorize.getClassValue(2).getColor(null) == class2.getColor(null));
            assertTrue(categorize.getClassValue(3).getColor(null) == class3.getColor(null));
        } catch (ParameterException ex) {
            Logger.getLogger(Categorize2ColorTest.class.getName()).log(Level.SEVERE, null, ex);
        }*/

    }

    public void testSetThresholds(){
        try {
            categorize.setThresholdValue(2, t4);
            assertTrue(categorize.getNumClasses() == 4);
            assertTrue(categorize.getClassValue(0).getColor(null) == class1.getColor(null));
            assertTrue(categorize.getClassValue(1).getColor(null) == class4.getColor(null));
            assertTrue(categorize.getClassValue(2).getColor(null) == class3.getColor(null));
            assertTrue(categorize.getClassValue(3).getColor(null) == class2.getColor(null));
            categorize.setThresholdValue(0, t5);
            assertTrue(categorize.getNumClasses() == 4);
            assertTrue(categorize.getClassValue(0).getColor(null) == class1.getColor(null));
            assertTrue(categorize.getClassValue(1).getColor(null) == class3.getColor(null));
            assertTrue(categorize.getClassValue(2).getColor(null) == class2.getColor(null));
            assertTrue(categorize.getClassValue(3).getColor(null) == class4.getColor(null));
        } catch (ParameterException ex) {
            Logger.getLogger(Categorize2ColorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }



    public void testRemoveClasses(){
        try {
            categorize.removeClass(0);
            assertTrue(categorize.getNumClasses() == 3);
            assertTrue(categorize.getClassValue(0).getColor(null) == class4.getColor(null));
            assertTrue(categorize.getClassValue(1).getColor(null) == class2.getColor(null));
            assertTrue(categorize.getClassValue(2).getColor(null) == class3.getColor(null));
            categorize.removeClass(2);
            assertTrue(categorize.getNumClasses() == 2);
            assertTrue(categorize.getClassValue(0).getColor(null) == class4.getColor(null));
            assertTrue(categorize.getClassValue(1).getColor(null) == class2.getColor(null));
            categorize.removeClass(1);
            assertTrue(categorize.getNumClasses() == 1);
            assertTrue(categorize.getClassValue(0).getColor(null) == class4.getColor(null));
        } catch (ParameterException ex) {
            Logger.getLogger(Categorize2ColorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void testGetParameter(){
        
    }

    protected Categorize2Color categorize;
    
    protected ColorParameter class1;
    protected ColorParameter class2;
    protected ColorParameter class3;
    protected ColorParameter class4;

    protected RealLiteral t1;
    protected RealLiteral t2;
    protected RealLiteral t3;
    protected RealLiteral t4;
    protected RealLiteral t5;

    protected ColorLiteral fallback;
}
