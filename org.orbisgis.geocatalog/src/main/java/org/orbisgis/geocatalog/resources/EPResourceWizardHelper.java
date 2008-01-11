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
package org.orbisgis.geocatalog.resources;

import java.util.ArrayList;

import org.orbisgis.core.actions.IActionFactory;
import org.orbisgis.core.actions.Menu;
import org.orbisgis.core.actions.MenuTree;
import org.orbisgis.core.wizards.WizardAndId;
import org.orbisgis.core.wizards.WizardGetter;
import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.INewResource;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.ui.ChoosePanel;
import org.sif.UIFactory;

public class EPResourceWizardHelper {

	public static void openWizard(Catalog myCatalog, IResource parent) {
		ArrayList<WizardAndId<INewResource>> wizards = getWizards(null);
		String[] names = new String[wizards.size()];
		String[] ids = new String[wizards.size()];
		for (int i = 0; i < names.length; i++) {
			names[i] = wizards.get(i).getWizard().getName();
			ids[i] = wizards.get(i).getId();
		}
		ChoosePanel cp = new ChoosePanel("Select the resource type", names, ids);
		boolean accepted = UIFactory.showDialog(cp);
		if (accepted) {
			int index = cp.getSelectedIndex();
			runWizard(myCatalog, wizards.get(index).getWizard(), parent);

		}

	}

	public static IResource[] runWizard(Catalog myCatalog, String id,
			IResource parent) {
		if (id == null) {
			throw new RuntimeException("id is null");
		}
		ArrayList<WizardAndId<INewResource>> wizards = getWizards(id);
		return runWizard(myCatalog, wizards.get(0).getWizard(), parent);
	}

	public static IResource[] runWizard(Catalog myCatalog, INewResource wizard,
			IResource parent) {
		myCatalog.setIgnoreSourceOperations(true);
		IResource[] resources = wizard.getResources();
		for (IResource resource : resources) {
			try {
				if (parent != null) {
					parent.addResource(resource);
				} else {
					myCatalog.getTreeModel().getRoot().addResource(resource);
				}
			} catch (ResourceTypeException e) {
				PluginManager.error("Cannot add the layer", e);
			}
		}
		myCatalog.setIgnoreSourceOperations(false);

		return resources;
	}

	private static ArrayList<WizardAndId<INewResource>> getWizards(String id) {
		WizardGetter<INewResource> wg = new WizardGetter<INewResource>(
				"org.orbisgis.geocatalog.ResourceWizard");
		return wg.getWizards(id);
	}

	public static void addWizardMenus(MenuTree menuTree, IActionFactory factory) {
		ArrayList<WizardAndId<INewResource>> wizards = getWizards(null);
		for (WizardAndId<INewResource> wizard : wizards) {
			Menu menu = new Menu("org.orbisgis.geocatalog.file.New", wizard
					.getId(), "org.orbisgis.geocatalog.file.new.Wizards",
					wizard.getWizard().getName(), null, false, factory
							.getAction(wizard.getId()));
			menuTree.addMenu(menu);
		}
	}
}
