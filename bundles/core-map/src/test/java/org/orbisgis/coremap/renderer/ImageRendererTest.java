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
package org.orbisgis.coremap.renderer;

import org.h2gis.functions.factory.H2GISDBFactory;
import org.h2gis.functions.factory.H2GISFunctions;
import org.h2gis.utilities.SFSUtilities;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.internal.DataManagerImpl;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.coremap.layerModel.OwsMapContext;
import org.orbisgis.coremap.map.MapTransform;
import org.orbisgis.commons.progress.NullProgressMonitor;

import javax.sql.DataSource;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.sql.Connection;

import static org.junit.Assert.assertEquals;

/**
 * Rendering test for image renderer
 * @author Nicolas Fortin
 */
public class ImageRendererTest {
    private static Connection connection;
    private static DataManager dataManager;

    @BeforeClass
    public static void tearUpClass() throws Exception {
        DataSource dataSource = SFSUtilities.wrapSpatialDataSource(H2GISDBFactory.createDataSource(ImageRendererTest.class.getSimpleName(), false));
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


    @Test
    public void drawLandcover() throws Exception {
        // Open map context and data
        MapContext mc = new OwsMapContext(getDataManager());
        URI owsFile = ImageRendererTest.class.getResource("../../../../data/landcover2000.ows").toURI();
        mc.setLocation(owsFile);
        mc.read(new FileInputStream(new File(owsFile)));
        mc.open(new NullProgressMonitor());
        // Draw in buffered image
        MapTransform mapTransform = new MapTransform();
        mapTransform.setExtent(mc.getBoundingBox());
        BufferedImage outImage = new BufferedImage(50, 150, BufferedImage.TYPE_4BYTE_ABGR);
        mapTransform.setImage(outImage);
        mc.draw(mapTransform, new NullProgressMonitor());
        assertEquals(new Color(0, 204, 102).getRGB(), outImage.getRGB(23, 86));
        assertEquals(new Color(204,204,0).getRGB(), outImage.getRGB(30, 112));
    }
}
