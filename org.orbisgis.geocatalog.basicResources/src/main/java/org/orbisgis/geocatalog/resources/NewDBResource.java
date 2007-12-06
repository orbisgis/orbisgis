package org.orbisgis.geocatalog.resources;

import java.util.ArrayList;
import java.util.List;

import org.orbisgis.geocatalog.INewResource;
import org.sif.DynamicUIPanel;
import org.sif.SQLUIPanel;
import org.sif.UIFactory;
import org.sif.UIPanel;

public class NewDBResource implements INewResource {

	public String getName() {
		return "Add a set of DataBase's tables";
	}

	public IResource[] getResources() {
		final List<IResource> resources = new ArrayList<IResource>();
		final DynamicUIPanel dbPanel = UIFactory.getDynamicUIPanel(
				"org.orbisgis.geocatalog.resources.NewDBResource.ID1",
				getName(), null, new String[] { "DBMS Server address",
						"portNumber", "Remote DataBase name", "User name",
						"Password" }, new int[] { SQLUIPanel.STRING,
						SQLUIPanel.INT, SQLUIPanel.STRING, SQLUIPanel.STRING,
						SQLUIPanel.STRING }, new String[] { "portNumber > 0",
						"portNumber < 65536" }, new String[] {
						"The port number must be greater than 0 !",
						"The port number must be less than 65536 !" });
		if (UIFactory.showDialog(new UIPanel[] { dbPanel })) {
			// File[] files = filePanel.getSelectedFiles();
			// for (File file : files) {
			// String name = OrbisgisCore.registerInDSF(file.getName(),
			// new FileSourceDefinition(file));
			// resources.add(ResourceFactory.createResource(name,
			// new AbstractGdmsSource()));
			// }
		}
		return resources.toArray(new IResource[0]);
	}
}
