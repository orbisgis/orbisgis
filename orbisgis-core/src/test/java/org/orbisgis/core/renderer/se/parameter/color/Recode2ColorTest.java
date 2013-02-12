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
package org.orbisgis.core.renderer.se.parameter.color;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import net.opengis.se._2_0.core.*;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;

/**
 *
 * @author Maxence Laurent
 */
public class Recode2ColorTest {

    protected Recode2Color recode;

    String key1;
    String key2;
    String key3;
    String key4;
    String key5;

    ColorParameter c1;
    ColorLiteral c2;
    ColorLiteral c3;
    ColorLiteral c4;
    ColorLiteral c5;

    ColorLiteral fb;

    StringParameter lookup;

    @Before
    public void setUp() throws Exception {
        key1 = "k1";
        key2 = "k2";
        key3 = "k3";
        key4 = "k4";
        key5 = "k5";

        c1 = new ColorLiteral();
        c2 = new ColorLiteral();
        c3 = new ColorLiteral();
        c4 = new ColorLiteral();
        c5 = new ColorLiteral();
        
        fb = new ColorLiteral();

        lookup = new StringLiteral("hello");

        recode = new Recode2Color(fb, lookup);
    }

    @Test
    public void testAddMapItems(){
        try {
            recode.addMapItem(key1, c1);
            assertTrue(recode.getNumMapItem() == 1);
            assertTrue(recode.getMapItemKey(0).equals("k1"));
            assertTrue(recode.getMapItemValue(0).getColor(null, -1) == c1.getColor(null, -1));
            assertTrue(recode.getMapItemValue("k1").getColor(null, -1) == c1.getColor(null, -1));
            recode.addMapItem(key2, c2);
            assertTrue(recode.getNumMapItem() == 2);
            assertTrue(recode.getMapItemKey(0).equals("k1"));
            assertTrue(recode.getMapItemKey(1).equals("k2"));
            assertTrue(recode.getMapItemValue(1).getColor(null, -1) == c2.getColor(null, -1));
            assertTrue(recode.getMapItemValue("k2").getColor(null, -1) == c2.getColor(null, -1));
            recode.addMapItem(key3, c3);
            assertTrue(recode.getNumMapItem() == 3);
            assertTrue(recode.getMapItemKey(0).equals("k1"));
            assertTrue(recode.getMapItemKey(1).equals("k2"));
            assertTrue(recode.getMapItemKey(2).equals("k3"));
            assertTrue(recode.getMapItemValue(2).getColor(null, -1) == c3.getColor(null, -1));
            assertTrue(recode.getMapItemValue("k3").getColor(null, -1) == c3.getColor(null, -1));
        } catch (ParameterException ex) {
            Logger.getLogger(Recode2ColorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testSetMapKey(){
            recode.addMapItem(key1, c1);
            recode.addMapItem(key2, c2);
            recode.addMapItem(key3, c3);
            recode.setKey(1, "youhou");
            assertTrue(recode.getMapItemKey(1).equals("youhou"));
        
    }

    @Test
    public void testGetParameter(){
        try {
            testAddMapItems();
            recode.setLookupValue(new StringLiteral("k1"));

            assertTrue(recode.getColor(null, -1) == c1.getColor(null, -1));
            recode.setLookupValue(new StringLiteral("k2"));
            assertTrue(recode.getColor(null, -1) == c2.getColor(null, -1));
            recode.setLookupValue(new StringLiteral("k3"));
            assertTrue(recode.getColor(null, -1) == c3.getColor(null, -1));
        } catch (ParameterException ex) {
            Logger.getLogger(Recode2ColorTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(false);
        }
    }

    @Test
    public void testRemoveMapItem(){
        testAddMapItems();
        assertTrue(recode.getNumMapItem() == 3);
        recode.removeMapItem("k2");
        assertTrue(recode.getNumMapItem() == 2);
        recode.removeMapItem("k1");
        assertTrue(recode.getNumMapItem() == 1);
        recode.removeMapItem("k3");
        assertTrue(recode.getNumMapItem() == 0);
    }

    @Test
    public void testMarshalUnmarshal() throws Exception {
        String xml = "src/test/resources/org/orbisgis/core/renderer/se/colorRecode.se";
        Unmarshaller u = Services.JAXBCONTEXT.createUnmarshaller();
        JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                new FileInputStream(xml));
        Marshaller m = Services.JAXBCONTEXT.createMarshaller();
        m.marshal(ftsElem, new FileOutputStream("target/c2routput.se"));
        Style st = new Style(ftsElem, null);
        JAXBElement<StyleType> elem = st.getJAXBElement();
        m.marshal(elem, new FileOutputStream("target/c2routput.se"));
        //Let's search in the created JAXBElement
        StyleType stype = elem.getValue();
        RuleType rt = stype.getRule().get(0);
        LineSymbolizerType lst = ((LineSymbolizerType)(rt.getSymbolizer().getValue()));
        SolidFillType sf = ((SolidFillType)((PenStrokeType)lst.getStroke().getValue()).getFill().getValue());
        JAXBElement je = (JAXBElement) (sf.getColor().getContent().get(0));
        RecodeType rect = ((RecodeType)(je.getValue()));
        assertTrue(rect.getMapItem().size() == 4);
    }

    @Test
    public void testRecodeGetChildren() throws Exception {
        testAddMapItems();
        assertTrue(recode.getChildren().size()==4);
    }

}
