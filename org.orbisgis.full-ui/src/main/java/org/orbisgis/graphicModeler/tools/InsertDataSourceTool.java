package org.orbisgis.graphicModeler.tools;

import java.util.ArrayList;

import org.gdms.source.SourceManager;
import org.orbisgis.DataManager;
import org.orbisgis.Services;

public class InsertDataSourceTool extends AbstractInsertionTool {
	@Override
	protected String[] getAvailableElements() {
		SourceManager manager = Services.getService(DataManager.class)
				.getSourceManager();
		String[] sources = manager.getSourceNames();
		ArrayList<String> validSources = new ArrayList<String>();
		for (int i = 0; i < sources.length; i++) {
			if (manager.getSource(sources[i]).isWellKnownName()) {
				validSources.add(sources[i]);
			}
		}

		return validSources.toArray(new String[0]);
	}

	@Override
	protected String getType() {
		return DATASOURCE_TYPE;
	}
}
