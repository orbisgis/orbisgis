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

import java.awt.Color;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import net.opengis.se._2_0.core.AreaSymbolizerType;
import net.opengis.se._2_0.core.CategorizeType;
import net.opengis.se._2_0.core.SolidFillType;
import net.opengis.se._2_0.core.StyleType;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;

/**
 *
 * @author Maxence Laurent
 */
public class Categorize2ColorTest {

    protected Categorize2Color categorize;

    protected ColorParameter class1;
    protected ColorParameter class2;
    protected ColorParameter class3;
    protected ColorParameter class4;

    protected RealLiteral t1;
    protected RealLiteral t2;
    protected RealLiteral t3;
    protected RealLiteral t4;
    protected RealLiteral t5;

    protected ColorLiteral fallback;

    @Before
    public void setUp() throws Exception {

        fallback = new ColorLiteral();

        class1 = new ColorLiteral();
        class2 = new ColorLiteral();
        class3 = new ColorLiteral();
        class4 = new ColorLiteral();

        t1 = new RealLiteral(100.0);
        t2 = new RealLiteral(200.0);
        t3 = new RealLiteral(50.0);
        t4 = new RealLiteral(75.0);
        t5 = new RealLiteral(300.0);

        categorize = new Categorize2Color(class1, fallback, new RealLiteral());
    }

    @Test
    public void testConstructor(){
        assertTrue(categorize.getNumClasses() == 1);
        try {
            assertTrue(categorize.get(0).getColor(null, -1) == class1.getColor(null, -1));
        } catch (ParameterException ex) {
            Logger.getLogger(Categorize2ColorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testAddClasses(){
        try {
            //We first have ;
            // -INF -> class1
            // t1 -> class2
            categorize.put(t1, class2);
            assertTrue(categorize.getNumClasses() == 2);
            assertTrue(categorize.get(0).getColor(null, -1) == class1.getColor(null, -1));
            assertTrue(categorize.get(1).getColor(null, -1) == class2.getColor(null, -1));
            categorize.put(t2, class3);
            // -INF -> class1
            // t1 -> class2
            // t2 -> class3
            assertTrue(categorize.getNumClasses() == 3);
            assertTrue(categorize.get(0).getColor(null, -1) == class1.getColor(null, -1));
            assertTrue(categorize.get(1).getColor(null, -1) == class2.getColor(null, -1));
            assertTrue(categorize.get(2).getColor(null, -1) == class3.getColor(null, -1));
            categorize.put(t3, class4);
            // -INF -> class1
            // t3 -> class4
            // t1 -> class2
            // t2 -> class3
            assertTrue(categorize.getNumClasses() == 4);
            assertTrue(categorize.get(0).getColor(null, -1) == class1.getColor(null, -1));
            assertTrue(categorize.get(2).getColor(null, -1) == class2.getColor(null, -1));
            assertTrue(categorize.get(3).getColor(null, -1) == class3.getColor(null, -1));
            assertTrue(categorize.get(1).getColor(null, -1) == class4.getColor(null, -1));
        } catch (ParameterException ex) {
            Logger.getLogger(Categorize2ColorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Test
    public void testSetThresholds(){
        try {
            // To retrieve classes...
            // -INF -> class1
            // t3 (50)  -> class4
            // t1 (100) -> class2
            // t2 (200) -> class3
            testAddClasses();
            categorize.setThreshold(2, t4);
            //We have replaced t1 with t4 and now have
            // -INF -> class1
            // t3 (50)  -> class4
            // t4 (75)  -> class2
            // t2 (200) -> class3
            assertTrue(categorize.getNumClasses() == 4);
            assertTrue(categorize.get(0).getColor(null, -1) == class1.getColor(null, -1));
            assertTrue(categorize.get(1).getColor(null, -1) == class4.getColor(null, -1));
            assertTrue(categorize.get(2).getColor(null, -1) == class2.getColor(null, -1));
            assertTrue(categorize.get(3).getColor(null, -1) == class3.getColor(null, -1));

            // Thresholds
            double first = categorize.getThreshold(0).getValue(null, -1);
            assertTrue(Double.isInfinite(first) && first < 0);
            assertEquals(categorize.getThreshold(1).getValue(null, -1), t3.getValue(null, -1));
            assertEquals(categorize.getThreshold(2).getValue(null, -1), t4.getValue(null, -1));
            assertEquals(categorize.getThreshold(3).getValue(null, -1), t2.getValue(null, -1));


            // Thresholds : 75, 100, 200, 500
            categorize.setThreshold(0, t5);
            //t5 is 500. We should obtain
            // t3 (50)  -> class4
            // t4 (75)  -> class2
            // t2 (200) -> class3
            // t5 (500) -> class1
            //but the first threshold must be -INF so we expect
            // -INF     -> class4
            // t4 (75)  -> class2
            // t2 (200) -> class3
            // t5 (500) -> class1
            assertTrue(categorize.getNumClasses() == 4);
            assertTrue(categorize.get(0).getColor(null, -1) == class4.getColor(null, -1));
            assertTrue(categorize.get(1).getColor(null, -1) == class2.getColor(null, -1));
            assertTrue(categorize.get(2).getColor(null, -1) == class3.getColor(null, -1));
            assertTrue(categorize.get(3).getColor(null, -1) == class1.getColor(null, -1));

            first = categorize.getThreshold(0).getValue(null, -1);
            assertTrue(Double.isInfinite(first) && first < 0);
            assertEquals(categorize.getThreshold(1).getValue(null, -1), t4.getValue(null, -1));
            assertEquals(categorize.getThreshold(2).getValue(null, -1), t2.getValue(null, -1));
            assertEquals(categorize.getThreshold(3).getValue(null, -1), t5.getValue(null, -1));
        } catch (ParameterException ex) {
            Logger.getLogger(Categorize2ColorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    @Test
    public void testRemoveClasses(){
        try {
            testAddClasses();
            // -INF -> class1
            // t3 (50)  -> class4
            // t1 (100) -> class2
            // t2 (200) -> class3
            categorize.remove(0);
            // -INF     -> class4
            // t1 (100) -> class2
            // t2 (200) -> class3
            double first = categorize.getThreshold(0).getValue(null, -1);
            assertTrue(Double.isInfinite(first) && first < 0);
            assertTrue(categorize.getNumClasses() == 3);
            assertTrue(categorize.get(0).getColor(null, -1) == class4.getColor(null, -1));
            assertTrue(categorize.get(1).getColor(null, -1) == class2.getColor(null, -1));
            assertTrue(categorize.get(2).getColor(null, -1) == class3.getColor(null, -1));
            categorize.remove(2);
            // -INF     -> class4
            // t1 (100) -> class2
            first = categorize.getThreshold(0).getValue(null, -1);
            assertTrue(Double.isInfinite(first) && first < 0);
            assertTrue(categorize.getNumClasses() == 2);
            assertTrue(categorize.get(0).getColor(null, -1) == class4.getColor(null, -1));
            assertTrue(categorize.get(1).getColor(null, -1) == class2.getColor(null, -1));
            categorize.remove(1);
            // -INF     -> class4
            first = categorize.getThreshold(0).getValue(null, -1);
            assertTrue(Double.isInfinite(first) && first < 0);
            assertTrue(categorize.getNumClasses() == 1);
            assertTrue(categorize.get(0).getColor(null, -1) == class4.getColor(null, -1));
        } catch (ParameterException ex) {
            Logger.getLogger(Categorize2ColorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testNullGivesFallback() throws Exception {
            String fname = "youhou";
            categorize.setLookupValue(new RealAttribute(fname));
            categorize.put(t1, class2);
            HashMap<String, Value> hm = new HashMap<String, Value>();
            hm.put(fname, ValueFactory.createNullValue());
            Color cpm = categorize.getColor(hm);
            assertTrue(cpm == categorize.getFallbackValue().getColor(null, -1));
    }

    /**
     * test that e are able to build a Categorize2Color directly from a Jaxb categorize
     * structure.
     *
     * @throws Exception
     */
    @Test
    public void testFromJaxb() throws Exception {
        Categorize2Color c2c = getFromJaxb();
        assertTrue(c2c.get(0).getColor(null, 0).equals(new Color(0x11, 0x33, 0x55)));
        assertTrue(c2c.get(1).getColor(null, 0).equals(new Color(0xdd, 0x66, 0xee)));
        assertTrue(c2c.get(2).getColor(null, 0).equals(new Color(0xff, 0xaa, 0x99)));
    }

    @Test
    public void testGetKeyFromRange() throws Exception{
        Categorize2Color c2c = getFromJaxb();
        assertNull(c2c.getKey(-1));
        assertNull(c2c.getKey(4));
        assertNull(c2c.getKey(3));
        assertTrue(new RealLiteral(Double.NEGATIVE_INFINITY).equals(c2c.getKey(0)));
        assertTrue(new RealLiteral(70000).equals(c2c.getKey(1)));
        assertTrue(new RealLiteral(100000).equals(c2c.getKey(2)));
    }

    private Categorize2Color getFromJaxb() throws Exception {
        //We want to import it directly from the input file.
        String xmlRecode = "src/test/resources/org/orbisgis/core/renderer/se/colorCategorize.se";
        Unmarshaller u = Services.JAXBCONTEXT.createUnmarshaller();
        JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                    new FileInputStream(xmlRecode));
        AreaSymbolizerType ast = (AreaSymbolizerType)(ftsElem.getValue().getRule().get(0).getSymbolizer().getValue());
        SolidFillType pst = (SolidFillType)(ast.getFill().getValue());
        JAXBElement je = (JAXBElement)(pst.getColor().getContent().get(1));
        return new Categorize2Color((JAXBElement<CategorizeType>) je);
    }
}
