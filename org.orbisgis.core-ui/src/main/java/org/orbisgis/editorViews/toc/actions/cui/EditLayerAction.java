package org.orbisgis.editorViews.toc.actions.cui;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.editorViews.toc.action.ILayerAction;
import org.orbisgis.editorViews.toc.actions.cui.gui.JPanelLegendList;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.renderer.legend.Legend;
import org.sif.UIFactory;

public class EditLayerAction implements ILayerAction {

	public boolean accepts(ILayer layer) {
		try {
			return layer.isVectorial();
		} catch (DriverException e) {
			return false;
		}
	}

	public boolean acceptsSelectionCount(int layerCount) {
		return layerCount == 1;
	}

	public void execute(MapContext mapContext, ILayer layer) {
		int geomConstraint = GeometryConstraint.MIXED;
		try {
			Type typ = layer.getDataSource().getMetadata().getFieldType(
					layer.getDataSource().getSpatialFieldIndex());
			if (typ.getTypeCode() == Type.GEOMETRY) {
				Constraint cons = typ.getConstraint(Constraint.GEOMETRY_TYPE);
				geomConstraint = ((GeometryConstraint) cons).getGeometryType();
			}

			Legend[] leg = layer.getLegend();

			JPanelLegendList pan = new JPanelLegendList(geomConstraint, leg);
			if (UIFactory.showDialog(pan)) {
				try {
					layer.setLegend(pan.getLegend());
				} catch (DriverException e) {
					System.out.println("Driver exception");
				}
			}
		} catch (DriverException e) {
			Services.getErrorManager().error("Cannot access the layer legend",
					e);
		}
	}

}