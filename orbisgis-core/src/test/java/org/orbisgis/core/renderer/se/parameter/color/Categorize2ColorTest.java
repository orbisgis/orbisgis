/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */



package org.orbisgis.core.renderer.se.parameter.color;

import java.awt.Color;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import junit.framework.TestCase;
import net.opengis.se._2_0.core.AreaSymbolizerType;
import net.opengis.se._2_0.core.CategorizeType;
import net.opengis.se._2_0.core.PenStrokeType;
import net.opengis.se._2_0.core.SolidFillType;
import net.opengis.se._2_0.core.StyleType;
import org.junit.Test;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;

/**
 *
 * @author maxence
 */
public class Categorize2ColorTest extends TestCase {

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

    public Categorize2ColorTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

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
        //categorize.addClass(t1, class2);
        //categorize.addClass(t2, class3);
        //categorize.addClass(t3, class4);

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testConstructor(){
        assertTrue(categorize.getNumClasses() == 1);
        try {
            assertTrue(categorize.getClassValue(0).getColor(null, -1) == class1.getColor(null, -1));
        } catch (ParameterException ex) {
            Logger.getLogger(Categorize2ColorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void testAddClasses(){
        try {
            categorize.addClass(t1, class2);
            System.out.println ("Num Classes:" + categorize.getNumClasses());

            assertTrue(categorize.getNumClasses() == 2);
            assertTrue(categorize.getClassValue(0).getColor(null, -1) == class1.getColor(null, -1));
            assertTrue(categorize.getClassValue(1).getColor(null, -1) == class2.getColor(null, -1));
            categorize.addClass(t2, class3);
            assertTrue(categorize.getNumClasses() == 3);
            assertTrue(categorize.getClassValue(0).getColor(null, -1) == class1.getColor(null, -1));
            assertTrue(categorize.getClassValue(1).getColor(null, -1) == class2.getColor(null, -1));
            assertTrue(categorize.getClassValue(2).getColor(null, -1) == class3.getColor(null, -1));
            categorize.addClass(t3, class4);
            assertTrue(categorize.getNumClasses() == 4);
            assertTrue(categorize.getClassValue(0).getColor(null, -1) == class1.getColor(null, -1));
            assertTrue(categorize.getClassValue(1).getColor(null, -1) == class2.getColor(null, -1));
            assertTrue(categorize.getClassValue(2).getColor(null, -1) == class3.getColor(null, -1));
            assertTrue(categorize.getClassValue(3).getColor(null, -1) == class4.getColor(null, -1));
        } catch (ParameterException ex) {
            Logger.getLogger(Categorize2ColorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void testSetThresholds(){
        try {
            // To retrieve classes...
            testAddClasses();
            categorize.setThresholdValue(2, t4);
            assertTrue(categorize.getNumClasses() == 4);
            assertTrue(categorize.getClassValue(0).getColor(null, -1) == class1.getColor(null, -1));
            assertTrue(categorize.getClassValue(1).getColor(null, -1) == class2.getColor(null, -1));
            assertTrue(categorize.getClassValue(2).getColor(null, -1) == class3.getColor(null, -1));
            assertTrue(categorize.getClassValue(3).getColor(null, -1) == class4.getColor(null, -1));

            // Thresholds : 75, 100, 200
            assertEquals(categorize.getThresholdValue(0).getValue(null, -1), t4.getValue(null, -1));
            assertEquals(categorize.getThresholdValue(1).getValue(null, -1), t1.getValue(null, -1));
            assertEquals(categorize.getThresholdValue(2).getValue(null, -1), t2.getValue(null, -1));


            // Thresholds : 75, 100, 200, 500
            categorize.setThresholdValue(0, t5);
            assertTrue(categorize.getNumClasses() == 4);
            assertTrue(categorize.getClassValue(0).getColor(null, -1) == class1.getColor(null, -1));
            assertTrue(categorize.getClassValue(1).getColor(null, -1) == class2.getColor(null, -1));
            assertTrue(categorize.getClassValue(2).getColor(null, -1) == class3.getColor(null, -1));
            assertTrue(categorize.getClassValue(3).getColor(null, -1) == class4.getColor(null, -1));

            assertEquals(categorize.getThresholdValue(0).getValue(null, -1), t1.getValue(null, -1));
            assertEquals(categorize.getThresholdValue(1).getValue(null, -1), t2.getValue(null, -1));
            assertEquals(categorize.getThresholdValue(2).getValue(null, -1), t5.getValue(null, -1));



        } catch (ParameterException ex) {
            Logger.getLogger(Categorize2ColorTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }



    public void testRemoveClasses(){
        try {
            testAddClasses();
            categorize.removeClass(0);
            assertTrue(categorize.getNumClasses() == 3);
            assertTrue(categorize.getClassValue(0).getColor(null, -1) == class2.getColor(null, -1));
            assertTrue(categorize.getClassValue(1).getColor(null, -1) == class3.getColor(null, -1));
            assertTrue(categorize.getClassValue(2).getColor(null, -1) == class4.getColor(null, -1));
            categorize.removeClass(2);
            assertTrue(categorize.getNumClasses() == 2);
            assertTrue(categorize.getClassValue(0).getColor(null, -1) == class2.getColor(null, -1));
            assertTrue(categorize.getClassValue(1).getColor(null, -1) == class3.getColor(null, -1));
            categorize.removeClass(1);
            assertTrue(categorize.getNumClasses() == 1);
            assertTrue(categorize.getClassValue(0).getColor(null, -1) == class2.getColor(null, -1));
        } catch (ParameterException ex) {
            Logger.getLogger(Categorize2ColorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * test that e are able to build a Categorize2Color directly from a Jaxb categorize
     * structure.
     * @throws Exception
     */
    @Test
    public void testFromJaxb() throws Exception{
            //We want to import it directly from the input file.
            String xmlRecode = "src/test/resources/org/orbisgis/core/renderer/se/colorCategorize.se";
        JAXBContext jaxbContext = JAXBContext.newInstance(StyleType.class);
        Unmarshaller u = jaxbContext.createUnmarshaller();
        JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                new FileInputStream(xmlRecode));
        AreaSymbolizerType ast = (AreaSymbolizerType)(ftsElem.getValue().getRule().get(0).getSymbolizer().getValue());
        SolidFillType pst = (SolidFillType)(ast.getFill().getValue());
        JAXBElement je = (JAXBElement)(pst.getColor().getContent().get(1));
        Categorize2Color c2c = new Categorize2Color((JAXBElement<CategorizeType>) je);
        assertTrue(c2c.getClassValue(0).getColor(null, 0).equals(new Color(0x11,0x33,0x55)));
        assertTrue(c2c.getClassValue(1).getColor(null, 0).equals(new Color(0xdd,0x66,0xee)));
        assertTrue(c2c.getClassValue(2).getColor(null, 0).equals(new Color(0xff,0xaa,0x99)));
            
    }
    
}
