/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se;

import com.vividsolutions.jts.geom.Envelope;
import java.awt.Color;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.orbisgis.core.AbstractTest;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.Layer;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.ImageRenderer;
import org.orbisgis.core.renderer.Renderer;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.fill.HatchedFill;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;

/**
 *
 * @author maxence
 */
public class HatchIntesiveTest extends AbstractTest {

    private final static int WIDTH = 1000;
    private final static int HEIGHT = 1000;

    @Override
	public void setUp() throws Exception {
        super.setUp();
    }

    /**
     * We don't want negative distances
     */
    public void testDistanceContext() throws ParameterException {
            HatchedFill hf = new HatchedFill();
            hf.setDistance(new RealLiteral(-1));
            assertTrue(hf.getDistance().getValue(null, 1) == 0);
    }

    public void template(String shapefile, String title, String stylePath, String source,
            String savePath, Envelope extent)
            throws IOException, InvalidStyle, DriverException, DriverLoadException, DataSourceCreationException {
            DataSourceFactory dsf = new DataSourceFactory();
            DataSource ds = dsf.getDataSource(new File(shapefile));
            ds.open();

            SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);


            MapTransform mt = new MapTransform();


            if (extent == null) {
                extent = sds.getFullExtent();
            }

            mt.resizeImage(WIDTH, HEIGHT);
            mt.setExtent(extent);
            Envelope effectiveExtent = mt.getAdjustedExtent();
            System.out.print("Extent: " + effectiveExtent);

            BufferedImage img = mt.getImage();
            Graphics2D g2 = (Graphics2D) img.getGraphics();

            g2.setRenderingHints(mt.getCurrentRenderContext().getRenderingHints());

            ILayer layer = new Layer("swiss", sds);

            FeatureTypeStyle style = new FeatureTypeStyle(layer, stylePath);
            layer.setFeatureTypeStyle(style);

            Renderer renderer = new ImageRenderer();
            BufferedImage image = mt.getImage();

            Graphics graphics = image.getGraphics();
            graphics.setColor(Color.white);
            graphics.fillRect(0, 0, WIDTH, HEIGHT);


            renderer.draw(img, effectiveExtent , layer);

            if (source != null) {
                graphics.setColor(Color.black);
                graphics.drawChars(source.toCharArray(), 0, source.length(), 20, HEIGHT - 30);
            }

            if (savePath != null) {
                File file = new File(savePath);
                ImageIO.write(image, "png", file);
            }
    }

    public void drawMaps()
            throws ParameterException, IOException, InvalidStyle, DriverException, DriverLoadException, DataSourceCreationException {

        this.template("src/test/resources/org/orbisgis/core/renderer/se/HatchedFill/hatches_dataset.shp", "Hatches 0°",
               "src/test/resources/org/orbisgis/core/renderer/se/HatchedFill/hatches_0.se", null, "/tmp/hatches_000.png", null);

        this.template("src/test/resources/org/orbisgis/core/renderer/se/HatchedFill/hatches_dataset.shp", "Hatches 45°",
               "src/test/resources/org/orbisgis/core/renderer/se/HatchedFill/hatches_45.se", null, "/tmp/hatches_045.png", null);

        this.template("src/test/resources/org/orbisgis/core/renderer/se/HatchedFill/hatches_dataset.shp", "Hatches 90°",
               "src/test/resources/org/orbisgis/core/renderer/se/HatchedFill/hatches_90.se", null, "/tmp/hatches_090.png", null);

        this.template("src/test/resources/org/orbisgis/core/renderer/se/HatchedFill/hatches_dataset.shp", "Hatches 135°",
               "src/test/resources/org/orbisgis/core/renderer/se/HatchedFill/hatches_135.se", null, "/tmp/hatches_135.png", null);

        this.template("src/test/resources/org/orbisgis/core/renderer/se/HatchedFill/hatches_dataset.shp", "Hatches 180°",
               "src/test/resources/org/orbisgis/core/renderer/se/HatchedFill/hatches_180.se", null, "/tmp/hatches_180.png", null);

        this.template("src/test/resources/org/orbisgis/core/renderer/se/HatchedFill/hatches_dataset.shp", "Hatches 215°",
               "src/test/resources/org/orbisgis/core/renderer/se/HatchedFill/hatches_215.se", null, "/tmp/hatches_215.png", null);

        this.template("src/test/resources/org/orbisgis/core/renderer/se/HatchedFill/hatches_dataset.shp", "Hatches 270°",
               "src/test/resources/org/orbisgis/core/renderer/se/HatchedFill/hatches_270.se", null, "/tmp/hatches_270.png", null);

        this.template("src/test/resources/org/orbisgis/core/renderer/se/HatchedFill/hatches_dataset.shp", "Hatches 315°",
               "src/test/resources/org/orbisgis/core/renderer/se/HatchedFill/hatches_315.se", null, "/tmp/hatches_315.png", null);
    }
}
