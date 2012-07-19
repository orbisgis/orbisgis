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
