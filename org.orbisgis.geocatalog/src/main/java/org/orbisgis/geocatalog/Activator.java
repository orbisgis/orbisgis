package org.orbisgis.geocatalog;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.gdms.source.Source;
import org.gdms.source.SourceManager;
import org.orbisgis.CollectionUtils;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.core.windows.EPWindowHelper;
import org.orbisgis.geocatalog.resources.AbstractGdmsSource;
import org.orbisgis.geocatalog.resources.IResource;
import org.orbisgis.geocatalog.resources.NodeFilter;
import org.orbisgis.pluginManager.PluginActivator;

public class Activator implements PluginActivator {

	private ArrayList<String> memoryResources;

	public boolean allowStop() {
		GeoCatalog geoCatalog = (GeoCatalog) EPWindowHelper
				.getWindows("org.orbisgis.geocatalog.Window")[0];
		Catalog catalog = geoCatalog.getCatalog();
		IResource[] res = catalog.getTreeModel().getNodes(new NodeFilter() {
			public boolean accept(IResource resource) {
				return true;
			}
		});

		memoryResources = new ArrayList<String>();
		SourceManager sm = OrbisgisCore.getDSF().getSourceManager();
		for (IResource resource : res) {
			if (resource.getResourceType() instanceof AbstractGdmsSource) {
				Source src = sm.getSource(resource.getName());
				if ((src.getType() & SourceManager.MEMORY) == SourceManager.MEMORY) {
					memoryResources.add(src.getName());
				}
			}
		}

		if (memoryResources.size() > 0) {
			String resourceList = CollectionUtils
					.getCommaSeparated(memoryResources.toArray(new String[0]));

			int exit = JOptionPane
					.showConfirmDialog(
							catalog,
							"The following resources are stored "
									+ "in memory and its content may be lost: \n"
									+ resourceList
									+ ".\nDo you want to exit"
									+ " and probably lose the content of those sources?",
							"Loose object resources?",
							JOptionPane.YES_NO_OPTION);

			return exit == JOptionPane.YES_OPTION;
		} else {
			return true;
		}
	}

	public void start() throws Exception {

	}

	public void stop() throws Exception {
	}

}
