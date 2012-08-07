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
package org.orbisgis.core.renderer.se.stroke;


import com.sun.media.jai.widget.DisplayJAI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.media.jai.RenderableGraphics;
import javax.swing.JFrame;
import javax.xml.bind.Marshaller;
import org.apache.log4j.Logger;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.orbisgis.core.Services;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.common.VariableOnlineResource;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;

/**
 *
 * @author Maxence Laurent
 */
public class StrokeRapportTest {
    private static final Logger LOGGER = Logger.getLogger(StrokeRapportTest.class);
    private Style fts;

    @Before
    public void setUp() throws Exception {

    }

    /**
     * We must not be able to have WKN and onlineResource set in a MarkGraphic.
     */
    @Test
    public void testWKNOnlineExclusivity(){
            MarkGraphic mg = new MarkGraphic();
            mg.setWkn(new StringLiteral("CIRCLE"));
            mg.setOnlineResource(new VariableOnlineResource());
            assertNull(mg.getWkn());
            mg.setWkn(new StringLiteral("CIRCLE"));
            assertNull(mg.getOnlineResource());
    }

    public void drawGraphic() throws IOException, ParameterException, InvalidStyle {
        JFrame frame = new JFrame();
        frame.setTitle("Test GraphicCollection");

        // Get the JFrame’s ContentPane.
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        // Create an instance of DisplayJAI.
        DisplayJAI dj = new DisplayJAI();

        System.out.println(dj.getColorModel());

        fts = new Style(null, "src/test/resources/org/orbisgis/core/renderer/se/strokes_rapport.se");

        // 1)
        try {
			Marshaller marshaller = Services.JAXBCONTEXT.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            ByteArrayOutputStream oStream = new ByteArrayOutputStream();

			marshaller.marshal(fts.getJAXBElement(), oStream);
		} catch (Exception ex) {
            LOGGER.error("Unable to marshall", ex);
            ex.printStackTrace(System.err);
            assertTrue(false);
		}



        PointSymbolizer ps = (PointSymbolizer) fts.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        GraphicCollection collec = ps.getGraphicCollection();

		MapTransform mt = new MapTransform();
        double width = Uom.toPixel(270, Uom.MM, mt.getDpi(), null, null);
        double height = Uom.toPixel(160, Uom.MM, mt.getDpi(), null, null);

        Rectangle2D.Double dim = new Rectangle2D.Double(-width/2, -height/2, width, height);
        RenderableGraphics rg = new RenderableGraphics(dim);

        collec.draw(rg, null, false, mt, new AffineTransform());

        rg.setPaint(Color.BLACK);
        rg.drawLine((int)rg.getMinX(), 0, (int)(rg.getMinX() + rg.getWidth()), 0);
        rg.drawLine(0, (int)rg.getMinY(), 0, (int)(rg.getMinY() + rg.getHeight()));

        dj.setBounds((int)rg.getMinX(), (int)rg.getMinY(), (int)rg.getWidth(), (int)rg.getHeight());
        RenderedImage r = rg.createRendering(mt.getCurrentRenderContext());
        dj.set(r, (int)rg.getWidth()/2, (int)rg.getHeight()/2);

        File file = new File("/tmp/stroke_rapport.png");
        ImageIO.write(r, "png", file);

        // Add to the JFrame’s ContentPane an instance of JScrollPane
        // containing the DisplayJAI instance.
        //contentPane.add(new JScrollPane(dj), BorderLayout.CENTER);
        contentPane.add(dj, BorderLayout.CENTER);

        // Set the closing operation so the application is finished.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize((int)rg.getWidth(), (int)rg.getHeight()+24); // adjust the frame size.
        frame.setVisible(true); // show the frame.
        
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            LOGGER.error(ex);
        }
        
    }
}
