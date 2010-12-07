/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se;

import com.vividsolutions.jts.geom.Envelope;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import junit.framework.TestCase;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.persistance.se.SymbolizerType;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.graphic.ViewBox;
import org.orbisgis.core.renderer.se.graphic.WellKnownName;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;

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
            mt.resizeImage(1200, 800);

            Envelope extent = new Envelope(472212.0, 843821.0, 68786.0, 293586.0);

            mt.setExtent(extent);

            extent = mt.getAdjustedExtent();

            BufferedImage img = mt.getImage();
            Graphics2D g2 = img.createGraphics();

            g2.setRenderingHints(mt.getCurrentRenderContext().getRenderingHints());

            DataSourceFactory dsf = new DataSourceFactory();
            //DataSource ds = dsf.getDataSource(new File("../../datas2tests/shp/Swiss/g4districts98_region.shp"));
            DataSource ds = dsf.getDataSource(new File("/data/Geodata/Europe/EUcountries_them.shp"));
            ds.open();

            SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);

/*

            AreaSymbolizer aSymb = new AreaSymbolizer();
            AreaSymbolizer aSymb2 = new AreaSymbolizer();
            SolidFill complexSolidFill = new SolidFill();

            aSymb.setUom(Uom.MM);
            aSymb2.setUom(Uom.MM);


            Categorize2Color classesNon = new Categorize2Color(new ColorLiteral(Color.RED),
                    new ColorLiteral(Color.LIGHT_GRAY), new RealAttribute("ARMEMENTSO", ds));
            classesNon.addClass(new RealLiteral(47.0), new ColorLiteral(Color.ORANGE));


            Categorize2Color classesOui = new Categorize2Color(new ColorLiteral(Color.GREEN),
                    new ColorLiteral(Color.cyan), new RealAttribute("ARMEMENTSO", ds));
            classesOui.addClass(new RealLiteral(53.0), new ColorLiteral("#33772b"));


            Categorize2Color classes = new Categorize2Color(classesNon, new ColorLiteral(Color.darkGray),
                    new RealAttribute("ARMEMENTSO", ds));
            classes.addClass(new RealLiteral(50.0), classesOui);


            Recode2Color canton = new Recode2Color(new ColorLiteral(Color.YELLOW), new StringAttribute("AK", sds));
            canton.addMapItem("VD", classes);
            canton.addMapItem("ZH", new ColorLiteral(Color.blue));


            MarkGraphic mosMark = new MarkGraphic();
            mosMark.setUom(Uom.MM);
            mosMark.setFill(new SolidFill());
            mosMark.setViewBox(new ViewBox(new RealLiteral(10.0)));
            mosMark.setSource(WellKnownName.TRIANGLE);

            GraphicCollection mosCol = new GraphicCollection();

            mosCol.addGraphic(mosMark);

            GraphicFill gFill = new GraphicFill();
            gFill.setGraphic(mosCol);

            complexSolidFill.setOpacity(new RealLiteral(100.0));

            complexSolidFill.setColor(canton);

            aSymb2.setFill(complexSolidFill);

            DensityFill hFill = new DensityFill();


            RealAttribute prop = new RealAttribute("ONU_2002", sds);
            RealLiteral cst = new RealLiteral(100.0);
            RealBinaryOperator op = new RealBinaryOperator();
            op.setLeftValue(prop);
            op.setRightValue(cst);
            op.setOperator(RealBinaryOperatorType.DIV);

            PenStroke hatch = new PenStroke();
            hatch.setUom(Uom.MM);
            hatch.setWidth(new RealLiteral(0.5));
            hatch.setColor(new ColorLiteral(Color.black));
            hFill.setHatches(hatch);

            //hFill.setPercentageCovered(new RealLiteral(30.0)); // -> 1cm

            Recode2Real rRealAngle = new Recode2Real(new RealLiteral(45.0), new StringAttribute("AK", sds));
            rRealAngle.addMapItem("GR", new RealLiteral(-45));
            hFill.setHatchesOrientation(rRealAngle);

            Recode2Real rReal = new Recode2Real(new RealLiteral(0.0), new StringAttribute("AK", sds));
            rReal.addMapItem("GR", new RealLiteral(20.0));
            rReal.addMapItem("VS", new RealLiteral(10.0));

            hFill.setPercentageCovered(rReal); // -> 1cmc

            //aSymb.setFill(complexSolidFill);

            aSymb.setFill(hFill);
            aSymb.setStroke(new PenStroke());

            DensityFill hFill2 = new DensityFill();

            Recode2Real rRealAngle2 = new Recode2Real(new RealLiteral(-45.0), new StringAttribute("AK", sds));
            rRealAngle2.addMapItem("GR", new RealLiteral(45));


            hFill2.setHatchesOrientation(rRealAngle2);


            Recode2Real rReal2 = new Recode2Real(new RealLiteral(0.0), new StringAttribute("AK", sds));
            rReal2.addMapItem("GR", new RealLiteral(20.0));
            rReal2.addMapItem("VS", new RealLiteral(10.0));

            hFill2.setPercentageCovered(rReal2);

            PenStroke hatch2 = new PenStroke();
            hatch2.setWidth(new RealLiteral(0.5));

            hFill2.setHatches(hatch2);

            AreaSymbolizer aSymb3 = new AreaSymbolizer();
            aSymb3.setFill(hFill2);

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

            pie.setType(PieChartSubType.WHOLE);

            pie.setStroke(new PenStroke());
            pie.setDisplayValue(false);
            //pie.setRadius(new RealLiteral(7));
            
            RealBinaryOperator width = new RealBinaryOperator();
            width.setLeftValue(new RealLiteral(1));

            RealUnitaryOperator sqrt = new RealUnitaryOperator();

            sqrt.setOperand(new RealAttribute("ONU_2002", sds));
            sqrt.setOperator(RealUnitaryOperatorType.SQRT);

            width.setRightValue(sqrt);
            width.setOperator(RealBinaryOperatorType.MUL);

            pie.setRadius(width);

            TextGraphic tGraphic = new TextGraphic();
            StyledLabel sLabel = new StyledLabel();

            sLabel.setLabelText(new StringAttribute("NAME_ANSI", sds));

            tGraphic.setStyledLabel(sLabel);

            Transform tGr = new Transform();
            tGr.addTransformation(new Rotate(new RealLiteral(45.0)));
            
            tGr.addTransformation(new Scale(new RealLiteral(2.0)));

            tGr.addTransformation(new Rotate(new RealLiteral(45.0), new RealLiteral(1.0), new RealLiteral(1.0)));
            tGr.addTransformation(new Scale(new RealLiteral(2.0)));
            tGr.addTransformation(new Scale(new RealLiteral(0.25), new RealLiteral(1.25)));
            tGr.addTransformation(new Translate(new RealLiteral(10.0), null));
            tGr.addTransformation(new Translate(null, new RealLiteral(10.0)));
            */

            //tGraphic.setTransform(tGr);

            PointSymbolizer pSymb = new PointSymbolizer();
            pSymb.setUom(Uom.MM);

            GraphicCollection collec = pSymb.getGraphicCollection();

            MarkGraphic mosMark = new MarkGraphic();
            mosMark.setUom(Uom.MM);
            mosMark.setFill(new SolidFill());
            mosMark.setViewBox(new ViewBox(new RealLiteral(10.0)));
            mosMark.setSource(WellKnownName.TRIANGLE);

            //collec.addGraphic(pie);
            //collec.addGraphic(tGraphic);

            collec.addGraphic(mosMark);
            long fid;
            for (fid = 0; fid < ds.getRowCount(); fid++) {
                //aSymb2.draw(g2, sds, fid);
                //aSymb3.draw(g2, sds, fid);
                //aSymb.draw(g2, sds, fid);
                pSymb.draw(g2, sds.getFeature(fid), false, mt);
            }
            g2.finalize();

            System.out.println("Creation JFrame");

            JFrame frame = new JFrame("Test AreaSymbolizer");

            // Create an instance of DisplayJAI.
            ImagePanel panel = new ImagePanel(img);

            frame.getContentPane().add(panel);

            // Set the closing operation so the application is finished.
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800); // adjust the frame size.
            frame.setVisible(true); // show the frame.


            System.out.print("");
            System.out.println("Captain marshall");

            JAXBContext jaxbContext;

            jaxbContext = JAXBContext.newInstance(SymbolizerType.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                    new Boolean(true));
            //Validator validator = jaxbContext.createValidator();

            //System.out.println("Validator returned " + validator.validate(collection));
            System.out.println("Created a content tree " +
               "and marshalled it to jaxbOutput2.xml");
/*
            marshaller.marshal(((Symbolizer)(aSymb)).getJAXBElement(),
                    new FileOutputStream("/tmp/aSymb.xml"));

            marshaller.marshal(((Symbolizer)(aSymb2)).getJAXBElement(),
                  new FileOutputStream("/tmp/aSymb2.xml"));


            marshaller.marshal(((Symbolizer)(aSymb3)).getJAXBElement(),
                  new FileOutputStream("/tmp/aSymb3.xml"));

            marshaller.marshal(((Symbolizer)(pSymb)).getJAXBElement(),
                  new FileOutputStream("/tmp/pSymb.xml"));


            System.out.println("See output in /tmp/symbolizer.xml " ) ;
*/

            Thread.sleep(20000);




        } catch (JAXBException ex) {
            Logger.getLogger(AreaSymbolizerTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error");
        } catch (InterruptedException ex) {
            Logger.getLogger(AreaSymbolizerTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error");
        } catch (DriverLoadException ex) {
            Logger.getLogger(AreaSymbolizerTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error");
        } catch (DataSourceCreationException ex) {
            Logger.getLogger(AreaSymbolizerTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error");
        } catch (DriverException ex) {
            Logger.getLogger(AreaSymbolizerTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error");
        }
    }
}
