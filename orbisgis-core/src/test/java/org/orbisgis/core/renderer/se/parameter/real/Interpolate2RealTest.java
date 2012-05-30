/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.parameter.real;

import net.opengis.se._2_0.core.ModeType;
import org.orbisgis.core.renderer.se.parameter.Interpolate;
import org.orbisgis.core.renderer.se.parameter.Interpolate.InterpolationMode;
import org.orbisgis.core.renderer.se.parameter.InterpolationPoint;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author maxence
 */
public class Interpolate2RealTest {

        @Test
        public void testInterpolateWKN() throws Exception {
                ModeType.fromValue(Interpolate.InterpolationMode.COSINE.toString().toLowerCase());
                ModeType.fromValue(Interpolate.InterpolationMode.LINEAR.toString().toLowerCase());
                ModeType.fromValue(Interpolate.InterpolationMode.CUBIC.toString().toLowerCase());
                try{
                        ModeType.fromValue(Interpolate.InterpolationMode.COSINE.toString());
                        fail();
                } catch (IllegalArgumentException e){
                }
                try{
                        ModeType.fromValue(Interpolate.InterpolationMode.LINEAR.toString());
                        fail();
                } catch (IllegalArgumentException e){
                }
                try{
                        ModeType.fromValue(Interpolate.InterpolationMode.CUBIC.toString());
                        fail();
                } catch (IllegalArgumentException e){
                }
                assertTrue(true);
        }

	/**
	 * Test of linear interpolation
	 */
        @Test
        public void testLinearInterpolation() throws ParameterException {
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
		assertEquals(result, 100.0, 0.00000001);

		interpolate.setLookupValue(new RealLiteral(20.0));
		result = interpolate.getValue(null, -1);
		System.out.println("Result is: " + result);
		assertEquals(result, 102.5, 0.00000001);

		interpolate.setLookupValue(new RealLiteral(55.0));
		result = interpolate.getValue(null, -1);
		System.out.println("Result is: " + result);
		assertEquals(result, 155.0, 0.00000001);

		interpolate.setLookupValue(new RealLiteral(70.0));
		result = interpolate.getValue(null, -1);
		System.out.println("Result is: " + result);
		assertEquals(result, 200.0, 0.00000001);
	}


	/**
	 * Test of linear interpolation
	 */ 
        @Test
        public void testCosineInterpolation() throws ParameterException {
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
		assertEquals(result, 100.0, 0.00000001);

		interpolate.setLookupValue(new RealLiteral(20.0));
		result = interpolate.getValue(null, -1);
		System.out.println("Result is: " + result);
		assertEquals(result, 101.46446609406726, 0.00000001);

		interpolate.setLookupValue(new RealLiteral(55.0));
		result = interpolate.getValue(null, -1);
		System.out.println("Result is: " + result);
		assertEquals(result, 155.0, 0.00000001);

		interpolate.setLookupValue(new RealLiteral(70.0));
		result = interpolate.getValue(null, -1);
		System.out.println("Result is: " + result);
		assertEquals(result, 200.0, 0.00000001);
	}

}
