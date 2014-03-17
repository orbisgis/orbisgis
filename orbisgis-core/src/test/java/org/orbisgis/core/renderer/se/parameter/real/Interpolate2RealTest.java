/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.coremap.renderer.se.parameter.real;

import net.opengis.se._2_0.core.ModeType;
import org.orbisgis.coremap.renderer.se.parameter.Interpolate;
import org.orbisgis.coremap.renderer.se.parameter.Interpolate.InterpolationMode;
import org.orbisgis.coremap.renderer.se.parameter.InterpolationPoint;
import org.orbisgis.coremap.renderer.se.parameter.ParameterException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Maxence Laurent
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
                Interpolate2Real interpolate= getLinearInterpolation();
		double result;
		interpolate.setLookupValue(new RealLiteral(1.0));
		result = interpolate.getValue(null, -1);
		assertEquals(result, 100.0, 0.00000001);
		interpolate.setLookupValue(new RealLiteral(20.0));
		result = interpolate.getValue(null, -1);
		assertEquals(result, 102.5, 0.00000001);
		interpolate.setLookupValue(new RealLiteral(55.0));
		result = interpolate.getValue(null, -1);
		assertEquals(result, 155.0, 0.00000001);
		interpolate.setLookupValue(new RealLiteral(70.0));
		result = interpolate.getValue(null, -1);
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

        @Test
        public void testChildren(){
                Interpolate2Real interpolate = getLinearInterpolation();
                //We will have 4 children : the lookup value and the three interpolation points
                assertTrue(interpolate.getChildren().size() == 4);
        }

        private Interpolate2Real getLinearInterpolation() {
		System.out.println("Linear Interpolation");
		Interpolate2Real interpolate = new Interpolate2Real(new RealLiteral(-1));
		interpolate.setInterpolationMode(InterpolationMode.LINEAR);
                //First point
		InterpolationPoint<RealParameter> ip1 = new InterpolationPoint<RealParameter>();
		ip1.setData(10.0);
		ip1.setValue(new RealLiteral(100.0));
		interpolate.addInterpolationPoint(ip1);
                //Second point
		InterpolationPoint<RealParameter> ip2 = new InterpolationPoint<RealParameter>();
		ip2.setData(50.0);
		ip2.setValue(new RealLiteral(110.0));
		interpolate.addInterpolationPoint(ip2);
                //Third point
		InterpolationPoint<RealParameter> ip3 = new InterpolationPoint<RealParameter>();
		ip3.setData(60.0);
		ip3.setValue(new RealLiteral(200.0));
		interpolate.addInterpolationPoint(ip3);
                return interpolate;
        }

}
