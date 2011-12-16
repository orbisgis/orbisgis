/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.parameter.string;

import org.orbisgis.core.renderer.se.graphic.PointTextGraphic;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import net.opengis.se._2_0.core.StyleType;
import org.orbisgis.core.renderer.se.Style;
import java.io.FileOutputStream;
import javax.xml.bind.Marshaller;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.Locale;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBContext;
import org.orbisgis.core.AbstractTest;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author alexis
 */
public class StringParameterTest extends AbstractTest {

        @Test
        public void testMarshallAndUnmarshallCategorize() throws Exception {
                String xml = "src/test/resources/org/orbisgis/core/renderer/se/stringCategorize.se";
                JAXBContext jaxbContext = JAXBContext.newInstance(StyleType.class);
                Unmarshaller u = jaxbContext.createUnmarshaller();
                JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                        new FileInputStream(xml));
                Marshaller m = jaxbContext.createMarshaller();
                m.marshal(ftsElem, new FileOutputStream("target/c2routput.se"));
                Style st = new Style(ftsElem, null);
                JAXBElement<StyleType> elem = st.getJAXBElement();
                m.marshal(elem, new FileOutputStream("target/c2routput.se"));
                assertTrue(true);

        }

        @Test
        public void testMarshallAndUnmarshallRecode() throws Exception {
                String xml = "src/test/resources/org/orbisgis/core/renderer/se/stringRecode.se";
                JAXBContext jaxbContext = JAXBContext.newInstance(StyleType.class);
                Unmarshaller u = jaxbContext.createUnmarshaller();
                JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                        new FileInputStream(xml));
                Marshaller m = jaxbContext.createMarshaller();
                m.marshal(ftsElem, new FileOutputStream("target/c2routput.se"));
                Style st = new Style(ftsElem, null);
                JAXBElement<StyleType> elem = st.getJAXBElement();
                m.marshal(elem, new FileOutputStream("target/c2routput.se"));
                assertTrue(true);

        }

        @Test
        public void testNumberFormat() throws Exception {
                String xml = "src/test/resources/org/orbisgis/core/renderer/se/numberFormat.se";
                JAXBContext jaxbContext = JAXBContext.newInstance(StyleType.class);
                Unmarshaller u = jaxbContext.createUnmarshaller();
                JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                        new FileInputStream(xml));
                Style st = new Style(ftsElem, null);
                PointTextGraphic ptg = (PointTextGraphic) ((PointSymbolizer) st.getRules().get(0).getCompositeSymbolizer().
                        getSymbolizerList().get(0)).getGraphicCollection().getGraphic(0);
                StringParameter sp = ptg.getPointLabel().getLabel().getText();
                assertTrue(sp instanceof Number2String);
                Number2String ns = (Number2String) sp;
                String ret = ns.getValue(null, 0);
                assertTrue(ret.equals("12+345:6"));
                JAXBElement<StyleType> elem = st.getJAXBElement();
                Marshaller m = jaxbContext.createMarshaller();
                m.marshal(elem, new FileOutputStream("target/c2routput.se"));
                assertTrue(true);
        }

        @Test
        public void testFormatNumberDecimalPoint() throws Exception {
                String xml = "src/test/resources/org/orbisgis/core/renderer/se/numberFormat.se";
                JAXBContext jaxbContext = JAXBContext.newInstance(StyleType.class);
                Unmarshaller u = jaxbContext.createUnmarshaller();
                JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                        new FileInputStream(xml));
                Style st = new Style(ftsElem, null);
                PointTextGraphic ptg = (PointTextGraphic) ((PointSymbolizer) st.getRules().get(0).getCompositeSymbolizer().
                        getSymbolizerList().get(0)).getGraphicCollection().getGraphic(0);
                StringParameter sp = ptg.getPointLabel().getLabel().getText();
                Number2String ns = (Number2String) sp;
                String ret = ns.getValue(null, 0);
                assertTrue(ns.getDecimalPoint().equals(":"));
                assertTrue(ret.equals("12+345:6"));
                ns.setDecimalPoint("è");
                ret = ns.getValue(null, 0);
                assertTrue(ns.getDecimalPoint().equals("è"));
                assertTrue(ret.equals("12+345è6"));
                try{
                        ns.setDecimalPoint("youhou ?");
                        fail();
                } catch(IllegalArgumentException i){
                        assertTrue(true);
                }
        }

        @Test
        public void testFormatNumberGroupingSeparator() throws Exception {
                String xml = "src/test/resources/org/orbisgis/core/renderer/se/numberFormat.se";
                JAXBContext jaxbContext = JAXBContext.newInstance(StyleType.class);
                Unmarshaller u = jaxbContext.createUnmarshaller();
                JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                        new FileInputStream(xml));
                Style st = new Style(ftsElem, null);
                PointTextGraphic ptg = (PointTextGraphic) ((PointSymbolizer) st.getRules().get(0).getCompositeSymbolizer().
                        getSymbolizerList().get(0)).getGraphicCollection().getGraphic(0);
                StringParameter sp = ptg.getPointLabel().getLabel().getText();
                Number2String ns = (Number2String) sp;
                String ret = ns.getValue(null, 0);
                assertTrue(ns.getGroupingSeparator().equals("+"));
                assertTrue(ret.equals("12+345:6"));
                ns.setGroupingSeparator("!");
                ret = ns.getValue(null, 0);
                assertTrue(ns.getGroupingSeparator().equals("!"));
                assertTrue(ret.equals("12!345:6"));
                try{
                        ns.setGroupingSeparator("youhou ?");
                        fail();
                } catch(IllegalArgumentException i){
                        assertTrue(true);
                }
        }
        
}
