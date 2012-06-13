/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.analyzer;

import org.orbisgis.legend.analyzer.parameter.ColorParameterAnalyzer;
import org.orbisgis.legend.structure.categorize.Categorize2ColorLegend;
import org.orbisgis.core.renderer.se.AreaSymbolizer;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.core.renderer.se.Style;
import java.io.FileInputStream;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.StyleType;
import javax.xml.bind.Unmarshaller;
import org.orbisgis.core.renderer.se.parameter.color.ColorParameter;
import org.orbisgis.legend.AnalyzerTest;
import org.junit.Test;
import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.structure.literal.ColorLiteralLegend;
import org.orbisgis.legend.structure.recode.Recode2ColorLegend;
import static org.junit.Assert.*;

/**
 * This tests check that we are able to analyze color nodes properly.
 * @author alexis
 */
public class ColorAnalyzerTest extends AnalyzerTest{

        private String xmlRecode = "src/test/resources/org/orbisgis/legend/colorRecode.se";
        private String xmlCategorize = "src/test/resources/org/orbisgis/legend/colorCategorize.se";

        @Test
        public void testColorLiteral(){
                //We build a simple ColorLiteral
                ColorParameter cp = new ColorLiteral("#002233");
                ColorParameterAnalyzer cpa = new ColorParameterAnalyzer(cp);
                assertTrue(cpa.getLegend() instanceof ColorLiteralLegend);

        }

        @Test
        public void testColorRecode() throws Exception {
                //We retrieve a Recode from an external file...
                Unmarshaller u = Services.JAXBCONTEXT.createUnmarshaller();
                JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                        new FileInputStream(xmlRecode));
                Style st = new Style(ftsElem, null);
                LineSymbolizer ls = (LineSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                PenStroke ps = (PenStroke)ls.getStroke();
                SolidFill sf = (SolidFill) ps.getFill();
                ColorParameter cp = sf.getColor();
                ColorParameterAnalyzer cpa = new ColorParameterAnalyzer(cp);
                assertTrue(cpa.getLegend() instanceof Recode2ColorLegend);
        }

        @Test
        public void testColorCategorize() throws Exception {
                //We retrieve a Categorize from an external file...
                Unmarshaller u = Services.JAXBCONTEXT.createUnmarshaller();
                JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                        new FileInputStream(xmlCategorize));
                Style st = new Style(ftsElem, null);
                AreaSymbolizer as = (AreaSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                SolidFill sf = (SolidFill) as.getFill();
                ColorParameter cp = sf.getColor();
                ColorParameterAnalyzer cpa = new ColorParameterAnalyzer(cp);
                assertTrue(cpa.getLegend() instanceof Categorize2ColorLegend);
        }

}
