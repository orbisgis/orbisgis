package org.orbisgis.editors.table.actions;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.editor.IEditor;
import org.orbisgis.editors.map.MapEditor;
import org.orbisgis.editors.table.Selection;
import org.orbisgis.editors.table.TableEditableElement;
import org.orbisgis.editors.table.TableEditor;
import org.orbisgis.editors.table.action.ITableCellAction;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.pluginManager.background.BackgroundJob;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.views.editor.EditorManager;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class ZoomToSelected implements ITableCellAction {

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
					Selection selection = element.getSelection();
					int[] selectedRow = selection.getSelectedRows();

					SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
							element.getDataSource());

					Envelope rect = null;
					for (int i = 0; i < selectedRow.length; i++) {
						Geometry geometry = sds.getGeometry(i);
						if (geometry != null) {
							Envelope geometryEnvelope = geometry
									.getEnvelopeInternal();
							if (rect == null) {
								rect = new Envelope(geometryEnvelope);
							} else {
								rect.expandToInclude(geometryEnvelope);
							}
						}
					}

					EditorManager em = (EditorManager) Services
							.getService(EditorManager.class);
					IEditor[] editors = em
							.getEditors("org.orbisgis.editors.Map", element
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
				return "Calculating selected extent";
			}
		});
	}
}
