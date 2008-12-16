package org.orbisgis.graphicModeler.tools;

import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.driver.DriverException;
import org.orbisgis.editors.map.tool.FinishedAutomatonException;
import org.orbisgis.editors.map.tool.ToolManager;
import org.orbisgis.editors.map.tool.TransitionException;
import org.orbisgis.editors.map.tools.AbstractSelectionTool;
import org.orbisgis.editors.map.tools.ToolUtilities;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;

public class SelectionTool extends AbstractSelectionTool {
	@Override
	public void transitionTo_MakeMove(MapContext mc, ToolManager tm)
			throws TransitionException, FinishedAutomatonException {
		super.transitionTo_MakeMove(mc, tm);
		try {
			// TODO with transactions saving the layer for each movement won't
			// be necessary
			mc.getActiveLayer().getDataSource().commit();
		} catch (DriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NonEditableDataSourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected ILayer getLayer(MapContext mc) {
		return mc.getActiveLayer();
	}

	@Override
	public boolean isEnabled(MapContext vc, ToolManager tm) {
		return ToolUtilities.isActiveLayerEditable(vc)
				&& ToolUtilities.isActiveLayerVisible(vc)
				&& ToolUtilities.geometryTypeIs(vc, GeometryConstraint.POINT);
	}

	@Override
	public boolean isVisible(MapContext vc, ToolManager tm) {
		return isEnabled(vc, tm);
	}
}
