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
package org.orbisgis.wizards;

import java.util.ArrayList;
import java.util.HashMap;

import org.orbisgis.action.IActionAdapter;
import org.orbisgis.action.IActionFactory;
import org.orbisgis.action.Menu;
import org.orbisgis.action.MenuTree;
import org.orbisgis.pluginManager.ui.ChoosePanel;
import org.sif.UIFactory;

public abstract class EPWizardHelper<Wiz extends IWizard, Res extends Object> {

	/**
	 * Returns the new resources or null if no resource should be added
	 *
	 * @return
	 */
	public Res[] openWizard() {
		ArrayList<WizardAndId<Wiz>> wizards = getWizards(null);
		String[] names = new String[wizards.size()];
		String[] ids = new String[wizards.size()];
		for (int i = 0; i < names.length; i++) {
			names[i] = wizards.get(i).getWizard().getName();
			ids[i] = wizards.get(i).getId();
		}
		ChoosePanel cp = new ChoosePanel("Select the " + getElementName()
				+ " type", names, ids);
		boolean accepted = UIFactory.showDialog(cp);
		if (accepted) {
			int index = cp.getSelectedIndex();
			Wiz wizard = wizards.get(index).getWizard();
			Res[] layers = getElements(wizard);
			return layers;
		} else {
			return null;
		}
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

	private ArrayList<WizardAndId<Wiz>> getWizards(String wizardId) {
		WizardGetter<Wiz> wg = new WizardGetter<Wiz>(getExtensionPointId());
		return wg.getWizards(wizardId);
	}

	public void addWizardMenus(MenuTree menuTree, IActionFactory factory,
			String parentMenuId) {
		ArrayList<WizardAndId<Wiz>> wizards = getWizards(null);
		for (WizardAndId<Wiz> wizard : wizards) {
			IActionAdapter action = factory.getAction(wizard.getId(),
					new HashMap<String, String>());
			if (action.isVisible()) {
				Menu menu = new Menu(parentMenuId, wizard.getId(), parentMenuId
						+ ".wizard", wizard.getWizard().getName(), null, false,
						action);
				menuTree.addMenu(menu);
			}
		}
	}

}
