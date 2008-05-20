package org.orbisgis.pluginManager.background;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

public class ProgressDialog extends JDialog {

	private Job job;

	private ProgressBar progressBar;

	public ProgressDialog() {
		this.setModal(true);
		this.getContentPane().setLayout(new BorderLayout());
		this.setLocationRelativeTo(null);
		this.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent e) {
				job.start();
			}

		});
	}

	public void setJob(final Job job) {
		this.job = job;
		if (SwingUtilities.isEventDispatchThread()) {
			refreshProgressBar();
		} else {
			SwingUtilities.invokeLater(new Runnable() {

				public void run() {
					refreshProgressBar();
				}

			});
		}
	}

	private void refreshProgressBar() {
		if (progressBar != null) {
			progressBar.setJob(job);
		} else {
			progressBar = new ProgressBar(job);
			this.getContentPane().add(progressBar);
		}
		this.pack();
	}

	public void jobFinished() {
		job = null;
		progressBar = null;
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {

				public void run() {
					setVisible(false);
				}

			});
		} else {
			setVisible(false);
		}
	}
}
