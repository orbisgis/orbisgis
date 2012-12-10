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

import java.io.File;
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
import org.orbisgis.legend.structure.graphic.ConstantWKNLegend;
import org.orbisgis.legend.structure.graphic.ProportionalWKNLegend;

/**
 *
 * @author Alexis Guéganno
 */
public class GraphicAnalyzerTest extends AnalyzerTest {

    private String constant = "src/test/resources/org/orbisgis/legend/constantWKN.se";
    private String proportional = "src/test/resources/org/orbisgis/legend/proportionalSymbol.se";

    @Test
    public void testConstantWKN() throws Exception {
        File path = new File(constant);
        Unmarshaller u = Services.JAXBCONTEXT.createUnmarshaller();
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
        Unmarshaller u = Services.JAXBCONTEXT.createUnmarshaller();
        JAXBElement<StyleType> st = (JAXBElement<StyleType>) u.unmarshal(path);
        Style style = new Style(st.getValue(), null);
        //We retrieve the MarkGraphic
        PointSymbolizer ps = (PointSymbolizer) style.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        MarkGraphic mg = (MarkGraphic) ps.getGraphicCollection().getGraphic(0);
        MarkGraphicAnalyzer mga = new MarkGraphicAnalyzer(mg);
        assertTrue(mga.getLegend() instanceof ProportionalWKNLegend);
    }

}
