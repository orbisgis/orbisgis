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
package org.orbisgis.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URI;
import java.sql.SQLException;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.layerModel.OwsMapContext;
import org.orbisgis.core.map.export.MapImageWriter;
import org.orbisgis.core.renderer.se.common.Description;
import org.orbisgis.progress.NullProgressMonitor;

/**
 *
 */
public class OwsMapContextTest extends AbstractTest  {

        
    @Test
	public void testRemoveSelectedLayer() throws Exception {
		MapContext mc = new OwsMapContext(getDataManager());
		mc.open(null);
		ILayer layer = mc.createLayer(getDataManager().registerDataSource(new URI("../src/test/resources/data/bv_sap.shp")));
		mc.getLayerModel().addLayer(layer);
		mc.setSelectedLayers(new ILayer[] { layer });
		assertTrue(mc.getSelectedLayers().length == 1);
		assertTrue(mc.getSelectedLayers()[0] == layer);
		mc.getLayerModel().remove(layer);
		assertTrue(mc.getSelectedLayers().length == 0);
		mc.close(null);
	}

    @Test
    public void testTitleAndDescriptionMapContext() throws Exception {
        String title = "Map of Ankh-Morpork";
        Locale locale = Locale.UK;
        String mapAbstract = "The principal city of the Sto Plains";
        // Define the map description
        Description mapDescription = new Description();
        mapDescription.addTitle(locale, title);
        mapDescription.addAbstract(locale, mapAbstract);
        MapContext mc = new OwsMapContext(getDataManager());
        mc.open(null);
        mc.setDescription(mapDescription);
        mc.close(null);
        ByteArrayOutputStream mapData = new ByteArrayOutputStream();
        mc.write(mapData);
        // Map data contain the serialisation
        // Read this data with another instance
        MapContext mc2 = new OwsMapContext(getDataManager());
        mc2.read(new ByteArrayInputStream(mapData.toByteArray()));
        mc2.open(null);
        // Test default title
        assertTrue(mc2.getTitle().equals(title));
        // Test the title with the provided locale
        assertTrue(mc2.getDescription().getTitle(locale).equals(title));
        // Test the abstract with the provided locale
        assertTrue(mc2.getDescription().getAbstract(locale).equals(mapAbstract));
        mc2.close(null);
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

    @Test
    public void makeLayerUriFromTableName() throws Exception {
        File dataFile = new File("../src/test/resources/data/landcover2000.shp").getCanonicalFile();
        File mapContextLocation = new File("landco_db.ows");
        String tableReference = getDataManager().registerDataSource(dataFile.toURI());
        MapContext mc = new OwsMapContext(getDataManager());
        mc.setLocation(mapContextLocation.toURI());
        mc.open(new NullProgressMonitor());
        ILayer layer = mc.createLayer(tableReference);
        mc.getLayerModel().addLayer(layer);
        mc.close(new NullProgressMonitor());
        try(FileOutputStream out = new FileOutputStream(mapContextLocation)) {
            mc.write(out);
        }
        // Open the map context
        MapContext newMapContext = new OwsMapContext(getDataManager());
        newMapContext.setLocation(mapContextLocation.toURI());
        try(FileInputStream in = new FileInputStream(mapContextLocation)) {
            newMapContext.read(in);
        }
        newMapContext.open(new NullProgressMonitor());
        assertEquals(1, newMapContext.getLayers().length);
        assertEquals(dataFile.toURI(), newMapContext.getLayers()[0].getDataUri());
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
}
