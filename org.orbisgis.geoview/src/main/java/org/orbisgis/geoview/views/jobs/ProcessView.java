package org.orbisgis.geoview.views.jobs;

import java.awt.Component;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.SwingUtilities;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.IView;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.SystemAdapter;
import org.orbisgis.pluginManager.background.Job;

public class ProcessView implements IView {

	private ProcessPanel processPanel = new ProcessPanel();

	public void delete() {
	}

	public Component getComponent(GeoView2D geoview) {
		return processPanel;
	}

	public void initialize(GeoView2D geoView2D) {
		PluginManager.addSystemListener(new SystemAdapter() {

			@Override
			public void jobAdded(final Job job) {
				if (SwingUtilities.isEventDispatchThread()) {
					processPanel.addJob(job);
				} else {
					SwingUtilities.invokeLater(new Runnable() {

						public void run() {
							processPanel.addJob(job);
						}

					});
				}
			}

			@Override
			public void jobRemoved(final Job job) {
				if (SwingUtilities.isEventDispatchThread()) {
					processPanel.removeJob(job);
				} else {
					SwingUtilities.invokeLater(new Runnable() {

						public void run() {
							processPanel.removeJob(job);
						}

					});
				}
			}

			@Override
			public void jobReplaced(final Job job) {
				if (SwingUtilities.isEventDispatchThread()) {
					processPanel.replaceJob(job);
				} else {
					SwingUtilities.invokeLater(new Runnable() {

						public void run() {
							processPanel.replaceJob(job);
						}

					});
				}
			}
		});
	}

	public void loadStatus(InputStream ois) {
	}

	public void saveStatus(OutputStream oos) {
	}

}
