/**
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
 * @author Alexis Guéganno
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
