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
import java.io.FileOutputStream;
import java.util.Locale;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
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
    
       
	@Override
        @Before
	public void setUp() throws Exception {
		super.setUp();
		registerDataManager();
	}
        
        
        @Test
	public void testRemoveSelectedLayer() throws Exception {
		MapContext mc = new OwsMapContext();
		mc.open(null);
		ILayer layer = mc.createLayer(
                        getDataSourceFromPath("src/test/resources/data/bv_sap.shp"));
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
                MapContext mc = new OwsMapContext();
                mc.open(null);
                mc.setDescription(mapDescription);
                mc.close(null);
                ByteArrayOutputStream mapData = new ByteArrayOutputStream();
                mc.write(mapData);
                // Map data contain the serialisation
                // Read this data with another instance
                MapContext mc2 = new OwsMapContext();
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

    @Test
    public void exportToImage() throws Exception {
        MapContext mc = new OwsMapContext();
        mc.open(null);
        ILayer layer = mc.createLayer(
                getDataSourceFromPath("src/test/resources/data/landcover2000.shp"));
        mc.getLayerModel().addLayer(layer);
        MapImageWriter mapImageWriter = new MapImageWriter(mc.getLayerModel());
        FileOutputStream out = new FileOutputStream(new File("target/mapExportTest.png"));
        mapImageWriter.setFormat(MapImageWriter.Format.PNG);
        mapImageWriter.write(out, new NullProgressMonitor());
    }
}
