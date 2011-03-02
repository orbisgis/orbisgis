package org.orbisgis.core.ui.editorViews.toc.actions.cui.legends;

import org.orbisgis.core.sif.OpenFilePanel;

public class ImageFileChooser extends OpenFilePanel {

	public static final String IMAGE_FILE_CHOOSER = "org.orbisgis.ImageFileChooser";

	public ImageFileChooser(String title) {
		super(IMAGE_FILE_CHOOSER, title);
		this.addFilter(new String[] { "tif", "tiff" }, "TIF");
		this.addFilter("png", "PNG");
		this.addFilter("jpg", "JPG");
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
