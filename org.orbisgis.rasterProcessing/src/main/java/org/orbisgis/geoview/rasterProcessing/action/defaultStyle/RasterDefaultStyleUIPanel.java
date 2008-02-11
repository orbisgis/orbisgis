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
package org.orbisgis.geoview.rasterProcessing.action.defaultStyle;

import java.awt.Component;
import java.net.URL;

import org.grap.model.GeoRaster;
import org.orbisgis.pluginManager.PluginManager;
import org.sif.SQLUIPanel;

public class RasterDefaultStyleUIPanel implements SQLUIPanel {
	private GeoRaster geoRaster;
	private RasterDefaultStylePanel rasterDefaultStylePanel;

	public RasterDefaultStyleUIPanel(final GeoRaster geoRaster) {
		this.geoRaster = geoRaster;
	}

	public Component getComponent() {
		if (null == rasterDefaultStylePanel) {
			rasterDefaultStylePanel = new RasterDefaultStylePanel(geoRaster);
		}
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

	public String validate() {
		return null;
	}

	public String cbGetSelection() {
		return ((RasterDefaultStylePanel) getComponent())
				.getJComboBoxSelection();
	}

	public String getOpacity() {
		return ((RasterDefaultStylePanel) getComponent()).getOpacity();
	}

	public String[] getErrorMessages() {
		return null;
	}

	public String[] getFieldNames() {
		return new String[] { "cbList", "opacity" };
	}

	public int[] getFieldTypes() {
		return new int[] { STRING, INT };
	}

	public String getId() {
		return "org.orbisgis.ui.RasterDefaultStyle";
	}

	public String[] getValidationExpressions() {
		return null;
	}

	public String[] getValues() {
		return new String[] { cbGetSelection(), getOpacity() };
	}

	public void setValue(String fieldName, String fieldValue) {
		if (fieldName.equals("cbList")) {
			rasterDefaultStylePanel.setLut(fieldValue);
		} else if (fieldName.equals("opacity")) {
			rasterDefaultStylePanel.setOpacity(fieldValue);
		} else {
			PluginManager
					.error("Error in RasterDefaultStyleUIClass.setValue()");
		}
	}

	public String getInfoText() {
		// TODO Auto-generated method stub
		return null;
	}

	public String postProcess() {
		// TODO Auto-generated method stub
		return null;
	}

	public String validateInput() {
		// TODO Auto-generated method stub
		return null;
	}
}