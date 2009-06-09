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
package org.orbisgis.core.ui.wizards;

import java.util.ArrayList;
import java.util.HashMap;

import org.orbisgis.core.ui.action.IActionAdapter;
import org.orbisgis.core.ui.action.IActionFactory;
import org.orbisgis.core.ui.action.Menu;
import org.orbisgis.core.ui.action.MenuTree;
import org.orbisgis.core.ui.components.sif.CategorizedChoosePanel;
import org.orbisgis.pluginManager.ExtensionPointManager;
import org.orbisgis.pluginManager.ItemAttributes;
import org.orbisgis.sif.UIFactory;

public abstract class EPWizardHelper<Wiz extends IWizard, Res extends Object> {

	/**
	 * Returns the new resources or null if no resource should be added
	 * 
	 * @return
	 */
	public Res[] openWizard() {
		HashMap<String, String> categoriesNames = getCategories();
		ExtensionPointManager<Wiz> epm = new ExtensionPointManager<Wiz>(
				getExtensionPointId());
		String query = "/extension/wizard";
		ArrayList<ItemAttributes<Wiz>> xmlWizards = epm
				.getItemAttributes(query);
		CategorizedChoosePanel ccp = new CategorizedChoosePanel(
				"Select type of " + getElementName(), getExtensionPointId());
		for (ItemAttributes<Wiz> itemAttributes : xmlWizards) {
			IWizard wizard = itemAttributes.getInstance("class");
			String id = itemAttributes.getAttribute("id");
			String icon = itemAttributes.getAttribute("icon");
			String categoryId = itemAttributes.getAttribute("category");
			if (categoryId != null) {
				ccp.addOption(categoryId, categoriesNames.get(categoryId),
						wizard.getName(), id, icon);
			} else {
				ccp.addOption("org.orbisgis.DefaultCategory", "General", wizard
						.getName(), id, icon);
			}
		}
		if (UIFactory.showDialog(ccp)) {
			String wizardId = ccp.getSelectedElement();
			for (ItemAttributes<Wiz> itemAttributes : xmlWizards) {
				String id = itemAttributes.getAttribute("id");
				if (id.equals(wizardId)) {
					Wiz wizard = itemAttributes.getInstance("class");
					return getElements(wizard);
				}
			}
		}
		return null;
	}

	protected abstract String getElementName();

	/**
	 * Returns the resource array created by the wizard or null if the wizard
	 * was canceled
	 * 
	 * @param wizard
	 * @return
	 */
	protected abstract Res[] getElements(Wiz wizard);

	protected abstract String getExtensionPointId();

	public Res[] runWizard(String wizardId) {
		ArrayList<WizardAndId<Wiz>> wizards = getWizards(wizardId);
		if (wizards.size() == 0) {
			throw new IllegalArgumentException("There is no such wizard: "
					+ wizardId);
		} else if (wizards.size() > 1) {
			throw new IllegalStateException("Duplicate wizard id :" + wizardId);
		} else {
			return getElements(wizards.get(0).getWizard());
		}
	}

	protected ArrayList<WizardAndId<Wiz>> getWizards(String wizardId) {
		WizardGetter<Wiz> wg = new WizardGetter<Wiz>(getExtensionPointId());
		return wg.getWizards(wizardId);
	}

	public void addWizardMenus(MenuTree menuTree, IActionFactory factory,
			String parentMenuId) {
		ExtensionPointManager<Wiz> epm = new ExtensionPointManager<Wiz>(
				getExtensionPointId());
		String query = "/extension/wizard";
		ArrayList<ItemAttributes<Wiz>> wizards = epm.getItemAttributes(query);
		for (ItemAttributes<Wiz> itemAttributes : wizards) {
			IWizard wizard = itemAttributes.getInstance("class");
			String id = itemAttributes.getAttribute("id");
			String icon = itemAttributes.getAttribute("icon");
			String category = itemAttributes.getAttribute("category");
			IActionAdapter action = factory.getAction(id,
					new HashMap<String, String>());
			if (action.isVisible()) {
				Menu menu = new Menu(parentMenuId, id, category, wizard
						.getName(), icon, false, action);
				menuTree.addMenu(menu);
			}
		}
	}

	protected HashMap<String, String> getCategories() {
		HashMap<String, String> categories = new HashMap<String, String>();
		ExtensionPointManager<Object> epm = new ExtensionPointManager<Object>(
				getExtensionPointId());
		String query = "/extension/category";
		ArrayList<ItemAttributes<Object>> xmlCategories = epm
				.getItemAttributes(query);
		for (ItemAttributes<Object> itemAttributes : xmlCategories) {
			categories.put(itemAttributes.getAttribute("id"), itemAttributes
					.getAttribute("name"));
		}

		return categories;
	}
}
