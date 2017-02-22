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
package org.orbisgis.core_export;

import org.h2gis.functions.factory.H2GISDBFactory;
import org.h2gis.functions.factory.H2GISFunctions;
import org.h2gis.utilities.SFSUtilities;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.internal.DataManagerImpl;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.coremap.layerModel.OwsMapContext;
import org.orbisgis.commons.progress.NullProgressMonitor;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;

public class ExportTest {

    private static Connection connection;
    private static DataManager dataManager;

    @BeforeClass
    public static void tearUpClass() throws Exception {
        DataSource dataSource = SFSUtilities.wrapSpatialDataSource(H2GISDBFactory.createDataSource(ExportTest.class.getSimpleName(), false));
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
    public void exportToPNG() throws Exception {
        saveAs("target/mapExportTest.png", MapImageWriter.Format.PNG);
    }

    @Test
    public void exportToJEPG() throws Exception {
        saveAs("target/mapExportTest.jpg", MapImageWriter.Format.JPEG);
    }

    @Test
    public void exportToTIFF() throws Exception {
        saveAs("target/mapExportTest.tiff", MapImageWriter.Format.TIFF);
    }

    @Test
    public void exportToPDF() throws Exception {
        saveAs("target/mapExportTest.pdf", MapImageWriter.Format.PDF);
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
        ILayer layer = mc.createLayer(ExportTest.class.getResource("landcover2000.shp").toURI());
        mc.getLayerModel().addLayer(layer);
        MapImageWriter mapImageWriter = new MapImageWriter(mc.getLayerModel());
        FileOutputStream out = new FileOutputStream(new File(imagePath));
        mapImageWriter.setFormat(format);
        mapImageWriter.write(out, new NullProgressMonitor());
    }
}
