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
package org.orbisgis.renderer;

import java.awt.image.BufferedImage;
import java.io.File;

import org.orbisgis.AbstractTest;
import org.orbisgis.layerModel.DefaultMapContext;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.renderer.legend.Legend;
import org.orbisgis.renderer.legend.carto.LegendFactory;
import org.orbisgis.renderer.legend.carto.UniqueSymbolLegend;
import org.orbisgis.renderer.symbol.SymbolFactory;

public class LegendTest extends AbstractTest {

	public void testSetLegend() throws Exception {
		MapContext mc = new DefaultMapContext();
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
	}

	public void testNoNullSymbol() throws Exception {
		MapContext mc = new DefaultMapContext();
		ILayer layer = getDataManager().createLayer(
				new File("src/test/resources/linestring.shp"));
		mc.getLayerModel().addLayer(layer);
		UniqueSymbolLegend usl = LegendFactory.createUniqueSymbolLegend();
		usl.setSymbol(SymbolFactory.createPolygonSymbol());
		layer.setLegend(usl);
		Legend legend = layer.getRenderingLegend()[0];
		for (int i = 0; i < layer.getDataSource().getRowCount(); i++) {
			assertTrue(legend.getSymbol(layer.getDataSource(), i) != null);
		}

	}
}
