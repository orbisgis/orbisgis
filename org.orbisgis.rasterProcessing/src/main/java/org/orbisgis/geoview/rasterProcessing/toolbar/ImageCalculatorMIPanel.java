/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.geoview.rasterProcessing.toolbar;

import org.gdms.driver.DriverException;
import org.grap.processing.operation.GeoRasterCalculator;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.sif.RasterLayerCombo;
import org.orbisgis.pluginManager.PluginManager;
import org.sif.multiInputPanel.ComboBoxChoice;
import org.sif.multiInputPanel.MultiInputPanel;

public class ImageCalculatorMIPanel extends MultiInputPanel {
	public static final String DIALOG_ID = "org.orbisgis.geoview.rasterProcessing.ImageCalculator";

	private GeoView2D geoView2D;

	public ImageCalculatorMIPanel(GeoView2D geoView2D) {
		super(DIALOG_ID, "Raster Calculator");
		this.geoView2D = geoView2D;
		try {
			addInput("source1", "Raster layer1", new RasterLayerCombo(geoView2D
					.getViewContext()));
		} catch (DriverException e) {
			PluginManager.error("Problem while accessing GeoRaster datas", e);
		}
		addInput("method", "Method", new ComboBoxChoice(
				GeoRasterCalculator.operators.keySet().toArray(new String[0])));
		try {
			addInput("source2", "Raster layer2", new RasterLayerCombo(geoView2D
					.getViewContext()));
		} catch (DriverException e) {
			PluginManager.error("Problem while accessing GeoRaster datas", e);
		}
		addValidationExpression("source1 is not null",
				"A layer must be selected.");
		addValidationExpression("source2 is not null",
				"A layer must be selected.");
	}

	public String postProcess() {
		final ILayer raster1 = geoView2D.getViewContext().getLayerModel()
				.getLayerByName(getInput("source1"));
		final ILayer raster2 = geoView2D.getViewContext().getLayerModel()
				.getLayerByName(getInput("source2"));

		try {
			if (raster1.getEnvelope().equals(raster2.getEnvelope())
					&& raster1.getRaster().getMetadata().getPixelSize_X() == raster2
							.getRaster().getMetadata().getPixelSize_X()) {
				return null;
			}
		} catch (DriverException e) {
			PluginManager.error("Unable to access the raster metadata", e);
		}
		return "The two raster must have the same extent and same pixel size.";
	}

	public String validateInput() {
		return null;
	}
}