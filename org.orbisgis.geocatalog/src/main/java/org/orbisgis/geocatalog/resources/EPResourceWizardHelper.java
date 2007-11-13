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
import org.orbisgis.pluginManager.ItemAttributes;
import org.sif.UIFactory;
import org.sif.UIPanel;

public class EPResourceWizardHelper {

	public static void openWizard(Catalog myCatalog, IResource parent) {
		WizardAndId[] wizards = getWizards(null);
		String[] names = new String[wizards.length];
		String[] ids = new String[wizards.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = wizards[i].wizard.getName();
			ids[i] = wizards[i].id;
		}
		ChoosePanel cp = new ChoosePanel("Select the resource type", names, ids);
		boolean accepted = UIFactory.showDialog(cp);
		if (accepted) {
			int index = cp.getSelectedIndex();
			runWizard(myCatalog, wizards[index].wizard, parent);

		}

	}

	public static void runWizard(Catalog myCatalog, String id, IResource parent) {
		WizardAndId[] wizards = getWizards(id);
		runWizard(myCatalog, wizards[0].wizard, parent);
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

	public static WizardAndId[] getWizards(String id) {
		ExtensionPointManager<INewResource> epm = new ExtensionPointManager<INewResource>(
				"org.orbisgis.geocatalog.ResourceWizard");
		String query = "/extension/wizard";
		if (id != null) {
			query += "[@id='" + id + "']";
		}
		ArrayList<ItemAttributes<INewResource>> wizards = epm.getItemAttributes(query);
		ArrayList<WizardAndId> ret = new ArrayList<WizardAndId>();
		for (ItemAttributes<INewResource> itemAttributes : wizards) {
			ret.add(new WizardAndId(itemAttributes.getInstance("class"),
					itemAttributes.getAttribute("id")));
		}
		return ret.toArray(new WizardAndId[0]);
	}

	public static void addWizardMenus(MenuTree menuTree, ActionListener al) {
		WizardAndId[] wizards = getWizards(null);
		for (WizardAndId wizard : wizards) {
			Menu menu = new Menu("org.orbisgis.geocatalog.file.New", wizard
					.id, "org.orbisgis.geocatalog.file.new.Wizards",
					wizard.wizard.getName(), null, al);
			menuTree.addMenu(menu);
		}
	}

	private static class WizardAndId {
		INewResource wizard;
		String id;

		public WizardAndId(INewResource wizard, String id) {
			super();
			this.wizard = wizard;
			this.id = id;
		}
	}
}
