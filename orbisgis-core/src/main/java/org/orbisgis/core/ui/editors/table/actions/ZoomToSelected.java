package org.orbisgis.core.ui.editors.table.actions;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editors.map.MapEditor;
import org.orbisgis.core.ui.editors.table.Selection;
import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.core.ui.editors.table.TableEditor;
import org.orbisgis.core.ui.editors.table.action.ITableCellAction;
import org.orbisgis.core.ui.views.editor.EditorManager;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.pluginManager.background.BackgroundJob;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class ZoomToSelected implements ITableCellAction {

	@Override
	public boolean accepts(TableEditableElement element, int rowIndex,
			int columnIndex) {

		if (element.getMapContext() != null){
			if (element.getSelection().getSelectedRows().length>0){
				return true;
			}
		}
		return false;
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
					Geometry geometry = null;
					Envelope geometryEnvelope = null;
					for (int i = 0; i < selectedRow.length; i++) {
						if (sds.isDefaultVectorial()) {
							geometry = sds.getGeometry(selectedRow[i]);
							if (geometry != null) {
								geometryEnvelope = geometry
										.getEnvelopeInternal();
							}
						} else if (sds.isDefaultRaster()) {
							geometryEnvelope = sds.getRaster(selectedRow[i]).getMetadata()
									.getEnvelope();
						}

						if (rect == null) {
							rect = new Envelope(geometryEnvelope);
						} else {
							rect.expandToInclude(geometryEnvelope);
						}

					}

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
				return "Calculating selected extent";
			}
		});
	}
}
