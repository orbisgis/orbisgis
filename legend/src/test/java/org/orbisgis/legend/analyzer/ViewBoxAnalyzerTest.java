/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.analyzer;

import java.io.FileInputStream;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import net.opengis.se._2_0.core.StyleType;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.structure.viewbox.BivariateProportionalViewBox;
import org.orbisgis.legend.structure.viewbox.ConstantViewBox;
import org.orbisgis.legend.structure.viewbox.MonovariateLinearVB;
import org.orbisgis.legend.structure.viewbox.MonovariateProportionalViewBox;

/**
 * Test that the ViewBoxAnalyzer works well.
 * @author alexis
 */
public class ViewBoxAnalyzerTest extends AnalyzerTest {
        
        private String xml  = "src/test/resources/org/orbisgis/legend/proportionalSymbol.se";
        private String xml2 = "src/test/resources/org/orbisgis/legend/lengthProportional.se";
        private String xml3 = "src/test/resources/org/orbisgis/legend/bivariateProportional.se";
        private String xml4 = "src/test/resources/org/orbisgis/legend/constantWKN.se";
        private String xml5 = "src/test/resources/org/orbisgis/legend/constant2DWKN.se";

        @Test
        public void testMonovariateProportionalVB() throws Exception {
                //First we build the JAXB tree
                Unmarshaller u = Services.JAXBCONTEXT.createUnmarshaller();
                JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                        new FileInputStream(xml));
                Style st = new Style(ftsElem, null);
                PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                MarkGraphic mg = (MarkGraphic) (ps.getGraphicCollection().getGraphic(0));
                ViewBoxAnalyzer vba = new ViewBoxAnalyzer(mg.getViewBox());
                assertTrue(vba.getLegend() instanceof MonovariateProportionalViewBox);
        }

        @Test
        public void testLinearProportionalVB() throws Exception {
                //First we build the JAXB tree
                Unmarshaller u = Services.JAXBCONTEXT.createUnmarshaller();
                JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                        new FileInputStream(xml2));
                Style st = new Style(ftsElem, null);
                PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                MarkGraphic mg = (MarkGraphic) (ps.getGraphicCollection().getGraphic(0));
                ViewBoxAnalyzer vba = new ViewBoxAnalyzer(mg.getViewBox());
                assertTrue(vba.getLegend() instanceof MonovariateLinearVB);

        }

        @Test
        public void testBivariateProp() throws Exception {
                //First we build the JAXB tree
                Unmarshaller u = Services.JAXBCONTEXT.createUnmarshaller();
                JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                        new FileInputStream(xml3));
                Style st = new Style(ftsElem, null);
                PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                MarkGraphic mg = (MarkGraphic) (ps.getGraphicCollection().getGraphic(0));
                ViewBoxAnalyzer vba = new ViewBoxAnalyzer(mg.getViewBox());
                assertTrue(vba.getLegend() instanceof BivariateProportionalViewBox);
        }

        @Test
        public void testConstantViewBox() throws Exception {
                Unmarshaller u = Services.JAXBCONTEXT.createUnmarshaller();
                JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                        new FileInputStream(xml4));
                Style st = new Style(ftsElem, null);
                PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                MarkGraphic mg = (MarkGraphic) (ps.getGraphicCollection().getGraphic(0));
                ViewBoxAnalyzer vba = new ViewBoxAnalyzer(mg.getViewBox());
                assertTrue(vba.getLegend() instanceof ConstantViewBox);
            
        }

        @Test
        public void testConstantViewBox2D() throws Exception {
                Unmarshaller u = Services.JAXBCONTEXT.createUnmarshaller();
                JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                        new FileInputStream(xml5));
                Style st = new Style(ftsElem, null);
                PointSymbolizer ps = (PointSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                MarkGraphic mg = (MarkGraphic) (ps.getGraphicCollection().getGraphic(0));
                ViewBoxAnalyzer vba = new ViewBoxAnalyzer(mg.getViewBox());
                assertTrue(vba.getLegend() instanceof ConstantViewBox);

        }
}
