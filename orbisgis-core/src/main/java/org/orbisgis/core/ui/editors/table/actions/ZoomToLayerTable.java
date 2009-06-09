package org.orbisgis.core.ui.editors.table.actions;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editors.map.MapEditor;
import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.core.ui.editors.table.TableEditor;
import org.orbisgis.core.ui.editors.table.action.ITableCellAction;
import org.orbisgis.core.ui.views.editor.EditorManager;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.pluginManager.background.BackgroundJob;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Envelope;

public class ZoomToLayerTable implements ITableCellAction {

	@Override
	public boolean accepts(TableEditableElement element, int rowIndex,
			int columnIndex) {

		return element.getMapContext() != null;
	}

	@Override
	public void execute(TableEditor editor, final TableEditableElement element,
			final int rowIndex, final int columnIndex) {
		BackgroundManager bm = Services.getService(BackgroundManager.class);
		bm.backgroundOperation(new BackgroundJob() {

			@Override
			public void run(IProgressMonitor pm) {
				try {

					SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
							element.getDataSource());

					Envelope rect = sds.getFullExtent();

					EditorManager em = (EditorManager) Services
							.getService(EditorManager.class);
					IEditor[] editors = em
							.getEditors("org.orbisgis.core.ui.editors.Map", element
									.getMapContext());
					for (IEditor mapEditor : editors) {
						((MapEditor) mapEditor).getMapTransform().setExtent(
								rect);
					}

				} catch (DriverException e) {
					Services.getService(ErrorManager.class).error(
							"Cannot compute envelope", e);
				}
			}

			@Override
			public String getTaskName() {
				return "Calculating  extent";
			}
		});
	}
}
