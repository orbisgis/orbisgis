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
package org.orbisgis;

import java.io.File;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.layerModel.DefaultMapContext;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.renderer.legend.Legend;
import org.orbisgis.renderer.legend.RenderException;
import org.orbisgis.renderer.symbol.Symbol;

public class MapContextTest extends AbstractTest {

	public void testRemoveSelectedLayer() throws Exception {
		MapContext mc = new DefaultMapContext();
		ILayer layer = getDataManager().createLayer(
				new File("src/test/resources/bv_sap.shp"));
		mc.getLayerModel().addLayer(layer);
		mc.setSelectedLayers(new ILayer[] { layer });
		assertTrue(mc.getSelectedLayers().length == 1);
		assertTrue(mc.getSelectedLayers()[0] == layer);
		mc.getLayerModel().remove(layer);
		assertTrue(mc.getSelectedLayers().length == 0);
	}

	public void testSetBadLayerSelection() throws Exception {
		MapContext mc = new DefaultMapContext();
		ILayer layer = getDataManager().createLayer(
				new File("src/test/resources/bv_sap.shp"));
		ILayer layer2 = getDataManager().createLayer(
				new File("src/test/resources/1.shp"));
		mc.getLayerModel().addLayer(layer);
		mc.setSelectedLayers(new ILayer[] { layer2 });
		assertTrue(mc.getSelectedLayers().length == 0);
		mc.setSelectedLayers(new ILayer[] { layer });
		assertTrue(mc.getSelectedLayers().length == 1);
	}

	public void testRemoveActiveLayer() throws Exception {
		MapContext mc = new DefaultMapContext();
		ILayer layer = getDataManager().createLayer(
				new File("src/test/resources/bv_sap.shp"));
		mc.getLayerModel().addLayer(layer);
		mc.setActiveLayer(layer);
		mc.getLayerModel().remove(layer);
		assertTrue(mc.getActiveLayer() == null);
	}

	public void testSaveAndRecoverMapContext() throws Exception {
		MapContext mc = new DefaultMapContext();
		ILayer layer1 = getDataManager().createLayer(
				new File("src/test/resources/linestring.shp"));
		ILayer layer2 = getDataManager().createLayer(
				new File("src/test/resources/bv_sap.shp"));
		mc.getLayerModel().addLayer(layer1);
		mc.getLayerModel().addLayer(layer2);
		Symbol sym1 = layer1.getVectorLegend()[0].getSymbol(layer1
				.getDataSource(), 0);
		Symbol sym2 = layer2.getVectorLegend()[0].getSymbol(layer2
				.getDataSource(), 0);
		File file = new File("target/mapContextTest.xml");
		mc.saveStatus(file, new NullProgressMonitor());
		mc.loadStatus(file, new NullProgressMonitor());
		assertTrue(mc.getLayers().length == 2);
		Legend legend1 = mc.getLayerModel().getLayer(0).getVectorLegend()[0];
		assertTrue(legend1.getSymbol(layer1.getDataSource(), 0)
				.getPersistentProperties().equals(
						sym1.getPersistentProperties()));
		Legend legend2 = mc.getLayerModel().getLayer(1).getVectorLegend()[0];
		assertTrue(legend2.getSymbol(layer2.getDataSource(), 0)
				.getPersistentProperties().equals(
						sym2.getPersistentProperties()));
	}

	public void testRecover1_1_0() throws Exception {
		getDataManager().getSourceManager().register("bv_sap",
				new File("src/test/resources/bv_sap.shp"));
		getDataManager().getSourceManager().register("linestring",
				new File("src/test/resources/linestring.shp"));
		File file = new File("src/test/resources/mapContext-1_1_0.xml");
		MapContext mc = new DefaultMapContext();
		mc.loadStatus(file, new NullProgressMonitor());
		assertTrue(mc.getLayers().length == 2);
		testNotNullSymbols(mc.getLayerModel().getLayer(0));
		testNotNullSymbols(mc.getLayerModel().getLayer(1));
	}

	private void testNotNullSymbols(ILayer layer) throws DriverException,
			RenderException {
		Legend legend = layer.getVectorLegend()[0];
		SpatialDataSourceDecorator sds = layer.getDataSource();
		for (int i = 0; i < sds.getRowCount(); i++) {
			assertTrue(legend.getSymbol(sds, i) != null);
		}
	}
}
