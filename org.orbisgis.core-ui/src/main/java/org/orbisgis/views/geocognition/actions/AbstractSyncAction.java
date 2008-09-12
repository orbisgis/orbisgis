package org.orbisgis.views.geocognition.actions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.orbisgis.PersistenceException;
import org.orbisgis.Services;
import org.orbisgis.editor.IEditor;
import org.orbisgis.geocognition.Geocognition;
import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.geocognition.mapContext.GeocognitionException;
import org.orbisgis.pluginManager.background.BackgroundJob;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.views.editor.EditorManager;
import org.orbisgis.views.geocognition.action.IGeocognitionGroupAction;
import org.orbisgis.views.geocognition.sync.ComparePanel;
import org.orbisgis.views.geocognition.sync.IdPath;
import org.sif.UIFactory;

public abstract class AbstractSyncAction implements IGeocognitionGroupAction {

	@Override
	public boolean accepts(Geocognition geocog, GeocognitionElement[] element) {
		return true;
	}

	protected void showSynchronizePanel(Geocognition geocognition,
			GeocognitionElement[] elements, Object remoteSource)
			throws IOException {

		EditorManager em = Services.getService(EditorManager.class);
		IEditor[] editors = em.getEditors();
		if (editors.length > 0) {
			int res = JOptionPane.showConfirmDialog(null,
					"All editors will be closed before "
							+ "synchronizing. Proceed?", "Synchronize",
					JOptionPane.YES_NO_OPTION);
			if (res == JOptionPane.NO_OPTION) {
				return;
			}
		}
		for (IEditor editor : editors) {
			if (!em.closeEditor(editor)) {
				return;
			}
		}

		try {
			GeocognitionElement local = geocognition.getRoot().cloneElement();
			ArrayList<IdPath> filter;

			if (elements.length == 0) {
				filter = null;
			} else {
				filter = new ArrayList<IdPath>();
				for (int i = 0; i < elements.length; i++) {
					filter.add(new IdPath(elements[i].getIdPath()));
				}
				
			}

			// Show panel
			ComparePanel panel = new ComparePanel();
			panel.setModel(local, local, filter);
			panel.setModel(local, remoteSource, filter);
			if (UIFactory.showDialog(panel)) {
				BackgroundManager bm = Services
						.getService(BackgroundManager.class);
				bm.backgroundOperation(new SavingJob(remoteSource,
						geocognition, panel));
			}
		} catch (PersistenceException e) {
			Services.getErrorManager().error(
					"The file cannot be readed. Probably it's not a "
							+ "valid geocognition xml file", e);
		} catch (GeocognitionException e) {
			Services.getErrorManager().error(
					"The local geocognition cannot be synchronized", e);
		}
	}

	private class SavingJob implements BackgroundJob {
		private Object remoteSource;
		private Geocognition geocognition;
		private ComparePanel panel;

		private SavingJob(Object rem, Geocognition g, ComparePanel p) {
			remoteSource = rem;
			geocognition = g;
			panel = p;
		}

		@Override
		public String getTaskName() {
			return "Saving resources";
		}

		@Override
		public void run(IProgressMonitor pm) {
			try {
				pm.progressTo(0);

				// Update remote file
				if (remoteSource instanceof File) {
					pm.startTask("Saving file");
					FileOutputStream output = new FileOutputStream(
							(File) remoteSource);
					geocognition.write(panel.getRemoteElement(), output);
					output.close();
					pm.endTask();
				}

				pm.progressTo(50);
				pm.startTask("Saving geocognition");

				// Update local geocognition
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				geocognition.write(panel.getLocalElement(), buffer);
				geocognition.clear();
				geocognition
						.read(new ByteArrayInputStream(buffer.toByteArray()));
				pm.endTask();
				pm.progressTo(100);
			} catch (IOException e) {
				Services.getErrorManager().error(
						"The file cannot be saved. Probably the file has "
								+ "changed while synchronizing", e);
			} catch (UnsupportedOperationException e) {
				Services.getErrorManager().error("bug!", e);
			} catch (PersistenceException e) {
				Services.getErrorManager().error(
						"The geocognition cannot be updated", e);
			}
		}

	}
}