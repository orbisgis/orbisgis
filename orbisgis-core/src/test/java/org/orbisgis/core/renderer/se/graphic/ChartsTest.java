/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.graphic;


import com.sun.media.jai.widget.DisplayJAI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.image.RenderedImage;
import java.io.File;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.media.jai.RenderableGraphics;

import javax.swing.JFrame;

import junit.framework.TestCase;
import org.orbisgis.core.Services;
import org.orbisgis.core.errorManager.ErrorListener;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.FeatureTypeStyle;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 *
 * @author maxence
 */
public class ChartsTest extends TestCase {

    private FeatureTypeStyle fts;

    public ChartsTest(String testName) throws IOException {
        super(testName);
    }

	protected FailErrorManager failErrorManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

		failErrorManager = new FailErrorManager();
		Services.registerService(ErrorManager.class, "", failErrorManager);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGraphic() throws IOException, ParameterException, InvalidStyle {
        JFrame frame = new JFrame();
        frame.setTitle("Test GraphicCollection");

        // Get the JFrame’s ContentPane.
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        // Create an instance of DisplayJAI.
        DisplayJAI dj = new DisplayJAI();

        System.out.println(dj.getColorModel());

        fts = new FeatureTypeStyle(null, "src/test/resources/org/orbisgis/core/renderer/se/charts.se");
        PointSymbolizer ps = (PointSymbolizer) fts.getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        GraphicCollection collec = ps.getGraphicCollection();

        RenderableGraphics rg;
		MapTransform mt = new MapTransform();
        rg = collec.getGraphic(null, -1, false, mt);

        rg.setPaint(Color.BLACK);
        rg.drawLine((int)rg.getMinX(), 0, (int)(rg.getMinX() + rg.getWidth()), 0);
        rg.drawLine(0, (int)rg.getMinY(), 0, (int)(rg.getMinY() + rg.getHeight()));

        dj.setBounds((int)rg.getMinX(), (int)rg.getMinY(), (int)rg.getWidth(), (int)rg.getHeight());

        RenderedImage r = rg.createRendering(mt.getCurrentRenderContext());
        dj.set(r, (int)rg.getWidth()/2, (int)rg.getHeight()/2);

        File file = new File("/tmp/charts.png");
        ImageIO.write(r, "png", file);

        // Add to the JFrame’s ContentPane an instance of JScrollPane
        // containing the DisplayJAI instance.
        //contentPane.add(new JScrollPane(dj), BorderLayout.CENTER);
        contentPane.add(dj, BorderLayout.CENTER);

        // Set the closing operation so the application is finished.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize((int)rg.getWidth(), (int)rg.getHeight()+24); // adjust the frame size.
        frame.setVisible(true); // show the frame.

        System.out.print("");
        
        try {
            Thread.sleep(20000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ChartsTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }



	protected class FailErrorManager implements ErrorManager {

        @Override
		public void addErrorListener(ErrorListener listener) {
		}

        @Override
		public void error(String userMsg) {
            System.out.println ("ERR: " + userMsg);
		}

        @Override
		public void error(String userMsg, Throwable exception) {
            System.out.println ("ERR: " + userMsg + ": " + exception);
		}

        @Override
		public void removeErrorListener(ErrorListener listener) {
		}

        @Override
		public void warning(String userMsg, Throwable exception) {
            System.out.println ("WARN: " + userMsg + ": " + exception);
		}

        @Override
		public void warning(String userMsg) {
            System.out.println ("WARN: " + userMsg);
		}
	}
}
