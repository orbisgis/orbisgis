package org.orbisgis.views.jobs;

import java.awt.Component;

import javax.swing.SwingUtilities;

import org.orbisgis.Services;
import org.orbisgis.pluginManager.background.BackgroundListener;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.pluginManager.background.Job;
import org.orbisgis.view.IView;

public class JobView implements IView {

	private JobPanel processPanel = new JobPanel();

	public void delete() {
	}

	public Component getComponent() {
		return processPanel;
	}

	public void initialize() {
		BackgroundManager bm = (BackgroundManager) Services
				.getService("org.orbisgis.BackgroundManager");

		bm.addBackgroundListener(new BackgroundListener() {

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

	public void loadStatus() {
	}

	public void saveStatus() {
	}

}
