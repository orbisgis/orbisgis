package org.orbisgis.geocatalog.converter;

import java.util.Map;

import javax.swing.JFileChooser;

import org.orbisgis.pluginManager.ui.FilePanel;
import org.orbisgis.pluginManager.util.SimpleFileFilter;

public class InFilePanel extends FilePanel {

	public static final String SIF_ID = "org.orbigis.geocatalog.converter";

	private Map<String, String> formatAndDescription;

	public InFilePanel(Map<String, String> formatAndDescription) {

		this.formatAndDescription = formatAndDescription;
	}

	@Override
	protected JFileChooser getFileChooser() {
		fileChooser = new JFileChooser();
		fileChooser.setControlButtonsAreShown(false);
		fileChooser.setMultiSelectionEnabled(true);

		if (formatAndDescription != null) {
			for (String key : formatAndDescription.keySet()) {
				fileChooser.addChoosableFileFilter(new SimpleFileFilter(key,
						formatAndDescription.get(key)));
			}

		}
		return fileChooser;
	}

	@Override
	public String getId() {
		return SIF_ID;
	}

}
