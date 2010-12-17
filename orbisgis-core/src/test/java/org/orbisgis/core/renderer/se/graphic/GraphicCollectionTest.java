/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.graphic;


import com.sun.media.jai.widget.DisplayJAI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.jai.RenderableGraphics;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import junit.framework.TestCase;
import org.orbisgis.core.map.MapTransform;

import org.orbisgis.core.renderer.se.common.OnlineResource;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.GraphicFill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.graphic.PieChart.PieChartSubType;
import org.orbisgis.core.renderer.se.label.StyledLabel;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.core.renderer.se.stroke.PenStroke;

import org.orbisgis.core.renderer.se.transform.Rotate;
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
        PenStroke s1 = new PenStroke();
        s1.setWidth(new RealLiteral(0.1));
        mark.setStroke(s1);
        mark.setSource(WellKnownName.CIRCLE);
        mark.setViewBox(new ViewBox(new RealLiteral(6.0)));
        Transform t = new Transform();
        mark.setTransform(t);
        mark.setUom(Uom.MM);
        t = null;


        collec2.addGraphic(mark);

        GraphicFill gFill = new GraphicFill();
        gFill.setGraphic(collec2);
        gFill.setGapX(new RealLiteral(3));
        gFill.setGapY(new RealLiteral(5));


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
        ext.setSource(new OnlineResource("http://www.neatimage.com/im/Neat-logo.jpg"));
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


        PieChart pie = new PieChart();

        Slice slc = new Slice();
        slc.setFill(new SolidFill());
        slc.setValue(new RealLiteral(10));
        pie.addSlice(slc);

        Slice slc1 = new Slice();
        slc1.setFill(new SolidFill());
        slc1.setValue(new RealLiteral(20));
        pie.addSlice(slc1);
        
        Slice slc2 = new Slice();
        slc2.setFill(new SolidFill());
        slc2.setValue(new RealLiteral(30));
        pie.addSlice(slc2);
        
        Slice slc3 = new Slice();
        slc3.setFill(new SolidFill());
        slc3.setValue(new RealLiteral(40));
        pie.addSlice(slc3);
        

        pie.setRadius(new RealLiteral(100));
        pie.setType(PieChartSubType.HALF);
        
        Transform tp1 = new Transform();
        tp1.addTransformation(new Rotate(new RealLiteral(-90.0)));
        pie.setTransform(tp1);

        pie.setStroke(new PenStroke());
        pie.setDisplayValue(true);


        PieChart pie2 = new PieChart();

        Slice slc21 = new Slice();
        slc21.setFill(new SolidFill());
        slc21.setValue(new RealLiteral(10));
        pie2.addSlice(slc21);

        Slice slc22 = new Slice();
        slc22.setFill(new SolidFill());
        slc22.setValue(new RealLiteral(20));
        pie2.addSlice(slc22);

        Slice slc23 = new Slice();
        slc23.setFill(new SolidFill());
        slc23.setValue(new RealLiteral(30));
        pie2.addSlice(slc23);

        Slice slc24 = new Slice();
        slc24.setFill(new SolidFill());
        slc24.setValue(new RealLiteral(40));
        pie2.addSlice(slc24);


        pie2.setRadius(new RealLiteral(150));
        pie2.setType(PieChartSubType.HALF);

        pie2.setStroke(new PenStroke());
        Transform tp2 = new Transform();
        tp2.addTransformation(new Rotate(new RealLiteral(90.0)));
        pie2.setTransform(tp2);
        pie2.setDisplayValue(true);

        pie2.setHoleRadius(new RealLiteral(100));

        collec.addGraphic(ext);
        collec.addGraphic(mark2);
        collec.addGraphic(mark1);
        collec.addGraphic(text);
        collec.addGraphic(text2);
        collec.addGraphic(text3);
        collec.addGraphic(pie);
        collec.addGraphic(pie2);
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
		MapTransform mt = new MapTransform();
        rg = collec.getGraphic(null, -1, false, mt);

        rg.setPaint(Color.BLACK);
        rg.drawLine((int)rg.getMinX(), 0, (int)(rg.getMinX() + rg.getWidth()), 0);
        rg.drawLine(0, (int)rg.getMinY(), 0, (int)(rg.getMinY() + rg.getHeight()));

        dj.set(rg.createRendering(mt.getCurrentRenderContext()), 200, 200);

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
