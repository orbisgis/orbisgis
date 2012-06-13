/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 * @author alexis
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
