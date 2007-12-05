package org.orbisgis.core.windows;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.workspace.Workspace;

public class PersistenceContext {

	private Workspace workspace;
	private HashMap<String, File> nameFile;

	public PersistenceContext(HashMap<String, File> filePaths) {
		this.workspace = PluginManager.getWorkspace();
		this.nameFile = filePaths;
		if (nameFile == null) {
			nameFile = new HashMap<String, File>();
		}
	}

	public File getFile(String name, String prefix, String suffix) {
		File ret = nameFile.get(name);
		if (ret == null) {
			ret = workspace.getNewFile(prefix, suffix);
			nameFile.put(name, ret);
		}

		return ret;
	}

	public File getFile(String name) {
		File ret = nameFile.get(name);
		if (ret == null) {
			ret = workspace.getNewFile();
			nameFile.put(name, ret);
		}

		return ret;
	}

	public Iterator<String> getFileNames() {
		return nameFile.keySet().iterator();
	}

	public File get(String key) {
		return nameFile.get(key);
	}
}
