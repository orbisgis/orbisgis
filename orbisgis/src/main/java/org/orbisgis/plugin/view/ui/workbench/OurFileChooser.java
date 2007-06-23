package org.orbisgis.plugin.view.ui.workbench;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;

import org.orbisgis.plugin.view.utilities.file.FileChooserAction;
import org.orbisgis.plugin.view.utilities.file.SimpleFileFilter;

public class OurFileChooser {
	private JFileChooser jfc;

	public OurFileChooser(final Component parent, final String extensions,
			final String description, final boolean multiSelectionEnabled,
			FileChooserAction fileChooserAction) {
		this(parent, new String[] { extensions }, description,
				multiSelectionEnabled, fileChooserAction);
	}

	public OurFileChooser(final Component parent, final String[] extensions,
			final String description, final boolean multiSelectionEnabled,
			FileChooserAction fileChooserAction) {
		jfc = new JFileChooser(new File("../../datas2tests/"));
		// jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		jfc.setMultiSelectionEnabled(multiSelectionEnabled);
		jfc
				.addChoosableFileFilter(new SimpleFileFilter(extensions,
						description));
		jfc.setAcceptAllFileFilterUsed(false);

		if (JFileChooser.APPROVE_OPTION == jfc.showOpenDialog(parent)) {
			final File[] files = jfc.getSelectedFiles();
			for (File file : files) {
				fileChooserAction.action(file);
			}
		}
	}
}