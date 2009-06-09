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
package org.orbisgis.geoprocessing.editorViews.toc.actions.defaultStyle;

import java.awt.Component;
import java.awt.image.ColorModel;
import java.net.URL;

import org.orbisgis.core.renderer.legend.RasterLegend;
import org.orbisgis.sif.AbstractUIPanel;
import org.orbisgis.sif.UIPanel;

public class RasterDefaultStyleUIPanel extends AbstractUIPanel implements
		UIPanel {
	private RasterDefaultStylePanel rasterDefaultStylePanel;

	public RasterDefaultStyleUIPanel(final RasterLegend legend,
			ColorModel defaultColorModel) {
		rasterDefaultStylePanel = new RasterDefaultStylePanel(legend,
				defaultColorModel);
	}

	public Component getComponent() {
		return rasterDefaultStylePanel;
	}

	public URL getIconURL() {
		return null;
	}

	public String getTitle() {
		return "Simple default colorModel for raster data";
	}

	public String initialize() {
		return null;
	}

	public String getInfoText() {
		return "Edit the raster styling";
	}

	public String postProcess() {
		return null;
	}

	public String validateInput() {
		return null;
	}

	public ColorModel getColorModel() {
		return rasterDefaultStylePanel.getColorModel();
	}

	public float getOpacity() {
		return rasterDefaultStylePanel.getOpacity();
	}
}