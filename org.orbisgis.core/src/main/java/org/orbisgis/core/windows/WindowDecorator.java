package org.orbisgis.core.windows;

import java.io.File;
import java.util.HashMap;


public class WindowDecorator {
	private IWindow window;
	private HashMap<String, File> files;

	public WindowDecorator(IWindow window, HashMap<String, File> storageFiles) {
		this.window = window;
		this.files = storageFiles;
	}

	public IWindow getWindow() {
		return window;
	}

	public HashMap<String, File> getFiles() {
		return files;
	}
}
