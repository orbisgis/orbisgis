/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.analyzer;

import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.legend.structure.fill.CategorizedSolidFillLegend;
import org.orbisgis.legend.structure.fill.RecodedSolidFillLegend;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import javax.xml.bind.Unmarshaller;
import net.opengis.se._2_0.core.StyleType;
import java.io.File;
import javax.xml.bind.JAXBElement;
import org.junit.Test;
import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.se.AreaSymbolizer;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.legend.structure.fill.ConstantSolidFillLegend;
import static org.junit.Assert.*;

/**
 *
 * @author alexis
 */
public class FillAnalyzerTest extends AnalyzerTest {

        @Test
        public void testConstantSolidFill() throws Exception{
                //We have some xml with a constant solidfill
                File xml = new File("src/test/resources/org/orbisgis/legend/colorCategorize.se");
                Unmarshaller u = Services.JAXBCONTEXT.createUnmarshaller();
                JAXBElement<StyleType> st = (JAXBElement<StyleType>) u.unmarshal(xml);
                Style style = new Style(st.getValue(), null);
                AreaSymbolizer as = (AreaSymbolizer) style.getRules().get(0).getCompositeSymbolizer().
                                getSymbolizerList().get(0);
                Fill fill = ((PenStroke)as.getStroke()).getFill();
                FillAnalyzer fa = new FillAnalyzer(fill);
                assertTrue(fa.getLegend() instanceof ConstantSolidFillLegend);

        }
        
        @Test
        public void testCategorizedSolidFill() throws Exception{
                //We have some xml with a categorized solidfill. And it's the same one
                //that has been used in the previous test :-p
                File xml = new File("src/test/resources/org/orbisgis/legend/colorCategorize.se");
                Unmarshaller u = Services.JAXBCONTEXT.createUnmarshaller();
                JAXBElement<StyleType> st = (JAXBElement<StyleType>) u.unmarshal(xml);
                Style style = new Style(st.getValue(), null);
                AreaSymbolizer as = (AreaSymbolizer) style.getRules().get(0).getCompositeSymbolizer().
                                getSymbolizerList().get(0);
                Fill fill = as.getFill();
                FillAnalyzer fa = new FillAnalyzer(fill);
                assertTrue(fa.getLegend() instanceof CategorizedSolidFillLegend);

        }

        @Test
        public void testRecodedSolidFill() throws Exception{
                //We have some xml with a constant solidfill
                File xml = new File("src/test/resources/org/orbisgis/legend/colorRecode.se");
                Unmarshaller u = Services.JAXBCONTEXT.createUnmarshaller();
                JAXBElement<StyleType> st = (JAXBElement<StyleType>) u.unmarshal(xml);
                Style style = new Style(st.getValue(), null);
                LineSymbolizer as = (LineSymbolizer) style.getRules().get(0).getCompositeSymbolizer().
                                getSymbolizerList().get(0);
                Fill fill = ((PenStroke)as.getStroke()).getFill();
                FillAnalyzer fa = new FillAnalyzer(fill);
                assertTrue(fa.getLegend() instanceof RecodedSolidFillLegend);

        }
}
