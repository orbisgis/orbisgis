/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.coremap.renderer.se;

import com.vividsolutions.jts.geom.Envelope;
import java.awt.Color;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import javax.sql.DataSource;

import org.h2gis.functions.factory.H2GISDBFactory;
import org.h2gis.functions.factory.H2GISFunctions;
import org.h2gis.utilities.TableLocation;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.internal.DataManagerImpl;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.layerModel.Layer;
import org.orbisgis.coremap.map.MapTransform;
import org.orbisgis.coremap.renderer.ImageRenderer;
import org.orbisgis.coremap.renderer.Renderer;
import org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.coremap.renderer.se.parameter.ParameterException;
import org.h2gis.utilities.SFSUtilities;
import org.orbisgis.commons.progress.NullProgressMonitor;

/**
 *
 * @author Maxence Laurent
 */
public class Gallery {
    private static Connection connection;
    private static DataManager dataManager;

    @BeforeClass
    public static void tearUpClass() throws Exception {
        DataSource dataSource = H2GISDBFactory.createDataSource(Gallery.class.getSimpleName(), false);
        connection = dataSource.getConnection();
        H2GISFunctions.load(connection);
        dataManager = new DataManagerImpl(dataSource);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        connection.close();
        dataManager.dispose();
    }

    private DataManager getDataManager() {
        return dataManager;
    }

    private Connection getConnection() {
        return connection;
    }

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 1000;

    public void template(String shapefile, String title, String stylePath, String source,
            String savePath, Envelope extent)
            throws IOException, InvalidStyle, SQLException {
            String tableReference = getDataManager().registerDataSource(new File(shapefile).toURI());
            MapTransform mt = new MapTransform();

            if (extent == null) {
                extent = SFSUtilities.getTableEnvelope(getConnection(), TableLocation.parse(tableReference),"");
            }

            mt.resizeImage(WIDTH, HEIGHT);
            mt.setExtent(extent);
            Envelope effectiveExtent = mt.getAdjustedExtent();
            System.out.print("Extent: " + effectiveExtent);

            BufferedImage img = mt.getImage();
            Graphics2D g2 = (Graphics2D) img.getGraphics();

            g2.setRenderingHints(mt.getRenderingHints());

            ILayer layer = new Layer("swiss", tableReference, getDataManager());

            Style style = new Style(layer, stylePath);
            layer.setStyle(0,style);

            Renderer renderer = new ImageRenderer();
            BufferedImage image = mt.getImage();

            Graphics graphics = image.getGraphics();
            graphics.setColor(Color.white);
            graphics.fillRect(0, 0, WIDTH, HEIGHT);


            renderer.draw(img, effectiveExtent , layer, new NullProgressMonitor());

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
