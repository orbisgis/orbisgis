/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.parameter.real;

import junit.framework.TestCase;
import org.orbisgis.core.renderer.se.parameter.Interpolate.InterpolationMode;
import org.orbisgis.core.renderer.se.parameter.InterpolationPoint;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 *
 * @author maxence
 */
public class Interpolate2RealTest extends TestCase {
    
    public Interpolate2RealTest(String testName) {
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


	/**
	 * Test of linear interpolation
	 */ public void testLinearInterpolation() throws ParameterException {
		System.out.println("Linear Interpolation");
		Interpolate2Real interpolate = new Interpolate2Real(new RealLiteral(-1));

		interpolate.setInterpolationMode(InterpolationMode.LINEAR);

		InterpolationPoint<RealParameter> ip1 = new InterpolationPoint<RealParameter>();
		ip1.setData(10.0);
		ip1.setValue(new RealLiteral(100.0));
		interpolate.addInterpolationPoint(ip1);

		InterpolationPoint<RealParameter> ip2 = new InterpolationPoint<RealParameter>();
		ip2.setData(50.0);
		ip2.setValue(new RealLiteral(110.0));
		interpolate.addInterpolationPoint(ip2);


		InterpolationPoint<RealParameter> ip3 = new InterpolationPoint<RealParameter>();
		ip3.setData(60.0);
		ip3.setValue(new RealLiteral(200.0));
		interpolate.addInterpolationPoint(ip3);

		double result;

		interpolate.setLookupValue(new RealLiteral(1.0));
		result = interpolate.getValue(null, -1);

		System.out.println("Result is: " + result);
		assertEquals(result, 100.0);

		interpolate.setLookupValue(new RealLiteral(20.0));
		result = interpolate.getValue(null, -1);
		System.out.println("Result is: " + result);
		assertEquals(result, 102.5);

		interpolate.setLookupValue(new RealLiteral(55.0));
		result = interpolate.getValue(null, -1);
		System.out.println("Result is: " + result);
		assertEquals(result, 155.0);

		interpolate.setLookupValue(new RealLiteral(70.0));
		result = interpolate.getValue(null, -1);
		System.out.println("Result is: " + result);
		assertEquals(result, 200.0);
	}


	/**
	 * Test of linear interpolation
	 */ public void testCosineInterpolation() throws ParameterException {
		System.out.println("Cosine Interpolation");
		Interpolate2Real interpolate = new Interpolate2Real(new RealLiteral(-1));

		interpolate.setInterpolationMode(InterpolationMode.COSINE);

		InterpolationPoint<RealParameter> ip1 = new InterpolationPoint<RealParameter>();
		ip1.setData(10.0);
		ip1.setValue(new RealLiteral(100.0));
		interpolate.addInterpolationPoint(ip1);

		InterpolationPoint<RealParameter> ip2 = new InterpolationPoint<RealParameter>();
		ip2.setData(50.0);
		ip2.setValue(new RealLiteral(110.0));
		interpolate.addInterpolationPoint(ip2);


		InterpolationPoint<RealParameter> ip3 = new InterpolationPoint<RealParameter>();
		ip3.setData(60.0);
		ip3.setValue(new RealLiteral(200.0));
		interpolate.addInterpolationPoint(ip3);

		double result;

		interpolate.setLookupValue(new RealLiteral(1.0));
		result = interpolate.getValue(null, -1);
		System.out.println("Result is: " + result);
		assertEquals(result, 100.0);

		interpolate.setLookupValue(new RealLiteral(20.0));
		result = interpolate.getValue(null, -1);
		System.out.println("Result is: " + result);
		assertEquals(result, 101.46446609406726);

		interpolate.setLookupValue(new RealLiteral(55.0));
		result = interpolate.getValue(null, -1);
		System.out.println("Result is: " + result);
		assertEquals(result, 155.0);

		interpolate.setLookupValue(new RealLiteral(70.0));
		result = interpolate.getValue(null, -1);
		System.out.println("Result is: " + result);
		assertEquals(result, 200.0);
	}

}
