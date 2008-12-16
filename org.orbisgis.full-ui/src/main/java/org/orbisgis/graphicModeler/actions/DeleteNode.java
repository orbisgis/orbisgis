package org.orbisgis.graphicModeler.actions;

import java.util.Arrays;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.orbisgis.DataManager;
import org.orbisgis.Services;
import org.orbisgis.editor.IEditor;
import org.orbisgis.editor.action.IEditorAction;
import org.orbisgis.editors.map.MapContextManager;
import org.orbisgis.editors.map.tools.ToolUtilities;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;

public class DeleteNode implements IEditorAction {

	@Override
	public void actionPerformed(IEditor editor) {
		try {
			DataManager dm = Services.getService(DataManager.class);
			MapContext map = (MapContext) editor.getElement().getObject();
			ILayer activeLayer = map.getActiveLayer();
			int[] sel = activeLayer.getSelection().clone();
			Arrays.sort(sel);
			SpatialDataSourceDecorator nodes = activeLayer.getDataSource();

			// TODO get the right data source
			DataSource edges = dm.getDSF().getDataSource("edges");

			if (!edges.isOpen()) {
				edges.open();
			}

			nodes.setDispatchingMode(DataSource.STORE);
			for (int i = sel.length - 1; i >= 0; i--) {
				String id = nodes.getString(sel[i], "id");

				for (long j = edges.getRowCount() - 1; j >= 0; j--) {
					if (edges.getString(j, "src").equals(id)
							|| edges.getString(j, "dest").equals(id)) {
						edges.deleteRow(j);
					}
				}

				nodes.deleteRow(sel[i]);
			}
			nodes.setDispatchingMode(DataSource.DISPATCH);

			nodes.commit();
			edges.commit();
			edges.close();
		} catch (DriverLoadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchTableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataSourceCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NonEditableDataSourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isEnabled(IEditor editor) {
		MapContextManager mcm = Services.getService(MapContextManager.class);
		MapContext vc = mcm.getActiveView();
		return ToolUtilities.isActiveLayerEditable(vc)
				&& ToolUtilities.isActiveLayerVisible(vc)
				&& ToolUtilities.geometryTypeIs(vc, GeometryConstraint.POINT);
	}

	public boolean isVisible(IEditor editor) {
		return isEnabled(editor);
	}
}
