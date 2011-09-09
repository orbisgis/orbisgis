/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Lead Erwan BOCHER, scientific researcher,
 *
 *  Developer lead : Pierre-Yves FADET, computer engineer.
 *
 *  User support lead : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://orbisgis.cerma.archi.fr/>
 * <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * Pierre-Yves.Fadet _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 **/

package org.orbisgis.core.ui.plugins.views.geocognition.wizards;

import java.util.Map;

import javax.swing.Icon;

import org.orbisgis.core.Services;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.geocognition.GeocognitionElementFactory;
import org.orbisgis.core.geocognition.symbology.GeocognitionLegendFactory;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.renderer.legend.carto.LegendManager;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.ui.components.sif.ChoosePanel;
import org.orbisgis.core.ui.plugins.views.geocognition.wizard.ElementRenderer;
import org.orbisgis.core.ui.plugins.views.geocognition.wizard.INewGeocognitionElement;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

public class NewLegend implements INewGeocognitionElement {

	private Object legend;

	public GeocognitionElementFactory[] getFactory() {
		return new GeocognitionElementFactory[] { new GeocognitionLegendFactory() };
	}

	public void runWizard() {
		LegendManager legendManager = Services.getService(LegendManager.class);
		Legend[] availableLegends = legendManager.getAvailableLegends();
		String[] names = new String[availableLegends.length];
		String[] ids = new String[availableLegends.length];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = availableLegends[i].getLegendTypeId();
			names[i] = availableLegends[i].getLegendTypeName();
		}
		ChoosePanel cp = new ChoosePanel("Select the legend type", names, ids);
		if (UIFactory.showDialog(cp)) {
			Legend legend = legendManager.getNewLegend(ids[cp
					.getSelectedIndex()]);
			this.legend = legend;
		} else {
			this.legend = null;
		}
	}

	public String getName() {
		return "Legend";
	}

	public ElementRenderer getElementRenderer() {
		return new ElementRenderer() {

			public Icon getIcon(String contentTypeId,
					Map<String, String> properties) {
				return getDefaultIcon(contentTypeId);
			}

			public Icon getDefaultIcon(String contentTypeId) {
				if (getFactory()[0].acceptContentTypeId(contentTypeId)) {
					return OrbisGISIcon.PALETTE;
				} else {
					return null;
				}
			}

			public String getTooltip(GeocognitionElement element) {
				return null;
			}

		};
	}

	public Object getElement(int index) {
		return legend;
	}

	public int getElementCount() {
		return (legend != null) ? 1 : 0;
	}

	public String getFixedName(int index) {
		return null;
	}

	public boolean isUniqueIdRequired(int index) {
		return false;
	}

	public String getBaseName(int elementIndex) {
		return "Legend";
	}
}
