package org.orbisgis.pluginManager.background;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class ProgressBar extends JPanel {

	private Job job;
	private JProgressBar progressBar;

	public ProgressBar(Job job) {
		this.job = job;
		job.addProgressListener(new ProgressListener() {

			public void subTaskStarted(Job job) {

			}

			public void subTaskFinished(Job job) {

			}

			public void progressChanged(Job job) {
				SwingUtilities.invokeLater(new Runnable() {

					public void run() {
						progressBar
								.setValue(ProgressBar.this.job.getProgress());
					}

				});
			}

		});
		this.setLayout(new BorderLayout());
		this.setBackground(new Color(224, 224, 224));
		JLabel label = new JLabel(job.getTaskName());
		this.add(label, BorderLayout.NORTH);
		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(job.getProgress());
		this.add(progressBar, BorderLayout.CENTER);
		progressBar.setValue(job.getProgress());
		JButton btn = new JButton("Cancel");
		this.add(btn, BorderLayout.EAST);
		btn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ProgressBar.this.job.cancel();
			}

		});

		if (job.isCancelled()) {
			label.setEnabled(false);
			progressBar.setEnabled(false);
			btn.setEnabled(false);
		}
	}
}
