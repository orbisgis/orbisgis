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

import java.awt.Color;
import java.io.FileInputStream;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import net.opengis.se._2_0.core.StyleType;
import static org.junit.Assert.*;
import org.junit.Test;
import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.se.AreaSymbolizer;
import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.color.ColorParameter;
import org.orbisgis.core.renderer.se.parameter.color.Recode2Color;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.analyzer.parameter.ColorParameterAnalyzer;
import org.orbisgis.legend.structure.categorize.Categorize2ColorLegend;
import org.orbisgis.legend.structure.literal.ColorLiteralLegend;
import org.orbisgis.legend.structure.recode.RecodedColor;

/**
 * This tests check that we are able to analyze color nodes properly.
 * @author Alexis Guéganno
 */
public class ColorAnalyzerTest extends AnalyzerTest{

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
                        new FileInputStream(COLOR_RECODE));
                Style st = new Style(ftsElem, null);
                LineSymbolizer ls = (LineSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                PenStroke ps = (PenStroke)ls.getStroke();
                SolidFill sf = (SolidFill) ps.getFill();
                ColorParameter cp = sf.getColor();
                ColorParameterAnalyzer cpa = new ColorParameterAnalyzer(cp);
                assertTrue(cpa.getLegend() instanceof RecodedColor);
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

        @Test
        public void testRecodeWrapperGetter() throws Exception{
                Recode2Color r2 = getRecode2Color();
                RecodedColor r2d2 = new RecodedColor(r2);
                assertTrue(r2d2.getKey(0).equals("1"));
                assertTrue(r2d2.getKey(1).equals("2.5"));
                assertTrue(r2d2.getItemValue(0).equals(new Color(0x22, 0x33, 0x44)));
                assertTrue(r2d2.getItemValue(1).equals(new Color(0xAA, 0x17, 0xB4)));
                assertTrue(r2d2.getItemValue(2).equals(new Color(0xCC, 0x00, 0x99)));
                assertTrue(r2d2.getItemValue("1").equals(new Color(0x22, 0x33, 0x44)));
                assertTrue(r2d2.getItemValue("2.5").equals(new Color(0xAA, 0x17, 0xB4)));
                assertTrue(r2d2.getItemValue("20").equals(new Color(0xCC, 0x00, 0x99)));
                assertTrue(r2d2.getLookupFieldName().equals("PREC_ALTI"));

        }

        @Test
        public void testRecodeWrapperSetters() throws Exception {
                Recode2Color r2 = getRecode2Color();
                RecodedColor r2d2 = new RecodedColor(r2);
                r2d2.setKey(0, "youhou ?");
                r2d2.setKey(1, ":-)");
                assertTrue(r2d2.getKey(0).equals("youhou ?"));
                assertTrue(r2d2.getKey(1).equals(":-)"));
                assertTrue(r2d2.getItemValue(0).equals(new Color(0x22, 0x33, 0x44)));
                assertTrue(r2d2.getItemValue(1).equals(new Color(0xAA, 0x17, 0xB4)));
                assertTrue(r2d2.getItemValue(2).equals(new Color(0xCC, 0x00, 0x99)));
                assertTrue(r2d2.getItemValue("youhou ?").equals(new Color(0x22, 0x33, 0x44)));
                assertTrue(r2d2.getItemValue(":-)").equals(new Color(0xAA, 0x17, 0xB4)));
                assertNull(r2d2.getItemValue("0"));
                assertNull(r2d2.getItemValue("50.0"));
        }

        @Test
        public void testRecodeWrapperAddValue() throws Exception {
                Recode2Color r2 = getRecode2Color();
                RecodedColor r2d2 = new RecodedColor(r2);
                r2d2.addItem("1", new Color(0x22, 0x33, 0x45));
                assertTrue(r2d2.getItemValue(0).equals(new Color(0x22, 0x33, 0x45)));
                assertTrue(r2d2.getItemValue("1").equals(new Color(0x22, 0x33, 0x45)));
                assertTrue(r2.getMapItemValue(0).getColor(null).equals(new Color(0x22, 0x33, 0x45)));
                assertTrue(r2.getMapItemValue("1").getColor(null).equals(new Color(0x22, 0x33, 0x45)));
                r2d2.addItem("50.0", new Color(0x22, 0x33, 0x46));
                assertTrue(r2d2.size() == 5);
                assertTrue(r2.getNumMapItem() == 5);
                assertTrue(r2d2.getItemValue(4).equals(new Color(0x22, 0x33, 0x46)));
                assertTrue(r2d2.getItemValue("50.0").equals(new Color(0x22, 0x33, 0x46)));
                assertTrue(r2.getMapItemValue(4).getColor(null).equals(new Color(0x22, 0x33, 0x46)));
                assertTrue(r2.getMapItemValue("50.0").getColor(null).equals(new Color(0x22, 0x33, 0x46)));

        }

        @Test
        public void testColorRecodedFromLiteral() throws Exception {
                ColorLiteral sl = new ColorLiteral(new Color(0x5,0x5,0x5));
                RecodedColor rs = new RecodedColor(sl);
                assertTrue(rs.getParameter() == sl);
                assertTrue(rs.size()==0);
                assertTrue(rs.getFallbackValue().equals(new Color(0x5,0x5,0x5)));
                rs.addItem("r",new Color(0x15,0x15,0x15));
                assertTrue(rs.getItemValue("r").equals(new Color(0x15,0x15,0x15)));
                assertTrue(rs.size()==1);
                assertTrue(rs.getFallbackValue().equals(new Color(0x5,0x5,0x5)));
                assertFalse(rs.getParameter() == sl);
        }

        @Test
        public void testColorLiteralFromRecode() throws Exception{
                Recode2Color r2 = getRecode2Color();
                RecodedColor r2d2 = new RecodedColor(r2);
                assertTrue(r2d2.getFallbackValue().equals(new Color(0x33,0x55,0x66)));
                assertTrue(r2d2.size() == 4);
                r2d2.removeItem(0);
                assertTrue(r2d2.size() == 3);
                r2d2.removeItem(0);
                assertTrue(r2d2.size() == 2);
                r2d2.removeItem(0);
                assertTrue(r2d2.size() == 1);
                r2d2.removeItem(0);
                assertTrue(r2d2.size() == 0);
                assertTrue(r2d2.getParameter() instanceof ColorLiteral);
                assertTrue(r2d2.getFallbackValue().equals(new Color(0x33,0x55,0x66)));
        }

        @Test
        public void testColorRecodeFallback() throws Exception{
                Recode2Color r2 = getRecode2Color();
                RecodedColor r2d2 = new RecodedColor(r2);
                assertTrue(r2d2.getFallbackValue().equals(new Color(0x33,0x55,0x66)));
                r2d2.setFallbackValue(Color.CYAN);
                assertTrue(r2d2.getFallbackValue().equals(Color.CYAN));
                assertTrue(r2.getFallbackValue().getColor(null).equals(Color.CYAN));
        }

        @Test
        public void testColorRecodeFallbackLiteral() throws Exception {
                ColorLiteral sl = new ColorLiteral(new Color(0x5,0x5,0x5));
                RecodedColor rs = new RecodedColor(sl);
                assertTrue(rs.getFallbackValue().equals(new Color(0x5,0x5,0x5)));
                rs.setFallbackValue(Color.CYAN);
                assertTrue(rs.getFallbackValue().equals(Color.CYAN));
                assertTrue(sl.getColor(null).equals(Color.CYAN));
        }

        private Recode2Color getRecode2Color() throws Exception {
                //We retrieve a Recode from an external file...
                Unmarshaller u = Services.JAXBCONTEXT.createUnmarshaller();
                JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                        new FileInputStream(COLOR_RECODE));
                Style st = new Style(ftsElem, null);
                LineSymbolizer ls = (LineSymbolizer) (st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0));
                PenStroke ps = (PenStroke)ls.getStroke();
                SolidFill sf = (SolidFill) ps.getFill();
                return (Recode2Color)sf.getColor();

        }

}
