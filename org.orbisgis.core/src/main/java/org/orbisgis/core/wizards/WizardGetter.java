package org.orbisgis.core.wizards;

import java.util.ArrayList;

import org.orbisgis.pluginManager.ExtensionPointManager;
import org.orbisgis.pluginManager.ItemAttributes;

public class WizardGetter<T> {

	private String extensionPoint;

	public WizardGetter(String extensionPoint) {
		this.extensionPoint = extensionPoint;
	}

	public ArrayList<WizardAndId<T>> getWizards(String id) {

		ExtensionPointManager<T> epm = new ExtensionPointManager<T>(
				extensionPoint);
		String query = "/extension/wizard";
		if (id != null) {
			query += "[@id='" + id + "']";
		}
		ArrayList<ItemAttributes<T>> wizards = epm.getItemAttributes(query);
		ArrayList<WizardAndId<T>> ret = new ArrayList<WizardAndId<T>>();
		for (ItemAttributes<T> itemAttributes : wizards) {
			ret.add(new WizardAndId<T>(itemAttributes.getInstance("class"),
					itemAttributes.getAttribute("id")));
		}
		return ret;

	}

}
