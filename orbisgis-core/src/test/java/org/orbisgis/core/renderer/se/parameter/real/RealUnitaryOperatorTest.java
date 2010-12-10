/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.parameter.real;

import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealUnaryOperator.RealUnitaryOperatorType;

/**
 *
 * @author maxence
 */
public class RealUnitaryOperatorTest extends TestCase {
    
    public RealUnitaryOperatorTest(String testName) {
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
            RealUnaryOperator op1 = new RealUnaryOperator(new RealLiteral(25.0), RealUnitaryOperatorType.SQRT);
            assertEquals(5.0, op1.getValue(null));
            RealUnaryOperator op2 = new RealUnaryOperator(new RealLiteral(-25.0), RealUnitaryOperatorType.SQRT);
            assertEquals(Double.NaN, op2.getValue(null));
        } catch (ParameterException ex) {
            Logger.getLogger(RealUnitaryOperatorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void testLog(){
        try {
            RealUnaryOperator op1 = new RealUnaryOperator(new RealLiteral(100), RealUnitaryOperatorType.LOG);
            assertEquals(2.0, op1.getValue(null));
            RealUnaryOperator op2 = new RealUnaryOperator(new RealLiteral(-100), RealUnitaryOperatorType.LOG);
            assertEquals(Double.NaN, op2.getValue(null));
        }
        /*
        public void runTest(){
        testSqrt();
        testLog();
        }*/
        catch (ParameterException ex) {
            Logger.getLogger(RealUnitaryOperatorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


/*
    public void runTest(){
        testSqrt();
        testLog();
    }*/
}
