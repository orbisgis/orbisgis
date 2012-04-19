/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import javax.xml.bind.JAXBContext;
import net.opengis.se._2_0.core.StyleType;
import javax.xml.bind.JAXBElement;
import java.io.FileInputStream;
import org.junit.Test;
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
                JAXBContext jaxbContext = JAXBContext.newInstance(StyleType.class);
                Unmarshaller u = jaxbContext.createUnmarshaller();
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
                JAXBContext jaxbContext = JAXBContext.newInstance(StyleType.class);
                Unmarshaller u = jaxbContext.createUnmarshaller();
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
                JAXBContext jaxbContext = JAXBContext.newInstance(StyleType.class);
                Unmarshaller u = jaxbContext.createUnmarshaller();
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
