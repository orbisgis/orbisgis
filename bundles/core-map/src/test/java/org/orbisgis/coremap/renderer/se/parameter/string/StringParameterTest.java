/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.coremap.renderer.se.parameter.string;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import net.opengis.se._2_0.core.StyleType;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.orbisgis.coremap.renderer.se.PointSymbolizer;
import org.orbisgis.coremap.renderer.se.Style;
import org.orbisgis.coremap.renderer.se.graphic.PointTextGraphic;

/**
 *
 * @author Alexis Guéganno
 */
public class StringParameterTest {

        @Test
        public void testMarshallAndUnmarshallCategorize() throws Exception {
                String xml = StringParameterTest.class.getResource("../../stringCategorize.se").getFile();
                Unmarshaller u = org.orbisgis.coremap.map.JaxbContainer.JAXBCONTEXT.createUnmarshaller();
                JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                        new FileInputStream(xml));
                Marshaller m = org.orbisgis.coremap.map.JaxbContainer.JAXBCONTEXT.createMarshaller();
                m.marshal(ftsElem, new FileOutputStream("2routput.se"));
                Style st = new Style(ftsElem, null);
                JAXBElement<StyleType> elem = st.getJAXBElement();
                m.marshal(elem, new FileOutputStream("c2routput.se"));
                assertTrue(true);

        }

        @Test
        public void testMarshallAndUnmarshallRecode() throws Exception {
                String xml = StringParameterTest.class.getResource("../../stringRecode.se").getFile();
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
        public void testNumberFormat() throws Exception {
                String xml = StringParameterTest.class.getResource("../../numberFormat.se").getFile();
                Unmarshaller u = org.orbisgis.coremap.map.JaxbContainer.JAXBCONTEXT.createUnmarshaller();
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
                Marshaller m = org.orbisgis.coremap.map.JaxbContainer.JAXBCONTEXT.createMarshaller();
                m.marshal(elem, new FileOutputStream("c2routput.se"));
                assertTrue(true);
        }

        @Test
        public void testFormatNumberDecimalPoint() throws Exception {
            String xml = StringParameterTest.class.getResource("../../numberFormat.se").getFile();
                Unmarshaller u = org.orbisgis.coremap.map.JaxbContainer.JAXBCONTEXT.createUnmarshaller();
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
                String xml = StringParameterTest.class.getResource("../../numberFormat.se").getFile();
                Unmarshaller u = org.orbisgis.coremap.map.JaxbContainer.JAXBCONTEXT.createUnmarshaller();
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

        @Test
        public void testFormatNumberPattern() throws Exception {
                String xml = StringParameterTest.class.getResource("../../numberFormat.se").getFile();
                Unmarshaller u = org.orbisgis.coremap.map.JaxbContainer.JAXBCONTEXT.createUnmarshaller();
                JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                        new FileInputStream(xml));
                Style st = new Style(ftsElem, null);
                PointTextGraphic ptg = (PointTextGraphic) ((PointSymbolizer) st.getRules().get(0).getCompositeSymbolizer().
                        getSymbolizerList().get(0)).getGraphicCollection().getGraphic(0);
                StringParameter sp = ptg.getPointLabel().getLabel().getText();
                Number2String ns = (Number2String) sp;
                String ret = ns.getValue(null, 0);
                assertTrue(ns.getFormattingPattern().equals("###,000.##"));
                assertTrue(ret.equals("12+345:6"));
                ns.setFormattingPattern("00000000.0");
                ret = ns.getValue(null, 0);
                assertTrue(ns.getFormattingPattern().equals("00000000.0"));
                assertTrue(ret.equals("00012345:6"));
                ns.setFormattingPattern("bonjour");
                assertTrue(true);
        }

        @Test
        public void testStringConcatenateUnmarshallMarshall() throws Exception {
                String xml = StringParameterTest.class.getResource("../../concatenateString.se").getFile();
                Unmarshaller u = org.orbisgis.coremap.map.JaxbContainer.JAXBCONTEXT.createUnmarshaller();
                JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                        new FileInputStream(xml));
                Marshaller m = org.orbisgis.coremap.map.JaxbContainer.JAXBCONTEXT.createMarshaller();
                m.marshal(ftsElem, new FileOutputStream("concatoutput.se"));
                Style st = new Style(ftsElem, null);
                PointTextGraphic ptg = (PointTextGraphic) ((PointSymbolizer) st.getRules().get(0).getCompositeSymbolizer().
                        getSymbolizerList().get(0)).getGraphicCollection().getGraphic(0);
                StringParameter sp = ptg.getPointLabel().getLabel().getText();
                assertTrue(sp instanceof StringConcatenate);
                JAXBElement<StyleType> elem = st.getJAXBElement();
                m.marshal(elem, new FileOutputStream("concatoutput.se"));
                assertTrue(true);
        }

        @Test
        public void testStringConcatenateSizeAndAdd() throws Exception {
                String xml = StringParameterTest.class.getResource("../../concatenateString.se").getFile();
                Unmarshaller u = org.orbisgis.coremap.map.JaxbContainer.JAXBCONTEXT.createUnmarshaller();
                JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                        new FileInputStream(xml));
                Style st = new Style(ftsElem, null);
                PointTextGraphic ptg = (PointTextGraphic) ((PointSymbolizer) st.getRules().get(0).getCompositeSymbolizer().
                        getSymbolizerList().get(0)).getGraphicCollection().getGraphic(0);
                StringParameter sp = ptg.getPointLabel().getLabel().getText();
                StringConcatenate conc = (StringConcatenate) sp;
                assertTrue(conc.size()==2);
                conc.add(new StringLiteral("third parameter"));
                assertTrue(conc.size()==3);
                conc.add(1,new StringLiteral("fourth parameter"));
                assertTrue(conc.size()==4);
                assertTrue(conc.get(1).getValue(null, 0).equals("fourth parameter"));
                conc.set(1,new StringLiteral("fifth parameter"));
                assertTrue(conc.size()==4);
                assertTrue(conc.get(1).getValue(null, 0).equals("fifth parameter"));
                JAXBElement<StyleType> elem = st.getJAXBElement();
                Marshaller m = org.orbisgis.coremap.map.JaxbContainer.JAXBCONTEXT.createMarshaller();
                m.marshal(elem, new FileOutputStream("concatoutput.se"));
                JAXBElement<StyleType> ftsElemBis = (JAXBElement<StyleType>) u.unmarshal(
                        new FileInputStream("concatoutput.se"));
                st = new Style(ftsElemBis, null);
                ptg = (PointTextGraphic) ((PointSymbolizer) st.getRules().get(0).getCompositeSymbolizer().
                        getSymbolizerList().get(0)).getGraphicCollection().getGraphic(0);
                sp = ptg.getPointLabel().getLabel().getText();
                conc = (StringConcatenate) sp;
                assertTrue(conc.size()==4);
        }

        @Test
        public void testBasicJAXBSerialization() throws Exception {
                StringAttribute sa = new StringAttribute("bonjour");
                JAXBElement<?> je = sa.getJAXBExpressionType();
                sa = new StringAttribute((JAXBElement<String>) je);
                assertTrue(sa.getColumnName().equals("bonjour"));
        }

        @Test
        public void testStringLiteralRestriction() throws Exception {
                StringLiteral sl = new StringLiteral("youhou");
                sl.setRestrictionTo(new String[]{"youhou","yaha"});
                sl.setValue("yaha");
                try{
                        sl.setValue("bonjour");
                        fail();
                } catch(InvalidString is){
                        assertTrue(true);
                }
        }

        @Test
        public void testStringLiteralSetNotCompatibleRestriction() throws Exception {
                StringLiteral sl = new StringLiteral("youhou");
                try {
                        sl.setRestrictionTo(new String[]{"yoho","yaha"});
                        fail();
                } catch(InvalidString is){
                        assertTrue(true);
                }
        }

        @Test
        public void testGetChildrenLiteral() throws Exception {
                StringLiteral sl = new StringLiteral("youhou");
                assertTrue(sl.getChildren().isEmpty());
        }

        @Test
        public void testGetChildrenConcatenate() throws Exception {
                StringConcatenate conc = getConcatenate();
                assertTrue(conc.getChildren().size() == 2);
        }

        private StringConcatenate getConcatenate() throws Exception {
                String xml = StringParameterTest.class.getResource("../../concatenateString.se").getFile();
                Unmarshaller u = org.orbisgis.coremap.map.JaxbContainer.JAXBCONTEXT.createUnmarshaller();
                JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                        new FileInputStream(xml));
                Style st = new Style(ftsElem, null);
                PointTextGraphic ptg = (PointTextGraphic) ((PointSymbolizer) st.getRules().get(0).getCompositeSymbolizer().
                        getSymbolizerList().get(0)).getGraphicCollection().getGraphic(0);
                StringParameter sp = ptg.getPointLabel().getLabel().getText();
                return (StringConcatenate) sp;
        }
}
