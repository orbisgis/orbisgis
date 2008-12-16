package org.orbisgis.graphicModeler.tools;

import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.editors.map.tool.ToolManager;
import org.orbisgis.editors.map.tool.TransitionException;
import org.orbisgis.editors.map.tools.AbstractPointTool;
import org.orbisgis.editors.map.tools.ToolUtilities;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.ui.sif.ChoosePanel;
import org.sif.UIFactory;

import com.vividsolutions.jts.geom.Point;

public abstract class AbstractInsertionTool extends AbstractPointTool {
	protected static final String PROCESS_TYPE = "process";
	protected static final String DATASOURCE_TYPE = "data source";

	@Override
	protected void pointDone(Point point, MapContext mc, ToolManager tm)
			throws TransitionException {
		String[] elements = getAvailableElements();
		ChoosePanel dlg = new ChoosePanel("Select a " + getType(), elements,
				elements);
		dlg.setMultiple(false);

		if (UIFactory.showDialog(dlg)) {
			String selectedElement = elements[dlg.getSelectedIndex()];
			SpatialDataSourceDecorator sds = mc.getActiveLayer()
					.getDataSource();
			try {
				Value[] row = new Value[sds.getMetadata().getFieldCount()];
				row[sds.getSpatialFieldIndex()] = ValueFactory
						.createValue(point);
				row[sds.getFieldIndexByName("id")] = ValueFactory
						.createValue(selectedElement);
				row[sds.getFieldIndexByName("type")] = ValueFactory
						.createValue(getType());
				row = ToolUtilities.populateNotNullFields(sds, row);
				sds.insertFilledRow(row);
				sds.commit();
			} catch (DriverException e) {
				throw new TransitionException("Cannot insert " + getType(), e);
			} catch (NonEditableDataSourceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Get all the available elements to choose from
	 * 
	 * @return the elements to show in a choose list
	 */
	protected abstract String[] getAvailableElements();

	/**
	 * The type of the insertion tool. Use AbstractInsertionTool.PROCESS_TYPE or
	 * AbstractInsertionTool.DATASOURCE_TYPE
	 * 
	 * @return the type of the insertion tool
	 */
	protected abstract String getType();

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

	@Override
	public double getInitialZ(MapContext mapContext) {
		return ToolUtilities.getActiveLayerInitialZ(mapContext);
	}
}
