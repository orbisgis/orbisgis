package org.orbisgis.geocatalog.resources;

import java.awt.event.ActionListener;
import java.util.ArrayList;

import org.orbisgis.core.ChoosePanel;
import org.orbisgis.core.Menu;
import org.orbisgis.core.MenuTree;
import org.orbisgis.core.resourceTree.IResource;
import org.orbisgis.core.resourceTree.ResourceTypeException;
import org.orbisgis.core.wizards.WizardAndId;
import org.orbisgis.core.wizards.WizardGetter;
import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.INewResource;
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
		ArrayList<WizardAndId<INewResource>> wizards = getWizards(id);
		return runWizard(myCatalog, wizards.get(0).getWizard(), parent);
	}

	public static IResource[] runWizard(Catalog myCatalog, INewResource wizard,
			IResource parent) {
		UIPanel[] panels = wizard.getWizardPanels();
		boolean ok = UIFactory.showDialog(panels);
		if (ok) {
			IResource[] resources = wizard.getResources();
			myCatalog.setIgnoreSourceOperations(true);
			for (IResource resource : resources) {
				try {
					if (parent != null) {
						parent.addResource(resource);
					} else {
						myCatalog.getTreeModel().getRoot()
								.addResource(resource);
					}
				} catch (ResourceTypeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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

	public static void addWizardMenus(MenuTree menuTree, ActionListener al) {
		ArrayList<WizardAndId<INewResource>> wizards = getWizards(null);
		for (WizardAndId<INewResource> wizard : wizards) {
			Menu menu = new Menu("org.orbisgis.geocatalog.file.New", wizard
					.getId(), "org.orbisgis.geocatalog.file.new.Wizards",
					wizard.getWizard().getName(), null, al);
			menuTree.addMenu(menu);
		}
	}
}
