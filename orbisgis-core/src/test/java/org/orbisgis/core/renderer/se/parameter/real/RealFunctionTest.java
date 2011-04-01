/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.parameter.real;

import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 *
 * @author maxence
 */
public class RealFunctionTest extends TestCase {
    
    public RealFunctionTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSqrt(){
        try {

            RealFunction op1 = new RealFunction("sqrt");
            op1.addOperand(new RealLiteral(25.0));

            assertEquals(5.0, op1.getValue(null, -1));
            RealFunction op2 = new RealFunction("sqrt");
            op2.addOperand(new RealLiteral(-25.0));

            assertEquals(Double.NaN, op2.getValue(null, -1));
        } catch (ParameterException ex) {
            Logger.getLogger(RealFunctionTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void testLog(){
        try {
            RealFunction op1 = new RealFunction("log");
            op1.addOperand(new RealLiteral(100));
            assertEquals(2.0, op1.getValue(null, -1));

            RealFunction op2 = new RealFunction("log");
            op2.addOperand(new RealLiteral(-100));
            assertEquals(Double.NaN, op2.getValue(null, -1));
        }
        catch (ParameterException ex) {
            Logger.getLogger(RealFunctionTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
