package org.orbisgis.geocatalog.resources;

import java.util.ArrayList;

import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.ChoosePanel;
import org.orbisgis.geocatalog.INewResource;
import org.orbisgis.pluginManager.Configuration;
import org.orbisgis.pluginManager.Extension;
import org.orbisgis.pluginManager.IExtensionRegistry;
import org.orbisgis.pluginManager.RegistryFactory;
import org.sif.UIFactory;
import org.sif.UIPanel;

public class ResourceWizardEP {

	public static IResource[] openWizard(Catalog myCatalog) {
		IExtensionRegistry reg = RegistryFactory.getRegistry();
		Extension[] exts = reg
				.getExtensions("org.orbisgis.geocatalog.resourceWizard");
		ArrayList<INewResource> wizards = new ArrayList<INewResource>();
		for (int i = 0; i < exts.length; i++) {
			Configuration c = exts[i].getConfiguration();

			INewResource nr = (INewResource) c
					.instantiateFromAttribute("/extension/wizard",
							"class");
			wizards.add(nr);
		}
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
