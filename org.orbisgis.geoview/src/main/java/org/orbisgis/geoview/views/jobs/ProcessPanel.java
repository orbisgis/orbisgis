package org.orbisgis.geoview.views.jobs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.background.Job;
import org.orbisgis.pluginManager.background.JobId;
import org.orbisgis.pluginManager.background.ProgressBar;
import org.sif.CRFlowLayout;
import org.sif.CarriageReturn;

public class ProcessPanel extends JPanel {

	private JPanel progressPanel;
	private HashMap<JobId, Component[]> idBar = new HashMap<JobId, Component[]>();

	public ProcessPanel() {
		this.setBackground(Color.white);
		progressPanel = new JPanel();
		progressPanel.setLayout(new CRFlowLayout());
		this.setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(progressPanel);
		this.add(scrollPane, BorderLayout.CENTER);
	}

	public void addJob(Job job) {
		progressPanel.removeAll();
		Job[] jobs = PluginManager.getJobQueue().getJobs();
		for (Job queuedJob : jobs) {
			Component[] comps = idBar.get(queuedJob.getId());
			if (comps != null) {
				((ProgressBar)comps[0]).setJob(queuedJob);
				for (Component component : comps) {
					progressPanel.add(component);
				}
			} else {
				ProgressBar bar = new ProgressBar(job);
				progressPanel.add(bar);
				CarriageReturn cr = new CarriageReturn();
				progressPanel.add(cr);
				idBar.put(job.getId(), new Component[] { bar, cr });
			}
		}
		System.out.println("Added job " + job.getId());
	}

	public void removeJob(Job job) {
		Job[] jobs = PluginManager.getJobQueue().getJobs();
		if (jobs.length == 0 || !job.getId().is(jobs[0].getId())) {
			Component[] comps = idBar.remove(job.getId());
			for (Component component : comps) {
				progressPanel.remove(component);
			}
			invalidate();
			repaint(0, 0, getWidth(), getHeight());
			progressPanel.invalidate();
			progressPanel.doLayout();
			System.out.println("Removed job " + job.getId());
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.fillRect(0, 0, getWidth(), getHeight());
		super.paintComponent(g);
	}

	public void replaceJob(Job job) {
		Component[] comps = idBar.get(job.getId());
		ProgressBar bar = (ProgressBar) comps[0];
		bar.setJob(job);
		System.out.println("Replaced job " + job.getId());
	}

}
