/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.analyzer;

import java.awt.Color;
import java.io.FileInputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import net.opengis.se._2_0.core.StyleType;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.orbisgis.core.renderer.se.AreaSymbolizer;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.legend.AnalyzerTest;
import org.orbisgis.legend.analyzer.symbolizers.AreaSymbolizerAnalyzer;
import org.orbisgis.legend.thematic.choropleth.ChoroplethArea;
import org.orbisgis.legend.thematic.constant.UniqueSymbolArea;

/**
 *
 * @author alexis
 */
public class AreaSymbolizerAnalyzerTest extends AnalyzerTest {

    private String constant = "src/test/resources/org/orbisgis/legend/constantArea.se";
    private String categorize = "src/test/resources/org/orbisgis/legend/colorCategorize.se";

    @Test
    public void testConstantConstructor() throws Exception {
        AreaSymbolizer ls = getConstantSymbolizer();
        UniqueSymbolArea usl = new UniqueSymbolArea(ls);
        assertTrue(true);
    }

    @Test
    public void testConstantAnalyzer() throws Exception {
        AreaSymbolizer ls = getConstantSymbolizer();
        UniqueSymbolArea usl =(UniqueSymbolArea) new AreaSymbolizerAnalyzer(ls).getLegend();
        assertTrue(true);
    }
    
    @Test
    public void testConstantConstructorFail() throws Exception {
        AreaSymbolizer ls = getChoroSymbolizer();
        try{
            UniqueSymbolArea usl = new UniqueSymbolArea(ls);
            fail();
        } catch(IllegalArgumentException iae){
            assertTrue(true);
        }
    }

    @Test
    public void testConstantGetFillColor() throws Exception {
        AreaSymbolizer ls = getConstantSymbolizer();
        UniqueSymbolArea usl = new UniqueSymbolArea(ls);
        assertTrue(usl.getFillColor().equals(new Color(0x12, 0x34, 0x56)));
    }

    @Test
    public void testConstantSetFillColor() throws Exception {
        AreaSymbolizer ls = getConstantSymbolizer();
        UniqueSymbolArea usl = new UniqueSymbolArea(ls);
        usl.setFillColor(Color.red);
        assertTrue(usl.getFillColor().equals(Color.red));
    }

    @Test
    public void testConstantGetStrokeColor() throws Exception {
        AreaSymbolizer ls = getConstantSymbolizer();
        UniqueSymbolArea usl = new UniqueSymbolArea(ls);
        assertTrue(usl.getLineColor().equals(new Color(0x88, 0x88, 0x88)));
    }

    @Test
    public void testConstantSetStrokeColor() throws Exception {
        AreaSymbolizer ls = getConstantSymbolizer();
        UniqueSymbolArea usl = new UniqueSymbolArea(ls);
        usl.setLineColor(Color.red);
        assertTrue(usl.getLineColor().equals(Color.red));
    }

    @Test
    public void testGetLineDash() throws Exception {
        AreaSymbolizer ls = getConstantSymbolizer();
        UniqueSymbolArea uvp = new UniqueSymbolArea(ls);
        assertTrue(uvp.getDashArray().isEmpty());
    }

    @Test
    public void testSetLineDash() throws Exception {
        AreaSymbolizer ls = getConstantSymbolizer();
        UniqueSymbolArea uvp = new UniqueSymbolArea(ls);
        uvp.setDashArray("2 2");
        assertTrue(uvp.getDashArray().equals("2 2"));
    }

    @Test
    public void testConstantGetLineWidth() throws Exception {
        AreaSymbolizer ls = getConstantSymbolizer();
        UniqueSymbolArea usl = new UniqueSymbolArea(ls);
        assertTrue(usl.getLineWidth() == 0.15);
    }

    @Test
    public void testConstantSetLineWidth() throws Exception {
        AreaSymbolizer ls = getConstantSymbolizer();
        UniqueSymbolArea usl = new UniqueSymbolArea(ls);
        usl.setLineWidth(12.2);
        assertTrue(usl.getLineWidth() == 12.2);
    }

    @Test
    public void testConstantDefaultConstructor() throws Exception {
        UniqueSymbolArea usa = new UniqueSymbolArea();
        assertTrue(usa.getLineWidth() == 0.1);
        assertTrue(usa.getLineColor().equals(Color.black));
    }

    @Test
    public void testChoroConstructor() throws Exception {
        AreaSymbolizer ls = getChoroSymbolizer();
        ChoroplethArea ca = new ChoroplethArea(ls);
        assertTrue(true);
    }
    
    @Test
    public void testChoroConstructorFail() throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(JAXBCONTEXT);
        Unmarshaller u = jaxbContext.createUnmarshaller();
        JAXBElement<StyleType> ftsElem = (JAXBElement<StyleType>) u.unmarshal(
                new FileInputStream(constant));
        Style st = new Style(ftsElem, null);
        AreaSymbolizer ls = (AreaSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        try{
            ChoroplethArea usl = new ChoroplethArea(ls);
            fail();
        } catch(IllegalArgumentException iae){
            assertTrue(true);
        }
    }

    @Test
    public void testChoroGetFallBackColor() throws Exception {
        AreaSymbolizer ls = getChoroSymbolizer();
        ChoroplethArea ca = new ChoroplethArea(ls);
        assertTrue(ca.getFallBackColor().equals(new Color(0x11,0x11,0x11)));
    }

    @Test
    public void testChoroSetFallBackColor() throws Exception {
        AreaSymbolizer ls = getChoroSymbolizer();
        ChoroplethArea ca = new ChoroplethArea(ls);
        ca.setFallBackColor(Color.CYAN);
        assertTrue(ca.getFallBackColor().equals(Color.CYAN));
    }

    @Test
    public void testChoroGetColors() throws Exception {
        AreaSymbolizer ls = getChoroSymbolizer();
        ChoroplethArea ca = new ChoroplethArea(ls);
        assertTrue(ca.getColor(0).equals(new Color(0x11,0x33,0x55)));
        assertTrue(ca.getColor(1).equals(new Color(0xdd,0x66,0xee)));
        assertTrue(ca.getColor(2).equals(new Color(0xff,0xaa,0x99)));
    }

    @Test
    public void testChoroSetColors() throws Exception {
        AreaSymbolizer ls = getChoroSymbolizer();
        ChoroplethArea ca = new ChoroplethArea(ls);
        ca.setColor(0,Color.CYAN);
        assertTrue(ca.getColor(0).equals(Color.CYAN));
        ca.setColor(1,Color.BLUE);
        assertTrue(ca.getColor(1).equals(Color.BLUE));
        ca.setColor(2,Color.RED);
        assertTrue(ca.getColor(2).equals(Color.RED));
    }

    @Test
    public void testChoroGetThresholds() throws Exception {
        AreaSymbolizer ls = getChoroSymbolizer();
        ChoroplethArea ca = new ChoroplethArea(ls);
        assertTrue(ca.getThreshold(0) == 70000.0);
        assertTrue(ca.getThreshold(1) == 100000.0);
    }

    @Test
    public void testChoroSetThresholds() throws Exception {
        AreaSymbolizer ls = getChoroSymbolizer();
        ChoroplethArea ca = new ChoroplethArea(ls);
        assertTrue(ca.getThreshold(0) == 70000.0);
        ca.setThreshold(0, 25.3);
        assertTrue(ca.getThreshold(0) == 25.3);
        assertTrue(ca.getThreshold(1) == 100000.0);
        ca.setThreshold(1, 25.4);
        assertTrue(ca.getThreshold(1) == 25.4);
    }

    @Test
    public void testChoroNumClass() throws Exception {
        AreaSymbolizer ls = getChoroSymbolizer();
        ChoroplethArea ca = new ChoroplethArea(ls);
        assertTrue(ca.getNumClass() == 3);
    }

    @Test
    public void testAddClass() throws Exception {
        AreaSymbolizer ls = getChoroSymbolizer();
        ChoroplethArea ca = new ChoroplethArea(ls);
        ca.addClass(2000000.5, Color.red);
        assertTrue(ca.getNumClass() == 4);
        assertTrue(ca.getColor(3).equals(Color.red));
        assertTrue(ca.getThreshold(2) == 2000000.5);

    }

    @Test
    public void testRemoveFirstClass() throws Exception {
        AreaSymbolizer ls = getChoroSymbolizer();
        ChoroplethArea ca = new ChoroplethArea(ls);
        ca.removeClass(0);
        assertTrue(ca.getNumClass() == 2);
        assertTrue(ca.getColor(0).equals(new Color(0xdd, 0x66, 0xee)));
        assertTrue(ca.getThreshold(0) == 100000);
    }

    @Test
    public void testRemoveClass() throws Exception {
        AreaSymbolizer ls = getChoroSymbolizer();
        ChoroplethArea ca = new ChoroplethArea(ls);
        ca.removeClass(1);
        assertTrue(ca.getNumClass() == 2);
        assertTrue(ca.getColor(0).equals(new Color(0x11, 0x33, 0x55)));
        assertTrue(ca.getThreshold(0) == 100000);
        assertTrue(ca.getColor(1).equals(new Color(0xff, 0xaa, 0x99)));
        try{
            ca.getThreshold(1);
            fail();
        } catch(IndexOutOfBoundsException ioobe){
            assertTrue(true);
        }
    }

    private AreaSymbolizer getChoroSymbolizer() throws Exception {
        Style st = getStyle(categorize);
        return (AreaSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
    }
    private AreaSymbolizer getConstantSymbolizer() throws Exception {
        Style st = getStyle(constant);
        return (AreaSymbolizer)st.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
    }

}
