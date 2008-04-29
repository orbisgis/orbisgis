package org.orbisgis.geoview.cui;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.cui.gui.JPanelLegend;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.VectorLayer;
import org.orbisgis.geoview.renderer.legend.Legend;
import org.orbisgis.geoview.renderer.legend.LegendComposite;
import org.orbisgis.geoview.renderer.legend.LegendFactory;
import org.orbisgis.geoview.views.toc.ILayerAction;
import org.sif.UIFactory;

public class EditLayerAction implements ILayerAction {

	public boolean accepts(ILayer layer) {
		return layer instanceof VectorLayer;
	}

	public boolean acceptsAll(ILayer[] layer) {
		return true;
	}

	public boolean acceptsSelectionCount(int layerCount) {
		return layerCount == 1;
	}

	public void execute(GeoView2D view, ILayer resource) {
		VectorLayer vectorResource = null;
		int geomConstraint = GeometryConstraint.MIXED;
		if (resource instanceof VectorLayer) {
			vectorResource = (VectorLayer) resource;
			try {
				Type typ = vectorResource.getDataSource().getMetadata()
						.getFieldType(
								vectorResource.getDataSource()
										.getSpatialFieldIndex());
				if (typ.getTypeCode() == Type.GEOMETRY) {
					Constraint cons = typ
							.getConstraint(Constraint.GEOMETRY_TYPE);
					geomConstraint = ((GeometryConstraint) cons)
							.getGeometryType();
				}
			} catch (DriverException e) {
				System.out.println("Driver exception");
			}

			Legend leg = vectorResource.getLegend();

			LegendComposite legC = null;
			if (leg instanceof LegendComposite) {
				legC = (LegendComposite) leg;

			} else {
				legC = (LegendComposite) LegendFactory.createLegendComposite();
			}

			JPanelLegend pan = new JPanelLegend(geomConstraint, legC);
			if (UIFactory.showDialog(pan)) {
				try {
					vectorResource.setLegend(pan.getLegend());
				} catch (DriverException e) {
					System.out.println("Driver exception");
				}
			}

		} else {
			System.out.println("is not an instance of vectorlayer");
			return;
		}

	}

	public void executeAll(GeoView2D view, ILayer[] layers) {
		// TODO Auto-generated method stub

	}

}
