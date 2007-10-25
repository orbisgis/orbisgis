package org.orbisgis.geocatalog.resources;

import java.util.ArrayList;

import org.orbisgis.core.ChoosePanel;
import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.INewResource;
import org.orbisgis.pluginManager.ExtensionPointManager;
import org.sif.UIFactory;
import org.sif.UIPanel;

public class ResourceWizardEP {

	public static IResource[] openWizard(Catalog myCatalog) {
		ExtensionPointManager<INewResource> epm = new ExtensionPointManager<INewResource>(
				"org.orbisgis.geocatalog.ResourceWizard");
		ArrayList<INewResource> wizards = epm.getInstancesFrom(
				"/extension/wizard", "class");
		String[] names = new String[wizards.size()];
		for (int i = 0; i < names.length; i++) {
			names[i] = wizards.get(i).getName();
		}
		ChoosePanel cp = new ChoosePanel("Select the resource type", names);
		boolean accepted = UIFactory.showDialog(cp);
		if (accepted) {
			int index = cp.getSelectedIndex();
			INewResource wizard = wizards.get(index);
			UIPanel[] panels = wizard.getWizardPanels();
			boolean ok = UIFactory.showDialog(panels);
			if (ok) {
				myCatalog.setIgnoreSourceOperations(true);
				IResource[] resources = wizard.getResources();
				myCatalog.setIgnoreSourceOperations(false);
				return resources;
			}
		}

		return new IResource[0];
	}
}
