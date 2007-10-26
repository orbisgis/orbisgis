package org.orbisgis.geocatalog.resources;

import org.orbisgis.core.resourceTree.BasicResource;

public class FileResource extends BasicResource{

	// This will keep a reference to the real file
	private String filePath = null;

	public FileResource(String name, String filePath) {
		super(name);
		this.filePath = filePath;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}