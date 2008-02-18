package org.orbisgis.geoview.views.process;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.orbisgis.pluginManager.background.Job;
import org.orbisgis.pluginManager.background.ProgressBar;

public class JobRenderer implements ListCellRenderer {

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		Job job = (Job) value;
		return new ProgressBar(job);
	}

}
