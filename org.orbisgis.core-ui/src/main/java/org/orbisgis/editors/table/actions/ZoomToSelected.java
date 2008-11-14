package org.orbisgis.editors.table.actions;

import java.util.ArrayList;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.editors.map.MapEditor;
import org.orbisgis.editors.table.Selection;
import org.orbisgis.editors.table.TableEditableElement;
import org.orbisgis.editors.table.TableEditor;
import org.orbisgis.editors.table.action.ITableCellAction;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.pluginManager.background.BackgroundJob;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.views.editor.EditorManager;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.operation.union.UnaryUnionOp;

public class ZoomToSelected implements ITableCellAction {

	private ArrayList<Geometry> toUnite = new ArrayList<Geometry>();

	@Override
	public boolean accepts(TableEditableElement element, int rowIndex,
			int columnIndex) {
		MapContext mc = element.getMapContext();

		return mc != null;

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

					for (int i = 0; i < selectedRow.length; i++) {

						addGeometry(sds.getGeometry(i));
					}

					Envelope envelope = UnaryUnionOp.union(toUnite)
							.getEnvelopeInternal();
										
					EditorManager em = (EditorManager) Services
							.getService(EditorManager.class);
					if (em.getActiveEditor() != null) {
						((MapEditor) em.getActiveEditor()).getMapTransform()
								.setExtent(envelope);
					}

				} catch (DriverException e) {
					Services.getService(ErrorManager.class).error(
							"Cannot read source", e);
				}
			}

			@Override
			public String getTaskName() {
				return "finding matches";
			}
		});
	}

	private void addGeometry(Geometry geom) {
		if (geom.getGeometryType().equals("GeometryCollection")) {
			for (int i = 0; i < geom.getNumGeometries(); i++) {
				addGeometry(geom.getGeometryN(i));
			}
		} else {
			toUnite.add(geom);
		}
	}
}
