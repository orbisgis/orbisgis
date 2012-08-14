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
package org.orbisgis.core.geocognition;

import org.junit.Test;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.gdms.sql.function.spatial.geometry.operators.ST_Buffer;
import org.gdms.sql.function.system.RegisterCall;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.errorManager.ErrorListener;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.geocognition.mapContext.GeocognitionMapContextFactory;
import org.orbisgis.core.geocognition.symbology.GeocognitionLegendFactory;
import org.orbisgis.core.geocognition.symbology.GeocognitionSymbolFactory;
import org.orbisgis.core.layerModel.DefaultMapContext;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.renderer.legend.carto.LegendFactory;
import org.orbisgis.core.renderer.legend.carto.UniqueSymbolLegend;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.renderer.symbol.SymbolFactory;
import org.orbisgis.progress.NullProgressMonitor;

import static org.junit.Assert.*;

public class GeoCognitionTest extends AbstractGeocognitionTest {

        @Test
        public void testRootId() throws Exception {
                assertEquals(gc.getRoot().getId(), "");
                saveAndLoad();
                assertEquals(gc.getRoot().getId(), "");
        }

        @Test
        public void testSymbolPersistence() throws Exception {
                Symbol symbol = SymbolFactory.createPolygonSymbol();
                gc.addElement("/org/mysymbol", symbol);
                saveAndLoad();
                Symbol symbol2 = gc.getElement("/org/mysymbol", Symbol.class);
                assertEquals(symbol2.getPersistentProperties(), symbol.getPersistentProperties());
        }

        @Test
        public void testNonSupportedElement() throws Exception {
                try {
                        gc.addElement("org.wont.add", new GeoCognitionTest());
                        fail();
                } catch (IllegalArgumentException e) {
                }
        }

        @Test
        public void testNonUniqueId() throws Exception {
                gc.addElement("org.wont.add", SymbolFactory.createPolygonSymbol());
                try {
                        gc.addElement("org.wont.add", SymbolFactory.createPolygonSymbol());
                        fail();
                } catch (IllegalArgumentException e) {
                }
                try {
                        gc.addFolder("org.wont.add");
                        fail();
                } catch (IllegalArgumentException e) {
                }
                gc.addFolder("myfolder");
                gc.addElement("/myfolder/org.wont.add/ST_Buffer", SymbolFactory.createPolygonSymbol());
                try {
                        gc.addElement("/myfolder/org.wont.add/ST_Buffer", SymbolFactory.createPolygonSymbol());
                        fail();
                } catch (IllegalArgumentException e) {
                }
                try {
                        gc.addFolder("/myfolder/org.wont.add");
                        fail();
                } catch (IllegalArgumentException e) {
                }
        }

        @Test
        public void testAddParentDoesNotExist() throws Exception {
                gc.addElement("/it.will.be.created/ST_Buffer", SymbolFactory.createPolygonSymbol());
                assertNotNull(gc.getGeocognitionElement("it.will.be.created"));
        }

        @Test
        public void testLegendPersistence() throws Exception {
                UniqueSymbolLegend legend = LegendFactory.createUniqueSymbolLegend();
                Symbol symbol = SymbolFactory.createPolygonSymbol(Color.pink);
                legend.setSymbol(symbol);
                UniqueSymbolLegend legend2 = LegendFactory.createUniqueSymbolLegend();
                gc.addElement("org.mylegend", legend);
                gc.addElement("org.mylegend2", legend2);
                saveAndLoad();
                legend = gc.getElement("org.mylegend", UniqueSymbolLegend.class);
                assertTrue(legend.getSymbol().getPersistentProperties().equals(
                        symbol.getPersistentProperties()));
                legend2 = gc.getElement("org.mylegend2", UniqueSymbolLegend.class);
                assertNull(legend2.getSymbol());
        }

        @Test
        public void testMapContextPersistence() throws Exception {
                MapContext mc = new DefaultMapContext();
                mc.open(null);
                DataManager dm = (DataManager) Services.getService(DataManager.class);
                ILayer lyr = dm.createLayer(new File(
                        "src/test/resources/data/bv_sap.shp"));
                mc.getLayerModel().addLayer(lyr);
                mc.close(null);
                gc.addElement("org.mymap", mc);
                saveAndLoad();
                mc = gc.getElement("org.mymap", MapContext.class);
                mc.open(null);
                assertEquals(mc.getLayerModel().getLayerCount(), 1);
                mc.close(null);
        }

        @Test
        public void testMapContextIsModifed() throws Exception {
                MapContext mc = new DefaultMapContext();
                mc.open(null);
                DataManager dm = (DataManager) Services.getService(DataManager.class);
                ILayer lyr = dm.createLayer(new File(
                        "src/test/resources/data/bv_sap.shp"));
                mc.getLayerModel().addLayer(lyr);
                mc.getLayerModel().addLayer(dm.createLayerCollection("group"));
                mc.close(null);
                gc.addElement("org.mymap", mc);
                GeocognitionElement elem = gc.getGeocognitionElement("org.mymap");
                elem.open(null);
                assertFalse(elem.isModified());
                elem.close(null);
        }

        @Test
        public void testNotFoundSymbolReturnsNull() throws Exception {
                assertNull(gc.getElement("org.not.exists", Symbol.class));
        }

        @Test
        public void testListenRemove() throws Exception {
                Symbol s = SymbolFactory.createPolygonSymbol();
                TestListener listener = new TestListener();
                gc.addGeocognitionListener(listener);
                gc.addElement("org.id1", s);
                gc.removeElement("org.id1");
                assertEquals(listener.removed, 1);
                assertEquals(listener.removing, 1);
                gc.addFolder("org.myfolder");
                gc.addElement("/org.myfolder/org.id1", s);
                gc.addElement("/org.myfolder/org.id2", s);
                gc.removeElement("/org.myfolder/org.id1");
                assertEquals(listener.removed, 2);
                assertEquals(listener.removing, 2);
                gc.removeElement("org.myfolder");
                assertEquals(listener.removed, 3);
                assertEquals(listener.removing, 3);
                assertEquals(gc.getRoot().getElementCount(), 0);
        }

        @Test
        public void testListenAddRemoveFromFolder() throws Exception {
                TestListener listener = new TestListener();
                gc.addGeocognitionListener(listener);
                gc.addElement("org.id1", SymbolFactory.createPolygonSymbol());
                assertEquals(listener.added, 1);
                gc.getRoot().removeElement("org.id1");
                assertEquals(listener.removed, 1);
                assertEquals(listener.removing, 1);
                gc.getRoot().addElement(gc.createFolder("ppp"));
                assertEquals(listener.added, 2);
        }

        @Test
        public void testRemovalCancellation() throws Exception {
                TestListener listener = new TestListener();
                gc.addGeocognitionListener(listener);
                gc.addElement("/ST_Buffer", SymbolFactory.createPolygonSymbol());
                listener.cancel = true;
                assertNull(gc.removeElement("/ST_Buffer"));
        }

        @Test
        public void testListenAdd() throws Exception {
                Symbol s = SymbolFactory.createPolygonSymbol();
                TestListener listener = new TestListener();
                gc.addGeocognitionListener(listener);
                gc.addElement("org", s);
                gc.addFolder("org.folder");
                // Two elements are created here
                gc.addElement("/org.another.folder/org.contains", s);
                assertEquals(listener.added, 4);
        }

        @Test
        public void testListenMoveNotRemovePlusAdd() throws Exception {
                Symbol s = SymbolFactory.createPolygonSymbol();
                TestListener listener = new TestListener();
                gc.addGeocognitionListener(listener);
                gc.addElement("org", s);
                gc.addFolder("org.folder");
                int added = listener.added;
                int removed = listener.removed;
                int removing = listener.removing;
                gc.move("/org", "/org.folder");
                assertEquals(listener.moved, 1);
                assertEquals(listener.added, added);
                assertEquals(listener.removed, removed);
                assertEquals(listener.removing, removing);
        }

        @Test
        public void testClear() throws Exception {
                Symbol s = SymbolFactory.createPolygonSymbol();
                gc.addElement("org", s);
                gc.addFolder("org.folder");
                gc.clear();
                assertEquals(gc.getRoot().getElementCount(), 0);
        }

        @Test
        public void testMapContextLoadLayerWithoutSource() throws Exception {
                MapContext mc = new DefaultMapContext();
                mc.open(null);
                ILayer layer = getDataManager().createLayer("linestring",
                        new File("src/test/resources/data/linestring.shp"));
                mc.getLayerModel().addLayer(layer);
                mc.close(null);

                gc.addElement("org.map", mc);
                GeocognitionElement mapElement = gc.getGeocognitionElement("org.map");
                getDataManager().getSourceManager().remove("linestring");

                saveAndLoad();
                mapElement = gc.getGeocognitionElement("org.map");
                CountingErrorManager em = new CountingErrorManager();
                ErrorManager previous = Services.getErrorManager();
                Services.setService(ErrorManager.class, em);
                mapElement.open(null);
                Services.setService(ErrorManager.class, previous);
                assertEquals(((MapContext) mapElement.getObject()).getLayerModel().getLayerCount(), 0);
                assertTrue(mapElement.isModified());
                mapElement.close(null);
        }

        @Test
        public void testImportExport() throws Exception {
                Symbol s = SymbolFactory.createPolygonSymbol();
                gc.addFolder("org");
                gc.addElement("/org/mysymbol", s);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                gc.write(bos, "/org");
                GeocognitionElement tree = gc.createTree(new ByteArrayInputStream(bos.toByteArray()));
                gc.addGeocognitionElement("/newFolder", tree);
                GeocognitionElement org = gc.getGeocognitionElement("org");
                GeocognitionElement newFolder = gc.getGeocognitionElement("newFolder");
                assertEquals(org.getElementCount(), newFolder.getElementCount());
        }

        @Test
        public void testFullPath() throws Exception {
                gc.addFolder("org");
                GeocognitionElement org = gc.getGeocognitionElement("org");
                gc.addFolder(org.getIdPath() + "/orbisgis");
                GeocognitionElement orbisgis = gc.getGeocognitionElement("/org/orbisgis");
                gc.addFolder(orbisgis.getIdPath() + "/test");
                GeocognitionElement test = gc.getGeocognitionElement("/org/orbisgis/test");
                assertEquals(org.getElementCount(), 1);
                assertEquals(orbisgis.getElementCount(), 1);
                assertEquals(test.getElementCount(), 0);
                assertEquals(org.getIdPath(), "/org");
                assertEquals(orbisgis.getIdPath(), "/org/orbisgis");
                assertEquals(test.getIdPath(), "/org/orbisgis/test");
        }

        @Test
        public void testMoveError() throws Exception {
                Symbol s = SymbolFactory.createPolygonSymbol();
                gc.addFolder("org");
                gc.addElement("sym", s);
                try {
                        gc.move("/org", "/sym");
                        fail();
                } catch (IllegalArgumentException e) {
                } catch (UnsupportedOperationException e) {
                }
                assertEquals(gc.getRoot().getElementCount(), 2);
        }

        @Test
        public void testMoveSameName() throws Exception {
                gc.addFolder("org");
                gc.addFolder("/org/foo");
                gc.addFolder("foo");
                try {
                        gc.move("/foo", "/org");
                        fail();
                } catch (UnsupportedOperationException e) {
                }
        }

        @Test
        public void testModifyMapContextXML() throws Exception {
                MapContext mc = new DefaultMapContext();
                mc.open(null);
                DataManager dm = (DataManager) Services.getService(DataManager.class);
                ILayer lyr = dm.createLayer(new File(
                        "src/test/resources/data/bv_sap.shp"));
                mc.getLayerModel().addLayer(lyr);
                mc.close(null);
                gc.addElement("mymap", mc);

                GeocognitionElement element = gc.getGeocognitionElement("mymap");
                String xml = element.getXMLContent();
                MapContext mc2 = new DefaultMapContext();
                gc.addElement("mymap2", mc2);
                GeocognitionElement element2 = gc.getGeocognitionElement("mymap2");
                element2.setXMLContent(xml);

                mc2.open(null);
                assertEquals(mc2.getLayerModel().getLayerCount(), 1);
        }

        @Test
        public void testChangeMapIdConflict() throws Exception {
                gc.addElement("A", new DefaultMapContext());
                gc.addElement("B", new DefaultMapContext());
                try {
                        gc.getGeocognitionElement("A").setId("B");
                        fail();
                } catch (IllegalArgumentException e) {
                }
        }

        @Test
        public void testKeepUnsupportedElement() throws Exception {
                MapContext mc = new DefaultMapContext();
                mc.open(null);
                DataManager dm = (DataManager) Services.getService(DataManager.class);
                ILayer lyr = dm.createLayer(new File(
                        "src/test/resources/data/bv_sap.shp"));
                mc.getLayerModel().addLayer(lyr);
                mc.close(null);
                gc.addElement("org.mymap", mc);
                File temp = new File("target/temp.xml");
                gc.write(new FileOutputStream(temp));

                // Create a geocognition without support for maps
                gc = new DefaultGeocognition();
                DefaultGeocognition.clearFactories();

                failErrorManager.setIgnoreWarnings(true);
                gc.read(new FileInputStream(temp));
                failErrorManager.setIgnoreWarnings(false);
                GeocognitionElement elem = gc.getGeocognitionElement("org.mymap");
                assertNotNull(elem);
                assertFalse((elem.getObject() instanceof MapContext));
                gc.write(new FileOutputStream(temp));

                gc = new DefaultGeocognition();
                gc.addElementFactory(new GeocognitionSymbolFactory());
                gc.addElementFactory(new GeocognitionLegendFactory());
                gc.addElementFactory(new GeocognitionMapContextFactory());
                gc.read(new FileInputStream(temp));
                elem = gc.getGeocognitionElement("org.mymap");
                assertNotNull(elem);
                assertTrue(new GeocognitionMapContextFactory().acceptContentTypeId(elem.getTypeId()));
                MapContext map = gc.getElement("org.mymap", MapContext.class);
                map.open(null);
                assertTrue(map.getLayerModel().getLayerCount() > 0);
                map.close(null);
        }

        private class TestListener implements GeocognitionListener {

                public int moved = 0;
                private int added = 0;
                private int removed = 0;
                private int removing = 0;
                private boolean cancel = false;

                @Override
                public void elementRemoved(Geocognition geocognition,
                        GeocognitionElement element) {
                        removed++;
                }

                @Override
                public void elementAdded(Geocognition geocognition,
                        GeocognitionElement parent, GeocognitionElement newElement) {
                        added++;
                }

                @Override
                public boolean elementRemoving(Geocognition geocognition,
                        GeocognitionElement element) {
                        removing++;
                        return !cancel;
                }

                @Override
                public void elementMoved(Geocognition geocognition,
                        GeocognitionElement element, GeocognitionElement oldParent) {
                        moved++;
                }
        }

        private final class CountingErrorManager implements ErrorManager {

                private int warnings = 0;
                private int errors = 0;

                @Override
                public void warning(String userMsg, Throwable exception) {
                        warnings++;
                }

                @Override
                public void warning(String userMsg) {
                        warnings++;
                }

                @Override
                public void removeErrorListener(ErrorListener listener) {
                }

                @Override
                public void error(String userMsg, Throwable exception) {
                        errors++;
                }

                @Override
                public void error(String userMsg) {
                        errors++;
                }

                @Override
                public void addErrorListener(ErrorListener listener) {
                }
        }
}
