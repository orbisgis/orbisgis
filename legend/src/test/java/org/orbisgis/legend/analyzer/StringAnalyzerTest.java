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
import javax.xml.bind.JAXBException;
import net.opengis.se._2_0.core.StyleType;
import org.junit.Test;
import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.structure.literal.StringLiteralLegend;
import static org.junit.Assert.*;

/**
 *
 * @author Alexis Guéganno
 */
public class StringAnalyzerTest {

        @Test
        public void testLiteralString() throws Exception {
                String location = "src/test/resources/org/orbisgis/legend/simpleText.se";
                File xml = new File(location);
                Unmarshaller u = Services.JAXBCONTEXT.createUnmarshaller();
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
                Unmarshaller u = Services.JAXBCONTEXT.createUnmarshaller();
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
                Unmarshaller u = Services.JAXBCONTEXT.createUnmarshaller();
                JAXBElement<StyleType> st = (JAXBElement<StyleType>) u.unmarshal(xml);
                Style style = new Style(st, null);
                PointSymbolizer ps = (PointSymbolizer) style.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
                PointTextGraphic ptg = (PointTextGraphic) ps.getGraphicCollection().getGraphic(0);
                StringParameter sp = (StringParameter) ptg.getPointLabel().getLabel().getText();
                StringParameterAnalyzer spa = new StringParameterAnalyzer(sp);
                assertTrue(spa.getLegend() instanceof Recode2StringLegend);
        }
}
