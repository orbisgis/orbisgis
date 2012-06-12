/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.analyzer;

import org.orbisgis.legend.analyzer.parameter.StringParameterAnalyzer;
import org.orbisgis.legend.structure.recode.Recode2StringLegend;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.util.ValidationEventCollector;
import org.orbisgis.legend.structure.categorize.Categorize2StringLegend;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;
import org.orbisgis.core.renderer.se.graphic.PointTextGraphic;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import net.opengis.se._2_0.core.StyleType;
import org.junit.Test;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.structure.literal.StringLiteralLegend;
import static org.junit.Assert.*;

/**
 *
 * @author alexis
 */
public class StringAnalyzerTest {

        @Test
        public void testLiteralString() throws Exception {
                String location = "src/test/resources/org/orbisgis/legend/simpleText.se";
                File xml = new File(location);
                JAXBContext jc = JAXBContext.newInstance(AnalyzerTest.JAXBCONTEXT);
                Unmarshaller u = jc.createUnmarshaller();
                JAXBElement<StyleType> st = (JAXBElement<StyleType>) u.unmarshal(xml);
                Style style = new Style(st, null);
                PointSymbolizer ps = (PointSymbolizer) style.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
                PointTextGraphic ptg = (PointTextGraphic) ps.getGraphicCollection().getGraphic(0);
                StringParameter sp = (StringParameter) ptg.getPointLabel().getLabel().getText();
                StringParameterAnalyzer spa = new StringParameterAnalyzer(sp);
                assertTrue(spa.getLegend() instanceof StringLiteralLegend);
        }

        @Test
        public void testCategorize() throws Exception {
                String location = "src/test/resources/org/orbisgis/legend/stringCategorize.se";
                File xml = new File(location);
                JAXBContext jc = JAXBContext.newInstance(AnalyzerTest.JAXBCONTEXT);
                Unmarshaller u = jc.createUnmarshaller();
                JAXBElement<StyleType> st = (JAXBElement<StyleType>) u.unmarshal(xml);
                Style style = new Style(st, null);
                PointSymbolizer ps = (PointSymbolizer) style.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
                PointTextGraphic ptg = (PointTextGraphic) ps.getGraphicCollection().getGraphic(0);
                StringParameter sp = (StringParameter) ptg.getPointLabel().getLabel().getText();
                StringParameterAnalyzer spa = new StringParameterAnalyzer(sp);
                assertTrue(spa.getLegend() instanceof Categorize2StringLegend);
        }

        @Test
        public void testRecode() throws Exception {
                String location = "src/test/resources/org/orbisgis/legend/stringRecode.se";
                File xml = new File(location);
                JAXBContext jc = JAXBContext.newInstance(AnalyzerTest.JAXBCONTEXT);
                Unmarshaller u = jc.createUnmarshaller();
                JAXBElement<StyleType> st = (JAXBElement<StyleType>) u.unmarshal(xml);
                Style style = new Style(st, null);
                PointSymbolizer ps = (PointSymbolizer) style.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
                PointTextGraphic ptg = (PointTextGraphic) ps.getGraphicCollection().getGraphic(0);
                StringParameter sp = (StringParameter) ptg.getPointLabel().getLabel().getText();
                StringParameterAnalyzer spa = new StringParameterAnalyzer(sp);
                assertTrue(spa.getLegend() instanceof Recode2StringLegend);
        }
}
