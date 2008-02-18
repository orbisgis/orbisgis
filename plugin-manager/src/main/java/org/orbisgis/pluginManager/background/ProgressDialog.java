package org.orbisgis.pluginManager.background;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

public class ProgressDialog extends JDialog {

	private Job job;

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
		this.getContentPane().removeAll();
		this.getContentPane().add(new ProgressBar(job));
		this.pack();
	}
}
