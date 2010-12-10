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

import java.util.ArrayList;
import java.util.Map;

import javax.swing.Icon;

import org.orbisgis.core.Services;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.geocognition.GeocognitionElementFactory;
import org.orbisgis.core.geocognition.symbology.GeocognitionSymbolFactory;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.renderer.symbol.SymbolManager;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.ui.components.sif.ChoosePanel;
import org.orbisgis.core.ui.plugins.views.geocognition.wizard.ElementRenderer;
import org.orbisgis.core.ui.plugins.views.geocognition.wizard.INewGeocognitionElement;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

public class NewSymbol implements INewGeocognitionElement {

	private Symbol symbol;

	public GeocognitionElementFactory[] getFactory() {
		return new GeocognitionElementFactory[] { new GeocognitionSymbolFactory() };
	}

	public void runWizard() {
		SymbolManager symbolManager = Services.getService(SymbolManager.class);
		ArrayList<Symbol> availableSymbols = symbolManager
				.getAvailableSymbols();
		String[] names = new String[availableSymbols.size()];
		String[] ids = new String[availableSymbols.size()];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = availableSymbols.get(i).getId();
			names[i] = availableSymbols.get(i).getClassName();
		}
		ChoosePanel cp = new ChoosePanel("Select the legend type", names, ids);
		if (UIFactory.showDialog(cp)) {

			Symbol symbol = symbolManager.createSymbol(ids[cp
					.getSelectedIndex()]);

			GeoCognitionSymbolBuilder symbolBuilder = new GeoCognitionSymbolBuilder();
			symbolBuilder.setSymbol(symbol);

			if (UIFactory.showDialog(symbolBuilder)) {
				this.symbol = symbolBuilder.getSymbolComposite();
			}

		} else {
			this.symbol = null;
		}
	}

	public String getName() {
		return "Symbol";
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
		return symbol;
	}

	public int getElementCount() {
		return (symbol != null) ? 1 : 0;
	}

	public String getFixedName(int index) {
		return null;
	}

	public boolean isUniqueIdRequired(int index) {
		return false;
	}

	public String getBaseName(int elementIndex) {
		return "Symbol";
	}
}
