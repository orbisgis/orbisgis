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
package org.orbisgis.geoprocessing.ui.sif;

import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.components.sif.RasterLayerCombo;
import org.orbisgis.sif.multiInputPanel.DoubleType;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;

import com.vividsolutions.jts.geom.Envelope;

public class RasterExtendPanel extends MultiInputPanel {

	private Envelope envelope;
	private MapContext mapContext;

	public RasterExtendPanel(MapContext mapContext, Envelope envelope) {
		super("org.orbisgis.geoview.rasterProcessing.Rasterization",
				"Rasterizing Tool");

		this.mapContext = mapContext;
		this.envelope = envelope;
		setInfoText("Convert a set of lines or multilines onto a set of pixels");

		try {
			addInput("source1", "Raster reference", new RasterLayerCombo(
					mapContext));
		} catch (DriverException e) {
			Services.getErrorManager().error(
					"Problem while accessing GeoRaster datas", e);
		}

		addInput("AddValue", "Value to rastering", "0", new DoubleType());
		addValidationExpression("source1 is not null",
				"A layer must be selected.");
	}

	public String postProcess() {
		final ILayer raster1 = mapContext.getLayerModel().getLayerByName(
				getInput("source1"));
		if (raster1.getEnvelope().intersects(envelope)) {
			return null;
		}
		return "The raster layer doesn't intersect the selected layer.";
	}

	public String validateInput() {
		return null;
	}
}