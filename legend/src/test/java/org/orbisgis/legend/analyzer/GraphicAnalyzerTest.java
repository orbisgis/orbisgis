/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.analyzer;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import net.opengis.se._2_0.core.StyleType;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.structure.graphic.ConstantWKNLegend;
import org.orbisgis.legend.structure.graphic.ProportionalWKNLegend;

/**
 *
 * @author alexis
 */
public class GraphicAnalyzerTest extends AnalyzerTest {

    private String constant = "src/test/resources/org/orbisgis/legend/constantWKN.se";
    private String proportional = "src/test/resources/org/orbisgis/legend/proportionalSymbol.se";

    @Test
    public void testConstantWKN() throws Exception {
        File path = new File(constant);
        JAXBContext context = JAXBContext.newInstance(StyleType.class);
        Unmarshaller u = context.createUnmarshaller();
        JAXBElement<StyleType> st = (JAXBElement<StyleType>) u.unmarshal(path);
        Style style = new Style(st.getValue(), null);
        //We retrieve the MarkGraphic
        PointSymbolizer ps = (PointSymbolizer) style.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        MarkGraphicAnalyzer mga = new MarkGraphicAnalyzer(mg);
        assertTrue(mga.getLegend() instanceof ConstantWKNLegend);
    }

    @Test
    public void testProportionalWKN() throws Exception {
        File path = new File(proportional);
        JAXBContext context = JAXBContext.newInstance(StyleType.class);
        Unmarshaller u = context.createUnmarshaller();
        JAXBElement<StyleType> st = (JAXBElement<StyleType>) u.unmarshal(path);
        Style style = new Style(st.getValue(), null);
        //We retrieve the MarkGraphic
        PointSymbolizer ps = (PointSymbolizer) style.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        MarkGraphicAnalyzer mga = new MarkGraphicAnalyzer(mg);
        assertTrue(mga.getLegend() instanceof ProportionalWKNLegend);
    }

}
