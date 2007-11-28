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
import org.sif.UIPanel;

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
		UIPanel[] panels = wizard.getWizardPanels();
		boolean ok = UIFactory.showDialog(panels);
		if (ok) {
			myCatalog.setIgnoreSourceOperations(true);
			IResource[] resources = wizard.getResources();
			for (IResource resource : resources) {
				try {
					if (parent != null) {
						parent.addResource(resource);
					} else {
						myCatalog.getTreeModel().getRoot()
								.addResource(resource);
					}
				} catch (ResourceTypeException e) {
					PluginManager.error("Cannot add the layer", e);
				}
			}
			myCatalog.setIgnoreSourceOperations(false);

			return resources;
		} else {
			return new IResource[0];
		}
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
