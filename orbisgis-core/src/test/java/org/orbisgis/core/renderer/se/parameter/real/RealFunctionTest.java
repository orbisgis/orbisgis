/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.parameter.real;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author maxence
 */
public class RealFunctionTest {

        @Test
    public void testDiv(){
        try {
            RealFunction op1 = new RealFunction("div");
            op1.addOperand(new RealLiteral(100));
            op1.addOperand(new RealLiteral(10));
            assertEquals(op1.getValue(null, -1), 10.0, 0.0000001);
        } catch (ParameterException ex) {
            assertTrue(false);
        }
    }

    @Test
    public void testSqrt(){
        try {

            RealFunction op1 = new RealFunction("sqrt");
            op1.addOperand(new RealLiteral(25.0));

            assertEquals(5.0, op1.getValue(null, -1), 0.0000001);
            RealFunction op2 = new RealFunction("sqrt");
            op2.addOperand(new RealLiteral(-25.0));

            assertEquals(Double.NaN, op2.getValue(null, -1), 0.0000001);
        } catch (ParameterException ex) {
            Logger.getLogger(RealFunctionTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Test
    public void testLog(){
        try {
            RealFunction op1 = new RealFunction("log");
            op1.addOperand(new RealLiteral(100));
            assertEquals(2.0, op1.getValue(null, -1), 0.0000001);

            RealFunction op2 = new RealFunction("log");
            op2.addOperand(new RealLiteral(-100));
            assertEquals(Double.NaN, op2.getValue(null, -1), 0.0000001);
        }
        catch (ParameterException ex) {
            Logger.getLogger(RealFunctionTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testgetOperator() {
            RealFunction op1 = new RealFunction("log");
            assertTrue(op1.getOperator() == RealFunction.Operators.LOG);
    }
}
