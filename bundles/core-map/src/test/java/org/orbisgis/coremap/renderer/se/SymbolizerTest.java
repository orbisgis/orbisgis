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
package org.orbisgis.coremap.renderer.se;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.Set;
import javax.swing.JPanel;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.validation.Schema;
import net.opengis.se._2_0.core.SymbolizerType;
import org.junit.Test;
import static org.junit.Assert.*;
import org.orbisgis.coremap.renderer.se.fill.SolidFill;
import org.orbisgis.coremap.renderer.se.parameter.UsedAnalysis;
import org.orbisgis.coremap.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.coremap.renderer.se.parameter.color.Recode2Color;
import org.orbisgis.coremap.renderer.se.parameter.string.StringAttribute;
import org.orbisgis.coremap.renderer.se.visitors.FeaturesVisitor;
import org.orbisgis.coremap.renderer.se.visitors.UsedAnalysisVisitor;

/**
 *
 * @author Maxence Laurent
 */
public class SymbolizerTest {

    private class ImagePanel extends JPanel {

        private BufferedImage img;

        ImagePanel(BufferedImage img) {
            super();
            this.img = img;
        }

        @Override
        public void paintComponent(Graphics g) {
            //super.paintComponent(g);
            g.drawImage(img, 0, 0, null);
            //   ((Graphics2D) g).setStroke(new BasicStroke(10.0f));
            // g.drawLine(0, 0, 1000, 1000);
        }
    }

    @Test
    public void testMarshallInvalidSeFile() throws Exception {
            //The following file contains an invalid markup that MUST NOT be recognized.
            String xml = SymbolizerTest.class.getResource("invalidCategorize.se").getFile();

            Unmarshaller u = org.orbisgis.coremap.map.JaxbContainer.JAXBCONTEXT.createUnmarshaller();


            Schema schema = u.getSchema();
            ValidationEventCollector validationCollector = new ValidationEventCollector();
            u.setEventHandler(validationCollector);




            JAXBElement<? extends SymbolizerType> symb = (JAXBElement<? extends SymbolizerType>) u.unmarshal(
                    new FileInputStream(xml));

            if(validationCollector.getEvents().length == 0){
                    fail();
            }

            for (ValidationEvent event : validationCollector.getEvents()) {
                String msg = event.getMessage();
                ValidationEventLocator locator = event.getLocator();
                int line = locator.getLineNumber();
                int column = locator.getColumnNumber();
                System.out.println("Error at line " + line + " column " + column);
                //We've encountered an error - everything's normal.
                assertTrue(true);
            }
    }
    
    @Test 
    public void testDependsOnFeature() throws Exception {
        FeaturesVisitor fv = new FeaturesVisitor();
        String xml = SymbolizerTest.class.getResource("symbol_prop_canton_interpol_lin.se").getFile();
        Style fts = new Style(null, xml);
        fts.acceptVisitor(fv);
        Set<String> feat = fv.getResult();
        assertTrue(feat.size() == 1);
        assertTrue(feat.contains("PTOT99"));
        AreaSymbolizer as = (AreaSymbolizer)fts.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        SolidFill fill = (SolidFill) as.getFill();
        StringAttribute sa = new StringAttribute("PTOT99");
        Recode2Color rc = new Recode2Color(new ColorLiteral("#887766"), sa);
        rc.addMapItem("bonjour", new ColorLiteral("#546576"));
        fill.setColor(rc);
        fts.acceptVisitor(fv);
        feat = fv.getResult();
        assertTrue(feat.size() == 1);
        assertTrue(feat.contains("PTOT99"));
        sa.setColumnName("ohhai");
        fts.acceptVisitor(fv);
        feat = fv.getResult();
        assertTrue(feat.size() == 2);
        assertTrue(feat.contains("PTOT99"));
        assertTrue(feat.contains("ohhai"));
    }

    @Test
    public void testRecodeUsedAnalysis() throws Exception {
        Style style = new Style(null, SymbolizerTest.class.getResource("colorRecode.se").getFile());
        LineSymbolizer ps =(LineSymbolizer) style.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        UsedAnalysisVisitor uv = new UsedAnalysisVisitor();
        uv.visitSymbolizerNode(ps);
        UsedAnalysis ua = uv.getUsedAnalysis();
        assertTrue(ua.isRecodeUsed());
        assertTrue(ua.getAnalysis().size()==1);
    }
}
