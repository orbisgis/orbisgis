package org.orbisgis.pluginManager.background;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.sif.CRFlowLayout;
import org.sif.CarriageReturn;

public class ProgressBar extends JPanel {

	private Job job;
	private JProgressBar overallProgressBar;
	private JLabel lblProgress;
	private JLabel lblSubTask;
	private JLabel lblTask;
	private ProgressListener progressListener;

	public ProgressBar(Job job) {
		this.job = job;
		progressListener = new ProgressListener() {

			public void subTaskStarted(Job job) {
				changeSubTask();
			}

			public void subTaskFinished(Job job) {
				changeSubTask();
			}

			public void progressChanged(Job job) {
				changeProgress();
			}

		};
		job.addProgressListener(progressListener);
		this.setLayout(new BorderLayout());
		this.setBackground(new Color(224, 224, 224));
		this.add(getLabelPanel(), BorderLayout.NORTH);
		overallProgressBar = new JProgressBar(0, 100);
		int overallProgress = job.getOverallProgress();
		this.add(overallProgressBar, BorderLayout.CENTER);
		overallProgressBar.setValue(overallProgress);
		lblProgress.setText(Integer.toString(overallProgress) + "%");
		changeSubTask();
		JButton btn = new JButton("Cancel");
		this.add(btn, BorderLayout.EAST);
		btn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ProgressBar.this.job.cancel();
			}

		});

		if (job.isCancelled()) {
			lblProgress.setEnabled(false);
			lblSubTask.setEnabled(false);
			lblTask.setEnabled(false);
			overallProgressBar.setEnabled(false);
			btn.setEnabled(false);
		}
	}

	private JPanel getLabelPanel() {
		JPanel ret = new JPanel();
		ret.setLayout(new CRFlowLayout());
		lblTask = new JLabel(job.getTaskName() + ": ");
		ret.add(lblTask);
		lblProgress = new JLabel("0%");
		ret.add(lblProgress);
		ret.add(new CarriageReturn());
		lblSubTask = new JLabel("");
		Font taskFont = lblTask.getFont();
		lblSubTask.setFont(taskFont.deriveFont(taskFont.getSize() - 2));
		ret.add(lblSubTask);
		return ret;
	}

	public void setJob(Job job) {
		this.job.removeProgressListener(progressListener);
		this.job = job;
		this.job.addProgressListener(progressListener);
		changeSubTask();
		changeProgress();

	}

	private void changeSubTask() {
		String currentTaskName = job.getCurrentTaskName();
		if (currentTaskName != null) {
			lblSubTask.setText(currentTaskName + "(" + job.getCurrentProgress()
					+ "%)");
		} else if (job.isStarted()){
			lblSubTask.setText("processing...");
		} else {
			lblSubTask.setText("waiting...");
		}
	}

	private void changeProgress() {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				int overallProgress = ProgressBar.this.job.getOverallProgress();
				overallProgressBar.setValue(overallProgress);
				lblProgress.setText(Integer.toString(overallProgress) + "%");
				changeSubTask();
			}

		});
	}

}
