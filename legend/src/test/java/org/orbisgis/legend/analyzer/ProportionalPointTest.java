/*
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
package org.orbisgis.legend.analyzer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.parameter.InterpolationPoint;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.*;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.analyzer.parameter.RealParameterAnalyzer;
import org.orbisgis.legend.analyzer.symbolizers.PointSymbolizerAnalyzer;
import org.orbisgis.legend.structure.parameter.NumericLegend;
import org.orbisgis.legend.thematic.proportional.ProportionalPoint;

/**
 *
 * @author Alexis Guéganno
 */
public class ProportionalPointTest extends AnalyzerTest {
    @Test
    public void testProportionalPointConstructor() throws Exception {
                Style st = getStyle(PROPORTIONAL_POINT);
                PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                ProportionalPoint uvp = new ProportionalPoint(ps);
                assertTrue(true);
    }
    @Test
    public void testProportionalPointConstructorWithoutStroke() throws Exception {
                Style st = getStyle(PROPORTIONAL_POINT_WITHOUT_STROKE);
                PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                ProportionalPoint uvp = new ProportionalPoint(ps);
                assertTrue(true);
    }
    @Test
    public void testProportionalPointConstructorWithoutFill() throws Exception {
                Style st = getStyle(PROPORTIONAL_POINT_WITHOUT_FILL);
                PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                ProportionalPoint uvp = new ProportionalPoint(ps);
                assertTrue(true);
    }

    @Test
    public void testLegendFromAnalyzerProportionalPoint() throws Exception {
                Style st = getStyle(PROPORTIONAL_POINT);
                PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                PointSymbolizerAnalyzer psa = new PointSymbolizerAnalyzer(ps);
                assertTrue(psa.getLegend() instanceof ProportionalPoint);

    }

    @Test
    public void testProportionalPointConstructorFail() throws Exception {
                Style st = getStyle(CONSTANT_POINT);
                PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                try{
                    ProportionalPoint uvp = new ProportionalPoint(ps);
                    fail();
                } catch(ClassCastException cce){
                    assertTrue(true);
                }
    }

    @Test
    public void testProportionalGetFirstData() throws Exception {
        Style st = getStyle(PROPORTIONAL_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        ProportionalPoint uvp = new ProportionalPoint(ps);
        assertTrue(uvp.getFirstData() == .0);
    }
    @Test
    public void testProportionalGetSecondData() throws Exception {
        Style st = getStyle(PROPORTIONAL_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        ProportionalPoint uvp = new ProportionalPoint(ps);
        double youhou = uvp.getSecondData();
        assertTrue(youhou == 1000.0);
    }

    @Test
    public void testProportionalSetFirstData() throws Exception {
        Style st = getStyle(PROPORTIONAL_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        ProportionalPoint uvp = new ProportionalPoint(ps);
        uvp.setFirstData(25.2);
        assertTrue(uvp.getFirstData() == 25.2);
    }
    @Test
    public void testProportionalSetSecondData() throws Exception {
        Style st = getStyle(PROPORTIONAL_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        ProportionalPoint uvp = new ProportionalPoint(ps);
        uvp.setSecondData(42.0);
        assertTrue(uvp.getSecondData() == 42.0);
    }

    @Test
    public void testProportionalGetFirstValue() throws Exception {
        Style st = getStyle(PROPORTIONAL_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        ProportionalPoint uvp = new ProportionalPoint(ps);
        assertTrue(uvp.getFirstValue() == .0);
    }
    @Test
    public void testProportionalGetSecondValue() throws Exception {
        Style st = getStyle(PROPORTIONAL_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        ProportionalPoint uvp = new ProportionalPoint(ps);
        assertTrue( uvp.getSecondValue()== 200.0);
    }

    @Test
    public void testProportionalSetFirstValue() throws Exception {
        Style st = getStyle(PROPORTIONAL_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        ProportionalPoint uvp = new ProportionalPoint(ps);
        uvp.setFirstValue(24.0);
        assertTrue(uvp.getFirstValue() == 24.0);
    }
    @Test
    public void testProportionalSetSecondValue() throws Exception {
        Style st = getStyle(PROPORTIONAL_POINT);
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        ProportionalPoint uvp = new ProportionalPoint(ps);
        uvp.setSecondValue(250.0);
        assertTrue( uvp.getSecondValue()== 250.0);
    }

    @Test
    public void testAnalyzeBackDefaultPropPoint() throws Exception {
            ProportionalPoint pp = new ProportionalPoint();
            PointSymbolizer ps = (PointSymbolizer) pp.getSymbolizer();
            Legend leg = (Legend) new PointSymbolizerAnalyzer(ps).getLegend();
            assertTrue(leg instanceof ProportionalPoint);
    }

    @Test
    public void testGetFieldName() throws Exception{
        Style st = getStyle(PROPORTIONAL_POINT);//PTOT99
        PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
        ProportionalPoint uvp = new ProportionalPoint(ps);
        assertTrue(uvp.getLookupFieldName().equals("PTOT99"));
    }

    @Test
    public void testValidateInterpolate() throws Exception {
        PointSymbolizerAnalyzer psa = new PointSymbolizerAnalyzer(new PointSymbolizer());
        Interpolate2Real rp = new Interpolate2Real(new RealLiteral(2.0));
        assertFalse(psa.validateInterpolateForProportionalPoint(rp));
    }

    @Test
    public void testValidateInterpolate2() throws Exception {
        PointSymbolizerAnalyzer psa = new PointSymbolizerAnalyzer(new PointSymbolizer());
        Interpolate2Real ir = new Interpolate2Real(new RealLiteral(0));
        InterpolationPoint<RealParameter> ip =new InterpolationPoint<RealParameter>();
        ip.setData(0);
        ip.setValue(new RealLiteral(0));
        ir.addInterpolationPoint(ip);
        InterpolationPoint<RealParameter> ip2 =new InterpolationPoint<RealParameter>();
        ip2.setData(1);
        ip2.setValue(new RealLiteral(1));
        ir.addInterpolationPoint(ip2);
        //We must not forget our interpolation function...
        //It's empty ! Don't forget to fill it later !
        RealFunction rf = new RealFunction(RealFunction.Operators.SQRT);
        try{
            rf.addOperand(new RealAttribute());
        } catch(ParameterException pe){
            throw new IllegalStateException("We've just failed at giving"
                        + "an operand to a log. Something must be going REALLY wrong...", pe);
        }
        ir.setLookupValue(rf);
        assertTrue(psa.validateInterpolateForProportionalPoint(ir));
    }

    @Test
    public void testCreateAndAnalyze() throws Exception{
        ProportionalPoint pp = new ProportionalPoint();
        PointSymbolizer ps = (PointSymbolizer) pp.getSymbolizer();
        PointSymbolizerAnalyzer psa = new PointSymbolizerAnalyzer(ps);
        assertTrue(psa.getLegend() instanceof ProportionalPoint);
    }

    @Test
    public void testCreateAndAnalyze2() throws Exception{
        ProportionalPoint pp = new ProportionalPoint();
        pp.setOnVertex();
        PointSymbolizer ps = (PointSymbolizer) pp.getSymbolizer();
        PointSymbolizerAnalyzer psa = new PointSymbolizerAnalyzer(ps);
        assertTrue(psa.getLegend() instanceof ProportionalPoint);
    }

    @Test
    public void testValidateInterpolate3() throws Exception {
        PointSymbolizerAnalyzer psa = new PointSymbolizerAnalyzer(new PointSymbolizer());
        Interpolate2Real ir = new Interpolate2Real(new RealLiteral(0));
        InterpolationPoint<RealParameter> ip =new InterpolationPoint<RealParameter>();
        ip.setData(0);
        ip.setValue(new RealLiteral(0));
        ir.addInterpolationPoint(ip);
        InterpolationPoint<RealParameter> ip2 =new InterpolationPoint<RealParameter>();
        ip2.setData(1);
        ip2.setValue(new RealLiteral(1));
        ir.addInterpolationPoint(ip2);
        //We must not forget our interpolation function...
        //It's empty ! Don't forget to fill it later !
        RealFunction rf = new RealFunction(RealFunction.Operators.LOG);
        try{
            rf.addOperand(new RealAttribute());
        } catch(ParameterException pe){
            throw new IllegalStateException("We've just failed at giving"
                        + "an operand to a log. Something must be going REALLY wrong...", pe);
        }
        ir.setLookupValue(rf);
        assertFalse(psa.validateInterpolateForProportionalPoint(ir));
    }
}
