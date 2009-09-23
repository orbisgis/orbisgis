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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.orbisgis.core.Services;
import org.orbisgis.core.AbstractTest;
import org.orbisgis.core.renderer.Renderer;
import org.orbisgis.core.renderer.legend.Legend;
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
import org.orbisgis.core.layerModel.DefaultMapContext;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;

public class LegendTest extends AbstractTest {

	private DataSourceFactory dsf;
	private DataSource ds;
	private String fieldName;
	private LegendManager lm;

	public void testRendererWithNullSymbols() throws Exception {
		ILayer layer = getDataManager().createLayer(
				new File("src/test/resources/bv_sap.shp"));
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

	public void testSetLegend() throws Exception {
		MapContext mc = new DefaultMapContext();
		mc.open(null);
		ILayer layer1 = getDataManager().createLayer(
				new File("src/test/resources/bv_sap.shp"));
		mc.getLayerModel().addLayer(layer1);
		ILayer layer2 = getDataManager().createLayer(
				new File("src/test/resources/ace.tiff"));
		mc.getLayerModel().addLayer(layer2);
		Renderer r = new Renderer();
		BufferedImage img = new BufferedImage(100, 100,
				BufferedImage.TYPE_INT_ARGB);
		r.draw(img, mc.getLayerModel().getEnvelope(), mc.getLayerModel());


		try {
			layer1.getRasterLegend();
			assertTrue(false);
		} catch (UnsupportedOperationException e) {
		}
		try {
			layer1.getRasterLegend("the_geom");
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}
		try {
			layer2.getRasterLegend("rasterr");
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}

		try {
			layer2.getVectorLegend();
			assertTrue(false);
		} catch (UnsupportedOperationException e) {
		}
		try {
			layer2.getVectorLegend("raster");
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}
		try {
			layer1.getVectorLegend("thegeom");
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}
		mc.close(null);
	}

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
		assertTrue(usl.getName().equals(name));
		assertTrue(usl.getSymbol().getPersistentProperties().equals(
				sym.getPersistentProperties()));
	}

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
		assertTrue(uvl.getName().equals(name));
		assertTrue(uvl.getDefaultSymbol().getPersistentProperties().equals(
				sym.getPersistentProperties()));
		assertTrue(uvl.getClassificationField().equals(fieldName));
		assertTrue(uvl.getClassificationCount() == 1);
		assertTrue(uvl.getValue(0).equals(classValue).getAsBoolean());
		assertTrue(uvl.getSymbol(0).getPersistentProperties().equals(
				classSym.getPersistentProperties()));
		assertTrue(uvl.getLabel(0).equals(aLabel));
	}

	public void testMinimumUniqueValuePersistence() throws Exception {
		UniqueValueLegend uvl = LegendFactory.createUniqueValueLegend();
		uvl.setName(null);
		uvl.setDefaultSymbol(null);
		uvl.setDefaultLabel(null);
		uvl.setClassificationField(fieldName, ds);
		Object object = uvl.getJAXBObject();

		uvl = (UniqueValueLegend) lm.getNewLegend(uvl.getLegendTypeId());
		uvl.setJAXBObject(object);
		assertTrue(uvl.getName() == null);
		assertTrue(uvl.getDefaultSymbol() == null);
		assertTrue(uvl.getClassificationField().equals(fieldName));
		assertTrue(uvl.getClassificationCount() == 0);
	}

	public void testClearUniqueValue() throws Exception {
		UniqueValueLegend uvl = LegendFactory.createUniqueValueLegend();
		Symbol symbol = SymbolFactory.createPointCircleSymbol(Color.black,
				Color.red, 20);
		uvl.addClassification(ValueFactory.createValue(3), symbol, "");
		uvl.clear();
		uvl.addClassification(ValueFactory.createValue(6), symbol, "");
		assertTrue(uvl.getValue(0).getAsInt() == 6);
	}

	public void testClearInterval() throws Exception {
		IntervalLegend uvl = LegendFactory.createIntervalLegend();
		Symbol symbol = SymbolFactory.createPointCircleSymbol(Color.black,
				Color.red, 20);
		uvl.addIntervalWithMaxLimit(ValueFactory.createValue(3), false, symbol,
				"");
		uvl.clear();
		uvl.addIntervalWithMaxLimit(ValueFactory.createValue(6), false, symbol,
				"");
		assertTrue(uvl.getInterval(0).getMaxValue().getAsInt() == 6);
	}

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
		assertTrue(uvl.getName().equals(name));
		assertTrue(uvl.getDefaultSymbol().getPersistentProperties().equals(
				sym.getPersistentProperties()));
		assertTrue(uvl.getClassificationField().equals(fieldName));
		assertTrue(uvl.getClassificationCount() == 1);
		assertTrue(uvl.getInterval(0).getMinValue().equals(initValue)
				.getAsBoolean());
		assertTrue(uvl.getInterval(0).getMaxValue().equals(endValue)
				.getAsBoolean());
		assertTrue(!uvl.getInterval(0).isMinIncluded());
		assertTrue(uvl.getInterval(0).isMaxIncluded());
		assertTrue(uvl.getSymbol(0).getPersistentProperties().equals(
				classSym.getPersistentProperties()));
		assertTrue(uvl.getLabel(0).equals(aLabel));
	}

	public void testMinimumIntervalPersistence() throws Exception {
		IntervalLegend uvl = LegendFactory.createIntervalLegend();
		uvl.setName(null);
		uvl.setDefaultSymbol(null);
		uvl.setDefaultLabel(null);
		uvl.setClassificationField(fieldName, ds);
		Object object = uvl.getJAXBObject();

		uvl = (IntervalLegend) lm.getNewLegend(uvl.getLegendTypeId());
		uvl.setJAXBObject(object);
		assertTrue(uvl.getName() == null);
		assertTrue(uvl.getDefaultSymbol() == null);
		assertTrue(uvl.getClassificationField().equals(fieldName));
		assertTrue(uvl.getClassificationCount() == 0);
	}

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
		assertTrue(uvl.getName() == null);
		assertTrue(uvl.getDefaultSymbol() == null);
		assertTrue(uvl.getClassificationField().equals(fieldName));
		assertTrue(uvl.getClassificationCount() == 1);
	}

	public void testFullProportionalPersistence() throws Exception {
		ProportionalLegend legend = LegendFactory.createProportionalLegend();
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
		assertTrue(legend.getName().equals(name));
		assertTrue(legend.getSampleSymbol().getPersistentProperties().equals(
				sampleSym.getPersistentProperties()));
		assertTrue(legend.getClassificationField().equals(fieldName));
		assertTrue(legend.getMaxSize() == 14);
		assertTrue(legend.getMethod() == ProportionalLegend.LOGARITHMIC);
	}

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
		assertTrue(legend.getName().equals(name));
		assertTrue(legend.getClassificationField().equals(fieldName));
		assertTrue(legend.getLabelSizeField().equals(fieldName));
		assertTrue(legend.getFontSize() == 10);
		assertTrue(legend.isSmartPlacing());
	}

	public void testMinLabelPersistence() throws Exception {
		LabelLegend legend = LegendFactory.createLabelLegend();
		String name = "mylegend";
		legend.setName(name);
		legend.setClassificationField(fieldName);
		legend.setLabelSizeField(fieldName);
		Object object = legend.getJAXBObject();

		legend = (LabelLegend) lm.getNewLegend(legend.getLegendTypeId());
		legend.setJAXBObject(object);
		assertTrue(legend.getName().equals(name));
		assertTrue(legend.getClassificationField().equals(fieldName));
		assertTrue(legend.getLabelSizeField().equals(fieldName));
	}

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
		assertTrue(usl.getMinScale() == 4000);
		assertTrue(usl.getMaxScale() == Integer.MAX_VALUE);
	}

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

	public void testLoadNonExistingLegend() throws Exception {
		Legend legend = lm.getNewLegend("not.exists");
		assertTrue(legend == null);
	}

	public void testAlreadyExists() throws Exception {
		try {
			lm.addLegend(new DefaultUniqueSymbolLegend());
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dsf = new DataSourceFactory("target", "target");
		ds = dsf.getDataSource(new ObjectMemoryDriver(new String[] { "long" },
				new Type[] { TypeFactory.createType(Type.INT) }));
		ds.open();
		fieldName = "long";

		lm = (LegendManager) Services.getService(LegendManager.class);
	}

	@Override
	protected void tearDown() throws Exception {
		ds.close();
	}
}
