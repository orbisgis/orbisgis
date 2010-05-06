/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.graphic;

import com.sun.media.jai.widget.DisplayJAI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;

import java.awt.image.renderable.RenderContext;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.jai.RenderableGraphics;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import junit.framework.TestCase;

import org.orbisgis.core.renderer.se.common.OnlineResource;
import org.orbisgis.core.renderer.se.common.RenderContextFactory;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.GraphicFill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.label.StyledLabel;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.core.renderer.se.stroke.PenStroke;

import org.orbisgis.core.renderer.se.transform.Rotate;
import org.orbisgis.core.renderer.se.transform.Scale;
import org.orbisgis.core.renderer.se.transform.Transform;
import org.orbisgis.core.renderer.se.transform.Translate;

/**
 *
 * @author maxence
 */
public class GraphicCollectionTest extends TestCase {

    public GraphicCollectionTest(String testName) throws IOException {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        SolidFill fill = new SolidFill();
        ColorLiteral gray = new ColorLiteral(Color.WHITE);

        PenStroke stroke = new PenStroke();
        stroke.setWidth(new RealLiteral(0.5));

        
        fill.setColor(gray);

        collec = new GraphicCollection();
        collec2 = new GraphicCollection();

        mark = new MarkGraphic();
        mark.setFill(fill);
        mark.setStroke(new PenStroke());
        mark.setSource(WellKnownName.STAR);
        mark.setViewBox(new ViewBox(new RealLiteral(6.0)));
        Transform t = new Transform();
        mark.setTransform(t);
        mark.setUom(Uom.MM);
        t = null;


        collec2.addGraphic(mark);

        GraphicFill gFill = new GraphicFill();
        gFill.setGraphic(collec2);
        //gFill.setGapX(new RealLiteral(5));
        //gFill.setGapY(new RealLiteral(5));


        collec = new GraphicCollection();
        mark1 = new MarkGraphic();
        mark1.setFill(gFill);
        mark1.setStroke(new PenStroke());
        mark1.setSource(WellKnownName.TRIANGLE);
        mark1.setViewBox(new ViewBox(new RealLiteral(60.0)));
        Transform t4 = new Transform();
        t4.addTransformation(new Translate(new RealLiteral(100), null));
        mark1.setTransform(t4);
        mark1.setUom(Uom.MM);

        t4 = null;


        mark2 = new MarkGraphic();
        mark2.setUom(Uom.MM);
        mark2.setFill(gFill);
        mark2.setStroke(stroke);
        mark2.setSource(WellKnownName.CIRCLE);
        mark2.setViewBox(new ViewBox(new RealLiteral(60.0)));
        Transform t3 = new Transform();
        t3.addTransformation(new Translate(new RealLiteral(70), null));
        mark2.setTransform(t3);
        

        
        ext = new ExternalGraphic();
        ext.setUom(Uom.MM);
        //ext.setSource(new OnlineResource("file:///home/maxence/symbol.jpg"));
        ext.setSource(new OnlineResource("http://tof.canardpc.com/view/8a85f406-1d3f-4725-81fb-a24a9eab0421.jpg"));
        ext.setViewBox(new ViewBox(new RealLiteral(48.0)));
        Transform t2 = new Transform();
        t2.addTransformation(new Translate(null, new RealLiteral(50)));
        ext.setTransform(t2);

        text = new TextGraphic();
        StyledLabel label = new StyledLabel();
        text.setStyledLabel(label);
        Transform tl1 = new Transform();
        tl1.addTransformation(new Translate(null, new RealLiteral(20)));
        text.setTransform(tl1);
        label.setFontSize(new RealLiteral(18));

        text2 = new TextGraphic();
        StyledLabel label2 = new StyledLabel();
        text2.setStyledLabel(label2);
        label2.setFontStyle(new StringLiteral("italic"));
        Transform tl2 = new Transform();
        tl2.addTransformation(new Translate(null, new RealLiteral(40)));
        text2.setTransform(tl2);
        label2.setFontSize(new RealLiteral(18));

        text3 = new TextGraphic();
        StyledLabel label3 = new StyledLabel();
        label3.setFontWeight(new StringLiteral("bold"));
        text3.setStyledLabel(label3);
        Transform tl3 = new Transform();
        tl3.addTransformation(new Translate(null, new RealLiteral(60)));
        text3.setTransform(tl3);
        label3.setFontSize(new RealLiteral(18));

        collec.addGraphic(ext);
        collec.addGraphic(mark2);
        collec.addGraphic(mark1);
        collec.addGraphic(text);
        collec.addGraphic(text2);
        collec.addGraphic(text3);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAll() throws ParameterException, IOException {
        JFrame frame = new JFrame();
        frame.setTitle("Test GraphicCollection");

        // Get the JFrame’s ContentPane.
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        // Create an instance of DisplayJAI.
        DisplayJAI dj = new DisplayJAI();

        dj.setBackground(Color.PINK);
        dj.setBounds(0, 0, 500, 500);

        System.out.println(dj.getColorModel());

        RenderableGraphics rg;
        rg = collec.getGraphic(null, 0);

        System.out.println ("DJ: " + dj);
        System.out.println ("RG: " + rg);
        System.out.println ("Rendered : " + rg.createDefaultRendering());

        RenderContext ctc = RenderContextFactory.getContext();

        rg.setPaint(Color.BLACK);
        rg.drawLine((int)rg.getMinX(), 0, (int)(rg.getMinX() + rg.getWidth()), 0);
        rg.drawLine(0, (int)rg.getMinY(), 0, (int)(rg.getMinY() + rg.getHeight()));

        dj.set(rg.createRendering(ctc), 200, 200);

        // Add to the JFrame’s ContentPane an instance of JScrollPane
        // containing the DisplayJAI instance.
        contentPane.add(new JScrollPane(dj), BorderLayout.CENTER);

        // Set the closing operation so the application is finished.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 500); // adjust the frame size.
        frame.setVisible(true); // show the frame.

        System.out.print("");
        
        try {
            Thread.sleep(20000);
        } catch (InterruptedException ex) {
            Logger.getLogger(GraphicCollectionTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    private GraphicCollection collec;

    private GraphicCollection collec2;

    MarkGraphic mark;
    MarkGraphic mark1;
    MarkGraphic mark2;

    TextGraphic text;
    TextGraphic text2;
    TextGraphic text3;

    ExternalGraphic ext;

}
