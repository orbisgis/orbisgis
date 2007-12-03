package org.orbisgis.pluginManager.ui;

import java.io.File;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.basic.BasicFileChooserUI;

public abstract class SaveFilePanel extends OpenFilePanel {

	public SaveFilePanel(String title) {
		super(title);
	}

	public SaveFilePanel(String title, Map<String, String> formatAndDescription) {
		super(title, formatAndDescription);
		getFileChooser().setDialogType(JFileChooser.SAVE_DIALOG);
	}

	@Override
	public File getSelectedFile() {
		File ret;
		JFileChooser fc = getFileChooser();
		FileChooserUI ui = fc.getUI();
		if (ui instanceof BasicFileChooserUI) {
			BasicFileChooserUI basicUI = (BasicFileChooserUI) ui;
			String fileName = basicUI.getFileName();
			if ((fileName == null) || (fileName.length() == 0)) {
				ret = null;
			} else {
				ret = autoComplete(new File(fileName));
			}
		} else {
			ret = autoComplete(super.getSelectedFile());
		}
		System.out.println(ret);
		return ret;
	}

	private File autoComplete(File selectedFile) {
		FileFilter ff = getFileChooser().getFileFilter();
		if (ff instanceof FormatFilter) {
			FormatFilter filter = (FormatFilter) ff;
			return filter.autoComplete(selectedFile);
		} else {
			return selectedFile;
		}
	}

	public String validate() {
		File file = getSelectedFile();
		if (file == null) {
			return "A file must be specified";
		} else {
			return null;
		}
	}

	@Override
	public File[] getSelectedFiles() {
		return new File[] { getSelectedFile() };
	}
}
