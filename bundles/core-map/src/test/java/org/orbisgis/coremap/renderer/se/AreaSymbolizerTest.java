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
import java.io.FileOutputStream;
import javax.swing.JPanel;
import javax.xml.bind.Marshaller;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.orbisgis.coremap.renderer.se.parameter.UsedAnalysis;
import org.orbisgis.coremap.renderer.se.visitors.UsedAnalysisVisitor;

/**
 *
 * @author Maxence Laurent
 */
public class AreaSymbolizerTest {

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
    public void testAreaSymbolizer() throws Exception {
            Style style = new Style(null, AreaSymbolizerTest.class.getResource("Districts/choro.se").getFile());
            Marshaller marshaller = org.orbisgis.coremap.map.JaxbContainer.JAXBCONTEXT.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(style.getJAXBElement(), new FileOutputStream("output.se"));
            assertTrue(true);
    }

    @Test
    public void testCategorizeUsedAnalysis() throws Exception {
        Style style = new Style(null, AreaSymbolizerTest.class.getResource("colorCategorize.se").getFile());
        AreaSymbolizer ps =(AreaSymbolizer) style.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        UsedAnalysisVisitor uv = new UsedAnalysisVisitor();
        uv.visitSymbolizerNode(ps);
        UsedAnalysis ua = uv.getUsedAnalysis();
        assertTrue(ua.isCategorizeUsed());
        assertTrue(ua.getAnalysis().size()==1);
    }
}
