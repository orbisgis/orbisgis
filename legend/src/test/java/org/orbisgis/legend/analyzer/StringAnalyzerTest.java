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

import net.opengis.se._2_0.core.StyleType;
import org.junit.Test;
import org.orbisgis.core.Services;
import org.orbisgis.coremap.renderer.se.PointSymbolizer;
import org.orbisgis.coremap.renderer.se.Style;
import org.orbisgis.coremap.renderer.se.graphic.PointTextGraphic;
import org.orbisgis.coremap.renderer.se.parameter.string.Recode2String;
import org.orbisgis.coremap.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.structure.recode.RecodedString;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Alexis Guéganno
 */
public class StringAnalyzerTest extends AnalyzerTest {

        private Recode2String getRecode2String() throws Exception {
                File xml = new File(STRING_RECODE);
                Unmarshaller u = org.orbisgis.coremap.map.JaxbContainer.JAXBCONTEXT.createUnmarshaller();
                JAXBElement<StyleType> st = (JAXBElement<StyleType>) u.unmarshal(xml);
                Style style = new Style(st, null);
                PointSymbolizer ps = (PointSymbolizer) style.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
                PointTextGraphic ptg = (PointTextGraphic) ps.getGraphicCollection().getGraphic(0);
                return (Recode2String)  ptg.getPointLabel().getLabel().getText();

        }

        @Test
        public void testStringRecodedAddValue() throws Exception {
                Recode2String r2 = getRecode2String();
                RecodedString r2d2 = new RecodedString(r2);
                assertTrue(r2d2.size() == 3);
                r2d2.addItem("2", "Pas large");
                assertTrue(r2d2.size() == 3);
                assertTrue(r2d2.getItemValue(0).equals("Pas large"));
                assertTrue(r2d2.getItemValue("2").equals("Pas large"));
                assertTrue(r2.getMapItemValue(0).getValue(null).equals("Pas large"));
                assertTrue(r2.getMapItemValue("2").getValue(null).equals("Pas large"));
                r2d2.addItem("50.0", "75.0");
                assertTrue(r2d2.size() == 4);
                assertTrue(r2.getNumMapItem() == 4);
                assertTrue(r2d2.getItemValue(3).equals("75.0"));
                assertTrue(r2d2.getItemValue("50.0").equals("75.0"));
                assertTrue(r2.getMapItemValue(3).getValue(null).equals("75.0"));
                assertTrue(r2.getMapItemValue("50.0").getValue(null).equals("75.0"));
        }

        @Test
        public void testStringRecodedFromLiteral() throws Exception {
                StringLiteral sl = new StringLiteral("bonjour");
                RecodedString rs = new RecodedString(sl);
                assertTrue(rs.getParameter() == sl);
                assertTrue(rs.size()==0);
                assertTrue(rs.getFallbackValue().equals("bonjour"));
                rs.addItem("r","s");
                assertTrue(rs.getItemValue("r").equals("s"));
                assertTrue(rs.size()==1);
                assertTrue(rs.getFallbackValue().equals("bonjour"));
                assertFalse(rs.getParameter() == sl);
        }

        @Test
        public void testSetFallbackRecoded() throws Exception {
            Recode2String r2 = getRecode2String();
            RecodedString r2d2 = new RecodedString(r2);
            assertTrue(r2d2.getFallbackValue().equals("Road"));
            r2d2.setFallbackValue("Route");
            assertTrue(r2d2.getFallbackValue().equals("Route"));
            assertTrue(r2.getFallbackValue().getValue(null).equals("Route"));
        }

        @Test
        public void testSetFallbackRecodedLiteral() throws Exception {
            StringLiteral sl = new StringLiteral("bonjour");
            RecodedString rs = new RecodedString(sl);
            assertTrue(rs.getFallbackValue().equals("bonjour"));
            rs.setFallbackValue("Route");
            assertTrue(rs.getFallbackValue().equals("Route"));
            assertTrue(sl.getValue(null).equals("Route"));
        }

        @Test
        public void testStringLiteralFromRecode() throws Exception{
                Recode2String r2 = getRecode2String();
                RecodedString r2d2 = new RecodedString(r2);
                assertTrue(r2d2.size() == 3);
                r2d2.removeItem(0);
                assertTrue(r2d2.size() == 2);
                r2d2.removeItem(0);
                assertTrue(r2d2.size() == 1);
                r2d2.removeItem(0);
                assertTrue(r2d2.size() == 0);
                assertTrue(r2d2.getParameter() instanceof StringLiteral);
                assertTrue(r2d2.getFallbackValue().equals("Road"));
        }
}
