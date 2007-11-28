package org.orbisgis.pluginManager.ui;

import java.io.File;

import javax.swing.JFileChooser;

public class FolderFilePanel extends FilePanel {

	private String title;
	private String dir;

	public FolderFilePanel(String title, String dir) {
		this.title = title;
		this.dir = dir;
	}

	@Override
	protected JFileChooser getFileChooser() {
		JFileChooser ret = super.getFileChooser();
		ret.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		ret.setMultiSelectionEnabled(false);
		ret.setSelectedFile(new File(dir));
		return ret;
	}

	@Override
	public String getTitle() {
		return title;
	}

}
