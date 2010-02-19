/**
 * Job popup at bootom right to follow processes loading
 */

package org.orbisgis.core.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowEvent;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JWindow;

import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.background.Job;
import org.orbisgis.core.background.JobId;
import org.orbisgis.core.background.ProgressBar;
import org.orbisgis.core.sif.CRFlowLayout;
import org.orbisgis.core.sif.CarriageReturn;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;

public class JobWindow extends JPanel {

	private JWindow window;
	private JPanel progressPanel;
	private Container parent;
	private HashMap<JobId, Component[]> idBar = new HashMap<JobId, Component[]>();

	public JobWindow(JFrame frame) {
		this.parent = frame;
	}
	
	public void show() {
		if (window == null) {
			initUI();
			
		}				
		window.setSize(270, 150);		
		window.setBackground(Color.GRAY);
		WorkbenchContext wbContext = Services.getService(WorkbenchContext.class);
		parent = wbContext.getWorkbench().getFrame();
		centerParent();		
	}

	public void hide() {
		if (window != null) {
			window.setVisible(false);
		}
	}	
	
	public void centerParent () {
		  int x;
		  int y;
		  Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		  int resolution = Toolkit.getDefaultToolkit().getScreenResolution();

		  // Find out our parent 
		  Point topLeft = parent.getLocationOnScreen();
		  Dimension parentSize = parent.getSize();

		  Dimension mySize = getSize();

		  if (parentSize.width > mySize.width) 
		    x = ((parentSize.width - mySize.width)/2) + topLeft.x;
		  else 
		    x = topLeft.x;
		   
		  if (parentSize.height > mySize.height) 
		    y = ((parentSize.height - mySize.height)/2 -100) + topLeft.y;
		  else 
		    y = topLeft.y;
		  
		  window.setLocation (x + 445, y + 350);
		 
	}  

	public void initUI() {		
		window = new JWindow();
		
		progressPanel = new JPanel();
		progressPanel.setLayout(new CRFlowLayout());
		JScrollPane scrollPane = new JScrollPane(progressPanel);
		window.requestFocus();
		window.setAlwaysOnTop(true);
		window.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				window.dispatchEvent(new WindowEvent(window,
						WindowEvent.WINDOW_ACTIVATED));
			}

			public void focusLost(FocusEvent e) {
				hide();
			}
		});
		window.add(scrollPane, BorderLayout.CENTER);
		window.pack();
	}

	public void addJob(Job job) {
		progressPanel.removeAll();
		Job[] jobs = getBackgroundManager().getJobQueue().getJobs();
		for (Job queuedJob : jobs) {
			Component[] comps = idBar.get(queuedJob.getId());
			if (comps != null) {
				((ProgressBar) comps[0]).setJob(queuedJob);
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
		window.setVisible(true);
	}

	private BackgroundManager getBackgroundManager() {
		return (BackgroundManager) Services.getService(BackgroundManager.class);
	}

	public void removeJob(Job job) {
		Job[] jobs = getBackgroundManager().getJobQueue().getJobs();
		if (jobs.length == 0 || !job.getId().is(jobs[0].getId())) {
			Component[] comps = idBar.remove(job.getId());
			if (comps != null) {
				for (Component component : comps) {
					progressPanel.remove(component);
				}
			}
			invalidate();
			repaint(0, 0, getWidth(), getHeight());
			progressPanel.invalidate();
			progressPanel.doLayout();
			System.out.println("Removed job " + job.getId());
		}
		if (idBar.isEmpty())
			hide();
	}


	public void replaceJob(Job job) {
		Component[] comps = idBar.get(job.getId());
		ProgressBar bar = (ProgressBar) comps[0];
		bar.setJob(job);
		System.out.println("Replaced job " + job.getId());
	}
}
