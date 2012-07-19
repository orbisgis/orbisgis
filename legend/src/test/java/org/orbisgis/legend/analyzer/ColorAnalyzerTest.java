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
