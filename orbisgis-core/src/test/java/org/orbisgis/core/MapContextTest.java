/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core;

import org.junit.Before;
import org.junit.Test;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;

import org.gdms.data.DataSource;
import org.gdms.source.SourceManager;
import org.orbisgis.core.layerModel.DefaultMapContext;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.map.export.MapExportManager;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.renderer.legend.carto.LabelLegend;
import org.orbisgis.core.renderer.legend.carto.LegendFactory;
import org.orbisgis.core.renderer.legend.carto.UniqueSymbolLegend;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.renderer.symbol.SymbolFactory;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.utils.FileUtils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

import static org.junit.Assert.*;

public class MapContextTest extends AbstractTest {

        @Override
        @Before
        public void setUp() throws Exception {
                super.setUp();
                registerDataManager();
        }

        @Test
        public void testRemoveSelectedLayer() throws Exception {
                MapContext mc = new DefaultMapContext();
                mc.open(null);
                ILayer layer = getDataManager().createLayer(
                        new File("src/test/resources/data/bv_sap.shp"));
                mc.getLayerModel().addLayer(layer);
                mc.setSelectedLayers(new ILayer[]{layer});
                assertEquals(mc.getSelectedLayers().length, 1);
                assertEquals(mc.getSelectedLayers()[0], layer);
                mc.getLayerModel().remove(layer);
                assertEquals(mc.getSelectedLayers().length, 0);
                mc.close(null);
        }

        @Test
        public void testSetBadLayerSelection() throws Exception {
                MapContext mc = new DefaultMapContext();
                mc.open(null);
                ILayer layer = getDataManager().createLayer(
                        new File("src/test/resources/data/bv_sap.shp"));
                ILayer layer2 = getDataManager().createLayer(
                        new File("src/test/resources/data/linestring.shp"));
                mc.getLayerModel().addLayer(layer);
                mc.setSelectedLayers(new ILayer[]{layer2});
                assertEquals(mc.getSelectedLayers().length, 0);
                mc.setSelectedLayers(new ILayer[]{layer});
                assertEquals(mc.getSelectedLayers().length, 1);
                mc.close(null);
        }

        @Test
        public void testRemoveActiveLayer() throws Exception {
                MapContext mc = new DefaultMapContext();
                mc.open(null);
                ILayer layer = getDataManager().createLayer(
                        new File("src/test/resources/data/bv_sap.shp"));
                mc.getLayerModel().addLayer(layer);
                mc.setActiveLayer(layer);
                mc.getLayerModel().remove(layer);
                assertNull(mc.getActiveLayer());
                mc.close(null);
        }

        @Test
        public void testSaveAndRecoverMapContext() throws Exception {
                MapContext mc = new DefaultMapContext();
                mc.open(null);
                ILayer layer1 = getDataManager().createLayer(
                        new File("src/test/resources/data/linestring.shp"));
                ILayer layer2 = getDataManager().createLayer(
                        new File("src/test/resources/data/bv_sap.shp"));
                mc.getLayerModel().addLayer(layer1);
                mc.getLayerModel().addLayer(layer2);
                Symbol sym1 = layer1.getVectorLegend()[0].getSymbol(layer1.getSpatialDataSource(), 0);
                Symbol sym2 = layer2.getVectorLegend()[0].getSymbol(layer2.getSpatialDataSource(), 0);
                Object persistence = mc.getJAXBObject();
                DefaultMapContext mc2 = new DefaultMapContext();
                mc2.setJAXBObject(persistence);
                mc2.open(null);
                assertEquals(mc2.getLayers().length, 2);
                Legend legend1 = mc2.getLayerModel().getLayer(0).getVectorLegend()[0];
                assertEquals(legend1.getSymbol(layer1.getSpatialDataSource(), 0).getPersistentProperties(), sym1.getPersistentProperties());
                Legend legend2 = mc2.getLayerModel().getLayer(1).getVectorLegend()[0];
                assertEquals(legend2.getSymbol(layer2.getSpatialDataSource(), 0).getPersistentProperties(), sym2.getPersistentProperties());
                mc.close(null);
                mc2.close(null);
        }

        @Test
        public void testSaveAndRecoverTwoNestedCollections() throws Exception {
                MapContext mc = new DefaultMapContext();
                mc.open(null);
                ILayer layer1 = getDataManager().createLayerCollection("a");
                ILayer layer2 = getDataManager().createLayerCollection("a");
                ILayer layer3 = getDataManager().createLayer("linestring",
                        new File("src/test/resources/data/linestring.shp"));
                mc.getLayerModel().addLayer(layer1);
                layer1.addLayer(layer2);
                layer2.addLayer(layer3);
                Object persistence = mc.getJAXBObject();
                mc.close(null);
                mc = new DefaultMapContext();
                mc.setJAXBObject(persistence);
                mc.open(null);
                ILayer layer1_ = mc.getLayerModel().getLayer(0);
                assertEquals(layer1_.getLayerCount(), 1);
                assertEquals(layer1_.getLayer(0).getLayerCount(), 1);
                assertEquals(layer1_.getLayer(0).getLayer(0).getName(), "linestring");
                mc.close(null);
        }

        @Test
        public void testOperateOnClosedMapContext() throws Exception {
                MapContext mc = new DefaultMapContext();
                try {
                        mc.getSelectedLayers();
                        fail();
                } catch (IllegalStateException e) {
                }
                try {
                        mc.draw(null, null, null);
                        fail();
                } catch (IllegalStateException e) {
                }
                try {
                        mc.getActiveLayer();
                        fail();
                } catch (IllegalStateException e) {
                }
                try {
                        mc.getLayerModel();
                        fail();
                } catch (IllegalStateException e) {
                }
                try {
                        mc.getLayers();
                        fail();
                } catch (IllegalStateException e) {
                }
                try {
                        mc.setActiveLayer(null);
                        fail();
                } catch (IllegalStateException e) {
                }
                try {
                        mc.setSelectedLayers(null);
                        fail();
                } catch (IllegalStateException e) {
                }
        }

        @Test
        public void testIsOpen() throws Exception {
                MapContext mc = new DefaultMapContext();
                assertFalse(mc.isOpen());
                mc.open(new NullProgressMonitor());
                assertTrue(mc.isOpen());
        }

        @Test
        public void testOpenTwice() throws Exception {
                MapContext mc = new DefaultMapContext();
                mc.open(new NullProgressMonitor());
                try {
                        mc.open(new NullProgressMonitor());
                        fail();
                } catch (IllegalStateException e) {
                }
        }

        @Test
        public void testCloseClosedMap() throws Exception {
                MapContext mc = new DefaultMapContext();
                try {
                        mc.close(new NullProgressMonitor());
                        fail();
                } catch (IllegalStateException e) {
                }
        }

        @Test
        public void testSetJAXBOnOpenMap() throws Exception {
                MapContext mc = new DefaultMapContext();
                Object obj = mc.getJAXBObject();
                mc.open(new NullProgressMonitor());
                try {
                        mc.setJAXBObject(obj);
                        fail();
                } catch (IllegalStateException e) {
                }
        }

        @Test
        public void testRemoveSource() throws Exception {
                MapContext mc = new DefaultMapContext();
                mc.open(null);
                ILayer layer = getDataManager().createLayer("linestring",
                        new File("src/test/resources/data/linestring.shp"));
                mc.getLayerModel().addLayer(layer);
                getDataManager().getSourceManager().remove("linestring");
                assertEquals(mc.getLayerModel().getLayerCount(), 0);
                mc.close(null);
        }

        @Test
        public void testGetJAXBObject() throws Exception {
                MapContext mc = getSampleMapContext();
                Object jaxbObj = mc.getJAXBObject();

                MapContext mc2 = new DefaultMapContext();
                mc2.setJAXBObject(jaxbObj);
                jaxbObj = mc2.getJAXBObject();
                mc2.open(null);
                assertEquals(mc2.getLayerModel().getLayerCount(), 1);
                mc2.close(null);

                mc2.setJAXBObject(jaxbObj);
                jaxbObj = mc2.getJAXBObject();
                mc2.open(null);
                assertEquals(mc2.getLayerModel().getLayerCount(), 1);
                mc2.close(null);
        }

        private MapContext getSampleMapContext() throws LayerException {
                MapContext mc = new DefaultMapContext();
                mc.open(null);
                ILayer layer = getDataManager().createLayerCollection("a");
                mc.getLayerModel().addLayer(layer);
                mc.close(null);
                return mc;
        }

        @Test
        public void testSetJAXBOpenTwice() throws Exception {
                MapContext mc = getSampleMapContext();
                Object jaxbObj = mc.getJAXBObject();

                MapContext mc2 = new DefaultMapContext();
                mc2.setJAXBObject(jaxbObj);
                mc2.open(null);
                assertEquals(mc2.getLayerModel().getLayerCount(), 1);
                ILayer layer = getDataManager().createLayerCollection("b");
                mc2.getLayerModel().addLayer(layer);
                assertEquals(mc2.getLayerModel().getLayerCount(), 2);
                mc2.close(null);

                mc2.open(null);
                assertEquals(mc2.getLayerModel().getLayerCount(), 2);
                mc2.close(null);
        }

        @Test
        public void testLegendPersistenceOpeningTwice() throws Exception {
                MapContext mc = new DefaultMapContext();
                mc.open(null);
                ILayer layer = getDataManager().createLayer("bv_sap",
                        new File("src/test/resources/data/bv_sap.shp"));
                mc.getLayerModel().addLayer(layer);
                UniqueSymbolLegend legend = LegendFactory.createUniqueSymbolLegend();
                Symbol symbol = SymbolFactory.createPolygonSymbol(Color.pink);
                legend.setSymbol(symbol);
                layer.setLegend(legend);
                assertEquals(legend.getSymbol().getPersistentProperties(), symbol.getPersistentProperties());
                mc.close(null);
                MapContext mc2 = new DefaultMapContext();
                mc2.setJAXBObject(mc.getJAXBObject());
                mc2.open(null);
                assertEquals(legend.getSymbol().getPersistentProperties(), symbol.getPersistentProperties());
                mc2.close(null);
                mc2.open(null);
                layer = mc2.getLayerModel().getLayerByName("bv_sap");
                legend = (UniqueSymbolLegend) layer.getVectorLegend()[0];
                assertEquals(legend.getSymbol().getPersistentProperties(), symbol.getPersistentProperties());
                mc2.close(null);
        }

        @Test
        public void testgetJAXBAfterSetModifyAndClose() throws Exception {
                MapContext mc = getSampleMapContext();
                Object jaxbObj = mc.getJAXBObject();

                MapContext mc2 = new DefaultMapContext();
                // set JAXB
                mc2.setJAXBObject(jaxbObj);
                // modify
                mc2.open(null);
                assertEquals(mc2.getLayerModel().getLayerCount(), 1);
                ILayer layer = getDataManager().createLayerCollection("b");
                mc2.getLayerModel().addLayer(layer);
                assertEquals(mc2.getLayerModel().getLayerCount(), 2);
                // close
                mc2.close(null);
                Object obj = mc2.getJAXBObject();
                // check obj is good
                MapContext mc3 = new DefaultMapContext();
                mc3.setJAXBObject(obj);
                mc3.open(null);
                assertEquals(mc3.getLayerModel().getLayerCount(), 2);
                mc3.close(null);
        }

        @Test
        public void testActiveLayerClearedOnClose() throws Exception {
                MapContext mc = new DefaultMapContext();
                mc.open(null);
                ILayer layer = getDataManager().createLayer(
                        new File("src/test/resources/data/bv_sap.shp"));
                mc.getLayerModel().addLayer(layer);
                mc.setActiveLayer(layer);
                mc.close(null);
                mc.open(null);
                assertNull(mc.getActiveLayer());
        }

        @Test
        public void testGetJAXBAfterModify() throws Exception {
                MapContext mc = new DefaultMapContext();
                mc.open(null);
                mc.getJAXBObject();
                ILayer layer = getDataManager().createLayer(
                        new File("src/test/resources/data/bv_sap.shp"));
                mc.getLayerModel().addLayer(layer);
                org.orbisgis.core.layerModel.persistence.MapContext xmlMC = (org.orbisgis.core.layerModel.persistence.MapContext) mc.getJAXBObject();
                assertEquals(xmlMC.getLayerCollection().getLayer().size(), 1);
                mc.close(null);
        }

        @Test
        public void testMapOpensWithBadLayer() throws Exception {
                File shp = new File("target/bv_sap.shp");
                File dbf = new File("target/bv_sap.dbf");
                File shx = new File("target/bv_sap.shx");
                File originalShp = new File("src/test/resources/data/bv_sap.shp");
                FileUtils.copy(originalShp, shp);
                FileUtils.copy(new File("src/test/resources/data/bv_sap.dbf"), dbf);
                FileUtils.copy(new File("src/test/resources/data/bv_sap.shx"), shx);
                MapContext mc = new DefaultMapContext();
                mc.open(null);
                mc.getLayerModel().addLayer(getDataManager().createLayer(shp));
                mc.getLayerModel().addLayer(getDataManager().createLayer(originalShp));
                mc.close(null);
                shp.delete();
                dbf.delete();
                shx.delete();
                failErrorManager.setIgnoreWarnings(true);
                failErrorManager.setIgnoreErrors(true);
                mc.open(null);
                failErrorManager.setIgnoreWarnings(false);
                failErrorManager.setIgnoreErrors(false);
                assertEquals(mc.getLayerModel().getLayerCount(), 1);
                mc.close(null);
        }

        @Test
        public void testRemoveFieldUsedInLegend() throws Exception {
                File shp = new File("target/bv_sap.shp");
                File dbf = new File("target/bv_sap.dbf");
                File shx = new File("target/bv_sap.shx");
                FileUtils.copy(new File("src/test/resources/data/bv_sap.shp"), shp);
                FileUtils.copy(new File("src/test/resources/data/bv_sap.dbf"), dbf);
                FileUtils.copy(new File("src/test/resources/data/bv_sap.shx"), shx);
                MapContext mc = new DefaultMapContext();
                mc.open(null);
                ILayer layer = getDataManager().createLayer(shp);
                mc.getLayerModel().addLayer(layer);
                LabelLegend labelLegend = LegendFactory.createLabelLegend();
                int legendFieldIndex = 1;
                labelLegend.setClassificationField(layer.getSpatialDataSource().getFieldName(
                        legendFieldIndex));
                layer.setLegend(labelLegend);
                mc.close(null);

                DataSource ds = getDataManager().getDataSourceFactory().getDataSource(
                        shp);
                ds.open();
                ds.removeField(legendFieldIndex);
                ds.commit();
                ds.close();

                failErrorManager.setIgnoreWarnings(true);
                mc.open(null);
                failErrorManager.setIgnoreWarnings(false);
                ILayer readLayer = mc.getLayerModel().getLayer(0);
                assertEquals(readLayer.getVectorLegend().length, 1);
                assertEquals(readLayer.getRenderingLegend().length, 0);
                mc.close(null);
        }

        @Test
        public void testExportSVG() throws Exception {
                MapContext mc = new DefaultMapContext();
                mc.open(null);
                ILayer layer = getDataManager().createLayer("bv",
                        new File("src/test/resources/data/bv_sap.shp"));
                mc.getLayerModel().addLayer(layer);
                ILayer layer2 = getDataManager().createLayer("linestring",
                        new File("src/test/resources/data/linestring.shp"));
                mc.getLayerModel().addLayer(layer2);

                MapExportManager mem = Services.getService(MapExportManager.class);
                Envelope envelope = mc.getLayerModel().getEnvelope();
                FileOutputStream outStream = new FileOutputStream(new File(
                        "/tmp/output.svg"));
                mem.exportSVG(mc, outStream, 10, 10, new Envelope(new Coordinate(
                        306260, 2251944), new Coordinate(310000, 2253464)), null, 72);
                outStream.close();
                mc.close(null);

                try {
                        mem.exportSVG(mc, outStream, 10, 10, envelope, null, 72);
                        fail();
                } catch (IllegalArgumentException e) {
                }
        }

        @Test
        public void testRenameLayerSource() throws Exception {
                MapContext mc = new DefaultMapContext();
                mc.open(null);
                SourceManager sm = getDataManager().getSourceManager();
                sm.register("bv", new File("src/test/resources/data/bv_sap.shp"));
                ILayer layer = getDataManager().createLayer("bv");
                mc.getLayerModel().addLayer(layer);
                sm.rename("bv", "bva");
                assertEquals(sm.getAllNames("bva").length, 1);
                assertEquals(sm.getAllNames("bva")[0], "bv");
                mc.close(null);
        }

        @Test
        public void testRenameLayerBack() throws Exception {
                MapContext mc = new DefaultMapContext();
                mc.open(null);
                SourceManager sm = getDataManager().getSourceManager();
                sm.register("bv", new File("src/test/resources/data/bv_sap.shp"));
                ILayer layer = getDataManager().createLayer("bv");
                mc.getLayerModel().addLayer(layer);
                layer.setName("bva");
                assertEquals(sm.getAllNames("bv").length, 1);
                assertEquals(sm.getAllNames("bv")[0], "bva");
                layer.setName("bv");
                assertEquals(sm.getAllNames("bv").length, 0);
                mc.close(null);
        }
}