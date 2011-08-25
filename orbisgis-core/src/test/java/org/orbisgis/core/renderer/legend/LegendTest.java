/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.core.renderer.legend;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.SQLDataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.driver.memory.MemoryDataSetDriver;
import org.orbisgis.core.AbstractTest;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.DefaultMapContext;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.renderer.Renderer;
import org.orbisgis.core.renderer.legend.carto.DefaultUniqueSymbolLegend;
import org.orbisgis.core.renderer.legend.carto.IntervalLegend;
import org.orbisgis.core.renderer.legend.carto.LabelLegend;
import org.orbisgis.core.renderer.legend.carto.LegendFactory;
import org.orbisgis.core.renderer.legend.carto.LegendManager;
import org.orbisgis.core.renderer.legend.carto.ProportionalLegend;
import org.orbisgis.core.renderer.legend.carto.UniqueSymbolLegend;
import org.orbisgis.core.renderer.legend.carto.UniqueValueLegend;
import org.orbisgis.core.renderer.symbol.StandardPointSymbol;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.renderer.symbol.SymbolFactory;

import static org.junit.Assert.*;

public class LegendTest extends AbstractTest {

        private SQLDataSourceFactory dsf;
        private DataSource ds;
        private String fieldName;
        private LegendManager lm;

        @Test
        public void testRendererWithNullSymbols() throws Exception {
                ILayer layer = getDataManager().createLayer(
                        new File("src/test/resources/data/bv_sap.shp"));
                layer.open();
                UniqueValueLegend legend = LegendFactory.createUniqueValueLegend();
                legend.setClassificationField(layer.getDataSource().getFieldName(1),
                        layer.getDataSource());
                legend.setDefaultSymbol(null);
                layer.setLegend(legend);

                Renderer r = new Renderer();
                BufferedImage img = new BufferedImage(100, 100,
                        BufferedImage.TYPE_INT_ARGB);
                r.draw(img, layer.getEnvelope(), layer);
                layer.close();
        }

        @Test
        public void testSetLegend() throws Exception {
                MapContext mc = new DefaultMapContext();
                mc.open(null);
                ILayer layer1 = getDataManager().createLayer("myVectorLegend",
                        new File("src/test/resources/data/bv_sap.shp"));
                mc.getLayerModel().addLayer(layer1);
                ILayer layer2 = getDataManager().createLayer("myRasterLegend",
                        new File("src/test/resources/data/ace.tiff"));
                mc.getLayerModel().addLayer(layer2);
                Renderer r = new Renderer();
                BufferedImage img = new BufferedImage(100, 100,
                        BufferedImage.TYPE_INT_ARGB);
                r.draw(img, mc.getLayerModel().getEnvelope(), mc.getLayerModel());

                try {
                        layer1.getRasterLegend();
                        fail();
                } catch (UnsupportedOperationException e) {
                }
                try {
                        layer1.getRasterLegend("the_geom");
                        fail();
                } catch (IllegalArgumentException e) {
                }
                try {
                        layer2.getRasterLegend("rasterr");
                        fail();
                } catch (IllegalArgumentException e) {
                }
                try {
                        layer2.getVectorLegend("raster");
                        fail();
                } catch (IllegalArgumentException e) {
                }
                try {
                        layer1.getVectorLegend("thegeom");
                        fail();
                } catch (IllegalArgumentException e) {
                }
                mc.close(null);
        }

        @Test
        public void testUniqueSymbolPersistence() throws Exception {
                UniqueSymbolLegend usl = LegendFactory.createUniqueSymbolLegend();
                String name = "mylegend";
                usl.setName(name);
                Symbol sym = SymbolFactory.createPointCircleSymbol(Color.black,
                        Color.red, 20);
                usl.setSymbol(sym);
                Object object = usl.getJAXBObject();

                usl = (UniqueSymbolLegend) lm.getNewLegend(usl.getLegendTypeId());
                usl.setJAXBObject(object);
                assertEquals(usl.getName(), name);
                assertEquals(usl.getSymbol().getPersistentProperties(), sym.getPersistentProperties());
        }

        @Test
        public void testFullUniqueValuePersistence() throws Exception {
                UniqueValueLegend uvl = LegendFactory.createUniqueValueLegend();
                String name = "mylegend";
                uvl.setName(name);
                Symbol classSym = SymbolFactory.createPointCircleSymbol(Color.black,
                        Color.red, 20);
                Symbol sym = classSym;
                uvl.setDefaultSymbol(sym);
                String aLabel = "aLabel";
                uvl.setDefaultLabel(aLabel);
                uvl.setClassificationField(fieldName, ds);
                Value classValue = ValueFactory.createValue(2);
                uvl.addClassification(classValue, classSym, aLabel);
                Object object = uvl.getJAXBObject();

                uvl = (UniqueValueLegend) lm.getNewLegend(uvl.getLegendTypeId());
                uvl.setJAXBObject(object);
                assertEquals(uvl.getName(), name);
                assertEquals(uvl.getDefaultSymbol().getPersistentProperties(), sym.getPersistentProperties());
                assertEquals(uvl.getClassificationField(), fieldName);
                assertEquals(uvl.getClassificationCount(), 1);
                assertTrue(uvl.getValue(0).equals(classValue).getAsBoolean());
                assertEquals(uvl.getSymbol(0).getPersistentProperties(), classSym.getPersistentProperties());
                assertEquals(uvl.getLabel(0), aLabel);
        }

        @Test
        public void testMinimumUniqueValuePersistence() throws Exception {
                UniqueValueLegend uvl = LegendFactory.createUniqueValueLegend();
                uvl.setName(null);
                uvl.setDefaultSymbol(null);
                uvl.setDefaultLabel(null);
                uvl.setClassificationField(fieldName, ds);
                Object object = uvl.getJAXBObject();

                uvl = (UniqueValueLegend) lm.getNewLegend(uvl.getLegendTypeId());
                uvl.setJAXBObject(object);
                assertNull(uvl.getName());
                assertNull(uvl.getDefaultSymbol());
                assertEquals(uvl.getClassificationField(), fieldName);
                assertEquals(uvl.getClassificationCount(), 0);
        }

        @Test
        public void testClearUniqueValue() throws Exception {
                UniqueValueLegend uvl = LegendFactory.createUniqueValueLegend();
                Symbol symbol = SymbolFactory.createPointCircleSymbol(Color.black,
                        Color.red, 20);
                uvl.addClassification(ValueFactory.createValue(3), symbol, "");
                uvl.clear();
                uvl.addClassification(ValueFactory.createValue(6), symbol, "");
                assertEquals(uvl.getValue(0).getAsInt(), 6);
        }

        @Test
        public void testClearInterval() throws Exception {
                IntervalLegend uvl = LegendFactory.createIntervalLegend();
                Symbol symbol = SymbolFactory.createPointCircleSymbol(Color.black,
                        Color.red, 20);
                uvl.addIntervalWithMaxLimit(ValueFactory.createValue(3), false, symbol,
                        "");
                uvl.clear();
                uvl.addIntervalWithMaxLimit(ValueFactory.createValue(6), false, symbol,
                        "");
                assertEquals(uvl.getInterval(0).getMaxValue().getAsInt(), 6);
        }

        @Test
        public void testFullIntervalPersistence() throws Exception {
                IntervalLegend uvl = LegendFactory.createIntervalLegend();
                String name = "mylegend";
                uvl.setName(name);
                Symbol classSym = SymbolFactory.createPointCircleSymbol(Color.black,
                        Color.red, 20);
                Symbol sym = classSym;
                uvl.setDefaultSymbol(sym);
                String aLabel = "aLabel";
                uvl.setDefaultLabel(aLabel);
                uvl.setClassificationField(fieldName, ds);
                Value initValue = ValueFactory.createValue(0);
                Value endValue = ValueFactory.createValue(1000d);
                uvl.addInterval(initValue, false, endValue, true, classSym, aLabel);
                Object object = uvl.getJAXBObject();

                uvl = (IntervalLegend) lm.getNewLegend(uvl.getLegendTypeId());
                uvl.setJAXBObject(object);
                assertEquals(uvl.getName(), name);
                assertEquals(uvl.getDefaultSymbol().getPersistentProperties(), sym.getPersistentProperties());
                assertEquals(uvl.getClassificationField(), fieldName);
                assertTrue(uvl.getClassificationCount() == 1);
                assertTrue(uvl.getInterval(0).getMinValue().equals(initValue).getAsBoolean());
                assertTrue(uvl.getInterval(0).getMaxValue().equals(endValue).getAsBoolean());
                assertTrue(!uvl.getInterval(0).isMinIncluded());
                assertTrue(uvl.getInterval(0).isMaxIncluded());
                assertEquals(uvl.getSymbol(0).getPersistentProperties(), classSym.getPersistentProperties());
                assertEquals(uvl.getLabel(0), aLabel);
        }

        @Test
        public void testMinimumIntervalPersistence() throws Exception {
                IntervalLegend uvl = LegendFactory.createIntervalLegend();
                uvl.setName(null);
                uvl.setDefaultSymbol(null);
                uvl.setDefaultLabel(null);
                uvl.setClassificationField(fieldName, ds);
                Object object = uvl.getJAXBObject();

                uvl = (IntervalLegend) lm.getNewLegend(uvl.getLegendTypeId());
                uvl.setJAXBObject(object);
                assertNull(uvl.getName());
                assertNull(uvl.getDefaultSymbol());
                assertEquals(uvl.getClassificationField(), fieldName);
                assertEquals(uvl.getClassificationCount(), 0);
        }

        @Test
        public void testNullIntervalPersistence() throws Exception {
                IntervalLegend uvl = LegendFactory.createIntervalLegend();
                uvl.setName(null);
                uvl.setDefaultSymbol(null);
                uvl.setDefaultLabel(null);
                uvl.setClassificationField(fieldName, ds);
                uvl.setClassificationField(fieldName, ds);
                Value endValue = ValueFactory.createValue(1000d);
                Symbol classSym = SymbolFactory.createPolygonSymbol();
                uvl.addInterval(ValueFactory.createNullValue(), false, endValue, true,
                        classSym, "");
                Object object = uvl.getJAXBObject();

                uvl = (IntervalLegend) lm.getNewLegend(uvl.getLegendTypeId());
                uvl.setJAXBObject(object);
                assertNull(uvl.getName());
                assertNull(uvl.getDefaultSymbol());
                assertEquals(uvl.getClassificationField(), fieldName);
                assertEquals(uvl.getClassificationCount(), 1);
        }

        @Test
        public void testFullProportionalPersistence() throws Exception {
                ProportionalLegend legend = LegendFactory.createProportionalPointLegend();
                String name = "mylegend";
                legend.setName(name);
                Symbol sampleSym = SymbolFactory.createPointCircleSymbol(Color.black,
                        Color.red, 20);
                legend.setClassificationField(fieldName);
                legend.setMethod(ProportionalLegend.LOGARITHMIC);
                legend.setMaxSize(14);
                legend.setSampleSymbol((StandardPointSymbol) sampleSym);
                Object object = legend.getJAXBObject();

                legend = (ProportionalLegend) lm.getNewLegend(legend.getLegendTypeId());
                legend.setJAXBObject(object);
                assertEquals(legend.getName(), name);
                assertEquals(legend.getSampleSymbol().getPersistentProperties(), sampleSym.getPersistentProperties());
                assertEquals(legend.getClassificationField(), fieldName);
                assertEquals(legend.getMaxSize(), 14);
                assertEquals(legend.getMethod(), ProportionalLegend.LOGARITHMIC);
        }

        @Test
        public void testFullLabelPersistence() throws Exception {
                LabelLegend legend = LegendFactory.createLabelLegend();
                String name = "mylegend";
                legend.setName(name);
                legend.setClassificationField(fieldName);
                legend.setFontSize(10);
                legend.setLabelSizeField(fieldName);
                legend.setSmartPlacing(true);
                Object object = legend.getJAXBObject();

                legend = (LabelLegend) lm.getNewLegend(legend.getLegendTypeId());
                legend.setJAXBObject(object);
                assertEquals(legend.getName(), name);
                assertEquals(legend.getClassificationField(), fieldName);
                assertEquals(legend.getLabelSizeField(), fieldName);
                assertEquals(legend.getFontSize(), 10);
                assertTrue(legend.isSmartPlacing());
        }

        @Test
        public void testMinLabelPersistence() throws Exception {
                LabelLegend legend = LegendFactory.createLabelLegend();
                String name = "mylegend";
                legend.setName(name);
                legend.setClassificationField(fieldName);
                legend.setLabelSizeField(fieldName);
                Object object = legend.getJAXBObject();

                legend = (LabelLegend) lm.getNewLegend(legend.getLegendTypeId());
                legend.setJAXBObject(object);
                assertEquals(legend.getName(), name);
                assertEquals(legend.getClassificationField(), fieldName);
                assertEquals(legend.getLabelSizeField(), fieldName);
        }

        @Test
        public void testSaveScale() throws Exception {
                UniqueSymbolLegend usl = LegendFactory.createUniqueSymbolLegend();
                String name = "mylegend";
                usl.setName(name);
                Symbol sym = SymbolFactory.createPointCircleSymbol(Color.black,
                        Color.red, 20);
                usl.setSymbol(sym);
                usl.setMinScale(4000);
                Object object = usl.getJAXBObject();

                usl = (UniqueSymbolLegend) lm.getNewLegend(usl.getLegendTypeId());
                usl.setJAXBObject(object);
                assertEquals(usl.getMinScale(), 4000);
                assertEquals(usl.getMaxScale(), Integer.MAX_VALUE);
        }

        @Test
        public void testGetImageWithNullLabels() throws Exception {
                UniqueValueLegend uvl = LegendFactory.createUniqueValueLegend();
                uvl.setName(null);
                uvl.setDefaultSymbol(SymbolFactory.createPolygonSymbol());
                uvl.setDefaultLabel(null);
                uvl.setClassificationField(fieldName, ds);

                Graphics2D graphics = new BufferedImage(10, 10,
                        BufferedImage.TYPE_BYTE_GRAY).createGraphics();
                uvl.getImageSize(graphics);
                uvl.drawImage(graphics);
        }

        @Test
        public void testLoadNonExistingLegend() throws Exception {
                Legend legend = lm.getNewLegend("not.exists");
                assertNull(legend);
        }

        @Test
        public void testAlreadyExists() throws Exception {
                try {
                        lm.addLegend(new DefaultUniqueSymbolLegend());
                        fail();
                } catch (IllegalArgumentException e) {
                }
        }

        @Override
        @Before
        public void setUp() throws Exception {
                super.setUp();
                dsf = new SQLDataSourceFactory("target", "target");
                ds = dsf.getDataSource(new MemoryDataSetDriver(new String[]{"long"},
                        new Type[]{TypeFactory.createType(Type.INT)}), DriverManager.DEFAULT_SINGLE_TABLE_NAME);
                ds.open();
                fieldName = "long";

                lm = Services.getService(LegendManager.class);

                registerDataManager(dsf);
        }

        @After
        public void tearDown() throws Exception {
                ds.close();
        }
}
