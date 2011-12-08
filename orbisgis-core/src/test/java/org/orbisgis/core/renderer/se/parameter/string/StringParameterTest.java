/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.parameter.string;

import net.opengis.se._2_0.core.StyleType;
import org.orbisgis.core.renderer.se.Style;
import java.io.FileOutputStream;
import javax.xml.bind.Marshaller;
import java.io.FileInputStream;
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
                String xml= "src/test/resources/org/orbisgis/core/renderer/se/stringCategorize.se";
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
                String xml= "src/test/resources/org/orbisgis/core/renderer/se/stringRecode.se";
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

}
