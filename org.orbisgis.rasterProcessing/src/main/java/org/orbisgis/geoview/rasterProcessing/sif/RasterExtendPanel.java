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
package org.orbisgis.geoview.rasterProcessing.sif;

import java.util.HashMap;
import java.util.Map;

import org.grap.processing.operation.others.RasteringMode;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.layerModel.RasterLayer;
import org.orbisgis.geoview.sif.RasterLayerCombo;
import org.sif.multiInputPanel.ComboBoxChoice;
import org.sif.multiInputPanel.DoubleType;
import org.sif.multiInputPanel.MultiInputPanel;

import com.vividsolutions.jts.geom.Envelope;

public class RasterExtendPanel extends MultiInputPanel {
	public final static Map<String, RasteringMode> rasteringMode = new HashMap<String, RasteringMode>();
	static {
		rasteringMode.put("FILL", RasteringMode.FILL);
		rasteringMode.put("DRAW", RasteringMode.DRAW);
	}
	private GeoView2D geoView2D;
	private Envelope envelope;

	public RasterExtendPanel(GeoView2D geoView2D, Envelope envelope) {
		super("org.orbisgis.geoview.rasterProcessing.Rasterization",
				"Rasterizing Tool");

		this.geoView2D = geoView2D;
		this.envelope = envelope;
		setInfoText("Introduce the connection parameters");
		addInput("source1", "Raster reference", new RasterLayerCombo(geoView2D
				.getViewContext()));

		addInput("mode", "Mode", null, new ComboBoxChoice(rasteringMode
				.keySet().toArray(new String[0])));
		addInput("AddValue", "Value to rastering", "0", new DoubleType());
		addValidationExpression("source1 is not null",
				"A layer must be selected.");
	}

	public String postProcess() {
		final RasterLayer raster1 = (RasterLayer) geoView2D.getViewContext()
				.getLayerModel().getLayerByName(getInput("source1"));
		if (raster1.getEnvelope().intersects(envelope)) {
			return null;
		}
		return "The raster layer doesn't intersect the selected layer.";
	}

	public String validateInput() {
		return null;
	}
}