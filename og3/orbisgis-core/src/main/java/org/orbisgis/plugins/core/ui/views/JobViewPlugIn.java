package org.orbisgis.plugins.core.ui.views;

import java.util.Observable;

import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.background.BackgroundListener;
import org.orbisgis.plugins.core.background.BackgroundManager;
import org.orbisgis.plugins.core.background.Job;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.ViewPlugIn;
import org.orbisgis.plugins.core.ui.workbench.Names;

public class JobViewPlugIn extends ViewPlugIn {

	private JobPanel panel;
	private JMenuItem menuItem;

	public JobViewPlugIn() {

	}

	public void initialize(PlugInContext context) throws Exception {
		panel = new JobPanel();

		BackgroundManager bm = (BackgroundManager) Services
				.getService(BackgroundManager.class);

		bm.addBackgroundListener(new BackgroundListener() {

			public void jobAdded(final Job job) {
				if (SwingUtilities.isEventDispatchThread()) {
					panel.addJob(job);
				} else {
					SwingUtilities.invokeLater(new Runnable() {

						public void run() {
							panel.addJob(job);
						}

					});
				}
			}

			public void jobRemoved(final Job job) {
				if (SwingUtilities.isEventDispatchThread()) {
					panel.removeJob(job);
				} else {
					SwingUtilities.invokeLater(new Runnable() {

						public void run() {
							panel.removeJob(job);
						}

					});
				}
			}

			public void jobReplaced(final Job job) {
				if (SwingUtilities.isEventDispatchThread()) {
					panel.replaceJob(job);
				} else {
					SwingUtilities.invokeLater(new Runnable() {

						public void run() {
							panel.replaceJob(job);
						}

					});
				}
			}
		});

		menuItem = context.getFeatureInstaller().addMainMenuItem(this,
				new String[] { Names.VIEW }, Names.JOBS, true,
				getIcon(Names.JOBS_ICON), null, panel, null, null,
				context.getWorkbenchContext());

	}

	public boolean execute(PlugInContext context) throws Exception {
		getUpdateFactory().loadView(getId());
		return true;
	}

	public void update(Observable o, Object arg) {
		setSelected();
	}

	public void setSelected() {
		menuItem.setSelected(isVisible());
	}

	public boolean isVisible() {
		return getUpdateFactory().viewIsOpen(getId());
	}

}
