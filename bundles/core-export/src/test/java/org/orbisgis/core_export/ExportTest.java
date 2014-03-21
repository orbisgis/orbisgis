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
package org.orbisgis.core_export;

import org.h2gis.h2spatial.ut.SpatialH2UT;
import org.h2gis.h2spatialext.CreateSpatialExtension;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.internal.DataManagerImpl;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.coremap.layerModel.OwsMapContext;
import org.orbisgis.progress.NullProgressMonitor;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.sql.Connection;

public class ExportTest {

    private static Connection connection;
    private static DataManager dataManager;

    @BeforeClass
    public static void tearUpClass() throws Exception {
        DataSource dataSource = SpatialH2UT.createDataSource(ExportTest.class.getSimpleName(), false);
        connection = dataSource.getConnection();
        CreateSpatialExtension.initSpatialExtension(connection);
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
    public void exportToPNG() throws Exception {
        saveAs("mapExportTest.png", MapImageWriter.Format.PNG);
    }

    @Test
    public void exportToJEPG() throws Exception {
        saveAs("mapExportTest.jpg", MapImageWriter.Format.JPEG);
    }

    @Test
    public void exportToTIFF() throws Exception {
        saveAs("mapExportTest.tiff", MapImageWriter.Format.TIFF);
    }

    @Test
    public void exportToPDF() throws Exception {
        saveAs("mapExportTest.pdf", MapImageWriter.Format.PDF);
    }

    /**
     * Method to export the mapcontext as a file image
     * @param imagePath
     * @param format
     * @throws Exception
     */
    private void saveAs(String imagePath, MapImageWriter.Format format) throws Exception {
        MapContext mc = new OwsMapContext(getDataManager());
        mc.open(null);
        ILayer layer = mc.createLayer(URI.create("../src/test/resources/data/landcover2000.shp"));
        mc.getLayerModel().addLayer(layer);
        MapImageWriter mapImageWriter = new MapImageWriter(mc.getLayerModel());
        FileOutputStream out = new FileOutputStream(new File(imagePath));
        mapImageWriter.setFormat(format);
        mapImageWriter.write(out, new NullProgressMonitor());
    }
}