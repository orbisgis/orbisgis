package org.orbisgis.editorViews.toc.actions.cui.extensions;

import org.orbisgis.pluginManager.ui.OpenFilePanel;

public class ImageFileChooser extends OpenFilePanel {

	public static final String IMAGE_FILE_CHOOSER = "org.orbisgis.ImageFileChooser";

	public ImageFileChooser(String title) {
		super(IMAGE_FILE_CHOOSER, title);
		this.addFilter(new String[] { "tif", "tiff" }, "TIF (*.tif; *.tiff)");
		this.addFilter("png", "PNG (*.png)");
		this.addFilter("jpg", "JPG (*.jpg)");
	}

	public String[] getErrorMessages() {
		return null;
	}

	public String getId() {
		return IMAGE_FILE_CHOOSER;
	}

	public String[] getValidationExpressions() {
		return null;
	}

}
