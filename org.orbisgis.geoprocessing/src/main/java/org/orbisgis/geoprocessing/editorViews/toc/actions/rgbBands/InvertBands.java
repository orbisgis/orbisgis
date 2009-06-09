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
package org.orbisgis.geoprocessing.editorViews.toc.actions.rgbBands;

import ij.ImagePlus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.legend.RasterLegend;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.multiInputPanel.ComboBoxChoice;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;

/**
 *
 * @author bocher
 *
 */
public class InvertBands implements
		org.orbisgis.core.ui.editorViews.toc.action.ILayerAction {
	private static final String BLUE = "Blue";
	private static final String GREEN = "Green";
	private static final String RED = "Red";
	public final static Map<String, String> bands = new HashMap<String, String>();
	static {
		bands.put(RED, "r");
		bands.put(GREEN, "g");
		bands.put(BLUE, "b");
	}

	private MultiInputPanel mip;

	public boolean accepts(MapContext mc, ILayer layer) {
		try {
			if (layer.isRaster()) {
				SpatialDataSourceDecorator ds = layer.getDataSource();
				if (ds.getRaster(0).getType() == ImagePlus.COLOR_RGB) {
					return true;
				}
			}
		} catch (IOException e) {
		} catch (DriverException e) {
		}
		return false;
	}

	public boolean acceptsSelectionCount(int selectionCount) {

		return 1 == selectionCount;
	}

	public void execute(MapContext mapContext, ILayer layer) {

		RasterLegend legend;
		try {
			legend = (RasterLegend) layer.getRasterLegend()[0];
			mip = new MultiInputPanel("Invert bands order");
			mip.addInput("red", RED, getComponent(legend.getBands(), 0),
					new ComboBoxChoice(bands.keySet().toArray(new String[0])));
			mip.addInput("green", GREEN, getComponent(legend.getBands(), 1),
					new ComboBoxChoice(bands.keySet().toArray(new String[0])));
			mip.addInput("blue", BLUE, getComponent(legend.getBands(), 2),
					new ComboBoxChoice(bands.keySet().toArray(new String[0])));

			if (UIFactory.showDialog(mip)) {
				String codeBands = bands.get(mip.getInput("red"))
						+ bands.get(mip.getInput("green"))
						+ bands.get(mip.getInput("blue"));
				legend.setBands(codeBands);
			}

		} catch (DriverException e) {
			Services.getErrorManager().error("Cannot get the legend", e);
		}

	}

	private String getComponent(String bands, int i) {
		if (bands == null) {
			bands = "rgb";
		}

		if (bands.charAt(i) == 'r') {
			return RED;
		} else if (bands.charAt(i) == 'g') {
			return GREEN;
		} else if (bands.charAt(i) == 'b') {
			return BLUE;
		} else {
			throw new RuntimeException("bug!");
		}
	}

}