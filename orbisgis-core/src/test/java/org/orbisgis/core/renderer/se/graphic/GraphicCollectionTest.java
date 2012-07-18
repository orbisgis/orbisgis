/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.graphic;


import com.sun.media.jai.widget.DisplayJAI;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 *
 * @author maxence
 */
public class GraphicCollectionTest {

    private Style fts;

    public void variousGraphicDisplay() throws IOException, ParameterException, InvalidStyle {
        JFrame frame = new JFrame();
        frame.setTitle("Test GraphicCollection");

        // Get the JFrame’s ContentPane.
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        // Create an instance of DisplayJAI.
        DisplayJAI dj = new DisplayJAI();

        System.out.println(dj.getColorModel());

        fts = new Style(null, "src/test/resources/org/orbisgis/core/renderer/se/graphics.se");
        PointSymbolizer ps = (PointSymbolizer) fts.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        GraphicCollection collec = ps.getGraphicCollection();


		MapTransform mt = new MapTransform();
        double width = Uom.toPixel(270, Uom.MM, mt.getDpi(), null, null);
        double height = Uom.toPixel(160, Uom.MM, mt.getDpi(), null, null);


        //Rectangle2D.Double dim = new Rectangle2D.Double(-width/2, -height/2, width, height);
        BufferedImage img = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D rg = img.createGraphics();
        rg.setRenderingHints(mt.getRenderingHints());

        collec.draw(rg, null, false, mt, AffineTransform.getTranslateInstance(width/2, height/2));

        rg.setStroke(new BasicStroke(1));
        rg.setPaint(Color.BLACK);

        rg.drawLine(0, (int)height/2, (int)width, (int)height/2);
        rg.drawLine((int)width/2, 0, (int)width/2, (int)height);

        dj.setBounds(0, 0, (int)width, (int)height);
        //dj.setBounds((int)rg.getMinX(), (int)rg.getMinY(), (int)rg.getWidth(), (int)rg.getHeight());

        //RenderedImage r = rg.createRendering(mt.getCurrentRenderContext());

        dj.set(img, 0,0);

        File file = new File("/tmp/graphics.png");
        ImageIO.write(img, "png", file);

        // Add to the JFrame’s ContentPane an instance of JScrollPane
        // containing the DisplayJAI instance.
        //contentPane.add(new JScrollPane(dj), BorderLayout.CENTER);
        contentPane.add(dj, BorderLayout.CENTER);

        // Set the closing operation so the application is finished.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize((int)width, (int)height+24); // adjust the frame size.
        frame.setVisible(true); // show the frame.

        
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(GraphicCollectionTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertTrue(true);
    }

    @Test
    public void testMoveUp() throws Exception{
            GraphicCollection gc = new GraphicCollection();
            PointTextGraphic ptg1 = new PointTextGraphic();
            PointTextGraphic ptg2 = new PointTextGraphic();
            PointTextGraphic ptg3 = new PointTextGraphic();
            PointTextGraphic ptg4 = new PointTextGraphic();
            gc.addGraphic(ptg1);
            gc.addGraphic(ptg2);
            gc.addGraphic(ptg3);
            gc.addGraphic(ptg4);
            assertEquals(gc.getNumGraphics(), 4);
            assertTrue(gc.getGraphic(0) == ptg1);
            assertTrue(gc.getGraphic(1) == ptg2);
            assertTrue(gc.getGraphic(2) == ptg3);
            assertTrue(gc.getGraphic(3) == ptg4);
            //We move ptg2 up
            gc.moveGraphicUp(1);
            assertTrue(gc.getGraphic(0) == ptg2);
            assertTrue(gc.getGraphic(1) == ptg1);
            assertTrue(gc.getGraphic(2) == ptg3);
            assertTrue(gc.getGraphic(3) == ptg4);
            //We move ptg2 up. Nothing is supposed to happen, as it is the uppest element.
            gc.moveGraphicUp(0);
            assertTrue(gc.getGraphic(0) == ptg2);
            assertTrue(gc.getGraphic(1) == ptg1);
            assertTrue(gc.getGraphic(2) == ptg3);
            assertTrue(gc.getGraphic(3) == ptg4);
            //We move ptg4 up. 
            gc.moveGraphicUp(3);
            assertTrue(gc.getGraphic(0) == ptg2);
            assertTrue(gc.getGraphic(1) == ptg1);
            assertTrue(gc.getGraphic(2) == ptg4);
            assertTrue(gc.getGraphic(3) == ptg3);
    }

    @Test
    public void testMoveDown() throws Exception{
            GraphicCollection gc = new GraphicCollection();
            PointTextGraphic ptg1 = new PointTextGraphic();
            PointTextGraphic ptg2 = new PointTextGraphic();
            PointTextGraphic ptg3 = new PointTextGraphic();
            PointTextGraphic ptg4 = new PointTextGraphic();
            gc.addGraphic(ptg1);
            gc.addGraphic(ptg2);
            gc.addGraphic(ptg3);
            gc.addGraphic(ptg4);
            assertEquals(gc.getNumGraphics(), 4);
            //We move ptg2 down
            gc.moveGraphicDown(1);
            assertTrue(gc.getGraphic(0) == ptg1);
            assertTrue(gc.getGraphic(1) == ptg3);
            assertTrue(gc.getGraphic(2) == ptg2);
            assertTrue(gc.getGraphic(3) == ptg4);
            //We move ptg1 down
            gc.moveGraphicDown(0);
            assertTrue(gc.getGraphic(0) == ptg3);
            assertTrue(gc.getGraphic(1) == ptg1);
            assertTrue(gc.getGraphic(2) == ptg2);
            assertTrue(gc.getGraphic(3) == ptg4);
            //We move ptg1 down. Nothing is supposed to happen, as it is the lowest element.
            gc.moveGraphicDown(3);
            assertTrue(gc.getGraphic(0) == ptg3);
            assertTrue(gc.getGraphic(1) == ptg1);
            assertTrue(gc.getGraphic(2) == ptg2);
            assertTrue(gc.getGraphic(3) == ptg4);
            
    }
}
