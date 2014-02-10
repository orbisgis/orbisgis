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
import java.sql.Connection;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.layerModel.OwsMapContext;
import org.orbisgis.progress.NullProgressMonitor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MapContextTest extends AbstractTest {


    @Test
	public void testRemoveSelectedLayer() throws Exception {
		MapContext mc = new OwsMapContext(getDataManager());
		mc.open(null);
		ILayer layer = mc.createLayer(getDataManager().registerDataSource(
				new File("../src/test/resources/data/bv_sap.shp").toURI()));
		mc.getLayerModel().addLayer(layer);
		mc.setSelectedLayers(new ILayer[] { layer });
		assertTrue(mc.getSelectedLayers().length == 1);
		assertTrue(mc.getSelectedLayers()[0] == layer);
		mc.getLayerModel().remove(layer);
		assertTrue(mc.getSelectedLayers().length == 0);
		mc.close(null);
	}

    @Test
	public void testSetBadLayerSelection() throws Exception {
		MapContext mc = new OwsMapContext(getDataManager());
		mc.open(null);
		ILayer layer = mc.createLayer(getDataManager().registerDataSource(
				new File("../src/test/resources/data/bv_sap.shp").toURI()));
		ILayer layer2 = mc.createLayer(getDataManager().registerDataSource(
				new File("../src/test/resources/data/linestring.shp").toURI()));
		mc.getLayerModel().addLayer(layer);
		mc.setSelectedLayers(new ILayer[] { layer2 });
		assertTrue(mc.getSelectedLayers().length == 0);
		mc.setSelectedLayers(new ILayer[] { layer });
		assertTrue(mc.getSelectedLayers().length == 1);
		mc.close(null);
	}

        @Test
	public void testRemoveActiveLayer() throws Exception {
		MapContext mc = new OwsMapContext(getDataManager());
		mc.open(null);
		ILayer layer = mc.createLayer(getDataManager().registerDataSource(
				new File("../src/test/resources/data/bv_sap.shp").toURI()));
		mc.getLayerModel().addLayer(layer);
		mc.setActiveLayer(layer);
		mc.getLayerModel().remove(layer);
		assertTrue(mc.getActiveLayer() == null);
		mc.close(null);
	}

    @Test
	public void testSaveAndRecoverTwoNestedCollections() throws Exception {
                //Define a MapContext
		MapContext mc = new OwsMapContext(getDataManager());
		mc.open(null);
        ILayer layer1 = mc.createLayerCollection("a");
        ILayer layer2 = mc.createLayerCollection("a");
        ILayer layer3 = mc.createLayer("linestring",
                getDataManager().registerDataSource(new File("../src/test/resources/data/linestring.shp").toURI()));
		mc.getLayerModel().addLayer(layer1);
		layer1.addLayer(layer2);
		layer2.addLayer(layer3);
                ByteArrayOutputStream map = new ByteArrayOutputStream();
		mc.write(map);
		mc.close(null);
                
                //Define a new MapContext from previous MapContext serialisation
		mc = new OwsMapContext(getDataManager());
		mc.read(new ByteArrayInputStream(map.toByteArray()));
		mc.open(null);
		ILayer layer1_ = mc.getLayerModel().getLayer(0);
		assertEquals(1, layer1_.getLayerCount());
        assertEquals(1, layer1_.getLayer(0).getLayerCount());
        assertEquals("linestring", layer1_.getLayer(0).getLayer(0).getName());
		mc.close(null);
	}

        @Test
	public void testOperateOnClosedMapContext() throws Exception {
		MapContext mc = new OwsMapContext(getDataManager());
		try {
			mc.getSelectedLayers();
			assertTrue(false);
		} catch (IllegalStateException e) {
		}
		try {
			mc.draw(null, null);
			assertTrue(false);
		} catch (IllegalStateException e) {
		}
		try {
			mc.getActiveLayer();
			assertTrue(false);
		} catch (IllegalStateException e) {
		}
		try {
			mc.getLayerModel();
			assertTrue(false);
		} catch (IllegalStateException e) {
		}
		try {
			mc.getLayers();
			assertTrue(false);
		} catch (IllegalStateException e) {
		}
		try {
			mc.setActiveLayer(null);
			assertTrue(false);
		} catch (IllegalStateException e) {
		}
		try {
			mc.setSelectedLayers(null);
			assertTrue(false);
		} catch (IllegalStateException e) {
		}
	}

        @Test
	public void testIsOpen() throws Exception {
		MapContext mc = new OwsMapContext(getDataManager());
		assertTrue(!mc.isOpen());
		mc.open(new NullProgressMonitor());
		assertTrue(mc.isOpen());
	}

        @Test
	public void testOpenTwice() throws Exception {
		MapContext mc = new OwsMapContext(getDataManager());
		mc.open(new NullProgressMonitor());
		try {
			mc.open(new NullProgressMonitor());
			assertTrue(false);
		} catch (IllegalStateException e) {
		}
	}

        @Test
	public void testCloseClosedMap() throws Exception {
		MapContext mc = new OwsMapContext(getDataManager());
		try {
			mc.close(new NullProgressMonitor());
			assertTrue(false);
		} catch (IllegalStateException e) {
		}
	}

        @Test
	public void testWriteOnOpenMap() throws Exception {
		MapContext mc = new OwsMapContext(getDataManager());
                
                

                ByteArrayOutputStream map = new ByteArrayOutputStream();
		mc.write(map);
		mc.open(new NullProgressMonitor());
		try {
                        mc.read(new ByteArrayInputStream(map.toByteArray()));
			assertTrue(false);
		} catch (IllegalStateException e) {
		}
	}

    @Test
	public void testReadWriteMapContext() throws Exception {
		MapContext mc = getSampleMapContext();

                ByteArrayOutputStream map = new ByteArrayOutputStream();
		mc.write(map);

		MapContext mc2 = new OwsMapContext(getDataManager());
                mc2.read(new ByteArrayInputStream(map.toByteArray()));
		mc2.open(null);
		assertTrue(mc2.getLayerModel().getLayerCount() == 1);
		mc2.close(null);

                mc2.read(new ByteArrayInputStream(map.toByteArray()));
		mc2.open(null);
		assertTrue(mc2.getLayerModel().getLayerCount() == 1);
		mc2.close(null);
	}

	private MapContext getSampleMapContext() throws LayerException {
		MapContext mc = new OwsMapContext(getDataManager());
		mc.open(null);
		ILayer layer = mc.createLayerCollection("a");
		mc.getLayerModel().addLayer(layer);
		mc.close(null);
		return mc;
	}

        @Test
	public void testSetJAXBOpenTwice() throws Exception {
		MapContext mc = getSampleMapContext();
                ByteArrayOutputStream map = new ByteArrayOutputStream();
		mc.write(map);

		MapContext mc2 = new OwsMapContext(getDataManager());
		mc2.read(new ByteArrayInputStream(map.toByteArray()));
		mc2.open(null);
		assertTrue(mc2.getLayerModel().getLayerCount() == 1);
		ILayer layer = mc2.createLayerCollection("b");
		mc2.getLayerModel().addLayer(layer);
		assertTrue(mc2.getLayerModel().getLayerCount() == 2);
		mc2.close(null);

		mc2.open(null);
		assertTrue(mc2.getLayerModel().getLayerCount() == 2);
		mc2.close(null);
	}

//        @Test
//	public void testLegendPersistenceOpeningTwice() throws Exception {
//		MapContext mc = new OwsMapContext();
//		mc.open(null);
//		ILayer layer = mc.createLayer("bv_sap",
//				new File("src/test/resources/data/bv_sap.shp"));
//		mc.getLayerModel().addLayer(layer);
//		UniqueSymbolLegend legend = LegendFactory.createUniqueSymbolLegend();
//		Symbol symbol = SymbolFactory.createPolygonSymbol(Color.pink);
//		legend.setSymbol(symbol);
//		layer.setLegend(legend);
//		assertTrue(legend.getSymbol().getPersistentProperties().equals(
//				symbol.getPersistentProperties()));
//		mc.close(null);
//		MapContext mc2 = new OwsMapContext();
//		mc2.setJAXBObject(mc.getJAXBObject());
//		mc2.open(null);
//		assertTrue(legend.getSymbol().getPersistentProperties().equals(
//				symbol.getPersistentProperties()));
//		mc2.close(null);
//		mc2.open(null);
//		layer = mc2.getLayerModel().getLayerByName("bv_sap");
//		legend = (UniqueSymbolLegend) layer.getVectorLegend()[0];
//		assertTrue(legend.getSymbol().getPersistentProperties().equals(
//				symbol.getPersistentProperties()));
//		mc2.close(null);
//	}

        @Test
	public void testWriteAfterReadModifyAndClose() throws Exception {
		MapContext mc = getSampleMapContext();
                ByteArrayOutputStream map = new ByteArrayOutputStream();
		mc.write(map);

		MapContext mc2 = new OwsMapContext(getDataManager());
		// set DATA
                mc2.read(new ByteArrayInputStream(map.toByteArray()));
		// modify
		mc2.open(null);
		assertTrue(mc2.getLayerModel().getLayerCount() == 1);
		ILayer layer = mc2.createLayerCollection("b");
		mc2.getLayerModel().addLayer(layer);
		assertTrue(mc2.getLayerModel().getLayerCount() == 2);
		// close
		mc2.close(null);
                ByteArrayOutputStream map2 = new ByteArrayOutputStream();
		mc2.write(map2);
		// check obj is good
		MapContext mc3 = new OwsMapContext(getDataManager());
		mc3.read(new ByteArrayInputStream(map2.toByteArray()));
		mc3.open(null);
		assertTrue(mc3.getLayerModel().getLayerCount() == 2);
		mc3.close(null);
	}

        @Test
	public void testActiveLayerClearedOnClose() throws Exception {
		MapContext mc = new OwsMapContext(getDataManager());
		mc.open(null);
		ILayer layer = mc.createLayer(getDataManager().registerDataSource(
				new File("../src/test/resources/data/bv_sap.shp").toURI()));
		mc.getLayerModel().addLayer(layer);
		mc.setActiveLayer(layer);
		mc.close(null);
		mc.open(null);
		assertTrue(mc.getActiveLayer() == null);
	}


    @Test
	public void testMapOpensWithBadLayer() throws Exception {
		File shp = new File("bv_sap.shp");
		File dbf = new File("bv_sap.dbf");
		File shx = new File("bv_sap.shx");
		File originalShp = new File("../src/test/resources/data/bv_sap.shp");
		FileUtils.copyFile(originalShp, shp);
		FileUtils.copyFile(new File("../src/test/resources/data/bv_sap.dbf"), dbf);
		FileUtils.copyFile(new File("../src/test/resources/data/bv_sap.shx"), shx);
		MapContext mc = new OwsMapContext(getDataManager());
		mc.open(null);
        ILayer layer = mc.createLayer("youhou",shp.toURI());
        String linkedTable = layer.getTableReference();
		mc.getLayerModel().addLayer(layer);
		mc.getLayerModel().addLayer(mc.createLayer("yaha",originalShp.toURI()));
		mc.close(null);
        try {
            assertTrue(shp.delete());
            assertTrue(dbf.delete());
            assertTrue(shx.delete());
            mc.open(null);
            // Unreachable data does not mean delete layer style
            assertEquals(2, mc.getLayerModel().getLayerCount());
            mc.close(null);
        } finally {
            try(Connection connection = dataSource.getConnection()) {
                if(!linkedTable.isEmpty()) {
                    connection.createStatement().execute("DROP TABLE IF EXISTS "+linkedTable);
                }
            }
        }
	}

//    @Test(expected = IllegalArgumentException.class)
//	public void testExportSVG() throws Exception {
//		MapContext mc = new OwsMapContext();
//		mc.open(null);
//		ILayer layer = mc.createLayer("bv",
//                getDataManager().registerDataSource(new File("../src/test/resources/data/bv_sap.shp").toURI()));
//		mc.getLayerModel().addLayer(layer);
//		ILayer layer2 = mc.createLayer("linestring",
//                getDataManager().registerDataSource(new File("../src/test/resources/data/linestring.shp").toURI()));
//		mc.getLayerModel().addLayer(layer2);
//
//		MapExportManager mem = Services.getService(MapExportManager.class);
//		Envelope envelope = mc.getLayerModel().getEnvelope();
//        File outFile = new File("output.svg");
//        if(outFile.exists()) {
//            assertTrue(outFile.delete());
//        } else {
//            outFile.mkdir();
//        }
//        assertTrue("Cannot write to ut folder:\n" +outFile.getAbsolutePath(), outFile.canWrite());
//		FileOutputStream outStream = new FileOutputStream(outFile);
//		mem.exportSVG(mc, outStream, 10, 10, new Envelope(new Coordinate(
//				306260, 2251944), new Coordinate(310000, 2253464)), null, 72);
//		outStream.close();
//		mc.close(null);
//		mem.exportSVG(mc, outStream, 10, 10, envelope, null, 72);
//	}

}
