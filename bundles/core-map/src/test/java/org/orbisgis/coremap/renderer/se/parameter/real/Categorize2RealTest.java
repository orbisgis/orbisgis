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
package org.orbisgis.coremap.renderer.se.parameter.real;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import net.opengis.se._2_0.core.AreaSymbolizerType;
import net.opengis.se._2_0.core.CategorizeType;
import net.opengis.se._2_0.core.StyleType;
import net.opengis.se._2_0.thematic.DensityFillType;
import static org.junit.Assert.*;
import org.junit.Test;
import org.orbisgis.core.AbstractTest;
import org.orbisgis.core.Services;
import org.orbisgis.coremap.renderer.se.Style;

/**
 *
 * @author Alexis Guéganno
 */
public class Categorize2RealTest {
        
        private String xml = "../src/test/resources/org/orbisgis/core/renderer/se/Districts/density_hatch_classif.se";

        @Test
        public void testFromJAXB() throws Exception {
                Categorize2Real c2r = getCategorize();
                assertTrue(c2r.get(0).getValue(null, 0) == 0.3);
                assertTrue(c2r.get(1).getValue(null, 0) == 0.4);
                assertTrue(c2r.get(2).getValue(null, 0) == 0.45);
                assertTrue(c2r.get(3).getValue(null, 0) == 0.5);
                assertTrue(c2r.get(4).getValue(null, 0) == 0.55);
                assertTrue(c2r.get(5).getValue(null, 0) == 0.6);
                assertTrue(c2r.get(6).getValue(null, 0) == 0.7);
                assertTrue(c2r.get(7).getValue(null, 0) == 0.8);
        }

        @Test
        public void testMarshalAndUnmarshal() throws Exception {
                Unmarshaller u = org.orbisgis.coremap.map.JaxbContainer.JAXBCONTEXT.createUnmarshaller();
                JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                        new FileInputStream(xml));
                Marshaller m = org.orbisgis.coremap.map.JaxbContainer.JAXBCONTEXT.createMarshaller();
                m.marshal(ftsElem, new FileOutputStream("c2routput.se"));
                Style st = new Style(ftsElem, null);
                JAXBElement<StyleType> elem = st.getJAXBElement();
                m.marshal(elem, new FileOutputStream("c2routput.se"));
                assertTrue(true);
        }

        @Test
        public void testChlidren() throws Exception {
                Categorize2Real c2r = getCategorize();
                //We will have 16 children : 7 thresholds, 8 values and 1 lookup value
                assertTrue(c2r.getChildren().size() == 16);
        }

        private Categorize2Real getCategorize() throws Exception{
                Unmarshaller u = org.orbisgis.coremap.map.JaxbContainer.JAXBCONTEXT.createUnmarshaller();
                JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                        new FileInputStream(xml));
                AreaSymbolizerType ast = (AreaSymbolizerType) (ftsElem.getValue().getRule().get(0).getSymbolizer().getValue());
                DensityFillType dft = (DensityFillType) (ast.getFill().getValue());
                JAXBElement je = (JAXBElement) (dft.getPercentage().getContent().get(1));
                return new Categorize2Real((CategorizeType) je.getValue());

        }
}
