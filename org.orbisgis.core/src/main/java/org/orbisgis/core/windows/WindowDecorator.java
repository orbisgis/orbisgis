package org.orbisgis.core.windows;

import java.io.File;

public class WindowDecorator {
	private IWindow window;
	private File file;

	public WindowDecorator(IWindow window, File storageFile) {
		this.window = window;
		this.file = storageFile;
	}

	public IWindow getWindow() {
		return window;
	}

	public File getFile() {
		return file;
	}
}
