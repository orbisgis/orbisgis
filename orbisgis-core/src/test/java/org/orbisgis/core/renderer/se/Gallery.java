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
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 *
 * @author maxence
 */
public class Gallery extends AbstractTest {

    private final static int WIDTH = 1000;
    private final static int HEIGHT = 1000;

    @Override
	protected void setUp() throws Exception {
        super.setUp();
    }

    public void template(String shapefile, String title, String stylePath, String source,
            String savePath, Envelope extent)
            throws IOException, InvalidStyle, DriverException, DriverLoadException, DataSourceCreationException {
            DataSourceFactory dsf = new DataSourceFactory();
            DataSource ds = dsf.getDataSource(new File(shapefile));
            ds.open();

            SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);


            MapTransform mt = new MapTransform();


            if (extent == null)
                extent = sds.getFullExtent();

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

    public void testMaps()
            throws ParameterException, IOException, InvalidStyle, DriverException, DriverLoadException, DataSourceCreationException {

        this.template("../../datas2tests/shp/Swiss/g4districts98_region.shp", "SVG",
               "src/test/resources/org/orbisgis/core/renderer/se/svg.se", null, "/tmp/extG.png", null);


        this.template("../../datas2tests/shp/bigshape2D/cantons.shp", "Population canton (linéaire)",
                "src/test/resources/org/orbisgis/core/renderer/se/symbol_prop_canton_interpol_lin.se", null,
                "/tmp/pop_canton_lin.png", new Envelope(47680, 277971, 2265056, 2452630));


        this.template("../../datas2tests/shp/bigshape2D/cantons.shp", "Population canton (sqrt)",
                "src/test/resources/org/orbisgis/core/renderer/se/symbol_prop_canton_interpol_sqrt.se", null,
                "/tmp/pop_canton_sqrt.png", new Envelope(47680, 277971, 2265056, 2452630));


        this.template("../../datas2tests/shp/bigshape2D/cantons.shp", "Population canton (log)",
                "src/test/resources/org/orbisgis/core/renderer/se/symbol_prop_canton_interpol_log.se", null,
                "/tmp/pop_canton_log.png", new Envelope(47680, 277971, 2265056, 2452630));

        this.template("../../datas2tests/shp/bigshape2D/communes.shp", "DotMap Population communes",
                "src/test/resources/org/orbisgis/core/renderer/se/dotmap_communes.se", null, "/tmp/dot_map_communes.png", null);

        this.template("../../datas2tests/shp/Swiss/g4districts98_region.shp",
                "Pie à la con", "src/test/resources/org/orbisgis/core/renderer/se/Districts/pie.se", null, "/tmp/pies.png", null);


        this.template("../../datas2tests/shp/Swiss/g4districts98_region.shp",
                "Silouette", "src/test/resources/org/orbisgis/core/renderer/se/Districts/radar.se", null, "/tmp/radar.png", null);

        this.template("../../datas2tests/shp/Swiss/g4districts98_region.shp",
                "Oui EEE 1992 (%)", "src/test/resources/org/orbisgis/core/renderer/se/Districts/choro.se", null, "/tmp/choro_ouiEEE.png", null);
        this.template("../../datas2tests/shp/Swiss/g4districts98_region.shp",
                "Oui EEE 1992 (%)", "src/test/resources/org/orbisgis/core/renderer/se/Districts/density_hatch.se", null, "/tmp/denstiy_hatch_raw_ouiEEE.png", null);
        this.template("../../datas2tests/shp/Swiss/g4districts98_region.shp",
                "Oui EEE 1992 (%)", "src/test/resources/org/orbisgis/core/renderer/se/Districts/density_hatch_classif.se", null, "/tmp/denstiy_hatch_classif_ouiEEE.png", null);
        this.template("../../datas2tests/shp/Swiss/g4districts98_region.shp",
                "Oui EEE 1992 (%)", "src/test/resources/org/orbisgis/core/renderer/se/Districts/density_mark.se", null, "/tmp/denstiy_mark_ouiEEE.png", null);
        this.template("../../datas2tests/shp/Swiss/g4districts98_region.shp",
                "Oui EEE 1992 (%)", "src/test/resources/org/orbisgis/core/renderer/se/Districts/density_mark_classif.se", null, "/tmp/denstiy_mark_classif_ouiEEE.png", null);


    }
}
