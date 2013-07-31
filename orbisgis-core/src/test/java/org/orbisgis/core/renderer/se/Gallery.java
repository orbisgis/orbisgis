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
package org.orbisgis.core.renderer.se;

import com.vividsolutions.jts.geom.Envelope;
import java.awt.Color;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import org.orbisgis.core.AbstractTest;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.Layer;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.ImageRenderer;
import org.orbisgis.core.renderer.Renderer;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.sputilities.SFSUtilities;

/**
 *
 * @author Maxence Laurent
 */
public class Gallery extends AbstractTest {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 1000;

    public void template(String shapefile, String title, String stylePath, String source,
            String savePath, Envelope extent)
            throws IOException, InvalidStyle, SQLException {
            String tableReference = getDataManager().registerDataSource(new File(shapefile).toURI());
            MapTransform mt = new MapTransform();

            if (extent == null) {
                extent = SFSUtilities.getTableEnvelope(getConnection(), SFSUtilities.splitCatalogSchemaTableName(tableReference),"");
            }

            mt.resizeImage(WIDTH, HEIGHT);
            mt.setExtent(extent);
            Envelope effectiveExtent = mt.getAdjustedExtent();
            System.out.print("Extent: " + effectiveExtent);

            BufferedImage img = mt.getImage();
            Graphics2D g2 = (Graphics2D) img.getGraphics();

            g2.setRenderingHints(mt.getCurrentRenderContext().getRenderingHints());

            ILayer layer = new Layer("swiss", tableReference);

            Style style = new Style(layer, stylePath);
            layer.setStyle(0,style);

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
            throws ParameterException, IOException, InvalidStyle, SQLException {

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
