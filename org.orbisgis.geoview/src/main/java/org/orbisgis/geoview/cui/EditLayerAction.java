package org.orbisgis.geoview.cui;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.cui.gui.JPanelLegend;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.renderer.legend.Legend;
import org.orbisgis.geoview.views.toc.ILayerAction;
import org.sif.UIFactory;

public class EditLayerAction implements ILayerAction {

	public boolean accepts(ILayer layer) {
		try {
			return layer.isVectorial();
		} catch (DriverException e) {
			return false;
		}
	}

	public boolean acceptsAll(ILayer[] layer) {
		return true;
	}

	public boolean acceptsSelectionCount(int layerCount) {
		return layerCount == 1;
	}

	public void execute(GeoView2D view, ILayer resource) {
		int geomConstraint = GeometryConstraint.MIXED;
		try {
			Type typ = resource.getDataSource().getMetadata().getFieldType(
					resource.getDataSource().getSpatialFieldIndex());
			if (typ.getTypeCode() == Type.GEOMETRY) {
				Constraint cons = typ.getConstraint(Constraint.GEOMETRY_TYPE);
				geomConstraint = ((GeometryConstraint) cons).getGeometryType();
			}

			Legend[] leg = resource.getLegend();

			JPanelLegend pan = new JPanelLegend(geomConstraint, leg);
			if (UIFactory.showDialog(pan)) {
				try {
					resource.setLegend(pan.getLegend());
				} catch (DriverException e) {
					System.out.println("Driver exception");
				}
			}
		} catch (DriverException e) {
			System.out.println("Driver exception");
		}
	}

	public void executeAll(GeoView2D view, ILayer[] layers) {
		// TODO Auto-generated method stub

	}

}
