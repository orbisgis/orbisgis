package org.orbisgis.geocatalog.resources;

import java.util.ArrayList;
import java.util.List;

import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;
import org.orbisgis.core.OrbisgisCore;
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
		final FirstUIPanel firstPanel = new FirstUIPanel();
		final SecondUIPanel secondPanel = new SecondUIPanel(firstPanel);

		if (UIFactory.showDialog(new UIPanel[] { firstPanel, secondPanel })) {
			for (DBSource dBSource : secondPanel.getSelectedDBSources()) {
				System.out.println(dBSource.getTableName());
				final String name = OrbisgisCore.registerInDSF(dBSource
						.getTableName().toString(),
						new DBTableSourceDefinition(dBSource));
				resources.add(ResourceFactory.createResource(name,
						new AbstractGdmsSource()));
			}
		}
		return resources.toArray(new IResource[0]);
	}
}