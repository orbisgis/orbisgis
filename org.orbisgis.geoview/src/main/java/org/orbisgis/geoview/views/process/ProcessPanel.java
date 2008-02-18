package org.orbisgis.geoview.views.process;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataListener;

import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.background.Job;
import org.orbisgis.pluginManager.background.ProgressListener;

public class ProcessPanel extends JPanel {

	private JList jobList;

	public ProcessPanel() {
		this.setBackground(Color.white);
		jobList = new JList();
		jobList.setCellRenderer(new JobRenderer());
		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(jobList), BorderLayout.CENTER);
		jobList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				for (int i = 0; i < jobList.getModel().getSize(); i++) {
					Rectangle bounds = jobList.getCellBounds(i, i);
					if (bounds.contains(e.getPoint())) {
						Job job = (Job) jobList.getModel().getElementAt(i);
						job.cancel();
					}
				}
			}

		});
	}

	public void refresh() {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {

				public void run() {
					update();
				}

			});
		} else {
			update();
		}
	}

	private void update() {
		jobList
				.setModel(new JobListModel(PluginManager.getJobQueue()
						.getJobs()));
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.clearRect(0, 0, getWidth(), getHeight());
		super.paintComponent(g);
	}

	private class JobListModel implements ListModel {

		private Job[] jobs;

		public JobListModel(Job[] jobs) {
			this.jobs = jobs;
			for (Job job : jobs) {
				job.addProgressListener(new ProgressListener() {

					public void subTaskStarted(Job job) {
					}

					public void subTaskFinished(Job job) {
					}

					public void progressChanged(Job job) {
						refresh();
					}

				});
			}
		}

		public void addListDataListener(ListDataListener l) {
		}

		public Object getElementAt(int index) {
			return jobs[index];
		}

		public int getSize() {
			return jobs.length;
		}

		public void removeListDataListener(ListDataListener l) {
		}

	}

}
