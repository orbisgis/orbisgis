package org.orbisgis.geocatalog.resources;

import java.util.ArrayList;
import java.util.List;

import org.orbisgis.geocatalog.INewResource;
import org.orbisgis.geocatalog.resources.db.FirstUIPanel;
import org.orbisgis.geocatalog.resources.db.SecondUIPanel;
import org.sif.UIFactory;
import org.sif.UIPanel;

public class NewDBResource implements INewResource {

	public String getName() {
		return "Add a set of DataBase's tables";
	}

	public IResource[] getResources() {
		final List<IResource> resources = new ArrayList<IResource>();
		if (UIFactory.showDialog(prepareUIPanel())) {
		}
		return resources.toArray(new IResource[0]);
	}

	private UIPanel[] prepareUIPanel() {
		final FirstUIPanel firstPanel = new FirstUIPanel();
		return new UIPanel[] { firstPanel, new SecondUIPanel(firstPanel) };
	}
}