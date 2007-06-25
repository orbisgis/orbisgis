package org.orbisgis.plugin.view.ui.workbench;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;

import org.orbisgis.plugin.view.utilities.file.SimpleFileFilter;

public class OurFileChooser extends JFileChooser {
	private Component parent;

	public OurFileChooser(final String extensions, final String description,
			final boolean multiSelectionEnabled) {
		this(new String[] { extensions }, description, multiSelectionEnabled);
	}

	public OurFileChooser(final String[] extensions, final String description,
			final boolean multiSelectionEnabled) {
		super(new File("../../datas2tests/"));
		// jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		setMultiSelectionEnabled(multiSelectionEnabled);
		addChoosableFileFilter(new SimpleFileFilter(extensions, description));
		setAcceptAllFileFilterUsed(false);
	}

	public File[] selectedFiles() {
		if (JFileChooser.APPROVE_OPTION == showOpenDialog(parent)) {
			return super.getSelectedFiles();
		}
		return new File[0];
	}
}