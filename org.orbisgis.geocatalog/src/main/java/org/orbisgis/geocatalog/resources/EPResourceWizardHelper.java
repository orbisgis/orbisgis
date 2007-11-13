package org.orbisgis.geocatalog.resources;

import java.awt.event.ActionListener;
import java.util.ArrayList;

import org.orbisgis.core.ChoosePanel;
import org.orbisgis.core.Menu;
import org.orbisgis.core.MenuTree;
import org.orbisgis.core.resourceTree.IResource;
import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.INewResource;
import org.orbisgis.pluginManager.ExtensionPointManager;
import org.sif.UIFactory;
import org.sif.UIPanel;

public class EPResourceWizardHelper {

	public static void openWizard(Catalog myCatalog, IResource parent) {
		INewResource[] wizards = getWizards();
		String[] names = new String[wizards.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = wizards[i].getName();
		}
		ChoosePanel cp = new ChoosePanel("Select the resource type", names);
		boolean accepted = UIFactory.showDialog(cp);
		if (accepted) {
			int index = cp.getSelectedIndex();
			runWizard(myCatalog, wizards[index], parent);

		}

	}

	public static void runWizard(Catalog myCatalog, String id, IResource parent) {
		INewResource[] wizards = getWizards();
		INewResource wizard = null;
		for (INewResource newResource : wizards) {
			if (newResource.getName().equals(id)) {
				wizard = newResource;
				break;
			}
		}
		runWizard(myCatalog, wizard, parent);
	}

	public static void runWizard(Catalog myCatalog, INewResource wizard,
			IResource parent) {
		UIPanel[] panels = wizard.getWizardPanels();
		boolean ok = UIFactory.showDialog(panels);
		if (ok) {
			myCatalog.setIgnoreSourceOperations(true);
			IResource[] resources = wizard.getResources();
			myCatalog.setIgnoreSourceOperations(false);
			for (IResource resource : resources) {
				if (parent != null) {
					myCatalog.getTreeModel().insertNodeInto(resource, parent);
				} else {
					myCatalog.getTreeModel().insertNode(resource);
				}
			}
		}
	}

	public static INewResource[] getWizards() {
		ExtensionPointManager<INewResource> epm = new ExtensionPointManager<INewResource>(
				"org.orbisgis.geocatalog.ResourceWizard");
		ArrayList<INewResource> wizards = epm.getInstancesFrom(
				"/extension/wizard", "class");
		return wizards.toArray(new INewResource[0]);
	}

	public static void addWizardMenus(MenuTree menuTree, ActionListener al) {
		INewResource[] wizards = getWizards();
		for (INewResource wizard : wizards) {
			Menu menu = new Menu("org.orbisgis.geocatalog.file.New", wizard
					.getName(), "org.orbisgis.geocatalog.file.new.Wizards",
					wizard.getName(), null, al);
			menuTree.addMenu(menu);
		}
	}
}
