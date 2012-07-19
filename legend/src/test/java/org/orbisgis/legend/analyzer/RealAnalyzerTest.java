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

import org.orbisgis.legend.analyzer.parameter.RealParameterAnalyzer;
import org.orbisgis.legend.structure.recode.Recode2RealLegend;
import org.orbisgis.legend.structure.categorize.Categorize2RealLegend;
import org.orbisgis.core.renderer.se.AreaSymbolizer;
import java.io.FileInputStream;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import net.opengis.se._2_0.core.StyleType;
import org.junit.Test;
import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.fill.DensityFill;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.structure.interpolation.InterpolationLegend;
import org.orbisgis.legend.structure.literal.RealLiteralLegend;
import static org.junit.Assert.*;

/**
 * Class that test the good behaviour of the {@code RealParameterAnalyzer}.
 * @author Alexis Guéganno
 */
public class RealAnalyzerTest extends AnalyzerTest {
        
        private String xml = "src/test/resources/org/orbisgis/legend/proportionalSymbol.se";

        /**
         * Test that we are able to recognize a RealParameter that is an
         * instance of {@code Interpolate2Real} (whatever the interpolation
         * method is).
         * @throws Exception
         */
        @Test
        public void testInterpolate2Real() throws Exception {
                //First we build the JAXB tree

                Unmarshaller u = Services.JAXBCONTEXT.createUnmarshaller();
                JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                        new FileInputStream(xml));
                Style st = new Style(ftsElem, null);
                PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                MarkGraphic mg = (MarkGraphic) (ps.getGraphicCollection().getGraphic(0));
                RealParameter ir = mg.getViewBox().getWidth();
                RealParameterAnalyzer rpa = new RealParameterAnalyzer(ir);
                assertTrue(rpa.getLegend() instanceof InterpolationLegend);
                
        }

        @Test
        public void testRealLiteral() throws Exception {
                //Let's create the constant to analyze.
                RealLiteral rl = new RealLiteral(80.24);
                RealParameter rp = rl;
                RealParameterAnalyzer rpa = new RealParameterAnalyzer(rp);
                assertTrue(rpa.getLegend() instanceof RealLiteralLegend);
        }

        @Test
        public void testRealCategorize() throws Exception {
                String xmlCat = "src/test/resources/org/orbisgis/legend/density_hatch_classif.se";
                Unmarshaller u = Services.JAXBCONTEXT.createUnmarshaller();
                JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                        new FileInputStream(xmlCat));
                Style st = new Style(ftsElem, null);
                AreaSymbolizer as = (AreaSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                DensityFill df = (DensityFill) (as.getFill());
                RealParameter rp = df.getPercentageCovered();
                RealParameterAnalyzer rpa = new RealParameterAnalyzer(rp);
                assertTrue(rpa.getLegend() instanceof Categorize2RealLegend);
        }

        @Test
        public void testRealRecode() throws Exception {
                String xmlCat = "src/test/resources/org/orbisgis/legend/density_hatch_recode.se";
                Unmarshaller u = Services.JAXBCONTEXT.createUnmarshaller();
                JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                        new FileInputStream(xmlCat));
                Style st = new Style(ftsElem, null);
                AreaSymbolizer as = (AreaSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                DensityFill df = (DensityFill) (as.getFill());
                RealParameter rp = df.getHatchesOrientation();
                RealParameterAnalyzer rpa = new RealParameterAnalyzer(rp);
                assertTrue(rpa.getLegend() instanceof Recode2RealLegend);
        }

}
