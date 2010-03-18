package org.orbisgis.core.ui.components.job;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundListener;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.background.Job;

public class JobPopup {

	public JobWindow panel;

	public JobWindow getPanel() {
		return panel;
	}

	public JobPopup(JFrame frame) {
		panel = new JobWindow(frame);
	}

	public void initialize() {

		BackgroundManager bm = (BackgroundManager) Services
				.getService(BackgroundManager.class);

		bm.addBackgroundListener(new BackgroundListener() {

			public void jobAdded(final Job job) {
				if (SwingUtilities.isEventDispatchThread()) {
					panel.show();
					panel.addJob(job);
				} else {
					SwingUtilities.invokeLater(new Runnable() {

						public void run() {
							panel.show();
							panel.addJob(job);
						}

					});
				}
			}

			public void jobRemoved(final Job job) {
				if (SwingUtilities.isEventDispatchThread()) {
					panel.show();
					panel.removeJob(job);
				} else {
					SwingUtilities.invokeLater(new Runnable() {

						public void run() {
							panel.show();
							panel.removeJob(job);
						}

					});
				}
			}

			public void jobReplaced(final Job job) {
				if (SwingUtilities.isEventDispatchThread()) {
					panel.show();
					panel.replaceJob(job);
				} else {
					SwingUtilities.invokeLater(new Runnable() {

						public void run() {
							panel.show();
							panel.replaceJob(job);
						}

					});
				}
			}

			@Override
			public void jobFinished(Job job) {

			}
		});
	}
}
