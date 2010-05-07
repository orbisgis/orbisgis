/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se;

import com.vividsolutions.jts.geom.Envelope;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import junit.framework.TestCase;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.common.RenderContextFactory;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.graphic.ViewBox;
import org.orbisgis.core.renderer.se.graphic.WellKnownName;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.color.Categorize2Color;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.se.parameter.real.RealBinaryOperator;
import org.orbisgis.core.renderer.se.parameter.real.RealBinaryOperatorType;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealUnitaryOperator;
import org.orbisgis.core.renderer.se.parameter.real.RealUnitaryOperatorType;
import org.orbisgis.core.renderer.se.stroke.PenStroke;

/**
 *
 * @author maxence
 */
public class AreaSymbolizerTest extends TestCase {

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

    public void testAreaSymbolizer() throws ParameterException, IOException {
        try {
            MapTransform mt = new MapTransform();
            mt.resizeImage(1000, 700);

            Envelope extent = new Envelope(472212.0, 843821.0, 68786.0, 293586.0);

            mt.setExtent(extent);

            extent = mt.getAdjustedExtent();

            RenderContextFactory.setMapTransform(mt);

            BufferedImage img = mt.getImage();
            Graphics2D g2 = img.createGraphics();

            DataSourceFactory dsf = new DataSourceFactory();
            DataSource ds = dsf.getDataSource(new File("/data/Cartes/Swiss/g4districts98_region.shp"));
            ds.open();

            SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);

            AreaSymbolizer aSymb = new AreaSymbolizer();
            SolidFill choropleth = new SolidFill();

            Categorize2Color classification = new Categorize2Color(new ColorLiteral(), new ColorLiteral(), new RealAttribute("ONU_2002", ds));

            classification.addClass(new RealLiteral(45.0), new ColorLiteral());
            classification.addClass(new RealLiteral(50.0), new ColorLiteral());
            classification.addClass(new RealLiteral(55.0), new ColorLiteral());

            choropleth.setOpacity(new RealLiteral(100.0));

            choropleth.setColor(classification);
            aSymb.setFill(choropleth);

            aSymb.setStroke(new PenStroke());

            GraphicCollection collec = new GraphicCollection();
            MarkGraphic mark = new MarkGraphic();

            RealBinaryOperator width = new RealBinaryOperator();
            width.setLeftValue(new RealLiteral(4));
            RealUnitaryOperator sqrt = new RealUnitaryOperator();

            sqrt.setOperand(new RealAttribute("ONU_2002", sds));
            sqrt.setOperator(RealUnitaryOperatorType.SQRT);

            width.setRightValue(sqrt);
            width.setOperator(RealBinaryOperatorType.MUL);


            mark.setSource(WellKnownName.CIRCLE);
            mark.setFill(new SolidFill());
            mark.setStroke(new PenStroke());

            mark.setViewBox(new ViewBox(width));

            collec.addGraphic(mark);
            

            PointSymbolizer pSymb = new PointSymbolizer();
            pSymb.setGraphic(collec);



            long fid;
            for (fid = 0; fid < ds.getRowCount(); fid++) {
                aSymb.draw(g2, sds, fid);
                pSymb.draw(g2, sds, fid);
            }
            g2.finalize();

            JFrame frame = new JFrame("Test AreaSymbolizer");

            // Create an instance of DisplayJAI.
            ImagePanel panel = new ImagePanel(img);

            frame.getContentPane().add(panel);

            // Set the closing operation so the application is finished.
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 500); // adjust the frame size.
            frame.setVisible(true); // show the frame.


            System.out.print("");


            Thread.sleep(20000);

        } catch (InterruptedException ex) {
            Logger.getLogger(AreaSymbolizerTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DriverLoadException ex) {
            Logger.getLogger(AreaSymbolizerTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DataSourceCreationException ex) {
            Logger.getLogger(AreaSymbolizerTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DriverException ex) {
            Logger.getLogger(AreaSymbolizerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
