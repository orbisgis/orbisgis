/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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

import org.orbisgis.legend.analyzer.function.InterpolationAnalyzer;
import org.orbisgis.legend.structure.interpolation.InterpolationLegend;
import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.legend.structure.interpolation.LinearInterpolationLegend;
import org.orbisgis.core.renderer.se.parameter.real.Interpolate2Real;
import org.orbisgis.legend.AnalyzerTest;
import javax.xml.bind.Unmarshaller;
import org.orbisgis.core.renderer.se.Style;
import net.opengis.se._2_0.core.StyleType;
import javax.xml.bind.JAXBElement;
import java.io.FileInputStream;
import org.junit.Test;
import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.parameter.real.RealFunction;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.structure.interpolation.SqrtInterpolationLegend;
import static org.junit.Assert.*;

/**
 * We test the good behaviour of the interpolation analyzer here. We will
 * partially rely on the files that can be found in the resources.
 * @author alexis
 */
public class InterpolationAnalyzerTest extends AnalyzerTest{

        private String xml = "src/test/resources/org/orbisgis/legend/proportionalSymbol.se";
        private String linearInterpo = "src/test/resources/org/orbisgis/legend/linearProportional.se";

        /**
         * We test that we are able to retrieve an interpolation made on the
         * square root of a numeric field.
         */
        @Test
        public void testFindSqrtInterp() throws Exception {
                //First we build the JAXB tree
                Unmarshaller u = Services.JAXBCONTEXT.createUnmarshaller();
                JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                        new FileInputStream(xml));
                Style st = new Style(ftsElem, null);
                PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                MarkGraphic mg = (MarkGraphic) (ps.getGraphicCollection().getGraphic(0));
                Interpolate2Real ir = (Interpolate2Real) mg.getViewBox().getWidth();
                //We make our analyze
                InterpolationAnalyzer ia = new InterpolationAnalyzer(ir);
                assertTrue(ia.getLegend() instanceof SqrtInterpolationLegend);
        }

        /**
         * We test that we are able to retrieve an interpolation made on
         * a numeric field.
         */
        @Test
        public void testInterpolationLinear() throws Exception {
                //First we build the JAXB tree
                Unmarshaller u = Services.JAXBCONTEXT.createUnmarshaller();
                JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                        new FileInputStream(linearInterpo));
                Style st = new Style(ftsElem, null);
                LineSymbolizer ls = (LineSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                PenStroke mg = (PenStroke) (ls.getStroke());
                Interpolate2Real ir = (Interpolate2Real) mg.getWidth();
                //We make our analyze
                InterpolationAnalyzer ia = new InterpolationAnalyzer(ir);
                assertTrue(ia.getLegend() instanceof LinearInterpolationLegend);
        }

        /**
         * We test that we are able to retrieve an interpolation made on
         * a numeric field.
         */
        @Test
        public void testInterpolationOnOtherFunction() throws Exception {
                //First we build the JAXB tree
                Unmarshaller u = Services.JAXBCONTEXT.createUnmarshaller();
                JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                        new FileInputStream(xml));
                Style st = new Style(ftsElem, null);
                PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                MarkGraphic mg = (MarkGraphic) (ps.getGraphicCollection().getGraphic(0));
                Interpolate2Real ir = (Interpolate2Real) mg.getViewBox().getWidth();
                RealFunction rf = (RealFunction)(ir.getLookupValue());
                RealFunction tb = new RealFunction("log");
                tb.addOperand(rf.getOperand(0));
                ir.setLookupValue(tb);
                //We make our analyze
                InterpolationAnalyzer ia = new InterpolationAnalyzer(ir);
                boolean b1 = ia.getLegend() instanceof InterpolationLegend;
                boolean b2 = (ia.getLegend() instanceof SqrtInterpolationLegend);
                assertTrue(b1
                        && !b2);
        }
}
